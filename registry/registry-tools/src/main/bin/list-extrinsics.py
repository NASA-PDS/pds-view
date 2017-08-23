#!/usr/bin/env python
#
# Copyright 2016, by the California Institute of Technology
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
filtered by Object Type and LID wildcard. The extrinsics are listed in CSV
format to standard out with fields: lid, version_id, name and status.
'''
from pds.registry.net import PDSRegistryClient
import sys

# Get the command-line arguments.
if len(sys.argv) < 4:
  print "Usage:", sys.argv[0], "<registry-url> <object-type> <lid-prefix>"
  sys.exit(1)
else:
  registryUrl = sys.argv[1]
  objectType = sys.argv[2]
  lidPrefix = sys.argv[3]

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
    if extrinsic.objectType == objectType:
      if extrinsic.lid.startswith(lidPrefix):
        versionId = ""
        for slot in extrinsic.slots:
          if slot.name == "version_id":
            versionId = slot.values[0]
        print extrinsic.lid + "," + versionId + "," + extrinsic.name + "," + extrinsic.status

sys.exit(0)
