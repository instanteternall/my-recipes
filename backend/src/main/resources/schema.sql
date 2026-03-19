CREATE DATABASE IF NOT EXISTS my_recipes DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE my_recipes;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `recipe_ingredient`;
DROP TABLE IF EXISTS `cooking_step`;
DROP TABLE IF EXISTS `recipe_tag_relation`;
DROP TABLE IF EXISTS `recipe_tag`;
DROP TABLE IF EXISTS `recipe_category`;
DROP TABLE IF EXISTS `ingredient_category`;
DROP TABLE IF EXISTS `ingredient`;
DROP TABLE IF EXISTS `recipe`;
DROP TABLE IF EXISTS `user`;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
  `password` VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
  `nickname` VARCHAR(50) NOT NULL COMMENT '昵称',
  `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=正常，1=已删除',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE `recipe_category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `description` TEXT DEFAULT NULL COMMENT '分类描述',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜谱分类表';

CREATE TABLE `recipe_tag` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `name` VARCHAR(50) NOT NULL COMMENT '标签名称',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜谱标签表';

CREATE TABLE `recipe` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '菜谱ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `name` VARCHAR(100) NOT NULL COMMENT '菜谱名称',
  `description` TEXT DEFAULT NULL COMMENT '菜谱描述',
  `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '封面图URL',
  `cooking_time` INT DEFAULT NULL COMMENT '烹饪时间（分钟）',
  `difficulty` VARCHAR(20) DEFAULT NULL COMMENT '难度',
  `category_id` BIGINT DEFAULT NULL COMMENT '分类ID',
  `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览量',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜谱表';

CREATE TABLE `recipe_tag_relation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `recipe_id` BIGINT NOT NULL COMMENT '菜谱ID',
  `tag_id` BIGINT NOT NULL COMMENT '标签ID',
  PRIMARY KEY (`id`),
  KEY `idx_recipe_id` (`recipe_id`),
  KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜谱标签关联表';

CREATE TABLE `cooking_step` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '步骤ID',
  `recipe_id` BIGINT NOT NULL COMMENT '菜谱ID',
  `step_order` INT NOT NULL COMMENT '步骤顺序',
  `content` TEXT NOT NULL COMMENT '步骤内容',
  `image` VARCHAR(500) DEFAULT NULL COMMENT '步骤图片URL',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_recipe_id` (`recipe_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='烹饪步骤表';

CREATE TABLE `recipe_ingredient` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `recipe_id` BIGINT NOT NULL COMMENT '菜谱ID',
  `ingredient_name` VARCHAR(100) NOT NULL COMMENT '食材名称',
  `quantity` DECIMAL(10,2) NOT NULL COMMENT '数量',
  `unit` VARCHAR(20) NOT NULL COMMENT '单位',
  PRIMARY KEY (`id`),
  KEY `idx_recipe_id` (`recipe_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜谱食材关联表';

CREATE TABLE `ingredient_category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `description` TEXT DEFAULT NULL COMMENT '分类描述',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='食材分类表';

CREATE TABLE `ingredient` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '食材ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `name` VARCHAR(100) NOT NULL COMMENT '食材名称',
  `quantity` DECIMAL(10,2) NOT NULL COMMENT '数量',
  `unit` VARCHAR(20) NOT NULL COMMENT '单位',
  `expiry_date` DATE DEFAULT NULL COMMENT '保质期',
  `category_id` BIGINT DEFAULT NULL COMMENT '分类ID',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='食材表';

-- 初始化分类数据
INSERT INTO `recipe_category` (`name`, `description`) VALUES
('家常菜', '简单易做的日常菜肴'),
('川菜', '四川特色菜肴'),
('粤菜', '广东特色菜肴'),
('湘菜', '湖南特色菜肴'),
('西餐', '西方风味菜肴'),
('烘焙', '面包、蛋糕等烘焙食品');

INSERT INTO `recipe_tag` (`name`) VALUES
('快手菜'),
('下饭菜'),
('早餐'),
('午餐'),
('晚餐'),
('素菜'),
('肉类'),
('海鲜'),
('甜品'),
('饮品');

INSERT INTO `ingredient_category` (`name`, `description`) VALUES
('蔬菜', '各类新鲜蔬菜'),
('肉类', '猪牛羊肉等'),
('海鲜', '鱼虾蟹等'),
('水果', '新鲜水果'),
('调味品', '酱油、醋、盐等'),
('主食', '米面等主食');
