#!/bin/sh
# Copyright 2010, by the California Institute of Technology. 
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

# This script traverses the module directories deploy the artifacts 
# to the Maven repository.

cd ..
mvn clean

# Traverse the modules and selectively deploy artifacts.

mvn deploy --non-recursive

cd preparation
mvn deploy --non-recursive
cd core
mvn site
maven pdf
mvn deploy
cd ../validate
mvn site
maven pdf
mvn deploy

#cd ../registry/registry-core
#maven pdf
#cd ../registry-service

cd ../../registry
mvn deploy --non-recursive
cd registry-service
mvn site
maven pdf
mvn deploy
cd ../registry-ui
mvn site
maven pdf
mvn deploy

cd ../../harvest
mvn site
maven pdf
mvn deploy
