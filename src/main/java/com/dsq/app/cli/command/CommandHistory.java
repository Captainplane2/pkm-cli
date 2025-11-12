package com.dsq.app.cli.command;

import java.util.ArrayList;
import java.util.List;

/**
 * 命令历史管理器 - 记录用户执行的命令
 */
public class CommandHistory {
    private final List<String> history;
    private final int maxSize;
    private int currentIndex;

    public CommandHistory() {
        this(100); // 默认保存100条历史记录
    }

    public CommandHistory(int maxSize) {
        this.history = new ArrayList<>();
        this.maxSize = maxSize;
        this.currentIndex = -1;
    }

    /**
     * 添加命令到历史记录
     */
    public void addCommand(String commandLine) {
        if (commandLine != null && !commandLine.trim().isEmpty()) {
            // 避免重复记录连续相同的命令
            if (history.isEmpty() || !history.get(history.size() - 1).equals(commandLine)) {
                history.add(commandLine);

                // 限制历史记录大小
                if (history.size() > maxSize) {
                    history.remove(0);
                }
            }
            currentIndex = history.size();
        }
    }

    /**
     * 获取上一条命令
     */
    public String getPrevious() {
        if (history.isEmpty()) {
            return "";
        }
        currentIndex = Math.max(0, currentIndex - 1);
        return history.get(currentIndex);
    }

    /**
     * 获取下一条命令
     */
    public String getNext() {
        if (history.isEmpty()) {
            return "";
        }
        currentIndex = Math.min(history.size(), currentIndex + 1);
        if (currentIndex >= history.size()) {
            return "";
        }
        return history.get(currentIndex);
    }

    /**
     * 获取所有历史记录
     */
    public List<String> getAllHistory() {
        return new ArrayList<>(history);
    }

    /**
     * 清空历史记录
     */
    public void clear() {
        history.clear();
        currentIndex = -1;
    }

    /**
     * 获取历史记录数量
     */
    public int size() {
        return history.size();
    }
}