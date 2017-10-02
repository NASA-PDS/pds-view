#!/usr/bin/env python
#
# Copyright 2017, by the California Institute of Technology
# ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
# Any commercial use must be negotiated with the Office of Technology Transfer 
# at the California Institute of Technology.
# 
# This software may be subject to U.S. export control laws and regulations. 
# By accepting this document, the user agrees to comply with all applicable 
# U.S. export laws and regulations. User has the responsibility to obtain 
# export licenses, or other export authority as may be required before 
# exporting such information to foreign countries or providing access to 
# foreign persons.
#
# $Id$

'''
This script replaces a slot value where the source-string is replaced by
the replace-string for a list of extrinsics filtered optionally by Object 
Type and LID wildcard.
'''

from pds.registry.net import PDSRegistryClient
import sys

# Get the command-line arguments.
argvLen = len(sys.argv)
if argvLen < 5 or argvLen > 7:
    print "Usage:", sys.argv[0], "<registry-url> <slot-name> <source-string> "
    + "<replace-string> [<object-type> [<lid-prefix>]]"  
    sys.exit(1)
elif argvLen == 5:
    registryUrl = sys.argv[1]
    slotName = sys.argv[2]
    sourceString = sys.argv[3]
    replaceString = sys.argv[4]
    objectType = None
    lidPrefix = None
elif argvLen == 6:
    registryUrl = sys.argv[1]
    slotName = sys.argv[2]
    sourceString = sys.argv[3]
    replaceString = sys.argv[4]
    objectType = sys.argv[5]
    lidPrefix = None
elif argvLen == 7:
    registryUrl = sys.argv[1]
    slotName = sys.argv[2]
    sourceString = sys.argv[3]
    replaceString = sys.argv[4]
    objectType = sys.argv[5]
    lidPrefix = sys.argv[6]

# Initialize the registry clients.
rc = PDSRegistryClient(registryUrl)

# Query the registry service for the desired extrinsics.
count = 1
start = 0
rows = 50
while count > 0:
    extrinsics = rc.getExtrinsics(start, rows)
    count = len(extrinsics)
    start = start + rows
    for extrinsic in extrinsics:
        bCont = True
        if (objectType is not None) and (extrinsic.objectType != objectType):
            bCont = False
        if (lidPrefix is not None) and (extrinsic.lid.startswith(lidPrefix)):
            bCont = False
        if bCont:
            for slot in extrinsic.slots:
                if slot.name == slotName:
                    for i, value in enumerate(slot.values):
                        slot.values[i] = slot.values[i].replace(sourceString, 
                                                                replaceString)
        # Put the updated extrinsic back in the Registry.
        rc.putExtrinsic(extrinsic, True)

sys.exit(0)
