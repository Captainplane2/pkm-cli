package com.dsq.app.cli;

import com.dsq.app.controller.NoteController;
import com.dsq.app.controller.TagController;
import com.dsq.app.service.NoteFileStorageService;
import com.dsq.app.service.NoteService;
import com.dsq.app.service.StorageService;
import com.dsq.app.service.TagService;

import java.util.Scanner;

/**
 * 命令解析器 - 程序的核心，负责命令解析和分发
 */
public class CommandParser {
    private final NoteController noteController;
    private final TagController tagController;
    private final Scanner scanner;
    private boolean isRunning;

    public CommandParser() {
        // 依赖注入装配 - 构建完整的应用依赖链
        StorageService storageService = new NoteFileStorageService();
        NoteService noteService = new NoteService(storageService);
        TagService tagService = new TagService(storageService);

        this.noteController = new NoteController(noteService);
        this.tagController = new TagController(tagService);
        this.scanner = new Scanner(System.in);
        this.isRunning = true;
    }

    //启动程序，解析启动参数
    public void parseArgs(String[] args) {
        if (args.length == 0) {
            startInteractiveMode();
        } else {
            executeCommand(String.join(" ", args));
        }
    }

    //启动时输出主菜单文本
    private void startInteractiveMode() {
        System.out.println("欢迎使用个人知识管理系统（CLI版）");
        System.out.println("输入 'help' 查看可用命令，输入 'exit' 退出程序");
        System.out.println();

        while (isRunning) {
            System.out.print("pkm> ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                break;
            }

            executeCommand(input);
        }

        System.out.println("感谢使用个人知识管理系统，再见！");
    }


    //解析并执行输入的命令
    private void executeCommand(String input) {
        // 检查输入是否为空或仅包含空白字符
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        // 解析命令行，将输入字符串分割成命令部分数组
        String[] parts = parseCommandLine(input);
        // 检查解析后的数组是否为空
        if (parts.length == 0) {
            return;
        }

        // 将命令转换为小写，实现大小写不敏感
        String command = parts[0].toLowerCase();
        // 创建一个新的字符串数组，用于存储命令参数
        // 数组长度为原始数组长度减1（去掉命令部分）
        String[] args = new String[parts.length - 1];
        // 使用System.arraycopy方法复制参数
        // 源数组：parts
        // 源位置：1（跳过第一个元素，即命令本身）
        // 目标数组：args
        // 目标位置：0（从args数组的开始位置填充）
        // 复制长度：args.length（即parts.length-1）
        System.arraycopy(parts, 1, args, 0, args.length);

        try {
            switch (command) {
                case "new":
                    handleNewCommand(args);
                    break;
                case "list":
                    handleListCommand(args);
                    break;
                case "view":
                    handleViewCommand(args);
                    break;
                case "edit":
                    handleEditCommand(args);
                    break;
                case "delete":
                    handleDeleteCommand(args);
                    break;
                case "tag":
                    handleTagCommand(args);
                    break;
                case "untag":
                    handleUntagCommand(args);
                    break;
                case "search":
                    handleSearchCommand(args);
                    break;
                case "tags":
                    handleTagsCommand(args);
                    break;
                case "tag-stats":
                    handleTagStatsCommand(args);
                    break;
                case "tag-search":
                    handleTagSearchCommand(args);
                    break;
                case "help":
                    handleHelpCommand();
                    break;
                default:
                    System.err.println("✘ 未知命令: " + command);
                    System.out.println("输入 'help' 查看可用命令");
            }
        } catch (Exception e) {
            System.err.println("✘ 命令执行出错: " + e.getMessage());
        }
    }

    private String[] parseCommandLine(String input) {
        // 简单的命令解析，支持带引号的参数
        return input.split("\\s+(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
    }

    //命令实现
    private void handleNewCommand(String[] args) {
        // 检查参数数量是否足够，至少需要一个参数
        if (args.length < 1) {
            // 打印错误信息，提示正确的命令用法
            System.err.println("✘ 用法: new <标题> [内容]");
            return;
        }

        String title = removeQuotes(args[0]);

// 使用三元运算符判断命令行参数长度是否大于1
// 如果大于1，则对第二个参数(args[1])执行removeQuotes方法去除引号后赋值给content变量
// 否则，将content变量初始化为空字符串
        String content = args.length > 1 ? removeQuotes(args[1]) : "";
        noteController.createNote(title, content);
    }

    private void handleListCommand(String[] args) {
        noteController.listAllNotes();
    }

    private void handleViewCommand(String[] args) {
        if (args.length < 1) {
            System.err.println("✘ 用法: view <笔记ID>");
            return;
        }
        noteController.viewNote(args[0]);
    }

    private void handleEditCommand(String[] args) {
        if (args.length < 2) {
            System.err.println("✘ 用法: edit <笔记ID> <新内容>");
            return;
        }
        String id = args[0];
        String newContent = removeQuotes(args[1]);
        noteController.editNoteContent(id, newContent);
    }

    private void handleDeleteCommand(String[] args) {
        if (args.length < 1) {
            System.err.println("✘ 用法: delete <笔记ID>");
            return;
        }
        noteController.deleteNote(args[0]);
    }

    private void handleTagCommand(String[] args) {
        if (args.length < 2) {
            System.err.println("✘ 用法: tag <笔记ID> <标签>");
            return;
        }
        noteController.addTag(args[0], args[1]);
    }

    private void handleUntagCommand(String[] args) {
        if (args.length < 2) {
            System.err.println("✘ 用法: untag <笔记ID> <标签>");
            return;
        }
        noteController.removeTag(args[0], args[1]);
    }

    private void handleSearchCommand(String[] args) {
        if (args.length < 1) {
            System.err.println("✘ 用法: search <关键词>");
            return;
        }
        String keyword = removeQuotes(args[0]);
        noteController.searchNotes(keyword);
    }

    private void handleTagsCommand(String[] args) {
        tagController.listAllTags();
    }

    private void handleTagStatsCommand(String[] args) {
        tagController.showTagStatistics();
    }

    private void handleTagSearchCommand(String[] args) {
        if (args.length < 1) {
            System.err.println("✘ 用法: tag-search <关键词>");
            return;
        }
        String keyword = removeQuotes(args[0]);
        tagController.fuzzySearchByTag(keyword);
    }

    private void handleHelpCommand() {
        System.out.println("可用命令:");
        System.out.println("  new <标题> [内容]        - 创建新笔记");
        System.out.println("  list                    - 列出所有笔记");
        System.out.println("  view <笔记ID>           - 查看笔记详情");
        System.out.println("  edit <笔记ID> <内容>    - 编辑笔记内容");
        System.out.println("  delete <笔记ID>         - 删除笔记");
        System.out.println("  tag <笔记ID> <标签>     - 为笔记添加标签");
        System.out.println("  untag <笔记ID> <标签>   - 为笔记移除标签");
        System.out.println("  search <关键词>         - 搜索笔记");
        System.out.println("  tags                    - 列出所有标签");
        System.out.println("  tag-stats               - 显示标签统计");
        System.out.println("  tag-search <关键词>     - 按标签模糊搜索");
        System.out.println("  help                    - 显示此帮助信息");
        System.out.println("  exit                    - 退出程序");
    }

    private String removeQuotes(String str) {
        if (str.startsWith("\"") && str.endsWith("\"")) {
            return str.substring(1, str.length() - 1);
        }
        return str;
    }

    public void close() {
        scanner.close();
    }
}