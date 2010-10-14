Installation
============

A. Embedded

To run a local registry using an embedded tomcat server simply type "mvn tomcat:run". 
This will launch tomcat with the registry running on localhost:8080. By default this 
will use Derby as its database and create a directory named RegistryDB. In addition, 
derby will create a log file named "derby.log" that will contain all the SQL statements 
sent to derby if the system property "-Dderby.language.logStatementText=true" is set.

If you would like to change the port the registry service will start up on in embedded mode 
use set the "maven.tomcat.port" on the command line. For instance, to start up on port 8000 
instead of 8080 run "mvn -Dmaven.tomcat.port=8000 tomcat:run" from the command line.

If you would like to set the detination of the registry database when using derby set the 
following "mvn -Dderby.system.home=/path/to/registrydb/home tomcat:run"

If you are going to start the registry service from maven using "mvn tomcat:run" you 
may want to up the intial and max java heap size. This can be done by setting the MAVEN_OPTS
environment variable. For instance in a bash shell one would run "export MAVEN_OPTS="-Xms128m -Xmx256m".

The file src/main/resources/applicationContext.xml contains most of the configuration for
the registry and its database (or at least pointers to it). Here you will find a reference
to db.properties which are the Derby settings. This can be changed to mysql.properties to 
support a MySQL setup but has not yet been tested. 

B. Deployable War

If one wishes the registry can be deployed as a war file onto an external app server. To 
generate the war file simply type "mvn compile war:war", this will generate the war file in the 
target directory.

If deploying to Tomcat and using Derby as a back end you may want to permanently set the home 
directory of the database by adding the following to the CATALINA_OPTS environment variable
"-Dderby.system.home=/path/to/registrydb/home". The CATALINA_OPTS environment variable is used in 
Tomcat's startup scripts. If this is not set the Derby Registry Database home will be set to wherever
you start Tomcat from. 

C. Jar 

To create the jar to use as a dependency within other projects simply run "mvn compile jar:jar". 
This will create the target/registry-service.jar which can be used to integrate with other libraries.

Documentation
=============
To generate the documentation for the registry service type "mvn package". The result of 
this command will be target/application.html which can be viewed in any browser and will 
give a basic description of the end points.


Eclipse
=======
To generate the Eclipse files simply type "mvn eclipse:clean eclipse:eclipse", this will 
set up the eclipse project files. One will also need to set the "M2_REPO" variable from 
within Eclipse as the jars are referenced with this as their path. Typically this can 
be set to ~/.m2/repository.


Commands
========
The following commands are meant to be run from the base directory of the project source
as the paths are relative. This will help you throw a small sampling of seed data into your
local registry and test your setup.

curl -X POST -H "Content-type:application/xml" -v -d @src/test-data/new_product.xml http://localhost:8080/registry-service/registry/products
curl -X POST -H "Content-type:application/xml" -v -d @src/test-data/new_product_v3.xml http://localhost:8080/registry-service/registry/products/1234
curl -X POST -H "Content-type:application/xml" -v -d @src/test-data/new_product_v2.xml http://localhost:8080/registry-service/registry/products/1234?major=false
curl -X POST -H "Content-type:application/json" -v -d @src/test-data/json_product.txt http://localhost:8080/registry-service/registry/products
curl -X POST -H "Content-type:application/json" -v -d @src/test-data/json2_product.txt http://localhost:8080/registry-service/registry/products
curl -X GET -H "Accept:application/xml" -v http://localhost:8080/registry-service/registry/products
curl -X GET -H "Accept:application/xml" -v http://localhost:8080/registry-service/registry/products/1234
curl -X GET -H "Accept:application/xml" -v http://localhost:8080/registry-service/registry/products/1234/all
curl -X GET -H "Accept:application/xml" -v http://localhost:8080/registry-service/registry/products/1234/3.0
curl -X GET -H "Accept:application/json" -v http://localhost:8080/registry-service/registry/products/1234/3.0
curl -X POST -H "Content-type:application/xml" -v http://localhost:8080/registry-service/registry/products/1234/1.0/Approve
curl -X GET -v http://localhost:8080/registry-service/registry/products/1234/3.0.json
curl -X GET -H "Accept:application/json" -v http://localhost:8080/registry-service/registry/status
curl -X DELETE -v http://localhost:8080/registry-service/registry/1234/1.0

Product Querying
================
The http://localhost:8080/registry-service/registry/products end point has been expanded to accept the following parameters:

guid - Supports filtering on the global unique id. **
name - Supports filtering on the name. **
lid - Supports filtering on the logical identifier. **
versionName - Supports filtering on the registry version. **
versionId - Supports filtering on the user version. **
objectType - Supports filtering on the type of registry object type. Associations are not available through this interface. **
status - Supports filtering on the registry object status that maps to the gov.nasa.pds.registry.model.ObjectStatus enum
sort - Supports sorting on all of the above parameters. Supports optional ASC or DESC. If not specified the default is "guid ASC".
queryOp - Supports whether the other the filters are AND/OR together. The default is to AND them together.
start - Where to start in the results set. Defaults to 1 if not specified.
rows - How many results to retrieve. Defaults to 20 if not specified.

Association Querying
================
The http://localhost:8080/registry-service/registry/associations accepts the following parameters:

targetLid - Supports filtering on the lid of the target registry object. **
targetVersionId - Supports filtering on the userVersion of the target registry object. **
targetHome - Supports filtering on the home of the target registry object. **
sourceLid - Supports filtering on the lid of the source registry object. **
sourceVersionId - Supports filtering on the userVersion of the source registry object. **
sourceHome - Supports filtering on the home of the source registry object. **
associationType - Supports filtering on the type of association between the source and target registry object. **
sort - Supports sorting on all of the above parameters. Supports optional ASC or DESC. If not specified the default is "guid ASC".
queryOp - Supports whether the other the filters are AND/OR together. The default is to AND them together.
start - Where to start in the results set. Defaults to 1 if not specified.
rows - How many results to retrieve. Defaults to 20 if not specified.

** Denotes support for wildcard matches using '*' in the string provided. For instance guid=urn:uuid:* will match all guid's starting with 'urn:uuid:'
 


