ALTER TABLE `language` CHANGE `tag` `canonical_name` VARCHAR(255) NOT NULL;
CREATE UNIQUE INDEX language_canonical_name_uindex ON `language` (`canonical_name`);

ALTER TABLE team MODIFY canonical_name VARCHAR(255) NOT NULL;

ALTER TABLE `organization` CHANGE `nick` `canonical_name` VARCHAR(255) NOT NULL;

ALTER TABLE `user` CHANGE `username` `canonical_name` VARCHAR(255) NOT NULL;