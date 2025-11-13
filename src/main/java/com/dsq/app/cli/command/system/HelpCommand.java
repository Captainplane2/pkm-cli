package com.dsq.app.cli.command.system;

import com.dsq.app.cli.command.core.AbstractCommand;
import com.dsq.app.cli.command.core.CliCommand;
import com.dsq.app.cli.command.core.Command;
import com.dsq.app.cli.command.registry.CommandRegistry;

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

        // 如果没有指定具体命令，显示完整帮助
        if (args.length == 0) {
            displayFullHelp();
        } else {
            // 显示具体命令的帮助
            displaySpecificCommandHelp(args[0]);
        }
    }

    /**
     * 显示完整帮助信息
     */
    private void displayFullHelp() {
        System.out.println("个人知识管理系统 - 命令行版本");
        System.out.println("============================= \n");

        displayNoteCommands();
        displayTagCommands();
        displaySystemCommands();
        displayPerformanceCommands();

        System.out.println("\n输入 'help <命令名>' 查看具体命令用法");
        System.out.println("示例: help new");
        System.out.println("示例: help stats");
    }

    /**
     * 显示笔记相关命令
     */
    private void displayNoteCommands() {
        System.out.println("=== 笔记管理命令 ===");
        displayCommand("new, create", "创建新笔记", "new <标题> [内容]");
        displayCommand("list, ls", "列出所有笔记", "list");
        displayCommand("view, show", "查看笔记详情", "view <笔记ID>");
        displayCommand("edit, update", "编辑笔记内容", "edit <笔记ID> <新内容>");
        displayCommand("delete, remove, rm", "删除笔记", "delete <笔记ID>");
        displayCommand("search, find", "搜索笔记", "search <关键词>");
        System.out.println();
    }

    /**
     * 显示标签相关命令
     */
    private void displayTagCommands() {
        System.out.println("=== 标签管理命令 ===");
        displayCommand("tag, add-tag", "为笔记添加标签", "tag <笔记ID> <标签>");
        displayCommand("untag, remove-tag", "为笔记移除标签", "untag <笔记ID> <标签>");
        displayCommand("tags, list-tags", "列出所有标签", "tags");
        displayCommand("tag-stats, tag-statistics", "显示标签统计", "tag-stats");
        displayCommand("tag-search, search-tag", "按标签模糊搜索", "tag-search <关键词>");
        System.out.println();
    }

    /**
     * 显示系统命令
     */
    private void displaySystemCommands() {
        System.out.println("=== 系统命令 ===");
        displayCommand("help, ?", "显示帮助信息", "help [命令名]");
        displayCommand("exit, quit", "退出程序", "exit");
        displayCommand("history, hist", "显示命令历史", "history");
        displayCommand("reload, refresh", "重新加载命令系统", "reload [verbose]");
        System.out.println();
    }

    /**
     * 显示性能相关命令
     */
    private void displayPerformanceCommands() {
        System.out.println("=== 性能管理命令 ===");
        displayCommand("stats, statistics", "显示系统统计信息", "stats [time on|off]");
        displayCommand("performance, perf", "性能设置管理", "performance [time on|off]");
        System.out.println();
    }

    /**
     * 显示单个命令的帮助信息
     */
    private void displayCommand(String names, String description, String usage) {
        System.out.printf("  %-25s - %s\n", names, description);
        System.out.printf("    %s\n", usage);
    }

    /**
     * 显示具体命令的详细帮助
     */
    private void displaySpecificCommandHelp(String commandName) {
        Command cmd = commandRegistry.getCommand(commandName);
        if (cmd != null) {
            System.out.println(commandName + " 命令详细用法:");
            System.out.println("描述: " + cmd.getDescription());
            cmd.printUsage();

            // 为特定命令提供额外信息
            displayAdditionalCommandInfo(commandName, cmd);
        } else {
            System.out.println("未知命令: " + commandName);
            System.out.println("输入 'help' 查看所有可用命令");
        }
    }

    /**
     * 为特定命令显示额外信息
     */
    private void displayAdditionalCommandInfo(String commandName, Command cmd) {
        switch (commandName) {
            case "stats":
                System.out.println("\n额外信息:");
                System.out.println("  - 显示笔记数量、标签数量、平均标签数");
                System.out.println("  - 显示命令执行统计（调用次数、平均时间）");
                System.out.println("  - 使用 'stats time on' 开启执行时间显示");
                System.out.println("  - 使用 'stats time off' 关闭执行时间显示");
                break;

            case "performance":
                System.out.println("\n额外信息:");
                System.out.println("  - 管理命令执行性能相关设置");
                System.out.println("  - 可以开启/关闭执行时间显示");
                System.out.println("  - 不影响命令功能，只影响显示");
                break;

            case "reload":
                System.out.println("\n额外信息:");
                System.out.println("  - 重新扫描命令包，加载新的命令类");
                System.out.println("  - 保持现有的命令历史和执行统计");
                System.out.println("  - 使用 'reload verbose' 显示详细命令列表");
                System.out.println("  - 主要用于开发调试和动态扩展");
                break;

            case "history":
                System.out.println("\n额外信息:");
                System.out.println("  - 显示最近执行的命令历史记录");
                System.out.println("  - 历史记录在会话期间有效");
                System.out.println("  - 退出程序后历史记录会清空");
                break;

            case "new":
                System.out.println("\n额外信息:");
                System.out.println("  - 标题为必填项，内容为可选项");
                System.out.println("  - 标题长度限制: 100字符");
                System.out.println("  - 内容长度限制: 10000字符");
                System.out.println("  - 支持带引号的参数");
                break;

            case "search":
                System.out.println("\n额外信息:");
                System.out.println("  - 在标题、内容和标签中搜索关键词");
                System.out.println("  - 搜索不区分大小写");
                System.out.println("  - 关键词至少需要2个字符");
                break;
        }
    }

    @Override
    public void printUsage() {
        System.out.println("用法: help [命令名]");
        System.out.println("示例:");
        System.out.println("  help           - 显示所有命令帮助");
        System.out.println("  help new       - 显示new命令详细用法");
        System.out.println("  help stats     - 显示stats命令详细用法");
    }
}