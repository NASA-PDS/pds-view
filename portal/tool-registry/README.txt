The Tool Registrty component is a web application that supports entry and 
search of PDS-related tools and services. The software is packaged as a 
JQuery-based application.

The software is not compiled but can instead be deployed with a Subversion 
"external" directory configuration within the PDS Home site directory 
structure. The desired end point is tools/tool-registry within the site and 
can be configured as follows:

% svn checkout https://starcell.jpl.nasa.gov/repo/websites/pds-beta htdocs
% cd htdocs/tools
% svn propset svn:externals \
'tool-registry https://starcell.jpl.nasa.gov/repo/2010/trunk/portal/tool-registry/src/main/webapp' .

The documentation including release notes, installation and operation of the 
software should be online at 
http://pds-cm.jpl.nasa.gov/pds4/portal/tool-registry/. If it is not 
accessible, you can execute the "mvn site:run" command and view the 
documentation locally at http://localhost:8080.
