CREATE TABLE IF NOT EXISTS `logs` (
	`logid` int(11) NOT NULL auto_increment,
	`datecreated` TIMESTAMP NOT NULL DEFAULT NOW(),
	`message` TEXT NOT NULL,
	PRIMARY KEY (`logid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
			
UPDATE version SET db_schema = 4;