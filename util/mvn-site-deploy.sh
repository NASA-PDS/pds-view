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
# the associated sites for the CM web site.

cd ..
mvn clean

# Build each site (recursive).
mvn site

# Go back through the modules and create the PDFs.

cd harvest
maven pdf
cd ..

cd preparation/core
maven pdf
cd ../design
maven pdf
cd ../validate
maven pdf
cd ../..

cd registry/registry-core
maven pdf
cd ../registry-service
maven pdf
cd ../registry-ui
maven pdf
cd ../..

#cd report/profile-setup
#maven pdf
#cd ../sawmill
#maven pdf
#cd ../..

cd report
maven pdf
cd ..

cd security
maven pdf
cd ..

# Deploy each site (recursive) including the generated PDFs.
mvn site:deploy
