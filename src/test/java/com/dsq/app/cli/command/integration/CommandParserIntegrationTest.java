package com.dsq.app.cli.command.integration;

import com.dsq.app.cli.CommandParser;
import com.dsq.app.cli.command.core.Command;
import com.dsq.app.cli.command.registry.CommandRegistry;
import com.dsq.app.cli.command.util.CommandHistory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 命令系统集成测试 - 完整功能验证
 */
class CommandSystemIntegrationTest {

    private CommandParser commandParser;
    private CommandRegistry commandRegistry;
    private CommandHistory commandHistory;

    @BeforeEach
    void setUp() {
        commandParser = new CommandParser();
        commandRegistry = commandParser.getCommandRegistry();
        commandHistory = commandParser.getCommandHistory();
    }

    @AfterEach
    void tearDown() {
        // 清理资源
        if (commandParser != null) {
            commandParser.close();
        }
    }

    @Test
    @DisplayName("测试系统初始化完整性")
    void testSystemInitializationIntegrity() {
        // 验证核心组件正确初始化
        assertAll(
                () -> assertNotNull(commandParser, "命令解析器必须正确初始化"),
                () -> assertNotNull(commandRegistry, "命令注册器必须正确初始化"),
                () -> assertNotNull(commandHistory, "命令历史记录器必须正确初始化")
        );

        // 验证命令自动注册成功
        int commandCount = commandRegistry.getCommandCount();
        assertTrue(commandCount >= 35, "系统应该注册足够数量的命令名称，实际: " + commandCount);

        System.out.println("系统初始化验证:");
        System.out.println("  命令名称数量: " + commandCount);
        System.out.println("  唯一命令实例: " + commandRegistry.getAllCommands().size());
    }

    @Test
    @DisplayName("测试无参数命令功能完整性")
    void testNoArgumentCommandsFunctionality() {
        // 测试所有不需要参数的核心命令
        String[][] noArgCommands = {
                {"help", "帮助信息"},
                {"list", "笔记列表"},
                {"tags", "标签列表"},
                {"tag-stats", "标签统计"},
                {"history", "命令历史"},
                {"stats", "系统统计"},
                {"performance", "性能设置"},
                {"reload", "热重载"}
        };

        for (String[] commandInfo : noArgCommands) {
            String commandName = commandInfo[0];
            String functionality = commandInfo[1];

            Command command = commandRegistry.getCommand(commandName);
            assertNotNull(command, functionality + " 命令 '" + commandName + "' 必须存在");

            // 通过parseArgs测试完整执行流程
            assertDoesNotThrow(() ->
                            commandParser.parseArgs(new String[]{commandName}),
                    functionality + " 命令必须能正常执行");

            System.out.println("✓ " + functionality + " (" + commandName + ") 功能正常");
        }
    }

    @Test
    @DisplayName("测试命令别名系统")
    void testCommandAliasSystem() {
        // 验证主要命令和别名指向同一个实例且功能正常
        String[][] aliasGroups = {
                {"help", "?"},
                {"list", "ls"},
                {"view", "show"},
                {"edit", "update"},
                {"delete", "rm", "remove"},
                {"exit", "quit"},
                {"history", "hist"},
                {"stats", "statistics"},
                {"reload", "refresh"},
                {"performance", "perf"}
        };

        for (String[] aliases : aliasGroups) {
            String mainCommand = aliases[0];
            Command mainCmd = commandRegistry.getCommand(mainCommand);
            assertNotNull(mainCmd, "主命令 '" + mainCommand + "' 必须存在");

            for (String alias : aliases) {
                Command aliasCmd = commandRegistry.getCommand(alias);
                assertNotNull(aliasCmd, "别名 '" + alias + "' 必须存在");
                assertSame(mainCmd, aliasCmd, "别名 '" + alias + "' 必须指向主命令 '" + mainCommand + "'");

                System.out.println("✓ 别名 '" + alias + "' -> '" + mainCommand + "' 正确指向");
            }
        }
    }

