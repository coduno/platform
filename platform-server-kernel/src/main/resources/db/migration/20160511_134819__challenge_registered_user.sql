--
-- Table structure for table `challenge_registered_user`
--

DROP TABLE IF EXISTS `challenge_registered_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `challenge_registered_user` (
  `challenge_id` binary(16) NOT NULL,
  `user_id` binary(16) NOT NULL,
  PRIMARY KEY (`challenge_id`,`user_id`),
  KEY `FK12t4facac13vcxcahha` (`user_id`),
  CONSTRAINT `FKgax4fasdhczyvcx123a` FOREIGN KEY (`challenge_id`) REFERENCES `challenge` (`id`),
  CONSTRAINT `FK12t4facac13vcxcahha` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;