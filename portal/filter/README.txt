The Filter component is a library for filtering parameter values passed 
into web applications. The software is packaged in a JAR file.

The software can be compiled with the "mvn compile" command but in order 
to create the JAR file, you must execute the "mvn package" command. 
The documentation including release notes, installation and operation of the 
software should be online at http://pds-cm.jpl.nasa.gov/pds4/portal/filter/. 
If it is not accessible, you can execute the "mvn site:run" command and view 
the documentation locally at http://localhost:8080.

In order to create a complete package for distribution, execute the 
following commands: 

% mvn site
% mvn package
