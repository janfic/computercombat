-- MySQL dump 10.13  Distrib 8.0.26, for Win64 (x86_64)
--
-- Host: computer-combat-db.cloqezbutiub.us-east-1.rds.amazonaws.com    Database: computer_combat
-- ------------------------------------------------------
-- Server version	8.0.23

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
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '';

USE computer_combat;

--
-- Table structure for table `ability`
--

DROP TABLE IF EXISTS `ability`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ability` (
  `id` int NOT NULL,
  `name` varchar(45) NOT NULL,
  `description` longtext NOT NULL,
  `code` text NOT NULL,
  `textureName` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ability`
--

LOCK TABLES `ability` WRITE;
/*!40000 ALTER TABLE `ability` DISABLE KEYS */;
INSERT INTO `ability` VALUES (0,'Draw','Draw a card from your deck','new DrawAbility();','draw_card'),(1,'Infect','Spawn 5 [BUG]Bugs[WHITE] on the board','new SpawnAbility(5, {state, move -> A:{return 5;}} as StateAnalyzer<Integer>, []);','infect'),(2,'Open Folder','Collect All [STORAGE]Storage Components','new CollectAbility([], [{match, move, c -> F: {return c.getColor() == 3;}} as CollectFilter],\n	{match, move -> G:{return 64;}} as StateAnalyzer<Integer>\n);','open_folder'),(3,'Tunnel','Collect a row of components','Filter f = new ComponentFilter(\"Select a row\") {\n 	public boolean filter(Component c, MatchState state, Move move) {return c.getX() == 0;}\n};\n\nnew CollectAbility([f], \n	[{match, UseAbilityMove move, c -> F:{\n		Component selected = move.getSelectedComponents().get(0);\n		return c.getY() == selected.getY();\n	}} as CollectFilter],\n        {MatchState state, Move move -> G:{\n		return 8;\n         }} as StateAnalyzer<Integer>\n);','tunnel'),(4,'Click','Attack an Opponent for 5 damage','Filter filter = new CardFilter(\"Choose an opponents card to damage\") {\n     	public boolean filter(Card card, MatchState state, Move move) {\n		System.out.println(card.getOwnerUID() + \" \"  + state.currentPlayerMove.getUID());\n		return !card.getOwnerUID().equals(state.currentPlayerMove.getUID())\n	}\n};\nnew FocusedDamageAbility([filter], {state, move -> A:{return 5;}} as StateAnalyzer<Integer>);','click'),(5,'Corruption','Transform all [STORAGE]Storage Components[WHITE] to [BUG]Bugs','ComponentFilter filter = new ComponentFilter(\"All Storage Components\") {\n     public boolean filter(Component c, MatchState state, Move move) { return c.getColor() == 3}\n};\n\nnew TransformComponentsAbility(\n  	[],\n        filter,\n        [5]\n);','corrupt'),(6,'Gigabyte Flash','Spawn 8 [STORAGE]Storage Components[WHITE] ','new SpawnAbility(3, {state, move -> A:{return 8;}} as StateAnalyzer, []);','gigabyte_flash'),(7,'Terabyte Onslaught','Deal 3 damage boosted by [STORAGE]Storage Components[WHITE]. Then Collect half of all [STORAGE] Storage Components [WHITE]','Filter filter = new CardFilter(\"Choose an opponents card to damage\") {\n     public boolean filter(Card card, MatchState state, Move move) {return !card.getOwnerUID().equals(state.currentPlayerMove.getUID());}\n    };\n\nComponentFilter storageFilter = new ComponentFilter(\"Storage Components\"){\n	public boolean filter(Component component, MatchState state, Move move) {return component.getColor() == 3};\n} as ComponentFilter;\n    \nStateAnalyzer<Integer> scaleDamage = {\n	state, move -> D:{return 3 + state.countComponents(storageFilter, move).intdiv(2) ;}\n} as StateAnalyzer<Integer>;\n  \nnew MultiAbility(\n        [\n            new FocusedDamageAbility([filter], scaleDamage),\n            new CollectAbility(\n		[],\n		[{match, move, c -> F: {return c.getColor() == 3;}} as CollectFilter], \n		{MatchState state, move -> A:{return state.countComponents(storageFilter, move).intdiv(2);}} as StateAnalyzer<Integer>)\n	]\n);','terabyte_onslaught'),(8,'Violent Volume','Deal 1 damage to all opponents boosted by [POWER] Power Components [WHITE]','ComponentFilter powerFilter = new ComponentFilter(\"Power Filter\") {\n	public boolean filter(Component c, MatchState state, Move move) {return c.getColor() == 6 }\n};\n\nStateAnalyzer<Integer> powerCount = {state, move -> A:{return 1 + state.countComponents(powerFilter, move).intdiv(2)}} as StateAnalyzer<Integer>;\n\nnew DamageAllAbility(powerCount);','violent_volume'),(9,'Blinding Brightness','Deal 4 damage to all opponents','StateAnalyzer<Integer> damage = {state, move -> A:{return 4;}} as StateAnalyzer<Integer>;\nnew DamageAllAbility(damage);','blinding_brightness'),(10,'Key Smash','Collect 5-10 random components. Gain an extra turn.','new MultiAbility(\n	[\n		new CollectAbility(\n			[],\n			[{state, move, c -> C:{return true;}} as CollectFilter],\n			{state, move -> F:{return 5 + (int)(6 * Math.random())}} as StateAnalyzer<Integer>\n		),\n		new ExtraTurnAbility()\n	]\n);','key_smash'),(11,'Open File','There is 20% chance to transform into a Corrupted File. Spawn 5 [STORAGE]Storage Components','new MultiAbility(\n	[\n		new TransformCardAbility(\n			[{card, state, move -> F:{\n				UseAbilityMove abilityMove = (UseAbilityMove) move;\n				return Math.random() < 0.5 && abilityMove.getCard().equals(card);\n			}} as CardFilter],\n			[12],\n			[]\n		),\n		new SpawnAbility(3, {state, move -> F:{return 5;}} as StateAnalyzer<Integer>, [])\n	]\n);\n\n','open_file'),(12,'Open Shortcut','Spawn a random card','new DrawAbility([\n	{state, move -> C:{return 1 + (int)(Math.random() * 11)}} as StateAnalyzer<Integer>\n]);','open_shortcut'),(13,'Arrange Tasks','Select an opponent to move to the first position.','Filter filter = new CardFilter(\"Choose an opponents card to damage\") {\n    	public boolean filter(Card card, MatchState state, Move move) {\n		return !card.getOwnerUID().equals(state.currentPlayerMove.getUID())\n	}\n};\n\nnew CardToFrontAbility([filter]);','arrange_tasks'),(14,'Power Surge','Double the amount of [POWER]Power Components[WHITE] on the board.','ComponentFilter powerFilter = new ComponentFilter(\"Power Filter\") {\n	public boolean filter(Component c, MatchState state, Move move) {return c.getColor() == 6}\n};\n\nnew SpawnAbility(\n	6,\n	{state, move -> A:{ \n		return state.countComponents(powerFilter, move);\n	}} as StateAnalyzer<Integer>,\n	[]\n);','power_surge');
/*!40000 ALTER TABLE `ability` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `card`
--

LOCK TABLES `card` WRITE;
/*!40000 ALTER TABLE `card` DISABLE KEYS */;
INSERT INTO `card` VALUES (0,'Computer',20,0,0,15,0,'computer',1,0,0,'Protect it at all costs. It lets you play this game.'),(1,'Bubble Sort',2,1,1,4,1,'bubble_sort',2,3,5,'Isn\'t there enough switching in the game, but now we have to sort with it.'),(2,'Directory',3,4,3,8,1,'directory',1,2,5,'A cozy place for those files.'),(3,'Disk Defragmenter',2,1,2,8,1,'disk_defragmenter',1,3,3,'Have you defragmented your disk recently? Take this as a reminder to do so.'),(4,'Fire Wall',6,3,1,8,1,'firewall',4,3,3,'I protect!...from fire?'),(5,'Random Number Generator',3,1,2,6,1,'rng',2,3,5,'Choose a number between 1 and 4294967296, no cheating.'),(6,'Virus',3,2,5,7,1,'virus',4,1,3,'I heard these are dangerous in the real world too'),(7,'Web Search',2,1,1,9,1,'web_search',4,3,3,'BOO! Your browsing history. Scared you didn\'t I?'),(8,'Worm',3,1,5,9,1,'worm',4,3,2,'I heard these are not dangerous in the real world.'),(9,'Flash Drive',3,5,2,8,1,'flash_drive',1,6,4,'If you find my flashdrive from 2008 please let me know.'),(10,'Router',7,3,2,8,1,'router',4,2,5,'Do your packets need directions?'),(11,'Mouse',3,3,4,6,1,'mouse',1,4,5,'How else do you use your computer? A joystick? That would be cool'),(12,'Corrupted File',7,3,5,10,1,'corrupted_file',1,5,1,'I thought I trusted you, but now you must be deleted'),(13,'Hard Drive',5,8,3,7,1,'hardrive',1,7,2,'Its like a flash drive, but bigger. '),(14,'Speaker',5,2,5,9,1,'speaker',1,8,3,'If you turn up your volume you can hear the sound of me winning this game.'),(15,'Monitor',4,3,5,7,1,'monitor',1,9,4,'Brighter, bigger, wider, better.'),(16,'Keyboard',3,3,3,7,1,'keyboard',1,10,5,'I can type at 5 words a minute because I can\'t type without looking at this thing.'),(17,'File',3,2,2,6,1,'file',1,11,5,'Ooo! A file! What\'s in it?!'),(18,'Shortcut',4,2,3,8,1,'shortcut',1,12,4,'I heard these take you from one place to another, but like really quickly.'),(19,'Task Bar',3,4,3,6,1,'task_bar',1,13,4,'Tasks? I\'d rather not. But maybe the bar.'),(20,'Power Strip',4,2,2,9,1,'power_strip',1,14,4,'More POWER! MORE !!!!');
/*!40000 ALTER TABLE `card` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `card_info`
--

DROP TABLE IF EXISTS `card_info`;
/*!50001 DROP VIEW IF EXISTS `card_info`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `card_info` AS SELECT 
 1 AS `card_id`,
 1 AS `name`,
 1 AS `maxAttack`,
 1 AS `maxHealth`,
 1 AS `maxDefense`,
 1 AS `runRequirements`,
 1 AS `level`,
 1 AS `textureName`,
 1 AS `rarity`,
 1 AS `ability_id`,
 1 AS `description`,
 1 AS `code`,
 1 AS `ability_name`,
 1 AS `ability_textureName`,
 1 AS `component_id`,
 1 AS `collection_id`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `card_wins`
--

DROP TABLE IF EXISTS `card_wins`;
/*!50001 DROP VIEW IF EXISTS `card_wins`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `card_wins` AS SELECT 
 1 AS `match_id`,
 1 AS `deck_id`,
 1 AS `card_id`,
 1 AS `did_win`*/;
