CREATE TABLE `password_reset_token` (
  `token` varchar(26) NOT NULL,
  `expire` datetime NOT NULL,
  `user_id` binary(16) NOT NULL,
  PRIMARY KEY (`token`),
  CONSTRAINT `password_reset_token_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
