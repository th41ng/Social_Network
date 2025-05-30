-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: localhost    Database: socialnetwork
-- ------------------------------------------------------
-- Server version	8.0.40

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
  `name` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;
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
  `name` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category2`
--

LOCK TABLES `category2` WRITE;
/*!40000 ALTER TABLE `category2` DISABLE KEYS */;
INSERT INTO `category2` VALUES (1,'Posts','Các bài viết'),(2,'Notifications','Thông báo'),(3,'Surveys','Khảo sát'),(4,'Users','Người dùng'),(5,'Chats','Nhắn tin');
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
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comments`
--

LOCK TABLES `comments` WRITE;
/*!40000 ALTER TABLE `comments` DISABLE KEYS */;
INSERT INTO `comments` VALUES (39,47,5,'Bài đăng rất hay! Mong chờ thêm nhiều chia sẻ từ bạn.','2025-05-01 10:00:00',NULL,0),(40,48,6,'Hôm nay mình định đi công viên với bạn bè.','2025-05-02 11:00:00',NULL,0),(41,49,7,'Cảm ơn bạn! Mình sẽ thử áp dụng các mẹo này.','2025-05-03 12:00:00',NULL,0),(42,50,8,'Chúc mừng bạn hoàn thành dự án! Cố gắng phát triển thêm nhé.','2025-05-04 13:00:00',NULL,0),(43,51,9,'Mình thích phim hoạt hình của Ghibli, rất đáng xem.','2025-05-05 15:30:00','2025-05-05 16:00:00',0),(44,52,10,'Câu nói này rất truyền cảm hứng, cảm ơn bạn đã chia sẻ!','2025-05-06 17:00:00',NULL,0),(45,53,11,'Hình ảnh đẹp quá! Bạn đã đi những đâu trong chuyến du lịch này?','2025-05-07 19:00:00',NULL,0),(46,54,12,'Chúc mừng bạn! Python là một ngôn ngữ tuyệt vời để bắt đầu.','2025-05-08 20:00:00',NULL,0),(47,55,13,'Bài viết rất thú vị. Bạn có thể chia sẻ thêm không?','2025-05-09 21:00:00',NULL,0),(48,56,4,'Bạn gặp vấn đề gì? Mình có thể giúp bạn giải bài toán này.','2025-05-10 22:00:00',NULL,0);
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
) ENGINE=InnoDB AUTO_INCREMENT=102 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Thống kê tổng quan hàng ngày của nền tảng';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `daily_platform_summary`
--

