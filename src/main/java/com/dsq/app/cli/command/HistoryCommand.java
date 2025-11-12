package com.dsq.app.cli.command;

import java.util.List;

/**
 * 历史命令 - 查看和管理命令历史
 */
public class HistoryCommand extends AbstractCommand {
    private final CommandHistory commandHistory;

    public HistoryCommand(CommandHistory commandHistory) {
        super("history", "显示命令历史记录");
        this.commandHistory = commandHistory;
    }

    @Override
    public void execute(String[] args) {
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