-- MySQL dump 10.13  Distrib 8.0.38, for Win64 (x86_64)
--
-- Host: localhost    Database: timetabling
-- ------------------------------------------------------
-- Server version	9.0.1

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
-- Table structure for table `class_week`
--

DROP TABLE IF EXISTS `class_week`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `class_week` (
  `class_week` varchar(255) NOT NULL,
  `weeks` int NOT NULL,
  KEY `FKfcrb86ohysme4uvwceypth42d` (`class_week`),
  CONSTRAINT `FKfcrb86ohysme4uvwceypth42d` FOREIGN KEY (`class_week`) REFERENCES `timetable_item` (`id_class`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `class_week`
--

LOCK TABLES `class_week` WRITE;
/*!40000 ALTER TABLE `class_week` DISABLE KEYS */;
/*!40000 ALTER TABLE `class_week` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `generate_id`
--

DROP TABLE IF EXISTS `generate_id`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `generate_id` (
  `id` int NOT NULL AUTO_INCREMENT,
  `pre` varchar(2) NOT NULL,
  `institute` varchar(255) NOT NULL,
  `department` varchar(255) NOT NULL,
  `institute_code` varchar(3) NOT NULL,
  `department_code` varchar(3) NOT NULL,
  `count` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `generate_id`
--

LOCK TABLES `generate_id` WRITE;
/*!40000 ALTER TABLE `generate_id` DISABLE KEYS */;
INSERT INTO `generate_id` VALUES (1,'CB','Trường Công nghệ Thông tin và Truyền thông','Khoa Khoa học Máy tính','001','001',2),(2,'CB','Trường Công nghệ Thông tin và Truyền thông','Khoa Kỹ thuật Máy tính','001','002',1),(3,'CB','Trường Công nghệ Thông tin và Truyền thông','Trung tâm Hỗ trợ nghiên cứu, phát triển và chuyển giao công nghệ','001','003',0),(4,'CB','Trường Công nghệ Thông tin và Truyền thông','Văn phòng Trường Công nghệ Thông tin và Truyền thông','001','004',0),(5,'CB','Trường Công nghệ Thông tin và Truyền thông','Trung tâm Máy tính và Thực hành','001','005',0),(6,'CB','Trường Vật liệu','Khoa Vật liệu Điện tử và linh kiện','002','001',0),(7,'CB','Khoa Khoa học và Công nghệ giáo dục','Công đoàn Khoa Khoa học và Công nghệ giáo dục','003','001',0);
/*!40000 ALTER TABLE `generate_id` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `room`
--

DROP TABLE IF EXISTS `room`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `room` (
  `name` varchar(255) NOT NULL,
  `sl_max` int NOT NULL,
  `type` enum('DA','GD','TN','TT') NOT NULL,
  PRIMARY KEY (`name`),
  CONSTRAINT `room_chk_1` CHECK ((`type` between 0 and 3))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `room`
--

LOCK TABLES `room` WRITE;
/*!40000 ALTER TABLE `room` DISABLE KEYS */;
/*!40000 ALTER TABLE `room` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `room_subject`
--

DROP TABLE IF EXISTS `room_subject`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `room_subject` (
  `name_room` varchar(255) NOT NULL,
  `subject_codes` varchar(255) NOT NULL,
  KEY `FKqywwxwoj9lnfqjpetcbhk0yg2` (`name_room`),
  CONSTRAINT `FKqywwxwoj9lnfqjpetcbhk0yg2` FOREIGN KEY (`name_room`) REFERENCES `room` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `room_subject`
--

LOCK TABLES `room_subject` WRITE;
/*!40000 ALTER TABLE `room_subject` DISABLE KEYS */;
/*!40000 ALTER TABLE `room_subject` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subjects`
--

DROP TABLE IF EXISTS `subjects`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subjects` (
  `subject_code` varchar(255) NOT NULL,
  `bt` int NOT NULL,
  `count` int NOT NULL,
  `lt` int NOT NULL,
  `name_english_subject` varchar(255) NOT NULL,
  `name_subject` varchar(255) NOT NULL,
  `requesttn` bit(1) NOT NULL,
  `tn` int NOT NULL,
  `tuhoc` int NOT NULL,
  PRIMARY KEY (`subject_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subjects`
--

LOCK TABLES `subjects` WRITE;
/*!40000 ALTER TABLE `subjects` DISABLE KEYS */;
INSERT INTO `subjects` VALUES ('BF2020',2,3,2,'Technical Writing and Presentation','Technical Writing and Presentation',_binary '\0',0,0),('BF2601',1,3,2,'Biomedical Biochemistry','Hóa sinh y sinh',_binary '',1,0);
/*!40000 ALTER TABLE `subjects` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teacher_subject`
--

DROP TABLE IF EXISTS `teacher_subject`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teacher_subject` (
  `teacher_code` varchar(255) NOT NULL,
  `subject_codes` varchar(255) NOT NULL,
  KEY `FKf36bemjk4mpksm89oi3tpob9j` (`teacher_code`),
  CONSTRAINT `FKf36bemjk4mpksm89oi3tpob9j` FOREIGN KEY (`teacher_code`) REFERENCES `teachers` (`teacher_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teacher_subject`
--

LOCK TABLES `teacher_subject` WRITE;
/*!40000 ALTER TABLE `teacher_subject` DISABLE KEYS */;
INSERT INTO `teacher_subject` VALUES ('CB.001.001.001','IT3020'),('CB.001.001.001','IT3070'),('CB.001.001.001','IT1110'),('CB.001.002.001','IT3080'),('CB.001.002.001','IT1016'),('CB.001.002.001','IT2120'),('CB.001.001.002','IT3040'),('CB.001.001.002','IT5433'),('CB.001.001.002','IT3180E');
/*!40000 ALTER TABLE `teacher_subject` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teacher_type`
--

DROP TABLE IF EXISTS `teacher_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teacher_type` (
  `teacher_code` varchar(255) NOT NULL,
  `type` enum('DA','GD','TN','TT') NOT NULL,
  KEY `FKcjkd5uxm5rc8qk1ho0f280y27` (`teacher_code`),
  CONSTRAINT `FKcjkd5uxm5rc8qk1ho0f280y27` FOREIGN KEY (`teacher_code`) REFERENCES `teachers` (`teacher_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teacher_type`
--

LOCK TABLES `teacher_type` WRITE;
/*!40000 ALTER TABLE `teacher_type` DISABLE KEYS */;
INSERT INTO `teacher_type` VALUES ('CB.001.001.001','GD'),('CB.001.002.001','GD'),('CB.001.001.002','GD');
/*!40000 ALTER TABLE `teacher_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teachers`
--

DROP TABLE IF EXISTS `teachers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teachers` (
  `teacher_code` varchar(255) NOT NULL,
  `hoc_vi` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `priority_gd` int NOT NULL,
  `priority_tn` int NOT NULL,
  `time` int NOT NULL,
  `department` varchar(255) NOT NULL,
  `institute` varchar(255) NOT NULL,
  PRIMARY KEY (`teacher_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teachers`
--

LOCK TABLES `teachers` WRITE;
/*!40000 ALTER TABLE `teachers` DISABLE KEYS */;
INSERT INTO `teachers` VALUES ('CB.001.001.001','PGS','Ban Hà Bằng',1,0,40,'Khoa Khoa học Máy tính','Trường Công nghệ Thông tin và Truyền thông'),('CB.001.001.002','TS','Bùi Thị Mai Anh',3,1,42,'Khoa Khoa học Máy tính','Trường Công nghệ Thông tin và Truyền thông'),('CB.001.002.001','ThS','Bành Thị Quỳnh Mai',2,3,44,'Khoa Kỹ thuật Máy tính','Trường Công nghệ Thông tin và Truyền thông');
/*!40000 ALTER TABLE `teachers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `timetable_item`
--

DROP TABLE IF EXISTS `timetable_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `timetable_item` (
  `id_class` varchar(255) NOT NULL,
  `day` int NOT NULL,
  `end` int NOT NULL,
  `id_subject` varchar(255) NOT NULL,
  `management_code` varchar(255) NOT NULL,
  `name_english_subject` varchar(255) NOT NULL,
  `name_subject` varchar(255) NOT NULL,
  `requettn` bit(1) NOT NULL,
  `room` varchar(255) NOT NULL,
  `session` varchar(255) NOT NULL,
  `sl_max` int NOT NULL,
  `start` int NOT NULL,
  `teacher_code` varchar(255) NOT NULL,
  `teacher_name` varchar(255) NOT NULL,
  `teaching_type` varchar(255) NOT NULL,
  `term` int NOT NULL,
  `timeend` int NOT NULL,
  `time_start` int NOT NULL,
  `type` tinyint NOT NULL,
  `weight` varchar(255) NOT NULL,
  PRIMARY KEY (`id_class`),
  CONSTRAINT `timetable_item_chk_1` CHECK ((`type` between 0 and 3))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `timetable_item`
--

LOCK TABLES `timetable_item` WRITE;
/*!40000 ALTER TABLE `timetable_item` DISABLE KEYS */;
/*!40000 ALTER TABLE `timetable_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tokens`
--

DROP TABLE IF EXISTS `tokens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tokens` (
  `id` binary(16) NOT NULL,
  `expired` bit(1) NOT NULL,
  `revoked` bit(1) NOT NULL,
  `token` varchar(500) NOT NULL,
  `token_type` enum('ACCESS','REFRESH') DEFAULT NULL,
  `user_id` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKna3v9f8s7ucnj16tylrs822qj` (`token`),
  KEY `FK2dylsfo39lgjyqml2tbe0b0ss` (`user_id`),
  CONSTRAINT `FK2dylsfo39lgjyqml2tbe0b0ss` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tokens`
--

LOCK TABLES `tokens` WRITE;
/*!40000 ALTER TABLE `tokens` DISABLE KEYS */;
INSERT INTO `tokens` VALUES (_binary '�g\�\��Kn�ũ\�\�^h\�',_binary '\0',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiYW5oYWJhbmdAZXhhbXBsZS5jb20iLCJyb2xlcyI6IltST0xFX1BEVF0iLCJ0ZWFjaGVyX2NvZGUiOiJDQi4wMDEuMDAxLjAwMSIsImlhdCI6MTc0ODg3MTYzMCwiZXhwIjoxNzQ5NDc2NDMwfQ.2aV7hg-pSA6pWhGelXLNYH_VaC9T4fn5SrAqs-DjkyUqHP-bkSZ6kTqZz2WNsMln4G3QqJE1qRh73EuVkobC7Q','REFRESH',_binary '𹙛\�pJЙ�\�&x2�'),(_binary '	\r\�\0\�eBt\�1��\"\�',_binary '\0',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiYW5oYWJhbmdAZXhhbXBsZS5jb20iLCJyb2xlcyI6IltST0xFX1BEVF0iLCJ0ZWFjaGVyX2NvZGUiOiJDQi4wMDEuMDAxLjAwMSIsImlhdCI6MTc0ODg3OTE1OCwiZXhwIjoxNzQ5NDgzOTU4fQ.ckMCY2Z10BCHQpRv0j921G2igKn_uN35OuPUwVM_q2w2zQ_J_QaKtyD0tboIBUi1FiT-1zYOcEi0lCgNlppJBA','REFRESH',_binary '𹙛\�pJЙ�\�&x2�'),(_binary '\�|\'II���gi�D',_binary '\0',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiYW5oYWJhbmdAZXhhbXBsZS5jb20iLCJyb2xlcyI6IltST0xFX1BEVF0iLCJ0ZWFjaGVyX2NvZGUiOiJDQi4wMDEuMDAxLjAwMSIsImlhdCI6MTc0ODg3NjIzNCwiZXhwIjoxNzQ5NDgxMDM0fQ.To3djB9lvXg_VKqegX9r_aXzjog-8S8ORbazP5VL1JrF7xf_Ul6XNp2geCHPiiZPaR8FnvE1xfGNmZldQ5_kag','REFRESH',_binary '𹙛\�pJЙ�\�&x2�'),(_binary '+s�\�\��Na�Oc�a\�!',_binary '\0',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiYW5odGhpcXV5bmhtYWlAZXhhbXBsZS5jb20iLCJyb2xlcyI6IltST0xFX1RFQUNIRVJdIiwidGVhY2hlcl9jb2RlIjoiQ0IuMDAxLjAwMi4wMDEiLCJpYXQiOjE3NDg4NzUwMTgsImV4cCI6MTc0OTQ3OTgxOH0.pvWPEhe708vJ9MsEnAGTsOYq0Rap1ngqKwuqeIoVoXPSMqRu7Xbas_6p1ZmQAIksJsCavLWg92qxjTygVGZfbg','REFRESH',_binary '�{t�W�N\'�9�\�\��'),(_binary '3����@�~\�b��g\�',_binary '\0',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiYW5oYWJhbmdAZXhhbXBsZS5jb20iLCJyb2xlcyI6IltST0xFX1BEVF0iLCJ0ZWFjaGVyX2NvZGUiOiJDQi4wMDEuMDAxLjAwMSIsImlhdCI6MTc0ODg3NDY5MCwiZXhwIjoxNzQ5NDc5NDkwfQ.7JB1pET8A3VDaVgTgGV6jiWCA-82FAelHzxQnwKFRdqw4DfUES8FvIvI8-f0VGTFCwSLp8tMeC5t2cxKe-RNug','REFRESH',_binary '𹙛\�pJЙ�\�&x2�'),(_binary '>La\�6I��t�~\��',_binary '\0',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiYW5oYWJhbmdAZXhhbXBsZS5jb20iLCJyb2xlcyI6IltST0xFX1BEVF0iLCJ0ZWFjaGVyX2NvZGUiOiJDQi4wMDEuMDAxLjAwMSIsImlhdCI6MTc0ODg3NjM0NCwiZXhwIjoxNzQ5NDgxMTQ0fQ.4i0GkoXFqytaWR3DSsa9hyJmzFOtlxQ_vvC6olKDOcwDc_gev7QMCdQrLIDHM8cD4FhptOcc0JGX_QvUMMwvKQ','REFRESH',_binary '𹙛\�pJЙ�\�&x2�'),(_binary 'My\�\�\�H�٣�\�',_binary '\0',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiYW5oYWJhbmdAZXhhbXBsZS5jb20iLCJyb2xlcyI6IltST0xFX1BEVF0iLCJ0ZWFjaGVyX2NvZGUiOiJDQi4wMDEuMDAxLjAwMSIsImlhdCI6MTc0ODg3OTM3OCwiZXhwIjoxNzQ5NDg0MTc4fQ.XB-EtP7EVe20Vcoc9CTlmKEwPrKBJDRKR1kws7S-UZo95E53SPXR52CYJ9GpgZq8d60BnLe9tiWFurLX2bqoLA','REFRESH',_binary '𹙛\�pJЙ�\�&x2�'),(_binary 'T_?�\�\�J��z�9��-',_binary '\0',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiYW5oYWJhbmdAZXhhbXBsZS5jb20iLCJyb2xlcyI6IltST0xFX1RFQUNIRVJdIiwiaWF0IjoxNzQ4NzkyMDUzLCJleHAiOjE3NDkzOTY4NTN9.LqWceVNIEDErq0FhNzANS--M9nU4tXyCfpy8QkLlgK2asX7Y0FmTCh8UpOiGoglbDwUPUFRr7HZzQZgSuAACyg','REFRESH',_binary '𹙛\�pJЙ�\�&x2�'),(_binary 'U+\"b�K,�\rl�\�\�',_binary '\0',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiYW5oYWJhbmdAZXhhbXBsZS5jb20iLCJyb2xlcyI6IltST0xFX1BEVF0iLCJ0ZWFjaGVyX2NvZGUiOiJDQi4wMDEuMDAxLjAwMSIsImlhdCI6MTc0ODg3ODcyMiwiZXhwIjoxNzQ5NDgzNTIyfQ.phTCwVdgfEJTF9UD5vhUI4mnMGXjgOolkxaSedQ2s9PrBG7M3Mt-n2whHIlpQh5alyMAsPsYdhUWHgK6_rTC3g','REFRESH',_binary '𹙛\�pJЙ�\�&x2�'),(_binary '\\\�C\�yD�yĉ\�',_binary '\0',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiYW5oYWJhbmdAZXhhbXBsZS5jb20iLCJyb2xlcyI6IltST0xFX1BEVF0iLCJ0ZWFjaGVyX2NvZGUiOiJDQi4wMDEuMDAxLjAwMSIsImlhdCI6MTc0ODg3NDk1NywiZXhwIjoxNzQ5NDc5NzU3fQ._F4hrzRJG1oLO18tGBryw6m2R3wHVfUcYb0tSAqGIshYRrFCMOiPiQeSuY4lsxb7dZ5sZ-LYGq0Qjf3hvjaZjA','REFRESH',_binary '𹙛\�pJЙ�\�&x2�'),(_binary 'g�z�^�Lw�~\�\�|L#�',_binary '\0',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiYW5oYWJhbmdAZXhhbXBsZS5jb20iLCJyb2xlcyI6IltST0xFX1BEVF0iLCJ0ZWFjaGVyX2NvZGUiOiJDQi4wMDEuMDAxLjAwMSIsImlhdCI6MTc0ODg3ODIyMSwiZXhwIjoxNzQ5NDgzMDIxfQ.6O9QyacJK7jNvFpUkNcE2RSFEzCo_zWNbuMLz4reOyV3-aEMo0Wfv-DoAC9IiVnwxEmL_dRdtd4N4KierFf02A','REFRESH',_binary '𹙛\�pJЙ�\�&x2�'),(_binary 'y\�\�QznEǰo�Zt\�\�\�',_binary '\0',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiYW5oYWJhbmdAZXhhbXBsZS5jb20iLCJyb2xlcyI6IltST0xFX1BEVF0iLCJ0ZWFjaGVyX2NvZGUiOiJDQi4wMDEuMDAxLjAwMSIsImlhdCI6MTc0ODg3NTQwMSwiZXhwIjoxNzQ5NDgwMjAxfQ.YkGGwwlyBDait9OplB-2BPlIxjPlUZnxBC3pOHPr-o_0fsguZVWdIVbwRCMQ7rxv__T5POoGFj0rlZTl-IJ_LA','REFRESH',_binary '𹙛\�pJЙ�\�&x2�'),(_binary '�\�\�<�WL甡 \�\�U',_binary '\0',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiYW5oYWJhbmdAZXhhbXBsZS5jb20iLCJyb2xlcyI6IltST0xFX1RFQUNIRVJdIiwiaWF0IjoxNzQ4NzkyOTY5LCJleHAiOjE3NDkzOTc3Njl9.eq7V5l4KjZTv3oUHcrqNBM-kbjRcS079m4a81kcbXQPbghDKutVb1nysWXuR-wfgUCzwvJncrHdExhx0Wtkfpg','REFRESH',_binary '𹙛\�pJЙ�\�&x2�'),(_binary '��x�@\n��M',_binary '\0',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiYW5oYWJhbmdAZXhhbXBsZS5jb20iLCJyb2xlcyI6IltST0xFX1BEVF0iLCJ0ZWFjaGVyX2NvZGUiOiJDQi4wMDEuMDAxLjAwMSIsImlhdCI6MTc0ODg3NzU0OSwiZXhwIjoxNzQ5NDgyMzQ5fQ.3fx5j-8PbI5KomZ9l27p-5X3FLc7QeKFolUCEIDGrC2cKsw5pQuDuzXwN1e3zl2fOk7CQpCKnJVs7OdUqxFDPw','REFRESH',_binary '𹙛\�pJЙ�\�&x2�'),(_binary '�#�EN���1�\�$',_binary '\0',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiYW5oYWJhbmdAZXhhbXBsZS5jb20iLCJyb2xlcyI6IltST0xFX1BEVF0iLCJ0ZWFjaGVyX2NvZGUiOiJDQi4wMDEuMDAxLjAwMSIsImlhdCI6MTc0ODg3NDIxOSwiZXhwIjoxNzQ5NDc5MDE5fQ.jHfVlLrInQZp88D8dATIUuaNeJ4w-Rx3qvhITZlJ9uPRolXfBEfFyjDINiHUgX0IBL8qV-3Ie28w4TIFlledeQ','REFRESH',_binary '𹙛\�pJЙ�\�&x2�'),(_binary '�b��\�@]�X \�~@G,',_binary '\0',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiYW5odGhpcXV5bmhtYWlAZXhhbXBsZS5jb20iLCJyb2xlcyI6IltST0xFX1RFQUNIRVJdIiwidGVhY2hlcl9jb2RlIjoiQ0IuMDAxLjAwMi4wMDEiLCJpYXQiOjE3NDg4NzQyNzMsImV4cCI6MTc0OTQ3OTA3M30.Tj9h0zkT9ZhNXr4XeGsYVHOV-rVvPvPy3xQA2vANadWnZoZ-ksO33xKXbryd1s2FEk1BeHUEm49-AkPSt78JxQ','REFRESH',_binary '�{t�W�N\'�9�\�\��'),(_binary '�Tl\�k�I҇Z\�	\�F�',_binary '\0',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiYW5oYWJhbmdAZXhhbXBsZS5jb20iLCJyb2xlcyI6IltST0xFX1BEVF0iLCJ0ZWFjaGVyX2NvZGUiOiJDQi4wMDEuMDAxLjAwMSIsImlhdCI6MTc0ODg3Mzk5OCwiZXhwIjoxNzQ5NDc4Nzk4fQ.DCGEBfNvEhTG1UyhQ8t0AXEg8oQq-kkdCMG8BnwbRnjTvRUJ_HkdodAsxdE0wHG22mFaJT_Mf42iRr5kqOD9Mw','REFRESH',_binary '𹙛\�pJЙ�\�&x2�'),(_binary '\�,ܓR�G�\�1���\�Z',_binary '\0',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiYW5oYWJhbmdAZXhhbXBsZS5jb20iLCJyb2xlcyI6IltST0xFX1BEVF0iLCJ0ZWFjaGVyX2NvZGUiOiJDQi4wMDEuMDAxLjAwMSIsImlhdCI6MTc0ODkxNTM4NSwiZXhwIjoxNzQ5NTIwMTg1fQ.e0a8X-rmRn6B9J2MsXVbuCahokA0CYu-Cx1H5oqgxwHlQ-AmZC_AveYJ2CLTDnBIyxJoLaGteAuMxEA49GkAvA','REFRESH',_binary '𹙛\�pJЙ�\�&x2�'),(_binary '\�켐\r\�N1�_�n\�O$J',_binary '\0',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiYW5oYWJhbmdAZXhhbXBsZS5jb20iLCJyb2xlcyI6IltST0xFX1BEVF0iLCJ0ZWFjaGVyX2NvZGUiOiJDQi4wMDEuMDAxLjAwMSIsImlhdCI6MTc0ODg2NzYyNCwiZXhwIjoxNzQ5NDcyNDI0fQ.L7J_fHBYse0X4LRFJQx8XyOH_h9I_K4eTcuKSEcoeNe_xHiMjjjUBEMGHfrT-9KQ935AKmHDyQQTkmuGi3ms1g','REFRESH',_binary '𹙛\�pJЙ�\�&x2�'),(_binary '״\�⧧FK�To����\�',_binary '\0',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiYW5oYWJhbmdAZXhhbXBsZS5jb20iLCJyb2xlcyI6IltST0xFX1BEVF0iLCJ0ZWFjaGVyX2NvZGUiOiJDQi4wMDEuMDAxLjAwMSIsImlhdCI6MTc0ODg3Mzg4MSwiZXhwIjoxNzQ5NDc4NjgxfQ.87rFXDogpRrdH1qEq9KzBEr-p4G74P1cAmYf4yBenlumIeWEq_Cq-sIb1jR2FrUtXbrvFtP6dC5uieMP-MR6eQ','REFRESH',_binary '𹙛\�pJЙ�\�&x2�'),(_binary '׿���\�BZ�1\r��j�',_binary '\0',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiYW5oYWJhbmdAZXhhbXBsZS5jb20iLCJyb2xlcyI6IltST0xFX1BEVF0iLCJ0ZWFjaGVyX2NvZGUiOiJDQi4wMDEuMDAxLjAwMSIsImlhdCI6MTc0ODg3NjUzNywiZXhwIjoxNzQ5NDgxMzM3fQ.qp9ou15TTVCQ5fQsP4mowKWLckhwwhVs_7pBSvarnsJEjiKUOfxPUEgLCMbj4zJa_qOw2OIkRSUOMc1QMrW2iA','REFRESH',_binary '𹙛\�pJЙ�\�&x2�'),(_binary '\�M7��Mr��\�@/c',_binary '\0',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiYW5odGhpcXV5bmhtYWlAZXhhbXBsZS5jb20iLCJyb2xlcyI6IltST0xFX1RFQUNIRVJdIiwidGVhY2hlcl9jb2RlIjoiQ0IuMDAxLjAwMi4wMDEiLCJpYXQiOjE3NDg4NzUzOTUsImV4cCI6MTc0OTQ4MDE5NX0.fv9Zhhb7UADsiS_LqhoE9abrlSY3JmX7VLKGRNrv8GkKMjSTcC5Pm_-gPFE6KJEA4lkcFl7jmMgcLwi4fO2keg','REFRESH',_binary '�{t�W�N\'�9�\�\��'),(_binary 'ٗޭ�E���M�f\�~�',_binary '\0',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiYW5oYWJhbmdAZXhhbXBsZS5jb20iLCJyb2xlcyI6IltST0xFX1BEVF0iLCJ0ZWFjaGVyX2NvZGUiOiJDQi4wMDEuMDAxLjAwMSIsImlhdCI6MTc0ODg3NzgzMiwiZXhwIjoxNzQ5NDgyNjMyfQ.-rRTegf19f3LNa4rkAI9pF1KFurIT82yU3CJrZe_j67m9mvJ9Al9gyopH0PseqJzWnQYsXeVD1CX4Cvts5uuVw','REFRESH',_binary '𹙛\�pJЙ�\�&x2�'),(_binary '\�\�k�m7Hʊ��fg6�',_binary '\0',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiYW5oYWJhbmdAZXhhbXBsZS5jb20iLCJyb2xlcyI6IltST0xFX1BEVF0iLCJ0ZWFjaGVyX2NvZGUiOiJDQi4wMDEuMDAxLjAwMSIsImlhdCI6MTc0ODg3ODM2MywiZXhwIjoxNzQ5NDgzMTYzfQ.cNio8N8eCpZUFhbPqoU7gdMF3bB1tGrGFU88Bb85YrKTiBo2DQWmS86uJad3Sef829-s8KU3GnzIYsSy8PKVCQ','REFRESH',_binary '𹙛\�pJЙ�\�&x2�'),(_binary '\�JG\�\"\�F��ⷋ\��',_binary '\0',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiYW5oYWJhbmdAZXhhbXBsZS5jb20iLCJyb2xlcyI6IltST0xFX1RFQUNIRVJdIiwidGVhY2hlcl9jb2RlIjoiQ0IuMDAxLjAwMS4wMDEiLCJpYXQiOjE3NDg3OTMzMTAsImV4cCI6MTc0OTM5ODExMH0.8M8Q9cKidBLU8GzxeTh2Q4qL6vMhEHR5zhq9Bnb8kiBJV-UFBu9fCSYYhu8sL1sd89Y9xAuUT1lGzlkaZJcevA','REFRESH',_binary '𹙛\�pJЙ�\�&x2�'),(_binary '\�ʚ��D�\�pE7r�\�',_binary '\0',_binary '\0','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiYW5oYWJhbmdAZXhhbXBsZS5jb20iLCJyb2xlcyI6IltST0xFX1BEVF0iLCJ0ZWFjaGVyX2NvZGUiOiJDQi4wMDEuMDAxLjAwMSIsImlhdCI6MTc0ODg3NDQ1NCwiZXhwIjoxNzQ5NDc5MjU0fQ.1LwrbBfkDsOqGKkPF_gQioLhgGbbMIJt5ksfNQH_7VSGHEQkHZoT0TRpC9Ci2EnouYstP-qCl--7oQz-Kx5ilQ','REFRESH',_binary '𹙛\�pJЙ�\�&x2�');
/*!40000 ALTER TABLE `tokens` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` binary(16) NOT NULL,
  `auth_provider` enum('FACEBOOK','GITHUB','GOOGLE','LOCAL') DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `enabled` bit(1) NOT NULL,
  `password` varchar(255) NOT NULL,
  `password_changed_at` datetime(6) DEFAULT NULL,
  `provider_id` varchar(255) DEFAULT NULL,
  `role` enum('PDT','TEACHER') DEFAULT NULL,
  `teacher_code` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UKppvdcsb7oavmfcnqy28u0as9a` (`teacher_code`),
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (_binary 'cT�#M��\��c=','LOCAL','anhbuithimai@example.com',_binary '','$2a$10$OOigq6IMU3xs/20DQwUYlOq1RSE6NFTU7JrBxlLofyKSvZDCZo0fi','2025-06-02 22:54:05.785396',NULL,'TEACHER','CB.001.001.002','anhbuithimai@example.com'),(_binary '�{t�W�N\'�9�\�\��','LOCAL','banhthiquynhmai@example.com',_binary '','$2a$10$WDVxRM/6mi33bgUvTZs0Au4VLdDLvVm6bXNV56MU7OIuTNX9nqORy','2025-06-01 22:31:55.939909',NULL,'TEACHER','CB.001.002.001','banhthiquynhmai@example.com'),(_binary '𹙛\�pJЙ�\�&x2�','LOCAL','banhabang@example.com',_binary '','$2a$10$oQddzgoq49YEwOjgpHsbMOh2M0/JkstF52wqm.NgLjPu0CwL/dcGG','2025-06-01 22:31:55.841473',NULL,'PDT','CB.001.001.001','banhabang@example.com');
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

-- Dump completed on 2025-06-03 11:00:27
