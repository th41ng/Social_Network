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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (1,'Trang chủ','Về trang chủ'),(2,'Posts','Các bài viết'),(3,'Events','Sự kiện'),(4,'Surveys','Khảo sát'),(5,'Users','Người dùng');
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comments`
--

LOCK TABLES `comments` WRITE;
/*!40000 ALTER TABLE `comments` DISABLE KEYS */;
INSERT INTO `comments` VALUES (1,1,2,'Chào mừng bạn đến với mạng xã hội!','2025-01-10 10:05:00','2025-01-10 10:05:00',0),(2,1,3,'Rất vui được gặp bạn!','2025-01-10 10:10:00','2025-01-10 10:10:00',0),(3,2,1,'Cảm ơn bạn, ngày đẹp thật!','2025-01-11 14:05:00','2025-01-11 14:05:00',0),(4,4,5,'Tôi sẽ tham gia khảo sát ngay!','2025-01-13 15:10:00','2025-01-13 15:10:00',0),(5,2,4,'Comment này đã bị xóa.','2025-01-11 14:15:00','2025-01-11 14:15:00',1);
/*!40000 ALTER TABLE `comments` ENABLE KEYS */;
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
INSERT INTO `event_notifications` VALUES (1,4,'Mời tham gia hội thảo',1,1,NULL,'Bạn được mời tham gia hội thảo cựu sinh viên.','2025-01-25 10:00:00'),(2,4,'Thông báo ngày hội việc làm',2,NULL,2,'Nhóm CNTT được mời tham gia ngày hội việc làm.','2025-01-26 14:00:00'),(3,4,'Lễ kỷ niệm 20 năm',3,NULL,1,'Nhóm K2018 được mời tham gia lễ kỷ niệm.','2025-01-27 09:00:00'),(4,4,'Mời tham gia workshop',4,5,NULL,'Bạn được mời tham gia workshop kỹ năng.','2025-01-28 11:00:00'),(5,4,'Gala âm nhạc',5,NULL,5,'Nhóm yêu âm nhạc được mời tham gia gala.','2025-01-29 15:00:00');
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
INSERT INTO `events` VALUES (1,4,'Hội thảo cựu sinh viên','Gặp gỡ và chia sẻ kinh nghiệm.','2025-02-01 09:00:00','2025-02-01 12:00:00','Hội trường A'),(2,4,'Ngày hội việc làm','Tuyển dụng từ các công ty lớn.','2025-02-05 08:00:00','2025-02-05 17:00:00','Sân trường'),(3,4,'Lễ kỷ niệm 20 năm','Kỷ niệm thành lập trường.','2025-03-10 18:00:00','2025-03-10 21:00:00','Hội trường B'),(4,4,'Workshop kỹ năng','Học kỹ năng mềm từ chuyên gia.','2025-02-15 13:00:00','2025-02-15 16:00:00','Phòng học C1'),(5,4,'Gala âm nhạc','Buổi biểu diễn của cựu sinh viên.','2025-02-20 19:00:00','2025-02-20 22:00:00','Sân khấu ngoài trời');
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
  PRIMARY KEY (`post_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `posts_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `posts`
--

LOCK TABLES `posts` WRITE;
/*!40000 ALTER TABLE `posts` DISABLE KEYS */;
INSERT INTO `posts` VALUES (1,1,'Chào mọi người, tôi vừa tham gia mạng xã hội này!','2025-01-10 10:00:00','2025-01-10 10:00:00',0,0),(2,2,'Hôm nay là một ngày đẹp trời!','2025-01-11 14:00:00','2025-01-11 14:00:00',0,0),(3,3,'Thông báo lớp học bồi dưỡng sắp tới.','2025-01-12 09:00:00','2025-01-12 09:00:00',1,0),(4,4,'Khảo sát về chất lượng đào tạo, mọi người tham gia nhé!','2025-01-13 15:00:00','2025-01-13 15:00:00',0,0),(5,1,'Bài viết này đã bị xóa.','2025-01-14 12:00:00','2025-05-05 16:23:39',0,0),(6,1,'hahaa','2025-05-05 15:32:33','2025-05-05 22:33:53',0,0),(7,2,'hihi','2025-05-05 15:32:33','2025-05-05 22:33:53',0,0);
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
  UNIQUE KEY `post_id` (`post_id`,`user_id`),
  KEY `user_id` (`user_id`),
  KEY `fk_reactions_comment` (`comment_id`),
  CONSTRAINT `fk_reactions_comment` FOREIGN KEY (`comment_id`) REFERENCES `comments` (`comment_id`),
  CONSTRAINT `reactions_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `posts` (`post_id`) ON DELETE CASCADE,
  CONSTRAINT `reactions_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reactions`
