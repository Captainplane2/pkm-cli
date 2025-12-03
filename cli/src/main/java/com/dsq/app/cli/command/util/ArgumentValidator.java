package com.dsq.app.cli.command.util;

/**
 * 参数验证器 - 统一的命令参数验证
 */
public class ArgumentValidator {

    /**
     * 验证参数数量
     */
    public static void validateArgumentCount(String[] args, int min, int max, String commandName) {
        if (args.length < min) {
            throw new IllegalArgumentException("参数不足。用法: " + getCommandUsage(commandName));
        }
        if (max != -1 && args.length > max) {
            throw new IllegalArgumentException("参数过多。用法: " + getCommandUsage(commandName));
        }
    }

    /**
     * 验证参数数量范围（方法重载）
     */
    public static void validateArgumentCount(String[] args, int expected, String commandName) {
        validateArgumentCount(args, expected, expected, commandName);
    }

    /**
     * 验证笔记ID格式
     */
    public static String validateNoteId(String id, String commandName) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("笔记ID不能为空");
        }
        String trimmedId = id.trim();
        // 简单的UUID格式验证（36字符长度）
        if (trimmedId.length() != 36) {
            throw new IllegalArgumentException("笔记ID格式不正确，应为UUID格式");
        }
        return trimmedId;
    }

    /**
     * 验证标签格式
     */
    public static String validateTag(String tag, String commandName) {
        if (tag == null || tag.trim().isEmpty()) {
            throw new IllegalArgumentException("标签不能为空");
        }
        String trimmedTag = tag.trim();
        if (trimmedTag.contains(" ")) {
            throw new IllegalArgumentException("标签不能包含空格");
        }
        if (trimmedTag.length() > 20) {
            throw new IllegalArgumentException("标签长度不能超过20个字符");
        }
        return trimmedTag;
    }

    /**
     * 验证搜索关键词
     */
    public static String validateKeyword(String keyword, String commandName) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("搜索关键词不能为空");
        }
        String trimmedKeyword = keyword.trim();
        if (trimmedKeyword.length() < 2) {
            throw new IllegalArgumentException("搜索关键词至少需要2个字符");
        }
        return trimmedKeyword;
    }

    /**
     * 验证笔记标题
     */
    public static String validateTitle(String title, String commandName) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("笔记标题不能为空");
        }
        String trimmedTitle = title.trim();
        if (trimmedTitle.length() > 100) {
            throw new IllegalArgumentException("笔记标题长度不能超过100个字符");
        }
        return trimmedTitle;
    }

    /**
     * 验证笔记内容
     */
    public static String validateContent(String content, String commandName) {
        if (content == null) {
            return "";
        }
        String trimmedContent = content.trim();
        if (trimmedContent.length() > 10000) {
            throw new IllegalArgumentException("笔记内容长度不能超过10000个字符");
        }
        return trimmedContent;
    }

    private static String getCommandUsage(String commandName) {
        switch (commandName) {
            case "new": return "new <标题> [内容]";
            case "view": return "view <笔记ID>";
            case "edit": return "edit <笔记ID> <新内容>";
            case "delete": return "delete <笔记ID>";
            case "tag": return "tag <笔记ID> <标签>";
            case "untag": return "untag <笔记ID> <标签>";
            case "search": return "search <关键词>";
            case "tag-search": return "tag-search <关键词>";
            default: return commandName + " [参数]";
        }
    }
}