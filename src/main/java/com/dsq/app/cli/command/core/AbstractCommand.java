package com.dsq.app.cli.command.core;

/**
 * 抽象命令基类 - 提供通用功能
 */
public abstract class AbstractCommand implements Command {
    protected final String name;
    protected final String description;
    private long executionCount = 0;
    private long totalExecutionTime = 0;
    protected static boolean showExecutionTime = false;

    protected AbstractCommand(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    /**
     * 移除字符串两端的引号
     */
    protected String removeQuotes(String str) {
        if (str != null && str.startsWith("\"") && str.endsWith("\"")) {
            return str.substring(1, str.length() - 1);
        }
        return str;
    }

    /**
     * 安全的命令执行 - 统一异常处理和时间统计
     */
    public void executeSafely(String[] args) {
        long startTime = System.nanoTime();
        try {
            execute(args);
            recordExecutionTime(startTime);
        } catch (IllegalArgumentException e) {
            System.err.println("✘ 参数错误: " + e.getMessage());
            printUsage();
        } catch (Exception e) {
            System.err.println("✘ 执行命令时出错: " + e.getMessage());
            System.err.println("   详细错误: " + e.getClass().getSimpleName());
            printUsage();
        }
    }

    /**
     * 记录执行时间
     */
    private void recordExecutionTime(long startTime) {
        long endTime = System.nanoTime();
        long duration = endTime - startTime;

        executionCount++;
        totalExecutionTime += duration;

        if (showExecutionTime) {
            double milliseconds = duration / 1_000_000.0;
            System.out.printf("执行时间: %.2f ms\n", milliseconds);
        }
    }

    /**
     * 获取执行统计信息
     */
    public CommandStatistics getStatistics() {
        return new CommandStatistics(name, executionCount, totalExecutionTime);
    }

    /**
     * 设置是否显示执行时间
     */
    public static void setShowExecutionTime(boolean show) {
        showExecutionTime = show;
    }

    /**
     * 重置统计信息
     */
    public void resetStatistics() {
        executionCount = 0;
        totalExecutionTime = 0;
    }

    /**
     * 命令统计信息类
     */
    public static class CommandStatistics {
        private final String commandName;
        private final long executionCount;
        private final long totalExecutionTime;

        public CommandStatistics(String commandName, long executionCount, long totalExecutionTime) {
            this.commandName = commandName;
            this.executionCount = executionCount;
            this.totalExecutionTime = totalExecutionTime;
        }

        public String getCommandName() { return commandName; }
        public long getExecutionCount() { return executionCount; }
        public long getTotalExecutionTime() { return totalExecutionTime; }

        public double getAverageTime() {
            return executionCount > 0 ? totalExecutionTime / (double) executionCount / 1_000_000.0 : 0;
        }
    }
}