-- MySQL dump 10.13  Distrib 8.0.29, for Win64 (x86_64)
--
-- Host: localhost    Database: socialnetwork
-- ------------------------------------------------------
-- Server version	8.0.29

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8_unicode_ci NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb3 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (1,'Trang chủ','Về trang chủ'),(2,'Posts','Các bài viết'),(3,'Notifications','Thông báo'),(4,'Surveys','Khảo sát'),(5,'Users','Người dùng'),(6,'Statistics','Thống kê nền tảng');
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category2`
--

DROP TABLE IF EXISTS `category2`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category2` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8_unicode_ci NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb3 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category2`
--

LOCK TABLES `category2` WRITE;
/*!40000 ALTER TABLE `category2` DISABLE KEYS */;
INSERT INTO `category2` VALUES (1,'Posts','Các bài viết'),(2,'Notifications','Thông báo'),(3,'Surveys','Khảo sát'),(4,'Users','Người dùng');
/*!40000 ALTER TABLE `category2` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comments`
--

DROP TABLE IF EXISTS `comments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comments` (
  `comment_id` int NOT NULL AUTO_INCREMENT,
  `post_id` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  `content` text NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint DEFAULT '0',
  PRIMARY KEY (`comment_id`),
  KEY `post_id` (`post_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `comments_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `posts` (`post_id`) ON DELETE CASCADE,
  CONSTRAINT `comments_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comments`
--

LOCK TABLES `comments` WRITE;
/*!40000 ALTER TABLE `comments` DISABLE KEYS */;
INSERT INTO `comments` VALUES (1,1,2,'Chào mừng bạn đến với mạng xã hội!','2025-01-10 10:05:00','2025-01-10 10:05:00',0),(2,1,3,'Rất vui được gặp bạn!','2025-01-10 10:10:00','2025-01-10 10:10:00',0),(3,2,1,'Cảm ơn bạn, ngày đẹp thật!','2025-01-11 14:05:00','2025-01-11 14:05:00',0),(4,4,5,'Tôi sẽ tham gia khảo sát ngay!','2025-01-13 15:10:00','2025-01-13 15:10:00',0),(5,2,4,'Comment này đã bị xóa.','2025-01-11 14:15:00','2025-01-11 14:15:00',1),(6,12,8,'alo','2025-05-18 02:19:49',NULL,1),(7,15,8,'a','2025-05-18 15:35:03',NULL,0),(8,17,8,'hihi','2025-05-18 15:37:17',NULL,0),(9,12,8,'fsâ','2025-05-18 15:39:52','2025-05-18 23:12:03',0),(10,12,8,'f','2025-05-18 15:52:50',NULL,0),(11,16,8,'f','2025-05-18 15:52:55',NULL,0),(12,16,8,'a','2025-05-18 15:53:00',NULL,0),(13,16,8,'a','2025-05-18 15:53:07',NULL,0),(14,12,8,'a','2025-05-18 15:53:19',NULL,1),(15,18,8,'fsdfsd','2025-05-18 15:53:36',NULL,0),(16,19,8,'anh long long','2025-05-18 17:25:19',NULL,0),(17,15,8,'dfg','2025-05-18 21:29:52',NULL,0),(18,15,8,'vfdsv','2025-05-18 21:29:56',NULL,0),(19,20,8,'gdfg','2025-05-18 21:39:57',NULL,0),(20,21,8,'s','2025-05-18 21:40:28',NULL,0),(21,21,8,'d','2025-05-18 21:40:30',NULL,0),(22,24,8,'aa','2025-05-18 22:57:35','2025-05-18 23:13:21',0),(23,24,8,'f','2025-05-18 23:13:11','2025-05-18 23:13:11',1),(24,27,8,'alo alo','2025-05-19 01:21:11','2025-05-19 01:21:21',1),(25,27,8,'aaa','2025-05-19 01:21:44','2025-05-19 01:21:57',0),(26,27,8,'a','2025-05-19 13:23:55','2025-05-19 13:23:55',0),(27,29,13,'a','2025-05-19 13:42:07','2025-05-19 13:42:07',0),(28,28,8,'chào nhé','2025-05-19 13:53:45','2025-05-19 13:53:45',0),(29,29,8,'ok','2025-05-19 13:55:25','2025-05-19 13:55:25',0),(30,30,8,'alo alo','2025-05-19 23:30:00','2025-05-19 23:30:06',0),(31,33,13,'dsf','2025-05-21 21:57:38','2025-05-21 21:57:38',1),(32,34,13,'a','2025-05-21 22:37:12','2025-05-21 22:37:12',0),(33,34,8,'aaa','2025-05-21 22:41:04','2025-05-21 22:41:04',0),(34,35,8,'kk kk','2025-05-21 22:42:06','2025-05-21 22:42:30',1),(35,35,13,'gg haaaaa','2025-05-22 10:27:54','2025-05-22 11:24:04',0);
/*!40000 ALTER TABLE `comments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `daily_platform_summary`
--

DROP TABLE IF EXISTS `daily_platform_summary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `daily_platform_summary` (
  `summary_date` datetime DEFAULT NULL,
  `new_users_registered_today` int DEFAULT '0' COMMENT 'Số người dùng mới đăng ký trong ngày',
  `new_posts_created_today` int DEFAULT '0' COMMENT 'Số bài viết mới được tạo trong ngày',
  `last_calculated_at` datetime DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `total_posts` int DEFAULT NULL,
  `total_users` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=88 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Thống kê tổng quan hàng ngày của nền tảng';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `daily_platform_summary`
--

LOCK TABLES `daily_platform_summary` WRITE;
/*!40000 ALTER TABLE `daily_platform_summary` DISABLE KEYS */;
INSERT INTO `daily_platform_summary` VALUES ('2025-01-10 00:00:00',0,1,'2025-05-16 15:00:00',73,1,0),('2025-01-11 00:00:00',0,1,'2025-05-16 15:00:00',74,2,0),('2025-01-12 00:00:00',0,1,'2025-05-16 15:00:00',75,3,0),('2025-01-13 00:00:00',0,1,'2025-05-16 15:00:00',76,4,0),('2025-01-14 00:00:00',0,1,'2025-05-16 15:00:00',77,5,0),('2025-05-05 00:00:00',0,2,'2025-05-16 15:00:00',78,7,0),('2025-05-12 00:00:00',8,0,'2025-05-16 15:00:00',79,7,8),('2025-05-15 00:00:00',4,5,'2025-05-16 15:00:00',80,12,12),('2025-05-16 00:00:00',1,0,'2025-05-16 15:41:28',81,12,13),('2025-05-17 00:00:00',0,0,'2025-05-17 17:51:52',82,12,13),('2025-05-18 00:00:00',0,1,'2025-05-18 22:57:08',83,13,12),('2025-05-19 00:00:00',1,3,'2025-05-19 22:13:18',84,17,13),('2025-05-20 00:00:00',0,0,'2025-05-20 01:00:00',85,15,13),('2025-05-21 00:00:00',0,2,'2025-05-21 23:10:07',86,14,13),('2025-05-22 00:00:00',0,1,'2025-05-22 23:05:45',87,15,13);
/*!40000 ALTER TABLE `daily_platform_summary` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event_notifications`
--

DROP TABLE IF EXISTS `event_notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `event_notifications` (
  `notification_id` int NOT NULL AUTO_INCREMENT,
  `admin_id` int NOT NULL,
  `title` varchar(255) NOT NULL,
  `event_id` int NOT NULL,
  `receiver_user_id` int DEFAULT NULL,
  `group_id` int DEFAULT NULL,
  `content` text,
  `sent_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`notification_id`),
  KEY `admin_id` (`admin_id`),
  KEY `event_id` (`event_id`),
  KEY `receiver_user_id` (`receiver_user_id`),
  KEY `group_id` (`group_id`),
  CONSTRAINT `event_notifications_ibfk_1` FOREIGN KEY (`admin_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `event_notifications_ibfk_2` FOREIGN KEY (`event_id`) REFERENCES `events` (`event_id`) ON DELETE CASCADE,
  CONSTRAINT `event_notifications_ibfk_3` FOREIGN KEY (`receiver_user_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL,
  CONSTRAINT `event_notifications_ibfk_4` FOREIGN KEY (`group_id`) REFERENCES `user_groups` (`group_id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event_notifications`
--

LOCK TABLES `event_notifications` WRITE;
/*!40000 ALTER TABLE `event_notifications` DISABLE KEYS */;
INSERT INTO `event_notifications` VALUES (2,4,'Thông báo ngày hội việc làm',2,NULL,2,'Nhóm CNTT được mời tham gia ngày hội việc làm.','2025-01-26 14:00:00'),(3,4,'Lễ kỷ niệm 20 năm',3,NULL,1,'Nhóm K2018 được mời tham gia lễ kỷ niệm.','2025-01-27 09:00:00');
/*!40000 ALTER TABLE `event_notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `events`
--

DROP TABLE IF EXISTS `events`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `events` (
  `event_id` int NOT NULL AUTO_INCREMENT,
  `admin_id` int NOT NULL,
  `title` varchar(255) NOT NULL,
  `description` text,
  `start_date` datetime NOT NULL,
  `end_date` datetime NOT NULL,
  `location` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`event_id`),
  KEY `admin_id` (`admin_id`),
  CONSTRAINT `events_ibfk_1` FOREIGN KEY (`admin_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `events`
--

LOCK TABLES `events` WRITE;
/*!40000 ALTER TABLE `events` DISABLE KEYS */;
INSERT INTO `events` VALUES (2,4,'Ngày hội việc làm','Tuyển dụng từ các công ty lớn.','2025-02-05 08:00:00','2025-02-05 17:00:00','Sân trường'),(3,4,'Lễ kỷ niệm 20 năm','Kỷ niệm thành lập trường.','2025-03-10 18:00:00','2025-03-10 21:00:00','Hội trường B'),(4,4,'Workshop kỹ năng','Học kỹ năng mềm từ chuyên gia.','2025-02-15 13:00:00','2025-02-15 16:00:00','Phòng học C1'),(5,4,'Gala âm nhạc','Buổi biểu diễn của cựu sinh viên.','2025-02-20 19:00:00','2025-02-20 22:00:00','Sân khấu ngoài trời');
/*!40000 ALTER TABLE `events` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_members`
--

DROP TABLE IF EXISTS `group_members`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_members` (
  `group_member_id` int NOT NULL AUTO_INCREMENT,
  `group_id` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  `joined_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`group_member_id`),
  UNIQUE KEY `group_id` (`group_id`,`user_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `group_members_ibfk_1` FOREIGN KEY (`group_id`) REFERENCES `user_groups` (`group_id`) ON DELETE CASCADE,
  CONSTRAINT `group_members_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_members`
--

LOCK TABLES `group_members` WRITE;
/*!40000 ALTER TABLE `group_members` DISABLE KEYS */;
INSERT INTO `group_members` VALUES (1,1,1,'2025-01-20 10:05:00'),(2,1,2,'2025-01-20 10:10:00'),(3,2,3,'2025-01-21 14:05:00'),(4,3,3,'2025-01-22 09:05:00'),(5,4,5,'2025-01-23 11:05:00');
/*!40000 ALTER TABLE `group_members` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `periodic_summary_stats`
--

DROP TABLE IF EXISTS `periodic_summary_stats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `periodic_summary_stats` (
  `summary_id` int NOT NULL AUTO_INCREMENT,
  `summary_year` int NOT NULL COMMENT 'Năm thống kê (ví dụ: 2023)',
  `summary_quarter` tinyint DEFAULT NULL COMMENT 'Quý thống kê (1-4), NULL nếu không áp dụng',
  `summary_month` tinyint DEFAULT NULL COMMENT 'Tháng thống kê (1-12), NULL nếu không áp dụng',
  `period_type` enum('yearly','quarterly','monthly') NOT NULL COMMENT 'Loại chu kỳ: hàng năm, hàng quý, hàng tháng',
  `new_posts_count` int DEFAULT '0' COMMENT 'Số lượng bài viết mới trong chu kỳ',
  `new_users_count` int DEFAULT '0' COMMENT 'Số lượng người dùng mới đăng ký trong chu kỳ',
  `calculated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời điểm tính toán cuối cùng',
  PRIMARY KEY (`summary_id`),
  UNIQUE KEY `idx_period_unique` (`summary_year`,`summary_quarter`,`summary_month`,`period_type`) COMMENT 'Đảm bảo mỗi chu kỳ chỉ có một bản ghi thống kê'
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Bảng thống kê số lượng người dùng và bài viết theo tháng, quý, năm';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `periodic_summary_stats`
--

LOCK TABLES `periodic_summary_stats` WRITE;
/*!40000 ALTER TABLE `periodic_summary_stats` DISABLE KEYS */;
INSERT INTO `periodic_summary_stats` VALUES (16,2025,NULL,1,'monthly',5,0,'2025-05-16 15:00:00'),(17,2025,NULL,5,'monthly',8,13,'2025-05-16 15:00:00'),(18,2025,1,NULL,'quarterly',5,0,'2025-05-16 15:00:00'),(19,2025,2,NULL,'quarterly',8,13,'2025-05-16 15:00:00'),(20,2025,NULL,NULL,'yearly',13,13,'2025-05-16 15:00:00');
/*!40000 ALTER TABLE `periodic_summary_stats` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `posts`
--

DROP TABLE IF EXISTS `posts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `posts` (
  `post_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `content` text,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `is_comment_locked` tinyint DEFAULT '0',
  `is_deleted` tinyint DEFAULT '0',
  `image` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`post_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `posts_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `posts`
--

LOCK TABLES `posts` WRITE;
/*!40000 ALTER TABLE `posts` DISABLE KEYS */;
INSERT INTO `posts` VALUES (1,1,'Chào mọi người, tôi vừa tham gia mạng xã hội này!','2025-01-10 10:00:00','2025-05-14 15:56:04',0,0,'https://res.cloudinary.com/dxxwcby8l/image/upload/v1647248652/dkeolz3ghc0eino87iec.jpg'),(2,2,'Hôm nay là một ngày đẹp trời!','2025-01-11 14:00:00','2025-05-15 17:09:45',0,0,'https://res.cloudinary.com/dxxwcby8l/image/upload/v1647248652/dkeolz3ghc0eino87iec.jpg'),(3,3,'Thông báo lớp học bồi dưỡng sắp tới.','2025-01-12 09:00:00','2025-01-12 09:00:00',1,0,NULL),(4,4,'Khảo sát về chất lượng đào tạo, mọi người tham gia nhé!','2025-01-13 15:00:00','2025-05-15 15:27:44',0,0,NULL),(5,1,'Bài viết này đã bị xóa.','2025-01-14 12:00:00','2025-05-15 15:27:44',0,0,NULL),(6,1,'hahaa','2025-05-05 15:32:33','2025-05-15 15:27:44',0,0,NULL),(7,2,'hihi','2025-05-05 15:32:33','2025-05-15 15:27:44',0,0,NULL),(8,1,'kkkkkkkkkkkkkk','2025-05-15 13:01:26',NULL,0,1,NULL),(9,4,'alo alo','2025-05-15 14:09:04','2025-05-15 17:09:45',0,0,NULL),(10,3,'aaaa','2025-05-15 14:36:07','2025-05-15 17:09:45',0,0,NULL),(11,3,'fsdsdffsd','2025-05-15 22:14:14',NULL,0,0,NULL),(12,3,'sdfsdf','2025-05-15 22:15:25',NULL,0,0,NULL),(13,1,'sfsf','2025-05-16 14:21:23',NULL,0,1,NULL),(14,2,'co len','2025-05-16 15:38:56',NULL,0,1,NULL),(15,8,'fsdf','2025-05-18 15:34:53','2025-05-18 22:32:44',0,1,NULL),(16,8,'kkg','2025-05-18 15:35:16','2025-05-18 22:32:34',0,1,NULL),(17,8,'kk','2025-05-18 15:36:58','2025-05-19 13:19:57',0,1,NULL),(18,8,'aaaa','2025-05-18 15:53:31','2025-05-18 15:53:31',0,1,NULL),(19,8,'anh long','2025-05-18 17:25:06','2025-05-18 17:25:06',0,1,'https://res.cloudinary.com/dxxwcby8l/image/upload/v1747563903/social_app_posts/gfptf0wumrl14mzxfmis.png'),(20,8,'dfgdf','2025-05-18 21:30:06','2025-05-18 21:30:06',0,1,NULL),(21,8,'dfgsdfg','2025-05-18 21:40:18','2025-05-18 21:40:18',0,1,'https://res.cloudinary.com/dxxwcby8l/image/upload/v1747579215/social_app_posts/xa4dlqwramjcskv6ycdo.png'),(22,8,'g','2025-05-18 22:36:39','2025-05-18 22:36:39',0,1,NULL),(23,8,'','2025-05-18 22:39:06','2025-05-18 22:39:06',0,1,'https://res.cloudinary.com/dxxwcby8l/image/upload/v1747582746/social_app_posts/x7yavwzoydjd774gsdkx.png'),(24,8,'faffsdfsd','2025-05-18 22:57:29','2025-05-18 23:01:18',0,1,NULL),(25,8,'sdfsdf','2025-05-18 23:13:45','2025-05-18 23:13:45',0,1,NULL),(26,8,'aaa','2025-05-18 23:14:00','2025-05-19 01:20:41',0,0,NULL),(27,8,'hihi','2025-05-19 01:20:59','2025-05-19 13:25:58',0,1,NULL),(28,8,'xin chào nhé ok','2025-05-19 13:35:29','2025-05-19 13:56:06',1,1,'https://res.cloudinary.com/dxxwcby8l/image/upload/v1747636530/social_app_posts/rlxkahdpwybxou4d1s9w.png'),(29,13,'tui mới tạo tài khoản thôi à','2025-05-19 13:41:53','2025-05-19 13:54:47',0,1,'https://res.cloudinary.com/dxxwcby8l/image/upload/v1747637687/social_app_posts/vagomp03k8z9ahavx9ok.png'),(30,8,'đăng bài a long a','2025-05-19 23:29:30','2025-05-19 23:30:14',1,1,NULL),(31,13,'aaaaa','2025-05-20 10:34:58','2025-05-20 10:34:58',0,1,NULL),(32,13,'chào','2025-05-21 21:18:35','2025-05-21 22:00:53',0,1,NULL),(33,13,'fsdfas','2025-05-21 21:57:23','2025-05-21 21:57:23',0,1,NULL),(34,8,'chào','2025-05-21 22:37:00','2025-05-21 22:37:00',0,0,NULL),(35,8,'hô hô','2025-05-21 22:41:49','2025-05-21 22:42:21',0,0,NULL),(36,13,'hell0o','2025-05-22 10:28:59','2025-05-22 11:17:29',0,0,NULL);
/*!40000 ALTER TABLE `posts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `question_types`
--

DROP TABLE IF EXISTS `question_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `question_types` (
  `type_id` int NOT NULL AUTO_INCREMENT,
  `type_name` varchar(100) NOT NULL,
  PRIMARY KEY (`type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `question_types`
--

LOCK TABLES `question_types` WRITE;
/*!40000 ALTER TABLE `question_types` DISABLE KEYS */;
INSERT INTO `question_types` VALUES (1,'Multiple Choice'),(2,'Essay');
/*!40000 ALTER TABLE `question_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reactions`
--

DROP TABLE IF EXISTS `reactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reactions` (
  `reaction_id` int NOT NULL AUTO_INCREMENT,
  `post_id` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  `reaction_type` enum('like','haha','heart') NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `comment_id` int DEFAULT NULL,
  PRIMARY KEY (`reaction_id`),
  UNIQUE KEY `uc_user_reaction_target` (`user_id`,`post_id`,`comment_id`),
  KEY `user_id` (`user_id`),
  KEY `fk_reactions_comment` (`comment_id`),
  KEY `idx_reactions_post_id` (`post_id`),
  CONSTRAINT `fk_reactions_comment` FOREIGN KEY (`comment_id`) REFERENCES `comments` (`comment_id`),
  CONSTRAINT `reactions_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `posts` (`post_id`) ON DELETE CASCADE,
  CONSTRAINT `reactions_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=118 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reactions`
--

LOCK TABLES `reactions` WRITE;
/*!40000 ALTER TABLE `reactions` DISABLE KEYS */;
INSERT INTO `reactions` VALUES (1,1,2,'like','2025-01-10 10:06:00',NULL),(2,1,3,'heart','2025-01-10 10:11:00',NULL),(3,2,1,'haha','2025-01-11 14:06:00',NULL),(4,4,5,'like','2025-01-13 15:11:00',NULL),(5,3,4,'heart','2025-01-12 09:05:00',NULL),(13,NULL,2,'like','2025-05-18 01:40:53',1),(33,17,8,'heart','2025-05-18 15:37:09',NULL),(50,12,8,'haha','2025-05-18 15:52:51',10),(66,4,8,'like','2025-05-18 16:07:26',4),(75,1,8,'like','2025-05-18 16:26:05',1),(76,18,8,'heart','2025-05-18 16:27:55',NULL),(77,19,8,'like','2025-05-18 17:25:13',NULL),(78,19,8,'heart','2025-05-18 17:25:22',16),(81,21,8,'heart','2025-05-18 21:40:22',NULL),(83,21,8,'like','2025-05-18 21:40:33',20),(85,16,8,'like','2025-05-18 22:19:00',NULL),(86,15,8,'heart','2025-05-18 22:32:49',NULL),(91,27,8,'heart','2025-05-19 01:21:07',NULL),(92,27,8,'heart','2025-05-19 01:21:14',24),(97,29,13,'haha','2025-05-19 13:54:58',27),(99,30,8,'like','2025-05-19 23:29:56',NULL),(108,33,13,'haha','2025-05-21 21:57:44',31),(115,35,13,'like','2025-05-22 10:25:59',NULL),(116,35,13,'haha','2025-05-22 10:27:56',35);
/*!40000 ALTER TABLE `reactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `survey_options`
--

DROP TABLE IF EXISTS `survey_options`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `survey_options` (
  `option_id` int NOT NULL AUTO_INCREMENT,
  `question_id` int DEFAULT NULL,
  `option_text` varchar(255) NOT NULL,
  PRIMARY KEY (`option_id`),
  KEY `question_id` (`question_id`),
  CONSTRAINT `survey_options_ibfk_1` FOREIGN KEY (`question_id`) REFERENCES `survey_questions` (`question_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=66 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `survey_options`
--

LOCK TABLES `survey_options` WRITE;
/*!40000 ALTER TABLE `survey_options` DISABLE KEYS */;
INSERT INTO `survey_options` VALUES (1,1,'aaa'),(2,2,'aaa'),(4,1,'aa'),(5,2,'a'),(7,1,'a'),(30,24,'1'),(31,25,'âaa'),(46,4,'htmll'),(56,6,'có'),(57,6,'ko'),(61,32,'1'),(62,32,'2'),(63,32,'3'),(64,33,'ok'),(65,33,'ko');
/*!40000 ALTER TABLE `survey_options` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `survey_questions`
--

DROP TABLE IF EXISTS `survey_questions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `survey_questions` (
  `question_id` int NOT NULL AUTO_INCREMENT,
  `survey_id` int DEFAULT NULL,
  `question_text` text NOT NULL,
  `is_required` tinyint DEFAULT '0',
  `question_order` int DEFAULT NULL,
  `type_id` int NOT NULL,
  PRIMARY KEY (`question_id`),
  KEY `survey_id` (`survey_id`),
  KEY `survey_questions_ibfk_2` (`type_id`),
  CONSTRAINT `survey_questions_ibfk_1` FOREIGN KEY (`survey_id`) REFERENCES `surveys` (`survey_id`) ON DELETE CASCADE,
  CONSTRAINT `survey_questions_ibfk_2` FOREIGN KEY (`type_id`) REFERENCES `question_types` (`type_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `survey_questions`
--

LOCK TABLES `survey_questions` WRITE;
/*!40000 ALTER TABLE `survey_questions` DISABLE KEYS */;
INSERT INTO `survey_questions` VALUES (1,1,'Mức độ hài lòng của bạn về nội dung môn học trong học kỳ qua?',1,3,1),(2,1,'Phương pháp giảng dạy của giảng viên có phù hợp và dễ hiểu không?',1,2,1),(3,1,'Bạn có góp ý cụ thể nào để cải thiện chất lượng giảng dạy không?',0,1,2),(4,2,'Theo bạn, ngôn ngữ lập trình nào sẽ có nhu cầu cao trong 2 năm tới?',1,1,1),(5,2,'Bạn mong muốn nhà trường tổ chức thêm các workshop về chủ đề công nghệ nào?',1,2,2),(6,2,'Bạn có thường xuyên sử dụng các nguồn tài liệu trực tuyến để tự học không?',0,3,1),(7,3,'Bạn đánh giá thế nào về sự đa dạng của các đầu sách chuyên ngành tại thư viện?',1,1,1),(8,3,'Không gian học tập tại thư viện có yên tĩnh và thoải mái không???????????',1,2,1),(9,3,'Bạn có đề xuất gì để thư viện cải thiện nguồn tài liệu số (e-books, database online) không?',0,3,2),(22,6,'nhan xet',0,NULL,2),(23,6,'1 + 1 = ?',0,NULL,1),(24,6,'fsf',0,NULL,1),(25,6,'sdfsd',0,NULL,1),(27,2,'alo',0,NULL,2),(28,5,'sutt',0,NULL,2),(29,2,'1 + 1 = ?',0,NULL,2),(31,8,'Cảm nhận hiện tại',1,NULL,2),(32,8,'1 + 1 =  ?',1,NULL,1),(33,8,'trắc nghiệm không bắt buộc',0,NULL,1);
/*!40000 ALTER TABLE `survey_questions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `survey_responses`
--

DROP TABLE IF EXISTS `survey_responses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `survey_responses` (
  `response_id` int NOT NULL AUTO_INCREMENT,
  `survey_id` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  `question_id` int DEFAULT NULL,
  `option_id` int DEFAULT NULL,
  `response_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `response_text` text,
  PRIMARY KEY (`response_id`),
  KEY `survey_id` (`survey_id`),
  KEY `user_id` (`user_id`),
  KEY `question_id` (`question_id`),
  KEY `option_id` (`option_id`),
  CONSTRAINT `survey_responses_ibfk_1` FOREIGN KEY (`survey_id`) REFERENCES `surveys` (`survey_id`) ON DELETE CASCADE,
  CONSTRAINT `survey_responses_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL,
  CONSTRAINT `survey_responses_ibfk_3` FOREIGN KEY (`question_id`) REFERENCES `survey_questions` (`question_id`) ON DELETE CASCADE,
  CONSTRAINT `survey_responses_ibfk_4` FOREIGN KEY (`option_id`) REFERENCES `survey_options` (`option_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `survey_responses`
--

LOCK TABLES `survey_responses` WRITE;
/*!40000 ALTER TABLE `survey_responses` DISABLE KEYS */;
INSERT INTO `survey_responses` VALUES (3,1,1,3,NULL,'2025-05-11 11:22:31','Cần thêm nhiều bài tập tình huống thực tế hơn trong các môn chuyên ngành.'),(6,1,2,3,NULL,'2025-05-10 11:22:31','Thời lượng thực hành cho một số môn nên được tăng lên.'),(8,2,3,5,NULL,'2025-05-11 11:22:31','Workshop về AI và Machine Learning, cũng như Cloud Computing (AWS/Azure).'),(12,3,5,9,NULL,'2025-05-09 11:22:31','Nên có thêm nhiều máy tính cấu hình mạnh hơn ở thư viện và bổ sung các phần mềm chuyên dụng cho sinh viên ngành kỹ thuật.'),(14,2,1,5,NULL,'2025-05-11 08:22:31','Các buổi chia sẻ từ cựu sinh viên thành đạt về kinh nghiệm làm việc thực tế.'),(15,2,8,5,NULL,'2025-05-19 18:59:38','a'),(16,2,8,27,NULL,'2025-05-19 18:59:38','a'),(17,2,8,29,NULL,'2025-05-19 18:59:38','a'),(18,1,8,3,NULL,'2025-05-19 19:01:36','không có'),(20,1,13,1,1,'2025-05-19 21:15:33',NULL),(21,1,13,2,2,'2025-05-19 21:15:33',NULL),(22,1,13,3,NULL,'2025-05-19 21:15:33','dạ hong'),(23,6,8,22,NULL,'2025-05-19 23:30:47','alio'),(24,6,8,24,30,'2025-05-19 23:30:47',NULL),(25,6,8,25,31,'2025-05-19 23:30:47',NULL),(34,8,8,31,NULL,'2025-05-21 21:19:48','hic'),(35,8,8,32,62,'2025-05-21 21:19:48',NULL),(36,8,8,33,64,'2025-05-21 21:19:48',NULL),(37,8,13,31,NULL,'2025-05-22 10:21:46','tao lao'),(38,8,13,32,63,'2025-05-22 10:21:46',NULL),(39,8,13,33,65,'2025-05-22 10:21:46',NULL);
/*!40000 ALTER TABLE `survey_responses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `surveys`
--

DROP TABLE IF EXISTS `surveys`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `surveys` (
  `survey_id` int NOT NULL AUTO_INCREMENT,
  `admin_id` int NOT NULL,
  `post_id` int DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  `description` text,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `is_active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`survey_id`),
  KEY `admin_id` (`admin_id`),
  KEY `post_id` (`post_id`),
  CONSTRAINT `surveys_ibfk_1` FOREIGN KEY (`admin_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `surveys_ibfk_2` FOREIGN KEY (`post_id`) REFERENCES `posts` (`post_id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `surveys`
--

LOCK TABLES `surveys` WRITE;
/*!40000 ALTER TABLE `surveys` DISABLE KEYS */;
INSERT INTO `surveys` VALUES (1,4,NULL,'Đánh giá chất lượng đào tạo HK1 2024-2025','Vui lòng chia sẻ ý kiến của bạn để nhà trường cải thiện chất lượng đào tạo trong học kỳ vừa qua.','2025-05-11 11:22:31',0),(2,4,NULL,'Khảo sát nhu cầu kỹ năng CNTT năm 2025','Chúng tôi muốn tìm hiểu về các kỹ năng công nghệ thông tin quan trọng mà thị trường lao động đang yêu cầu.','2025-05-16 14:11:54',1),(3,4,NULL,'Đánh giá dịch vụ thư viện và nguồn tài liệu học tậppp','Ý kiến của bạn giúp thư viện nâng cao chất lượng phục vụ và bổ sung tài liệu phù hợp.','2025-05-16 11:10:34',1),(5,4,NULL,'alo','1234','2025-05-13 19:07:35',1),(6,4,NULL,'hello','kkk','2025-05-16 12:21:19',1),(8,4,NULL,'  Nghiêm túc nhé','zô đi kk','2025-05-20 12:18:12',0);
/*!40000 ALTER TABLE `surveys` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_groups`
--

DROP TABLE IF EXISTS `user_groups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_groups` (
  `group_id` int NOT NULL AUTO_INCREMENT,
  `admin_id` int NOT NULL,
  `group_name` varchar(255) NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`group_id`),
  KEY `admin_id` (`admin_id`),
  CONSTRAINT `user_groups_ibfk_1` FOREIGN KEY (`admin_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_groups`
--

LOCK TABLES `user_groups` WRITE;
/*!40000 ALTER TABLE `user_groups` DISABLE KEYS */;
INSERT INTO `user_groups` VALUES (1,4,'Cựu sinh viên K2018','2025-01-20 10:00:00'),(2,4,'Cựu sinh viên ngành CNTT','2025-01-21 14:00:00'),(3,4,'Nhóm giảng viên','2025-01-22 09:00:00'),(4,4,'Cựu sinh viên K2019','2025-01-23 11:00:00'),(5,4,'Nhóm yêu âm nhạc','2025-01-24 15:00:00');
/*!40000 ALTER TABLE `user_groups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(20) NOT NULL,
  `student_id` varchar(20) DEFAULT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` varchar(20) DEFAULT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  `cover_image` varchar(255) DEFAULT NULL,
  `full_name` varchar(100) NOT NULL,
  `is_verified` tinyint DEFAULT '0',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `last_password_change` datetime DEFAULT NULL,
  `is_locked` tinyint DEFAULT '0',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `student_id` (`student_id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'john_doe','S12345','john.doe@example.com','password123','alumni','avatar1.jpg','cover1.jpg','Nguyễn Văn A',1,'2025-05-12 10:02:06',NULL,0),(2,'jane_smith','S54321','jane.smith@example.com','password456','lecturer','avatar2.jpg','cover2.jpg','Trần Thị B',1,'2025-05-12 10:02:06',NULL,0),(3,'mark_taylor','S67890','mark.taylor@example.com','password789','admin','avatar3.jpg','cover3.jpg','Lê Quang C',1,'2025-05-12 10:02:06',NULL,0),(4,'lucy_white',NULL,'lucy.white@example.com','password101','alumni','avatar4.jpg','cover4.jpg','Phan Thị D',0,'2025-05-12 10:02:06',NULL,0),(5,'mike_brown','S11223','mike.brown@example.com','password202','lecturer','avatar5.jpg','cover5.jpg','Hoàng Minh E',1,'2025-05-12 10:02:06',NULL,0),(6,'anna_lee',NULL,'anna.lee@example.com','password303','admin','avatar6.jpg','cover6.jpg','Đỗ Mai F',1,'2025-05-12 10:02:06',NULL,0),(7,'david_green','S44556','david.green@example.com','password404','alumni','avatar7.jpg','cover7.jpg','Ngô Lê G',0,'2025-05-12 10:02:06',NULL,0),(8,'test','test','test@exmaple.com','$2a$12$qPnQF1mXa5weycJLVRKqnOzwZs9lhEWv9ctc1wLbEkofkmqKZU7qO','ROLE_ADMIN','avatar7.jpg','cover7.jpg','Ngô Test',1,'2025-05-12 10:02:06',NULL,0),(9,'coca','coca','coca@example.com','password123','alumni','avatar8.jpg','cover8.jpg','coca cola',0,'2025-05-15 16:08:39',NULL,0),(10,'pepsi','pepsi','pepsi@example.com','password123','alumni','avatar9.jpg','cover9.jpg','pepsi',0,'2025-05-15 16:13:06',NULL,0),(11,'sting','sting','sting@example.com','password123','alumni','avatar10.jpg','cover10.jpg','sting',0,'2025-05-15 16:31:45',NULL,0),(12,'aaa','aaa','aaa@example.com','password123','alumni','avatar10.jpg','cover10.jpg','aaa',0,'2025-05-15 17:08:45',NULL,0),(13,'danh','danh','danh@example.com','$2a$12$qPnQF1mXa5weycJLVRKqnOzwZs9lhEWv9ctc1wLbEkofkmqKZU7qO','lecturer','avatar10.jpg','cover10.jpg','danh danh',1,'2025-05-19 13:39:39',NULL,0);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-22 23:07:55
