CREATE TABLE `access_token` (
  `id` binary(16) NOT NULL,
  `comment` varchar(255) DEFAULT NULL,
  `created` datetime NOT NULL,
  `lastUsed` datetime DEFAULT NULL,
  `token` varchar(255) NOT NULL,
  `user_id` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKt6hfg4j66k3lwb5idt1tuaek6` (`user_id`),
  CONSTRAINT `FKt6hfg4j66k3lwb5idt1tuaek6` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
