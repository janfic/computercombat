-- MySQL dump 10.13  Distrib 8.0.26, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: computer_combat
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

CREATE SCHEMA IF NOT EXISTS `computer_combat` DEFAULT CHARACTER SET utf8 ;

USE `computer_combat`;
SET GLOBAL sql_mode=(SELECT REPLACE(@@sql_mode,'ONLY_FULL_GROUP_BY',''));

-- -----------------------------------------------------
-- Table `computer_combat`.`ability`
-- -----------------------------------------------------
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `computer_combat`.`ability` (
  `id` INT NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  `description` LONGTEXT NOT NULL,
  `code` TEXT NOT NULL,
  `textureName` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

--
-- Table structure for table `profile`
--

DROP TABLE IF EXISTS `profile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `profile` (
  `uid` varchar(45) NOT NULL,
  `username` varchar(20) NOT NULL,
  `email` tinytext NOT NULL,
  `packets` int(10) unsigned zerofill NOT NULL,
  PRIMARY KEY (`uid`),
  UNIQUE KEY `idProfile_UNIQUE` (`uid`),
  UNIQUE KEY `userName_UNIQUE` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `bot`
--

DROP TABLE IF EXISTS `bot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bot` (
  `id` int NOT NULL,
  `profile_uid` varchar(45) NOT NULL,
  `priority_list` varchar(45) NOT NULL,
  `name` varchar(45) NOT NULL,
  `deck_id` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `bot_card`
--

DROP TABLE IF EXISTS `bot_card`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bot_card` (
  `id` int unsigned NOT NULL,
  `name` varchar(45) NOT NULL,
  `description` longtext NOT NULL,
  `textureName` varchar(45) NOT NULL,
  `code` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='Table representing cards that players use to create priority lists for their "defense" bots. Created from HeuristicAnalyzers.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `card`
--

DROP TABLE IF EXISTS `card`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `card` (
  `id` int NOT NULL,
  `name` varchar(45) NOT NULL,
  `maxHealth` int NOT NULL,
  `maxDefense` int NOT NULL,
  `maxAttack` int NOT NULL,
  `runRequirements` int NOT NULL,
  `level` int NOT NULL,
  `textureName` varchar(45) NOT NULL,
  `collection_id` int unsigned NOT NULL,
  `ability_id` int NOT NULL,
  `rarity` int unsigned NOT NULL,
  `description` mediumtext NOT NULL,
  PRIMARY KEY (`id`,`ability_id`),
  UNIQUE KEY `name_UNIQUE` (`name`),
  KEY `fk_Card_Pack1_idx` (`collection_id`),
  KEY `fk_Card_Ability1_idx` (`ability_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `collection`
--

DROP TABLE IF EXISTS `collection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `collection` (
  `id` int NOT NULL,
  `name` varchar(45) NOT NULL,
  `description` mediumtext,
  `textureName` varchar(45) NOT NULL,
  `path` varchar(45) DEFAULT NULL,
  `price` int NOT NULL DEFAULT '50',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `components`
--

DROP TABLE IF EXISTS `components`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `components` (
  `id` int NOT NULL,
  `name` varchar(45) NOT NULL,
  `textureName` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `deck`
--

DROP TABLE IF EXISTS `deck`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `deck` (
  `id` int NOT NULL,
  `name` varchar(45) NOT NULL,
  `profile_id` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_Deck_Profile_idx` (`profile_id`),
  CONSTRAINT `fk_Deck_Profile` FOREIGN KEY (`profile_id`) REFERENCES `profile` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `deck_has_card`
--

DROP TABLE IF EXISTS `deck_has_card`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `deck_has_card` (
  `deck_id` int NOT NULL,
  `card_id` int NOT NULL,
  `amount` int DEFAULT NULL,
  KEY `fk_Deck_has_Card_Deck1_idx` (`deck_id`),
  KEY `fk_Deck_has_Card_Card1_idx` (`card_id`),
  CONSTRAINT `fk_Deck_has_Card_Card1` FOREIGN KEY (`card_id`) REFERENCES `card` (`id`),
  CONSTRAINT `fk_Deck_has_Card_Deck1` FOREIGN KEY (`deck_id`) REFERENCES `deck` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `match`
--

DROP TABLE IF EXISTS `match`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `match` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `player1_uid` varchar(45) NOT NULL,
  `player2_uid` varchar(45) NOT NULL,
  `deck1_id` int NOT NULL,
  `deck2_id` int NOT NULL,
  `winner` tinyint DEFAULT NULL,
  `starttime` datetime NOT NULL,
  `endtime` datetime NOT NULL,
  `packets_player1` int unsigned NOT NULL DEFAULT '0',
  `packets_player2` int unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `fk_Match_profile1_idx` (`player1_uid`),
  KEY `fk_Match_profile2_idx` (`player2_uid`),
  KEY `fk_Match_deck1_idx` (`deck1_id`),
  KEY `fk_Match_deck2_idx` (`deck2_id`),
  CONSTRAINT `fk_Match_deck1` FOREIGN KEY (`deck1_id`) REFERENCES `deck` (`id`),
  CONSTRAINT `fk_Match_deck2` FOREIGN KEY (`deck2_id`) REFERENCES `deck` (`id`),
  CONSTRAINT `fk_Match_profile1` FOREIGN KEY (`player1_uid`) REFERENCES `profile` (`uid`),
  CONSTRAINT `fk_Match_profile2` FOREIGN KEY (`player2_uid`) REFERENCES `profile` (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=89 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `move`
--

DROP TABLE IF EXISTS `move`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `move` (
  `id` int NOT NULL AUTO_INCREMENT,
  `data` json NOT NULL,
  `match_id` int unsigned NOT NULL,
  `move_number` int NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_move_match1_idx` (`match_id` ASC) VISIBLE,
  CONSTRAINT `fk_move_match1`
    FOREIGN KEY (`match_id`)
    REFERENCES `computer_combat`.`match` (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 21
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `computer_combat`.`player`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `computer_combat`.`player` (
  `id` INT NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  `deck_id` INT NOT NULL,
  `bot` LONGTEXT NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `computer_combat`.`profile_owns_card`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `computer_combat`.`profile_owns_card` (
  `profile_id` VARCHAR(45) NOT NULL,
  `card_id` INT NOT NULL,
  `amount` INT UNSIGNED NOT NULL DEFAULT '1',
  PRIMARY KEY (`profile_id`, `card_id`),
  INDEX `fk_Profile_has_Card_Profile1_idx` (`profile_id` ASC) VISIBLE,
  INDEX `fk_Profile_has_Card_Card1_idx` (`card_id` ASC) VISIBLE,
  CONSTRAINT `fk_Profile_has_Card_Card1`
    FOREIGN KEY (`card_id`)
    REFERENCES `computer_combat`.`card` (`id`),
  CONSTRAINT `fk_Profile_has_Card_Profile1`
    FOREIGN KEY (`profile_id`)
    REFERENCES `computer_combat`.`profile` (`uid`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `computer_combat`.`run_requirements`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `computer_combat`.`run_requirements` (
  `card_id` INT NOT NULL,
  `component_id` INT NOT NULL,
  PRIMARY KEY (`card_id`, `component_id`),
  INDEX `fk_Card_has_Components_Components1_idx` (`component_id` ASC) VISIBLE,
  INDEX `fk_Card_has_Components_Card1_idx` (`card_id` ASC) VISIBLE,
  CONSTRAINT `fk_Card_has_Components_Card1`
    FOREIGN KEY (`card_id`)
    REFERENCES `computer_combat`.`card` (`id`),
  CONSTRAINT `fk_Card_has_Components_Components1`
    FOREIGN KEY (`component_id`)
    REFERENCES `computer_combat`.`components` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;

CREATE VIEW `join_components` AS
SELECT card.id as card, group_concat(components.id) as components FROM card
JOIN run_requirements ON card.id = run_requirements.card_id
JOIN components ON components.id = run_requirements.component_id GROUP BY card.id;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
SET GLOBAL sql_mode=(SELECT REPLACE(@@sql_mode,'ONLY_FULL_GROUP_BY',''));
