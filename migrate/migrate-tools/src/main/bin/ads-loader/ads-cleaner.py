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
This script reads a text file containing product identifiers and creates a 
new file removing the duplicate identifiers. The input file should look like 
the following:

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

# Read the input file and write distinct identifiers to the output file.
outputFile = open(inputFileArg + ".clean", "w")
lastIdentifier = ""
with open(inputFileArg) as inputFile:
  for line in inputFile:
    if line == lastIdentifier:
      print "Skipping duplicate identifier: " + line.strip()
    else:
      outputFile.write(line)
      lastIdentifier = line

outputFile.close
inputFile.close

sys.exit(0)
