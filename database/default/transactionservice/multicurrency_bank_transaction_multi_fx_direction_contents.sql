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
-- Table structure for table `transaction_multi_fx_direction_contents`
--

DROP TABLE IF EXISTS `transaction_multi_fx_direction_contents`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transaction_multi_fx_direction_contents` (
  `id` varchar(255) NOT NULL,
  `transaction_id` varchar(255) NOT NULL,
  `transaction_line_id` varchar(255) NOT NULL,
  `fx_direction` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `fk_transaction_multi_fx_direction_contents_transaction_line_idx` (`transaction_line_id`),
  KEY `fk_transaction_multi_fx_direction_contents_transaction_id_idx` (`transaction_id`),
  CONSTRAINT `fk_transaction_multi_fx_direction_contents_transaction_id` FOREIGN KEY (`transaction_id`) REFERENCES `transactions` (`id`),
  CONSTRAINT `fk_transaction_multi_fx_direction_contents_transaction_line_id` FOREIGN KEY (`transaction_line_id`) REFERENCES `transaction_lines` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction_multi_fx_direction_contents`
--

LOCK TABLES `transaction_multi_fx_direction_contents` WRITE;
/*!40000 ALTER TABLE `transaction_multi_fx_direction_contents` DISABLE KEYS */;
INSERT INTO `transaction_multi_fx_direction_contents` VALUES ('0c38260f-26ee-4080-a6a5-f0708c84a3ad','55021f47-4d43-4ff5-afbc-fff7dce2bf0b','15389136-03f3-4d58-9b29-ca4ceeac2473','ASK'),('121024f3-a32a-4738-8d9e-bb914af6269b','55021f47-4d43-4ff5-afbc-fff7dce2bf0b','6d6956b4-ce16-4806-8a27-d740c3686eba','ASK'),('1276655f-5d64-480f-9368-2675a9446395','7bf7a00a-3218-42c1-88d0-64b1d480f6ac','e6e24ddb-bbd1-4eb6-a756-809e08a405de','ASK'),('1847642e-cb52-4cf6-a7b2-4a1011a6b5c2','0661cf52-9547-4d66-9d6a-61c20c8198ee','3550b82c-8ade-4927-966a-8c8f5639ac4b','ASK'),('4d6eb104-1fce-4425-baf1-f6b6d12b5f51','7bf7a00a-3218-42c1-88d0-64b1d480f6ac','1c0061a8-3d89-49c7-9937-298260395374','ASK'),('502ed64b-9127-47c2-889b-40ec04592929','b51038fc-ea00-4073-9733-185e5bcb6519','7af3a93d-0420-44a6-a271-9a32c69c71a1','BID'),('5bb83161-6826-4efa-a2f2-b9d764d2047d','7bf7a00a-3218-42c1-88d0-64b1d480f6ac','690667f6-17b9-40bc-bc09-307ab0cbaed1','ASK'),('618bb14b-ae9a-4832-a772-d88efeec586e','0661cf52-9547-4d66-9d6a-61c20c8198ee','8defc6c7-96ec-4655-b0a6-5f28a01118a3','ASK'),('70f58524-8047-4365-bc3d-f53424b53c13','b51038fc-ea00-4073-9733-185e5bcb6519','b4da9054-caea-439f-9cf6-18c61855b6c9','BID'),('85615987-5216-450a-be6f-78bb88acbe56','7bf7a00a-3218-42c1-88d0-64b1d480f6ac','c4906abd-8dc1-452d-a1ef-e93b1e329d7c','ASK'),('94309552-1d53-41fc-aefe-84a25ee67705','7bf7a00a-3218-42c1-88d0-64b1d480f6ac','30813529-6881-4459-8504-9b4be3406dfe','ASK'),('970bc872-7795-4676-b465-28eff1aad347','55021f47-4d43-4ff5-afbc-fff7dce2bf0b','47e03ebc-700b-44ee-8d18-0d67b9f37d41','ASK'),('9d3c78d6-2f3b-4d9d-808c-070917df7e38','7bf7a00a-3218-42c1-88d0-64b1d480f6ac','29568ed6-2c5f-4ffc-a3bd-dd8e95faf576','BID'),('aa4685e4-93a4-496b-bbb1-cb94dfdf10b5','55021f47-4d43-4ff5-afbc-fff7dce2bf0b','b6a62df8-6fce-4043-855a-005ad44f1511','ASK'),('b596423c-3713-4694-9574-cd7579c07bcf','55021f47-4d43-4ff5-afbc-fff7dce2bf0b','52b9d99a-a298-4ae9-b4c7-cefef37c2b7c','ASK'),('be44804d-2bb7-461a-82e2-1ef657cba0c1','7bf7a00a-3218-42c1-88d0-64b1d480f6ac','eef25d9a-ec0c-4fee-9a75-cb4d6619d733','ASK'),('bf70a4ae-6758-4fd7-a788-d0f9a2064ed2','55021f47-4d43-4ff5-afbc-fff7dce2bf0b','fc7cbf17-bb98-4124-8af8-d8cf062f926c','ASK'),('d90341fc-8bbe-4157-9eeb-49d99698c6eb','0661cf52-9547-4d66-9d6a-61c20c8198ee','109ec8f2-57d1-4ef3-b45b-a7e004ca4834','ASK'),('e0b04219-d424-47e0-860b-ffc6e08b0f70','7bf7a00a-3218-42c1-88d0-64b1d480f6ac','aaddd6b9-eb13-4a7e-a3c9-c5b9b04fc4bf','ASK'),('e6cbed68-df19-4d70-a2c2-842742aee5d8','7bf7a00a-3218-42c1-88d0-64b1d480f6ac','09a92e8f-b727-4c7d-8113-618f418c8743','ASK'),('f6f5dacd-53d7-4245-a2c0-72123b82f6e5','7bf7a00a-3218-42c1-88d0-64b1d480f6ac','8b214ba5-7c8f-42b4-b9f9-f9c625226c4e','ASK');
/*!40000 ALTER TABLE `transaction_multi_fx_direction_contents` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-26 17:38:50
