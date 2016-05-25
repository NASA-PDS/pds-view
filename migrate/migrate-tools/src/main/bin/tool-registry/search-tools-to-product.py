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

'''
This script reads an XML file specifying tools and outputs XML specifying those
tools in a format that can be read by the Registry Service.
'''

import copy
import os.path
import re
import sys
import time

def getTitle(product):
    if not "title" in product:
        print "No title found in product!"
        return None
    return product["title"]

def getTabs(n):
    tabs = ""
    for i in range(n * 2):
        tabs = tabs + " "
    return tabs

def getFieldWithDefault(product, key, default):
    if key in product:
        return product[key]
    else:
        return default

def getXmlElement(product, key, tag):
    value = product[key]
    return "<" + tag + ">" + value + "</" + tag + ">\n"

def makeUrnSafe(str):
    '''
    Obtain a lower case version of the string, replacing spaces with
    underscores and replacing special characters with dashes.
    '''
    safe = str.lower()
    safe = safe.replace(" ", "_")
    safe = re.sub("[^a-zA-Z0-9_]", "-", safe)
    return safe




# Get the command-line arguments.
if len(sys.argv) < 3:
    print "Insufficient number of arguments: " + str(len(sys.argv) - 1)
    print "Usage: " + sys.argv[0] + " <input-file> <output-location>"
    sys.exit(1)
else:
    inputFileArg = sys.argv[1];
    outputLocation = sys.argv[2];

# Verify that input file exists and has contents
if not os.path.exists(inputFileArg):
    print "No file exists at given input location: " + os.path.abspath(inputFileArg)
    sys.exit(1)
if os.path.getsize(inputFileArg) == 0:
    print "The input file is empty: " + os.path.abspath(inputFileArg)
    sys.exit(1)

# Verify that the output location exists
if not os.path.exists(outputLocation):
    print "Output location does not exist: " + os.path.abspath(outputLocation)
    sys.exit(1)

# Read in the input file
productList = list()
product = None
with open(inputFileArg) as inputFile:
    for line in inputFile:
    
        # Create a new dict representing the product when we encounter the
        # beginning tag.  Otherwise, ignore everything outside of product
        # tags
        if product == None:
            if line.strip() == "<doc>":
                product = dict()
                
        else:
            
            # If we reach the ending tag of the product, add it to our product
            # list and get ready for the next one
            if line.strip() == "</doc>":
                if getTitle(product) != None:
                    productList.append(product)
                    product = None
            
            # Extract the product details from the line and add them to the
            # dict representing the product
            else:
                result = re.match("\s+<field name=\"(\w+)\">(.+)</field>\s*", line)
                if result == None:
                    print "Improperly formatted field line: " + line.strip()
                key = result.group(1)
                value = result.group(2)
                
                # Store unique fields as strings in the dict
                if key not in product:
                    product[key] = value
                    
                # Store non-unique fields as lists, creating lists when the
                # second instance of a field is encountered, and appending
                # all subsequent values
                else:
                    prevValue = product[key]
                    if type(prevValue) is list:
                        prevValue.append(value)
                    else:
                        newList = [prevValue, value]
                        product[key] = newList

# Check that the final product was properly finished
if product != None and getTitle(product) != None:
    print "Product end-tag not found. Partial product will still be used."
    productList.append(product)