LOCK TABLES `daily_platform_summary` WRITE;
/*!40000 ALTER TABLE `daily_platform_summary` DISABLE KEYS */;
INSERT INTO `daily_platform_summary` VALUES ('2025-05-30 00:00:00',1,0,'2025-05-30 23:53:14',91,10,13),('2025-05-01 00:00:00',5,10,'2025-05-01 23:59:00',92,50,100),('2025-05-02 00:00:00',3,8,'2025-05-02 23:59:00',93,58,103),('2025-05-03 00:00:00',7,12,'2025-05-03 23:59:00',94,70,110),('2025-05-04 00:00:00',2,6,'2025-05-04 23:59:00',95,76,112),('2025-05-05 00:00:00',4,9,'2025-05-05 23:59:00',96,85,116),('2025-05-06 00:00:00',6,11,'2025-05-06 23:59:00',97,96,122),('2025-05-07 00:00:00',3,7,'2025-05-07 23:59:00',98,103,125),('2025-05-08 00:00:00',5,13,'2025-05-08 23:59:00',99,116,130),('2025-05-09 00:00:00',4,8,'2025-05-09 23:59:00',100,124,134),('2025-05-10 00:00:00',6,14,'2025-05-10 23:59:00',101,138,140);
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
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event_notifications`
--

LOCK TABLES `event_notifications` WRITE;
/*!40000 ALTER TABLE `event_notifications` DISABLE KEYS */;
INSERT INTO `event_notifications` VALUES (14,1,'Thông báo Hội thảo Công nghệ 2025 new',6,NULL,NULL,'Mời bạn tham gia Hội thảo Công nghệ 2025','2025-05-30 23:59:35'),(15,1,'Nhắc nhở Workshop Lập trình Python',2,NULL,3,'Workshop Lập trình Python','2025-05-30 23:47:44'),(16,1,'Sự kiện Ngày hội Du lịch Việt Nam',3,4,NULL,'Sắp tới có sự kiện Ngày hội Du lịch tại Đà Nẵng, hy vọng bạn sẽ đến tham dự.','2025-05-30 23:48:10'),(17,1,'Mời tham dự Buổi gặp mặt Developer',4,1,2,'Buổi gặp mặt cộng đồng developer sẽ diễn ra vào tối 10/07.','2025-05-30 23:48:41'),(18,1,'Hội thảo An ninh mạng sắp tới',5,13,NULL,'Hội thảo về an ninh mạng sẽ giúp bạn nâng cao kỹ năng bảo mật','2025-05-30 23:49:21'),(19,1,'Lễ hội Văn hóa Truyền thống',9,NULL,5,'Hãy tham gia lễ hội văn hóa để hiểu hơn về truyền thống','2025-05-30 23:49:47');
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
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `events`
--

LOCK TABLES `events` WRITE;
/*!40000 ALTER TABLE `events` DISABLE KEYS */;
INSERT INTO `events` VALUES (1,1,'Hội thảo Công nghệ 2025','Hội thảo chia sẻ về các xu hướng công nghệ mới nhất.','2025-06-10 09:00:00','2025-06-10 17:00:00','Hà Nội'),(2,1,'Workshop Lập trình Python','Khóa học thực hành lập trình Python cơ bản.','2025-06-15 13:00:00','2025-06-15 18:00:00','TP. Hồ Chí Minh'),(3,1,'Ngày hội Du lịch Việt Nam','Sự kiện giới thiệu các điểm đến hấp dẫn.','2025-07-01 08:00:00','2025-07-01 20:00:00','Đà Nẵng'),(4,1,'Buổi gặp mặt Cộng đồng Developer','Giao lưu, trao đổi kinh nghiệm giữa các lập trình viên.','2025-07-10 18:00:00','2025-07-10 21:00:00','Hải Phòng'),(5,1,'Hội thảo An ninh mạng','Tìm hiểu về các kỹ thuật bảo mật hiện đại.','2025-07-20 09:00:00','2025-07-20 16:00:00','Cần Thơ'),(6,1,'Sự kiện Khởi nghiệp Công nghệ','Chia sẻ kinh nghiệm và kết nối các startup.','2025-08-05 10:00:00','2025-08-05 17:00:00','Hà Nội'),(7,1,'Hội thảo AI và Machine Learning','Khám phá công nghệ AI ứng dụng trong thực tế.','2025-08-15 09:00:00','2025-08-15 15:00:00','TP. Hồ Chí Minh'),(8,1,'Triển lãm Sách Tháng 8','Giới thiệu và bán sách mới.','2025-08-20 08:00:00','2025-08-22 18:00:00','Đà Nẵng'),(9,1,'Lễ hội Văn hóa Truyền thống','Tôn vinh và quảng bá văn hóa dân tộc.','2025-09-01 09:00:00','2025-09-03 21:00:00','Hải Phòng'),(10,1,'Hội nghị Phát triển Phần mềm','Trao đổi về các công nghệ phát triển phần mềm mới.','2025-09-10 08:00:00','2025-09-10 17:00:00','Cần Thơ');
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
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_members`
--

