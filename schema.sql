-- PKM 系统数据库脚本 (MySQL)
-- 包含用户、笔记、分类和标签的表结构

-- 1. 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户唯一标识',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '加密后的密码',
    email VARCHAR(100) COMMENT '电子邮箱',
    role VARCHAR(20) DEFAULT 'ROLE_USER' COMMENT '用户角色'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- 2. 分类表
CREATE TABLE IF NOT EXISTS categories (
    id VARCHAR(50) PRIMARY KEY COMMENT '分类唯一标识',
    name VARCHAR(100) NOT NULL COMMENT '分类名称',
    description TEXT COMMENT '分类描述',
    user_id BIGINT NOT NULL COMMENT '所属用户ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_name_user (name, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='笔记分类表';

-- 3. 笔记表
CREATE TABLE IF NOT EXISTS notes (
    id VARCHAR(50) PRIMARY KEY COMMENT '笔记唯一标识',
    title VARCHAR(200) NOT NULL COMMENT '笔记标题',
    content LONGTEXT COMMENT '笔记内容',
    category_id VARCHAR(50) COMMENT '所属分类ID',
    user_id BIGINT NOT NULL COMMENT '所属用户ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='笔记主表';

-- 4. 笔记标签关联表 (ElementCollection 在 JPA 中的表现)
CREATE TABLE IF NOT EXISTS note_tags (
    note_id VARCHAR(50) NOT NULL COMMENT '笔记ID',
    tag VARCHAR(100) NOT NULL COMMENT '标签名称',
    PRIMARY KEY (note_id, tag),
    FOREIGN KEY (note_id) REFERENCES notes(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='笔记标签关联表';

-- 5. 独立标签记录表 (可选，用于标签管理功能)
CREATE TABLE IF NOT EXISTS tags (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '标签唯一标识',
    name VARCHAR(100) NOT NULL COMMENT '标签名称',
    user_id BIGINT NOT NULL COMMENT '所属用户ID',
    UNIQUE KEY uk_name_user (name, user_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户标签记录表';