--

LOCK TABLES `reactions` WRITE;
/*!40000 ALTER TABLE `reactions` DISABLE KEYS */;
INSERT INTO `reactions` VALUES (1,1,2,'like','2025-01-10 10:06:00',NULL),(2,1,3,'heart','2025-01-10 10:11:00',NULL),(3,2,1,'haha','2025-01-11 14:06:00',NULL),(4,4,5,'like','2025-01-13 15:11:00',NULL),(5,3,4,'heart','2025-01-12 09:05:00',NULL);
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
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `survey_options`
--

LOCK TABLES `survey_options` WRITE;
/*!40000 ALTER TABLE `survey_options` DISABLE KEYS */;
INSERT INTO `survey_options` VALUES (1,1,'Rất hài lòng'),(2,1,'Hài lòng'),(3,1,'Bình thường'),(4,1,'Không hài lòng'),(5,1,'Rất không hài lòng'),(6,2,'Rất phù hợp và dễ hiểu'),(7,2,'Phù hợp'),(8,2,'Tạm được, cần cải thiện'),(9,2,'Khó hiểu'),(10,4,'Python'),(11,4,'JavaScript (Node.js, React)'),(12,4,'Java'),(13,4,'Go'),(14,4,'Rust'),(15,4,'Kotlin/Swift (Mobile)'),(16,6,'Rất thường xuyên'),(17,6,'Thường xuyên'),(18,6,'Thỉnh thoảng'),(19,6,'Hiếm khi'),(20,6,'Không bao giờ'),(21,7,'Rất đa dạng, đầy đủ'),(22,7,'Khá đa dạng'),(23,7,'Bình thường, đủ dùng'),(24,7,'Còn thiếu nhiều sách mới'),(25,7,'Rất thiếu'),(26,8,'Rất tốt, yên tĩnh và thoải mái'),(27,8,'Tốt, đáp ứng nhu cầu'),(28,8,'Tạm ổn, đôi khi hơi ồn'),(29,8,'Cần cải thiện nhiều về không gian');
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
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `survey_questions`
--

LOCK TABLES `survey_questions` WRITE;
/*!40000 ALTER TABLE `survey_questions` DISABLE KEYS */;
INSERT INTO `survey_questions` VALUES (1,1,'Mức độ hài lòng của bạn về nội dung môn học trong học kỳ qua?',1,1,1),(2,1,'Phương pháp giảng dạy của giảng viên có phù hợp và dễ hiểu không?',1,2,1),(3,1,'Bạn có góp ý cụ thể nào để cải thiện chất lượng giảng dạy không?',0,3,2),(4,2,'Theo bạn, ngôn ngữ lập trình nào sẽ có nhu cầu cao trong 2 năm tới?',1,1,1),(5,2,'Bạn mong muốn nhà trường tổ chức thêm các workshop về chủ đề công nghệ nào?',1,2,2),(6,2,'Bạn có thường xuyên sử dụng các nguồn tài liệu trực tuyến để tự học không?',0,3,1),(7,3,'Bạn đánh giá thế nào về sự đa dạng của các đầu sách chuyên ngành tại thư viện?',1,1,1),(8,3,'Không gian học tập tại thư viện có yên tĩnh và thoải mái không?',1,2,1),(9,3,'Bạn có đề xuất gì để thư viện cải thiện nguồn tài liệu số (e-books, database online) không?',0,3,2),(10,2,'fsdf',0,1,2),(11,2,'alo',0,1,2);
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
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `survey_responses`
--

