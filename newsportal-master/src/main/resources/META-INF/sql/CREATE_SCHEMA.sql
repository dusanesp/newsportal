-- MySQL Schema for newsportal application
-- Generated from JPA entities

-- Drop existing tables to ensure fresh schema
DROP TABLE IF EXISTS article_tag;
DROP TABLE IF EXISTS user_role;
DROP TABLE IF EXISTS comment;
DROP TABLE IF EXISTS article;
DROP TABLE IF EXISTS tag;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS role;
DROP TABLE IF EXISTS hibernate_sequence;

-- Create sequence table for auto-increment IDs
CREATE TABLE IF NOT EXISTS hibernate_sequence (
    next_val BIGINT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Initialize sequence (start at 100 to avoid conflicts with seed data)
INSERT INTO hibernate_sequence VALUES (100);

-- User Roles table
CREATE TABLE IF NOT EXISTS role (
    id BIGINT NOT NULL PRIMARY KEY,
    version INT NOT NULL DEFAULT 0,
    authority VARCHAR(20) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Users table
CREATE TABLE IF NOT EXISTS user (
    id BIGINT NOT NULL PRIMARY KEY,
    version INT NOT NULL DEFAULT 0,
    login VARCHAR(30) NOT NULL UNIQUE,
    password VARCHAR(60) NOT NULL,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL UNIQUE,
    registered TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    locked BOOLEAN NOT NULL DEFAULT FALSE,
    enabled BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Categories table
CREATE TABLE IF NOT EXISTS category (
    id BIGINT NOT NULL PRIMARY KEY,
    version INT NOT NULL DEFAULT 0,
    name VARCHAR(30) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Articles table
CREATE TABLE IF NOT EXISTS article (
    id BIGINT NOT NULL PRIMARY KEY,
    version INT NOT NULL DEFAULT 0,
    title VARCHAR(100) NOT NULL,
    preview TEXT NOT NULL,
    content TEXT NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified TIMESTAMP NULL,
    view_count INT NOT NULL DEFAULT 0,
    user_id BIGINT NOT NULL,
    category_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (category_id) REFERENCES category(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Comments table
CREATE TABLE IF NOT EXISTS comment (
    id BIGINT NOT NULL PRIMARY KEY,
    version INT NOT NULL DEFAULT 0,
    content VARCHAR(500),
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT,
    article_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (article_id) REFERENCES article(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tags table
CREATE TABLE IF NOT EXISTS tag (
    id BIGINT NOT NULL PRIMARY KEY,
    version INT NOT NULL DEFAULT 0,
    name VARCHAR(20) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Many-to-many: User <-> UserRole
CREATE TABLE IF NOT EXISTS user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (role_id) REFERENCES role(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Many-to-many: Article <-> Tag
CREATE TABLE IF NOT EXISTS article_tag (
    article_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY (article_id, tag_id),
    FOREIGN KEY (article_id) REFERENCES article(id),
    FOREIGN KEY (tag_id) REFERENCES tag(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Indexes for better performance
CREATE INDEX idx_article_created ON article(created DESC);
CREATE INDEX idx_article_user ON article(user_id);
CREATE INDEX idx_article_category ON article(category_id);
CREATE INDEX idx_comment_article ON comment(article_id);
CREATE INDEX idx_comment_user ON comment(user_id);
CREATE INDEX idx_comment_created ON comment(created DESC);
