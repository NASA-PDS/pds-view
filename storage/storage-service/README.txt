The Storage Service provides functionality for managing the data 
repository. This includes movement of data files in and out of the 
repository.

The software is packaged in a JAR file with corresponding shell scripts 
for launching the service.

The software can be packaged with the "mvn package" command. The 
documentation including release notes, installation and operation of the 
software should be online at http://pds-cm.jpl.nasa.gov/2010/storage/storage-service. 
If it is not accessible, you can execute the "mvn site:run" command and 
view the documentation locally at http://localhost:8080.

In order to create a complete distribution package, execute the 
following commands: 

% mvn site
% mvn package

The project includes javadoc documentation created from the Apache OODT File 
Manager distribution that corresponds with the dependency version in the 
pom.xml file. This documentation resides in the /src/main/resources/apidocs 
directory. If the File Manager dependency is upgraded to a new version, this
documentation should also be regenerated and incorporated into this project.
