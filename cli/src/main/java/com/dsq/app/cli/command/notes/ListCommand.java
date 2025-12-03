package com.dsq.app.cli.command.notes;

import com.dsq.app.cli.command.core.AbstractCommand;
import com.dsq.app.cli.command.core.CliCommand;
import com.dsq.app.controller.NoteController;

@CliCommand({"list", "ls"})
public class ListCommand extends AbstractCommand {
    private final NoteController noteController;

    public ListCommand(NoteController noteController) {
        super("list", "列出所有笔记");
        this.noteController = noteController;
    }

    @Override
    public void execute(String[] args) {
        // list命令不需要参数
        if (args.length > 0) {
            System.out.println("提示: list命令不需要参数，忽略额外参数");
        }
        noteController.listAllNotes();
    }

    @Override
    public void printUsage() {
        System.out.println("用法: list");
        System.out.println("列出所有笔记");
    }
}