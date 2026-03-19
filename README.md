# 🍜 我的菜谱

> 记录每一道温暖的味道 —— 一个简洁温馨的个人菜谱管理应用

## ✨ 功能特性

- 📖 **菜谱管理** —— 创建、编辑、删除菜谱，支持封面图、步骤图上传
- 🥬 **食材管理** —— 记录食材库存，按分类管理，掌握冰箱动态
- 🔍 **智能搜索** —— 按名称、分类、难度综合筛选菜谱
- 🔥 **热门菜谱** —— 首页展示浏览量最高的菜谱
- 👤 **用户系统** —— 手机号注册登录，JWT 鉴权
- 🖼️ **图片上传** —— 支持拖拽上传菜谱封面及步骤配图
- 📱 **响应式设计** —— 适配桌面端与移动端

## 🛠️ 技术栈

### 前端
| 技术 | 版本 | 说明 |
|------|------|------|
| Vue 3 | 3.x | 组合式 API + `<script setup>` |
| TypeScript | 5.x | 类型安全 |
| Vite | 4.x | 构建工具 |
| Element Plus | 2.x | UI 组件库 |
| Pinia | 2.x | 状态管理 |
| Vue Router | 4.x | 路由管理 |
| Axios | 1.x | HTTP 客户端 |

### 后端
| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 2.7.18 | 主框架 |
| Java | 8+ | 编程语言 |
| MyBatis-Plus | 3.5.3 | ORM 框架 |
| MySQL | 8.0 | 数据库 |
| Spring Security | 5.x | 安全认证 |
| JWT | - | Token 鉴权 |
| Maven | 3.9 | 构建工具 |

## 🚀 快速启动

### 环境要求
- Java 8+
- Node.js 18+
- MySQL 8.0
- Maven 3.6+

### 1. 初始化数据库

```bash
# 登录 MySQL，创建数据库并执行建表脚本
mysql -u root -p
create database my_recipes charset utf8mb4;
use my_recipes;
source backend/src/main/resources/schema.sql;
```

### 2. 启动后端

```bash
cd backend
# 修改 src/main/resources/application.yml 中的数据库密码
mvn clean package -DskipTests
java -jar target/my-recipes-backend-1.0.0.jar
```

后端启动后访问：http://localhost:8080

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端启动后访问：http://localhost:3000

### 4. 登录体验

- 注册时验证码固定为 `123456`（开发环境 Mock 模式）
- 默认数据库配置：`root / 123456`，可在 `application.yml` 中修改

## 📁 项目结构

```
my-recipes/
├── backend/                    # Spring Boot 后端
│   └── src/main/java/com/recipes/
│       ├── controller/         # REST 控制器
│       ├── service/            # 业务逻辑层
│       ├── mapper/             # MyBatis-Plus Mapper
│       ├── entity/             # 数据库实体
│       ├── dto/                # 数据传输对象
│       ├── config/             # 配置类（Security、CORS 等）
│       └── utils/              # 工具类（JWT、短信等）
├── frontend/                   # Vue 3 前端
│   └── src/
│       ├── api/                # Axios 接口封装
│       ├── components/         # 可复用组件
│       ├── views/              # 页面组件
│       │   ├── auth/           # 登录、注册
│       │   ├── recipes/        # 菜谱相关页面
│       │   ├── ingredients/    # 食材相关页面
│       │   └── profile/        # 个人资料
│       ├── store/              # Pinia 状态管理
│       ├── router/             # 路由配置
│       └── types/              # TypeScript 类型定义
├── 需求分析和技术架构文档.md
├── 开发规范文档.md
└── 启动说明.md
```

## 🎨 界面预览

- **温馨暖色调**：以橙棕色为主色，米黄底色为背景，营造家庭厨房的温暖感
- **流畅动画**：页面切换渐变过渡，卡片 hover 上浮效果
- **衬线字体**：标题使用「ZCOOL 小薇」字体，兼具美观与可读性

## 📄 接口说明

| 模块 | 路径前缀 | 说明 |
|------|---------|------|
| 认证 | `/api/auth` | 注册、登录、获取用户信息 |
| 菜谱 | `/api/recipes` | 菜谱 CRUD、分类、标签 |
| 食材 | `/api/ingredients` | 食材 CRUD、分类 |
| 搜索 | `/api/search` | 全局搜索菜谱/食材 |
| 上传 | `/api/upload` | 图片上传 |

## 📝 License

MIT License
