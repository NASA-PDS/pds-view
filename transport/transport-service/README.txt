Transport Service package includes the web-grid war file, the PDS and third 
party jar libraries, and the PDS configuration files.

The software can be compiled with the "mvn compile".
The documentation including release notes, installation and operation of the 
software should be online at 
http://pds-cm.jpl.nasa.gov/2010/transport/transport-service/. If it is not 
accessible, you can execute the "mvn site:run" command and view the 
documentation locally at http://localhost:8080/index.html.

In order to create a complete package for distribution, 
execute the following commands: 

% mvn site
% mvn package
