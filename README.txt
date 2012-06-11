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

The project includes a couple of scripts to aide in the process of building 
and deploying this site and the module sites recursively as well as building
the packages and deploying them to the Maven repository. In order to build 
packages for all of the projects, execute the following:

% cd util
% ./mvn-package.sh

In order to deploy the sites to the CM site, execute the following:

% cd util
% ./mvn-site-deploy-cm.sh

Not all module sites are intended for the Engineering Node site. Additionally,
the EN site uses a different skin. In order to to build and deploy the
associated sites with the EN skin to a deployment directory, execute the 
following:

% cd util
% ./mvn-site-deploy-en.sh

In order to build the module packages and deploy them to the Maven repository,
execute the following:

% cd util
% ./mvn-deploy.sh
