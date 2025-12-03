package com.dsq.app.cli.command.test;

import com.dsq.app.cli.command.core.AbstractCommand;
import com.dsq.app.cli.command.core.CliCommand;

@CliCommand({"test", "test-cmd"})
public class TestCommand extends AbstractCommand {

    public TestCommand() {
        super("test", "测试命令");
    }

    @Override
    public void execute(String[] args) {
        System.out.println("测试命令执行成功");
        if (args.length > 0) {
            System.out.println("参数: " + String.join(", ", args));
        }
    }

    @Override
    public void printUsage() {
        System.out.println("用法: test [参数]");
        System.out.println("示例: test hello");
    }
}