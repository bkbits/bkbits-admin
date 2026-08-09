package com.bkbits.logging;

import com.bkbits.logging.annotations.Log;

public interface ILogProvider {
    String getRemark(Log log);

    String getCreateBy();
}
