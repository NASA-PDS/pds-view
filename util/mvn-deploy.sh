#!/bin/sh
# Copyright 2010-2017, by the California Institute of Technology. 
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

cd email
mvn deploy --non-recursive
cd email-core
mvn site
mvn deploy
cd ../email-service
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
cd ../harvest-pdap
mvn site
mvn deploy
cd ../..

cd migrate
mvn deploy --non-recursive
cd migrate-tools
mvn site
mvn deploy
cd ../..

cd model
mvn deploy --non-recursive
cd model-dmdocument
mvn site
mvn deploy
cd ../model-lddtool
mvn site
mvn deploy
cd ../model-ontology
mvn site
mvn deploy
cd ../..

cd portal
mvn deploy --non-recursive
cd ds-view
mvn site
mvn deploy
cd ../filter
mvn site
mvn deploy
cd ../..

cd preparation
mvn deploy --non-recursive
cd core
mvn site
mvn deploy
cd ../generate
mvn site
mvn deploy
cd ../pds4-tools
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
cd registry-client
python bootstrap.py
bin/buildout setup . egg_info -b "" sdist
cd ../registry-core
mvn site
mvn deploy
cd ../registry-service
mvn site
mvn deploy
cd ../registry-tools
mvn site
mvn deploy
cd ../registry-ui
mvn site
mvn deploy
cd ../..

cd report
mvn deploy --non-recursive
cd report-manager
mvn site
mvn deploy
cd ../..

cd search
mvn deploy --non-recursive
cd search-core
mvn site
mvn deploy
cd ../product-search-ui
mvn site
mvn deploy
cd ../search-analytics
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
mvn deploy --non-recursive
cd product-service
mvn site
mvn deploy
cd ../storage-service
mvn site
mvn deploy
cd ../..

cd transport
mvn deploy --non-recursive
cd transport-ofsn
mvn site
mvn deploy
cd ../transport-proxy
mvn site
mvn deploy
cd ../transport-registry
mvn site
mvn deploy
cd ../transport-upload
mvn site
mvn deploy
cd ../..
