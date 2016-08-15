<?xml version="1.0" encoding="UTF-8"?>
<!-- PDS4 Schematron file for the Mars Global Surveyor Dictionary -->

<sch:schema xmlns:sch="http://purl.oclc.org/dsdl/schematron" queryBinding="xslt2">

    <sch:title>Schematron using XPath 2.0</sch:title>
    
    <sch:ns uri="http://pds.nasa.gov/pds4/mission/mgs/v0" prefix="mgs"/>
    <sch:ns uri="http://pds.nasa.gov/pds4/pds/v1" prefix="pds"/>
    
    <sch:pattern id="Attribute_Value_Matching">
        
        <sch:rule context="pds:Mission_Area">
            <sch:assert test="mgs:instrument_id = ('MOC-NA', 'MOC-WA')">
                The attribute mgs:instrument_id must be equal to one of the following values: 'MOC-NA', 'MOC-WA'.
            </sch:assert>
            <sch:assert test="mgs:mission_phase_name = ('AEROBRAKING-1', 'EXTENDED', 'MAPPING', 'RELAY', 'SPO-1', 'SPO-2', 'SUPPORT')">
                MGS Dictionary: The attribute mgs:mission_phase_name must have one of the following values: 'AEROBRAKING-1', 'EXTENDED', 'MAPPING', 'RELAY', 'SPO-1', 'SPO-2', 'SUPPORT'.
            </sch:assert>
        </sch:rule>
    </sch:pattern>

</sch:schema>
