package com.dsq.app.cli.command.notes;

import com.dsq.app.cli.command.core.AbstractCommand;
import com.dsq.app.cli.command.core.CliCommand;
import com.dsq.app.cli.command.util.ArgumentValidator;
import com.dsq.app.controller.NoteController;

@CliCommand({"search", "find"})
public class SearchCommand extends AbstractCommand {
    private final NoteController noteController;

    public SearchCommand(NoteController noteController) {
        super("search", "搜索笔记");
        this.noteController = noteController;
    }

    @Override
    public void execute(String[] args) {
        ArgumentValidator.validateArgumentCount(args, 1, getName());
        String keyword = ArgumentValidator.validateKeyword(removeQuotes(args[0]), getName());
        noteController.searchNotes(keyword);
    }

    @Override
    public void printUsage() {
        System.out.println("用法: search <关键词>");
        System.out.println("示例: search \"Java编程\"");
        System.out.println("限制: 关键词至少2个字符");
    }
}