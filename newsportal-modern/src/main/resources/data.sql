-- Roles
INSERT IGNORE INTO role (id, authority) VALUES (1, 'ROLE_USER');
INSERT IGNORE INTO role (id, authority) VALUES (2, 'ROLE_ADMIN');
INSERT IGNORE INTO role (id, authority) VALUES (3, 'ROLE_AUTHOR');

-- Users (password is 'password' for all)
-- BCrypt encoded 'password': $2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG
INSERT IGNORE INTO user (id, login, password, name, email, enabled, locked, registered) VALUES 
(1, 'admin', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Admin User', 'admin@example.com', 1, 0, NOW()),
(2, 'author', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'John Author', 'author@example.com', 1, 0, NOW()),
(3, 'user', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Regular User', 'user@example.com', 1, 0, NOW());

-- User Roles
INSERT IGNORE INTO user_role (user_id, role_id) VALUES (1, 1);
INSERT IGNORE INTO user_role (user_id, role_id) VALUES (1, 2);
INSERT IGNORE INTO user_role (user_id, role_id) VALUES (1, 3);
INSERT IGNORE INTO user_role (user_id, role_id) VALUES (2, 1);
INSERT IGNORE INTO user_role (user_id, role_id) VALUES (2, 3);
INSERT IGNORE INTO user_role (user_id, role_id) VALUES (3, 1);

-- Categories
INSERT IGNORE INTO category (id, name) VALUES (1, 'Technology');
INSERT IGNORE INTO category (id, name) VALUES (2, 'Sports');
INSERT IGNORE INTO category (id, name) VALUES (3, 'Politics');
INSERT IGNORE INTO category (id, name) VALUES (4, 'Business');

-- Tags
INSERT IGNORE INTO tag (id, name) VALUES (1, 'Java');
INSERT IGNORE INTO tag (id, name) VALUES (2, 'Spring');
INSERT IGNORE INTO tag (id, name) VALUES (3, 'Docker');
INSERT IGNORE INTO tag (id, name) VALUES (4, 'Football');
INSERT IGNORE INTO tag (id, name) VALUES (5, 'Economy');

-- Articles
INSERT IGNORE INTO article (id, title, preview, content, user_id, category_id, created, view_count, image_url) VALUES 
(1, 'Getting Started with Spring Boot', 'Spring Boot makes it easy to create stand-alone, production-grade Spring based Applications.', '<p>Spring Boot makes it easy to create stand-alone, production-grade Spring based Applications that you can "just run".</p><p>We take an opinionated view of the Spring platform and third-party libraries so you can get started with minimum fuss. Most Spring Boot applications need very little Spring configuration.</p>', 2, 1, NOW(), 100, '/uploads/images/placeholders/tech_placeholder.png'),
(2, 'Docker for Beginners', 'Docker is an open platform for developing, shipping, and running applications.', '<p>Docker is an open platform for developing, shipping, and running applications. Docker enables you to separate your applications from your infrastructure so you can deliver software quickly.</p><p>With Docker, you can manage your infrastructure in the same ways you manage your applications.</p>', 2, 1, NOW(), 50, '/uploads/images/placeholders/default_placeholder.png'),
(3, 'Global Economy Outlook', 'The global economy is facing significant challenges in the coming year.', '<p>The global economy is facing significant challenges in the coming year. Inflation, supply chain disruptions, and geopolitical tensions are all contributing factors.</p>', 2, 4, NOW(), 75, '/uploads/images/placeholders/business_placeholder.png');

-- Article Tags
INSERT IGNORE INTO article_tag (article_id, tag_id) VALUES (1, 1);
INSERT IGNORE INTO article_tag (article_id, tag_id) VALUES (1, 2);
INSERT IGNORE INTO article_tag (article_id, tag_id) VALUES (2, 3);
INSERT IGNORE INTO article_tag (article_id, tag_id) VALUES (3, 5);

-- Comments
INSERT IGNORE INTO comment (id, content, created, article_id, user_id) VALUES 
(1, 'Great article! Very helpful.', NOW(), 1, 3),
(2, 'I love Spring Boot.', NOW(), 1, 1),
(3, 'Docker is amazing.', NOW(), 2, 3);
