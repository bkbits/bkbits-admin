package com.bkbits.orm;

import java.time.LocalDateTime;

public interface IUpdateBy {
    String getUpdateBy();

    void setUpdateBy(String updateBy);

    LocalDateTime getUpdateTime();

    void setUpdateTime(LocalDateTime updateTime);
}
