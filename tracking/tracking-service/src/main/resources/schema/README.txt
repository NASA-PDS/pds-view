This file describes the details for creating the Tracking Service database 
and its corresponding schema. This documentation will eventually be moved to 
the Installation document for the service. Create the "tracking" database and 
grant access from the "tracking" account on a local MySQL server with the 
following commands:

% mysqladmin -u root -p create tracking
% mysql -u root -p -e "GRANT ALL ON tracking.* TO tracking@localhost IDENTIFIED BY 'p@ssw0rd'"

Perform the following command to create the schema within the database:

% mysql -u tracking -p tracking < create-schema.sql

Perform the following command to load the schema with test data:

% mysql -u tracking -p tracking < load-schema.sql

Perform the following command to delete the contents from the schema:

% mysql -u tracking -p tracking < clean-schema.sql

Perform the following command to drop all database objects from the schema:

% mysql -u tracking -p tracking < drop-schema.sql
