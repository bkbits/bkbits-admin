package com.bkbits.upload.exception;

/**
 * 上传业务异常。
 */
public class UploadException extends RuntimeException {

    public UploadException(String message) {
        super(message);
    }

    public UploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
