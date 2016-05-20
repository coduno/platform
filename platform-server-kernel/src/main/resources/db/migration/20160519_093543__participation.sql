--
-- Table structure for table `participation`
--

DROP TABLE IF EXISTS `participation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `participation` (
  `id` binary(16) NOT NULL,
  `challenge_id` binary(16) NOT NULL,
  `user_id` binary(16),
  `team_id` binary(16),
  PRIMARY KEY (`id`),
  KEY `FK_PARTICIPATION_CHALLENGE` (`challenge_id`),
  KEY `FK_PARTICIPATION_USER` (`user_id`),
  KEY `FK_PARTICIPATION_TEAM` (`team_id`),
  CONSTRAINT `FK_PARTICIPATION_CHALLENGE` FOREIGN KEY (`challenge_id`) REFERENCES `challenge` (`id`),
  CONSTRAINT `FK_PARTICIPATION_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_PARTICIPATION_TEAM` FOREIGN KEY (`team_id`) REFERENCES `team` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

INSERT INTO `participation`(id, challenge_id, user_id) SELECT uuid(), challenge_id, user_id from challenge_registered_user;
DROP TABLE IF EXISTS `challenge_registered_user`;