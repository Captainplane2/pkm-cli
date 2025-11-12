package com.dsq.app.cli.command;

@CliCommand({"exit", "quit"})
public class ExitCommand extends AbstractCommand {
    private Runnable exitAction;

    public ExitCommand() {
        super("exit", "退出程序");
    }

    public ExitCommand(Runnable exitAction) {
        super("exit", "退出程序");
        this.exitAction = exitAction;
    }

    // 设置退出动作
    public void setExitAction(Runnable exitAction) {
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