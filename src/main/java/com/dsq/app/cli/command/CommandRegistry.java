package com.dsq.app.cli.command;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 命令注册器 - 管理所有可用命令
 */
public class CommandRegistry {
    private final Map<String, Command> commands = new HashMap<>();

    /**
     * 注册命令
     */
    public void registerCommand(Command command) {
        commands.put(command.getName().toLowerCase(), command);
    }

    /**
     * 注册命令别名
     */
    public void registerAlias(String alias, String commandName) {
        Command command = commands.get(commandName.toLowerCase());
        if (command != null) {
            commands.put(alias.toLowerCase(), command);
        }
    }

    /**
     * 获取命令
     */
    public Command getCommand(String name) {
        return commands.get(name.toLowerCase());
    }

    /**
     * 检查命令是否存在
     */
    public boolean hasCommand(String name) {
        return commands.containsKey(name.toLowerCase());
    }

    /**
     * 获取所有命令
     */
    public Collection<Command> getAllCommands() {
        return commands.values();
    }

    /**
     * 获取命令数量
     */
    public int getCommandCount() {
        return commands.size();
    }
}