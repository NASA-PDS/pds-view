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
This script queries a registry service instance for a list of extrinsics
filtered optionally by Object Type and LID wildcard. The extrinsics are 
listed in CSV format to standard out with fields: lid, version_id, name 
and status.
'''

from pds.registry.net import PDSRegistryClient
import sys

# Get the command-line arguments.
argvLen = len(sys.argv)
if argvLen < 2 or argvLen > 4:
  print "Usage:", sys.argv[0], "<registry-url> [<object-type> [<lid-prefix>]]"  
  sys.exit(1)
elif argvLen == 2:
  registryUrl = sys.argv[1]
elif argvLen == 3:
  registryUrl = sys.argv[1]
  objectType = sys.argv[2]
elif argvLen == 4:
  registryUrl = sys.argv[1]
  objectType = sys.argv[2]
  lidPrefix = sys.argv[3]

# Initialize the registry clients.
rc = PDSRegistryClient(registryUrl)

# Query the registry service for the registered extrinsics.
count = 1
start = 0
rows = 50
while count > 0:
  extrinsics = rc.getExtrinsics(start, rows)
  count = len(extrinsics)
  start = start + rows
  for extrinsic in extrinsics:
    versionId = ""
    for slot in extrinsic.slots:
      if slot.name == "version_id":
        versionId = slot.values[0]
    if argvLen == 2:
      print extrinsic.lid + "," + versionId + "," + extrinsic.name + "," + extrinsic.status
    elif argvLen == 3:
      if extrinsic.objectType == objectType:
        print extrinsic.lid + "," + versionId + "," + extrinsic.name + "," + extrinsic.status
    elif argvLen == 4:        
      if extrinsic.objectType == objectType and extrinsic.lid.startswith(lidPrefix):
        print extrinsic.lid + "," + versionId + "," + extrinsic.name + "," + extrinsic.status

sys.exit(0)
