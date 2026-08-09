package com.bkbits.starter;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Import;
import org.noear.solon.annotation.SolonMain;

/**
 * 启动类。
 */
@SolonMain
@Import(scanPackages = "com.bkbits")
public class App {

    public static void main(String[] args) {
        Solon.start(App.class, args);
    }
}
