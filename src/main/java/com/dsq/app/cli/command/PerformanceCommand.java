package com.dsq.app.cli.command;

@CliCommand({"performance", "perf"})
public class PerformanceCommand extends AbstractCommand {

    public PerformanceCommand() {
        super("performance", "性能设置管理");
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            displayCurrentSettings();
            return;
        }

        if (args.length >= 2 && "time".equals(args[0])) {
            if ("on".equals(args[1]) || "enable".equals(args[1])) {
                setShowExecutionTime(true);
                System.out.println("✔ 已开启命令执行时间显示");
            } else if ("off".equals(args[1]) || "disable".equals(args[1])) {
                setShowExecutionTime(false);
                System.out.println("✔ 已关闭命令执行时间显示");
            } else {
                System.err.println("✘ 无效的参数，使用 'on' 或 'off'");
                printUsage();
            }
        } else {
            System.err.println("✘ 无效的命令参数");
            printUsage();
        }
    }

    /**
     * 显示当前性能设置
     */
    private void displayCurrentSettings() {
        System.out.println("=== 性能设置 ===");
        System.out.println("执行时间显示: " + (showExecutionTime ? "开启" : "关闭"));
        System.out.println("\n可用选项:");
        System.out.println("  performance time on   - 开启执行时间显示");
        System.out.println("  performance time off  - 关闭执行时间显示");
    }

    @Override
    public void printUsage() {
        System.out.println("用法: performance [time on|off]");
        System.out.println("管理性能相关设置");
        System.out.println("示例:");
        System.out.println("  performance          - 显示当前设置");
        System.out.println("  performance time on  - 开启执行时间显示");
        System.out.println("  performance time off - 关闭执行时间显示");
    }
}