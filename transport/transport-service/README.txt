PREREQUISITES

o Java VM

o Tomcat 7+

The PDS Transport Service is a web application that runs within a servlet container. 
These instructions assume that the Transport Service is deployed within an existing installation of Tomcat 6+,
located in a directory referenced here as $CATALINA_HOME.

INSTALLATION FROM BINARY DISTRIBUTION (recommended)

Download the package distribution transport-service-<version>-bin.tar.gz to a location on your server referenced here as $PDS_HOME. 
A typical location on Unix system is PDS_HOME=/usr/local/pds, but the package can be installed anywhere. Unpack the distribution in
that location and create a symbolic link to facilitate configuration and future upgrades.

export PDS_HOME=/usr/local/pds
cd $PDS_HOME
tar xvfz transport-service-<version>-bin.tar.gz
ln -s ./transport-service-<version> ./transport-service
cd transport-service

INSTALLATION FROM SOURCE

Note: installing from source is recommended for PDS developers only. This process requires access to the PDS internal repository,
as well as installation of pre-requisite software libraries.

o Install the PDS tools library:

svn checkout http://pdscm/repo/2010/trunk/preparation/pds4-tools/
cd pds4-tools/superpom
mvn clean install
cd ../package
mvn clean install

o Build the transport-service package:

svn checkout http://pdscm/repo/2010/trunk/transport
cd transport
mvn clean install
cd transport-service
mvn clean package

The above commands should build the distribution package: target/transport-service-<version>-bin.tar.gz

o Then follow the "Installation from binary" instructions


DIRECTORY CONTENTS

The Transport Service distribution contains the following directories:

o LICENSE.txt : license file

o README.txt : this file, containin installation, configuration and testing instructions

o config/: directory containing all configuration files, which need to be customized for each deployment

o lib/: directory containing dependency jars, including the Transport Service classes (i.e. this module) packaged as a jar

o testdata/: directory containing some test files

o web-grid.war: the product servlet web application


CONFIGURATION

Note: the distribution comes pre-configured for an installation location of $PDS_HOME=/usr/local/pds.
If you have installed the package in this standard location, the changes to the configuration files will be minimal. 
If not, a few files must be edited to insert the custom system paths, as described below.

o Deploy the web-grid.war file to your existing Tomcat installation through a context file:

cp config/transport-service.xml $CATALINA_HOME/conf/Catalina/localhost/transport-service.xml

o Insert your specific installation location for $PDS_HOME, if different than "/usr/local/pds". The following three files must be edited:
	
	- $CATALINA_HOME/conf/Catalina/localhost/transport-service.xml: change the following 3 references:
		- the location of the war file: 
		  <Context path="/transport-service" docBase="/usr/local/pds/transport-service/web-grid.war"
		- the reference to the full path of config.xml:
		  <Parameter name="org.apache.oodt.grid.GridServlet.config" value="/usr/local/pds/transport-service/config/config.xml"/>
		- the location of the "lib/" directory for the Tomcat class loader:
		  <Loader className="org.apache.catalina.loader.VirtualWebappLoader" virtualClasspath="/usr/local/pds/transport-service/lib/*.jar" />
	
	- $PDS_HOME/transport-service/config/config.xml: change the location of the ofs-ps.xml file:
	  <property key="org.apache.oodt.product.handlers.ofsn.xmlConfigFilePath">/usr/local/pds/transport-service/config/ofsn-ps.xml</property>
	
	- $PDS_HOME/transport-service/config/ofsn-ps.xml: change the location of the "productRoot" directory. 
	  For now, insert the full path to the directory containing the test data:
	  productRoot="/usr/local/pds/transport-service/testdata"

o Restart Tomcat to load the web-grid application with the new configuration. 

TESTING

First of all, you should test that the Transport Service web application is up and running. To do, type the following URL in your browser:

http://localhost:8080/transport-service/

(assuming your Tomcat is deployed at the default URL localhost:8080, otherwise change the URL accordingly). 
You should see a simple introductory page with a few links.

Out of the box, the PDS Transport Service is configured to serve products in the directory $PDS_HOME/transport_service/testdata
(referenced as "productRoot" in the file ofsn-ps.xml). You can test your installation by accessing the following URL:

http://localhost:8080/transport-service/prod?q=OFSN=/i943630r.xml+AND+RT%3DPDS4_TO_JPG

If you see a JPG image, your installation was successful! If not, please look into the Tomcat logs for any error messages.

DEPLOYING IN PRODUCTION

To serve products from a real production directory, you must edit the file $PDS_HOME/config/ofsn-ps.xml and change the value of 
the "productRoot" attribute to the top-level directory containing your products.

