The Email component provides functionality to accept electronic mail 
requests and send those request to the target recipients via a configured 
SMTP server.

The software can be packaged with the "mvn package" command. The 
documentation including release notes, installation and operation of the 
software should be online at http://pds-cm.jpl.nasa.gov/pds4/email/. 
If it is not accessible, you can execute the "mvn site:run" command and 
view the documentation locally at http://localhost:8080.

In order to create a complete distribution package, execute the 
following commands: 

% mvn site
% mvn package
