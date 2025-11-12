package com.dsq.app.cli.command;

import com.dsq.app.controller.NoteController;

public class EditCommand extends AbstractCommand {
    private final NoteController noteController;

    public EditCommand(NoteController noteController) {
        super("edit", "编辑笔记内容");
        this.noteController = noteController;
    }

    @Override
    public void execute(String[] args) {
        ArgumentValidator.validateArgumentCount(args, 2, getName());
        String id = ArgumentValidator.validateNoteId(args[0], getName());
        String newContent = ArgumentValidator.validateContent(removeQuotes(args[1]), getName());
        noteController.editNoteContent(id, newContent);
    }

    @Override
    public void printUsage() {
        System.out.println("用法: edit <笔记ID> <新内容>");
        System.out.println("示例: edit 123e4567 \"新的笔记内容\"");
        System.out.println("限制: 内容≤10000字符");
    }
}