SET character_set_client = @saved_cs_client;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `collection`
--

LOCK TABLES `collection` WRITE;
/*!40000 ALTER TABLE `collection` DISABLE KEYS */;
INSERT INTO `collection` VALUES (0,'Mystery','Any Card can be found','mystery_pack','mystery_pack',25),(1,'Computer','Its called Computer Combat for a reason','computer_pack','computer_pack',50),(2,'Algorithms','Quick! Sort these for me!','algorithms_pack','algorithms_pack',50),(4,'Networks','The Internet!!!','networks_pack','networks_pack',50),(5,'Data Structures','So much data, so little space.','data_structures_pack','data_structures_pack',50);
/*!40000 ALTER TABLE `collection` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `components`
--

LOCK TABLES `components` WRITE;
/*!40000 ALTER TABLE `components` DISABLE KEYS */;
INSERT INTO `components` VALUES (1,'CPUComponent','cpu'),(2,'RAMComponent','ram'),(3,'StorageComponent','storage'),(4,'NetworkComponent','network'),(5,'BugComponent','bug'),(6,'PowerComponent','power');
/*!40000 ALTER TABLE `components` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `deck`
--

LOCK TABLES `deck` WRITE;
/*!40000 ALTER TABLE `deck` DISABLE KEYS */;
INSERT INTO `deck` VALUES (280270749,'Starter','f07cdf89-8b4b-4d7a-b44f-aac78c978f54'),(673726932,'Select Test','67ec7d73-78c7-4831-bdcc-f864dd5c2db4'),(1084082101,'Virus','49646eae-d1ef-4067-9360-de1ad38f5993'),(1132156873,'Malware','91a6c870-8d1b-4cfd-af0e-0ecb9d625333'),(1365716984,'cy','49646eae-d1ef-4067-9360-de1ad38f5993'),(1690378354,'Network Deck','49646eae-d1ef-4067-9360-de1ad38f5993'),(1864431213,'Storage','67ec7d73-78c7-4831-bdcc-f864dd5c2db4');
/*!40000 ALTER TABLE `deck` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `deck_has_card`
--

LOCK TABLES `deck_has_card` WRITE;
/*!40000 ALTER TABLE `deck_has_card` DISABLE KEYS */;
INSERT INTO `deck_has_card` VALUES (1690378354,4,1),(1690378354,6,2),(1690378354,7,2),(1690378354,8,2),(1690378354,10,1),(1084082101,16,2),(1132156873,4,1),(1132156873,6,3),(1132156873,8,4),(280270749,2,1),(280270749,14,1),(280270749,15,1),(280270749,16,1),(1864431213,2,2),(1864431213,17,2),(1864431213,6,1),(1864431213,8,1),(1864431213,9,2),(1864431213,20,1),(673726932,8,1);
/*!40000 ALTER TABLE `deck_has_card` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `deck_wins`
--

DROP TABLE IF EXISTS `deck_wins`;
/*!50001 DROP VIEW IF EXISTS `deck_wins`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `deck_wins` AS SELECT 
 1 AS `match_id`,
 1 AS `deck_id`,
 1 AS `did_win`*/;
SET character_set_client = @saved_cs_client;

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
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `match`
--

