#!/bin/sh
# First remove the old catalog dump
rm -rf $TSE_BASEDIR/extract
# Create a dump of the catalog
java gov.nasa.pds.catex.catalog.CatalogExtractor $TSE_BASEDIR
# Index the catalog
java gov.nasa.pds.catex.index.Indexer $TSE_BASEDIR/index $TSE_BASEDIR/extract $TSE_BASEDIR/weights.txt
# Remove the old soft link
rm $TSE_BASEDIR/current_index
# Update the link to the index
ln -s $TSE_BASEDIR/index $TSE_BASEDIR/current_index
