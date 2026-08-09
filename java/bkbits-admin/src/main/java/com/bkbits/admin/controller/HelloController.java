package com.bkbits.admin.controller;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Result;

/**
 * 示例控制器。
 */
@Controller
public class HelloController {

    @Mapping("/hello")
    @Get
    public Result<String> hello() {
        return Result.succeed("hello bkbits");
    }
}
