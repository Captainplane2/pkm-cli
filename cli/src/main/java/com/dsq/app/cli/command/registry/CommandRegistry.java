package com.dsq.app.cli.command.registry;

import com.dsq.app.cli.command.core.Command;
import com.dsq.app.cli.command.core.CliCommand;
import com.dsq.app.cli.command.core.AbstractCommand;
import com.dsq.app.cli.command.system.ExitCommand;
import com.dsq.app.cli.command.system.HistoryCommand;
import com.dsq.app.cli.command.system.StatisticsCommand;
import com.dsq.app.controller.NoteController;
import com.dsq.app.controller.TagController;
import com.dsq.app.service.NoteFileStorageService;
import com.dsq.app.service.NoteService;
import com.dsq.app.service.StorageService;
import com.dsq.app.service.TagService;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * 基于注解的命令注册器 - 完整的依赖注入支持
 */
public class CommandRegistry {
    private final Map<String, Command> commands = new HashMap<>();
    private final NoteController noteController;
    private final TagController tagController;
    private final NoteService noteService;
    private final TagService tagService;

    public CommandRegistry() {
        // 首先初始化所有核心依赖
        StorageService storageService = new NoteFileStorageService();
        this.noteService = new NoteService(storageService);
        this.tagService = new TagService(storageService);
        this.noteController = new NoteController(noteService);
        this.tagController = new TagController(tagService);

        // 然后注册命令
        autoRegisterCommands();
    }

    /**
     * 自动注册所有带有@CliCommand注解的命令类
     */
    private void autoRegisterCommands() {
        Reflections reflections = new Reflections("com.dsq.app.cli.command");
        Set<Class<?>> commandClasses = reflections.getTypesAnnotatedWith(CliCommand.class);

        System.out.println("发现 " + commandClasses.size() + " 个命令类");

        int successCount = 0;
        int failCount = 0;

        for (Class<?> clazz : commandClasses) {
            if (Command.class.isAssignableFrom(clazz)) {
                try {
                    boolean registered = registerCommandClass((Class<? extends Command>) clazz);
                    if (registered) {
                        successCount++;
                    } else {
                        failCount++;
                        System.err.println("注册失败: " + clazz.getSimpleName());
                    }
                } catch (Exception e) {
                    failCount++;
                    System.err.println("注册命令类失败: " + clazz.getName() + " - " + e.getMessage());
                }
            }
        }

        System.out.println("命令自动注册完成: " + successCount + " 成功, " + failCount + " 失败");
        System.out.println("共注册 " + commands.size() + " 个命令名称");

        // 设置特殊命令的依赖
        setupSpecialCommandDependencies();
    }

    /**
     * 注册单个命令类 - 完整的依赖注入支持
     */
    private boolean registerCommandClass(Class<? extends Command> commandClass) {
        try {
            CliCommand annotation = commandClass.getAnnotation(CliCommand.class);
            if (annotation == null) return false;

            // 创建命令实例（支持完整的依赖注入）
            Command command = createCommandWithDependencies(commandClass);
            if (command == null) {
                return false;
            }

            // 注册所有命令名称和别名
            String[] commandNames = annotation.value();
            for (String name : commandNames) {
                commands.put(name.toLowerCase(), command);
                System.out.println("注册命令: " + name + " -> " + commandClass.getSimpleName());
            }

            return true;

        } catch (Exception e) {
            System.err.println("注册命令类异常: " + commandClass.getName() + " - " + e.getMessage());
            return false;
        }
    }

    /**
     * 创建命令实例，支持完整的依赖注入
     */
    private Command createCommandWithDependencies(Class<? extends Command> commandClass) throws Exception {
        // 获取所有公共构造方法
        Constructor<?>[] constructors = commandClass.getConstructors();

        // 按参数数量排序，优先尝试参数少的构造方法
        Arrays.sort(constructors, (c1, c2) ->
                Integer.compare(c1.getParameterCount(), c2.getParameterCount()));

        for (Constructor<?> constructor : constructors) {
            Class<?>[] paramTypes = constructor.getParameterTypes();
            Object[] params = new Object[paramTypes.length];

            // 尝试为构造方法参数提供依赖
            boolean allParamsResolved = true;
            for (int i = 0; i < paramTypes.length; i++) {
                Object dependency = resolveDependency(paramTypes[i]);
                if (dependency != null) {
                    params[i] = dependency;
                } else {
                    allParamsResolved = false;
                    break;
                }
            }

            if (allParamsResolved) {
                try {
                    Command command = (Command) constructor.newInstance(params);
                    // 对于特定命令进行额外的依赖设置
                    setupAdditionalDependencies(command);
                    return command;
                } catch (Exception e) {
                    // 继续尝试下一个构造方法
                    continue;
                }
            }
        }

        // 如果没有找到合适的构造方法，记录详细错误
        System.err.println("无法为 " + commandClass.getSimpleName() + " 找到合适的构造方法");
        System.err.println("可用依赖: NoteController, TagController, NoteService, TagService, CommandRegistry");
        return null;
    }

