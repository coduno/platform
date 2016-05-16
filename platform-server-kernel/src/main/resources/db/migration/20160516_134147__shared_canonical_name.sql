--
-- Table structure for table `canonical_name`
--

DROP TABLE IF EXISTS `canonical_name`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `canonical_name` (
  `value` varchar(255) NOT NULL UNIQUE,
  `user_id` binary(16),
  `team_id` binary(16),
  `organization_id` binary(16),
  PRIMARY KEY (`value`),
  KEY `FK_USER_CANONICAL_NAME` (`user_id`),
  KEY `FK_TEAM_CANONICAL_NAME` (`team_id`),
  KEY `FK_ORGANIZATION_CANONICAL_NAME` (`organization_id`),
  CONSTRAINT `FK_USER_CANONICAL_NAME` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_TEAM_CANONICAL_NAME` FOREIGN KEY (`team_id`) REFERENCES `team` (`id`),
  CONSTRAINT `FK_ORGANIZATION_CANONICAL_NAME` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

INSERT INTO `canonical_name`(user_id, value) SELECT id, username FROM `user`;
INSERT INTO `canonical_name`(team_id, value) SELECT id, canonical_name FROM `team`;
INSERT INTO `canonical_name`(organization_id, value) SELECT id, nick FROM `organization`;

ALTER TABLE `user` ADD CONSTRAINT `FK_crrt5fvw67gi6xa7sad` FOREIGN KEY(`username`) REFERENCES `canonical_name`(`value`);
ALTER TABLE `team` ADD CONSTRAINT `FK_xrt5fv4gi6bvnxa7s8d` FOREIGN KEY(`canonical_name`) REFERENCES `canonical_name`(`value`);
ALTER TABLE `organization` ADD CONSTRAINT `FK_9pt5fv4gi68anxa7s8d` FOREIGN KEY(`nick`) REFERENCES `canonical_name`(`value`);