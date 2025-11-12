package com.dsq.app.cli.command;

import com.dsq.app.controller.NoteController;

@CliCommand({"delete", "remove", "rm"})
public class DeleteCommand extends AbstractCommand {
    private final NoteController noteController;

    public DeleteCommand(NoteController noteController) {
        super("delete", "删除笔记");
        this.noteController = noteController;
    }

    @Override
    public void execute(String[] args) {
        ArgumentValidator.validateArgumentCount(args, 1, getName());
        String id = ArgumentValidator.validateNoteId(args[0], getName());
        noteController.deleteNote(id);
    }

    @Override
    public void printUsage() {
        System.out.println("用法: delete <笔记ID>");
        System.out.println("示例: delete 123e4567-e89b-12d3-a456-426614174000");
        System.out.println("警告: 此操作不可撤销！");
    }
}