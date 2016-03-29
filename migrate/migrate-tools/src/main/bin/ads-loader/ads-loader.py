#!/usr/bin/env python
#
# Copyright 2014, by the California Institute of Technology
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
This script reads a text file containing product identifiers and makes a call 
to the profile2registry application to register that product with the 
Registry Service. The input file should look like the following:

GO-J/JSA-SSI-2-REDR-V1.0:G1G0030
GO-J/JSA-SSI-2-REDR-V1.0:G1G0031
...
'''

import os, sys

# Get the command-line arguments.
if len(sys.argv) < 2:
  print "Usage:", sys.argv[0], "<input-file>"
  sys.exit(1)
else:
  inputFileArg = sys.argv[1];

# Read the input file and register each product.
totalProducts = 0
successProducts = 0
errorProducts = 0
with open(inputFileArg) as inputFile:
  for line in inputFile:
    identifier = line.strip()
    totalProducts += 1
    print "Registering product " + identifier
    command = "profile2registry -r http://localhost:8080/registry " + identifier
    if os.system(command) != 0:
      errorProducts += 1
    else:
      successProducts += 1

# Print out the summary.
print
print "Summary:"
print "  Total products attempted: " + str(totalProducts)
print "  " + str(successProducts) + " products registered"
print "  " + str(errorProducts) + " products failed"

sys.exit(0)
