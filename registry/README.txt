The Registry project contains the web site that serves as the entry point 
for the registry components. It also includes the POM that should be 
inherited by all of the registry projects. The site should be online at 
http://pds-cm.jpl.nasa.gov/pds4/registry/. If it is not accessible, you can 
execute the "mvn site:run" command and view the documentation locally at 
http://localhost:8080.

Because this is a POM project, it should be deployed to the Maven repository 
so that it can be referenced by the child projects. This can be accomplished 
with the following command:

% mvn deploy --non-recursive

Using the "--non-recursive" option will by typical for most commands 
executed against this project so that they are not applied against the 
child projects or modules. The site can be deployed as follows: 

% mvn site-deploy --non-resursive
