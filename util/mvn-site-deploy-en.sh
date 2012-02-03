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

# This script traverses the module directories to build and deploy 
# the associated sites with the EN skin to a deployment directory.

if [ $# != 1 ] ; then
  echo "Usage $0 <deployment-directory>"
  exit 1
fi

cd ..
mvn clean

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

# Rebuild the project sites that have their own pom-en.xml.
cd registry
mvn --file pom-en.xml --non-recursive clean site
cd ..

cd report
mvn --file pom-en.xml --non-recursive clean site
cd ..

cd search
mvn --file pom-en.xml --non-recursive clean site
cd ..

# Deploy the sites to the deployment directory.
echo
echo "Creating deployment directory $1"
mkdir -p $1
echo "Deploying top-level site to the deployment directory."
cp -r target/site/* $1

echo "Deploying Ingest component sites to the deployment directory."
mkdir -p $1/ingest
cp -r ingest/target/site/* $1/ingest
mkdir -p $1/ingest/catalog
cp -r ingest/catalog/target/site/* $1/ingest/catalog
mkdir -p $1/ingest/harvest
cp -r ingest/harvest/target/site/* $1/ingest/harvest

echo "Deploying Preparation component sites to the deployment directory."
mkdir -p $1/preparation
cp -r preparation/target/site/* $1/preparation
mkdir -p $1/preparation/core
cp -r preparation/core/target/site/* $1/preparation/core
mkdir -p $1/preparation/design
cp -r preparation/design/target/site/* $1/preparation/design
mkdir -p $1/preparation/generate
cp -r preparation/generate/target/site/* $1/preparation/generate
mkdir -p $1/preparation/validate
cp -r preparation/validate/target/site/* $1/preparation/validate

echo "Deploying Registry component sites to the deployment directory."
mkdir -p $1/registry
cp -r registry/target/site/* $1/registry
mkdir -p $1/registry/registry-service
cp -r registry/registry-service/target/site/* $1/registry/registry-service
mkdir -p $1/registry/registry-ui
cp -r registry/registry-ui/target/site/* $1/registry/registry-ui

echo "Deploying Report component site to the deployment directory."
mkdir -p $1/report
cp -r report/target/site/* $1/report

echo "Deploying Search component site to the deployment directory."
mkdir -p $1/search
cp -r search/target/site/* $1/search

echo "Deploying Security component site to the deployment directory."
mkdir -p $1/security
cp -r security/target/site/* $1/security
