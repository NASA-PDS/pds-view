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
from string import lowercase, lower

'''
This script reads an XML file specifying tools and outputs XML specifying those
tools in a format that can be read by the Registry Service.
'''

import copy
import os.path
import re
import sys
import time

from pds.registry.net import PDSRegistryClient




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
    safe = safe.replace("&amp;", "&")
    safe = re.sub("[^a-zA-Z0-9_.]", "-", safe)
    return safe

def printExtrinsic(e):
    print e._objectType + ":"
    d = vars(e)
    for key, value in d.iteritems():
        if key != "slots":
            print key + ": " + str(value)
    for slot in e.slots:
        print str(vars(slot))
    print ""

def loadExtrinsics():
    count = 1
    start = 0
    rows = 1000
    total = 0
    print "Now loading extrinsics.  This could take a while."
    while count > 0:
        extrinsicsPage = rc.getExtrinsics(start, rows)
        count = len(extrinsicsPage)
        start = start + rows
        total = total + count
        for e in extrinsicsPage:
            objType = e._objectType
            if objType not in extrinsics:
                extrinsics[objType] = []
            extrinsics[objType].append(e)
        print "Extrinsics loaded: " + str(total)
    print "All extrinsics loaded"

def findExtrinsicsWithLidPostfixAndType(postfix, extrinsicType):
    results = []
    for e in extrinsics[extrinsicType]:
        lid = e._lid
        if lid.endswith(postfix) and lid not in results:
            results.append(lid)
    return results

def findExtrinsicsWithLidPostfix(postfix, extrinsicType = None):
    if len(extrinsics) == 0:
        return []
    if extrinsicType is not None:
        return findExtrinsicsWithLidPostfixAndType(postfix, extrinsicType)
    else:
        results = []
        for eType in extrinsics:
            results.extend(findExtrinsicsWithLidPostfixAndType(postfix, eType))
        return results

def getExtrinsicsWithName(name, extrinsicType):
    if len(extrinsics) == 0:
        return None
    results = []
    safeName = makeUrnSafe(name)
    for e in extrinsics[extrinsicType]:
        lid = e._lid
        if safeName == makeUrnSafe(e.name) and lid not in results:
            results.append(lid)
    if len(results) == 0:
        print "Found no extrinsics of type " + extrinsicType + " with name " + name
    return results

def getExtrinsicsWithSlotValue(slotName, slotValue, extrinsicType):
    if len(extrinsics) == 0:
        return None
    results = []
    safeValue = makeUrnSafe(slotValue)
    for e in extrinsics[extrinsicType]:
        lid = e._lid
        for slot in e.slots:
            if slot.name == slotName and lid not in results:
                for val in slot.values:
                    if makeUrnSafe(val) == safeValue:
                        results.append(lid)
    if len(results) == 0:
        print "Found no extrinsics of type " + extrinsicType + " with slot " + slotName + "=" + slotValue
    return results

def addReferencesWithLidPostfix(outputProduct, outKey, lidPostfix, extrinsicType):
    lids = findExtrinsicsWithLidPostfix(lidPostfix, extrinsicType)
    for lid in lids:
        if lid not in outputProduct["reference_list"][outKey]:
            outputProduct["reference_list"][outKey].append(lid)

def addReferencesWithSlotValue(outputProduct, outKey, slot, value, extrinsicType):
    lids = getExtrinsicsWithSlotValue("data_set_name", value, extrinsicType)
    for lid in lids:
        if lid not in outputProduct["reference_list"][outKey]:
            outputProduct["reference_list"][outKey].append(lid)

def addReferenceWithLidFromClient(outputProduct, outKey, lidPrefix, lidPostfix, extrinsicType, showNotFound = True):
    lid = lidPrefix + makeUrnSafe(lidPostfix)
    refExtrinsic = rc.getExtrinsicByLID(lid)
    if refExtrinsic is not None and lid not in outputProduct["reference_list"][outKey]:
        outputProduct["reference_list"][outKey].append(lid)
        return True
    elif showNotFound:
        print "Could not find " + extrinsicType + " extrinsic " + lid + " for product " + outputProduct["logical_identifier"]
    return False

def translateNode(nodeID):
    for node in nodes:
        if nodeID in nodes[node]:
            return node
    return None

def writeReference(lid, refType):
    output.write(getTabs(2) + "<Internal_Reference>\n")
    output.write(getTabs(3) +"<lid_reference>" + lid + "</lid_reference>\n")
    output.write(getTabs(3) +"<reference_type>" + refType + "</reference_type>\n")
    output.write(getTabs(2) + "</Internal_Reference>\n")

def writeReferences(refKey, refType):
    if refKey in product["reference_list"]:
        for n in product["reference_list"][refKey]:
            writeReference(n, refType)
    
    






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

# Load the extrinsics using the Registry Client
extrinsics = dict()
rc = PDSRegistryClient("https://pds.nasa.gov/services/registry-pds3")
loadExtrinsics()

