package com.dsq.app.cli.command.execution;

import com.dsq.app.cli.command.core.Command;
import com.dsq.app.cli.command.registry.CommandRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 命令执行功能测试 - 精简版
 */
class CommandExecutionTest {

    private CommandRegistry commandRegistry;

    @BeforeEach
    void setUp() {
        commandRegistry = new CommandRegistry();
    }

    @Test
    @DisplayName("测试核心命令执行")
    void testCoreCommandsExecution() {
        assertAll(
                () -> testCommandExecution("help"),
                () -> testCommandExecution("exit"),
                () -> testCommandExecution("list"),
                () -> testCommandExecution("stats"),
                () -> testCommandExecution("history")
        );
    }

    @Test
    @DisplayName("测试系统命令执行")
    void testSystemCommandsExecution() {
        assertAll(
                () -> testCommandExecution("reload"),
                () -> testCommandExecution("performance"),
                () -> testCommandWithArgs("stats", new String[]{"time", "on"}),
                () -> testCommandWithArgs("performance", new String[]{"time", "off"})
        );
    }

    @Test
    @DisplayName("测试测试命令执行")
    void testTestCommandsExecution() {
        assertAll(
                () -> testCommandExecution("test"),
                () -> testCommandWithArgs("test", new String[]{"param1", "param2"}),
                () -> testCommandExecution("another")
        );
    }

    private void testCommandExecution(String commandName) {
        Command command = commandRegistry.getCommand(commandName);
        assertNotNull(command, commandName + "命令应该存在");
        assertDoesNotThrow(() -> command.execute(new String[]{}),
                commandName + "命令执行不应该抛出异常");
    }

    private void testCommandWithArgs(String commandName, String[] args) {
        Command command = commandRegistry.getCommand(commandName);
        assertNotNull(command, commandName + "命令应该存在");
        assertDoesNotThrow(() -> command.execute(args),
                commandName + "带参数执行不应该抛出异常");
    }
}