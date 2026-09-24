CREATE DATABASE IF NOT EXISTS personal_blog;
USE personal_blog;

-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: personal_blog
-- ------------------------------------------------------
-- Server version	8.0.36

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `article`
--

DROP TABLE IF EXISTS `article`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article` (
  `article_id` int NOT NULL AUTO_INCREMENT COMMENT '文章ID',
  `article_user_id` int DEFAULT NULL COMMENT '用户id',
  `article_title` varchar(255) DEFAULT NULL COMMENT '标题',
  `article_content` mediumtext COMMENT '内容',
  `article_read_count` int DEFAULT '0' COMMENT '阅读量',
  `article_comment_count` int DEFAULT '0' COMMENT '评论数',
  `article_like_count` int DEFAULT '0' COMMENT '点赞数',
  `article_is_comment` int unsigned DEFAULT NULL COMMENT '是否允许评论',
  `article_status` int unsigned DEFAULT '1' COMMENT '状态',
  `article_update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `article_create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `article_summary` text COMMENT '摘要',
  `article_thumbnail` varchar(255) DEFAULT NULL COMMENT '缩略图',
  PRIMARY KEY (`article_id`),
  KEY `article_user_user_id_fk` (`article_user_id`),
  FULLTEXT KEY `idx_article_fulltext` (`article_title`,`article_content`),
  CONSTRAINT `article_user_user_id_fk` FOREIGN KEY (`article_user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `article`
--

