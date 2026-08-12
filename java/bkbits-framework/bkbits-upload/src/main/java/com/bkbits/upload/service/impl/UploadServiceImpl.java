package com.bkbits.upload.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.bkbits.generator.IdGenerator;
import com.bkbits.orm.ICreateBy;
import com.bkbits.upload.BkbitsUploadProperties;
import com.bkbits.upload.UploadFile;
import com.bkbits.upload.UploadStatus;
import com.bkbits.upload.UploadTask;
import com.bkbits.upload.UploadTaskPiece;
import com.bkbits.upload.pojo.UploadTaskCreateDTO;
import com.bkbits.upload.exception.UploadException;
import com.bkbits.upload.service.UploadService;
import com.bkbits.upload.pojo.UploadTaskCreateVO;
import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.easy.query.core.basic.jdbc.tx.Transaction;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.handle.UploadedFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 文件上传服务。
 *
 * <p>小文件：写临时目录 -> 校验哈希 -> 移动到上传目录 -> 落库。</p>
 * <p>大文件：创建任务（任务 + 分片落库）-> 分片上传（写临时目录、校验哈希/大小、更新分片状态）
 * -> 完成任务（核对分片、合并、校验、移动、落库）。</p>
 */
@Component
public class UploadServiceImpl implements UploadService {

    private static final DateTimeFormatter DATE_PATH = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Inject
    private EasyEntityQuery easyEntityQuery;

    @Inject
    private IdGenerator idGenerator;

    @Inject
    private BkbitsUploadProperties uploadProperties;

    /**
     * 小文件上传。
     *
     * @param file 上传文件
     * @param hash 文件哈希（SHA-256 hex）
     * @return 落库后的文件记录
     */
    public UploadFile upload(UploadedFile file, String hash) {
        if (file == null) {
            throw new UploadException("上传文件为空");
        }
        if (hash == null || hash.isBlank()) {
            throw new UploadException("缺少文件哈希");
        }
        byte[] bytes = readBytes(file);
        if (bytes.length == 0) {
            throw new UploadException("上传文件为空");
        }
        if (!sha256Hex(bytes).equalsIgnoreCase(hash)) {
            throw new UploadException("文件哈希校验失败");
        }

        String fileId = idGenerator.nextId();
        String fileName = sanitizeFileName(file.getName());

        // 写出到临时目录，再移动到上传目录
        Path tempFile = tempDir().resolve(fileId);
        try {
            Files.write(tempFile, bytes);
        } catch (IOException e) {
            throw new UploadException("写入临时文件失败", e);
        }
        Path target = moveToUploadDir(tempFile, fileId, fileName);

        UploadFile uploadFile = new UploadFile();
        uploadFile.setId(fileId);
        uploadFile.setPath(relativePath(target));
        uploadFile.setContentType(file.getContentType());
        uploadFile.setFileSize((long) bytes.length);
        uploadFile.setFileName(fileName);
        uploadFile.setHash(hash.toLowerCase());
        fillAudit(uploadFile);
        easyEntityQuery.insertable(uploadFile).executeRows();
        return uploadFile;
    }

    /**
     * 创建大文件上传任务（任务与分片信息落库）。
     */
    public UploadTaskCreateVO createTask(UploadTaskCreateDTO req) {
        if (req.getFileHash() == null || req.getFileHash().isBlank()) {
            throw new UploadException("缺少文件哈希");
        }
        if (req.getFileSize() == null || req.getFileSize() <= 0) {
            throw new UploadException("文件大小不合法");
        }
        if (req.getPieceSize() == null || req.getPieceSize() <= 0) {
            throw new UploadException("分片大小不合法");
        }
        if (req.getFileName() == null || req.getFileName().isBlank()) {
            throw new UploadException("缺少文件名称");
        }
        long pieceCount = (req.getFileSize() + req.getPieceSize() - 1) / req.getPieceSize();
        if (req.getPieceHashes() == null || req.getPieceHashes().size() != pieceCount) {
            throw new UploadException("分片哈希数量与分片数不一致");
        }

        String taskId = idGenerator.nextId();
        String createBy = loginId();
        LocalDateTime now = LocalDateTime.now();

        try (Transaction tx = easyEntityQuery.beginTransaction()) {
            UploadTask task = new UploadTask();
            task.setId(taskId);
            task.setUploadStatus(UploadStatus.WAITING);
            task.setContentType(req.getContentType());
            task.setFileSize(req.getFileSize());
            task.setFileName(sanitizeFileName(req.getFileName()));
            task.setHash(req.getFileHash().toLowerCase());
            task.setCreateBy(createBy);
            task.setCreateTime(now);
            easyEntityQuery.insertable(task).executeRows();

            for (int i = 0; i < pieceCount; i++) {
                UploadTaskPiece piece = new UploadTaskPiece();
                piece.setId(idGenerator.nextId());
                piece.setUploadId(taskId);
                piece.setFileIndex((long) i);
                piece.setUploadStatus(UploadStatus.WAITING);
                piece.setFileSize(Math.min(req.getPieceSize(), req.getFileSize() - i * req.getPieceSize()));
                piece.setHash(req.getPieceHashes().get(i).toLowerCase());
                piece.setCreateBy(createBy);
                piece.setCreateTime(now);
                easyEntityQuery.insertable(piece).executeRows();
            }
            tx.commit();
        }
        return new UploadTaskCreateVO(taskId, pieceCount, req.getPieceSize());
    }

