package com.dsq.app.cli.command;

import com.dsq.app.service.NoteService;
import com.dsq.app.service.TagService;

@CliCommand({"stats", "statistics"})
public class StatisticsCommand extends AbstractCommand {
    private final NoteService noteService;
    private final TagService tagService;

    public StatisticsCommand(NoteService noteService, TagService tagService) {
        super("stats", "显示系统统计信息");
        this.noteService = noteService;
        this.tagService = tagService;
    }

    @Override
    public void execute(String[] args) {
        var notes = noteService.getAllNotes();
        var tags = tagService.getAllTags();

        System.out.println("系统统计信息:");
        System.out.println("总笔记数: " + notes.size());
        System.out.println("总标签数: " + tags.size());
        if (!notes.isEmpty()) {
            double avgTags = (double) notes.stream()
                    .mapToInt(n -> n.getTags().size())
                    .sum() / notes.size();
            System.out.printf("平均标签数: %.2f\n", avgTags);
        }
    }

    @Override
    public void printUsage() {
        System.out.println("用法: stats");
        System.out.println("显示知识库的统计信息");
    }
}