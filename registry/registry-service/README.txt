The Registry Service provides functionality for tracking, auditing, locating, 
and maintaining artifacts within the system. The service provides a 
REST-based interface for interacting with the service. The software is 
packaged as a WAR file.

The software can be compiled with the "mvn compile" command but in order 
to create the WAR file, you must execute the "mvn package" command. The 
documentation including release notes, installation and operation of the 
software should be online at 
http://pds-cm.jpl.nasa.gov/pds4/registry/registry-service/. If it is not 
accessible, you can execute the "mvn site:run" command and view the 
documentation locally at http://localhost:8080.

In order to create a complete package for distribution including the auto 
generated API documentation (application.html), execute the following 
commands: 

% mvn site
% mvn package

The "env" property can be specified to set the 
registry service endpoint and the backend database. Execute as follows:

% mvn package -Denv=local-derby

Valid values are local-derby (default), local-mysql, dev and ops.


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
Here you will find a reference to derby.properties which are the Derby 
settings. This can be changed to mysql.properties to support a MySQL setup. 

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

If deploying to Tomcat and using MySQL as a back end simply run 
"mvn -Dregistry.db.type=mysql package". This will make a package that assumes 
that you have mysql installed with a database named "registry" and will use a 
default username and password as specified below:

If you want to change the database name, user, and/or password you will need
to edit the mysql.properties file located in the 
$TOMCAT_HOME/webapps/registry-service/WEB-INF/classes directory. 

The following line specifies the connection url (database name at the end):
javax.persistence.jdbc.url=jdbc:mysql://localhost:3306/registry 

The following line specifies the user name:
javax.persistence.jdbc.user=registry

The following line specifies the password:
javax.persistence.jdbc.password=p@ssw0rd

Additionally, if you are using a version of MySQL older than 5.x you will need
to change the dialect. To do this simply add a "#" before the first 
hibernate.dialect entry and remove the "# from the second entry.

Before:
# For use with MySQL 5+
hibernate.dialect=org.hibernate.dialect.MySQL5InnoDBDialect
# For use with older versions of MySQL. See hibernate documentation.
#hibernate.dialect=org.hibernate.dialect.MySQLInnoDBDialect

After:

# For use with MySQL 5+
#hibernate.dialect=org.hibernate.dialect.MySQL5InnoDBDialect
# For use with older versions of MySQL. See hibernate documentation.
hibernate.dialect=org.hibernate.dialect.MySQLInnoDBDialect


Eclipse
=======
To generate the Eclipse files simply type 
"mvn eclipse:clean eclipse:eclipse", this will set up the eclipse project 
files. One will also need to set the "M2_REPO" variable from within Eclipse 
as the jars are referenced with this as their path. Typically this can be
set to ~/.m2/repository.

