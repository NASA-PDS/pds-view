CREATE TABLE IF NOT EXISTS `exceptions` (
	`exception_id` int(11) NOT NULL auto_increment,
	`date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
	`url` TINYTEXT NOT NULL,
	`stack` TEXT NOT NULL,
	`message` TEXT NOT NULL,
	PRIMARY KEY (`exception_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `version` (
	`db_schema` int(5) NOT NULL,
	PRIMARY KEY (`db_schema`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO version SET db_schema = 1;
