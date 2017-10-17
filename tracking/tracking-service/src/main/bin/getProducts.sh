#!/usr/bin/bash

export APP_HOME=/Users/danyu/PDS-EN/PDS-EN_Eclipse-Neon_workspace/2010/tracking/tracking-service/target/tracking-service-0.2.0-dev
export LIBS=${APP_HOME}/lib

CLASSPATH=`echo ${LIBS}/*.jar | tr ' ' ':'`

export CLASSPATH

/usr/bin/java \
  -Dlog4j.configuration=file://${APP_HOME}/classes/log4j.xml \
  -DJDBC_CONSTS=${APP_HOME}/classes/mysql.properties \
 gov.nasa.pds.tracking.tracking.GetProducts $*