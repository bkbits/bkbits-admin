package com.bkbits.logging;

import com.bkbits.logging.annotations.Log;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Condition;

@Component
@Condition(onMissingBean = ILogProvider.class)
public class EmptyLogProvider implements ILogProvider {
    @Override
    public String getRemark(Log log) {
        return null;
    }

    @Override
    public String getCreateBy() {
        return "system";
    }
}
