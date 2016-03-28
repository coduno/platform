DROP TABLE IF EXISTS `result_starttimes`;
--
-- Table structure for table `task_result`
--
CREATE TABLE `task_result` (
  `endTime` datetime DEFAULT NULL,
  `green` bit(1) NOT NULL,
  `startTime` datetime DEFAULT NULL,
  `task_id` binary(16) NOT NULL,
  `result_id` binary(16) NOT NULL,
  PRIMARY KEY (`result_id`,`task_id`),
  KEY `FKrybvdir7ojloru8l7yyfc8ucp` (`task_id`),
  CONSTRAINT `FK8ooeckt3h28qw46q074r4qwnf` FOREIGN KEY (`result_id`) REFERENCES `result` (`id`),
  CONSTRAINT `FKrybvdir7ojloru8l7yyfc8ucp` FOREIGN KEY (`task_id`) REFERENCES `task` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `submission` DROP FOREIGN KEY `FK8cn4mdb6cxjdq2dvlaaw4et60`;
ALTER TABLE `submission` DROP FOREIGN KEY `FKh66q0hdbqk19lop36gyg3hvg0`;

ALTER TABLE `submission` DROP COLUMN `result_id`;
ALTER TABLE `submission` DROP COLUMN `task_id`;

ALTER TABLE `submission` ADD COLUMN `green` bit(1) NOT NULL;
ALTER TABLE `submission` ADD COLUMN `submissionTime` datetime DEFAULT NULL;
ALTER TABLE `submission` ADD COLUMN `taskResult_result_id` binary(16) NOT NULL;
ALTER TABLE `submission` ADD COLUMN `taskResult_task_id` binary(16) NOT NULL;

ALTER TABLE `submission` ADD KEY `FKmquolgstf98mxytbuvcgodh3c` (`taskResult_result_id`,`taskResult_task_id`);
ALTER TABLE `submission` ADD CONSTRAINT `FKmquolgstf98mxytbuvcgodh3c` FOREIGN KEY (`taskResult_result_id`, `taskResult_task_id`) REFERENCES `task_result` (`result_id`, `task_id`);