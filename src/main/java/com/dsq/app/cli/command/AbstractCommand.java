package com.dsq.app.cli.command;

/**
 * 抽象命令基类 - 提供通用功能
 */
public abstract class AbstractCommand implements Command {
    protected final String name;
    protected final String description;

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
     * 安全的命令执行 - 统一异常处理
     */
    public void executeSafely(String[] args) {
        try {
            execute(args);
        } catch (IllegalArgumentException e) {
            System.err.println("✘ 参数错误: " + e.getMessage());
            printUsage();
        } catch (Exception e) {
            System.err.println("✘ 执行命令时出错: " + e.getMessage());
            System.err.println("   详细错误: " + e.getClass().getSimpleName());
            printUsage();
        }
    }
}