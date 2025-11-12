package com.dsq.app.cli.command;

import com.dsq.app.controller.TagController;

public class TagSearchCommand extends AbstractCommand {
    private final TagController tagController;

    public TagSearchCommand(TagController tagController) {
        super("tag-search", "按标签模糊搜索");
        this.tagController = tagController;
    }

    @Override
    public void execute(String[] args) {
        ArgumentValidator.validateArgumentCount(args, 1, getName());
        String keyword = ArgumentValidator.validateKeyword(removeQuotes(args[0]), getName());
        tagController.fuzzySearchByTag(keyword);
    }

    @Override
    public void printUsage() {
        System.out.println("用法: tag-search <关键词>");
        System.out.println("示例: tag-search java");
        System.out.println("限制: 关键词至少2个字符");
    }
}