LOCK TABLES `group_members` WRITE;
/*!40000 ALTER TABLE `group_members` DISABLE KEYS */;
INSERT INTO `group_members` VALUES (9,1,1,'2025-05-30 23:41:08'),(11,2,5,'2025-05-30 23:41:17'),(12,2,6,'2025-05-30 23:41:17'),(13,3,11,'2025-05-30 23:41:30'),(14,3,12,'2025-05-30 23:41:30'),(15,3,13,'2025-05-30 23:41:30'),(16,3,17,'2025-05-30 23:41:30'),(17,3,18,'2025-05-30 23:41:30'),(18,4,7,'2025-05-30 23:41:42'),(19,4,9,'2025-05-30 23:41:42'),(20,4,10,'2025-05-30 23:41:42'),(21,4,13,'2025-05-30 23:41:42'),(22,5,12,'2025-05-30 23:41:51'),(23,5,13,'2025-05-30 23:41:51'),(24,5,17,'2025-05-30 23:41:51'),(25,5,18,'2025-05-30 23:41:51');
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
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Bảng thống kê số lượng người dùng và bài viết theo tháng, quý, năm';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `periodic_summary_stats`
--

LOCK TABLES `periodic_summary_stats` WRITE;
/*!40000 ALTER TABLE `periodic_summary_stats` DISABLE KEYS */;
INSERT INTO `periodic_summary_stats` VALUES (21,2025,NULL,5,'monthly',80,50,'2025-05-31 23:59:00'),(22,2025,2,NULL,'quarterly',240,150,'2025-06-30 23:59:00'),(23,2025,NULL,NULL,'yearly',960,600,'2025-12-31 23:59:00'),(24,2025,NULL,4,'monthly',75,40,'2025-04-30 23:59:00'),(25,2025,1,NULL,'quarterly',280,170,'2025-03-31 23:59:00'),(26,2024,NULL,NULL,'yearly',1120,700,'2024-12-31 23:59:00'),(27,2024,4,NULL,'quarterly',320,180,'2024-12-31 23:59:00'),(28,2024,NULL,12,'monthly',100,60,'2024-12-31 23:59:00'),(29,2024,NULL,11,'monthly',85,50,'2024-11-30 23:59:00'),(30,2024,NULL,10,'monthly',90,55,'2024-10-31 23:59:00');
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
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `posts`
--

