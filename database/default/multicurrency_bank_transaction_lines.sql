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
INSERT INTO `transaction_lines` VALUES ('TEST','TEST_NO_ASSET_EXPENSE','TEST','DEBIT',1.0000,1.0000,'DefaultIdWhatever'),('TEST10','TEST2','TEST2','CREDIT',1.0000,1.0000,'DefaultIdWhatever'),('TEST2','TEST_NO_ASSET_EXPENSE','TEST2','CREDIT',2.0000,2.0000,'DefaultIdWhatever'),('TEST3','TEST_ASSET_EXPENSE','TEST','CREDIT',1.0000,1.0000,'Default3'),('TEST4','TEST_ASSET_EXPENSE','TEST2','DEBIT',2.0000,2.0000,'Default4'),('TEST5','TEST_NO_ASSET_EXPENSE','TEST','DEBIT',1.0000,1.0000,'DefaultIdWhatever'),('TEST6','TEST_ASSET_EXPENSE','TEST','CREDIT',1.0000,1.0000,'Default3'),('TEST7','TEST','TEST','DEBIT',1.0000,1.0000,'DefaultIdWhatever'),('TEST8','TEST','TEST2','CREDIT',1.0000,1.0000,'DefaultIdWhatever'),('TEST9','TEST2','TEST','DEBIT',1.0000,1.0000,'DefaultIdWhatever');
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

-- Dump completed on 2026-11-06 22:41:26
