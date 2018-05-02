The Search UI is the user interface for the PDS Search Service. 
The software is packaged in a WAR file.

The software can be compiled with the "mvn compile" command but in order 
to create the WAR file, you must execute the "mvn compile war:war" command. 
The documentation including release notes, installation and operation of the 
software should be online at 
http://pds-cm.jpl.nasa.gov/pds4/search/search-ui/. If it is not 
accessible, you can execute the "mvn site:run" command and view the 
documentation locally at http://localhost:8080.

In order to create a complete package for distribution, execute the 
following commands: 

% mvn site
% mvn package

This software package includes several external directories from the PDS
web site that reside in the /src/main/resources/extras directory. The file
"svn_externals.txt", which resides a directory level above, details the 
locations in Subversion repository where these directories reside. If these
locations are changed, perform the following commands from the "extras" 
directory to delete and then set the new locations (after the 
"svn_externals.txt" file has been modified for the new locations):

% svn propdel svn:externals .
% svn propset svn:externals . -F ../svn_externals.txt
% svn commit

The updated properties can be viewed with the following command:

% svn propget svn:externals .