LOCK TABLES `match` WRITE;
/*!40000 ALTER TABLE `match` DISABLE KEYS */;
INSERT INTO `match` VALUES (49,'67ec7d73-78c7-4831-bdcc-f864dd5c2db4','botUID',673726932,673726932,0,'2022-03-04 13:19:23','2022-03-04 13:21:04',25,0),(50,'67ec7d73-78c7-4831-bdcc-f864dd5c2db4','botUID',673726932,673726932,0,'2022-03-04 13:21:10','2022-03-04 13:22:10',25,0),(51,'67ec7d73-78c7-4831-bdcc-f864dd5c2db4','botUID',673726932,673726932,0,'2022-03-04 13:26:53','2022-03-04 13:27:19',29,0);
/*!40000 ALTER TABLE `match` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `match_state`
--

DROP TABLE IF EXISTS `match_state`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `match_state` (
  `id` int NOT NULL AUTO_INCREMENT,
  `match_id` int unsigned NOT NULL,
  `match_state_number` int NOT NULL,
  `data` json NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_MatchState_Match1_idx` (`match_id`),
  CONSTRAINT `fk_MatchState_Match1` FOREIGN KEY (`match_id`) REFERENCES `match` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `match_state`
--

LOCK TABLES `match_state` WRITE;
/*!40000 ALTER TABLE `match_state` DISABLE KEYS */;
INSERT INTO `match_state` VALUES (1,49,0,'{\"decks\": {\"class\": \"java.util.HashMap\", \"botUID\": {\"id\": 673726932, \"name\": \"Select Test\", \"cards\": [\"8\", 1], \"class\": \"com.janfic.games.computercombat.model.Deck\", \"stack\": [8]}, \"67ec7d73-78c7-4831-bdcc-f864dd5c2db4\": {\"id\": 673726932, \"name\": \"Select Test\", \"cards\": [\"8\", 1], \"class\": \"com.janfic.games.computercombat.model.Deck\", \"stack\": [8]}}, \"winner\": null, \"players\": [{\"uid\": \"67ec7d73-78c7-4831-bdcc-f864dd5c2db4\", \"deck\": {\"id\": 673726932, \"name\": \"Select Test\", \"cards\": [\"8\", 1], \"stack\": [8]}, \"class\": \"com.janfic.games.computercombat.model.players.HumanPlayer\"}, {\"uid\": \"botUID\", \"deck\": {\"id\": 673726932, \"name\": \"Select Test\", \"cards\": [\"8\", 1], \"stack\": [8]}, \"class\": \"com.janfic.games.computercombat.model.players.HeuristicBotPlayer\"}], \"computers\": {\"class\": \"java.util.HashMap\", \"botUID\": {\"id\": 0, \"name\": \"Computer\", \"armor\": 0, \"class\": \"com.janfic.games.computercombat.model.Card\", \"level\": 0, \"magic\": 0, \"attack\": 0, \"health\": 20, \"rarity\": 0, \"ability\": {\"id\": 0, \"code\": \"new DrawAbility();\", \"name\": \"Draw\", \"class\": \"com.janfic.games.computercombat.model.abilities.DrawAbility\", \"description\": \"Draw a card from your deck\", \"textureName\": \"draw_card\"}, \"matchID\": 0, \"maxArmor\": 0, \"ownerUID\": \"67ec7d73-78c7-4831-bdcc-f864dd5c2db4\", \"maxAttack\": 0, \"maxHealth\": 20, \"collection\": {\"id\": 1, \"name\": \"Computer\", \"path\": \"computer_pack\", \"description\": \"Its called Computer Combat for a reason\", \"textureName\": \"computer_pack\"}, \"description\": \"Protect it at all costs. It lets you play this game.\", \"runProgress\": 15, \"textureName\": \"computer\", \"runComponents\": [1, 2, 3, 4, 6], \"runRequirements\": 15}, \"67ec7d73-78c7-4831-bdcc-f864dd5c2db4\": {\"id\": 0, \"name\": \"Computer\", \"armor\": 0, \"class\": \"com.janfic.games.computercombat.model.Card\", \"level\": 0, \"magic\": 0, \"attack\": 0, \"health\": 20, \"rarity\": 0, \"ability\": {\"id\": 0, \"code\": \"new DrawAbility();\", \"name\": \"Draw\", \"class\": \"com.janfic.games.computercombat.model.abilities.DrawAbility\", \"description\": \"Draw a card from your deck\", \"textureName\": \"draw_card\"}, \"matchID\": 0, \"maxArmor\": 0, \"ownerUID\": \"67ec7d73-78c7-4831-bdcc-f864dd5c2db4\", \"maxAttack\": 0, \"maxHealth\": 20, \"collection\": {\"id\": 1, \"name\": \"Computer\", \"path\": \"computer_pack\", \"description\": \"Its called Computer Combat for a reason\", \"textureName\": \"computer_pack\"}, \"description\": \"Protect it at all costs. It lets you play this game.\", \"runProgress\": 15, \"textureName\": \"computer\", \"runComponents\": [1, 2, 3, 4, 6], \"runRequirements\": 15}}, \"isGameOver\": false, \"activeEntities\": {\"class\": \"java.util.HashMap\", \"botUID\": [], \"67ec7d73-78c7-4831-bdcc-f864dd5c2db4\": []}, \"componentBoard\": \"1315662646134332323242433361562244324362311632261311261142664452\", \"currentPlayerMove\": {\"uid\": \"67ec7d73-78c7-4831-bdcc-f864dd5c2db4\", \"deck\": {\"id\": 673726932, \"name\": \"Select Test\", \"cards\": [\"8\", 1], \"stack\": [8]}, \"class\": \"com.janfic.games.computercombat.model.players.HumanPlayer\"}}'),(2,50,0,'{\"decks\": {\"class\": \"java.util.HashMap\", \"botUID\": {\"id\": 673726932, \"name\": \"Select Test\", \"cards\": [\"8\", 1], \"class\": \"com.janfic.games.computercombat.model.Deck\", \"stack\": [8]}, \"67ec7d73-78c7-4831-bdcc-f864dd5c2db4\": {\"id\": 673726932, \"name\": \"Select Test\", \"cards\": [\"8\", 1], \"class\": \"com.janfic.games.computercombat.model.Deck\", \"stack\": [8]}}, \"winner\": null, \"players\": [{\"uid\": \"67ec7d73-78c7-4831-bdcc-f864dd5c2db4\", \"deck\": {\"id\": 673726932, \"name\": \"Select Test\", \"cards\": [\"8\", 1], \"stack\": [8]}, \"class\": \"com.janfic.games.computercombat.model.players.HumanPlayer\"}, {\"uid\": \"botUID\", \"deck\": {\"id\": 673726932, \"name\": \"Select Test\", \"cards\": [\"8\", 1], \"stack\": [8]}, \"class\": \"com.janfic.games.computercombat.model.players.HeuristicBotPlayer\"}], \"computers\": {\"class\": \"java.util.HashMap\", \"botUID\": {\"id\": 0, \"name\": \"Computer\", \"armor\": 0, \"class\": \"com.janfic.games.computercombat.model.Card\", \"level\": 0, \"magic\": 0, \"attack\": 0, \"health\": 20, \"rarity\": 0, \"ability\": {\"id\": 0, \"code\": \"new DrawAbility();\", \"name\": \"Draw\", \"class\": \"com.janfic.games.computercombat.model.abilities.DrawAbility\", \"description\": \"Draw a card from your deck\", \"textureName\": \"draw_card\"}, \"matchID\": 0, \"maxArmor\": 0, \"ownerUID\": \"67ec7d73-78c7-4831-bdcc-f864dd5c2db4\", \"maxAttack\": 0, \"maxHealth\": 20, \"collection\": {\"id\": 1, \"name\": \"Computer\", \"path\": \"computer_pack\", \"description\": \"Its called Computer Combat for a reason\", \"textureName\": \"computer_pack\"}, \"description\": \"Protect it at all costs. It lets you play this game.\", \"runProgress\": 15, \"textureName\": \"computer\", \"runComponents\": [1, 2, 3, 4, 6], \"runRequirements\": 15}, \"67ec7d73-78c7-4831-bdcc-f864dd5c2db4\": {\"id\": 0, \"name\": \"Computer\", \"armor\": 0, \"class\": \"com.janfic.games.computercombat.model.Card\", \"level\": 0, \"magic\": 0, \"attack\": 0, \"health\": 20, \"rarity\": 0, \"ability\": {\"id\": 0, \"code\": \"new DrawAbility();\", \"name\": \"Draw\", \"class\": \"com.janfic.games.computercombat.model.abilities.DrawAbility\", \"description\": \"Draw a card from your deck\", \"textureName\": \"draw_card\"}, \"matchID\": 0, \"maxArmor\": 0, \"ownerUID\": \"67ec7d73-78c7-4831-bdcc-f864dd5c2db4\", \"maxAttack\": 0, \"maxHealth\": 20, \"collection\": {\"id\": 1, \"name\": \"Computer\", \"path\": \"computer_pack\", \"description\": \"Its called Computer Combat for a reason\", \"textureName\": \"computer_pack\"}, \"description\": \"Protect it at all costs. It lets you play this game.\", \"runProgress\": 15, \"textureName\": \"computer\", \"runComponents\": [1, 2, 3, 4, 6], \"runRequirements\": 15}}, \"isGameOver\": false, \"activeEntities\": {\"class\": \"java.util.HashMap\", \"botUID\": [], \"67ec7d73-78c7-4831-bdcc-f864dd5c2db4\": []}, \"componentBoard\": \"3443123265424251266114126236436151611214211216621214535141341634\", \"currentPlayerMove\": {\"uid\": \"67ec7d73-78c7-4831-bdcc-f864dd5c2db4\", \"deck\": {\"id\": 673726932, \"name\": \"Select Test\", \"cards\": [\"8\", 1], \"stack\": [8]}, \"class\": \"com.janfic.games.computercombat.model.players.HumanPlayer\"}}');
/*!40000 ALTER TABLE `match_state` ENABLE KEYS */;
UNLOCK TABLES;

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
  `move_results_id` int NOT NULL,
  `move_number` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_move_match1_idx` (`match_id`),
  KEY `fk_move_move_results1_idx` (`move_results_id`),
  CONSTRAINT `fk_move_match1` FOREIGN KEY (`match_id`) REFERENCES `match` (`id`),
  CONSTRAINT `fk_move_move_results1` FOREIGN KEY (`move_results_id`) REFERENCES `move_results` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `move`
--

LOCK TABLES `move` WRITE;
/*!40000 ALTER TABLE `move` DISABLE KEYS */;
/*!40000 ALTER TABLE `move` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `move_results`
--

DROP TABLE IF EXISTS `move_results`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `move_results` (
  `id` int NOT NULL AUTO_INCREMENT,
  `data` json NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=207 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `move_results`
--

LOCK TABLES `move_results` WRITE;
/*!40000 ALTER TABLE `move_results` DISABLE KEYS */;
INSERT INTO `move_results` VALUES (206,'[{\"move\": {\"class\": \"com.janfic.games.computercombat.model.moves.UseAbilityMove\", \"entity\": {\"id\": 0, \"name\": \"Computer\", \"armor\": 0, \"class\": \"com.janfic.games.computercombat.model.Computer\", \"level\": 1, \"magic\": 0, \"attack\": 0, \"health\": 20, \"ability\": {\"id\": 0, \"code\": \"new DrawAbility()\", \"name\": \"Draw Card\", \"class\": \"com.janfic.games.computercombat.model.abilities.DrawAbility\", \"description\": \"Draw a card from you deck\", \"textureName\": \"draw_card\"}, \"matchID\": 0, \"deckSize\": 8, \"maxArmor\": 0, \"ownerUID\": \"owner\", \"maxAttack\": 0, \"maxHealth\": 20, \"collection\": {\"id\": 1, \"name\": \"Computer\", \"path\": \"computer_pack\", \"description\": \"computer\", \"textureName\": \"computer_pack\"}, \"runProgress\": 20, \"textureName\": \"computer\", \"runComponents\": [\"com.janfic.games.computercombat.model.components.CPUComponent\", \"com.janfic.games.computercombat.model.components.NetworkComponent\", \"com.janfic.games.computercombat.model.components.StorageComponent\", \"com.janfic.games.computercombat.model.components.RAMComponent\", \"com.janfic.games.computercombat.model.components.PowerComponent\"], \"runRequirements\": 20}, \"player\": \"67ec7d73-78c7-4831-bdcc-f864dd5c2db4\", \"selectedSoftwares\": [], \"selectedComponents\": []}, \"class\": \"com.janfic.games.computercombat.model.moves.MoveResult\", \"newState\": {\"decks\": {\"class\": \"java.util.HashMap\", \"67ec7d73-78c7-4831-bdcc-f864dd5c2db4\": {\"id\": 852571628, \"name\": \"Storage\", \"cards\": [\"2\", 2, \"17\", 2, \"9\", 1, \"20\", 2], \"class\": \"com.janfic.games.computercombat.model.Deck\", \"stack\": [20, 2, 17, 17, 2, 20, 9]}, \"91a6c870-8d1b-4cfd-af0e-0ecb9d625333\": {\"id\": 1132156873, \"name\": \"Malware\", \"cards\": [\"4\", 1, \"6\", 3, \"8\", 4], \"class\": \"com.janfic.games.computercombat.model.Deck\", \"stack\": [8, 6, 6, 8, 8, 6, 8, 4]}}, \"winner\": null, \"players\": [{\"uid\": \"67ec7d73-78c7-4831-bdcc-f864dd5c2db4\", \"name\": \"janfic\", \"class\": \"com.janfic.games.computercombat.model.Profile\", \"decks\": [{\"id\": 852571628, \"name\": \"Storage\", \"cards\": [\"2\", 2, \"17\", 2, \"9\", 2, \"20\", 2], \"class\": \"com.janfic.games.computercombat.model.Deck\", \"stack\": [2, 2, 17, 17, 9, 9, 20, 20]}], \"packets\": 0, \"collection\": {\"id\": 0, \"name\": \"Collection\", \"cards\": [], \"stack\": []}, \"activePlayer\": {\"uid\": \"67ec7d73-78c7-4831-bdcc-f864dd5c2db4\", \"class\": \"com.janfic.games.computercombat.model.players.HumanPlayer\"}}, {\"uid\": \"91a6c870-8d1b-4cfd-af0e-0ecb9d625333\", \"name\": \"blakeearth\", \"class\": \"com.janfic.games.computercombat.model.Profile\", \"decks\": [{\"id\": 1132156873, \"name\": \"Malware\", \"cards\": [\"4\", 1, \"6\", 3, \"8\", 4], \"class\": \"com.janfic.games.computercombat.model.Deck\", \"stack\": [4, 6, 6, 6, 8, 8, 8, 8]}], \"packets\": 0, \"collection\": {\"id\": 0, \"name\": \"Collection\", \"cards\": [], \"stack\": []}, \"activePlayer\": {\"uid\": \"91a6c870-8d1b-4cfd-af0e-0ecb9d625333\", \"class\": \"com.janfic.games.computercombat.model.players.HumanPlayer\"}}], \"computers\": {\"class\": \"java.util.HashMap\", \"67ec7d73-78c7-4831-bdcc-f864dd5c2db4\": {\"id\": 0, \"name\": \"Computer\", \"armor\": 0, \"class\": \"com.janfic.games.computercombat.model.Computer\", \"level\": 1, \"magic\": 0, \"attack\": 0, \"health\": 20, \"ability\": {\"id\": 0, \"code\": \"new DrawAbility()\", \"name\": \"Draw Card\", \"class\": \"com.janfic.games.computercombat.model.abilities.DrawAbility\", \"description\": \"Draw a card from you deck\", \"textureName\": \"draw_card\"}, \"matchID\": 0, \"deckSize\": 8, \"maxArmor\": 0, \"ownerUID\": \"owner\", \"maxAttack\": 0, \"maxHealth\": 20, \"collection\": {\"id\": 1, \"name\": \"Computer\", \"path\": \"computer_pack\", \"description\": \"computer\", \"textureName\": \"computer_pack\"}, \"runProgress\": 0, \"textureName\": \"computer\", \"runComponents\": [\"com.janfic.games.computercombat.model.components.CPUComponent\", \"com.janfic.games.computercombat.model.components.NetworkComponent\", \"com.janfic.games.computercombat.model.components.StorageComponent\", \"com.janfic.games.computercombat.model.components.RAMComponent\", \"com.janfic.games.computercombat.model.components.PowerComponent\"], \"runRequirements\": 20}, \"91a6c870-8d1b-4cfd-af0e-0ecb9d625333\": {\"id\": 0, \"name\": \"Computer\", \"armor\": 0, \"class\": \"com.janfic.games.computercombat.model.Computer\", \"level\": 1, \"magic\": 0, \"attack\": 0, \"health\": 20, \"ability\": {\"id\": 0, \"code\": \"new DrawAbility()\", \"name\": \"Draw Card\", \"class\": \"com.janfic.games.computercombat.model.abilities.DrawAbility\", \"description\": \"Draw a card from you deck\", \"textureName\": \"draw_card\"}, \"matchID\": 0, \"deckSize\": 8, \"maxArmor\": 0, \"ownerUID\": \"owner\", \"maxAttack\": 0, \"maxHealth\": 20, \"collection\": {\"id\": 1, \"name\": \"Computer\", \"path\": \"computer_pack\", \"description\": \"computer\", \"textureName\": \"computer_pack\"}, \"runProgress\": 20, \"textureName\": \"computer\", \"runComponents\": [\"com.janfic.games.computercombat.model.components.CPUComponent\", \"com.janfic.games.computercombat.model.components.NetworkComponent\", \"com.janfic.games.computercombat.model.components.StorageComponent\", \"com.janfic.games.computercombat.model.components.RAMComponent\", \"com.janfic.games.computercombat.model.components.PowerComponent\"], \"runRequirements\": 20}}, \"isGameOver\": false, \"activeEntities\": {\"class\": \"java.util.HashMap\", \"67ec7d73-78c7-4831-bdcc-f864dd5c2db4\": [{\"id\": 9, \"name\": \"Flash Drive\", \"armor\": 2, \"class\": \"com.janfic.games.computercombat.model.Software\", \"level\": 1, \"magic\": 1, \"attack\": 2, \"health\": 5, \"ability\": {\"id\": 6, \"code\": \"new SpawnAbility(StorageComponent.class, {state, move -> A:{return 8;}} as StateAnalyzer, []);\", \"name\": \"Gigabyte Flash\", \"class\": \"com.janfic.games.computercombat.model.abilities.SpawnAbility\", \"description\": \"Spawn 8 Storage Components \", \"textureName\": \"gigabyte_flash\", \"componentType\": \"com.janfic.games.computercombat.model.components.StorageComponent\"}, \"matchID\": 934896151, \"maxArmor\": 2, \"ownerUID\": \"67ec7d73-78c7-4831-bdcc-f864dd5c2db4\", \"maxAttack\": 2, \"maxHealth\": 5, \"collection\": {\"id\": 1, \"name\": \"Computer\", \"path\": \"computer_pack\", \"description\": \"A Description\", \"textureName\": \"computer_pack\"}, \"runProgress\": 0, \"textureName\": \"flash_drive\", \"runComponents\": [\"com.janfic.games.computercombat.model.components.StorageComponent\", \"com.janfic.games.computercombat.model.components.PowerComponent\"], \"runRequirements\": 8}], \"91a6c870-8d1b-4cfd-af0e-0ecb9d625333\": []}, \"componentBoard\": \"4241351346344216366214413466211244244552336423612662462256163523\", \"currentPlayerMove\": {\"uid\": \"91a6c870-8d1b-4cfd-af0e-0ecb9d625333\", \"name\": \"blakeearth\", \"decks\": [{\"id\": 1132156873, \"name\": \"Malware\", \"cards\": [\"4\", 1, \"6\", 3, \"8\", 4], \"class\": \"com.janfic.games.computercombat.model.Deck\", \"stack\": [4, 6, 6, 6, 8, 8, 8, 8]}], \"packets\": 0, \"collection\": {\"id\": 0, \"name\": \"Collection\", \"cards\": [], \"stack\": []}, \"activePlayer\": {\"uid\": \"91a6c870-8d1b-4cfd-af0e-0ecb9d625333\", \"class\": \"com.janfic.games.computercombat.model.players.HumanPlayer\"}}}, \"oldState\": {\"decks\": {\"class\": \"java.util.HashMap\", \"67ec7d73-78c7-4831-bdcc-f864dd5c2db4\": {\"id\": 852571628, \"name\": \"Storage\", \"cards\": [\"2\", 2, \"17\", 2, \"9\", 2, \"20\", 2], \"class\": \"com.janfic.games.computercombat.model.Deck\", \"stack\": [9, 20, 2, 17, 17, 2, 20, 9]}, \"91a6c870-8d1b-4cfd-af0e-0ecb9d625333\": {\"id\": 1132156873, \"name\": \"Malware\", \"cards\": [\"4\", 1, \"6\", 3, \"8\", 4], \"class\": \"com.janfic.games.computercombat.model.Deck\", \"stack\": [8, 6, 6, 8, 8, 6, 8, 4]}}, \"winner\": null, \"players\": [{\"uid\": \"67ec7d73-78c7-4831-bdcc-f864dd5c2db4\", \"name\": \"janfic\", \"class\": \"com.janfic.games.computercombat.model.Profile\", \"decks\": [{\"id\": 852571628, \"name\": \"Storage\", \"cards\": [\"2\", 2, \"17\", 2, \"9\", 2, \"20\", 2], \"class\": \"com.janfic.games.computercombat.model.Deck\", \"stack\": [2, 2, 17, 17, 9, 9, 20, 20]}], \"packets\": 76, \"collection\": {\"id\": 0, \"name\": \"Collection\", \"cards\": [], \"stack\": []}, \"activePlayer\": {\"uid\": \"67ec7d73-78c7-4831-bdcc-f864dd5c2db4\", \"class\": \"com.janfic.games.computercombat.model.players.HumanPlayer\"}}, {\"uid\": \"91a6c870-8d1b-4cfd-af0e-0ecb9d625333\", \"name\": \"blakeearth\", \"class\": \"com.janfic.games.computercombat.model.Profile\", \"decks\": [{\"id\": 1132156873, \"name\": \"Malware\", \"cards\": [\"4\", 1, \"6\", 3, \"8\", 4], \"class\": \"com.janfic.games.computercombat.model.Deck\", \"stack\": [4, 6, 6, 6, 8, 8, 8, 8]}], \"packets\": 114, \"collection\": {\"id\": 0, \"name\": \"Collection\", \"cards\": [], \"stack\": []}, \"activePlayer\": {\"uid\": \"91a6c870-8d1b-4cfd-af0e-0ecb9d625333\", \"class\": \"com.janfic.games.computercombat.model.players.HumanPlayer\"}}], \"computers\": {\"class\": \"java.util.HashMap\", \"67ec7d73-78c7-4831-bdcc-f864dd5c2db4\": {\"id\": 0, \"name\": \"Computer\", \"armor\": 0, \"class\": \"com.janfic.games.computercombat.model.Computer\", \"level\": 1, \"magic\": 0, \"attack\": 0, \"health\": 20, \"ability\": {\"id\": 0, \"code\": \"new DrawAbility()\", \"name\": \"Draw Card\", \"class\": \"com.janfic.games.computercombat.model.abilities.DrawAbility\", \"description\": \"Draw a card from you deck\", \"textureName\": \"draw_card\"}, \"matchID\": 0, \"deckSize\": 8, \"maxArmor\": 0, \"ownerUID\": \"owner\", \"maxAttack\": 0, \"maxHealth\": 20, \"collection\": {\"id\": 1, \"name\": \"Computer\", \"path\": \"computer_pack\", \"description\": \"computer\", \"textureName\": \"computer_pack\"}, \"runProgress\": 20, \"textureName\": \"computer\", \"runComponents\": [\"com.janfic.games.computercombat.model.components.CPUComponent\", \"com.janfic.games.computercombat.model.components.NetworkComponent\", \"com.janfic.games.computercombat.model.components.StorageComponent\", \"com.janfic.games.computercombat.model.components.RAMComponent\", \"com.janfic.games.computercombat.model.components.PowerComponent\"], \"runRequirements\": 20}, \"91a6c870-8d1b-4cfd-af0e-0ecb9d625333\": {\"id\": 0, \"name\": \"Computer\", \"armor\": 0, \"class\": \"com.janfic.games.computercombat.model.Computer\", \"level\": 1, \"magic\": 0, \"attack\": 0, \"health\": 20, \"ability\": {\"id\": 0, \"code\": \"new DrawAbility()\", \"name\": \"Draw Card\", \"class\": \"com.janfic.games.computercombat.model.abilities.DrawAbility\", \"description\": \"Draw a card from you deck\", \"textureName\": \"draw_card\"}, \"matchID\": 0, \"deckSize\": 8, \"maxArmor\": 0, \"ownerUID\": \"owner\", \"maxAttack\": 0, \"maxHealth\": 20, \"collection\": {\"id\": 1, \"name\": \"Computer\", \"path\": \"computer_pack\", \"description\": \"computer\", \"textureName\": \"computer_pack\"}, \"runProgress\": 20, \"textureName\": \"computer\", \"runComponents\": [\"com.janfic.games.computercombat.model.components.CPUComponent\", \"com.janfic.games.computercombat.model.components.NetworkComponent\", \"com.janfic.games.computercombat.model.components.StorageComponent\", \"com.janfic.games.computercombat.model.components.RAMComponent\", \"com.janfic.games.computercombat.model.components.PowerComponent\"], \"runRequirements\": 20}}, \"isGameOver\": false, \"activeEntities\": {\"class\": \"java.util.HashMap\", \"67ec7d73-78c7-4831-bdcc-f864dd5c2db4\": [], \"91a6c870-8d1b-4cfd-af0e-0ecb9d625333\": []}, \"componentBoard\": \"4241351346344216366214413466211244244552336423612662462256163523\", \"currentPlayerMove\": {\"uid\": \"67ec7d73-78c7-4831-bdcc-f864dd5c2db4\", \"name\": \"janfic\", \"decks\": [{\"id\": 852571628, \"name\": \"Storage\", \"cards\": [\"2\", 2, \"17\", 2, \"9\", 2, \"20\", 2], \"class\": \"com.janfic.games.computercombat.model.Deck\", \"stack\": [2, 2, 17, 17, 9, 9, 20, 20]}], \"packets\": 76, \"collection\": {\"id\": 0, \"name\": \"Collection\", \"cards\": [], \"stack\": []}, \"activePlayer\": {\"uid\": \"67ec7d73-78c7-4831-bdcc-f864dd5c2db4\", \"class\": \"com.janfic.games.computercombat.model.players.HumanPlayer\"}}}, \"animations\": [{\"class\": \"com.janfic.games.computercombat.model.animations.ConsumeProgressAnimation\", \"software\": [{\"id\": 0, \"name\": \"Computer\", \"armor\": 0, \"class\": \"com.janfic.games.computercombat.model.Computer\", \"level\": 1, \"magic\": 0, \"attack\": 0, \"health\": 20, \"ability\": {\"id\": 0, \"code\": \"new DrawAbility()\", \"name\": \"Draw Card\", \"class\": \"com.janfic.games.computercombat.model.abilities.DrawAbility\", \"description\": \"Draw a card from you deck\", \"textureName\": \"draw_card\"}, \"matchID\": 0, \"deckSize\": 8, \"maxArmor\": 0, \"ownerUID\": \"owner\", \"maxAttack\": 0, \"maxHealth\": 20, \"collection\": {\"id\": 1, \"name\": \"Computer\", \"path\": \"computer_pack\", \"description\": \"computer\", \"textureName\": \"computer_pack\"}, \"runProgress\": 20, \"textureName\": \"computer\", \"runComponents\": [\"com.janfic.games.computercombat.model.components.CPUComponent\", \"com.janfic.games.computercombat.model.components.NetworkComponent\", \"com.janfic.games.computercombat.model.components.StorageComponent\", \"com.janfic.games.computercombat.model.components.RAMComponent\", \"com.janfic.games.computercombat.model.components.PowerComponent\"], \"runRequirements\": 20}], \"playerUID\": \"67ec7d73-78c7-4831-bdcc-f864dd5c2db4\"}, {\"class\": \"com.janfic.games.computercombat.model.animations.DrawAnimation\", \"playerUID\": \"67ec7d73-78c7-4831-bdcc-f864dd5c2db4\", \"newSoftware\": [{\"id\": 9, \"name\": \"Flash Drive\", \"armor\": 2, \"class\": \"com.janfic.games.computercombat.model.Software\", \"level\": 1, \"magic\": 1, \"attack\": 2, \"health\": 5, \"ability\": {\"id\": 6, \"code\": \"new SpawnAbility(StorageComponent.class, {state, move -> A:{return 8;}} as StateAnalyzer, []);\", \"name\": \"Gigabyte Flash\", \"class\": \"com.janfic.games.computercombat.model.abilities.SpawnAbility\", \"description\": \"Spawn 8 Storage Components \", \"textureName\": \"gigabyte_flash\", \"componentType\": \"com.janfic.games.computercombat.model.components.StorageComponent\"}, \"matchID\": 934896151, \"maxArmor\": 2, \"ownerUID\": \"67ec7d73-78c7-4831-bdcc-f864dd5c2db4\", \"maxAttack\": 2, \"maxHealth\": 5, \"collection\": {\"id\": 1, \"name\": \"Computer\", \"path\": \"computer_pack\", \"description\": \"A Description\", \"textureName\": \"computer_pack\"}, \"runProgress\": 0, \"textureName\": \"flash_drive\", \"runComponents\": [\"com.janfic.games.computercombat.model.components.StorageComponent\", \"com.janfic.games.computercombat.model.components.PowerComponent\"], \"runRequirements\": 8}]}]}]');
/*!40000 ALTER TABLE `move_results` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `player`
--

DROP TABLE IF EXISTS `player`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `player` (
  `id` int NOT NULL,
  `name` varchar(45) NOT NULL,
  `deck_id` int NOT NULL,
  `bot` longtext NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `player`
--

LOCK TABLES `player` WRITE;
/*!40000 ALTER TABLE `player` DISABLE KEYS */;
/*!40000 ALTER TABLE `player` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `profile`
--

LOCK TABLES `profile` WRITE;
/*!40000 ALTER TABLE `profile` DISABLE KEYS */;
INSERT INTO `profile` VALUES ('02ede276-8d60-4313-9df3-41bcda1b9495','newtest','greyjan.fic@gmail.com',0000000000),('49646eae-d1ef-4067-9360-de1ad38f5993','greyjan','janfc6@gmail.com',0000000339),('4ea014e6-c193-4240-beda-938502d688d5','greytest','greyjan.fic@gmail.com',0000000000),('67ec7d73-78c7-4831-bdcc-f864dd5c2db4','janfic','jan.fic18@ncf.edu',0000000055),('84c5a288-43f9-4f4f-a117-43b54b092c52','othertest','greyjan.fic@gmail.com',0000000000),('91a6c870-8d1b-4cfd-af0e-0ecb9d625333','blakeearth','isaac@blake.earth',0000000114),('982b987e-d0fe-46b9-91ab-6dd1564dc203','testtest','greyjan.fic@gmail.com',0000000000),('botUID','null','null',0000000000),('f07cdf89-8b4b-4d7a-b44f-aac78c978f54','Noetherian','mlepinski@gmail.com',0000000000);
/*!40000 ALTER TABLE `profile` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `profile_owns_card`
--

DROP TABLE IF EXISTS `profile_owns_card`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `profile_owns_card` (
  `profile_id` varchar(45) NOT NULL,
  `card_id` int NOT NULL,
  `amount` int unsigned NOT NULL DEFAULT '1',
  PRIMARY KEY (`profile_id`,`card_id`),
  KEY `fk_Profile_has_Card_Profile1_idx` (`profile_id`),
  KEY `fk_Profile_has_Card_Card1_idx` (`card_id`),
  CONSTRAINT `fk_Profile_has_Card_Card1` FOREIGN KEY (`card_id`) REFERENCES `card` (`id`),
  CONSTRAINT `fk_Profile_has_Card_Profile1` FOREIGN KEY (`profile_id`) REFERENCES `profile` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `profile_owns_card`
--

LOCK TABLES `profile_owns_card` WRITE;
/*!40000 ALTER TABLE `profile_owns_card` DISABLE KEYS */;
INSERT INTO `profile_owns_card` VALUES ('02ede276-8d60-4313-9df3-41bcda1b9495',1,1),('02ede276-8d60-4313-9df3-41bcda1b9495',2,1),('02ede276-8d60-4313-9df3-41bcda1b9495',3,1),('02ede276-8d60-4313-9df3-41bcda1b9495',4,1),('02ede276-8d60-4313-9df3-41bcda1b9495',5,1),('02ede276-8d60-4313-9df3-41bcda1b9495',6,1),('02ede276-8d60-4313-9df3-41bcda1b9495',7,1),('02ede276-8d60-4313-9df3-41bcda1b9495',8,1),('02ede276-8d60-4313-9df3-41bcda1b9495',9,1),('02ede276-8d60-4313-9df3-41bcda1b9495',10,1),('02ede276-8d60-4313-9df3-41bcda1b9495',11,1),('02ede276-8d60-4313-9df3-41bcda1b9495',12,1),('02ede276-8d60-4313-9df3-41bcda1b9495',13,1),('02ede276-8d60-4313-9df3-41bcda1b9495',14,1),('02ede276-8d60-4313-9df3-41bcda1b9495',15,1),('02ede276-8d60-4313-9df3-41bcda1b9495',16,1),('02ede276-8d60-4313-9df3-41bcda1b9495',17,1),('02ede276-8d60-4313-9df3-41bcda1b9495',18,1),('02ede276-8d60-4313-9df3-41bcda1b9495',19,1),('02ede276-8d60-4313-9df3-41bcda1b9495',20,1),('49646eae-d1ef-4067-9360-de1ad38f5993',2,1),('49646eae-d1ef-4067-9360-de1ad38f5993',3,1),('49646eae-d1ef-4067-9360-de1ad38f5993',4,1),('49646eae-d1ef-4067-9360-de1ad38f5993',5,1),('49646eae-d1ef-4067-9360-de1ad38f5993',6,1),('49646eae-d1ef-4067-9360-de1ad38f5993',7,1),('49646eae-d1ef-4067-9360-de1ad38f5993',8,1),('49646eae-d1ef-4067-9360-de1ad38f5993',9,1),('49646eae-d1ef-4067-9360-de1ad38f5993',10,1),('49646eae-d1ef-4067-9360-de1ad38f5993',11,1),('49646eae-d1ef-4067-9360-de1ad38f5993',13,1),('49646eae-d1ef-4067-9360-de1ad38f5993',14,1),('49646eae-d1ef-4067-9360-de1ad38f5993',15,1),('49646eae-d1ef-4067-9360-de1ad38f5993',16,1),('49646eae-d1ef-4067-9360-de1ad38f5993',17,1),('49646eae-d1ef-4067-9360-de1ad38f5993',18,1),('49646eae-d1ef-4067-9360-de1ad38f5993',19,1),('49646eae-d1ef-4067-9360-de1ad38f5993',20,1),('4ea014e6-c193-4240-beda-938502d688d5',1,1),('4ea014e6-c193-4240-beda-938502d688d5',2,1),('4ea014e6-c193-4240-beda-938502d688d5',3,1),('4ea014e6-c193-4240-beda-938502d688d5',4,1),('4ea014e6-c193-4240-beda-938502d688d5',5,1),('4ea014e6-c193-4240-beda-938502d688d5',6,1),('4ea014e6-c193-4240-beda-938502d688d5',7,1),('4ea014e6-c193-4240-beda-938502d688d5',8,1),('4ea014e6-c193-4240-beda-938502d688d5',9,1),('4ea014e6-c193-4240-beda-938502d688d5',10,1),('4ea014e6-c193-4240-beda-938502d688d5',11,1),('4ea014e6-c193-4240-beda-938502d688d5',12,1),('4ea014e6-c193-4240-beda-938502d688d5',13,1),('4ea014e6-c193-4240-beda-938502d688d5',14,1),('4ea014e6-c193-4240-beda-938502d688d5',15,1),('4ea014e6-c193-4240-beda-938502d688d5',16,1),('4ea014e6-c193-4240-beda-938502d688d5',17,1),('4ea014e6-c193-4240-beda-938502d688d5',18,1),('4ea014e6-c193-4240-beda-938502d688d5',19,1),('4ea014e6-c193-4240-beda-938502d688d5',20,1),('67ec7d73-78c7-4831-bdcc-f864dd5c2db4',1,2),('67ec7d73-78c7-4831-bdcc-f864dd5c2db4',2,1),('67ec7d73-78c7-4831-bdcc-f864dd5c2db4',3,1),('67ec7d73-78c7-4831-bdcc-f864dd5c2db4',4,1),('67ec7d73-78c7-4831-bdcc-f864dd5c2db4',5,1),('67ec7d73-78c7-4831-bdcc-f864dd5c2db4',6,2),('67ec7d73-78c7-4831-bdcc-f864dd5c2db4',7,1),('67ec7d73-78c7-4831-bdcc-f864dd5c2db4',8,2),('67ec7d73-78c7-4831-bdcc-f864dd5c2db4',9,2),('67ec7d73-78c7-4831-bdcc-f864dd5c2db4',10,2),('67ec7d73-78c7-4831-bdcc-f864dd5c2db4',11,7),('67ec7d73-78c7-4831-bdcc-f864dd5c2db4',12,1),('67ec7d73-78c7-4831-bdcc-f864dd5c2db4',13,3),('67ec7d73-78c7-4831-bdcc-f864dd5c2db4',14,3),('67ec7d73-78c7-4831-bdcc-f864dd5c2db4',15,1),('67ec7d73-78c7-4831-bdcc-f864dd5c2db4',16,1),('67ec7d73-78c7-4831-bdcc-f864dd5c2db4',17,3),('67ec7d73-78c7-4831-bdcc-f864dd5c2db4',18,1),('67ec7d73-78c7-4831-bdcc-f864dd5c2db4',19,2),('67ec7d73-78c7-4831-bdcc-f864dd5c2db4',20,1),('84c5a288-43f9-4f4f-a117-43b54b092c52',1,1),('84c5a288-43f9-4f4f-a117-43b54b092c52',2,1),('84c5a288-43f9-4f4f-a117-43b54b092c52',3,1),('84c5a288-43f9-4f4f-a117-43b54b092c52',4,1),('84c5a288-43f9-4f4f-a117-43b54b092c52',5,1),('84c5a288-43f9-4f4f-a117-43b54b092c52',6,1),('84c5a288-43f9-4f4f-a117-43b54b092c52',7,1),('84c5a288-43f9-4f4f-a117-43b54b092c52',8,1),('84c5a288-43f9-4f4f-a117-43b54b092c52',9,1),('84c5a288-43f9-4f4f-a117-43b54b092c52',10,1),('84c5a288-43f9-4f4f-a117-43b54b092c52',11,1),('84c5a288-43f9-4f4f-a117-43b54b092c52',12,1),('84c5a288-43f9-4f4f-a117-43b54b092c52',13,1),('84c5a288-43f9-4f4f-a117-43b54b092c52',14,1),('84c5a288-43f9-4f4f-a117-43b54b092c52',15,1),('84c5a288-43f9-4f4f-a117-43b54b092c52',16,1),('84c5a288-43f9-4f4f-a117-43b54b092c52',17,1),('84c5a288-43f9-4f4f-a117-43b54b092c52',18,1),('84c5a288-43f9-4f4f-a117-43b54b092c52',19,1),('84c5a288-43f9-4f4f-a117-43b54b092c52',20,1),('91a6c870-8d1b-4cfd-af0e-0ecb9d625333',1,1),('91a6c870-8d1b-4cfd-af0e-0ecb9d625333',2,1),('91a6c870-8d1b-4cfd-af0e-0ecb9d625333',3,1),('91a6c870-8d1b-4cfd-af0e-0ecb9d625333',4,1),('91a6c870-8d1b-4cfd-af0e-0ecb9d625333',5,1),('91a6c870-8d1b-4cfd-af0e-0ecb9d625333',6,1),('91a6c870-8d1b-4cfd-af0e-0ecb9d625333',7,1),('91a6c870-8d1b-4cfd-af0e-0ecb9d625333',8,1),('91a6c870-8d1b-4cfd-af0e-0ecb9d625333',9,1),('91a6c870-8d1b-4cfd-af0e-0ecb9d625333',10,1),('91a6c870-8d1b-4cfd-af0e-0ecb9d625333',13,1),('982b987e-d0fe-46b9-91ab-6dd1564dc203',1,1),('982b987e-d0fe-46b9-91ab-6dd1564dc203',2,1),('982b987e-d0fe-46b9-91ab-6dd1564dc203',3,1),('982b987e-d0fe-46b9-91ab-6dd1564dc203',4,1),('982b987e-d0fe-46b9-91ab-6dd1564dc203',5,1),('982b987e-d0fe-46b9-91ab-6dd1564dc203',6,1),('982b987e-d0fe-46b9-91ab-6dd1564dc203',7,1),('982b987e-d0fe-46b9-91ab-6dd1564dc203',8,1),('982b987e-d0fe-46b9-91ab-6dd1564dc203',9,1),('982b987e-d0fe-46b9-91ab-6dd1564dc203',10,1),('982b987e-d0fe-46b9-91ab-6dd1564dc203',11,1),('982b987e-d0fe-46b9-91ab-6dd1564dc203',12,1),('982b987e-d0fe-46b9-91ab-6dd1564dc203',13,1),('982b987e-d0fe-46b9-91ab-6dd1564dc203',14,1),('982b987e-d0fe-46b9-91ab-6dd1564dc203',15,1),('982b987e-d0fe-46b9-91ab-6dd1564dc203',16,1),('982b987e-d0fe-46b9-91ab-6dd1564dc203',17,1),('982b987e-d0fe-46b9-91ab-6dd1564dc203',18,1),('982b987e-d0fe-46b9-91ab-6dd1564dc203',19,1),('982b987e-d0fe-46b9-91ab-6dd1564dc203',20,1),('f07cdf89-8b4b-4d7a-b44f-aac78c978f54',1,1),('f07cdf89-8b4b-4d7a-b44f-aac78c978f54',2,1),('f07cdf89-8b4b-4d7a-b44f-aac78c978f54',3,1),('f07cdf89-8b4b-4d7a-b44f-aac78c978f54',4,1),('f07cdf89-8b4b-4d7a-b44f-aac78c978f54',5,1),('f07cdf89-8b4b-4d7a-b44f-aac78c978f54',6,1),('f07cdf89-8b4b-4d7a-b44f-aac78c978f54',7,1),('f07cdf89-8b4b-4d7a-b44f-aac78c978f54',8,1),('f07cdf89-8b4b-4d7a-b44f-aac78c978f54',9,1),('f07cdf89-8b4b-4d7a-b44f-aac78c978f54',10,1),('f07cdf89-8b4b-4d7a-b44f-aac78c978f54',11,1),('f07cdf89-8b4b-4d7a-b44f-aac78c978f54',12,1),('f07cdf89-8b4b-4d7a-b44f-aac78c978f54',13,1),('f07cdf89-8b4b-4d7a-b44f-aac78c978f54',14,1),('f07cdf89-8b4b-4d7a-b44f-aac78c978f54',15,1),('f07cdf89-8b4b-4d7a-b44f-aac78c978f54',16,1),('f07cdf89-8b4b-4d7a-b44f-aac78c978f54',17,1),('f07cdf89-8b4b-4d7a-b44f-aac78c978f54',18,1),('f07cdf89-8b4b-4d7a-b44f-aac78c978f54',19,1),('f07cdf89-8b4b-4d7a-b44f-aac78c978f54',20,1);
/*!40000 ALTER TABLE `profile_owns_card` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `run_requirements`
--

DROP TABLE IF EXISTS `run_requirements`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `run_requirements` (
  `card_id` int NOT NULL,
  `component_id` int NOT NULL,
  PRIMARY KEY (`card_id`,`component_id`),
  KEY `fk_Card_has_Components_Components1_idx` (`component_id`),
  KEY `fk_Card_has_Components_Card1_idx` (`card_id`),
  CONSTRAINT `fk_Card_has_Components_Card1` FOREIGN KEY (`card_id`) REFERENCES `card` (`id`),
  CONSTRAINT `fk_Card_has_Components_Components1` FOREIGN KEY (`component_id`) REFERENCES `components` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `run_requirements`
--

LOCK TABLES `run_requirements` WRITE;
/*!40000 ALTER TABLE `run_requirements` DISABLE KEYS */;
INSERT INTO `run_requirements` VALUES (0,1),(1,1),(3,1),(4,1),(5,1),(6,1),(7,1),(8,1),(14,1),(15,1),(18,1),(19,1),(0,2),(1,2),(3,2),(8,2),(18,2),(0,3),(2,3),(3,3),(9,3),(12,3),(13,3),(17,3),(0,4),(4,4),(6,4),(7,4),(8,4),(10,4),(0,6),(9,6),(10,6),(11,6),(13,6),(14,6),(15,6),(16,6),(20,6);
/*!40000 ALTER TABLE `run_requirements` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Final view structure for view `card_info`
--

/*!50001 DROP VIEW IF EXISTS `card_info`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`admin`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `card_info` AS select `card`.`id` AS `card_id`,`card`.`name` AS `name`,`card`.`maxAttack` AS `maxAttack`,`card`.`maxHealth` AS `maxHealth`,`card`.`maxDefense` AS `maxDefense`,`card`.`runRequirements` AS `runRequirements`,`card`.`level` AS `level`,`card`.`textureName` AS `textureName`,`card`.`rarity` AS `rarity`,`ability`.`id` AS `ability_id`,`ability`.`description` AS `description`,`ability`.`code` AS `code`,`ability`.`name` AS `ability_name`,`ability`.`textureName` AS `ability_textureName`,`components`.`id` AS `component_id`,`collection`.`id` AS `collection_id` from ((((`card` join `ability` on((`card`.`ability_id` = `ability`.`id`))) join `run_requirements` on((`card`.`id` = `run_requirements`.`card_id`))) join `components` on((`components`.`id` = `run_requirements`.`component_id`))) join `collection` on((`card`.`collection_id` = `collection`.`id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `card_wins`
--

/*!50001 DROP VIEW IF EXISTS `card_wins`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`admin`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `card_wins` AS select `deck_wins`.`match_id` AS `match_id`,`deck_wins`.`deck_id` AS `deck_id`,`deck_has_card`.`card_id` AS `card_id`,`deck_wins`.`did_win` AS `did_win` from (`deck_wins` join `deck_has_card` on((`deck_wins`.`deck_id` = `deck_has_card`.`deck_id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `deck_wins`
--

/*!50001 DROP VIEW IF EXISTS `deck_wins`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`admin`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `deck_wins` AS select `match`.`id` AS `match_id`,`deck`.`id` AS `deck_id`,if((((`match`.`deck1_id` = `deck`.`id`) and (`match`.`winner` = 0)) or ((`match`.`deck2_id` = `deck`.`id`) and (`match`.`winner` = 1))),1,0) AS `did_win` from (`match` join `deck` on(((`match`.`deck1_id` = `deck`.`id`) or (`match`.`deck2_id` = `deck`.`id`)))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-03-04 15:46:39
