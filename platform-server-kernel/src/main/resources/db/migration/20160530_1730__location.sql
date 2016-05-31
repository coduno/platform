--
-- Table structure for table `location`
--

DROP TABLE IF EXISTS `location`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `location` (
  `id`   BINARY(16) NOT NULL,
  `name` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `challenge_location`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `challenge_location` (
  `challenge_id` BINARY(16) NOT NULL,
  `location_id`  BINARY(16) NOT NULL,
  PRIMARY KEY (`challenge_id`, `location_id`),
  KEY `FK83xtp0mmtgycpnjda86517izk` (`location_id`),
  KEY `FKbbmq10mog5zzzln71iyd4byd3` (`challenge_id`),
  CONSTRAINT `FK83xtp0mmtgycpnjda86517izk` FOREIGN KEY (`location_id`) REFERENCES `location` (`id`),
  CONSTRAINT `FKbbmq10mog5zzzln71iyd4byd3` FOREIGN KEY (`challenge_id`) REFERENCES `challenge` (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


