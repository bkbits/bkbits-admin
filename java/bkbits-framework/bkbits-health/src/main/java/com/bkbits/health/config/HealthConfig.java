package com.bkbits.health.config;

import com.bkbits.health.detector.AppHealthDetector;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Init;
import org.noear.solon.health.detector.DetectorManager;

/**
 * 健康检查配置。
 */
@Configuration
public class HealthConfig {

    /**
     * 注册自定义健康检测器。
     */
    @Init
    public void init() {
        DetectorManager.add(new AppHealthDetector());
    }
}
