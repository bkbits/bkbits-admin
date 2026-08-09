package com.bkbits.upload;

import lombok.Data;
import org.noear.solon.annotation.BindProps;
import org.noear.solon.annotation.Configuration;

@BindProps(prefix = "bkbits.prefix")
@Configuration
@Data
public class BkbitsUploadProperties {
    /** 类型，目前仅支持 local */
    private String type = "local";

    /** 临时目录 */
    private String temp = "./tmp";

    /** 上传目录 */
    private String upload = "./upload";

    /** 上传分片文件大小 */
    private Long pieceSize = 4 * 1024 * 1024L;
}
