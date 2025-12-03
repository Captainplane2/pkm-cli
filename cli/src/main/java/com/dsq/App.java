package com.dsq;

import com.dsq.app.cli.CommandParser;

/**
 * 个人知识管理系统 - 主程序入口
 */
public class App {
    public static void main(String[] args) {
        CommandParser parser = new CommandParser();
        try {
            parser.parseArgs(args);
        } finally {
            parser.close();
        }
    }
}