    /**
     * 解析依赖类型到具体实例
     */
    private Object resolveDependency(Class<?> paramType) {
        if (paramType == NoteController.class) {
            return noteController;
        } else if (paramType == TagController.class) {
            return tagController;
        } else if (paramType == NoteService.class) {
            return noteService;
        } else if (paramType == TagService.class) {
            return tagService;
        } else if (paramType == CommandRegistry.class) {
            return this;
        } else if (paramType == Runnable.class) {
            // 为ExitCommand提供默认Runnable
            return (Runnable) () -> {};
        }
        return null;
    }

    /**
     * 设置额外的命令依赖（对于需要setter方法的命令）
     */
    private void setupAdditionalDependencies(Command command) {
        // 设置StatisticsCommand的命令注册器
        if (command instanceof StatisticsCommand) {
            ((StatisticsCommand) command).setCommandRegistry(this);
        }
    }

    /**
     * 设置特殊命令的依赖关系
     */
    private void setupSpecialCommandDependencies() {
        // 这个方法是空的，因为现在所有依赖都在创建时注入
        // 保留这个方法是为了将来的扩展
    }

    /**
     * 重新加载所有命令 - 支持热重载功能
     */
    public void reloadCommands() {
        System.out.println("开始重新加载命令系统...");

        // 保存特殊命令的状态
        Map<String, Object> savedStates = saveCommandStates();

        // 清空当前命令映射
        int oldCount = commands.size();
        commands.clear();

        // 重新扫描和注册命令
        autoRegisterCommands();

        // 恢复特殊命令的状态
        restoreCommandStates(savedStates);

        System.out.println("命令重新加载完成");
        System.out.println("重载前命令数: " + oldCount);
        System.out.println("重载后命令数: " + commands.size());
    }

    /**
     * 保存特殊命令的状态
     */
    private Map<String, Object> saveCommandStates() {
        Map<String, Object> states = new HashMap<>();

        // 保存ExitCommand的状态
        ExitCommand exitCommand = (ExitCommand) commands.get("exit");
        if (exitCommand != null) {
            states.put("exitAction", exitCommand.getExitAction());
        }

        // 保存HistoryCommand的状态
        HistoryCommand historyCommand = (HistoryCommand) commands.get("history");
        if (historyCommand != null) {
            states.put("commandHistory", historyCommand.getCommandHistory());
        }

        return states;
    }

    /**
     * 恢复特殊命令的状态
     */
    private void restoreCommandStates(Map<String, Object> savedStates) {
        // 恢复ExitCommand的状态
        ExitCommand exitCommand = (ExitCommand) commands.get("exit");
        if (exitCommand != null && savedStates.containsKey("exitAction")) {
            exitCommand.setExitAction((Runnable) savedStates.get("exitAction"));
        }

        // 恢复HistoryCommand的状态
        HistoryCommand historyCommand = (HistoryCommand) commands.get("history");
        if (historyCommand != null && savedStates.containsKey("commandHistory")) {
            historyCommand.setCommandHistory((com.dsq.app.cli.command.util.CommandHistory) savedStates.get("commandHistory"));
        }

        // 重新设置所有特殊命令的依赖
        setupSpecialCommandDependencies();
    }

    /**
     * 手动注册命令（保持向后兼容）
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

    /**
     * 获取NoteController（用于测试和特殊场景）
     */
    public NoteController getNoteController() {
        return noteController;
    }

    /**
     * 获取TagController（用于测试和特殊场景）
     */
    public TagController getTagController() {
        return tagController;
    }
}