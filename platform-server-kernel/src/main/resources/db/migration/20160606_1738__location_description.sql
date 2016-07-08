ALTER TABLE `location` DROP INDEX `name`;
ALTER TABLE `location` DROP INDEX `place_id`;

ALTER TABLE `location` ADD `description` VARCHAR(255);
ALTER TABLE `location` ADD `address` VARCHAR(255);