# Configure the mapping of expected IDs to nodes
ATM_NODE_NAMES = ["planetary_atmospheres"]
GEO_NODE_NAMES = ["geosciences", "geoscience"]
IMG_NODE_NAMES = ["imaging"]
PPI_NODE_NAMES = ["planetary_plasma_interactions"]
RINGS_NODE_NAMES = ["rings", "planetary rings"]
RS_NODE_NAMES = ["radio_science"]
SBN_NODE_NAMES = ["small bodies"]
nodes = dict()
nodes["atm"] = ATM_NODE_NAMES
nodes["geoscience"] = GEO_NODE_NAMES
nodes["imaging"] = IMG_NODE_NAMES
nodes["ppi-ucla"] = PPI_NODE_NAMES
nodes["rings"] = RINGS_NODE_NAMES
nodes["rs"] = RS_NODE_NAMES
nodes["sbn"] = SBN_NODE_NAMES

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
DEBUG_REPORT = False
outputList = list()
for product in productList:
    outputProduct = copy.deepcopy(product)
    
    # Populate details that simply always use defaults
    outputProduct["version_id"] = "1.0"
    outputProduct["information_model_version"] = "1.6.0.0"
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
    
    # Set output details that map to other details but use defaults
    outputProduct["data_product_type"] = getFieldWithDefault(product, "data_product_type", "Product_Context_Search_Tool")
    outputProduct["format"] = getFieldWithDefault(product, "format", "XML/RDF")
    outputProduct["language"] = getFieldWithDefault(product, "language", "en")
    outputProduct["publisher"] = getFieldWithDefault(product, "publisher", "NASA.PDS")
    outputProduct["data_product_type"] = getFieldWithDefault(product, "resource_class", "data_set_description")
    
    # Prepare the reference list
    outputProduct["reference_list"] = dict()
    
    # Add the node reference
    if "node_id" in product:
        outputProduct["reference_list"]["node"] = []
        product_nodeID = product["node_id"]
        if type(product_nodeID) is list:
            for id in product_nodeID:
                node = translateNode(id.lower())
                if node != None:
                    outputProduct["reference_list"]["node"].append("urn:nasa:pds:context_pds3:node:node." + node)
                else:
                    print "Unknown node label " + id + " found in product " + outputProduct["logical_identifier"]
        else:
            node = translateNode(product_nodeID.lower())
            if node != None:
                outputProduct["reference_list"]["node"].append("urn:nasa:pds:context_pds3:node:node." + node)
            else:
                print "Unknown node label " + product_nodeID + " found in product " + outputProduct["logical_identifier"]
    else:
        print "No node found for product " + outputProduct["logical_identifier"]
            
    # Add the investigation reference
    if "investigation_name" in product:
        outputProduct["reference_list"]["investigation"] = []
        productInvestigation = product["investigation_name"]
        if type(productInvestigation) is list:
            for investigaton in productInvestigation:
                addReferenceWithLidFromClient(outputProduct, "investigation", "urn:nasa:pds:context_pds3:investigation:mission.", investigaton, "investigation")
        else:
            addReferenceWithLidFromClient(outputProduct, "investigation", "urn:nasa:pds:context_pds3:investigation:mission.", productInvestigation, "investigation")
    else:
        print "No investigation found for product " + outputProduct["logical_identifier"]
    
    # Add the instrument host reference
    if "instrument_host_id" in product:
        outputProduct["reference_list"]["instrument_host"] = []
        productInstrumentHost = product["instrument_host_id"]
        if type(productInstrumentHost) is list:
            for instrumentHost in productInstrumentHost:
                addReferencesWithLidPostfix(outputProduct, "instrument_host", makeUrnSafe(instrumentHost), "Product_Instrument_Host_PDS3")
        else:
            addReferencesWithLidPostfix(outputProduct, "instrument_host", makeUrnSafe(productInstrumentHost), "Product_Instrument_Host_PDS3")
    else:
        print "No instrument host found for product " + outputProduct["logical_identifier"]

    # Add the instrument reference
    if "instrument_host_id" in product and "instrument_id" in product:
        foundLids, missingLids = 0, 0
        outputProduct["reference_list"]["instrument"] = []
        productInstrumentHost = product["instrument_host_id"]
        productInstrument = product["instrument_id"]
        if type(productInstrument) is list:
            for instrument in productInstrument:
                if type(productInstrumentHost) is list:
                    for intrumentHost in productInstrumentHost:
                        if addReferenceWithLidFromClient(outputProduct, "instrument", "urn:nasa:pds:context_pds3:instrument:instrument.", instrument + "__" + intrumentHost, "instrument", showNotFound=False):
                            foundLids = foundLids + 1
                        else:
                            missingLids = missingLids + 1
                else:
                    if addReferenceWithLidFromClient(outputProduct, "instrument", "urn:nasa:pds:context_pds3:instrument:instrument.", instrument + "__" + productInstrumentHost, "instrument", showNotFound=False):
                        foundLids = foundLids + 1
                    else:
                        missingLids = missingLids + 1
        else:
            if type(productInstrumentHost) is list:
                for intrumentHost in productInstrumentHost:
                    if addReferenceWithLidFromClient(outputProduct, "instrument", "urn:nasa:pds:context_pds3:instrument:instrument.", productInstrument + "__" + intrumentHost, "instrument", showNotFound=False):
                        foundLids = foundLids + 1
                    else:
                        missingLids = missingLids + 1
            else:
                if addReferenceWithLidFromClient(outputProduct, "instrument", "urn:nasa:pds:context_pds3:instrument:instrument.", productInstrument + "__" + productInstrumentHost, "instrument", showNotFound=False):
                    foundLids = foundLids + 1
                else:
                    missingLids = missingLids + 1
        if DEBUG_REPORT or foundLids == 0:
            print "Found " + str(foundLids) + " LIDs and searched for " + str(missingLids) + " missing LIDs for instruments of product " + outputProduct["logical_identifier"]
    else:
        print "No instrument found for product " + outputProduct["logical_identifier"]
        
    # Add the target reference
    if "target_type" in product and "target_name" in product:
        foundLids, missingLids = 0, 0
        outputProduct["reference_list"]["target"] = []
        productTargetType = product["target_type"]
        productTargetName = product["target_name"]
        if type(productTargetType) is list:
            for targetType in productTargetType:
                if type(productTargetName) is list:
                    for targetName in productTargetName:
                        if addReferenceWithLidFromClient(outputProduct, "target", "urn:nasa:pds:context_pds3:target:", targetType + "." + targetName, "target", showNotFound=False):
                            foundLids = foundLids + 1
                        else:
                            missingLids = missingLids + 1
                else:
                    if addReferenceWithLidFromClient(outputProduct, "target", "urn:nasa:pds:context_pds3:target:", targetType + "." + productTargetName, "target", showNotFound=False):
                        foundLids = foundLids + 1
                    else:
                        missingLids = missingLids + 1
        else:
            if type(productTargetName) is list:
                for targetName in productTargetName:
                    if addReferenceWithLidFromClient(outputProduct, "target", "urn:nasa:pds:context_pds3:target:", productTargetType + "." + targetName, "target", showNotFound=False):
                        foundLids = foundLids + 1
                    else:
                        missingLids = missingLids + 1
            else:
                if addReferenceWithLidFromClient(outputProduct, "target", "urn:nasa:pds:context_pds3:target:", productTargetType + "." + productTargetName, "target", showNotFound=False):
                    foundLids = foundLids + 1
                else:
                    missingLids = missingLids + 1
        if DEBUG_REPORT or foundLids == 0:
            print "Found " + str(foundLids) + " LIDs and searched for " + str(missingLids) + " missing LIDs for targets of product " + outputProduct["logical_identifier"]
    else:
        print "No target found for product " + outputProduct["logical_identifier"]
    
    # Add the data set reference
    if "data_set_name" in product:
        outputProduct["reference_list"]["dataset"] = []
        productDataSet = product["data_set_name"]
        if type(productDataSet) is list:
            for dataSet in productDataSet:
                addReferencesWithSlotValue(outputProduct, "dataset", "data_set_name", dataSet, "Product_Data_Set_PDS3")
        else:
            addReferencesWithSlotValue(outputProduct, "dataset", "data_set_name", productDataSet, "Product_Data_Set_PDS3")
    else:
        print "No data set found for product " + outputProduct["logical_identifier"]
    
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
    print "Writing product at " + path
    if os.path.exists(path):
        print "Existing file will be overwritten: " + path
    
    # Open the output file
    
    output = open(path, "w")
    
    # Write the boilerplate
    output.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
    output.write("<?xml-model href=\"http://pds.jpl.nasa.gov/pds4/pds/v1/PDS4_PDS_1600.sch\" schematypens=\"http://purl.oclc.org/dsdl/schematron\"?>\n")
    output.write("<Product_Service xmlns=\"http://pds.nasa.gov/pds4/pds/v1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://pds.nasa.gov/pds4/pds/v1 https://pds.nasa.gov/pds4/pds/v1/PDS4_PDS_1600.xsd\">\n")
    
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
    output.write(getTabs(1) + "<Reference_List>\n")
    writeReferences("node", "has_node")
    writeReferences("investigation", "resource_to_investigation")
    writeReferences("instrument_host", "resource_to_instrument_host")
    writeReferences("instrument", "resource_to_instrument")
    writeReferences("target", "resource_to_target")
    writeReferences("dataset", "has_data_set")
    output.write(getTabs(1) + "</Reference_List>\n")
    output.write("</Product_Service>\n")
    
    # Close the file
    output.close()
