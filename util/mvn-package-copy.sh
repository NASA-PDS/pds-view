#!/bin/sh
# Copyright 2012-2016, by the California Institute of Technology. 
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

# This script creates a package of the package artifacts so that they
# can be copied to the FTP site.

cd ..
mkdir -p target/packages
cd target/packages

# Selectively copy the artifacts.

echo "Copying the Ingest packages to the package directory."
cp ../../ingest/catalog/target/catalog*bin* .
cp ../../ingest/harvest/target/harvest*bin* .
cp ../../ingest/harvest-pdap/target/harvest-pdap*bin* .

echo "Copying the Migrate packages to the package directory."
cp ../../migrate/migrate-tools/target/migrate-tools*bin* .

echo "Copying the Model packages to the package directory."
cp ../../model/model-dmdocument/target/model-dmdocument*bin* .
cp ../../model/model-lddtool/target/lddtool*bin* .
cp ../../model/model-ontology/target/model-ontology*bin* .

echo "Copying the Portal packages to the package directory."
cp ../../portal/ds-view/target/ds-view*bin* .
cp ../../portal/filter/target/filter*bin* .

echo "Copying the Preparation packages to the package directory."
cp ../../preparation/core/target/core*bin* .
cp ../../preparation/generate/target/generate*bin* .
cp ../../preparation/pds4-tools/target/pds4-tools*bin* .
cp ../../preparation/transform/target/transform*bin* .
cp ../../preparation/validate/target/validate*bin* .

echo "Copying the Registry packages to the package directory."
cp ../../registry/registry-core/target/registry-core*bin* .
cp ../../registry/registry-service/target/registry-service*bin* .
cp ../../registry/registry-tools/target/registry-tools*bin* .
cp ../../registry/registry-ui/target/registry-ui*bin* .

echo "Copying the Report packages to the package directory."
cp ../../report/report-manager/target/report-manager*bin* .

echo "Copying the Search packages to the package directory."
cp ../../search/product-search-ui/target/product-search-ui*bin* .
cp ../../search/search-analytics/target/search-analytics*bin* .
cp ../../search/search-core/target/search-core*bin* .
cp ../../search/search-service/target/search-service*bin* .
cp ../../search/search-ui/target/search-ui*bin* .

echo "Copying the Storage packages to the package directory."
cp ../../storage/product-service/target/product-service*bin* .
cp ../../storage/storage-service/target/storage-service*bin* .

echo "Copying the Transport packages to the package directory."
cp ../../transport/transport-ofsn/target/transport-ofsn*bin* .
cp ../../transport/transport-proxy/target/transport-proxy*bin* .
cp ../../transport/transport-registry/target/transport-registry*bin* .
cp ../../transport/transport-upload/target/transport-upload*bin* .

# Create the package of packages

echo "Creating the package of packages."
cd ..
tar czvf packages.tar.gz ./packages
