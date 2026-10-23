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
-- Table structure for table `transaction_lines`
--

DROP TABLE IF EXISTS `transaction_lines`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transaction_lines` (
  `id` varchar(255) NOT NULL,
  `transaction_id` varchar(255) NOT NULL,
  `account_id` varchar(255) NOT NULL,
  `direction` varchar(45) NOT NULL,
  `based_amount` decimal(19,4) NOT NULL,
  `converted_amount` decimal(19,4) NOT NULL,
  `ledger_account_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idtransaction_lines_UNIQUE` (`id`),
  KEY `fk_transaction_lines_transaction_id_idx` (`transaction_id`),
  KEY `fk_transaction_lines_account_id_idx` (`account_id`),
  KEY `fk_transaction_lines_ledger_account_id_idx` (`ledger_account_id`),
  CONSTRAINT `fk_transaction_lines_account_id` FOREIGN KEY (`account_id`) REFERENCES `accounts` (`id`),
  CONSTRAINT `fk_transaction_lines_ledger_account_id` FOREIGN KEY (`ledger_account_id`) REFERENCES `ledger_accounts` (`id`),
  CONSTRAINT `fk_transaction_lines_transaction_id` FOREIGN KEY (`transaction_id`) REFERENCES `transactions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction_lines`
--

LOCK TABLES `transaction_lines` WRITE;
/*!40000 ALTER TABLE `transaction_lines` DISABLE KEYS */;
INSERT INTO `transaction_lines` VALUES ('001d1fe9-f711-4b95-9564-5653363a70e9','fdfe1e0b-b901-4f0b-9537-1b1e2872bff6','TEST2','DEBIT',1.0000,1.0000,'TEST'),('0a2f84a9-c158-47d2-a2b2-feb6ac371a21','c0639ddf-75cd-4f75-91c1-02124ad84752','TEST','CREDIT',1.0000,1.0000,'TEST'),('3292e894-14ba-42da-bee5-a76d603733c7','fdfe1e0b-b901-4f0b-9537-1b1e2872bff6','TEST','CREDIT',1.0000,1.0000,'TEST'),('4497bccf-4b5a-4635-82b3-be8d805c4bca','c0639ddf-75cd-4f75-91c1-02124ad84752','TEST2','DEBIT',1.0000,1.0000,'TEST'),('b9314587-1d6b-42ec-addf-f8a1eb615403','069b80bf-9703-4129-bd30-8c5a44e3cc30','TEST','CREDIT',1.0000,1.0000,'TEST'),('da1a95fb-a987-4c31-9e73-26da2955fe08','21e7ca0e-bdd4-4ff0-8883-7d8cf1d2a8b9','TEST','CREDIT',1.0000,1.0000,'TEST'),('e66a070e-44c5-4940-8c0a-a1066ead73c2','21e7ca0e-bdd4-4ff0-8883-7d8cf1d2a8b9','TEST2','DEBIT',1.0000,1.0000,'TEST'),('ff7d0c55-3e55-4b3a-a021-c96be3d85afa','069b80bf-9703-4129-bd30-8c5a44e3cc30','TEST2','DEBIT',1.0000,1.0000,'TEST');
/*!40000 ALTER TABLE `transaction_lines` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-23 17:24:30
