package com.dsq.app.cli;

import com.dsq.app.cli.command.*;
import com.dsq.app.controller.NoteController;
import com.dsq.app.controller.TagController;
import com.dsq.app.service.NoteFileStorageService;
import com.dsq.app.service.NoteService;
import com.dsq.app.service.StorageService;
import com.dsq.app.service.TagService;

import java.util.Arrays;
import java.util.Scanner;

/**
 * 命令解析器 - 程序的核心，负责命令解析和分发
 */
public class CommandParser {
    private final Scanner scanner;
    private boolean isRunning;
    private final CommandRegistry commandRegistry;
    private final CommandHistory commandHistory; // 新增历史记录

    public CommandParser() {
        this.scanner = new Scanner(System.in);
        this.isRunning = true;
        this.commandRegistry = new CommandRegistry();
        this.commandHistory = new CommandHistory(); // 初始化历史记录
        initializeCommands();
    }

    /**
     * 初始化并注册所有命令
     */
    private void initializeCommands() {
        // 依赖注入装配 - 构建完整的应用依赖链
        StorageService storageService = new NoteFileStorageService();
        NoteService noteService = new NoteService(storageService);
        TagService tagService = new TagService(storageService);

        NoteController noteController = new NoteController(noteService);
        TagController tagController = new TagController(tagService);

        // 注册基本命令
        commandRegistry.registerCommand(new NewCommand(noteController));
        commandRegistry.registerCommand(new ListCommand(noteController));
        commandRegistry.registerCommand(new ViewCommand(noteController));
        commandRegistry.registerCommand(new EditCommand(noteController));
        commandRegistry.registerCommand(new DeleteCommand(noteController));
        commandRegistry.registerCommand(new TagCommand(noteController));
        commandRegistry.registerCommand(new UntagCommand(noteController));
        commandRegistry.registerCommand(new SearchCommand(noteController));
        commandRegistry.registerCommand(new TagsCommand(tagController));
        commandRegistry.registerCommand(new TagStatsCommand(tagController));
        commandRegistry.registerCommand(new TagSearchCommand(tagController));
        commandRegistry.registerCommand(new HelpCommand(commandRegistry));
        commandRegistry.registerCommand(new ExitCommand(() -> {this.isRunning = false;
        }));
        commandRegistry.registerCommand(new HistoryCommand(commandHistory)); //注册历史记录命令

        // 注册命令别名
        commandRegistry.registerAlias("quit", "exit");
        commandRegistry.registerAlias("h", "history");
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
        System.out.println("提示: 使用 ↑↓ 箭头键浏览历史命令");
        System.out.println();

        while (isRunning) {
            System.out.print("pkm> ");
            String input = scanner.nextLine().trim();

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
        // 简化版：实际项目中可以使用JLine等库实现完整的命令行历史
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
     * 关闭资源
     */
    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }
}