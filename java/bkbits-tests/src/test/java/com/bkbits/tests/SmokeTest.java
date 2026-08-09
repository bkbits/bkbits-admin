package com.bkbits.tests;

import org.junit.jupiter.api.Test;
import org.noear.solon.test.SolonTest;

/**
 * 冒烟测试：验证 solon 容器可正常启动。
 */
@SolonTest
public class SmokeTest {

    @Test
    public void contextLoads() {
        // 容器启动成功即通过
    }
}
