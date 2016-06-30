CREATE TABLE IF NOT EXISTS `columns`( 
`column_id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
`column_name` varchar(200) NOT NULL, 
`data_type` varchar(30) NOT NULL,
`start_byte` int(5),
`bytes` int(5) NOT NULL,
`description` varchar(4000), 
`unit` varchar(50), 
`format` varchar(10), 
`missing_constant` varchar(50), 
`column_number` int(4), 
`display_order` int(4), 
`selected` boolean, 
`session_id` varchar(100), 
`tabular_container_id` varchar(100), 
`label` varchar(200)	
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `slice` (
`slice_id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
`session_id` char(32) NOT NULL, 
`label_url` TINYTEXT NOT NULL,
`user_ip` varchar(40) NOT NULL,
`time_created` timestamp NOT NULL default CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `tabulardata` (
`tabulardata_id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
`slice_id` int(11) NOT NULL,
`table_url` TINYTEXT NOT NULL,
`columns_orig` int(10) NOT NULL,
`rows_orig` int(10) NOT NULL,
`table_type` TINYTEXT NOT NULL,
`time_created` timestamp NOT NULL default CURRENT_TIMESTAMP,
`load_time` int(11),
`cancelled` boolean NOT NULL,
`interchange_format` char (6) NOT NULL,
FOREIGN KEY (`slice_id`) REFERENCES slice(`slice_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `filters` (
`filter_id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
`tabulardata_id` int(11) NOT NULL,
`column_name` varchar(200) NOT NULL, 
`column_type` varchar(20) NOT NULL,
`filter_condition` varchar(20) NOT NULL,
`condition_value` TINYTEXT,
`if_deleted` boolean ,
`time_created` timestamp NOT NULL default CURRENT_TIMESTAMP,
FOREIGN KEY (`tabulardata_id`) REFERENCES tabulardata(`tabulardata_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `downloads` (
`download_id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
`tabulardata_id` int(11) NOT NULL,
`headers_included` BOOLEAN NOT NULL,
`columns_selected` int(10) NOT NULL,
`rows_selected` int(10) NOT NULL,
`filter_count` int(2) NOT NULL,
`time_created` timestamp NOT NULL default CURRENT_TIMESTAMP,
`interchange_format` char(6) NOT NULL,
`file_type` char(3) NOT NULL,
FOREIGN KEY (`tabulardata_id`) REFERENCES tabulardata(`tabulardata_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


UPDATE version SET db_schema = 3;