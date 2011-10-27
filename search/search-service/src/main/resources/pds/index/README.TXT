Adding hierarchies to the index
-------------------------------

We're assuming there are 2 base files, one fed from the catalog, for all catalog
items, and one hand-curated (for now) in this directory, search-tools.xml, which
describes all the available search tools and their association with catalog items.

Let's assume they are:

solr_index.xml - Index from the catalog.
search-tools.xml - Search tool info.

Running XSLT 2.0: I'm using a shell script to run the Saxon-B XSLT 2.0 processor.
The Saxon JAR files are in the WEB-INF/lib directory of the Solr search web app,
but could be copied anywhere. A basic shell script:

-----
#! /bin/bash

export SAXON_LIB=path-to-saxon-jars
CLASSPATH=`echo $SAXON_LIB/*.jar | sed -e 's/ /:/g'`

if [ $# -lt 2 ]; then
  echo "usage: $0 xmlFile stylesheet [outFile]"
  exit 1
fi

IN="-s:$1"
STYLESHEET="-xsl:$2"
shift 2

if [ $# -lt 1 ]; then
  java -jar $SAXON_LIB/saxon9.jar "$IN" "$STYLESHEET"
else
  OUT="-o:$1"
  shift
  echo "java -Xmx256m -jar $SAXON_LIB/saxon9.jar \"$IN\" \"$STYLESHEET\" \"$OUT\" \"$@\""
  java -Xmx256m -jar $SAXON_LIB/saxon9.jar "$IN" "$STYLESHEET" "$OUT" "$@"
fi
-----

OK, assuming the above script is available as "xslt", then here's how to create the
final index files with the hierarchy:

xslt solr-index.xml add-hierarchy.xslt solr-index.hierarchy.xml
xslt search-tools.xml add-hierarchy.xslt search-tools.hierarchy.xml

Then, push these into Solr using curl or wget. Using curl, it would be:

SOLR_UPDATE="http://127.0.0.1/app/pds/update"

# Delete all existing documents.
curl "$SOLR_UPDATE" -H "Content-Type: text/xml" --data-binary '<delete><query>*:*</query></delete>'

# Push new index data
curl "$SOLR_UPDATE" -H "Content-Type: text/xml" --data-binary @solr-index.hierarchy.xml
curl "$SOLR_UPDATE" -H "Content-Type: text/xml" --data-binary @search-tools.hierarchy.xml

# Commit the changes
curl "$SOLR_UPDATE" -H "Content-Type: text/xml" --data-binary '<commit />'
