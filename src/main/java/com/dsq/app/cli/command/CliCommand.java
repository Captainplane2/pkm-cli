package com.dsq.app.cli.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 命令行命令注解 - 用于标记命令行命令类
 * 支持多个命令名称（别名）
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CliCommand {
    /**
     * 命令名称数组，支持多个别名
     */
    String[] value();
}