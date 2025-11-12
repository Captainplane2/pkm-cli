package com.dsq.app.cli.command;

import java.util.List;

@CliCommand({"history", "hist"})
public class HistoryCommand extends AbstractCommand {
    private CommandHistory commandHistory;

    public HistoryCommand() {
        super("history", "显示命令历史记录");
    }

    public HistoryCommand(CommandHistory commandHistory) {
        super("history", "显示命令历史记录");
        this.commandHistory = commandHistory;
    }

    // 设置历史管理器
    public void setCommandHistory(CommandHistory commandHistory) {
        this.commandHistory = commandHistory;
    }

    @Override
    public void execute(String[] args) {
        if (commandHistory == null) {
            System.out.println("历史记录功能未启用");
            return;
        }

        List<String> history = commandHistory.getAllHistory();

        if (history.isEmpty()) {
            System.out.println("暂无命令历史记录");
            return;
        }

        System.out.println("命令历史记录 (" + history.size() + " 条):");
        for (int i = 0; i < history.size(); i++) {
            System.out.printf("%3d. %s\n", i + 1, history.get(i));
        }
    }

    @Override
    public void printUsage() {
        System.out.println("用法: history");
        System.out.println("显示最近执行的命令历史记录");
    }
}