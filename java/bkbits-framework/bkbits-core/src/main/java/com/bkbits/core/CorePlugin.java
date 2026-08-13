package com.bkbits.core;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.Signal;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.core.event.AppPluginLoadEndEvent;
import org.noear.solon.core.util.ResourceUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

/**
 * 核心插件。
 */
public class CorePlugin implements Plugin {

    /**
     * banner 资源路径，classpath 中存在时优先加载
     */
    private static final String BANNER_PATH = "banner.txt";

    /**
     * 启动 banner
     */
    private static final String[] DEFAULT_BANNER = {
            "██████╗  ██╗  ██╗ ██████╗  ██████╗  ████████╗ ███████╗",
            "██╔══██╗ ██║ ██╔╝ ██╔══██╗ ╚══██╔╝  ╚══██╔══╝ ██╔════╝",
            "██████╔╝ █████╔╝  ██████╔╝   ██║       ██║    ███████╗",
            "██╔══██╗ ██╔═██╗  ██╔══██╗   ██║       ██║    ╚════██║",
            "██████╔╝ ██║  ██╗ ██████╔╝   ██║       ██║    ███████║",
            "╚═════╝  ╚═╝  ╚═╝ ╚═════╝    ╚═╝       ╚═╝    ╚══════╝"
    };

    private final long startupTime = System.currentTimeMillis();

    @Override
    public void start(AppContext context) throws Throwable {
        // 打印启动 banner
        printBanner();
        context.beanMake(BkbitsProperties.class);
        context.onEvent(AppPluginLoadEndEvent.class,
                e -> context.beanScan("com.bkbits")
        );

        // 启动完成后展示 ip 与端口
        context.onEvent(AppLoadEndEvent.class, e -> printStartedInfo());
    }

    /**
     * 打印启动 banner（classpath 中存在 banner.txt 时优先加载，否则使用默认图案）
     */
    private void printBanner() throws Throwable {
        List<String> lines = new ArrayList<>();
        InputStream in = ResourceUtil.getResourceAsStream(BANNER_PATH);
        if (in != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            }
        }
        if (lines.isEmpty()) {
            lines.addAll(Arrays.asList(DEFAULT_BANNER));
        }
        lines.forEach(System.out::println);
        System.out.println(" :: " + appName() + " ::");
    }

    /**
     * 打印启动完成信息（本机 ip 与端口）
     */
    private void printStartedInfo() {
        String contextPath = Solon.cfg().serverContextPath();
        String localIp = localIp();

        List<Signal> httpSignals = new ArrayList<>();
        for (Signal signal : Solon.app().signals()) {
            String protocol = signal.protocol();
            if (protocol != null && protocol.toLowerCase().contains("http") && signal.port() > 0) {
                httpSignals.add(signal);
            }
        }

        System.out.println("    Started " + appName() + " in " + (System.currentTimeMillis() - startupTime) + "ms");
        if (httpSignals.isEmpty()) {
            // 无 http 信号时回退到配置端口
            int port = Solon.cfg().serverPort();
            System.out.println("    Local:   http://127.0.0.1:" + port + contextPath);
            System.out.println("    Network: http://" + localIp + ":" + port + contextPath);
            printDocUrl(port, contextPath);
            return;
        }
        for (Signal signal : httpSignals) {
            String protocol = signal.protocol();
            System.out.println("    Local:   " + protocol + "://127.0.0.1:" + signal.port() + contextPath);
            System.out.println("    Network: " + protocol + "://" + localIp + ":" + signal.port() + contextPath);
        }
        printDocUrl(httpSignals.get(0).port(), contextPath);
    }

    /**
     * 打印 knife4j 文档地址（未集成 knife4j 或未开启时静默跳过）
     */
    private void printDocUrl(int port, String contextPath) {
        try {
            Class<?> resolverClass = Class.forName("com.github.xiaoymin.knife4j.solon.extension.OpenApiExtensionResolver");
            Object resolver = Solon.context().getBean(resolverClass);
            if (resolver == null) {
                return;
            }
            Object setting = resolverClass.getMethod("getSetting").invoke(resolver);
            if ((boolean) setting.getClass().getMethod("isEnable").invoke(setting)) {
                System.out.println("    Doc:     http://localhost:" + port + contextPath + "/doc.html");
            }
        } catch (Throwable ignored) {
            // 未集成 knife4j 或 API 变动时静默跳过
        }
    }

    private String appName() {
        String name = Solon.cfg().appName();
        return Utils.isEmpty(name) ? "solon" : name;
    }

    /**
     * 获取本机非回环 IPv4 地址
     */
    private static String localIp() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                if (!ni.isUp() || ni.isLoopback() || ni.isVirtual()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address instanceof Inet4Address && !address.isLoopbackAddress()) {
                        return address.getHostAddress();
                    }
                }
            }
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "127.0.0.1";
        }
    }
}
