package com.dsq.app.cli;

import com.dsq.app.cli.command.*;

import java.util.Arrays;
import java.util.Scanner;

/**
 * 命令解析器 - 基于反射和注解的自动命令注册
 */
public class CommandParser {
    private final Scanner scanner;
    private boolean isRunning;
    private final CommandRegistry commandRegistry;
    private final CommandHistory commandHistory;

    public CommandParser() {
        this.scanner = new Scanner(System.in);
        this.isRunning = true;
        this.commandHistory = new CommandHistory();

        // 创建命令注册器（会自动扫描和注册命令）
        this.commandRegistry = new CommandRegistry();

        // 设置特殊命令的依赖
        setupCommandDependencies();

        System.out.println("命令系统初始化完成，共加载 " +
                commandRegistry.getCommandCount() + " 个命令");
    }

    /**
     * 设置命令依赖（用于需要特殊依赖的命令）
     */
    private void setupCommandDependencies() {
        // 设置ExitCommand的依赖
        ExitCommand exitCommand = (ExitCommand) commandRegistry.getCommand("exit");
        if (exitCommand != null) {
            exitCommand.setExitAction(() -> this.isRunning = false);
        }

        // 设置HelpCommand的依赖
        HelpCommand helpCommand = (HelpCommand) commandRegistry.getCommand("help");
        if (helpCommand != null) {
            helpCommand.setCommandRegistry(commandRegistry);
        }

        // 设置HistoryCommand的依赖
        HistoryCommand historyCommand = (HistoryCommand) commandRegistry.getCommand("history");
        if (historyCommand != null) {
            historyCommand.setCommandHistory(commandHistory);
        }

        // 手动注册额外的命令别名（保持向后兼容）
        commandRegistry.registerAlias("h", "help");
    }

    /**
     * 解析命令行参数
     */
    public void parseArgs(String[] args) {
        if (args.length == 0) {
            startInteractiveMode();
        } else {
            executeCommand(String.join(" ", args));
        }
    }

    /**
     * 启动交互模式
     */
    private void startInteractiveMode() {
        System.out.println("欢迎使用个人知识管理系统（CLI版）");
        System.out.println("输入 'help' 查看可用命令，输入 'exit' 退出程序");
        System.out.println();

        while (isRunning) {
            System.out.print("pkm> ");
            String input = readCommandWithHistory();

            if (!input.isEmpty()) {
                executeCommand(input);
            }
        }

        System.out.println("感谢使用个人知识管理系统，再见！");
    }

    /**
     * 支持历史记录的读取命令
     */
    private String readCommandWithHistory() {
        String input = scanner.nextLine().trim();
        if (!input.isEmpty()) {
            commandHistory.addCommand(input);
        }
        return input;
    }

    /**
     * 执行命令
     */
    private void executeCommand(String commandLine) {
        String[] parts = parseCommandLine(commandLine);
        if (parts.length == 0) return;

        String commandName = parts[0].toLowerCase();
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);

        Command command = commandRegistry.getCommand(commandName);
        if (command instanceof AbstractCommand) {
            ((AbstractCommand) command).executeSafely(args);
        } else if (command != null) {
            try {
                command.execute(args);
            } catch (Exception e) {
                System.err.println("✘ 执行命令时出错：" + e.getMessage());
                command.printUsage();
            }
        } else {
            System.err.println("✘ 未知命令：" + commandName);
            System.out.println("输入 'help' 查看可用命令");
        }
    }

    /**
     * 解析命令行（支持引号）
     */
    private String[] parseCommandLine(String commandLine) {
        return commandLine.split("\\s+(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
    }

    /**
     * 设置运行状态
     */
    public void setRunning(boolean running) {
        this.isRunning = running;
    }

    /**
     * 获取命令注册器（用于测试）
     */
    public CommandRegistry getCommandRegistry() {
        return commandRegistry;
    }

    /**
     * 获取命令历史（用于测试）
     */
    public CommandHistory getCommandHistory() {
        return commandHistory;
    }

    /**
     * 关闭资源
     */
    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }
}