package com.bkbits.health.detector;

import org.noear.solon.health.detector.AbstractDetector;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 应用健康检测器。
 */
public class AppHealthDetector extends AbstractDetector {

    @Override
    public String getName() {
        return "app";
    }

    @Override
    public Map<String, Object> getInfo() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("name", "bkbits");
        info.put("status", "UP");
        return info;
    }
}
