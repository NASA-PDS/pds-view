ALTER TABLE columns
ADD COLUMN `start_bit` int(5),
ADD COLUMN `bits` int(5);

UPDATE version SET db_schema = 5;