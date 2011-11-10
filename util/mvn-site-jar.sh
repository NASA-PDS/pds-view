#!/bin/sh
# Copyright 2010-2011, by the California Institute of Technology. 
# ALL RIGHTS RESERVED. United States Government sponsorship acknowledged. 
# Any commercial use must be negotiated with the Office of Technology Transfer 
# at the California Institute of Technology. 
#
# This software is subject to U. S. export control laws and regulations 
# (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software 
# is subject to U.S. export control laws and regulations, the recipient has 
# the responsibility to obtain export licenses or other export authority as 
# may be required before exporting such information to foreign countries or 
# providing access to foreign nationals.
#
# $Id$

# This script traverses the module directories to build and create JAR 
# files for the associated sites.

cd ..
mvn --file pom-en.xml clean

# Install the dependent JARs locally.
cd preparation
mvn --non-recursive install clean
cd core
mvn install clean
cd ../..

cd registry
mvn --non-recursive install clean
cd registry-core
mvn install clean
cd ../..

cd report
mvn --non-recursive install clean
cd rs-update
mvn install clean
cd ../..

cd search
mvn --non-recursive install clean
cd search-core
mvn install clean
cd ../..

# Build each site (recursive).
mvn --file pom-en.xml site

# Go back through the modules and create the PDFs.

cd harvest
maven pdf
cd ..

cd preparation/core
maven pdf
cd ../design
maven pdf
cd ../generate
maven pdf
cd ../validate
maven pdf
cd ../..

cd registry
mvn --file pom-en.xml --non-recursive site
cd registry-core
maven pdf
cd ../registry-service
maven pdf
cd ../registry-ui
maven pdf
cd ../..

cd report
mvn --file pom-en.xml --non-recursive site
cd rs-update
maven pdf
cd ../profile-setup
maven pdf
cd ../sawmill
maven pdf
cd ../..

cd search
mvn --file pom-en.xml --non-recursive site
cd search-core
maven pdf
cd ../search-service
maven pdf
cd ../..

cd security
maven pdf
cd ..

cd storage
maven pdf
cd ..

# Create a JAR of each site (recursive) including the generated PDFs.
mvn --file pom-en.xml site:jar