    /**
     * 上传文件分片。
     *
     * <p>校验分片大小与哈希，写入临时目录，更新分片状态为成功。</p>
     */
    public void uploadPiece(String taskId, long fileIndex, UploadedFile file) {
        UploadTask task = easyEntityQuery.queryable(UploadTask.class).whereById(taskId).singleOrNull();
        if (task == null) {
            throw new UploadException("上传任务不存在");
        }
        if (!UploadStatus.WAITING.equals(task.getUploadStatus())) {
            throw new UploadException("上传任务状态不允许上传分片");
        }

        UploadTaskPiece piece = easyEntityQuery.queryable(UploadTaskPiece.class)
                .where(o -> o.uploadId().eq(taskId))
                .where(o -> o.fileIndex().eq(fileIndex))
                .singleOrNull();
        if (piece == null) {
            throw new UploadException("分片不存在: " + fileIndex);
        }
        if (file == null) {
            throw new UploadException("分片文件为空");
        }
        byte[] bytes = readBytes(file);
        if (bytes.length == 0) {
            throw new UploadException("分片文件为空");
        }
        if (bytes.length != piece.getFileSize()) {
            throw new UploadException("分片大小不匹配");
        }
        if (!sha256Hex(bytes).equalsIgnoreCase(piece.getHash())) {
            throw new UploadException("分片哈希校验失败");
        }

        Path partFile = taskTempDir(taskId).resolve(fileIndex + ".part");
        try {
            Files.createDirectories(partFile.getParent());
            Files.write(partFile, bytes);
        } catch (IOException e) {
            throw new UploadException("写入分片失败", e);
        }

        easyEntityQuery.updatable(UploadTaskPiece.class)
                .setColumns(o -> {
                    o.uploadStatus().set(UploadStatus.SUCCESS);
                    o.path().set(partFile.toString());
                })
                .whereById(piece.getId())
                .executeRows();
    }

    /**
     * 完成大文件上传任务。
     *
     * <p>核对所有分片状态，按序合并分片到临时目录，校验大小与文件哈希，
     * 移动到上传目录，写入文件记录并更新任务状态。</p>
     *
     * @return 落库后的文件记录
     */
    public UploadFile finishTask(String taskId) {
        UploadTask task = easyEntityQuery.queryable(UploadTask.class).whereById(taskId).singleOrNull();
        if (task == null) {
            throw new UploadException("上传任务不存在");
        }
        if (!UploadStatus.WAITING.equals(task.getUploadStatus())) {
            throw new UploadException("上传任务状态不允许完成");
        }

        List<UploadTaskPiece> pieces = easyEntityQuery.queryable(UploadTaskPiece.class)
                .where(o -> o.uploadId().eq(taskId))
                .orderBy(o -> o.fileIndex().asc())
                .toList();
        if (pieces.isEmpty()) {
            throw new UploadException("上传任务没有分片");
        }
        for (UploadTaskPiece piece : pieces) {
            if (!UploadStatus.SUCCESS.equals(piece.getUploadStatus())) {
                throw new UploadException("分片未完成上传: " + piece.getFileIndex());
            }
        }

        // 合并分片到临时目录
        Path mergeFile = taskTempDir(taskId).resolve("merged.part");
        try {
            Files.createDirectories(mergeFile.getParent());
            try (OutputStream out = Files.newOutputStream(mergeFile)) {
                for (UploadTaskPiece piece : pieces) {
                    Files.copy(Path.of(piece.getPath()), out);
                }
            }
        } catch (IOException e) {
            throw new UploadException("合并分片失败", e);
        }

        // 校验大小与哈希
        long size;
        try {
            size = Files.size(mergeFile);
        } catch (IOException e) {
            throw new UploadException("读取合并文件失败", e);
        }
        if (size != task.getFileSize()) {
            throw new UploadException("合并文件大小与任务不一致");
        }
        if (!sha256Hex(mergeFile).equalsIgnoreCase(task.getHash())) {
            throw new UploadException("文件哈希校验失败");
        }

        // 移动到上传目录
        Path target = moveToUploadDir(mergeFile, task.getId(), task.getFileName());
        String relPath = relativePath(target);

        UploadFile uploadFile = new UploadFile();
        uploadFile.setId(idGenerator.nextId());
        uploadFile.setPath(relPath);
        uploadFile.setContentType(task.getContentType());
        uploadFile.setFileSize(task.getFileSize());
        uploadFile.setFileName(task.getFileName());
        uploadFile.setHash(task.getHash());
        uploadFile.setCreateBy(task.getCreateBy());
        uploadFile.setCreateTime(LocalDateTime.now());

        try (Transaction tx = easyEntityQuery.beginTransaction()) {
            easyEntityQuery.insertable(uploadFile).executeRows();
            easyEntityQuery.updatable(UploadTask.class)
                    .setColumns(o -> {
                        o.uploadStatus().set(UploadStatus.SUCCESS);
                        o.path().set(relPath);
                    })
                    .whereById(taskId)
                    .executeRows(1, "上传任务状态已变更");
            tx.commit();
        } catch (RuntimeException e) {
            // 落库失败则移除已移动的文件，分片临时文件保留以便重试
            deleteQuietly(target);
            throw e;
        }
        // 清理分片临时目录
        deleteQuietly(taskTempDir(taskId));
        return uploadFile;
    }

