set FOREIGN_KEY_CHECKS = 0;

-- Fix up following foreign key references to location.
--  * challenge_location (references all locations that the challenge is held at)
--  * participation (references the location that the participant will attend at)

-- Add and fill new column that will reference to location.
alter table `challenge_location` add column `location_id` varchar(255) not null;
update `challenge_location`, `location` set `challenge_location`.`location_id` = `location`.`place_id` where `challenge_location`.`locations_id` = `location`.`id`;

-- Replace the old reference column with the newly created one.
alter table `challenge_location` drop primary key, add primary key (`location_id`, `challenges_id`);

-- Remove the foreign key constraint on the old column and remove the column altogether.
alter table `challenge_location` drop foreign key `FK83xtp0mmtgycpnjda86517izk`;
alter table `challenge_location` drop column `locations_id`;

alter table `participation` add column `location_place_id` varchar(255) not null;
update `participation`, `location` set `participation`.`location_place_id` = `location`.`place_id` where `participation`.`location_id` = `location`.`id`;

alter table `participation` drop foreign key `FKbbmq18iuz5zzpqln98iyd4bp9l`;
alter table `participation` drop column `location_id`;

ALTER TABLE `participation` CHANGE  `location_place_id` `location_id` VARCHAR(255) NOT NULL;

alter table `location` drop primary key, add primary key (`place_id`);

--
-- Table structure for table `location_detail`
--

DROP TABLE IF EXISTS `location_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `location_detail` (
  `name`        VARCHAR(255) NOT NULL,
  `address`     VARCHAR(255),
  `description` VARCHAR(255),
  `created` datetime NOT NULL,
  `challenge_id` binary(16) NOT NULL,
  `location_id` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`challenge_id`,`location_id`),
  CONSTRAINT `FK9ubp79ei4tv4crd0r9n7u5i6d` FOREIGN KEY (`challenge_id`) REFERENCES `challenge` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

insert into location_detail select
  l.place_id as location_id,
  cl.challenges_id as challenge_id,
  l.address as address,
  l.description as description,
  l.name as name,
  ('00000000000000') as created
from location l, challenge_location cl where cl.location_id = l.place_id;

alter table `location` drop column address;
alter table `location` drop column description;
alter table `location` drop column `name`;
alter table `location` drop primary key, add primary key (`place_id`);
alter table `location` drop column `id`;
alter table `location` change column `place_id` `id` varchar(255) not null;

alter table `challenge_location` add constraint `FK83xtp0mmtgycpnjda86517izl` foreign key (`location_id`) references `location` (`id`);
alter table `participation` add constraint `FKbbmq18iuz5zzpqln98iyd4bp9m` foreign key (`location_id`) references `location` (`id`);
alter table `location_detail` add constraint `FKg24qjftfifisxhilscl0vmrb2` FOREIGN KEY (`location_id`) REFERENCES `location` (`id`);

set FOREIGN_KEY_CHECKS = 1;