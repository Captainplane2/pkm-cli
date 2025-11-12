package com.dsq.app.cli.command;

import com.dsq.app.controller.TagController;

@CliCommand({"tag-stats", "tag-statistics"})
public class TagStatsCommand extends AbstractCommand {
    private final TagController tagController;

    public TagStatsCommand(TagController tagController) {
        super("tag-stats", "显示标签统计");
        this.tagController = tagController;
    }

    @Override
    public void execute(String[] args) {
        // tag-stats命令不需要参数
        if (args.length > 0) {
            System.out.println("提示: tag-stats命令不需要参数，忽略额外参数");
        }
        tagController.showTagStatistics();
    }

    @Override
    public void printUsage() {
        System.out.println("用法: tag-stats");
        System.out.println("显示标签使用统计");
    }
}