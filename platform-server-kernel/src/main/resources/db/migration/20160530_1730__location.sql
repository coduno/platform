--
-- Table structure for table `location`
--

DROP TABLE IF EXISTS `location`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `location` (
  `id`        BINARY(16)   NOT NULL,
  `name`      VARCHAR(255) NOT NULL,
  `place_id`  VARCHAR(255) DEFAULT NULL,
  `latitude`  VARCHAR(255) DEFAULT NULL,
  `longitude` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY (`name`),
  UNIQUE KEY (`place_id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `challenge_location`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `challenge_location` (
  `challenges_id` BINARY(16) NOT NULL,
  `locations_id`  BINARY(16) NOT NULL,
  PRIMARY KEY (`challenges_id`, `locations_id`),
  KEY `FK83xtp0mmtgycpnjda86517izk` (`locations_id`),
  KEY `FKbbmq10mog5zzzln71iyd4byd3` (`challenges_id`),
  CONSTRAINT `FK83xtp0mmtgycpnjda86517izk` FOREIGN KEY (`locations_id`) REFERENCES `location` (`id`),
  CONSTRAINT `FKbbmq10mog5zzzln71iyd4byd3` FOREIGN KEY (`challenges_id`) REFERENCES `challenge` (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

ALTER TABLE `participation` ADD `location_id` BINARY(16);
ALTER TABLE `participation` ADD KEY `FKbbmq18iuz5zzpqln98iyd4bp9l` (`location_id`);
ALTER TABLE `participation` ADD CONSTRAINT `FKbbmq18iuz5zzpqln98iyd4bp9l` FOREIGN KEY (`location_id`) REFERENCES `location` (`id`);


