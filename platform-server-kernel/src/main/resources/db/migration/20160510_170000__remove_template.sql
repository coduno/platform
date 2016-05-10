CREATE TABLE `task_templates` (
  `task_id` binary(16) NOT NULL,
  `template_value` longtext,
  `template_key` varchar(255) NOT NULL,
  PRIMARY KEY (`task_id`,`template_key`),
  CONSTRAINT `taskidmapstoidoftask` FOREIGN KEY (`task_id`) REFERENCES `task` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert
  into task_templates (task_id, template_value, template_key)
  select
    task.id as task_id,
    language.name as template_value,
    language.tag as template_key
  from template, task, language
  where
    task.id = template.task_id and
    language.id = template.language_id
;

drop table template;