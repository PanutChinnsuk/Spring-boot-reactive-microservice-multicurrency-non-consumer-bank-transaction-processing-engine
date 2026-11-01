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
INSERT INTO `transaction_profiles` VALUES ('0212f7e4-f7b2-41c0-a87a-a8acfaac63a3','NONE',NULL,NULL,NULL,0),('02183bdd-c313-4519-b9b6-fa902f90ac1a','NONE',NULL,NULL,NULL,0),('0c92c61d-1a0e-4f54-8e20-02ac9683d31e','NONE',NULL,NULL,NULL,0),('1a41bbac-f290-4ec2-bd4e-c27727f41012','NONE',NULL,NULL,NULL,0),('1e2b0ba7-27ed-4b74-9cae-ecdbbac4a829','NONE',NULL,NULL,NULL,0),('2efa8506-0b14-4284-a30c-53a9b5677c78','NONE',NULL,NULL,NULL,0),('2f1b37d6-3e43-408c-9ac7-4d5691813b31','NONE',NULL,NULL,NULL,0),('37099424-3f05-4981-bdf1-f58712cbd9be','NONE',NULL,NULL,NULL,0),('3c9c5362-6e2e-4ddd-82dd-72b3c3fcb995','NONE',NULL,NULL,NULL,0),('43b594fe-4b65-427e-b2ae-50f004bb5a66','NONE',NULL,NULL,NULL,0),('4f167f8c-d723-4508-b0fe-9b487800e552','NONE',NULL,NULL,NULL,0),('51114b58-74ea-4b36-8c1d-e791dd96a844','NONE',NULL,NULL,NULL,0),('79a180db-71e3-400c-9761-e3711f302881','NONE',NULL,NULL,NULL,0),('7caef917-a68b-49bc-9f9a-76c4a44c2468','NONE',NULL,NULL,NULL,0),('84d70293-81fa-4303-be0d-d7eeb5f4ee5d','NONE',NULL,NULL,NULL,0),('8648bb21-9052-468e-be85-8b3bcd2791e3','NONE',NULL,NULL,NULL,0),('9eb6d523-a52a-4cdb-a57a-e8032317e1a8','NONE',NULL,NULL,NULL,0),('9ecccfa1-6dad-4a38-9835-1f362fe41447','NONE',NULL,NULL,NULL,0),('a07cbcd8-7864-47c8-9236-63cf9b76d9d3','NONE',NULL,NULL,NULL,0),('a75e3a1e-0d8b-4314-b333-f9c7f0d6671c','NONE',NULL,NULL,NULL,0),('b2490d84-d53e-4549-90be-1fd185f478a2','NONE',NULL,NULL,NULL,0),('bd6da299-44a4-47bc-8451-d17ff7c7aa0e','NONE',NULL,NULL,NULL,0),('d7c59c42-c22a-4496-ad7e-4c67f8723558','NONE',NULL,NULL,NULL,0),('daea468b-b734-440d-871c-e15ad3b002aa','NONE',NULL,NULL,NULL,0),('e4cb76e8-1f43-4d0f-b785-0c19969d9781','NONE',NULL,NULL,NULL,0),('f67aa668-54c0-4ebb-a0e3-71bb0d716d5f','NONE',NULL,NULL,NULL,0),('f9fb25df-0319-415f-a4bd-120cac26cbfc','NONE',NULL,NULL,NULL,0),('fe0e0f93-2734-49df-b482-6bb5884defc4','NONE',NULL,NULL,NULL,0);
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

-- Dump completed on 2026-11-01 18:57:47
