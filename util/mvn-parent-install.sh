#!/bin/sh
# Copyright 2018, by the California Institute of Technology. 
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

# This script specifically installs parent POMs in the local respository.
# This is very useful after bumping all of the versions for a release or
# development of the next release.

cd ..

# Install the parent POMs locally. Only necessary for versioned parents.
cd email
mvn --non-recursive install clean
cd ..

cd model
mvn --non-recursive install clean
cd ..

cd registry
mvn --non-recursive install clean
cd ..

cd report
mvn --non-recursive install clean
cd ..

cd search
mvn --non-recursive install clean
cd ..

cd tracking
mvn --non-recursive install clean
