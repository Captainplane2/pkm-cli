package com.dsq.app.cli.command.system;

import com.dsq.app.cli.command.core.AbstractCommand;
import com.dsq.app.cli.command.core.CliCommand;
import com.dsq.app.cli.command.registry.CommandRegistry;

@CliCommand({"reload", "refresh"})
public class ReloadCommand extends AbstractCommand {
    private CommandRegistry commandRegistry;

    public ReloadCommand() {
        super("reload", "重新加载命令系统");
    }

    public ReloadCommand(CommandRegistry commandRegistry) {
        super("reload", "重新加载命令系统");
        this.commandRegistry = commandRegistry;
    }

    // 设置命令注册器
    public void setCommandRegistry(CommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
    }

    @Override
    public void execute(String[] args) {
        if (commandRegistry == null) {
            System.err.println("✘ 命令注册器未初始化，无法重新加载");
            return;
        }

        System.out.println("开始重新加载命令系统...");

        try {
            int beforeCount = commandRegistry.getCommandCount();
            commandRegistry.reloadCommands();  // 这里现在可以正常调用了
            int afterCount = commandRegistry.getCommandCount();

            System.out.println("✔ 命令系统重新加载完成");
            System.out.println("重载前命令数: " + beforeCount);
            System.out.println("重载后命令数: " + afterCount);
            System.out.println("变化: " + (afterCount - beforeCount));

            if (args.length > 0 && "verbose".equals(args[0])) {
                displayLoadedCommands();
            }

        } catch (Exception e) {
            System.err.println("✘ 重新加载命令失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 显示已加载的命令
     */
    private void displayLoadedCommands() {
        System.out.println("\n=== 已加载的命令 ===");
        commandRegistry.getAllCommands().forEach(cmd -> {
            System.out.printf("  %-15s - %s\n", cmd.getName(), cmd.getDescription());
        });
    }

    @Override
    public void printUsage() {
        System.out.println("用法: reload [verbose]");
        System.out.println("重新加载命令系统，支持动态添加新命令");
        System.out.println("  verbose - 显示详细的命令列表");
        System.out.println("示例:");
        System.out.println("  reload      - 重新加载命令");
        System.out.println("  reload verbose - 重新加载并显示命令列表");
    }
}