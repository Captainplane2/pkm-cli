-- PKM 系统初始测试数据
-- 预设测试用户及其相关的笔记、分类和标签
-- 注意：密码使用 BCrypt 加密，明文为 '741700dy' (与 DataInitializer 中的模拟逻辑匹配)
-- 账号：971203251，密码：741700dy

-- 1. 插入测试用户
-- $2a$10$vO8G0yvD0xWJ3H2X4v0Z7eX2J1Q7O8X5Z4z5w5w5w5w5w5w5w5w5w 是明文 '741700dy' 的一个 BCrypt 示例
INSERT INTO users (id, username, password, email, role) 
VALUES (1, '971203251', '$2a$10$C82oR.H0R1YF.vWf.qWwueY0N7E3V4rXWz9w5z5z5z5z5z5z5z5z5', 'test@example.com', 'ROLE_USER');

-- 2. 插入测试分类
INSERT INTO categories (id, name, description, user_id) 
VALUES ('cat_1', '工作笔记', '关于职业生涯和日常任务的笔记', 1);
INSERT INTO categories (id, name, description, user_id) 
VALUES ('cat_2', '生活随感', '记录生活的点滴和感悟', 1);
INSERT INTO categories (id, name, description, user_id) 
VALUES ('cat_3', '技术方案', '软件架构和技术难题的解决方案', 1);

-- 3. 插入测试笔记
INSERT INTO notes (id, title, content, category_id, user_id) 
VALUES ('note_1', '2026年工作计划', '1. 深入学习 Spring Boot 3
2. 完善 PKM 系统功能
3. 提高前端 UI 美观度', 'cat_1', 1);

INSERT INTO notes (id, title, content, category_id, user_id) 
VALUES ('note_2', '关于多用户系统的思考', '多用户系统最核心的是数据隔离。在 JPA 中通过 userId 字段配合 ThreadLocal 可以在 Service 层优雅实现隔离。', 'cat_3', 1);

INSERT INTO notes (id, title, content, category_id, user_id) 
VALUES ('note_3', '今日感悟', 'Trae 是一个非常智能的 IDE，能极大地提高开发效率。', 'cat_2', 1);

-- 4. 插入笔记标签
INSERT INTO note_tags (note_id, tag) VALUES ('note_1', '计划');
INSERT INTO note_tags (note_id, tag) VALUES ('note_1', '工作');
INSERT INTO note_tags (note_id, tag) VALUES ('note_2', '架构');
INSERT INTO note_tags (note_id, tag) VALUES ('note_2', 'Java');
INSERT INTO note_tags (note_id, tag) VALUES ('note_3', '心情');
INSERT INTO note_tags (note_id, tag) VALUES ('note_3', 'AI');

-- 5. 插入标签记录
INSERT INTO tags (name, user_id) VALUES ('计划', 1);
INSERT INTO tags (name, user_id) VALUES ('工作', 1);
INSERT INTO tags (name, user_id) VALUES ('架构', 1);
INSERT INTO tags (name, user_id) VALUES ('Java', 1);
INSERT INTO tags (name, user_id) VALUES ('心情', 1);
INSERT INTO tags (name, user_id) VALUES ('AI', 1);
