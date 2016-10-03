#!/bin/sh
# Copyright 2010-2016, by the California Institute of Technology. 
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

# This script traverses the module directories to build and deploy
# the associated sites for the CM web site.

cd ..
mvn clean

# Install the dependent JARs locally.
cd model
mvn --non-recursive install clean
cd model-dmdocument
mvn install clean
cd ../..

cd preparation
mvn --non-recursive install clean
cd core
mvn install clean
cd ../generate
mvn install clean
cd ../pds4-tools
mvn install clean
cd ../transform
mvn install clean
cd ../..

cd registry
mvn --non-recursive install clean
cd registry-core
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

# Build Registry Service site because WADL generation is failing
# when executing "mvn site-deploy" from the root.
cd registry/registry-service
mvn site
cd ../..

# Build and deploy each site (recursive).
mvn site-deploy
