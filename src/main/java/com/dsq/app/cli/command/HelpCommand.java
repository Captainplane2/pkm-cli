package com.dsq.app.cli.command;

import java.util.Collection;

public class HelpCommand extends AbstractCommand {
    private final CommandRegistry commandRegistry;

    public HelpCommand(CommandRegistry commandRegistry) {
        super("help", "显示帮助信息");
        this.commandRegistry = commandRegistry;
    }

    @Override
    public void execute(String[] args) {
        System.out.println("个人知识管理系统 - 命令行版本");
        System.out.println("============================= \n");
        System.out.println("可用命令:");

        Collection<Command> commands = commandRegistry.getAllCommands();
        for (Command cmd : commands) {
            System.out.printf("  %-15s - %s\n", cmd.getName(), cmd.getDescription());
        }

        System.out.println("\n输入 'help <命令名>' 查看具体命令用法");

        // 如果指定了具体命令，显示详细用法
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