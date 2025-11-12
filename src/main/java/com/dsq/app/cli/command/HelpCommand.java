package com.dsq.app.cli.command;

import java.util.Collection;

@CliCommand({"help", "?"})
public class HelpCommand extends AbstractCommand {
    private CommandRegistry commandRegistry;

    public HelpCommand() {
        super("help", "显示帮助信息");
    }

    public HelpCommand(CommandRegistry commandRegistry) {
        super("help", "显示帮助信息");
        this.commandRegistry = commandRegistry;
    }

    // 设置命令注册器
    public void setCommandRegistry(CommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
    }

    @Override
    public void execute(String[] args) {
        if (commandRegistry == null) {
            System.err.println("命令注册器未初始化");
            return;
        }

        System.out.println("个人知识管理系统 - 命令行版本");
        System.out.println("============================= \n");
        System.out.println("可用命令:");

        Collection<Command> commands = commandRegistry.getAllCommands();
        for (Command cmd : commands) {
            System.out.printf("  %-15s - %s\n", cmd.getName(), cmd.getDescription());
        }

        System.out.println("\n输入 'help <命令名>' 查看具体命令用法");

        if (args.length > 0) {
            String specificCommand = args[0];
            Command cmd = commandRegistry.getCommand(specificCommand);
            if (cmd != null) {
                System.out.println("\n" + specificCommand + " 命令详细用法:");
                cmd.printUsage();
            } else {
                System.out.println("未知命令: " + specificCommand);
            }
        }
    }

    @Override
    public void printUsage() {
        System.out.println("用法: help [命令名]");
        System.out.println("示例: help");
        System.out.println("示例: help new");
    }
}