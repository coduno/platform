-- MySQL dump 10.16  Distrib 10.1.13-MariaDB, for Linux (x86_64)
--
-- Host: localhost    Database: test_platform
-- ------------------------------------------------------
-- Server version	10.1.13-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `challenge`
--

DROP TABLE IF EXISTS `challenge`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `challenge` (
  `id` binary(16) NOT NULL,
  `canonical_name` varchar(255) NOT NULL,
  `end_date` datetime DEFAULT NULL,
  `invite_only` bit(1) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `start_date` datetime DEFAULT NULL,
  `challenge_template_id` binary(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKhcqo7gruoaecf5mmkloq5ldct` (`challenge_template_id`),
  CONSTRAINT `FKhcqo7gruoaecf5mmkloq5ldct` FOREIGN KEY (`challenge_template_id`) REFERENCES `challenge_template` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `challenge_template`
--

DROP TABLE IF EXISTS `challenge_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `challenge_template` (
  `id` binary(16) NOT NULL,
  `description` longtext NOT NULL,
  `duration` bigint(20) NOT NULL,
  `instructions` longtext NOT NULL,
  `name` varchar(255) NOT NULL,
  `endpoint_id` binary(16) DEFAULT NULL,
  `organization_id` binary(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKd5j69ht6utnlqcwat8q6nk8wq` (`endpoint_id`),
  KEY `FKtayntwqmm8ntlot76mmoonfio` (`organization_id`),
  CONSTRAINT `FKd5j69ht6utnlqcwat8q6nk8wq` FOREIGN KEY (`endpoint_id`) REFERENCES `endpoint` (`id`),
  CONSTRAINT `FKtayntwqmm8ntlot76mmoonfio` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `challenge_template_task`
--

DROP TABLE IF EXISTS `challenge_template_task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `challenge_template_task` (
  `challenge_template_id` binary(16) NOT NULL,
  `task_id` binary(16) NOT NULL,
  `tasks_ORDER` int(11) NOT NULL,
  PRIMARY KEY (`task_id`,`tasks_ORDER`),
  KEY `FKr1xioakulcpwam6vtrhdv3hx8` (`task_id`),
  CONSTRAINT `FKpm5h2fhdqxbqcyoboajp8sm2l` FOREIGN KEY (`challenge_template_id`) REFERENCES `challenge_template` (`id`),
  CONSTRAINT `FKr1xioakulcpwam6vtrhdv3hx8` FOREIGN KEY (`task_id`) REFERENCES `task` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `challenge_user`
--

DROP TABLE IF EXISTS `challenge_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `challenge_user` (
  `invited_challenge_id` binary(16) NOT NULL,
  `invited_user_id` binary(16) NOT NULL,
  PRIMARY KEY (`invited_challenge_id`,`invited_user_id`),
  KEY `FK7oa88321e99va1n7a20jt0y22` (`invited_user_id`),
  CONSTRAINT `FK4n330drdqh3fvuu6oaeeaubjw` FOREIGN KEY (`invited_challenge_id`) REFERENCES `challenge` (`id`),
  CONSTRAINT `FK7oa88321e99va1n7a20jt0y22` FOREIGN KEY (`invited_user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `coderprofile`
--

DROP TABLE IF EXISTS `coder_profile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `coder_profile` (
  `user_id` binary(255) NOT NULL,
  `last_updated` datetime DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `coderprofile_skillmap`
--

DROP TABLE IF EXISTS `coder_profile_skill_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `coder_profile_skill_map` (
  `coder_profile_user_id` binary(255) NOT NULL,
  `skill_map` double DEFAULT NULL,
  `skill_map_KEY` int(11) NOT NULL,
  PRIMARY KEY (`coder_profile_user_id`,`skill_map_KEY`),
  CONSTRAINT `FKqdt1d7fvegqwl6vfej9qs61kw` FOREIGN KEY (`coder_profile_user_id`) REFERENCES `coder_profile` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `endpoint`
--

DROP TABLE IF EXISTS `endpoint`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `endpoint` (
  `id` binary(16) NOT NULL,
  `component` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_94ygu9u957rtltu5sxjonfejc` (`component`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `invitation`
--

DROP TABLE IF EXISTS `invitation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `invitation` (
  `token` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `expire` datetime NOT NULL,
  `challenge_id` binary(16) NOT NULL,
  PRIMARY KEY (`token`),
  KEY `FKp4ua0egpb823cstofc0a983w1` (`challenge_id`),
  CONSTRAINT `FKp4ua0egpb823cstofc0a983w1` FOREIGN KEY (`challenge_id`) REFERENCES `challenge` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `language`
--

DROP TABLE IF EXISTS `language`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `language` (
  `id` binary(16) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `tag` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `organization`
--

DROP TABLE IF EXISTS `organization`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `organization` (
  `id` binary(16) NOT NULL,
  `name` varchar(255) NOT NULL,
  `nick` varchar(40) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_oyl3rdscpk4622gyovnhddo6k` (`nick`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `organization_member`
--

DROP TABLE IF EXISTS `organization_membership`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `organization_membership` (
  `admin` bit(1) NOT NULL,
  `created` datetime NOT NULL,
  `user_id` binary(16) NOT NULL,
  `organization_id` binary(16) NOT NULL,
  PRIMARY KEY (`organization_id`,`user_id`),
  KEY `FKs7b5ea4u5y4ds9vebvq7pxpa8` (`user_id`),
  CONSTRAINT `FKkta3960iv2gi5rtadvyyp046g` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`id`),
  CONSTRAINT `FKs7b5ea4u5y4ds9vebvq7pxpa8` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `result`
--

DROP TABLE IF EXISTS `result`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `result` (
  `id` binary(16) NOT NULL,
  `finished` datetime DEFAULT NULL,
  `started` datetime DEFAULT NULL,
  `challenge_id` binary(16) DEFAULT NULL,
  `user_id` binary(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKj1mdhxodh10lmj3o9uh17i5ea` (`challenge_id`),
  KEY `FKpjjrrf0483ih2cvyfmx70a16b` (`user_id`),
  CONSTRAINT `FKj1mdhxodh10lmj3o9uh17i5ea` FOREIGN KEY (`challenge_id`) REFERENCES `challenge` (`id`),
  CONSTRAINT `FKpjjrrf0483ih2cvyfmx70a16b` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `runner`
--

DROP TABLE IF EXISTS `runner`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `runner` (
  `id` binary(16) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `canonical_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY (`canonical_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `submission`
--

DROP TABLE IF EXISTS `submission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `submission` (
  `id` binary(16) NOT NULL,
  `file_name` varchar(255) DEFAULT NULL,
  `successful` bit(1) NOT NULL,
  `submission_time` datetime DEFAULT NULL,
  `language_id` BINARY(16),
  `taskResult_result_id` binary(16) NOT NULL,
  `taskResult_task_id` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKmquolgstf98mxytbuvcgodh3c` (`taskResult_result_id`,`taskResult_task_id`),
  KEY `fk_submission_language` (`language_id`),
  CONSTRAINT `FKmquolgstf98mxytbuvcgodh3c` FOREIGN KEY (`taskResult_result_id`, `taskResult_task_id`) REFERENCES `task_result` (`result_id`, `task_id`),
  CONSTRAINT  `fk_submission_language` FOREIGN KEY (`language_id`) REFERENCES `language` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task`
--

DROP TABLE IF EXISTS `task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task` (
  `id` binary(16) NOT NULL,
  `description` longtext NOT NULL,
  `duration` bigint(20) NOT NULL,
  `instructions` longtext NOT NULL,
  `name` varchar(255) NOT NULL,
  `canonical_name` varchar(255) NOT NULL,
  `isPublic` bit(1) DEFAULT NULL,
  `endpoint_id` binary(16) DEFAULT NULL,
  `organization_id` binary(16) DEFAULT NULL,
  `runner_id` binary(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY (`canonical_name`),
  KEY `FKhmkv7bsyrl43gufb23c1g1win` (`endpoint_id`),
  KEY `FKlr7r6n58jgscnls4brj8c6fg2` (`organization_id`),
  KEY `FKiu5jr4nqt9yey18c721ixwyud` (`runner_id`),
  CONSTRAINT `FKhmkv7bsyrl43gufb23c1g1win` FOREIGN KEY (`endpoint_id`) REFERENCES `endpoint` (`id`),
  CONSTRAINT `FKiu5jr4nqt9yey18c721ixwyud` FOREIGN KEY (`runner_id`) REFERENCES `runner` (`id`),
  CONSTRAINT `FKlr7r6n58jgscnls4brj8c6fg2` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_language`
--

DROP TABLE IF EXISTS `task_language`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_language` (
  `tasks_id` binary(16) NOT NULL,
  `languages_id` binary(16) NOT NULL,
  PRIMARY KEY (`tasks_id`,`languages_id`),
  KEY `FK83xtpggmbfxcpnjda873l7imf` (`languages_id`),
  CONSTRAINT `FK83xtpggmbfxcpnjda873l7imf` FOREIGN KEY (`languages_id`) REFERENCES `language` (`id`),
  CONSTRAINT `FKbbidfslog5l20ln71ifd4byd3` FOREIGN KEY (`tasks_id`) REFERENCES `task` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_params`
--

DROP TABLE IF EXISTS `task_params`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_params` (
  `task_id` binary(16) NOT NULL,
  `params` longtext,
  `params_KEY` varchar(255) NOT NULL,
  PRIMARY KEY (`task_id`,`params_KEY`),
  CONSTRAINT `FKb8ei0q6q2udt6o5nc0rfyfggo` FOREIGN KEY (`task_id`) REFERENCES `task` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_result`
--

DROP TABLE IF EXISTS `task_result`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_result` (
  `end_time` datetime DEFAULT NULL,
  `successful` bit(1) NOT NULL,
  `start_time` datetime DEFAULT NULL,
  `task_id` binary(16) NOT NULL,
  `result_id` binary(16) NOT NULL,
  PRIMARY KEY (`result_id`,`task_id`),
  KEY `FKrybvdir7ojloru8l7yyfc8ucp` (`task_id`),
  CONSTRAINT `FK8ooeckt3h28qw46q074r4qwnf` FOREIGN KEY (`result_id`) REFERENCES `result` (`id`),
  CONSTRAINT `FKrybvdir7ojloru8l7yyfc8ucp` FOREIGN KEY (`task_id`) REFERENCES `task` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_skillmap`
--

DROP TABLE IF EXISTS `task_skill_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_skill_map` (
  `task_id` binary(16) NOT NULL,
  `skill_map` double DEFAULT NULL,
  `skill_map_KEY` int(11) NOT NULL,
  PRIMARY KEY (`task_id`,`skill_map_KEY`),
  CONSTRAINT `FKkpyx9lkb064i8nhs8f3o6qv79` FOREIGN KEY (`task_id`) REFERENCES `task` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `team`
--

DROP TABLE IF EXISTS `team`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `team` (
  `id` binary(16) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_g2l9qqsoeuynt4r5ofdt1x2td` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `team_member`
--

DROP TABLE IF EXISTS `team_member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `team_member` (
  `admin` bit(1) NOT NULL,
  `created` datetime NOT NULL,
  `user_id` binary(16) NOT NULL,
  `team_id` binary(16) NOT NULL,
  PRIMARY KEY (`team_id`,`user_id`),
  KEY `FKg24qjftfifisxhilscl0vmrb1` (`user_id`),
  CONSTRAINT `FK9ubp79ei4tv4crd0r9n7u5i6e` FOREIGN KEY (`team_id`) REFERENCES `team` (`id`),
  CONSTRAINT `FKg24qjftfifisxhilscl0vmrb1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `template`
--

DROP TABLE IF EXISTS `template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `template` (
  `id` binary(16) NOT NULL,
  `language_id` binary(16) DEFAULT NULL,
  `task_id` binary(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK64v4pan96dlfarosoa8ajgt4f` (`language_id`),
  KEY `FKhxka5mfo042a51q4c79yfhl27` (`task_id`),
  CONSTRAINT `FK64v4pan96dlfarosoa8ajgt4f` FOREIGN KEY (`language_id`) REFERENCES `language` (`id`),
  CONSTRAINT `FKhxka5mfo042a51q4c79yfhl27` FOREIGN KEY (`task_id`) REFERENCES `task` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `test`
--

DROP TABLE IF EXISTS `test`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `test` (
  `id` binary(16) NOT NULL,
  `runner_id` binary(16) DEFAULT NULL,
  `task_id` binary(16) DEFAULT NULL,
   `tests_ORDER` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`, `tests_ORDER`),
  KEY `FKjvtfp7jnalo9d5em8rmpxbj68` (`runner_id`),
  KEY `FK2xx1yyitp3uqqmhgeg0qwuvps` (`task_id`),
  CONSTRAINT `FK2xx1yyitp3uqqmhgeg0qwuvps` FOREIGN KEY (`task_id`) REFERENCES `task` (`id`),
  CONSTRAINT `FKjvtfp7jnalo9d5em8rmpxbj68` FOREIGN KEY (`runner_id`) REFERENCES `runner` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `test_params`
--

DROP TABLE IF EXISTS `test_params`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `test_params` (
  `test_id` binary(16) NOT NULL,
  `params` longtext,
  `params_KEY` varchar(255) NOT NULL,
  PRIMARY KEY (`test_id`,`params_KEY`),
  CONSTRAINT `FKdfmjm368bkaw1knkqq7mxpiqh` FOREIGN KEY (`test_id`) REFERENCES `test` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `test_result`
--

DROP TABLE IF EXISTS `test_result`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `test_result` (
  `id` binary(16) NOT NULL,
  `successful` bit(1) NOT NULL,
  `submission_id` binary(16) NOT NULL,
  `test_id` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKmcokumtauiop1p52ox5l19np9` (`submission_id`),
  KEY `FKef3e8k7fgvkj4mox0lxrkf8hh` (`test_id`),
  CONSTRAINT `FKef3e8k7fgvkj4mox0lxrkf8hh` FOREIGN KEY (`test_id`) REFERENCES `test` (`id`),
  CONSTRAINT `FKmcokumtauiop1p52ox5l19np9` FOREIGN KEY (`submission_id`) REFERENCES `submission` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` binary(16) NOT NULL,
  `admin` bit(1) NOT NULL,
  `created` datetime NOT NULL,
  `email` varchar(255) NOT NULL,
  `first_name` varchar(255),
  `last_name` VARCHAR(255),
  `enabled` bit(1) NOT NULL,
  `last_login` datetime DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  `coder_profile` binary(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_ob8kqyqqgmefl0aco34akdtpe` (`email`),
  UNIQUE KEY `UK_sb8bbouer5wak8vyiiy4pf2bx` (`username`),
  KEY `FKmh1qpwl7m9ht39vgkf37jbd7g` (`coder_profile`),
  CONSTRAINT `FKmh1qpwl7m9ht39vgkf37jbd7g` FOREIGN KEY (`coder_profile`) REFERENCES `coder_profile` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-03-29 16:04:03
