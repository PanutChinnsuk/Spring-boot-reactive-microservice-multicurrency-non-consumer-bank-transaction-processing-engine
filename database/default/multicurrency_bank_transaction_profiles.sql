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
-- Table structure for table `transaction_profiles`
--

DROP TABLE IF EXISTS `transaction_profiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transaction_profiles` (
  `id` varchar(255) NOT NULL,
  `fx_direction` varchar(45) NOT NULL,
  `fx_rate_id` varchar(255) DEFAULT NULL,
  `posted_at` timestamp NULL DEFAULT NULL,
  `reversed_of_transaction_id` varchar(255) DEFAULT NULL,
  `version` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `transaction_id_UNIQUE` (`id`),
  UNIQUE KEY `reversed_of_transaction_id_UNIQUE` (`reversed_of_transaction_id`),
  KEY `fk_transaction_profiles_fx_rate_id_idx` (`fx_rate_id`),
  KEY `fk_transaction_profiles_reversed_of_transaction_id_optional_idx` (`reversed_of_transaction_id`),
  CONSTRAINT `fk_transaction_profiles_fx_rate_id` FOREIGN KEY (`fx_rate_id`) REFERENCES `fx_rates` (`id`),
  CONSTRAINT `fk_transaction_profiles_reversed_of_transaction_id_optional` FOREIGN KEY (`reversed_of_transaction_id`) REFERENCES `transactions` (`id`),
  CONSTRAINT `fk_transaction_profiles_transaction_id` FOREIGN KEY (`id`) REFERENCES `transactions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction_profiles`
--

LOCK TABLES `transaction_profiles` WRITE;
/*!40000 ALTER TABLE `transaction_profiles` DISABLE KEYS */;
INSERT INTO `transaction_profiles` VALUES ('9668894f-e2a1-4647-9367-e1cfe48e658b','NONE',NULL,NULL,NULL,0),('975f2b6a-926f-4f69-be57-8fd2fe40ce89','NONE',NULL,NULL,NULL,0),('c2b6ab69-1dd1-4c63-a8f1-8fdb43c09c2c','NONE',NULL,NULL,NULL,0),('TEST','NONE',NULL,NULL,NULL,0),('TEST_ASSET_EXPENSE','NONE',NULL,NULL,NULL,0),('TEST_AUTHORIZED_ASSET_EXPENSE','NONE',NULL,NULL,NULL,0),('TEST_AUTHORIZED_NO_ASSET_EXPENSE','NONE',NULL,NULL,NULL,0),('TEST_EMPTY_PENDING_POSTED','NONE',NULL,NULL,NULL,0),('TEST_NO_ASSET_EXPENSE','NONE',NULL,NULL,NULL,0),('TEST2','NONE',NULL,NULL,NULL,0);
/*!40000 ALTER TABLE `transaction_profiles` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-20  4:25:50
