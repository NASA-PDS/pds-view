The Registry Service provides functionality for tracking, auditing, locating, 
and maintaining artifacts within the system. The service provides a 
REST-based interface for interacting with the service. The software is 
packaged as a WAR file.

The software can be compiled with the "mvn compile" command but in order 
to create the WAR file, you must execute the "mvn package" command. The 
documentation including release notes, installation and operation of the 
software should be online at 
http://pds-cm.jpl.nasa.gov/2010/registry/registry-service/. If it is not 
accessible, you can execute the "mvn site:run" command and view the 
documentation locally at http://localhost:8080.

In order to create a complete package for distribution including the auto 
generated API documentation (application.html), execute the following 
commands: 

% mvn site
% maven pdf
% mvn package

The project still relies on Maven 1 functionality to generate the PDF form 
of the documentation.


Installation
============

A. Embedded

To run a local registry using an embedded tomcat server simply type 
"mvn tomcat:run". This will launch tomcat with the registry running on 
localhost:8080. By default this will use Derby as its database and create a 
directory named RegistryDB. In addition, derby will create a log file named 
"derby.log" that will contain all the SQL statements sent to derby if the 
system property "-Dderby.language.logStatementText=true" is set.

If you would like to change the port the registry service will start up on 
in embedded mode use set the "maven.tomcat.port" on the command line. For 
instance, to start up on port 8000 instead of 8080 run 
"mvn -Dmaven.tomcat.port=8000 tomcat:run" from the command line.

If you would like to set the destination of the registry database when using 
derby set the following 
"mvn -Dderby.system.home=/path/to/registrydb/home tomcat:run"

If you are going to start the registry service from maven using 
"mvn tomcat:run" you may want to up the initial and max java heap size. This 
can be done by setting the MAVEN_OPTS environment variable. For instance in a 
bash shell one would run "export MAVEN_OPTS="-Xms128m -Xmx256m".

The file src/main/resources/applicationContext.xml contains most of the 
configuration for the registry and its database (or at least pointers to it). 
Here you will find a reference to db.properties which are the Derby settings. 
This can be changed to mysql.properties to support a MySQL setup but has not 
yet been tested. 

B. Deployable War

If one wishes the registry can be deployed as a war file onto an external 
application server. To generate the war file simply type 
"mvn compile war:war", this will generate the war file in the target 
directory.

If deploying to Tomcat and using Derby as a back end you may want to 
permanently set the home directory of the database by adding the following 
to the CATALINA_OPTS environment variable 
"-Dderby.system.home=/path/to/registrydb/home". The CATALINA_OPTS environment 
variable is used in Tomcat's startup scripts. If this is not set the Derby 
Registry Database home will be set to wherever you start Tomcat from. 

C. Jar 

To create the jar to use as a dependency within other projects simply run 
"mvn compile jar:jar". This will create the target/registry-service.jar which 
can be used to integrate with other libraries.

Eclipse
=======
To generate the Eclipse files simply type 
"mvn eclipse:clean eclipse:eclipse", this will set up the eclipse project 
files. One will also need to set the "M2_REPO" variable from within Eclipse 
as the jars are referenced with this as their path. Typically this can be
set to ~/.m2/repository.

