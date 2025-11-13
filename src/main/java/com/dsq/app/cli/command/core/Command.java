package com.dsq.app.cli.command.core;

/**
 * 命令接口 - 所有具体命令必须实现此接口
 */
public interface Command {
    void execute(String[] args);
    String getName();
    String getDescription();

    default void printUsage() {
        System.out.println("用法: " + getName());
    }
}