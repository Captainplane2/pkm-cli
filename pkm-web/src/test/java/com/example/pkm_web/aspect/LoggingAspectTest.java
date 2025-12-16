package com.example.pkm_web.aspect;

import com.example.pkm_web.service.NoteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=password",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.h2.console.enabled=true"
})
public class LoggingAspectTest {

    @Autowired
    private NoteService noteService;

    @Test
    public void testLoggingAspect() {
        // 创建笔记
        noteService.createNote("测试笔记", "这是一个测试笔记内容");
        
        // 获取所有笔记
        noteService.getAllNotes();
        
        // 测试日志切面是否正常记录
        // 实际测试中，我们会检查日志输出
        // 这里只是简单地调用方法，验证切面不会抛出异常
        System.out.println("LoggingAspectTest completed successfully");
    }
}