# Reformat input contents to expected output format
outputList = list()
for product in productList:
    outputProduct = copy.deepcopy(product)
    
    # Populate details that simply always use defaults
    outputProduct["version_id"] = "1.0"
    outputProduct["information_model_version"] = "1.5.0.0"
    outputProduct["product_class"] = "Product_Service"
    outputProduct["description"] = "Migration from Search Service Search Tools list."
    outputProduct["service_type"] = "Service"
    outputProduct["interface_type"] = "GUI"
    outputProduct["category"] = "Search"
    
    # Create logical ID by making title lowercase, replacing spaces with
    # underscores, replacing special characters with dashes, and appending to
    # standard urn prefix
    oldTitle = getTitle(product)
    if oldTitle == None:
        continue    # Abandon this product if we can't find its title
    outputProduct["logical_identifier"] = "urn:nasa:pds:context_pds3:service:" + makeUrnSafe(oldTitle)
    
    # Set the modification date to todays date
    outputProduct["modification_date"] = time.strftime("%Y-%m-%d")
    
    # Set output details that map to other details
    outputProduct["name"] = product["title"]
    outputProduct["abstract_desc"] = product["description"]
    outputProduct["url"] = product["resource_link"]
    
    # Set output details that amp to other details but use defauls
    outputProduct["data_product_type"] = getFieldWithDefault(product, "data_product_type", "Product_Context_Search_Tool")
    outputProduct["format"] = getFieldWithDefault(product, "format", "XML/RDF")
    outputProduct["language"] = getFieldWithDefault(product, "language", "en")
    outputProduct["publisher"] = getFieldWithDefault(product, "publisher", "NASA.PDS")
    outputProduct["data_product_type"] = getFieldWithDefault(product, "resource_class", "data_set_description")
    
    # Add the product to the list of products to output
    outputList.append(outputProduct)

# Write the new XML
for product in outputList:
    
    # Warn the user if existing data will be clobbered
    title = getTitle(product)
    if title == None:
        continue
    title = makeUrnSafe(title)
    path = os.path.join(outputLocation, title + ".xml")
    if os.path.exists(path):
        print "Existing file will be overwritten: " + path
    
    # Open the output file
    
    output = open(path, "w")
    
    # Write the boilerplate
    output.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
    output.write("<?xml-model href=\"http://pds.jpl.nasa.gov/pds4/pds/v1/PDS4_PDS_1500.sch\" schematypens=\"http://purl.oclc.org/dsdl/schematron\"?>\n")
    output.write("<Product_Service xmlns=\"http://pds.nasa.gov/pds4/pds/v1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://pds.nasa.gov/pds4/pds/v1 https://pds.nasa.gov/pds4/pds/v1/PDS4_PDS_1500.xsd\">\n")
    
    # Write everything else
    output.write(getTabs(1) + "<Identification_Area>\n")
    output.write(getTabs(2) + getXmlElement(product, "logical_identifier", "logical_identifier"))
    output.write(getTabs(2) + getXmlElement(product, "version_id", "version_id"))
    output.write(getTabs(2) + getXmlElement(product, "title", "title"))
    output.write(getTabs(2) + getXmlElement(product, "information_model_version", "information_model_version"))
    output.write(getTabs(2) + getXmlElement(product, "product_class", "product_class"))
    output.write(getTabs(2) + "<Modification_History>\n")
    output.write(getTabs(3) + "<Modification_Detail>\n")
    output.write(getTabs(4) + getXmlElement(product, "modification_date", "modification_date"))
    output.write(getTabs(4) + getXmlElement(product, "version_id", "version_id"))
    output.write(getTabs(4) + getXmlElement(product, "description", "description"))
    output.write(getTabs(3) + "</Modification_Detail>\n")
    output.write(getTabs(2) + "</Modification_History>\n")
    output.write(getTabs(1) + "</Identification_Area>\n")
    output.write(getTabs(1) + "<Service>\n")
    output.write(getTabs(2) + getXmlElement(product, "name", "name"))
    output.write(getTabs(2) + getXmlElement(product, "abstract_desc", "abstract_desc"))
    output.write(getTabs(2) + getXmlElement(product, "url", "url"))
    output.write(getTabs(2) + getXmlElement(product, "service_type", "service_type"))
    output.write(getTabs(2) + getXmlElement(product, "interface_type", "interface_type"))
    output.write(getTabs(2) + getXmlElement(product, "category", "category"))
    output.write(getTabs(1) + "</Service>\n")
    
    # Once we have the Internal_References, we'll write them out here
    output.write(getTabs(1) + "<Reference_List>\n")
    output.write(getTabs(1) + "</Reference_List>\n")
    
    output.write("</Product_Service>\n")
    
    # Close the file
    output.close()
