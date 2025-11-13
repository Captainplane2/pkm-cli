package com.dsq.app.cli.command.test;

import com.dsq.app.cli.command.core.AbstractCommand;
import com.dsq.app.cli.command.core.CliCommand;

@CliCommand({"another", "alt"})
public class AnotherTestCommand extends AbstractCommand {

    public AnotherTestCommand() {
        super("another", "另一个测试命令");
    }

    @Override
    public void execute(String[] args) {
        System.out.println("另一个测试命令执行成功");
    }

    @Override
    public void printUsage() {
        System.out.println("用法: another");
    }
}