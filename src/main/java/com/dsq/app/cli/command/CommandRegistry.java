package com.dsq.app.cli.command;

import org.reflections.Reflections;
import com.dsq.app.controller.NoteController;
import com.dsq.app.controller.TagController;
import com.dsq.app.service.NoteFileStorageService;
import com.dsq.app.service.NoteService;
import com.dsq.app.service.StorageService;
import com.dsq.app.service.TagService;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 基于注解的命令注册器 - 自动扫描和注册命令
 */
public class CommandRegistry {
    private final Map<String, Command> commands = new HashMap<>();
    private NoteController noteController;
    private TagController tagController;

    public CommandRegistry() {
        initializeDependencies();
        autoRegisterCommands();
    }

    /**
     * 初始化依赖对象
     */
    private void initializeDependencies() {
        StorageService storageService = new NoteFileStorageService();
        NoteService noteService = new NoteService(storageService);
        TagService tagService = new TagService(storageService);

        this.noteController = new NoteController(noteService);
        this.tagController = new TagController(tagService);
    }

    /**
     * 自动注册所有带有@CliCommand注解的命令类
     */
    private void autoRegisterCommands() {
        Reflections reflections = new Reflections("com.dsq.app.cli.command");
        Set<Class<?>> commandClasses = reflections.getTypesAnnotatedWith(CliCommand.class);

        System.out.println("发现 " + commandClasses.size() + " 个命令类");

        for (Class<?> clazz : commandClasses) {
            if (Command.class.isAssignableFrom(clazz)) {
                registerCommandClass((Class<? extends Command>) clazz);
            }
        }
    }

    /**
     * 注册单个命令类
     */
    private void registerCommandClass(Class<? extends Command> commandClass) {
        try {
            CliCommand annotation = commandClass.getAnnotation(CliCommand.class);
            if (annotation == null) return;

            // 根据构造方法参数类型注入依赖
            Command command = createCommandInstance(commandClass);

            String[] commandNames = annotation.value();
            for (String name : commandNames) {
                commands.put(name.toLowerCase(), command);
                System.out.println("注册命令: " + name + " -> " + commandClass.getSimpleName());
            }

        } catch (Exception e) {
            System.err.println("注册命令类失败: " + commandClass.getName() + " - " + e.getMessage());
        }
    }

    /**
     * 创建命令实例，自动注入依赖
     */
    private Command createCommandInstance(Class<? extends Command> commandClass) throws Exception {
        Constructor<?>[] constructors = commandClass.getConstructors();

        for (Constructor<?> constructor : constructors) {
            Class<?>[] paramTypes = constructor.getParameterTypes();
            Object[] params = new Object[paramTypes.length];

            boolean canCreate = true;
            for (int i = 0; i < paramTypes.length; i++) {
                if (paramTypes[i] == NoteController.class) {
                    params[i] = noteController;
                } else if (paramTypes[i] == TagController.class) {
                    params[i] = tagController;
                } else if (paramTypes[i] == CommandRegistry.class) {
                    params[i] = this;
                } else if (paramTypes[i] == Runnable.class && commandClass.getSimpleName().equals("ExitCommand")) {
                    // 特殊处理ExitCommand
                    params[i] = (Runnable) () -> { /* 在CommandParser中设置 */ };
                } else {
                    canCreate = false;
                    break;
                }
            }

            if (canCreate) {
                return (Command) constructor.newInstance(params);
            }
        }

        // 如果没有找到合适的构造方法，使用默认构造方法
        return commandClass.getDeclaredConstructor().newInstance();
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
     * 获取NoteController（用于特殊命令设置）
     */
    public NoteController getNoteController() {
        return noteController;
    }

    /**
     * 获取TagController（用于特殊命令设置）
     */
    public TagController getTagController() {
        return tagController;
    }
}