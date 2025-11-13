package com.dsq.app.cli.command.annotation;

import com.dsq.app.cli.command.core.Command;
import com.dsq.app.cli.command.core.CliCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.reflections.Reflections;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 注解扫描功能测试 - 精简版
 */
class AnnotationScanningTest {

    @Test
    @DisplayName("测试@CliCommand注解扫描完整性")
    void testCliCommandAnnotationScanning() {
        Reflections reflections = new Reflections("com.dsq.app.cli.command");
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(CliCommand.class);

        assertFalse(annotatedClasses.isEmpty(), "应该扫描到带有@CliCommand注解的类");

        // 验证扫描结果
        long commandCount = annotatedClasses.stream()
                .filter(clazz -> Command.class.isAssignableFrom(clazz))
                .count();

        assertTrue(commandCount > 0, "应该扫描到实现Command接口的类");

        System.out.println("扫描结果:");
        System.out.println("  总注解类数: " + annotatedClasses.size());
        System.out.println("  命令类数: " + commandCount);

        // 显示前5个命令类信息
        annotatedClasses.stream()
                .filter(clazz -> Command.class.isAssignableFrom(clazz))
                .limit(5)
                .forEach(clazz -> {
                    CliCommand annotation = clazz.getAnnotation(CliCommand.class);
                    System.out.println("  " + clazz.getSimpleName() + " -> " +
                            String.join(", ", annotation.value()));
                });
    }

    @Test
    @DisplayName("测试命令包结构扫描")
    void testCommandPackageStructureScanning() {
        String[] packagesToScan = {
                "com.dsq.app.cli.command.notes",
                "com.dsq.app.cli.command.tags",
                "com.dsq.app.cli.command.system",
                "com.dsq.app.cli.command.test"
        };

        for (String packageName : packagesToScan) {
            Reflections reflections = new Reflections(packageName);
            Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(CliCommand.class);

            System.out.println(packageName + ": " + annotatedClasses.size() + " 个命令");

            // 验证每个包都有命令类（test包可能为空）
            if (!"com.dsq.app.cli.command.test".equals(packageName)) {
                assertFalse(annotatedClasses.isEmpty(),
                        "包 " + packageName + " 应该包含命令类");
            }
        }
    }
}