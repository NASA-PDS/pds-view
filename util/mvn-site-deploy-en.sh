#!/bin/sh
# Copyright 2010-2014, by the California Institute of Technology. 
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
cd ../generate
mvn install clean
# cd ../pds4-tools/superpom
# mvn clean install clean
# cd ../packages
# mvn clean install clean
# cd ../../transform
cd ../transform
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
mvn --file pom-en.xml site

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
mkdir -p $1/ingest/harvest-pdap
cp -r ingest/harvest-pdap/target/site/* $1/ingest/harvest-pdap

echo "Deploying Portal component sites to the deployment directory."
mkdir -p $1/portal
cp -r portal/target/site/* $1/portal
mkdir -p $1/portal/ds-view
cp -r portal/ds-view/target/site/* $1/portal/ds-view
mkdir -p $1/portal/filter
cp -r portal/filter/target/site/* $1/portal/filter

echo "Deploying Preparation component sites to the deployment directory."
mkdir -p $1/preparation
cp -r preparation/target/site/* $1/preparation
mkdir -p $1/preparation/core
cp -r preparation/core/target/site/* $1/preparation/core
mkdir -p $1/preparation/design
cp -r preparation/design/target/site/* $1/preparation/design
mkdir -p $1/preparation/generate
cp -r preparation/generate/target/site/* $1/preparation/generate
mkdir -p $1/preparation/pds4-tools
cp -r preparation/pds4-tools/target/site/* $1/preparation/pds4-tools
mkdir -p $1/preparation/transform
cp -r preparation/transform/target/site/* $1/preparation/transform
mkdir -p $1/preparation/validate
cp -r preparation/validate/target/site/* $1/preparation/validate

echo "Deploying Registry component sites to the deployment directory."
mkdir -p $1/registry
cp -r registry/target/site/* $1/registry
mkdir -p $1/registry/registry-client
cp -r registry/registry-client/target/site/* $1/registry/registry-client
mkdir -p $1/registry/registry-core
cp -r registry/registry-core/target/site/* $1/registry/registry-core
mkdir -p $1/registry/registry-service
cp -r registry/registry-service/target/site/* $1/registry/registry-service
mkdir -p $1/registry/registry-ui
cp -r registry/registry-ui/target/site/* $1/registry/registry-ui

echo "Deploying Report component site to the deployment directory."
mkdir -p $1/report
cp -r report/target/site/* $1/report
mkdir -p $1/report/profile-manager
cp -r report/profile-manager/target/site/* $1/report/profile-manager
mkdir -p $1/report/rs-update
cp -r report/rs-update/target/site/* $1/report/rs-update
mkdir -p $1/report/sawmill
cp -r report/sawmill/target/site/* $1/report/sawmill


echo "Deploying Search component site to the deployment directory."
mkdir -p $1/search
cp -r search/target/site/* $1/search
mkdir -p $1/search/product-search-ui
cp -r search/product-search-ui/target/site/* $1/search/product-search-ui
mkdir -p $1/search/search-core
cp -r search/search-core/target/site/* $1/search/search-core
mkdir -p $1/search/search-service
cp -r search/search-service/target/site/* $1/search/search-service
mkdir -p $1/search/search-ui
cp -r search/search-ui/target/site/* $1/search/search-ui

echo "Deploying Security component site to the deployment directory."
mkdir -p $1/security
cp -r security/target/site/* $1/security

echo "Deploying Storage component site to the deployment directory."
mkdir -p $1/storage
cp -r storage/target/site/* $1/storage
mkdir -p $1/storage/product-service
cp -r storage/product-service/target/site/* $1/storage/product-service
mkdir -p $1/storage/storage-service
cp -r storage/storage-service/target/site/* $1/storage/storage-service

echo "Deploying Transport component site to the deployment directory."
mkdir -p $1/transport
cp -r transport/target/site/* $1/transport
mkdir -p $1/transport/transport-ofsn
cp -r transport/transport-ofsn/target/site/* $1/transport/transport-ofsn
mkdir -p $1/transport/transport-proxy
cp -r transport/transport-proxy/target/site/* $1/transport/transport-proxy
mkdir -p $1/transport/transport-registry
cp -r transport/transport-registry/target/site/* $1/transport/transport-registry
