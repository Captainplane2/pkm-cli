package com.dsq.app.cli.command;

import com.dsq.app.controller.NoteController;

public class NewCommand extends AbstractCommand {
    private final NoteController noteController;

    public NewCommand(NoteController noteController) {
        super("new", "创建新笔记");
        this.noteController = noteController;
    }

    @Override
    public void execute(String[] args) {
        ArgumentValidator.validateArgumentCount(args, 1, 2, getName());

        String title = ArgumentValidator.validateTitle(removeQuotes(args[0]), getName());
        String content = args.length > 1 ? ArgumentValidator.validateContent(removeQuotes(args[1]), getName()) : "";

        noteController.createNote(title, content);
    }

    @Override
    public void printUsage() {
        System.out.println("用法: new <标题> [内容]");
        System.out.println("示例: new \"Java笔记\" \"面向对象编程的三大特性...\"");
        System.out.println("注意: 标题为必填项，内容为可选项");
        System.out.println("限制: 标题≤100字符，内容≤10000字符");
    }
}