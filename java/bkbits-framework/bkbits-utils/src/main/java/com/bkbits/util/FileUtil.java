package com.bkbits.util;

import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Stream;

/**
 * 文件与目录工具类（基于 java.nio，线程安全）。
 *
 * @author bkbits
 */
@UtilityClass
public class FileUtil {

    /**
     * 是否为空（null 或不存在）。
     *
     * @param file 文件或目录
     * @return 空返回 {@code true}
     */
    public static boolean isEmpty(File file) {
        return file == null || !file.exists();
    }

    /**
     * 是否非空（存在）。
     *
     * @param file 文件或目录
     * @return 存在返回 {@code true}
     */
    public static boolean isNotEmpty(File file) {
        return !isEmpty(file);
    }

    /**
     * 是否是文件（null 或不存在返回 false）。
     *
     * @param file 文件
     * @return 是文件返回 {@code true}
     */
    public static boolean isFile(File file) {
        return file != null && file.isFile();
    }

    /**
     * 是否是目录（null 或不存在返回 false）。
     *
     * @param dir 目录
     * @return 是目录返回 {@code true}
     */
    public static boolean isDirectory(File dir) {
        return dir != null && dir.isDirectory();
    }

    /**
     * 创建目录（含父目录），已存在时返回 true。
     *
     * @param dir 目录
     * @return 创建成功或已存在返回 {@code true}
     */
    public static boolean mkdirs(File dir) {
        return dir != null && dir.mkdirs();
    }

    /**
     * 创建文件，文件已存在时不覆盖。
     *
     * @param file 文件
     * @return 创建成功返回 {@code true}；已存在或参数为空返回 {@code false}
     */
    public static boolean create(File file) {
        if (file == null) {
            return false;
        }
        try {
            if (file.isDirectory()) {
                return file.mkdirs();
            }
            Path parent = file.toPath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            return file.createNewFile();
        } catch (IOException e) {
            throw new IllegalStateException("创建文件失败: " + file.getPath(), e);
        }
    }

    /**
     * 创建文件，文件已存在时直接返回。
     *
     * @param file 文件
     */
    public static void createIfAbsent(File file) {
        create(file);
    }

    /**
     * 创建目录，目录已存在时直接返回。
     *
     * @param dir 目录
     */
    public static void mkdirsIfAbsent(File dir) {
        if (dir != null) {
            dir.mkdirs();
        }
    }

    /**
     * 递归删除文件或目录（目录连同内容一并删除）。
     *
     * @param file 文件或目录
     * @return 删除成功返回 {@code true}；不存在或参数为空返回 {@code false}
     */
    public static boolean delete(File file) {
        if (file == null || !file.exists()) {
            return false;
        }
        try {
            deleteInternal(file.toPath());
            return true;
        } catch (IOException e) {
            throw new IllegalStateException("删除失败: " + file.getPath(), e);
        }
    }

    /**
     * 静默删除，失败时不抛异常。
     *
     * @param file 文件或目录
     */
    public static void deleteQuietly(File file) {
        try {
            delete(file);
        } catch (RuntimeException ignored) {
        }
    }

