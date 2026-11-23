-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: multicurrency_bank
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
-- Table structure for table `transaction_overdraft_content`
--

DROP TABLE IF EXISTS `transaction_overdraft_content`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transaction_overdraft_content` (
  `id` varchar(255) NOT NULL,
  `transaction_id` varchar(255) NOT NULL,
  `overdraft_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `fk_transaction_overdraft_content_transaction_id_idx` (`transaction_id`),
  KEY `fk_transaction_overdraft_content_overdraft_id_idx` (`overdraft_id`),
  CONSTRAINT `fk_transaction_overdraft_content_overdraft_id` FOREIGN KEY (`overdraft_id`) REFERENCES `account_overdrafts` (`id`),
  CONSTRAINT `fk_transaction_overdraft_content_transaction_id` FOREIGN KEY (`transaction_id`) REFERENCES `transactions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction_overdraft_content`
--

LOCK TABLES `transaction_overdraft_content` WRITE;
/*!40000 ALTER TABLE `transaction_overdraft_content` DISABLE KEYS */;
INSERT INTO `transaction_overdraft_content` VALUES ('TEST','TEST_EMPTY_OVERDRAFT','TEST_EMPTY'),('TEST2','TEST_EMPTY_OVERDRAFT','TEST_EMPTY2'),('TEST3','TEST_CANCEL_PROCESSED_WITH_OVERDRAFT_CONTENT','TEST_EMPTY'),('TEST4','TEST_CANCEL_PROCESSED_WITH_OVERDRAFT_CONTENT','TEST_EMPTY2'),('TEST5','TEST_CANCEL_HAS_EVERYTHING','TEST_EMPTY'),('TEST6','TEST_CANCEL_HAS_EVERYTHING','TEST_EMPTY2');
/*!40000 ALTER TABLE `transaction_overdraft_content` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-21 11:41:10
