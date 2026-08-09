package com.bkbits.orm;

import java.time.LocalDateTime;

public interface ICreateBy {
    String getCreateBy();

    void setCreateBy(String createBy);

    LocalDateTime getCreateTime();

    void setCreateTime(LocalDateTime createTime);
}
