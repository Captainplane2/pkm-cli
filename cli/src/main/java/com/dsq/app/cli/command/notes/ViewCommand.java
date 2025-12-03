package com.dsq.app.cli.command.notes;

import com.dsq.app.cli.command.core.AbstractCommand;
import com.dsq.app.cli.command.core.CliCommand;
import com.dsq.app.cli.command.util.ArgumentValidator;
import com.dsq.app.controller.NoteController;

@CliCommand({"view", "show"})
public class ViewCommand extends AbstractCommand {
    private final NoteController noteController;

    public ViewCommand(NoteController noteController) {
        super("view", "查看笔记详情");
        this.noteController = noteController;
    }

    @Override
    public void execute(String[] args) {
        ArgumentValidator.validateArgumentCount(args, 1, getName());
        String id = ArgumentValidator.validateNoteId(args[0], getName());
        noteController.viewNote(id);
    }

    @Override
    public void printUsage() {
        System.out.println("用法: view <笔记ID>");
        System.out.println("示例: view 123e4567-e89b-12d3-a456-426614174000");
        System.out.println("注意: 笔记ID为UUID格式");
    }
}