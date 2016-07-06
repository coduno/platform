ALTER TABLE `result` ADD `team_id` BINARY(16);
ALTER TABLE `result` ADD KEY `FK_RESULT_TEAM` (`team_id`);
ALTER TABLE `result` ADD CONSTRAINT `FK_RESULT_TEAM` FOREIGN KEY (`team_id`) REFERENCES `team` (`id`);