ALTER TABLE volume_stats ADD INDEX(volume_id);
ALTER TABLE volume_errors ADD INDEX(error_type);
ALTER TABLE volume_errors ADD INDEX(volume_stat_id);
ALTER TABLE volume_errors ADD CONSTRAINT FK_volume_errors FOREIGN KEY (volume_stat_id) REFERENCES volume_stats(volume_stat_id);

UPDATE version SET db_schema = 2;