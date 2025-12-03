package com.dsq.app.cli.command.registry;

import com.dsq.app.cli.command.core.Command;
import com.dsq.app.cli.command.registry.CommandRegistry;
import com.dsq.app.cli.command.test.TestCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 命令注册器单元测试 - 精简版
 */
class CommandRegistryTest {

    private CommandRegistry commandRegistry;

    @BeforeEach
    void setUp() {
        commandRegistry = new CommandRegistry();
    }

    @Test
    @DisplayName("测试基本命令自动注册")
    void testBasicCommandsAutoRegistered() {
        assertAll(
                () -> assertTrue(commandRegistry.hasCommand("new"), "new命令应该被注册"),
                () -> assertTrue(commandRegistry.hasCommand("list"), "list命令应该被注册"),
                () -> assertTrue(commandRegistry.hasCommand("help"), "help命令应该被注册"),
                () -> assertTrue(commandRegistry.hasCommand("exit"), "exit命令应该被注册")
        );
    }

    @Test
    @DisplayName("测试命令别名自动注册")
    void testCommandAliasesAutoRegistered() {
        assertAll(
                () -> assertTrue(commandRegistry.hasCommand("create"), "create别名应该被注册"),
                () -> assertTrue(commandRegistry.hasCommand("ls"), "ls别名应该被注册"),
                () -> assertTrue(commandRegistry.hasCommand("quit"), "quit别名应该被注册")
        );
    }

    @Test
    @DisplayName("测试测试命令自动注册")
    void testTestCommandsAutoRegistered() {
        assertAll(
                () -> assertTrue(commandRegistry.hasCommand("test"), "test命令应该被自动注册"),
                () -> assertTrue(commandRegistry.hasCommand("test-cmd"), "test-cmd别名应该被自动注册"),
                () -> assertTrue(commandRegistry.hasCommand("another"), "another命令应该被自动注册")
        );
    }

    @Test
    @DisplayName("测试命令获取和执行")
    void testCommandRetrievalAndExecution() {
        Command testCommand = commandRegistry.getCommand("test");
        assertNotNull(testCommand, "应该能获取到test命令");
        assertEquals("test", testCommand.getName(), "命令名称应该匹配");

        assertDoesNotThrow(() -> testCommand.execute(new String[]{}),
                "命令执行不应该抛出异常");
    }

    @Test
    @DisplayName("测试手动注册命令")
    void testManualCommandRegistration() {
        int initialCount = commandRegistry.getCommandCount();

        Command manualCommand = new TestCommand();
        commandRegistry.registerCommand(manualCommand);

        assertTrue(commandRegistry.hasCommand("test"), "手动注册的命令应该存在");
        assertEquals(initialCount, commandRegistry.getCommandCount(),
                "命令数量不应该重复增加（测试命令已自动注册）");
    }

    @Test
    @DisplayName("测试命令统计")
    void testCommandStatistics() {
        Collection<Command> allCommands = commandRegistry.getAllCommands();
        assertFalse(allCommands.isEmpty(), "命令列表不应该为空");

        int commandCount = commandRegistry.getCommandCount();
        assertEquals(commandCount, allCommands.size(), "命令数量应该匹配");

        System.out.println("当前注册的命令数量: " + commandCount);
    }
}