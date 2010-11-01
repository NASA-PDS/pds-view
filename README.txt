The PDS 2010 project contains the web site that serves as the entry point 
for the PDS 2010 effort. It also includes the POM that should be inherited 
by all of the 2010 projects. The site should be online at 
http://pds-cm.jpl.nasa.gov/2010/. If it is not accessible, you can execute 
the "mvn site:run" command and view the documentation locally at 
http://localhost:8080.

Because this is a POM project, it should be deployed to the Maven repository 
so that it can be referenced by the child projects. This can be accomplished 
with the following command:

% mvn deploy --non-recursive

Using the "--non-recursive" option will be typical for most commands 
executed against this project so that they are not applied against the 
child projects or modules. The site can be deployed as follows: 

% mvn site-deploy --non-recursive

By default, the site is built using the CM skin. In order to build the site 
with the EN skin, modify the following block in the pom.xml file: 

  <parent>
    <groupId>gov.nasa.pds</groupId>
    <artifactId>pds-en</artifactId>
    <version>0.0.0</version>
  </parent>

In addition, the Findbugs and Checkstyle reports should be commented out in 
the pom.xml file since we don't want the rest of PDS to see these reports.

The project includes a couple of scripts to aide in the process of building 
and deploying this site and the module sites recursively. In order to deploy 
the sites to the CM site, execute the following:

% cd util
% ./mvn-site-deploy.sh

In order to build JAR files of the sites for manual deployment to the EN 
site, execute the following:

% cd util
% ./mvn-site-jar.sh
