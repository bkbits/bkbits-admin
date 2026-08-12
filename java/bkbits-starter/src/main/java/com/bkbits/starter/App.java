package com.bkbits.starter;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Import;
import org.noear.solon.annotation.SolonMain;
import org.noear.solon.scheduling.annotation.EnableScheduling;

/**
 * 启动类。
 */
@SolonMain
@EnableScheduling
public class App {
    public static void main(String[] args) {
        Solon.start(App.class, args, (app) -> {
            app.enableWebSocket(true);
        });
    }
}
