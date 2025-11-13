package com.dsq.app.cli.command.system;

import com.dsq.app.cli.command.core.AbstractCommand;
import com.dsq.app.cli.command.core.CliCommand;
import com.dsq.app.cli.command.core.Command;
import com.dsq.app.cli.command.registry.CommandRegistry;
import com.dsq.app.service.NoteService;
import com.dsq.app.service.TagService;

import java.util.Collection;

@CliCommand({"stats", "statistics"})
public class StatisticsCommand extends AbstractCommand {
    private final NoteService noteService;
    private final TagService tagService;
    private CommandRegistry commandRegistry;

    // 主要构造方法 - 包含必需的依赖
    public StatisticsCommand(NoteService noteService, TagService tagService) {
        super("stats", "显示系统统计信息");
        this.noteService = noteService;
        this.tagService = tagService;
    }

    // 完整构造方法 - 包含所有依赖
    public StatisticsCommand(NoteService noteService, TagService tagService, CommandRegistry commandRegistry) {
        super("stats", "显示系统统计信息");
        this.noteService = noteService;
        this.tagService = tagService;
        this.commandRegistry = commandRegistry;
    }

    // 设置命令注册器（在依赖注入后调用）
    public void setCommandRegistry(CommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
    }

    @Override
    public void execute(String[] args) {
        try {
            var notes = noteService.getAllNotes();
            var tags = tagService.getAllTags();

            System.out.println("=== 系统统计信息 ===");
            System.out.println("总笔记数: " + notes.size());
            System.out.println("总标签数: " + tags.size());

            if (!notes.isEmpty()) {
                double avgTags = (double) notes.stream()
                        .mapToInt(n -> n.getTags().size())
                        .sum() / notes.size();
                System.out.printf("平均标签数: %.2f\n", avgTags);
            }

            // 显示命令执行统计（如果commandRegistry可用）
            if (commandRegistry != null) {
                displayCommandStatistics();
            }

            // 处理时间显示参数
            if (args.length >= 2 && "time".equals(args[0])) {
                if ("on".equals(args[1])) {
                    setShowExecutionTime(true);
                    System.out.println("✔ 已开启执行时间显示");
                } else if ("off".equals(args[1])) {
                    setShowExecutionTime(false);
                    System.out.println("✔ 已关闭执行时间显示");
                }
            }

            // 显示性能设置状态
            System.out.println("\n=== 性能设置 ===");
            System.out.println("执行时间显示: " + (showExecutionTime ? "开启" : "关闭"));
            System.out.println("使用 'stats time on/off' 切换执行时间显示");

        } catch (Exception e) {
            System.err.println("✘ 获取统计信息失败: " + e.getMessage());
        }
    }

    /**
     * 显示命令执行统计
     */
    private void displayCommandStatistics() {
        Collection<Command> commands = commandRegistry.getAllCommands();

        long totalExecutions = 0;
        System.out.println("\n=== 命令执行统计 ===");

        for (Command cmd : commands) {
            if (cmd instanceof AbstractCommand) {
                AbstractCommand abstractCmd = (AbstractCommand) cmd;
                AbstractCommand.CommandStatistics stats = abstractCmd.getStatistics();

                if (stats.getExecutionCount() > 0) {
                    System.out.printf("  %-15s: 调用 %d 次, 平均 %.2f ms\n",
                            stats.getCommandName(),
                            stats.getExecutionCount(),
                            stats.getAverageTime());
                    totalExecutions += stats.getExecutionCount();
                }
            }
        }

        if (totalExecutions > 0) {
            System.out.println("总命令执行次数: " + totalExecutions);
        } else {
            System.out.println("暂无命令执行统计");
        }
    }

    @Override
    public void printUsage() {
        System.out.println("用法: stats [time on|off]");
        System.out.println("显示系统统计信息");
        System.out.println("  time on    - 开启执行时间显示");
        System.out.println("  time off   - 关闭执行时间显示");
        System.out.println("示例: stats");
        System.out.println("示例: stats time on");
    }
}