-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: campus_lost_found
-- ------------------------------------------------------
-- Server version	8.0.42

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
-- Table structure for table `claims`
--

DROP TABLE IF EXISTS `claims`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `claims` (
  `claim_id` int NOT NULL AUTO_INCREMENT,
  `item_id` int NOT NULL,
  `student_id` int NOT NULL,
  `claim_description` text NOT NULL,
  `status` varchar(20) DEFAULT 'Pending',
  `claim_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`claim_id`),
  KEY `item_id` (`item_id`),
  KEY `student_id` (`student_id`),
  CONSTRAINT `claims_ibfk_1` FOREIGN KEY (`item_id`) REFERENCES `items` (`id`),
  CONSTRAINT `claims_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `claims`
--

LOCK TABLES `claims` WRITE;
/*!40000 ALTER TABLE `claims` DISABLE KEYS */;
INSERT INTO `claims` VALUES (1,7,5,'lost my black airpods near the library. It have blue cover and blue  pen line inside  cover','Approved','2026-11-01 09:47:37'),(2,9,5,'this is my laptop my uid is 24bcs10468','Rejected','2026-11-01 13:04:37');
/*!40000 ALTER TABLE `claims` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `items`
--

DROP TABLE IF EXISTS `items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `items` (
  `id` int NOT NULL AUTO_INCREMENT,
  `item_name` varchar(100) NOT NULL,
  `category` varchar(50) NOT NULL,
  `location` varchar(150) NOT NULL,
  `report_type` varchar(10) NOT NULL,
  `status` varchar(20) DEFAULT 'Open',
  `reported_date` date NOT NULL,
  `contact` varchar(30) DEFAULT NULL,
  `image_path` varchar(500) DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  `description` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `items`
--

LOCK TABLES `items` WRITE;
/*!40000 ALTER TABLE `items` DISABLE KEYS */;
INSERT INTO `items` VALUES (2,'specs box','box','c1 block','Found','Returned','2026-11-01','8146226120','images\\03cea722-dad3-41a2-ac94-ff8bbd27627f.jpg',NULL,NULL),(3,'specs','specs','c1 block','Found','Open','2026-11-01','8146226120','images\\9bf1c59c-efc8-4cfb-8115-c5c1ac4490e6.jpg',NULL,NULL),(4,'black wallet','wallet','library','Lost','Open','2026-11-01','8787878787','images\\62d14f8d-4cff-4b42-b6f7-35d22c2e9e22.jpg',NULL,NULL),(5,'bag','bag','library','Lost','Open','2026-11-01','1234567890','images\\70cb09bb-92e2-496c-ba96-c8e561dbd4ed.png',NULL,NULL),(6,'black wallet','wallet','library','Lost','Open','2026-11-01','8146226120','C:\\Users\\Harman Bawa\\OneDrive\\Pictures\\Screenshots\\Screenshot 2025-10-04 213630.png',1,'i lost my wallet if anyone found then put on this app'),(7,'blue airpods','electronic device','library','Found','Returned','2026-11-01','8146226120','C:\\Users\\Harman Bawa\\Pictures\\Screenshots\\Screenshot 2026-09-29 203442.png',1,'take it from me'),(8,'laptop','laptop','library','Lost','Open','2026-11-01','7894561230','C:\\Users\\Harman Bawa\\Pictures\\Screenshots\\Screenshot 2026-05-23 091841.png',5,'sssd'),(9,'laptop','laptop','library','Found','Open','2026-11-01','8456971230','C:\\Users\\Harman Bawa\\Pictures\\Screenshots\\Screenshot 2025-11-22 185208.png',1,'xds');
/*!40000 ALTER TABLE `items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `full_name` varchar(100) NOT NULL,
  `student_id` varchar(50) NOT NULL,
  `email` varchar(150) NOT NULL,
  `phone` varchar(15) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `role` enum('STUDENT','ADMIN') NOT NULL DEFAULT 'STUDENT',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `student_id` (`student_id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (2,'bawa','24bcs10346','24bcs10346@cuchd.in','9874563210','+eSysEXDVdQIBNUwCko7fQ==:4XGzBqzn+jGKos2h38wP2Es2CzWJe/Y3AI+MfjxP7eE=','STUDENT','2026-11-01 09:20:53'),(5,'bawa','24bcs11346','24bcs11346@cuchd.in','9876543210','15Hj7uAB9YVdYzZPjrmSrw==:anbs8o92nlgBq+ONq8aG/jgQfEV9FKnldc/emkWJ/y4=','STUDENT','2026-11-01 09:25:26'),(6,'Harman Bawa','1001','e1001@cuchd.in','8146226120','BAqds3defoy7bWBsvH6ewg==:aIF6UaqM0s6RCuZ0XfwPYCFS4P8TnutzvMWZg0iWCjo=','ADMIN','2026-11-01 12:36:59');
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

-- Dump completed on 2026-11-01 19:13:47
