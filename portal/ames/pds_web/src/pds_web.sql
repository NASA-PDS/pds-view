-- MySQL dump 10.13  Distrib 5.6.21, for osx10.8 (x86_64)
--
-- Host: localhost    Database: pds_web
-- ------------------------------------------------------
-- Server version	5.6.21

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `columns`
--

DROP TABLE IF EXISTS `columns`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `columns` (
  `column_id` int(11) NOT NULL AUTO_INCREMENT,
  `column_name` varchar(200) NOT NULL,
  `data_type` varchar(30) DEFAULT NULL,
  `start_byte` int(5) DEFAULT NULL,
  `bytes` int(5) DEFAULT NULL,
  `description` varchar(4000) DEFAULT NULL,
  `unit` varchar(50) DEFAULT NULL,
  `format` varchar(10) DEFAULT NULL,
  `missing_constant` varchar(50) DEFAULT NULL,
  `column_number` int(4) DEFAULT NULL,
  `display_order` int(4) DEFAULT NULL,
  `selected` tinyint(1) DEFAULT NULL,
  `session_id` varchar(100) DEFAULT NULL,
  `tabular_container_id` varchar(100) DEFAULT NULL,
  `label` varchar(200) DEFAULT NULL,
  `start_bit` int(5) DEFAULT NULL,
  `bits` int(5) DEFAULT NULL,
  `items` int(5) DEFAULT NULL,
  `item_bits` int(5) DEFAULT NULL,
  `item_bytes` int(5) DEFAULT NULL,
  `item_offset` int(5) DEFAULT NULL,
  `field_number` int(5) DEFAULT NULL,
  `stop_bit` int(5) DEFAULT NULL,
  PRIMARY KEY (`column_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `downloads`
--

DROP TABLE IF EXISTS `downloads`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `downloads` (
  `download_id` int(11) NOT NULL AUTO_INCREMENT,
  `tabulardata_id` int(11) NOT NULL,
  `headers_included` tinyint(1) NOT NULL,
  `columns_selected` int(10) NOT NULL,
  `rows_selected` int(10) NOT NULL,
  `filter_count` int(2) NOT NULL,
  `time_created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `interchange_format` char(6) NOT NULL,
  `file_type` char(3) NOT NULL,
  PRIMARY KEY (`download_id`),
  KEY `tabulardata_id` (`tabulardata_id`),
  CONSTRAINT `downloads_ibfk_1` FOREIGN KEY (`tabulardata_id`) REFERENCES `tabulardata` (`tabulardata_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `exceptions`
--

DROP TABLE IF EXISTS `exceptions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `exceptions` (
  `exception_id` int(11) NOT NULL AUTO_INCREMENT,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `url` tinytext NOT NULL,
  `stack` text NOT NULL,
  `message` text NOT NULL,
  PRIMARY KEY (`exception_id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `filters`
--

DROP TABLE IF EXISTS `filters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `filters` (
  `filter_id` int(11) NOT NULL AUTO_INCREMENT,
  `tabulardata_id` int(11) NOT NULL,
  `column_name` varchar(200) NOT NULL,
  `column_type` varchar(20) NOT NULL,
  `filter_condition` varchar(20) NOT NULL,
  `condition_value` tinytext,
  `if_deleted` tinyint(1) DEFAULT NULL,
  `time_created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`filter_id`),
  KEY `tabulardata_id` (`tabulardata_id`),
  CONSTRAINT `filters_ibfk_1` FOREIGN KEY (`tabulardata_id`) REFERENCES `tabulardata` (`tabulardata_id`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `logs`
--

DROP TABLE IF EXISTS `logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `logs` (
  `logid` int(11) NOT NULL AUTO_INCREMENT,
  `datecreated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `message` text NOT NULL,
  PRIMARY KEY (`logid`)
) ENGINE=MyISAM AUTO_INCREMENT=396 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `queue`
--

DROP TABLE IF EXISTS `queue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `queue` (
  `queue_id` int(11) NOT NULL AUTO_INCREMENT,
  `test_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `queue_size` int(5) NOT NULL,
  PRIMARY KEY (`queue_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `slice`
--

DROP TABLE IF EXISTS `slice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `slice` (
  `slice_id` int(11) NOT NULL AUTO_INCREMENT,
  `session_id` char(32) NOT NULL,
  `label_url` tinytext NOT NULL,
  `user_ip` varchar(40) NOT NULL,
  `time_created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`slice_id`)
) ENGINE=InnoDB AUTO_INCREMENT=95 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tabulardata`
--

DROP TABLE IF EXISTS `tabulardata`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tabulardata` (
  `tabulardata_id` int(11) NOT NULL AUTO_INCREMENT,
  `slice_id` int(11) NOT NULL,
  `table_url` tinytext NOT NULL,
  `columns_orig` int(10) NOT NULL,
  `rows_orig` int(10) NOT NULL,
  `table_type` tinytext NOT NULL,
  `time_created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `load_time` int(11) DEFAULT NULL,
  `cancelled` tinyint(1) NOT NULL,
  `interchange_format` char(6) NOT NULL,
  PRIMARY KEY (`tabulardata_id`),
  KEY `slice_id` (`slice_id`),
  CONSTRAINT `tabulardata_ibfk_1` FOREIGN KEY (`slice_id`) REFERENCES `slice` (`slice_id`)
) ENGINE=InnoDB AUTO_INCREMENT=78 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `version`
--

DROP TABLE IF EXISTS `version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `version` (
  `db_schema` int(5) NOT NULL,
  PRIMARY KEY (`db_schema`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `volume_errors`
--

DROP TABLE IF EXISTS `volume_errors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `volume_errors` (
  `error_id` int(11) NOT NULL AUTO_INCREMENT,
  `volume_stat_id` int(11) NOT NULL,
  `error_type` varchar(55) NOT NULL,
  `num_found` int(11) NOT NULL,
  PRIMARY KEY (`error_id`),
  KEY `error_type` (`error_type`),
  KEY `volume_stat_id` (`volume_stat_id`),
  CONSTRAINT `FK_volume_errors` FOREIGN KEY (`volume_stat_id`) REFERENCES `volume_stats` (`volume_stat_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1253 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `volume_stats`
--

DROP TABLE IF EXISTS `volume_stats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `volume_stats` (
  `volume_stat_id` int(11) NOT NULL AUTO_INCREMENT,
  `validating_ip` varchar(15) NOT NULL,
  `volume_id` varchar(255) NOT NULL,
  `volume_size` int(11) NOT NULL,
  `date_validated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `cancelled` tinyint(1) NOT NULL,
  `duration` int(11) NOT NULL,
  PRIMARY KEY (`volume_stat_id`),
  KEY `volume_id` (`volume_id`)
) ENGINE=InnoDB AUTO_INCREMENT=112 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-10-04 13:11:11
