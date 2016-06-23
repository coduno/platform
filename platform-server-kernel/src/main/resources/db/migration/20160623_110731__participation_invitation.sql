--
-- Table structure for table `participation_invitation`
--

DROP TABLE IF EXISTS `participation_invitation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `participation_invitation` (
  `user_id` binary(16) NOT NULL,
  `challenge_id` binary(16) NOT NULL,
  PRIMARY KEY (`challenge_id`,`user_id`),
  KEY `FKt2o14okf6h9o5oh0mqrwsp5il` (`user_id`),
  CONSTRAINT `FK4mb2v2qgd8hoxtsbemflylm5` FOREIGN KEY (`challenge_id`) REFERENCES `challenge` (`id`),
  CONSTRAINT `FKt2o14okf6h9o5oh0mqrwsp5il` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `participation_invitation_email`
--

DROP TABLE IF EXISTS `participation_invitation_email`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `participation_invitation_email` (
  `participation_invitation_challenge_id` binary(16) NOT NULL,
  `participation_invitation_user_id` binary(16) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  KEY `FK6h72og9nkr60n0w7yalli4snn` (`participation_invitation_challenge_id`,`participation_invitation_user_id`),
  CONSTRAINT `FK6h72og9nkr60n0w7yalli4snn` FOREIGN KEY (`participation_invitation_challenge_id`, `participation_invitation_user_id`) REFERENCES `participation_invitation` (`challenge_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



/*!40101 SET character_set_client = @saved_cs_client */;