LOCK TABLES `posts` WRITE;
/*!40000 ALTER TABLE `posts` DISABLE KEYS */;
INSERT INTO `posts` VALUES (47,4,'Chào mọi người, đây là bài đăng đầu tiên của tôi trên nền tảng này!','2025-05-01 09:00:00',NULL,0,0,'https://picsum.photos/seed/post47/800/600'),(48,5,'Hôm nay trời đẹp quá, mọi người có kế hoạch gì không?','2025-05-02 10:30:00',NULL,0,0,NULL),(49,6,'Chia sẻ một số mẹo học lập trình hiệu quả mà tôi thấy rất hữu ích!','2025-05-03 11:00:00',NULL,0,0,'https://picsum.photos/seed/post49/800/600'),(50,7,'Hôm nay tôi vừa hoàn thành dự án cá nhân, cảm giác rất tuyệt vời!','2025-05-04 12:45:00',NULL,0,0,NULL),(51,8,'Có ai muốn chia sẻ các bộ phim yêu thích không? Tôi cần gợi ý mới!','2025-05-05 14:20:00','2025-05-05 15:00:00',0,0,'https://picsum.photos/seed/post51/800/600'),(52,9,'Hãy sống tích cực và yêu đời hơn mỗi ngày, bạn nhé!','2025-05-06 16:00:00',NULL,0,0,NULL),(53,10,'Đây là hình ảnh chuyến du lịch cuối tuần của tôi. Rất vui và thú vị!','2025-05-07 18:10:00',NULL,0,0,'https://picsum.photos/seed/post53/800/600'),(54,11,'Tôi vừa học xong khóa học Python cơ bản. Cảm giác thật tự hào!','2025-05-08 19:00:00',NULL,1,0,NULL),(55,12,'Chào cả nhà, đây là bài viết mới nhất của tôi!','2025-05-09 20:15:00',NULL,0,0,'https://picsum.photos/seed/post55/800/600'),(56,13,'Có ai biết cách giải bài toán này không? Tôi đang gặp khó khăn.','2025-05-10 21:30:00',NULL,0,0,NULL);
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
) ENGINE=InnoDB AUTO_INCREMENT=147 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reactions`
--

LOCK TABLES `reactions` WRITE;
/*!40000 ALTER TABLE `reactions` DISABLE KEYS */;
INSERT INTO `reactions` VALUES (127,47,5,'like','2025-05-01 10:05:00',NULL),(128,48,6,'heart','2025-05-02 11:10:00',NULL),(129,49,7,'haha','2025-05-03 12:15:00',NULL),(130,50,8,'like','2025-05-04 13:20:00',NULL),(131,51,9,'heart','2025-05-05 15:35:00',NULL),(132,NULL,10,'like','2025-05-06 17:05:00',44),(133,NULL,11,'heart','2025-05-07 19:10:00',45),(134,NULL,12,'haha','2025-05-08 20:10:00',46),(135,NULL,13,'like','2025-05-09 21:15:00',47),(136,56,4,'haha','2025-05-10 22:15:00',NULL),(137,56,17,'haha','2025-05-30 23:18:55',NULL),(138,53,17,'like','2025-05-30 23:19:02',45),(139,52,17,'haha','2025-05-30 23:19:07',44),(142,51,17,'heart','2025-05-30 23:19:17',NULL),(143,50,17,'heart','2025-05-30 23:19:19',NULL),(144,49,17,'haha','2025-05-30 23:19:20',NULL),(145,48,17,'like','2025-05-30 23:19:23',NULL),(146,47,17,'like','2025-05-30 23:19:25',NULL);
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
) ENGINE=InnoDB AUTO_INCREMENT=71 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `survey_options`
--

LOCK TABLES `survey_options` WRITE;
/*!40000 ALTER TABLE `survey_options` DISABLE KEYS */;
INSERT INTO `survey_options` VALUES (60,30,'Rất tốt'),(61,30,'Tốt'),(62,30,'Trung bình'),(63,30,'Kém'),(64,32,'Bình luận'),(65,32,'Đăng bài'),(66,32,'Tương tác'),(67,32,'Khác');
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
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `survey_questions`
--

LOCK TABLES `survey_questions` WRITE;
/*!40000 ALTER TABLE `survey_questions` DISABLE KEYS */;
INSERT INTO `survey_questions` VALUES (30,15,'Bạn đánh giá trải nghiệm người dùng trên nền tảng thế nào?',1,1,1),(31,15,'Hãy cho chúng tôi biết ý kiến chi tiết của bạn về nền tảng.',0,2,2),(32,16,'Bạn thường sử dụng tính năng nào nhất?',1,1,1),(33,16,'Bạn mong muốn cải thiện tính năng nào?',0,2,2);
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
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `survey_responses`
--

LOCK TABLES `survey_responses` WRITE;
/*!40000 ALTER TABLE `survey_responses` DISABLE KEYS */;
INSERT INTO `survey_responses` VALUES (35,15,4,30,60,'2025-05-01 09:00:00',NULL),(36,15,4,31,NULL,'2025-05-01 09:05:00','Nền tảng rất dễ sử dụng và giao diện thân thiện.'),(37,16,5,32,64,'2025-05-02 10:00:00',NULL),(38,16,5,33,NULL,'2025-05-02 10:10:00','Mong muốn cải thiện tính năng tìm kiếm bài viết.');
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
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `surveys`
--

LOCK TABLES `surveys` WRITE;
/*!40000 ALTER TABLE `surveys` DISABLE KEYS */;
INSERT INTO `surveys` VALUES (15,1,47,'Khảo sát về trải nghiệm người dùng','Chúng tôi muốn biết cảm nhận của bạn về nền tảng này.','2025-05-01 08:00:00',1),(16,1,48,'Khảo sát về nội dung bài viết','Bạn đánh giá thế nào về chất lượng bài viết trên trang?','2025-05-02 09:30:00',1),(17,1,NULL,'Khảo sát về tính năng mới','Ý kiến của bạn về tính năng bình luận có nên bổ sung?','2025-05-03 10:00:00',1),(18,1,49,'Khảo sát về hình ảnh minh họa','Bạn thấy hình ảnh minh họa có làm bài viết hấp dẫn hơn không?','2025-05-04 11:00:00',0),(19,1,NULL,'Khảo sát về thời gian sử dụng','Bạn thường sử dụng nền tảng vào thời gian nào trong ngày?','2025-05-05 12:00:00',1),(20,1,50,'Khảo sát về tương tác bài viết','Bạn thường tương tác với loại bài viết nào nhất?','2025-05-06 13:00:00',1),(21,1,NULL,'Khảo sát về giao diện người dùng','Bạn có hài lòng với giao diện hiện tại không?','2025-05-07 14:00:00',1),(22,1,51,'Khảo sát về chế độ bình luận','Bạn thích bình luận mở hay đóng hơn?','2025-05-08 15:00:00',1),(23,1,NULL,'Khảo sát về tốc độ tải trang','Bạn có gặp vấn đề về tốc độ tải trang không?','2025-05-09 16:00:00',0),(24,1,52,'Khảo sát về sự kiện sắp tới','Bạn có quan tâm tham gia các sự kiện do nền tảng tổ chức không?','2025-05-10 17:00:00',1);
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
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_groups`
--

