ALTER TABLE columns
ADD COLUMN `stop_bit` int(5);

UPDATE version SET db_schema = 7;