#!/bin/sh
# Copyright 2010-2012, by the California Institute of Technology. 
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

# This script traverses the module directories to deploy the artifacts 
# to the Maven repository.

cd ..
mvn clean

# Traverse the modules and selectively deploy artifacts.

mvn deploy --non-recursive

cd preparation
mvn deploy --non-recursive
cd core
mvn site
mvn deploy
cd ../pds4-tools
mvn site
mvn deploy
cd ../generate
mvn site
mvn deploy
cd ../transform
mvn site
mvn deploy
cd ../validate
mvn site
mvn deploy
cd ../..

cd registry
mvn deploy --non-recursive
cd registry-core
mvn site
mvn deploy
cd ../registry-service
mvn site
mvn deploy
cd ../registry-ui
mvn site
mvn deploy
cd ../..

cd ingest
mvn deploy --non-recursive
cd catalog
mvn site
mvn deploy
cd ../harvest
mvn site
mvn deploy
cd ../..

cd portal
mvn deploy --non-recursive
cd ds-view
mvn site
mvn deploy
cd ../product-query
mvn site
mvn deploy
cd ../..

cd report
mvn deploy --non-recursive
cd rs-update
mvn site
mvn deploy
cd ../profile-setup
mvn site
mvn deploy
cd ../..

cd search
mvn deploy --non-recursive
cd search-core
mvn site
mvn deploy
cd ../search-service
mvn site
mvn deploy
cd ../search-ui
mvn site
mvn deploy
cd ../..

cd storage
mvn site
mvn deploy
cd ..

cd transport
mvn deploy --non-recursive
cd cas-product
mvn site
mvn deploy
