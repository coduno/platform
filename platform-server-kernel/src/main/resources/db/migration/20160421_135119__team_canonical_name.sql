ALTER TABLE `team` ADD `canonical_name` varchar(255);
ALTER TABLE `team` ADD CONSTRAINT `UK_casdf767gha6xa7sad` UNIQUE (`canonical_name`);