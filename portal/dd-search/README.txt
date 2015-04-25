The Data Dictionary Search component is a web application that serves as the 
search interface for PDS3 and PDS4 data dictionary products. The software is 
packaged as a JQuery-based application.

The software is not compiled but can instead be deployed with a Subversion 
"external" directory configuration within the PDS Home site directory 
structure. The desired end point is tools/dd-search within the site and can
be configured as follows:

% svn checkout https://starcell.jpl.nasa.gov/repo/websites/pds-beta htdocs
% cd htdocs/tools
% svn propset svn:externals \
'dd-search https://starcell.jpl.nasa.gov/repo/2010/trunk/portal/dd-search/src/main/webapp' .

The documentation including release notes, installation and operation of the 
software should be online at 
http://pds-cm.jpl.nasa.gov/pds4/portal/dd-search/. If it is not 
accessible, you can execute the "mvn site:run" command and view the 
documentation locally at http://localhost:8080.