    @Test
    @DisplayName("测试带参数命令功能")
    void testCommandsWithArguments() {
        // 测试需要参数的命令 - 提供正确的参数
        assertDoesNotThrow(() ->
                        commandParser.parseArgs(new String[]{"new", "测试笔记标题"}),
                "new命令带标题参数应该正常执行");

        assertDoesNotThrow(() ->
                        commandParser.parseArgs(new String[]{"new", "测试标题", "测试内容"}),
                "new命令带标题和内容参数应该正常执行");

        assertDoesNotThrow(() ->
                        commandParser.parseArgs(new String[]{"search", "测试关键词"}),
                "search命令带关键词参数应该正常执行");

        assertDoesNotThrow(() ->
                        commandParser.parseArgs(new String[]{"tag-search", "java"}),
                "tag-search命令带标签关键词应该正常执行");
    }

    @Test
    @DisplayName("测试参数验证系统")
    void testArgumentValidationSystem() {
        // 测试参数验证正常工作 - 这些应该给出友好的错误提示而不是崩溃
        assertDoesNotThrow(() ->
                        commandParser.parseArgs(new String[]{"new"}),
                "new命令参数不足时应该给出友好错误提示");

        assertDoesNotThrow(() ->
                        commandParser.parseArgs(new String[]{"view"}),
                "view命令参数不足时应该给出友好错误提示");

        assertDoesNotThrow(() ->
                        commandParser.parseArgs(new String[]{"edit"}),
                "edit命令参数不足时应该给出友好错误提示");

        assertDoesNotThrow(() ->
                        commandParser.parseArgs(new String[]{"tag"}),
                "tag命令参数不足时应该给出友好错误提示");

        System.out.println("✓ 参数验证系统正常工作");
    }

    @Test
    @DisplayName("测试命令历史记录功能")
    void testCommandHistoryFunctionality() {
        // 直接测试CommandHistory类
        CommandHistory testHistory = new CommandHistory();

        // 测试添加命令
        String[] testCommands = {"help", "list", "tags", "stats"};
        for (String cmd : testCommands) {
            testHistory.addCommand(cmd);
            System.out.println("添加命令: " + cmd + ", 当前历史记录数: " + testHistory.size());
        }

        // 验证历史记录数量
        assertEquals(testCommands.length, testHistory.size(),
                "应该记录所有添加的命令");

        // 验证历史记录内容
        java.util.List<String> history = testHistory.getAllHistory();
        for (int i = 0; i < testCommands.length; i++) {
            assertEquals(testCommands[i], history.get(i),
                    "历史记录第" + (i+1) + "条应该是: " + testCommands[i]);
        }

        // 测试导航功能
        testHistoryNavigation(testHistory, testCommands);

        System.out.println("✓ CommandHistory类功能验证完成");
    }

    private void testHistoryNavigation(CommandHistory history, String[] testCommands) {
        // 重置导航位置到最新
        String next;
        do {
            next = history.getNext();
        } while (next != null && !next.isEmpty());

        // 测试向前导航
        for (int i = testCommands.length - 1; i >= 0; i--) {
            String prevCommand = history.getPrevious();
            assertEquals(testCommands[i], prevCommand,
                    "向前导航第" + (testCommands.length - i) + "次应该返回: " + testCommands[i]);
        }

        // 测试向后导航
        for (int i = 0; i < testCommands.length; i++) {
            String nextCommand = history.getNext();
            if (i < testCommands.length - 1) {
                assertEquals(testCommands[i + 1], nextCommand,
                        "向后导航第" + (i + 1) + "次应该返回: " + testCommands[i + 1]);
            } else {
                assertEquals("", nextCommand, "超出范围应该返回空字符串");
            }
        }

        System.out.println("✓ 历史导航功能正常");
    }


    @Test
    @DisplayName("测试统计和性能功能")
    void testStatisticsAndPerformanceFeatures() {
        // 测试统计功能
        assertDoesNotThrow(() ->
                        commandParser.parseArgs(new String[]{"stats"}),
                "stats命令必须能正常显示统计信息");

        // 测试性能设置功能
        assertDoesNotThrow(() ->
                        commandParser.parseArgs(new String[]{"performance"}),
                "performance命令必须能正常显示设置");

        // 测试时间显示切换
        assertDoesNotThrow(() ->
                        commandParser.parseArgs(new String[]{"stats", "time", "on"}),
                "stats time on 必须能正常工作");

        assertDoesNotThrow(() ->
                        commandParser.parseArgs(new String[]{"stats", "time", "off"}),
                "stats time off 必须能正常工作");

        assertDoesNotThrow(() ->
                        commandParser.parseArgs(new String[]{"performance", "time", "on"}),
                "performance time on 必须能正常工作");

        assertDoesNotThrow(() ->
                        commandParser.parseArgs(new String[]{"performance", "time", "off"}),
                "performance time off 必须能正常工作");
    }