LOCK TABLES `user_groups` WRITE;
/*!40000 ALTER TABLE `user_groups` DISABLE KEYS */;
INSERT INTO `user_groups` VALUES (1,1,'Nhóm Quản trị viên','2025-05-01 08:00:00'),(2,1,'Nhóm Học viên CNTT','2025-05-02 09:00:00'),(3,1,'Nhóm Lập trình viên Python','2025-05-03 10:00:00'),(4,1,'Nhóm Đam mê Du lịch','2025-05-04 11:00:00'),(5,1,'Nhóm Yêu thích Phim ảnh','2025-05-05 12:00:00'),(6,1,'Nhóm Sáng tạo Nội dung','2025-05-06 13:00:00');
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
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin',NULL,'admin@example.com','$2a$12$wvedWt4H9ewpsbvozggktuMSoOIz8D83LhnLlprHyUOmGEHXRzcEu','ROLE_ADMIN',NULL,NULL,'Admin Hehe',0,'2025-05-12 10:02:06',NULL,0),(4,'phamthienan','B180004','thienan@gmail.com','$2a$12$wvedWt4H9ewpsbvozggktuMSoOIz8D83LhnLlprHyUOmGEHXRzcEu','ROLE_ALUMNI','/images/avatar4.jpg','/images/cover4.jpg','Phạm Thiên Ân',1,'2023-01-04 15:00:00','2023-03-01 18:00:00',0),(5,'dangquochuy','B180005','quochuy@gmail.com','$2a$12$wvedWt4H9ewpsbvozggktuMSoOIz8D83LhnLlprHyUOmGEHXRzcEu','ROLE_LECTURER','/images/avatar5.jpg','/images/cover5.jpg','Đặng Quốc Huy',0,'2023-01-05 16:00:00',NULL,0),(6,'nguyenthiminh','B180006','minhnguyen@gmail.com','$2a$12$wvedWt4H9ewpsbvozggktuMSoOIz8D83LhnLlprHyUOmGEHXRzcEu','ROLE_ALUMNI','/images/avatar6.jpg','/images/cover6.jpg','Nguyễn Thị Minh',1,'2023-01-06 17:00:00',NULL,0),(7,'phamvanhieu','B180007','vanhieu@gmail.com','$2a$12$wvedWt4H9ewpsbvozggktuMSoOIz8D83LhnLlprHyUOmGEHXRzcEu','ROLE_LECTURER','/images/avatar7.jpg','/images/cover7.jpg','Phạm Văn Hiếu',0,'2023-01-07 18:00:00',NULL,0),(8,'tranthanhdat','B180008','thanhdat@gmail.com','$2a$12$wvedWt4H9ewpsbvozggktuMSoOIz8D83LhnLlprHyUOmGEHXRzcEu','ROLE_ALUMNI','/images/avatar8.jpg','/images/cover8.jpg','Trần Thành Đạt',1,'2023-01-08 19:00:00','2023-03-15 20:00:00',0),(9,'lethibichngoc','B180009','bichngoc@gmail.com','$2a$12$wvedWt4H9ewpsbvozggktuMSoOIz8D83LhnLlprHyUOmGEHXRzcEu','ROLE_ALUMNI','/images/avatar9.jpg','/images/cover9.jpg','Lê Thị Bích Ngọc',1,'2023-01-09 20:00:00',NULL,0),(10,'ngothienbao','B180010','thienbao@gmail.com','$2a$12$wvedWt4H9ewpsbvozggktuMSoOIz8D83LhnLlprHyUOmGEHXRzcEu','ROLE_LECTURER','/images/avatar10.jpg','/images/cover10.jpg','Ngô Thiên Bảo',0,'2023-01-10 21:00:00',NULL,0),(11,'vuquocviet','B180011','quocviet@gmail.com','$2a$12$wvedWt4H9ewpsbvozggktuMSoOIz8D83LhnLlprHyUOmGEHXRzcEu','ROLE_LECTURER','/images/avatar11.jpg','/images/cover11.jpg','Vũ Quốc Việt',1,'2023-01-11 22:00:00',NULL,0),(12,'ngoclanhuong','B180012','lanhuong@gmail.com','$2a$12$wvedWt4H9ewpsbvozggktuMSoOIz8D83LhnLlprHyUOmGEHXRzcEu','ROLE_ALUMNI','/images/avatar12.jpg','/images/cover12.jpg','Ngọc Lan Hương',1,'2023-01-12 23:00:00',NULL,0),(13,'doantrungdung','B180013','trungdung@gmail.com','$2a$12$wvedWt4H9ewpsbvozggktuMSoOIz8D83LhnLlprHyUOmGEHXRzcEu','ROLE_ALUMNI','/images/avatar13.jpg','/images/cover13.jpg','Đoàn Trung Dũng',0,'2023-01-13 10:00:00',NULL,0),(17,'lecturer',NULL,'thanhth41ng@gmail.com','$2a$10$TjRbNfK5bkjx/FbaCejGXe9ET1lfZPQUFeGC1/qb.Lq8qT6NOtsVW','ROLE_LECTURER','https://res.cloudinary.com/dxxwcby8l/image/upload/v1748619205/mqtgivk8khotlugqvhga.jpg','https://res.cloudinary.com/dxxwcby8l/image/upload/v1748619206/dexs76j6v0qpcqrlvsos.jpg','Thanh Thắng',0,'2025-05-23 22:33:23','2025-05-25 22:37:53',0),(18,'alumni','2251010016','nguyenlethanhthang@gmail.com','$2a$10$x/UeHKT7d/Li/H/3h2y0Eurei4JLddsc7oIasNPAGobJjwVzpeeTW','ROLE_ALUMNI','https://res.cloudinary.com/dxxwcby8l/image/upload/v1748619290/xwpvvopl9ujwpxjmyffr.jpg','https://res.cloudinary.com/dxxwcby8l/image/upload/v1748619291/ckiax1zpluvdicyxwd5w.jpg','Hoàng Danh',1,'2025-05-30 22:34:51',NULL,0);
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

-- Dump completed on 2025-05-31  0:02:46
