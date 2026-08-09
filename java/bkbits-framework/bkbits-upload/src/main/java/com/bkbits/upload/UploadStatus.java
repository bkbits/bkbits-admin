package com.bkbits.upload;

/**
 * 上传任务状态常量。
 *
 * <p>对应 upload_status 字典：S=成功完成，A=任务丢弃，W=等待上传。</p>
 */
public final class UploadStatus {

    /** 等待上传 */
    public static final String WAITING = "W";

    /** 成功完成 */
    public static final String SUCCESS = "S";

    /** 任务丢弃 */
    public static final String ABANDONED = "A";

    private UploadStatus() {
    }
}
