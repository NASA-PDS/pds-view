The Catalog Ingest Tool (CITool) is a command-line application for 
comparing, validating and ingesting catalog submissions in the form of 
catalog files.

The software is packaged in a JAR file with corresponding shell scripts 
for launching the application.

The software can be compiled with the "mvn compile" command but in order 
to create the JAR file, you must execute the "mvn package" command. The 
documentation including release notes, installation and operation of the 
software should be online at http://pds-cm.jpl.nasa.gov/citool/. 
If it is not accessible, you can execute the "mvn site:run" command and 
view the documentation locally at http://localhost:8080.

In order to create a complete package for distribution, execute the 
following commands: 

% mvn site
% maven pdf
% mvn package

The project still relies on Maven 1 functionality to generate the PDF form 
of the documentation.
