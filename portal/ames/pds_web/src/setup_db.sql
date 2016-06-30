CREATE TABLE `volume_stats` (
  `volume_stat_id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `validating_ip` varchar(15) NOT NULL,
  `volume_id` varchar(255) NOT NULL,
  `volume_size` int(11) NOT NULL,
  `date_validated` timestamp NOT NULL,
  `cancelled` BOOLEAN NOT NULL,
  `duration` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `queue` (
  `queue_id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `test_date` timestamp NOT NULL,
  `queue_size` int(5) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `volume_errors` (
  `error_id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `volume_stat_id` int(11) NOT NULL,
  `error_type` varchar(55) NOT NULL,
  `num_found` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

