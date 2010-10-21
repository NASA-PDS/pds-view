The Design Tool is satisfied with the open source tool Eclipse and the 
commercial tool Oxygen. This project contains the web site containing the 
documentation for installing and configuring the design tools for the PDS 
2010 system.

The documentation including release notes, installation and operation of 
the software should be online at 
http://pds-cm.jpl.nasa.gov/2010/tools/design/. 
If it is not accessible, you can execute the "mvn site:run" command and 
view the documentation locally at http://localhost:8080.

In order to create a complete package for distribution, execute the 
following commands: 

% mvn site
% maven pdf
% mvn package

The project still relies on Maven 1 functionality to generate the PDF form 
of the documentation.
