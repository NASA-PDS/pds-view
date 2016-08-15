ALTER TABLE columns

ADD COLUMN `items` int(5),
ADD COLUMN `item_bits` int(5),
ADD COLUMN `item_bytes` int(5),
ADD COLUMN `item_offset` int(5),
ADD COLUMN `field_number` int(5),
MODIFY COLUMN `bytes` int(5),
MODIFY COLUMN `data_type` varchar(30);

UPDATE version SET db_schema = 6;