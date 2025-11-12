package com.dsq.app.cli.command;

import com.dsq.app.controller.NoteController;

public class UntagCommand extends AbstractCommand {
    private final NoteController noteController;

    public UntagCommand(NoteController noteController) {
        super("untag", "为笔记移除标签");
        this.noteController = noteController;
    }

    @Override
    public void execute(String[] args) {
        ArgumentValidator.validateArgumentCount(args, 2, getName());
        String id = ArgumentValidator.validateNoteId(args[0], getName());
        String tag = ArgumentValidator.validateTag(args[1], getName());
        noteController.removeTag(id, tag);
    }

    @Override
    public void printUsage() {
        System.out.println("用法: untag <笔记ID> <标签>");
        System.out.println("示例: untag 123e4567 java");
        System.out.println("限制: 标签不能包含空格，长度≤20字符");
    }
}