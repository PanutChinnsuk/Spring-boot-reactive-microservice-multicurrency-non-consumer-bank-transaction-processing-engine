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
INSERT INTO `transaction_lines` VALUES ('0dfd5d0e-0e80-4b84-b10a-e533e1725b8c','3c9c5362-6e2e-4ddd-82dd-72b3c3fcb995','TEST2','DEBIT',1.0000,1.0000,'Default3'),('10e9d169-70ea-4220-9fd8-a72099b515b6','d7c59c42-c22a-4496-ad7e-4c67f8723558','TEST','CREDIT',1.0000,1.0000,'Default4'),('12b6dbed-a5c0-454a-8339-7b9e005d7cb3','b2490d84-d53e-4549-90be-1fd185f478a2','TEST','CREDIT',1.0000,1.0000,'Default4'),('1e8e19a2-6e45-40e6-bc90-3a9a6fcd840e','51114b58-74ea-4b36-8c1d-e791dd96a844','TEST','CREDIT',1.0000,1.0000,'Default4'),('212ac442-97f4-4d5a-9279-3e3ddf980512','b2490d84-d53e-4549-90be-1fd185f478a2','TEST2','DEBIT',1.0000,1.0000,'Default4'),('266011a3-61e0-45f3-890e-ec33faf6da93','4f167f8c-d723-4508-b0fe-9b487800e552','TEST','CREDIT',1.0000,1.0000,'Default4'),('27bc9e65-c9e2-4ef8-a444-5f90dbe56e2c','3c9c5362-6e2e-4ddd-82dd-72b3c3fcb995','TEST','CREDIT',1.0000,1.0000,'Default3'),('2cd06a98-c87c-480b-b0de-791b33106518','1a41bbac-f290-4ec2-bd4e-c27727f41012','TEST2','DEBIT',1.0000,1.0000,'Default4'),('2f124ddd-2c65-45ea-b766-ce4ef9507753','4f167f8c-d723-4508-b0fe-9b487800e552','TEST2','DEBIT',1.0000,1.0000,'Default4'),('3453f6c4-aea6-401e-a1f5-fadb3c600cde','2f1b37d6-3e43-408c-9ac7-4d5691813b31','TEST','CREDIT',1.0000,1.0000,'Default4'),('3e3b4488-9a09-4785-840b-ff0989de0a5e','7caef917-a68b-49bc-9f9a-76c4a44c2468','TEST','CREDIT',1.0000,1.0000,'Default3'),('46e1b270-79a0-41f4-81d3-2f3768ae7436','79a180db-71e3-400c-9761-e3711f302881','TEST','DEBIT',1.0000,1.0000,'DefaultIdWhatever'),('4f296e16-ce32-4adb-bb53-37cd441b7256','37099424-3f05-4981-bdf1-f58712cbd9be','TEST','CREDIT',1.0000,1.0000,'Default3'),('55743acb-fb44-4ede-a6f0-69a388d099ba','a07cbcd8-7864-47c8-9236-63cf9b76d9d3','TEST2','DEBIT',1.0000,1.0000,'Default3'),('5806250d-143e-4733-90f1-3b0fe9bd2494','0c92c61d-1a0e-4f54-8e20-02ac9683d31e','TEST2','DEBIT',1.0000,1.0000,'Default3'),('6a359754-79ac-446c-b5f7-57b44431edbf','2f1b37d6-3e43-408c-9ac7-4d5691813b31','TEST2','DEBIT',1.0000,1.0000,'Default4'),('7071e251-06d8-488b-8f08-a392d3beb5e0','f9fb25df-0319-415f-a4bd-120cac26cbfc','TEST2','CREDIT',1.0000,1.0000,'DefaultIdWhatever'),('70b9740d-3d4b-4faa-b28d-47ba94b34ac1','2efa8506-0b14-4284-a30c-53a9b5677c78','TEST','CREDIT',1.0000,1.0000,'Default3'),('77c95a28-ae11-41fd-b93d-1b9fb2822a4f','79a180db-71e3-400c-9761-e3711f302881','TEST2','CREDIT',1.0000,1.0000,'DefaultIdWhatever'),('7df13827-e23c-48f2-9d2a-77aa8da9d41c','0c92c61d-1a0e-4f54-8e20-02ac9683d31e','TEST','CREDIT',1.0000,1.0000,'Default3'),('7edd400d-cf1a-4da9-b499-3ef122e52f33','f9fb25df-0319-415f-a4bd-120cac26cbfc','TEST','DEBIT',1.0000,1.0000,'DefaultIdWhatever'),('87180042-3d0d-4aa4-a29d-c94ccb260968','84d70293-81fa-4303-be0d-d7eeb5f4ee5d','TEST2','DEBIT',1.0000,1.0000,'Default3'),('8951ba1f-1d49-4a63-b668-96354201e7e8','84d70293-81fa-4303-be0d-d7eeb5f4ee5d','TEST','CREDIT',1.0000,1.0000,'Default3'),('93a64508-af24-4e00-a6ec-fd1b4aa8a6cc','e4cb76e8-1f43-4d0f-b785-0c19969d9781','TEST2','DEBIT',1.0000,1.0000,'Default4'),('9f39ab49-bdc8-4e3e-8a66-40a2040d8b48','51114b58-74ea-4b36-8c1d-e791dd96a844','TEST2','DEBIT',1.0000,1.0000,'Default4'),('a4ba9022-d055-4e1e-9dab-0aeda0b0774c','7caef917-a68b-49bc-9f9a-76c4a44c2468','TEST2','DEBIT',1.0000,1.0000,'Default3'),('a944a553-114f-491c-8198-72c9c90fa32e','37099424-3f05-4981-bdf1-f58712cbd9be','TEST2','DEBIT',1.0000,1.0000,'Default3'),('be9e14ed-e35e-404c-b90a-e74c1d860404','1a41bbac-f290-4ec2-bd4e-c27727f41012','TEST','CREDIT',1.0000,1.0000,'Default4'),('c8a5664a-f7de-48ba-993d-f4b5b55574df','e4cb76e8-1f43-4d0f-b785-0c19969d9781','TEST','CREDIT',1.0000,1.0000,'Default4'),('ed2539d7-3fe2-42ec-a2c8-d2fd44f7cd99','a07cbcd8-7864-47c8-9236-63cf9b76d9d3','TEST','CREDIT',1.0000,1.0000,'Default3'),('ef82df5f-cab8-4b41-90f0-15bf5c4009b9','d7c59c42-c22a-4496-ad7e-4c67f8723558','TEST2','DEBIT',1.0000,1.0000,'Default4'),('fdab2eac-7c49-4a75-b924-8a012ff9e974','2efa8506-0b14-4284-a30c-53a9b5677c78','TEST2','DEBIT',1.0000,1.0000,'Default3');
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

-- Dump completed on 2026-10-27 16:28:36
