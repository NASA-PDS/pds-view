The UCD Tools project contains software for validating PDS3 volumes 
and for exploring PDS3 and PDS4 tables. See the INSTALL.txt file for 
details on building and deploying the web application.

This rest of this document describes the steps required to configure 
your system in order to develop the TABLE EXPLORER in Eclipse.

Pre-requisites:
    Eclipse Java EE IDE for Web Developers
    Maven 2.x or greater
    MYSQL server (MAMP or BitNami Django Stack (bitnami.com/stack/django))

SETUP
    0. Download the latest version of Eclipse Java EE IDE for Web Developers
    1. Install Maven Integration for Eclipse
    2. Download MAMP or similar utility and install it. This is nice to have in order to view the database by going
       to http://localhost/MAMP or use the command line to view the tables using mysql. To connect to the MySQL server
       from own scripts or application, configure MAMP to use the following parameters:
                Apache Port: 80
                MySQL Port: 3306
                User: root
                Password: root
    3. Download Maven 2.x or greater from Apache website. Untar and copy the project directory to /usr/local
    4. Add the following commands to .bash_profile and source it. Do the following:
                export JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Home
                export PATH=/usr/local/apache-maven-2.2.1/bin:$PATH
                export MAVEN_OPTS="-Xmx1024m"
    5. Verify mvn was installed properly by running the following command
            mvn --version  
    6. From the command line, enter the following commands:
            a. cd <path to pds_web>
            b. run: mvn clean install -DskipTests
            c. run: ant init-eclipse
    7. Start Eclipse Java EE IDE for Web Developers
    8. Check out this project from SVN (You may need to install Subversion for Eclipse)
    9. (Optional) Import codeFormat.xml in Window -> Preferences -> Java -> Code Style->Formatter -> Import...
    10. Modify the settings in config.properties to connect to mysql
        - By default, mysql is at port 3306
        - Jetty runs in port 8888
    11. Refresh the project within Eclipse
    
To run pds_web, using mvn
    0. Execute the following script found in <path to pds_web>: ./jetty-run
    1. Go to the pds_web website: http://localhost:8888/pdsWeb/Home.action

To debug using mvn:

    0. Place the breakpoint in the code
    1. Run ./jetty-debug script to start Jetty
    2. In Eclipse, select 'pds_web Remote Debugging' launch configuration
    3. Go to the pds_web website: http://localhost:8888/pdsWeb/Home.action
    
 To run unit test:
 	0. Run the following command: mvn -Dtest=TabularDataTest test -DfailIfNoTests=false