LOCK TABLES `article` WRITE;
/*!40000 ALTER TABLE `article` DISABLE KEYS */;
INSERT INTO `article` VALUES (2,1,'第一篇博客','<p>这是我的第一篇博客,这次学习了软件工程中的总体设计，其中包括接口设计、逻辑设计等。</p>',10,5,20,1,1,'2018-11-25 20:51:05','2017-10-07 15:37:20',NULL,NULL),(3,1,'第二篇博客','<p>这是我的第二篇博客,这次学习了软件工程中的总体设计，其中包括接口设计、逻辑设计等。</p>',17,5,20,1,1,'2024-11-25 20:51:05','2024-11-28 15:37:20',NULL,NULL),(4,2,'第三篇','芜湖起飞，来吧走去',20,10,5,1,1,'2025-11-05 21:17:20','2025-11-05 21:17:20','瓦',NULL),(5,2,'11月12号的测试修改文章demo','为何这么快看清楚，留的这结果\n知我是个无法讨好的人，相恋一刻只是我的侥幸。',21,10,5,1,1,'2025-11-05 21:17:31','2025-11-05 21:17:31','瓦',NULL),(6,2,'11月6号的demo博客','刷了软考上午题，进行了想对应的复习。\n中午打算继续博客项目的练习。',20,10,5,1,1,'2025-11-06 13:09:27','2025-11-06 13:09:27','软考、blog',NULL),(10,2,'11月13号的测试博文','今日的行程安排：完善个人博客项目（添加标签分类、增加随笔功能、完善页面）。\n观看Redis视频，试着去投简历。',0,1,0,1,1,'2025-11-13 10:35:31','2025-11-13 10:35:31','个人博客完善、resume',NULL),(13,NULL,'11月25号的博文','',10,0,0,0,0,'2025-11-26 17:21:42','2025-11-26 17:21:42','图片存储',NULL),(14,1,'11月27号的测试','',5,0,0,0,0,'2025-11-27 10:37:17','2025-11-27 10:37:17','自动获取用户id、评论板块',''),(15,1,'testing','',3,0,0,0,0,'2025-11-27 11:00:07','2025-11-27 11:00:07','test text',''),(16,1,'测试wdasdas','撒法发哦酸钠吗佛牌纳米',2,0,0,0,0,'2025-11-27 11:23:53','2025-11-27 11:23:53','随便打的文字',''),(17,1,'25年12月8号的测试','今天新添加了图片上传功能，测试可不可行。',3,0,0,0,0,'2025-12-08 20:09:36','2025-12-08 20:09:36','图片上传',NULL),(18,1,'12月9号的测试','8号测试失败，图片的上传路径还是假文件路径。今日要解决图片上传路径问题。',2,0,0,0,0,'2025-12-09 17:15:42','2025-12-09 17:15:42','pictures_upload','cfd876d9-3fa3-4178-a7fe-d028bd01f562.jpg'),(19,2,'12月29','这段文本只是为了测试提交图片功能是否运行正常。',1,0,0,0,0,'2025-12-29 10:17:06','2025-12-29 10:17:06','测试图片提交','b1869a93-e664-46da-b0cd-8e5e78c14675.png'),(20,12,'Giaean的第一篇文章','这是用户Giaean的第一篇文章，昵称：”彦祖“。后面的文本内容单纯凑字数了',0,0,0,1,1,'2025-12-29 10:37:07','2025-12-29 10:37:07','First article','f078e18d-67bb-4a5e-913b-cd2ff1870cc1.jpg'),(21,2,'dasd','随便测试的文字',0,0,0,0,0,'2025-12-30 11:11:23','2025-12-30 11:11:23','testing','103dc8d0-b6c8-4532-9132-c94238043c4d.jpg'),(22,12,'Giaean的第二篇测试笔记','单纯的测试文本内容',0,0,0,1,1,'2025-12-30 11:17:29','2025-12-30 11:17:29','testing text','109049f8-a2c7-4a7e-94a1-c0583543a417.jpg'),(28,2,'03/23/2026，新增了markdown富文本编辑器到新增文章的弹窗。','# 前端页面完善-新增功能\n\n#### 3月23\n\n今日为我的前端项目中新增了一个功能——》markdown富文本编辑器。在我的博文列表展示页的新增博文的弹窗中添加了markdown编辑区域，使其更加完善，向着更加符合标准的方向。\n\n',0,0,0,0,0,'2026-03-23 21:30:30','2026-03-23 21:30:30','新增富文本编辑器','599fdefe-f01e-43b3-a9fd-d371cbb94221.png'),(29,10,'Yoru的第一篇测试博文','***这是登录Yoru账号后进行测试的第一篇博文内容，主要测试新增博文的接口和功能实现是否无误。***😄 \n\n',0,1,0,1,1,'2026-03-28 10:26:04','2026-03-28 10:26:04','Yoru的测试','5d3335fa-d22f-481e-98da-806119cbab88.png');
/*!40000 ALTER TABLE `article` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `article_category`
--

DROP TABLE IF EXISTS `article_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article_category` (
  `article_id` int NOT NULL COMMENT '文章id',
  `category_id` int NOT NULL COMMENT '标签id',
  PRIMARY KEY (`article_id`,`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文章-标签联系表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `article_category`
--

LOCK TABLES `article_category` WRITE;
/*!40000 ALTER TABLE `article_category` DISABLE KEYS */;
INSERT INTO `article_category` VALUES (2,2),(2,3),(3,1),(3,2),(3,3),(4,1),(4,3),(4,4),(5,1),(5,5),(6,2),(6,6),(6,7),(10,1),(10,3),(10,4),(10,5),(22,1),(22,2),(22,3),(28,2),(28,6),(28,7),(29,1),(29,3),(29,4),(29,5);
/*!40000 ALTER TABLE `article_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `article_tag`
--

DROP TABLE IF EXISTS `article_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article_tag` (
  `article_id` int NOT NULL COMMENT '文章id',
  `tag_id` int NOT NULL COMMENT '标签id',
  PRIMARY KEY (`article_id`,`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文章-标签联系表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `article_tag`
--

LOCK TABLES `article_tag` WRITE;
/*!40000 ALTER TABLE `article_tag` DISABLE KEYS */;
INSERT INTO `article_tag` VALUES (2,1),(2,3),(3,2),(3,4),(4,5),(4,9),(5,6),(5,8),(6,7),(10,1),(10,3),(10,5),(10,11),(29,16),(29,17),(29,18);
/*!40000 ALTER TABLE `article_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `category_id` int NOT NULL AUTO_INCREMENT COMMENT '分类id\r\n',
  `category_name` varchar(30) DEFAULT NULL COMMENT '分类名称',
  `category_order` int unsigned DEFAULT '1' COMMENT '排序\r\n',
  `category_description` varchar(100) DEFAULT NULL COMMENT '分类描述\r\n',
  `category_icon` varchar(20) DEFAULT NULL COMMENT '分类图标',
  `category_parent_id` int DEFAULT NULL COMMENT '父级分类id',
  PRIMARY KEY (`category_id`),
  UNIQUE KEY `category_name` (`category_name`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (1,'后端',1,'后端技术栈',NULL,0),(2,'前端',1,'前端技术栈',NULL,0),(3,'数据库',1,'数据库技术栈',NULL,0),(4,'Spring',2,'',NULL,1),(5,'Java',2,'',NULL,1),(6,'HTML+CSS',2,'',NULL,2),(7,'Vue',2,'',NULL,2);
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment` (
  `comment_id` int NOT NULL AUTO_INCREMENT,
  `article_id` int NOT NULL,
  `user_id` int DEFAULT NULL,
  `parent_id` int DEFAULT NULL,
  `content` text NOT NULL,
  `is_anonymous` tinyint(1) DEFAULT '0',
  `anonymous_name` varchar(30) DEFAULT NULL,
  `comment_status` tinyint DEFAULT '1',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`comment_id`),
  KEY `comment_article_fk` (`article_id`),
  KEY `comment_parent_fk` (`parent_id`),
  KEY `comment_user_fk` (`user_id`),
  CONSTRAINT `comment_article_fk` FOREIGN KEY (`article_id`) REFERENCES `article` (`article_id`),
  CONSTRAINT `comment_parent_fk` FOREIGN KEY (`parent_id`) REFERENCES `comment` (`comment_id`),
  CONSTRAINT `comment_user_fk` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='评论表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
INSERT INTO `comment` VALUES (2,21,2,NULL,'测试评论',0,NULL,1,'2026-03-25 23:29:28','2026-03-25 23:29:28'),(3,21,2,NULL,'第二条测试评论',0,NULL,1,'2026-03-26 15:52:24','2026-03-26 15:52:24'),(9,20,NULL,NULL,'这是在Apifox进行添加评论接口测试的第5条测试评，对应的文章id是20、用户id是12、用户名userName是Giaean。',1,'匿名用户8822',1,NULL,'2026-03-26 22:29:08'),(12,22,NULL,NULL,'这是在Apifox进行添加评论接口测试的第6条测试评，对应的文章id是22、用户id是12、用户名userName是Giaean。',1,'匿名用户9966',1,NULL,'2026-03-26 22:30:39'),(13,5,NULL,NULL,'这是在Apifox进行添加评论接口测试的第7条测试评，对应的文章id是5、用户id是2、用户名userName是zhangsan。',1,'匿名用户5479',1,'2026-03-26 23:27:06','2026-03-26 23:27:06'),(17,6,NULL,NULL,'Yoru的第二篇测试文本',1,'匿名用户1943',1,'2026-03-30 17:24:12','2026-03-30 17:24:12'),(18,6,NULL,NULL,'Yoru的第三篇测试文本',1,'匿名用户5296',1,'2026-03-31 10:24:15','2026-03-31 10:24:15'),(19,6,NULL,NULL,'Yoru的forth篇测试文本',1,'匿名用户9518',1,'2026-03-31 10:45:00','2026-03-31 10:45:00'),(21,29,12,NULL,'Giaean的第一篇测试文本，评论的是Yoru的博文',0,NULL,1,'2026-03-31 11:36:04','2026-03-31 11:36:04'),(22,2,12,NULL,'This is Giaean,and I\'m takling about JiaYn\'s article.',0,NULL,1,'2026-03-31 11:38:15','2026-03-31 11:38:15'),(23,4,12,NULL,'This is Giaean speaking,我正评论张三的博文id为4的博文内容。',NULL,NULL,1,'2026-03-31 11:38:55','2026-03-31 11:38:55'),(24,10,12,NULL,'This is Giaean speaking,我正评论张三的11月13号的测试博文。',0,NULL,1,'2026-03-31 11:40:29','2026-03-31 11:40:29'),(25,29,10,NULL,'回调夜露',0,'',0,'2026-04-07 16:13:48','2026-04-07 16:13:48'),(26,29,10,NULL,'牛逼',0,'',0,'2026-04-13 13:58:45','2026-04-13 13:58:45'),(27,10,2,NULL,'what the fuck',0,'',0,'2026-04-13 14:00:55','2026-04-13 14:00:55');
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notes`
--

DROP TABLE IF EXISTS `notes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notes` (
  `note_id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `note_topic` varchar(50) NOT NULL COMMENT '标题',
  `note_content` text NOT NULL COMMENT '随笔内容',
  `note_create_time` datetime DEFAULT NULL,
  `note_update_time` datetime DEFAULT NULL,
  `note_user_id` int DEFAULT NULL COMMENT '笔记作者id',
  PRIMARY KEY (`note_id`),
  UNIQUE KEY `note_id` (`note_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='随笔';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notes`
--

LOCK TABLES `notes` WRITE;
/*!40000 ALTER TABLE `notes` DISABLE KEYS */;
INSERT INTO `notes` VALUES (1,'第一篇随笔','今日11/14，这个随笔的内容文本单纯为了填充数据，为了前端页面展示','2025-11-03 00:00:00','2025-11-13 00:00:00',1),(2,'第二篇随笔','今日11/14，这个随笔的内容文本单纯为了填充数据，为了前端页面展示。Guiding through','2025-11-04 00:00:00','2025-11-14 00:00:00',1),(3,'第三篇随笔','今日11/14，这个随笔的内容文本单纯为了填充数据，为了前端页面展示。They are so dead','2025-11-05 00:00:00','2025-11-15 00:00:00',1),(4,'11月18号的笔记','这篇笔记只是单纯填充数据。\r\n也许今夜我不会让自己在思念里沉沦。',NULL,NULL,1),(8,'12月8号的笔记','今天完善了多个用户登录时，确定当前用户操作的唯一性。（查看自己的文章）','2025-12-08 00:00:00','2025-12-08 00:00:00',2),(9,'测试的笔记','这段文本只是单纯地填充数据，为了测试笔记是否也能保证用户操作的唯一性。','2025-12-08 00:00:00','2025-12-08 00:00:00',2),(10,'12月31号笔记','今天的笔记文本单纯凑字数','2025-12-31 14:22:42','2025-12-31 14:22:42',NULL),(11,'26年3/17笔记by张三','修改了用户登录验证的逻辑，使用SpringSecurity进行登录验证，提高了安全性。','2026-03-17 16:57:16','2026-03-17 16:57:16',2),(12,'Yoru的测试随笔','这是登录Yoru账号后进行测试的第一条随笔内容，查看新增随笔内容的接口和实现是否无误。','2026-03-28 10:22:21','2026-03-28 10:22:21',10),(13,'Giaean\'s first note','这是Giaean的第一篇笔记。','2026-04-09 17:26:54','2026-04-09 17:26:54',12),(15,'Giaean\'s third note','修改了Giaean的第三篇笔记，把标题的中文改为英文。','2026-04-09 17:28:01','2026-04-09 17:28:01',12),(16,'测试功能','测试笔记的新增功能是否完善。','2026-04-22 23:11:50','2026-04-22 23:11:50',2);
/*!40000 ALTER TABLE `notes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tag`
--

DROP TABLE IF EXISTS `tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tag` (
  `tag_id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `tag_name` varchar(50) DEFAULT NULL COMMENT '标签名称',
  `tag_description` varchar(100) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`tag_id`),
  UNIQUE KEY `tag_name` (`tag_name`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='标签表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tag`
--

LOCK TABLES `tag` WRITE;
/*!40000 ALTER TABLE `tag` DISABLE KEYS */;
INSERT INTO `tag` VALUES (1,'Java','Java'),(2,'算法','排序算法'),(3,'数据结构','顺序表、链表、栈、队列以及树等数据结构'),(4,'SpringBoot',''),(5,'操作系统','Windows操作系统、Linux操作系统'),(10,'MySQL','关系型数据库，持久化存储数据'),(11,'Spring',''),(12,'SpringMVC',''),(13,'MyBatis','ORM映射'),(14,'JVM','Java虚拟机'),(15,'设计模式',''),(16,'Redis','redis缓存技术'),(17,'RabbitMQ','中间件、消息队列'),(18,'SpringCloud','微服务架构'),(19,'Nacos','微服务注册中心');
/*!40000 ALTER TABLE `tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `user_id` int NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `user_name` varchar(30) NOT NULL DEFAULT '' COMMENT '用户名',
  `user_password` varchar(100) NOT NULL DEFAULT '' COMMENT '密码',
  `user_nickname` varchar(100) NOT NULL DEFAULT '' COMMENT '昵称',
  `user_email` varchar(100) DEFAULT '' COMMENT '邮箱',
  `user_url` varchar(100) DEFAULT '' COMMENT '个人主页',
  `user_avatar` varchar(100) DEFAULT NULL COMMENT '头像',
  `user_last_login_ip` varchar(30) DEFAULT NULL COMMENT '上次登录ip',
  `user_register_time` datetime DEFAULT NULL COMMENT '注册时间',
  `user_last_login_time` datetime DEFAULT NULL COMMENT '上次登录时间',
  `user_status` int unsigned DEFAULT '1' COMMENT '状态',
  `user_role` varchar(20) NOT NULL DEFAULT 'user' COMMENT '角色',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `user_name` (`user_name`),
  UNIQUE KEY `user_email` (`user_email`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin','123456','JiaYn','admin@wujiaying.com','/img/avatar/avatar.png','/img/avatar/avatar1.jpg','0:0:0:0:0:0:0:1','2023-10-06 21:56:33','2025-10-10 05:16:59',1,'admin'),(2,'zhangsan','123456','张三','zhangsan@china.com','','blog_avatar-1.png','0:0:0:0:0:0:0:1','2018-11-25 14:45:08','2021-02-25 10:19:30',1,'user'),(10,'Yoru','654321','夜戮','yoru@yr.com','','blog_avatar-3.png','127.0.0.1','2025-12-12 00:00:00','2025-12-12 00:00:00',1,'user'),(11,'Anyway','13579','任何','anyway111@aw.com','',NULL,'127.0.0.1','2025-12-12 17:21:18','2025-12-12 17:21:18',0,'user'),(12,'Giaean','13171','彦祖','1317192941@qq.com','',NULL,'127.0.0.1','2025-12-17 10:14:34','2025-12-17 10:14:34',1,'user'),(13,'newAdmin','123456','newAdmin','2342366713@qq.com','',NULL,NULL,'2026-03-13 16:01:23','2026-03-13 16:01:23',1,'admin'),(16,'Wuhu','$2a$10$OaOop.eDg1SZhy.Y6Mj/geGLKIPTs2OAoG1FKNAQ4uyxB.NPTPrRK','Wuhu','wuhu@qq.com','','D:/Self_Directory/Pictures/blog_avatar-3.png',NULL,'2026-04-09 15:59:55','2026-04-09 15:59:55',1,'user');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-25 13:23:32
