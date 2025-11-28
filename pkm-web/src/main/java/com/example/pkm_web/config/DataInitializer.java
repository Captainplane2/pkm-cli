package com.example.pkm_web.config;

import com.example.pkm_web.service.NoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 数据初始化（开发期使用）
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final NoteService noteService;

    @Autowired
    public DataInitializer(NoteService noteService) {
        this.noteService = noteService;
    }

    @Override
    public void run(String... args) throws Exception {
        // 检查是否有数据，如果没有则初始化一些示例数据
        if (noteService.getAllNotes().isEmpty()) {
            logger.info("初始化示例数据...");

            noteService.createNote("欢迎使用PKM系统",
                    "这是一个基于Spring Boot的个人知识管理系统。\n支持笔记的创建、编辑、搜索和标签管理。");

            noteService.createNote("RESTful API 设计原则",
                    "RESTful API 应该遵循以下原则：\n" +
                            "1. 使用HTTP方法明确操作意图\n" +
                            "2. 使用合适的HTTP状态码\n" +
                            "3. 提供清晰的错误信息\n" +
                            "4. 版本化API");

            logger.info("示例数据初始化完成");
        }
    }
}