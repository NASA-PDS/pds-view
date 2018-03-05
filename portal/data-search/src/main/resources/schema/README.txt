This file describes the details for creating the Data Search database 
and its corresponding schema. This documentation will eventually be moved to 
the Installation document for the service. Create the "search" database and 
grant access from the "search" account on a local MySQL server with the 
following commands:

% mysqladmin -u root -p create search
% mysql -u root -p -e "GRANT ALL ON search.* TO search@localhost IDENTIFIED BY 'p@ssw0rd'"

Perform the following command to create the schema within the database:

% mysql -u registry -p search < create-schema.sql

Perform the following command to load the schema with test data:

% mysql -u registry -p search < load-schema.sql

Perform the following command to delete the contents from the schema:

% mysql -u registry -p search < clean-schema.sql

Perform the following command to drop all database objects from the schema:

% mysql -u registry -p search < drop-schema.sql