LOCK TABLES `survey_responses` WRITE;
/*!40000 ALTER TABLE `survey_responses` DISABLE KEYS */;
INSERT INTO `survey_responses` VALUES (1,1,1,1,1,'2025-05-11 11:22:31',NULL),(2,1,1,2,6,'2025-05-11 11:22:31',NULL),(3,1,1,3,NULL,'2025-05-11 11:22:31','Cần thêm nhiều bài tập tình huống thực tế hơn trong các môn chuyên ngành.'),(4,1,2,1,2,'2025-05-10 11:22:31',NULL),(5,1,2,2,7,'2025-05-10 11:22:31',NULL),(6,1,2,3,NULL,'2025-05-10 11:22:31','Thời lượng thực hành cho một số môn nên được tăng lên.'),(7,2,3,4,10,'2025-05-11 11:22:31',NULL),(8,2,3,5,NULL,'2025-05-11 11:22:31','Workshop về AI và Machine Learning, cũng như Cloud Computing (AWS/Azure).'),(9,2,3,6,16,'2025-05-11 11:22:31',NULL),(10,3,5,7,22,'2025-05-09 11:22:31',NULL),(11,3,5,8,27,'2025-05-09 11:22:31',NULL),(12,3,5,9,NULL,'2025-05-09 11:22:31','Nên có thêm nhiều máy tính cấu hình mạnh hơn ở thư viện và bổ sung các phần mềm chuyên dụng cho sinh viên ngành kỹ thuật.'),(13,2,1,4,11,'2025-05-11 08:22:31',NULL),(14,2,1,5,NULL,'2025-05-11 08:22:31','Các buổi chia sẻ từ cựu sinh viên thành đạt về kinh nghiệm làm việc thực tế.');
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
  PRIMARY KEY (`survey_id`),
  KEY `admin_id` (`admin_id`),
  KEY `post_id` (`post_id`),
  CONSTRAINT `surveys_ibfk_1` FOREIGN KEY (`admin_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `surveys_ibfk_2` FOREIGN KEY (`post_id`) REFERENCES `posts` (`post_id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `surveys`
--

LOCK TABLES `surveys` WRITE;
/*!40000 ALTER TABLE `surveys` DISABLE KEYS */;
INSERT INTO `surveys` VALUES (1,4,NULL,'Đánh giá chất lượng đào tạo HK1 2024-2025','Vui lòng chia sẻ ý kiến của bạn để nhà trường cải thiện chất lượng đào tạo trong học kỳ vừa qua.','2025-05-11 11:22:31'),(2,4,NULL,'Khảo sát nhu cầu kỹ năng CNTT năm 2025','Chúng tôi muốn tìm hiểu về các kỹ năng công nghệ thông tin quan trọng mà thị trường lao động đang yêu cầu.','2025-05-01 11:22:31'),(3,4,NULL,'Đánh giá dịch vụ thư viện và nguồn tài liệu học tập','Ý kiến của bạn giúp thư viện nâng cao chất lượng phục vụ và bổ sung tài liệu phù hợp.','2025-04-21 11:22:31'),(4,4,NULL,'teéest','a','2025-05-11 12:05:26');
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
  `student_id` varchar(20) DEFAULT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('alumni','lecturer','admin') NOT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  `cover_image` varchar(255) DEFAULT NULL,
  `full_name` varchar(100) NOT NULL,
  `is_verified` tinyint DEFAULT '0',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `last_password_change` datetime DEFAULT NULL,
  `is_locked` tinyint DEFAULT '0',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `student_id` (`student_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'SV001','nguyen.van.a@example.com','hashed_password1','alumni','/avatars/nguyen.jpg','/covers/nguyen_cover.jpg','Nguyen Van A',1,'2025-01-01 10:00:00','2025-01-02 12:00:00',0),(2,'SV002','tran.thi.b@example.com','hashed_password2','alumni','/avatars/tran.jpg','/covers/tran_cover.jpg','Tran Thi B',1,'2025-01-02 15:00:00','2025-01-03 14:00:00',0),(3,NULL,'le.van.c@example.com','hashed_password3','lecturer','/avatars/le.jpg','/covers/le_cover.jpg','Le Van C',1,'2025-01-03 09:00:00','2025-01-04 10:00:00',0),(4,NULL,'admin1@example.com','hashed_password4','admin','/avatars/admin1.jpg','/covers/admin1_cover.jpg','Admin One',1,'2025-01-01 08:00:00','2025-01-01 09:00:00',0),(5,'SV003','pham.thi.d@example.com','hashed_password5','alumni','/avatars/pham.jpg','/covers/pham_cover.jpg','Pham Thi D',0,'2025-01-05 11:00:00',NULL,0);
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

-- Dump completed on 2025-05-11 14:16:26
