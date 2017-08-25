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
This script queries a registry service instance and generates a report
of the extrinsics registered with that instance.
'''

from pds.registry.net import PDSRegistryClient
import sys

# Get the command-line arguments.
if len(sys.argv) < 2:
  print "Usage:", sys.argv[0], "<registry-url>"
  sys.exit(1)
else:
  registryUrl = sys.argv[1]

# Initialize the registry clients.
rc = PDSRegistryClient(registryUrl)

# Set an empty dictionary
objTypeDic = {}

# Query the registry service for the registered extrinsics.
count = 1
start = 0
rows = 50

print "Extrinsic Report for ", registryUrl
while count > 0:
  extrinsics = rc.getExtrinsics(start, rows)
  count = len(extrinsics)
  start = start + rows
  for extrinsic in extrinsics:
    # Capture counts by extrinsic.objectType
    if objTypeDic.has_key(extrinsic.objectType) == False:
    	objTypeDic[extrinsic.objectType] = 1
    else:
    	objTypeDic[extrinsic.objectType] = objTypeDic[extrinsic.objectType] + 1
    
# Print counts for each objectType
for k in sorted(objTypeDic.keys()):
	print " " + k + ": " + str(objTypeDic[k])

sys.exit(0)
