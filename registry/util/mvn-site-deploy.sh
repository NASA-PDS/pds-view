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

# This script traverses the module directories to build and deploy
# the associated sites to the repository.

cd ..
mvn clean

cd registry-core
mvn install clean
cd ..

mvn site

cd registry-core
maven pdf
cd ../registry-service
maven pdf
cd ../registry-ui
maven pdf

cd ..
mvn site:deploy
