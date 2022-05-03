-- MySQL dump 10.13  Distrib 8.0.26, for Win64 (x86_64)
--
-- Host: 67.205.183.72    Database: computer_combat
-- ------------------------------------------------------
-- Server version	8.0.28-0ubuntu0.21.10.3

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
-- Temporary view structure for view `card_stats`
--

USE `computer_combat`;
DROP TABLE IF EXISTS `card_stats`;
/*!50001 DROP VIEW IF EXISTS `card_stats`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `card_stats` AS SELECT 
 1 AS `id`,
 1 AS `match_id`,
 1 AS `win`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `deck_stats`
--

DROP TABLE IF EXISTS `deck_stats`;
/*!50001 DROP VIEW IF EXISTS `deck_stats`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `deck_stats` AS SELECT 
 1 AS `id`,
 1 AS `match_id`,
 1 AS `win`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `join_components`
--

DROP TABLE IF EXISTS `join_components`;
/*!50001 DROP VIEW IF EXISTS `join_components`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `join_components` AS SELECT 
 1 AS `card`,
 1 AS `components`*/;
SET character_set_client = @saved_cs_client;

--
-- Final view structure for view `card_stats`
--

/*!50001 DROP VIEW IF EXISTS `card_stats`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `card_stats` AS select `card`.`id` AS `id`,`deck_stats`.`match_id` AS `match_id`,`deck_stats`.`win` AS `win` from ((`card` left join `deck_has_card` on((`deck_has_card`.`card_id` = `card`.`id`))) left join `deck_stats` on((`deck_has_card`.`deck_id` = `deck_stats`.`id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `deck_stats`
--

/*!50001 DROP VIEW IF EXISTS `deck_stats`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `deck_stats` AS select `deck`.`id` AS `id`,`match`.`id` AS `match_id`,(((`match`.`winner` = 0) and (`match`.`deck1_id` = `deck`.`id`)) or ((`match`.`winner` = 1) and (`match`.`deck2_id` = `deck`.`id`))) AS `win` from (`deck` join `match` on(((`deck`.`id` = `match`.`deck1_id`) or (`deck`.`id` = `match`.`deck2_id`)))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `join_components`
--

/*!50001 DROP VIEW IF EXISTS `join_components`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `join_components` AS select `card`.`id` AS `card`,group_concat(`components`.`id` separator ',') AS `components` from ((`card` join `run_requirements` on((`card`.`id` = `run_requirements`.`card_id`))) join `components` on((`components`.`id` = `run_requirements`.`component_id`))) group by `card`.`id` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-05-03 11:21:29
