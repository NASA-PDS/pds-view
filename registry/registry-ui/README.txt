The Registry UI is the user interface for the PDS Registry Service. 
The software is packaged in a WAR file.

The software can be compiled with the "mvn compile" command but in order 
to create the WAR file, you must execute the "mvn compile war:war" command. 
The documentation including release notes, installation and operation of the 
software should be online at 
http://pds-cm.jpl.nasa.gov/2010/registry/registry-ui/. If it is not 
accessible, you can execute the "mvn site:run" command and view the 
documentation locally at http://localhost:8080.

In order to create a complete package for distribution, execute the 
following commands: 

% mvn site
% mvn package -Dservice.endpoint={URL} -DskipTests=true

Replace the {URL} placeholder with URL for the 
Registry Service. It should end with ".../registry/".

For ease of deployment to an Apache Tomcat Server, use the 
"mvn tomcat:deploy" command. The tomcat-maven-plugin is configured in the 
pom.xml to deploy to the "tomcat-local" server. The username and password 
for the manager interface of this server should be specified in your local 
".m2/settings.xml" file. See Using Maven document at 
http://pds-cm.jpl.nasa.gov/maven2/ for more details. Use the 
"mvn tomcat:undeploy" command to reverse the deployment.