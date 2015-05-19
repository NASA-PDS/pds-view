The Phone Book Search component is a web application that serves as the 
search interface for PDS Affiliate products. The software is 
packaged as a JQuery-based application.

The software is not compiled but can instead be deployed with a Subversion 
"external" directory configuration within the PDS Home site directory 
structure. The desired end point is about/pb-search within the site and can
be configured as follows:

% svn checkout https://starcell.jpl.nasa.gov/repo/websites/pds-beta htdocs
% cd htdocs/about
% svn propset svn:externals \
'pb-search https://starcell.jpl.nasa.gov/repo/2010/trunk/portal/pb-search/src/main/webapp' .

The documentation including release notes, installation and operation of the 
software should be online at 
http://pds-cm.jpl.nasa.gov/pds4/portal/pb-search/. If it is not 
accessible, you can execute the "mvn site:run" command and view the 
documentation locally at http://localhost:8080.