    // ---------- 内部工具 ----------

    private byte[] readBytes(UploadedFile file) {
        try {
            return file.getContentAsBytes();
        } catch (IOException e) {
            throw new UploadException("读取上传文件失败", e);
        }
    }

    private void fillAudit(ICreateBy entity) {
        entity.setCreateBy(loginId());
        entity.setCreateTime(LocalDateTime.now());
    }

    private String loginId() {
        try {
            return StpUtil.getLoginIdAsString();
        } catch (Exception e) {
            // 非登录上下文（如测试）时创建者置空
            return null;
        }
    }

    /** 写出到临时目录后移动到上传目录，返回目标文件路径 */
    private Path moveToUploadDir(Path source, String fileId, String fileName) {
        Path dir = uploadDir().resolve(LocalDateTime.now().format(DATE_PATH));
        Path target = dir.resolve(fileId + "_" + sanitizeFileName(fileName));
        try {
            Files.createDirectories(dir);
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UploadException("移动文件失败", e);
        }
        return target;
    }

    /** 上传目录的相对路径（用于落库，便于目录迁移） */
    private String relativePath(Path target) {
        return uploadDir().relativize(target).toString().replace('\\', '/');
    }

    private Path tempDir() {
        return Path.of(uploadProperties.getTemp());
    }

    private Path uploadDir() {
        return Path.of(uploadProperties.getUpload());
    }

    private Path taskTempDir(String taskId) {
        return tempDir().resolve("upload").resolve(taskId);
    }

    private static String sanitizeFileName(String name) {
        if (name == null || name.isBlank()) {
            return "file";
        }
        String cleaned = name.replaceAll("[\\\\/:*?\"<>|]", "_").trim();
        return cleaned.isBlank() ? "file" : cleaned;
    }

    private static String sha256Hex(byte[] bytes) {
        try {
            return hex(MessageDigest.getInstance("SHA-256").digest(bytes));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 算法不可用", e);
        }
    }

    private static String sha256Hex(Path file) {
        try (InputStream in = Files.newInputStream(file)) {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) > 0) {
                digest.update(buffer, 0, len);
            }
            return hex(digest.digest());
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new UploadException("计算文件哈希失败", e);
        }
    }

    private static String hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(Character.forDigit((b >> 4) & 0xF, 16));
            sb.append(Character.forDigit(b & 0xF, 16));
        }
        return sb.toString();
    }

    private static void deleteQuietly(Path path) {
        try {
            if (path == null || !Files.exists(path)) {
                return;
            }
            if (Files.isDirectory(path)) {
                try (var stream = Files.walk(path)) {
                    stream.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                        try {
                            Files.deleteIfExists(p);
                        } catch (IOException ignored) {
                            // 尽力清理
                        }
                    });
                }
            } else {
                Files.deleteIfExists(path);
            }
        } catch (IOException ignored) {
            // 尽力清理
        }
    }
}
