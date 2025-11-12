package com.dsq.app.cli.command;

import com.dsq.app.controller.NoteController;

@CliCommand({"tag", "add-tag"})
public class TagCommand extends AbstractCommand {
    private final NoteController noteController;

    public TagCommand(NoteController noteController) {
        super("tag", "为笔记添加标签");
        this.noteController = noteController;
    }

    @Override
    public void execute(String[] args) {
        ArgumentValidator.validateArgumentCount(args, 2, getName());
        String id = ArgumentValidator.validateNoteId(args[0], getName());
        String tag = ArgumentValidator.validateTag(args[1], getName());
        noteController.addTag(id, tag);
    }

    @Override
    public void printUsage() {
        System.out.println("用法: tag <笔记ID> <标签>");
        System.out.println("示例: tag 123e4567 java");
        System.out.println("限制: 标签不能包含空格，长度≤20字符");
    }
}