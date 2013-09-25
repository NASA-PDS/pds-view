Transport Service (Registry) component is an implementation of the Apache 
OODT Web Grid Product Server and includes a handler for interfacing with the 
Registry Service. This software is packaged as a WAR file.

The software can be compiled with the "mvn compile". The documentation 
including release notes, installation and operation of the software should 
be online at 
http://pds-cm.jpl.nasa.gov/pds4/transport/transport-registry/. If it is not 
accessible, you can execute the "mvn site:run" command and view the 
documentation locally at http://localhost:8080/index.html.

In order to create a complete package for distribution, execute the 
following commands: 

% mvn site
% mvn package
