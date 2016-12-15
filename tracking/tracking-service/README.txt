The Tracking component provides functionality for tracking status and 
other aspects pertaining to PDS products that are not captured in the 
Registry Service. The software is packaged as a WAR file.

The software can be packaged with the "mvn package" command. The 
documentation including release notes, installation and operation of the 
software should be online at 
http://pds-cm.jpl.nasa.gov/pds4/tracking/tracking-service/. If it is not 
accessible, you can execute the "mvn site:run" command and  view the 
documentation locally at http://localhost:8080.

In order to create a complete distribution package, execute the 
following commands: 

% mvn site
% mvn package
