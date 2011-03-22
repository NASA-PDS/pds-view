The Profile Setup is a user interface for configuring the Report Service. 
The software is packaged in a WAR file.

The software can be compiled with the "mvn compile" command but in order 
to create the WAR file, you must execute the "mvn compile war:war" command. 

The documentation including release notes, installation and operation of 
the software should be online at 
http://pds-cm.jpl.nasa.gov/2010/report/profile-setup. 
If it is not accessible, you can execute the "mvn site:run" command and 
view the documentation locally at http://localhost:8080.

In order to create a complete package for distribution, execute the 
following commands: 

% mvn site
% maven pdf
% mvn package

The project still relies on Maven 1 functionality to generate the PDF form 
of the documentation.
