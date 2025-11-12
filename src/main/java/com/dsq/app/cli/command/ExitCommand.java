package com.dsq.app.cli.command;

public class ExitCommand extends AbstractCommand {
    private Runnable exitAction;

    public ExitCommand(Runnable exitAction) {
        super("exit", "退出程序");
        this.exitAction = exitAction;
    }

    @Override
    public void execute(String[] args) {
        System.out.println("感谢使用个人知识管理系统！");
        if (exitAction != null) {
            exitAction.run();
        }
    }

    @Override
    public void printUsage() {
        System.out.println("用法：exit");
        System.out.println("别名：quit");
    }
}