    /**
     * 递归删除（内部实现，非空目录先删除内容）。
     *
     * @param path 文件或目录路径
     * @throws IOException 删除失败
     */
    private static void deleteInternal(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (Stream<Path> children = Files.list(path)) {
                for (Path child : children.toList()) {
                    deleteInternal(child);
                }
            }
        }
        Files.deleteIfExists(path);
    }

    /**
     * 复制文件或目录（目录递归复制），已存在目标时覆盖。
     *
     * @param source 源文件或目录
     * @param target 目标文件或目录
     * @return 复制成功返回 {@code true}；参数为空返回 {@code false}
     */
    public static boolean copy(File source, File target) {
        if (source == null || target == null) {
            return false;
        }
        try {
            copyInternal(source.toPath(), target.toPath());
            return true;
        } catch (IOException e) {
            throw new IllegalStateException("复制失败: " + source.getPath() + " -> " + target.getPath(), e);
        }
    }

    /**
     * 递归复制（内部实现）。
     *
     * @param source 源路径
     * @param target 目标路径
     * @throws IOException 复制失败
     */
    private static void copyInternal(Path source, Path target) throws IOException {
        if (Files.isDirectory(source)) {
            Files.createDirectories(target);
            try (Stream<Path> children = Files.list(source)) {
                for (Path child : children.toList()) {
                    copyInternal(child, target.resolve(child.getFileName()));
                }
            }
        } else {
            Path parent = target.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * 移动文件或目录（目录移动内容），已存在目标时覆盖。
     *
     * @param source 源文件或目录
     * @param target 目标文件或目录
     * @return 移动成功返回 {@code true}；参数为空返回 {@code false}
     */
    public static boolean move(File source, File target) {
        if (source == null || target == null) {
            return false;
        }
        try {
            if (Files.isDirectory(source.toPath())) {
                copyInternal(source.toPath(), target.toPath());
                deleteInternal(source.toPath());
            } else {
                Path parent = target.toPath().getParent();
                if (parent != null) {
                    Files.createDirectories(parent);
                }
                Files.move(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            return true;
        } catch (IOException e) {
            throw new IllegalStateException("移动失败: " + source.getPath() + " -> " + target.getPath(), e);
        }
    }

    /**
     * 获取文件或目录大小（目录递归累加，字节）。
     *
     * @param file 文件或目录
     * @return 大小（字节）；不存在或参数为空返回 0
     */
    public static long size(File file) {
        if (file == null || !file.exists()) {
            return 0L;
        }
        try {
            return sizeInternal(file.toPath());
        } catch (IOException e) {
            throw new IllegalStateException("计算大小失败: " + file.getPath(), e);
        }
    }

    /**
     * 递归计算大小（内部实现）。
     *
     * @param path 文件或目录路径
     * @return 大小（字节）
     * @throws IOException 计算失败
     */
    private static long sizeInternal(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            long total = 0L;
            try (Stream<Path> children = Files.list(path)) {
                for (Path child : children.toList()) {
                    total += sizeInternal(child);
                }
            }
            return total;
        }
        return Files.size(path);
    }

    /**
     * 获取文件扩展名（不含点，小写；无扩展名返回空串）。
     *
     * @param file 文件
     * @return 扩展名；参数为空返回空串
     */
    public static String getExtension(File file) {
        return file == null ? "" : getExtension(file.getName());
    }

    /**
     * 获取文件名扩展名（不含点，小写；无扩展名返回空串）。
     *
     * @param fileName 文件名
     * @return 扩展名；参数为空返回空串
     */
    public static String getExtension(String fileName) {
        if (StringUtil.isBlank(fileName)) {
            return "";
        }
        int dot = fileName.lastIndexOf('.');
        if (dot < 0 || dot == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(dot + 1).toLowerCase();
    }

    /**
     * 去除文件扩展名（a.txt -&gt; a）。
     *
     * @param fileName 文件名
     * @return 去除扩展名后的文件名；参数为空返回原值
     */
    public static String removeExtension(String fileName) {
        if (StringUtil.isBlank(fileName)) {
            return fileName;
        }
        int dot = fileName.lastIndexOf('.');
        if (dot < 0) {
            return fileName;
        }
        return fileName.substring(0, dot);
    }

    /**
     * 读取文件内容为字符串（UTF-8）。
     *
     * @param path 文件路径
     * @return 文件内容
     */
    public static String readUTF8(String path) {
        return readString(new File(path), StandardCharsets.UTF_8);
    }

    /**
     * 写入字符串到文件（UTF-8，覆盖写）。
     *
     * @param path    文件路径
     * @param content 内容
     */
    public static void writeUTF8(String path, String content) {
        writeString(new File(path), content, StandardCharsets.UTF_8);
    }

    /**
     * 读取文件内容为字符串（UTF-8）。
     *
     * @param file 文件
     * @return 文件内容
     */
    public static String readString(File file) {
        return readString(file, StandardCharsets.UTF_8);
    }

    /**
     * 读取文件内容为字符串（指定字符集）。
     *
     * @param file    文件
     * @param charset 字符集
     * @return 文件内容
     */
    public static String readString(File file, Charset charset) {
        if (file == null || !file.isFile()) {
            throw new IllegalArgumentException("文件不存在: " + (file == null ? "null" : file.getPath()));
        }
        try {
            return Files.readString(file.toPath(), charset);
        } catch (IOException e) {
            throw new IllegalStateException("读取文件失败: " + file.getPath(), e);
        }
    }

    /**
     * 写入字符串到文件（UTF-8，覆盖写）。
     *
     * @param file    文件
     * @param content 内容
     */
    public static void writeString(File file, String content) {
        writeString(file, content, StandardCharsets.UTF_8);
    }

    /**
     * 写入字符串到文件（指定字符集，覆盖写）。
     *
     * @param file    文件
     * @param content 内容
     * @param charset 字符集
     */
    public static void writeString(File file, String content, Charset charset) {
        if (file == null) {
            throw new IllegalArgumentException("文件不能为空");
        }
        try {
            Path parent = file.toPath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(file.toPath(), content == null ? "" : content, charset);
        } catch (IOException e) {
            throw new IllegalStateException("写入文件失败: " + file.getPath(), e);
        }
    }

    /**
     * 读取文件全部字节。
     *
     * @param file 文件
     * @return 文件字节数组
     */
    public static byte[] readBytes(File file) {
        if (file == null || !file.isFile()) {
            throw new IllegalArgumentException("文件不存在: " + (file == null ? "null" : file.getPath()));
        }
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new IllegalStateException("读取文件失败: " + file.getPath(), e);
        }
    }

    /**
     * 写入字节数组到文件（覆盖写）。
     *
     * @param file  文件
     * @param bytes 字节数组
     */
    public static void writeBytes(File file, byte[] bytes) {
        if (file == null) {
            throw new IllegalArgumentException("文件不能为空");
        }
        try {
            Path parent = file.toPath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.write(file.toPath(), bytes == null ? new byte[0] : bytes);
        } catch (IOException e) {
            throw new IllegalStateException("写入文件失败: " + file.getPath(), e);
        }
    }

    /**
     * 获取目录下直接子文件与子目录（不含递归）。
     *
     * @param dir 目录
     * @return 直接子项列表；参数为空或非目录时返回空列表
     */
    public static List<File> listFiles(File dir) {
        if (dir == null || !dir.isDirectory()) {
            return List.of();
        }
        File[] files = dir.listFiles();
        return files == null ? List.of() : List.of(files);
    }
}
