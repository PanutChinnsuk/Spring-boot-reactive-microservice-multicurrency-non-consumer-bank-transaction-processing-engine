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
-- Table structure for table `account_profiles`
--

DROP TABLE IF EXISTS `account_profiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account_profiles` (
  `id` varchar(255) NOT NULL,
  `account_currency` varchar(45) NOT NULL,
  `account_type` varchar(45) NOT NULL,
  `status` varchar(45) NOT NULL,
  `version` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `account_id_UNIQUE` (`id`),
  CONSTRAINT `fk_account_profiles_account_id` FOREIGN KEY (`id`) REFERENCES `accounts` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account_profiles`
--

LOCK TABLES `account_profiles` WRITE;
/*!40000 ALTER TABLE `account_profiles` DISABLE KEYS */;
INSERT INTO `account_profiles` VALUES ('04eb7e73-1fe5-4b52-b187-4d56b22a6653','USD','BANK','ACTIVE',0),('2f339271-ee20-404e-acf2-8432870fe6e3','USD','BANK','ACTIVE',0),('40a58f72-c647-4777-86f0-38047fa0fcff','USD','BANK','ACTIVE',0),('4e1a0edc-994d-4a53-b601-1e61347e0155','USD','BANK','ACTIVE',0),('741804a8-fda8-4f48-9ee3-e987d9074d7d','USD','BANK','ACTIVE',0),('7afdc087-3d40-4649-b7e2-4b365e4ab055','USD','BANK','ACTIVE',0),('d26ce1ca-b7a7-495c-a7e2-76cc73a83dc5','USD','BANK','ACTIVE',0),('INTERNAL','USD','BANK','ACTIVE',0),('INTERNAL2','USD','BANK','ACTIVE',0),('TEST','USD','BANK','ACTIVE',0),('TEST2','USD','BANK','ACTIVE',0);
/*!40000 ALTER TABLE `account_profiles` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-17  6:40:53
