The Product Query component is a web application that serves as the name 
resolver for the Product Servers that are located at the various Discipline 
Nodes. The software is packaged in a WAR file.

The software can be compiled with the "mvn compile" command but in order 
to create the WAR file, you must execute the "mvn compile war:war" command. 
The documentation including release notes, installation and operation of the 
software should be online at 
http://pds-cm.jpl.nasa.gov/pds4/portal/product-query/. If it is not 
accessible, you can execute the "mvn site:run" command and view the 
documentation locally at http://localhost:8080.

In order to create a complete package for distribution, execute the 
following commands: 

% mvn site
% mvn package
