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
This script queries the IPDA Registry Service for Service entries and 
outputs a Product_Service label in XML for each of those entries. Those 
labels can then be ingested in a Registry Service instance. This script 
does not address all issues present in the registry entries. Some hand 
editing will be required to clean up spacing issues created by removing 
CRLF and Unicode characters.
'''

from pds.registry.net import PDSRegistryClient
import copy
import datetime
import os.path
import re
import string
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

def getXmlElements(value, tag):
    return "<" + tag + ">" + value + "</" + tag + ">\n"

def makeUrnSafe(str):
    '''
    Obtain a lower case version of the string, remove whitespace, 
    replace spaces with underscores, remove substrings with parenthesis,  
    and replace special characters with dashes.
    '''
    safe = str.lower()
    safe = safe.strip()
    safe = safe.replace(" ", "_")
    if (safe.find("(") != -1 and safe.find(")") != -1):
        safe = safe[:-(safe.find(")")-safe.find("("))-1]
        safe = safe.strip("_")
    safe = re.sub("[^a-zA-Z0-9_]", "-", safe)
    return safe

def removeStr(substr, str):
    index = 0
    length = len(substr)
    while string.find(str, substr) != -1:
        index = string.find(str, substr)
        str = str[0:index] + str[index+length:]
    return str


# Get the command-line arguments.
if len(sys.argv) < 3:
    print "Insufficient number of arguments: " + str(len(sys.argv) - 1)
    print "Usage: " + sys.argv[0] + " <registry-url> <output-location>"
    sys.exit(1)
else:
    registryUrl = sys.argv[1];
    outputLocation = sys.argv[2];

# Verify that the output location exists
if not os.path.exists(outputLocation):
    print "Output location does not exist: " + os.path.abspath(outputLocation)
    sys.exit(1)

# Initialize the registry client.
rc = PDSRegistryClient(registryUrl)

# Query the registry for the services. There are less than 50.
productList = list()
product = None
services = rc.getServices(0, 50)
for service in services:
    product = dict()
    product["name"] = service.name
    product["abstract_desc"] = service.description
    slots = service.slots
    for slot in slots:
        # Grab the single-valued slots.
        if slot.name == "abstract":
            product["description"] = slot.values[0]
        if slot.name == "curator-contactName":
            product["submitter_name"] = slot.values[0]
        if slot.name == "curator-description":
            product["submitter_institution"] = slot.values[0]
        if slot.name == "curator-emailAddress":
            product["submitter_email"] = slot.values[0]
        if slot.name == "releaseDate":
            product["release_date"] = slot.values[0]
        if slot.name == "requirements":
            product["system_requirements_note"] = slot.values[0]
        if slot.name == "toolURL":
            product["url"] = slot.values[0]

        # Grab the multi-valued slots.
        if slot.name == "categories":
            valueList = []
            for value in slot.values:
                valueList.append(value)
            product["category"] = valueList    
        if slot.name == "interfaceTypes":
            valueList = []
            for value in slot.values:
                valueList.append(value)
            product["interface_type"] = valueList    
        if slot.name == "operatingSystems":
            valueList = []
            for value in slot.values:
                valueList.append(value)
            product["supported_operating_system_note"] = valueList    

    for serviceBinding in service.serviceBindings:
        product["service_version_id"] = serviceBinding.versionName

    productList.append(product)
    product = None        

# Reformat input contents to expected output format
outputList = list()
for product in productList:
    outputProduct = copy.deepcopy(product)
    
    # Update information area defaults for the product label.
    outputProduct["title"] = product["name"]
    outputProduct["logical_identifier"] = "urn:nasa:pds:context_pds3:service:" + makeUrnSafe(getTitle(outputProduct))
    outputProduct["version_id"] = "1.0"
    outputProduct["information_model_version"] = "1.6.0.0"
    outputProduct["product_class"] = "Product_Service"
    outputProduct["modification_date"] = time.strftime("%Y-%m-%d")
    outputProduct["modification_description"] = "Migration from IPDA Tool Registry."

    # Update service related values for the product label.

    # Add a default URL if none was provided.
    if "url" not in product:
        outputProduct["url"] = "http://localhost"

    # Reformat the release date
    if "release_date" in outputProduct:
        outputProduct["release_date"] = datetime.datetime.strptime(outputProduct["release_date"][:10], "%Y/%m/%d").strftime("%Y-%m-%d")

    # Fill in a default service type.
    outputProduct["service_type"] = "Tool"

    # Convert interface type values.
    if "interface_type" in outputProduct:
        count = 0
        valueList = []
        for value in outputProduct["interface_type"]:
            if value == "Console" or value == "Script":
                if count == 0:
                    valueList.append("Command-Line")
                    count += 1
            else:
                valueList.append(value)
        outputProduct["interface_type"] = valueList    

    # Convert category values.
    if "category" in outputProduct:
        valueList = []
        for value in outputProduct["category"]:
            if value == "Data reader":
                valueList.append("Reader")
            elif value == "Search/retrieve":
                valueList.append("Search")
            else:
                valueList.append(value)
        outputProduct["category"] = valueList    

    # Generate an operating system note from the multiple values.
    if "supported_operating_system_note" in outputProduct:
        note = ""
        for entry in outputProduct["supported_operating_system_note"]:
            note += entry + ", "
        outputProduct["supported_operating_system_note"] = note.rstrip(", ")

    # Strip CRLF from the system requirements note.
    if "system_requirements_note" in outputProduct:
        outputProduct["system_requirements_note"] = outputProduct["system_requirements_note"].replace('\r', '').replace('\n', ' ')

    # Strip certain strings and CRLF from the description.
    outputProduct["description"] = removeStr("<p>", outputProduct["description"])
    outputProduct["description"] = removeStr("</p>", outputProduct["description"])
    outputProduct["description"] = removeStr("<span>", outputProduct["description"])
    outputProduct["description"] = removeStr("</span>", outputProduct["description"])
    outputProduct["description"] = outputProduct["description"].replace('\r', '').replace('\n', ' ')

    # Add default submitter information if none was provided.
    if "submitter_name" not in product:
        outputProduct["submitter_name"] = "Unknown"
    if "submitter_institution" not in product:
        outputProduct["submitter_institution"] = "Unknown"
    if "submitter_email" not in product:
        outputProduct["submitter_email"] = "Unknown"

    # Add the product to the list of products to output
    outputList.append(outputProduct)

# Write the new XML
for product in outputList:
    
    # Warn the user if existing data will be clobbered
    title = getTitle(product)
    if title == None:
        continue
    title = makeUrnSafe(title)
    path = os.path.join(outputLocation, title + "_" + product["version_id"] + ".xml")
    if os.path.exists(path):
        print "Existing file will be overwritten: " + path
    
    # Open the output file    
    output = open(path, "w")
    
    # Write the boilerplate
    output.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
    output.write("<?xml-model href=\"http://pds.jpl.nasa.gov/pds4/pds/v1/PDS4_PDS_1600.sch\" schematypens=\"http://purl.oclc.org/dsdl/schematron\"?>\n")
    output.write("<Product_Service xmlns=\"http://pds.nasa.gov/pds4/pds/v1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://pds.nasa.gov/pds4/pds/v1 http://pds.nasa.gov/pds4/pds/v1/PDS4_PDS_1600.xsd\">\n")
    
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
    output.write(getTabs(4) + getXmlElement(product, "modification_description", "description"))
    output.write(getTabs(3) + "</Modification_Detail>\n")
    output.write(getTabs(2) + "</Modification_History>\n")
    output.write(getTabs(1) + "</Identification_Area>\n")
    output.write(getTabs(1) + "<Service>\n")
    output.write(getTabs(2) + getXmlElement(product, "name", "name"))
    output.write(getTabs(2) + getXmlElement(product, "abstract_desc", "abstract_desc"))
    if "service_version_id" in product:
        output.write(getTabs(2) + getXmlElement(product, "service_version_id", "version_id"))
    output.write(getTabs(2) + getXmlElement(product, "url", "url"))
    if "release_date" in product:
        output.write(getTabs(2) + getXmlElement(product, "release_date", "release_date"))
    output.write(getTabs(2) + getXmlElement(product, "service_type", "service_type"))
    if "interface_type" in product:
        for interfaceType in product["interface_type"]:
            output.write(getTabs(2) + getXmlElements(interfaceType, "interface_type"))
    if "category" in product:
        for category in product["category"]:
            output.write(getTabs(2) + getXmlElements(category, "category"))
    if "supported_operating_system_note" in product:
        output.write(getTabs(2) + getXmlElement(product, "supported_operating_system_note", "supported_operating_system_note"))
    if "system_requirements_note" in product:
        output.write(getTabs(2) + getXmlElement(product, "system_requirements_note", "system_requirements_note").encode('ascii','ignore'))
    if "description" in product:
        output.write(getTabs(2) + getXmlElement(product, "description", "description").encode('ascii','ignore'))
    output.write(getTabs(1) + "</Service>\n")
    
    # Insert a comment containing the submitter information.
    output.write("  <!--\n")
    output.write("  Submitter Information\n")
    output.write("  Submitter Name:        " + product["submitter_name"] + "\n")
    output.write("  Submitter Institution: " + product["submitter_institution"] + "\n")
    output.write("  Submitter Email:       " + product["submitter_email"] + "\n")
    output.write("  -->\n")
    
    output.write("</Product_Service>\n")
    
    # Close the file
    output.close()

sys.exit(0)