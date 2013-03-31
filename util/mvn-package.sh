#!/bin/sh
# Copyright 2012-2013, by the California Institute of Technology. 
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

# This script traverses the module directories to package the artifacts.

cd ..
mvn clean

# Install the dependent JARs locally.
cd preparation
mvn --non-recursive install clean
cd core
mvn install clean
cd ../pds4-tools/superpom
mvn clean install clean
cd ../packages
mvn clean install clean
cd ../../transform
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

cd storage
mvn --non-recursive install clean
cd storage-service
mvn install clean
cd ../..

# Build each site (recursive).
mvn site

# Package each module (recursive).
mvn package