    @Test
    @DisplayName("测试热重载功能稳定性")
    void testHotReloadStability() {
        int initialCount = commandRegistry.getCommandCount();
        assertTrue(initialCount > 0, "初始命令数量应该大于0");

        // 记录重载前的一些关键命令状态
        Command helpBefore = commandRegistry.getCommand("help");
        Command listBefore = commandRegistry.getCommand("list");
        Command statsBefore = commandRegistry.getCommand("stats");

        // 执行热重载
        assertDoesNotThrow(() ->
                        commandParser.parseArgs(new String[]{"reload"}),
                "reload命令必须能正常执行");

        int afterReloadCount = commandRegistry.getCommandCount();

        // 允许命令数量有小幅变化（由于手动注册的别名）
        assertTrue(Math.abs(initialCount - afterReloadCount) <= 2,
                "热重载后命令数量变化应该在2个以内");

        // 验证重载后核心命令仍然可用
        Command helpAfter = commandRegistry.getCommand("help");
        Command listAfter = commandRegistry.getCommand("list");
        Command statsAfter = commandRegistry.getCommand("stats");

        assertNotNull(helpAfter, "重载后help命令必须存在");
        assertNotNull(listAfter, "重载后list命令必须存在");
        assertNotNull(statsAfter, "重载后stats命令必须存在");

        // 验证重载后命令功能正常
        assertDoesNotThrow(() ->
                        commandParser.parseArgs(new String[]{"help"}),
                "重载后help命令必须能正常执行");

        assertDoesNotThrow(() ->
                        commandParser.parseArgs(new String[]{"list"}),
                "重载后list命令必须能正常执行");

        assertDoesNotThrow(() ->
                        commandParser.parseArgs(new String[]{"stats"}),
                "重载后stats命令必须能正常执行");
    }

    @Test
    @DisplayName("测试错误处理能力")
    void testErrorHandlingCapability() {
        // 测试未知命令处理
        assertDoesNotThrow(() ->
                        commandParser.parseArgs(new String[]{"nonexistent-command-12345"}),
                "系统必须能处理未知命令而不崩溃");

        // 测试空输入处理
        assertDoesNotThrow(() ->
                        commandParser.parseArgs(new String[]{""}),
                "系统必须能处理空输入而不崩溃");

        // 测试只有空格的处理
        assertDoesNotThrow(() ->
                        commandParser.parseArgs(new String[]{"   "}),
                "系统必须能处理只有空格的输入而不崩溃");
    }

    @Test
    @DisplayName("测试系统健壮性")
    void testSystemRobustness() {
        // 执行一系列命令验证系统稳定性
        String[][] commandSequence = {
                {"help"},
                {"list"},
                {"tags"},
                {"tag-stats"},
                {"stats"},
                {"performance"},
                {"history"},
                {"reload"},
                {"help"},
                {"stats"}
        };

        for (String[] commandArgs : commandSequence) {
            assertDoesNotThrow(() ->
                            commandParser.parseArgs(commandArgs),
                    "命令序列执行不应该导致系统崩溃: " + String.join(" ", commandArgs));
        }

        System.out.println("✓ 系统健壮性验证完成");
        System.out.println("  执行命令序列: " + commandSequence.length + " 个命令");
        System.out.println("  历史记录数量: " + commandHistory.size());
    }

    @Test
    @DisplayName("测试依赖注入完整性")
    void testDependencyInjectionIntegrity() {
        // 验证所有系统命令都能正常执行（表明依赖注入成功）
        String[] systemCommands = {"help", "exit", "history", "stats", "reload", "performance"};

        for (String commandName : systemCommands) {
            Command command = commandRegistry.getCommand(commandName);
            assertNotNull(command, "系统命令 '" + commandName + "' 必须存在");

            // 对于exit命令，不执行因为它会停止程序
            if (!"exit".equals(commandName)) {
                assertDoesNotThrow(() ->
                                commandParser.parseArgs(new String[]{commandName}),
                        "系统命令 '" + commandName + "' 必须能正常执行（依赖注入成功）");
            }
        }
    }
}