# 系统还需完善

1、系统展示页面的样式优化
2、Docker容器化一键部署（RabbitMQ、Redis、Nginx）
3、评论功能还需完善（树形展示、评论权限）



# 个人博客系统

> 基于 Spring Boot 3 + Vue3 的前后端分离个人博客系统，支持多用户发文、分类标签、评论、点赞收藏、邮件通知、后台管理等功能。

## 项目简介

项目为个人学习开发的全栈博客系统，后端基于 Spring Boot 3 + Spring Security + MyBatis-Plus + Redis + RabbitMQ 实现，前端基于 Vue3 + Element Plus + Pinia 实现。涵盖用户认证鉴权、文章管理、分类标签、评论互动、邮件异步通知、管理员后台等完整业务模块。

## 技术栈

### 后端
- Java 17 / Spring Boot 3.5.6
- Spring Security（JWT 鉴权）
- MyBatis-Plus 3.5.5 + PageHelper（分页）
- Redis（阅读去重 / Token 缓存 / 验证码）
- RabbitMQ（邮件异步通知 + 死信队列）
- MySQL 8.0 + Druid 连接池
- Knife4j + SpringDoc OpenAPI（接口文档）
- Lombok / AOP（登录校验切面）

### 前端
- Vue 3 + Vite
- Element Plus
- Pinia（状态管理）
- Vue Router
- Axios

### 基础设施
- Docker + Docker Compose（一键编排 MySQL/Redis/RabbitMQ/前后端）

## 核心功能

| 模块 | 功能 |
|------|------|
| 用户 | 注册登录、JWT 鉴权、邮箱验证码注册 |
| 文章 | 发布/编辑/删除、分页查询、Markdown 渲染、阅读去重统计 |
| 分类 | 文章归类、管理员全局分类管理 |
| 标签 | 多标签关联 |
| 评论 | 二级评论回复、文章评论数统计 |
| 互动 | 文章点赞、收藏 |
| 笔记 | 用户私人笔记 |
| 后台 | 管理员用户/文章/评论管理 |
| 通知 | 邮件异步通知（RabbitMQ + 死信队列） |

## 目录结构

```
Personal_Blog/
├── back-end/                # Spring Boot 后端
│   └── src/main/java/com/wjy/personal_blog/
│       ├── controllers/    # 接口层（publics 公开 / user 登录 / admin 后台）
│       ├── service/        # 业务层（impl 实现 / auth 权限 / email 邮件）
│       ├── mapper/         # MyBatis Mapper
│       ├── pojo/           # entity 实体 / dto 入参 / vo 出参
│       ├── configs/        # Security / Redis / RabbitMQ / Web 配置
│       ├── filter/         # JWT 认证过滤器
│       ├── handler/        # 登录成功/失败/登出/元数据填充处理器
│       ├── interceptor/    # 登录/管理员拦截器
│       ├── aspect/         # AOP 登录校验切面
│       ├── rabbitmq/       # 邮件异步生产/消费/死信
│       ├── utils/          # JWT / Email / Markdown / 验证码
│       ├── exceptions/     # 统一异常处理
│       └── result/         # 统一返回结构
├── front-end/vue3-blog/    # Vue3 前端
├── database/database.sql   # 建表脚本
├── docker-compose.yml      # 一键编排
└── .env.example            # 环境变量示例
```

## 快速开始


### 方式：本地开发

#### 后端

1. 启动本地 MySQL 8.0 / Redis / RabbitMQ
2. 导入 `database/database.sql`
3. 配置环境变量（参考 `.env.example`），或直接修改 `application.yml` 中 `${ENV:默认值}` 的默认值
4. 运行：

   ```bash
   cd back-end
   ./mvnw spring-boot:run
   ```

#### 前端

```bash
cd front-end/vue3-blog
npm install
npm run dev
```

## 环境变量说明

参考 [.env.example](./.env.example)，关键变量：

| 变量 | 说明 | 示例 |
|------|------|------|
| MYSQL_ROOT_PASSWORD | MySQL root 密码 | - |
| MYSQL_PASSWORD | 后端连接 MySQL 的密码 | - |
| RABBITMQ_DEFAULT_PASS | RabbitMQ 默认用户密码 | - |
| MAIL_USERNAME | 发件邮箱 | your@qq.com |
| MAIL_PASSWORD | QQ 邮箱 SMTP 授权码（非登录密码） | - |
| JWT_SECRET | JWT 签名密钥（建议 64 字符） | `openssl rand -base64 48` |
| CORS_ALLOWED_ORIGINS | 允许的前端来源（逗号分隔） | http://localhost:5173 |

> 注意：QQ 邮箱 SMTP 授权码不是登录密码，需在 QQ 邮箱「设置 → 账户 → 开启 SMTP 服务」中生成。

## 接口文档

开发环境（`spring.profiles.active=dev`）下，访问 Knife4j 文档：
- http://localhost:8008/doc.html
- http://localhost:8008/swagger-ui.html

生产环境已默认关闭接口文档以避免暴露接口结构。

## License

[MIT License](./LICENSE)
