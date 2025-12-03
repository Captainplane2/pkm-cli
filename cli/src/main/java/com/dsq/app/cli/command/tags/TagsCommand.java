package com.dsq.app.cli.command.tags;

import com.dsq.app.cli.command.core.AbstractCommand;
import com.dsq.app.cli.command.core.CliCommand;
import com.dsq.app.controller.TagController;

@CliCommand({"tags", "list-tags"})
public class TagsCommand extends AbstractCommand {
    private final TagController tagController;

    public TagsCommand(TagController tagController) {
        super("tags", "列出所有标签");
        this.tagController = tagController;
    }

    @Override
    public void execute(String[] args) {
        // tags命令不需要参数
        if (args.length > 0) {
            System.out.println("提示: tags命令不需要参数，忽略额外参数");
        }
        tagController.listAllTags();
    }

    @Override
    public void printUsage() {
        System.out.println("用法: tags");
        System.out.println("列出所有标签");
    }
}