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
-- Table structure for table `transaction_multi_fx_contents`
--

DROP TABLE IF EXISTS `transaction_multi_fx_contents`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transaction_multi_fx_contents` (
  `id` varchar(255) NOT NULL,
  `transaction_id` varchar(255) NOT NULL,
  `fx_rate_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_transaction_multi_fx_contents_transaction_id_idx` (`transaction_id`),
  KEY `fk_transaction_multi_fx_contents_fx_rate_id_idx` (`fx_rate_id`),
  CONSTRAINT `fk_transaction_multi_fx_contents_fx_rate_id` FOREIGN KEY (`fx_rate_id`) REFERENCES `fx_rates` (`id`),
  CONSTRAINT `fk_transaction_multi_fx_contents_transaction_id` FOREIGN KEY (`transaction_id`) REFERENCES `transactions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction_multi_fx_contents`
--

LOCK TABLES `transaction_multi_fx_contents` WRITE;
/*!40000 ALTER TABLE `transaction_multi_fx_contents` DISABLE KEYS */;
INSERT INTO `transaction_multi_fx_contents` VALUES ('223fbe9a-82bf-450d-868c-2f3e18e261e5','49cf36e8-216f-495b-b509-380e5879efe5','TEST'),('3901f5fd-8b6d-4a01-9a1c-8414340f2c39','efa5c018-b4aa-4f9c-9eeb-e1d0888b43fd','TEST'),('cb42755c-599c-472f-9675-176fa28882ec','72f8cbc3-6b88-45d8-b717-3759154e5121','TEST'),('e62caaa7-9aa6-4a87-8163-5cd2195276e4','3843c998-e01d-4b7b-9281-756738ee8b92','TEST');
/*!40000 ALTER TABLE `transaction_multi_fx_contents` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-24 10:54:00
