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
This script tests the various aspects of creating, putting, retrieving
and deleting extrinsics.
'''

from pds.registry.net import PDSRegistryClient
from pds.registry.model.classes import ExtrinsicObject
import sys

# Get the command-line arguments.
if len(sys.argv) < 2:
  print "Usage:", sys.argv[0], "<registry-url>"
  sys.exit(1)
else:
  registryUrl = sys.argv[1]

# Initialize the registry clients.
rc = PDSRegistryClient(registryUrl)

# Register a test product.
try:
  extrinsic = ExtrinsicObject(None, 'urn:nasa:pds:test', None, None, 'Test')
  rc.putExtrinsic(extrinsic, False)
  print 'Successfully registered the test product.'
except Exception as e:
  print 'An error occurred registering the test product. ' + str(e)
  sys.exit(1)

# Retrieve the test product by LIDVID.
try:
  extrinsic = rc.getExtrinsicByLidvid('urn:nasa:pds:test::1.0')
  print 'Successfully retrieved the test product by LIDVID. GUID: ' + extrinsic.guid
except Exception as e:
  print 'An error occurred retrieving the test product. ' + str(e)
  sys.exit(1)

# Replace the test product.
try:
  extrinsic.description = 'This is the replaced product.'
  rc.putExtrinsic(extrinsic, True)
  print 'Successfully replaced the test product.'
except Exception as e:
  print 'An error occurred replacing the test product. ' + str(e)
  sys.exit(1)

# Register a new version of the test product.
try:
  extrinsic.description = 'This is the new version of the product.'
  rc.putExtrinsic(extrinsic, False)
  print 'Successfully registered a new version of the test product.'
except Exception as e:
  print 'An error occurred registering a new version of the test product. ' + str(e)
  sys.exit(1)

# Retrieve the new test product by LIDVID.
try:
  extrinsicNew = rc.getExtrinsicByLidvid('urn:nasa:pds:test::2.0')
  print 'Successfully retrieved the new test product by LIDVID. GUID: ' + extrinsicNew.guid
except Exception as e:
  print 'An error occurred retrieving the new test product. ' + str(e)
  sys.exit(1)

# Retrieve the earliest test product by LID.
try:
  extrinsicEarliest = rc.getExtrinsicByLID('urn:nasa:pds:test', True)
  print 'Successfully retrieved the earliest test product by LID. GUID: ' + extrinsicEarliest.guid
except Exception as e:
  print 'An error occurred retrieving the earliest test product. ' + str(e)
  sys.exit(1)

# Retrieve the latest test product by LID.
try:
  extrinsicLatest = rc.getExtrinsicByLID('urn:nasa:pds:test')
  print 'Successfully retrieved the latest test product by LID. GUID: ' + extrinsicLatest.guid
except Exception as e:
  print 'An error occurred retrieving the latest test product. ' + str(e)
  sys.exit(1)

# Delete the test products.
try:
  # Re-retrieve the original test product.
  extrinsic = rc.getExtrinsicByLidvid('urn:nasa:pds:test::1.0')
  rc.deleteExtrinsic(extrinsic.guid)
  rc.deleteExtrinsic(extrinsicNew.guid)
  print 'Successfully deleted the test products.'
except Exception as e:
  print 'An error occurred deleting the test products. ' + str(e)
  sys.exit(1)

# Replace the test product after deletion.
try:
  rc.putExtrinsic(extrinsic, True)
  print 'An error occurred because we successfully replaced the test product when it was not registered.'
  sys.exit(1)
except Exception as e:
  print 'Successfully generated an error attempting to replace the test product when it was not registered: ' + str(e)
  sys.exit(0)

sys.exit(0)
