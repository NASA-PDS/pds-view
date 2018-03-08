/*
**  Copyright 2016-2017, by the California Institute of Technology.
**  ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
**  Any commercial use must be negotiated with the Office of Technology
**  Transfer at the California Institute of Technology.
**
**  This software is subject to U. S. export control laws and regulations
**  (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
**  is subject to U.S. export control laws and regulations, the recipient has
**  the responsibility to obtain export licenses or other export authority as
**  may be required before exporting such information to foreign countries or
**  providing access to foreign nationals.
**
** $Id$
*/

/*
** This script populates the Tracking Service schema.
*/

/*
** product/*_reference
*/

/* Juno Gravity Data Sets */
/*
insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-e-rss-1-eagr-v1.0', '1.0', 'JUNO-E-RSS-1-EAGR-V1.0', 'PDS3-Data-Set', 'JUNO-E-RSS-1-EAGR-V1.0');

insert into investigation_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-e-rss-1-eagr-v1.0', 'urn:nasa:pds:context_pds3:investigation:mission.juno', 'Juno');

insert into instrument_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-e-rss-1-eagr-v1.0', 'urn:nasa:pds:context_pds3:instrument:gravity.jno', 'Gravity');

insert into node_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-e-rss-1-eagr-v1.0', 'urn:nasa:pds:context_pds3:node:node.atmos', 'Atmospheres');
*/

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-rss-1-ocru-v1.0', '1.0', 'JUNO-J-RSS-1-OCRU-V1.0', 'PDS3-Data-Set', 'JUNO-J-RSS-1-OCRU-V1.0');

insert into investigation_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-rss-1-ocru-v1.0', 'urn:nasa:pds:context_pds3:investigation:mission.juno', 'Juno');

insert into instrument_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-rss-1-ocru-v1.0', 'urn:nasa:pds:context_pds3:instrument:gravity.jno', 'Gravity');

insert into node_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-rss-1-ocru-v1.0', 'urn:nasa:pds:context_pds3:node:node.atmos', 'Atmospheres');


insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-rss-1-jugr-v1.0', '1.0', 'JUNO-J-RSS-1-JUGR-V1.0', 'PDS3-Data-Set', 'JUNO-J-RSS-1-JUGR-V1.0');

insert into investigation_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-rss-1-jugr-v1.0', 'urn:nasa:pds:context_pds3:investigation:mission.juno', 'Juno');

insert into instrument_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-rss-1-jugr-v1.0', 'urn:nasa:pds:context_pds3:instrument:gravity.jno', 'Gravity');

insert into node_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-rss-1-jugr-v1.0', 'urn:nasa:pds:context_pds3:node:node.atmos', 'Atmospheres');

/* Juno JIRAM Data Sets */
/*
insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-l-jiram-2-edr-v1.0', '1.0', 'JUNO-L-JIRAM-2-EDR-V1.0', 'PDS3-Data-Set', 'JUNO-L-JIRAM-2-EDR-V1.0');

insert into investigation_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-l-jiram-2-edr-v1.0', 'urn:nasa:pds:context_pds3:investigation:mission.juno', 'Juno');

insert into instrument_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-l-jiram-2-edr-v1.0', 'urn:nasa:pds:context_pds3:instrument:jiram.jno', 'JIRAM');

insert into node_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-l-jiram-2-edr-v1.0', 'urn:nasa:pds:context_pds3:node:node.atmos', 'Atmospheres');
*/
/*
insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-l-jiram-3-rdr-v1.0', '1.0', 'JUNO-L-JIRAM-3-RDR-V1.0', 'PDS3-Data-Set', 'JUNO-L-JIRAM-3-RDR-V1.0');

insert into investigation_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-l-jiram-3-rdr-v1.0', 'urn:nasa:pds:context_pds3:investigation:mission.juno', 'Juno');

insert into instrument_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-l-jiram-3-rdr-v1.0', 'urn:nasa:pds:context_pds3:instrument:jiram.jno', 'JIRAM');

insert into node_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-l-jiram-3-rdr-v1.0', 'urn:nasa:pds:context_pds3:node:node.atmos', 'Atmospheres');
*/

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-jiram-2-edr-v1.0', '1.0', 'JUNO-J-JIRAM-2-EDR-V1.0', 'PDS3-Data-Set', 'JUNO-J-JIRAM-2-EDR-V1.0');

insert into investigation_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-jiram-2-edr-v1.0', 'urn:nasa:pds:context_pds3:investigation:mission.juno', 'Juno');

insert into instrument_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-jiram-2-edr-v1.0', 'urn:nasa:pds:context_pds3:instrument:jiram.jno', 'JIRAM');

insert into node_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-jiram-2-edr-v1.0', 'urn:nasa:pds:context_pds3:node:node.atmos', 'Atmospheres');


insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-jiram-3-rdr-v1.0', '1.0', 'JUNO-J-JIRAM-3-RDR-V1.0', 'PDS3-Data-Set', 'JUNO-J-JIRAM-3-RDR-V1.0');

insert into investigation_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-jiram-3-rdr-v1.0', 'urn:nasa:pds:context_pds3:investigation:mission.juno', 'Juno');

insert into instrument_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-jiram-3-rdr-v1.0', 'urn:nasa:pds:context_pds3:instrument:jiram.jno', 'JIRAM');

insert into node_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-jiram-3-rdr-v1.0', 'urn:nasa:pds:context_pds3:node:node.atmos', 'Atmospheres');

/* Juno MWR Data Sets */

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-2-edr-cruise-v1.0', '1.0', 'JNO-J-MWR-2-EDR-cruise-V1.0', 'PDS3-Data-Set', 'JNO-J-MWR-2-EDR-cruise-V1.0');

insert into investigation_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-2-edr-cruise-v1.0', 'urn:nasa:pds:context_pds3:investigation:mission.juno', 'Juno');

insert into instrument_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-2-edr-cruise-v1.0', 'urn:nasa:pds:context_pds3:instrument:mwr.jno', 'MWR');

insert into node_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-2-edr-cruise-v1.0', 'urn:nasa:pds:context_pds3:node:node.atmos', 'Atmospheres');


insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-3-irdr-cruise-v1.0', '1.0', 'JNO-J-MWR-3-IRDR-cruise-V1.0', 'PDS3-Data-Set', 'JNO-J-MWR-3-IRDR-cruise-V1.0');

insert into investigation_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-3-irdr-cruise-v1.0', 'urn:nasa:pds:context_pds3:investigation:mission.juno', 'Juno');

insert into instrument_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-3-irdr-cruise-v1.0', 'urn:nasa:pds:context_pds3:instrument:mwr.jno', 'MWR');

insert into node_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-3-irdr-cruise-v1.0', 'urn:nasa:pds:context_pds3:node:node.atmos', 'Atmospheres');


insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-3-grdr-cruise-v1.0', '1.0', 'JNO-J-MWR-3-GRDR-cruise-V1.0', 'PDS3-Data-Set', 'JNO-J-MWR-3-GRDR-cruise-V1.0');

insert into investigation_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-3-grdr-cruise-v1.0', 'urn:nasa:pds:context_pds3:investigation:mission.juno', 'Juno');

insert into instrument_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-3-grdr-cruise-v1.0', 'urn:nasa:pds:context_pds3:instrument:mwr.jno', 'MWR');

insert into node_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-3-grdr-cruise-v1.0', 'urn:nasa:pds:context_pds3:node:node.atmos', 'Atmospheres');


insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-2-edr-orbit-v1.0', '1.0', 'JNO-J-MWR-2-EDR-orbit-V1.0', 'PDS3-Data-Set', 'JNO-J-MWR-2-EDR-orbit-V1.0');

insert into investigation_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-2-edr-orbit-v1.0', 'urn:nasa:pds:context_pds3:investigation:mission.juno', 'Juno');

insert into instrument_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-2-edr-orbit-v1.0', 'urn:nasa:pds:context_pds3:instrument:mwr.jno', 'MWR');

insert into node_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-2-edr-orbit-v1.0', 'urn:nasa:pds:context_pds3:node:node.atmos', 'Atmospheres');


insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-3-irdr-orbit-v1.0', '1.0', 'JNO-J-MWR-3-IRDR-orbit-V1.0', 'PDS3-Data-Set', 'JNO-J-MWR-3-IRDR-orbit-V1.0');

insert into investigation_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-3-irdr-orbit-v1.0', 'urn:nasa:pds:context_pds3:investigation:mission.juno', 'Juno');

insert into instrument_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-3-irdr-orbit-v1.0', 'urn:nasa:pds:context_pds3:instrument:mwr.jno', 'MWR');

insert into node_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-3-irdr-orbit-v1.0', 'urn:nasa:pds:context_pds3:node:node.atmos', 'Atmospheres');


insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-3-grdr-orbit-v1.0', '1.0', 'JNO-J-MWR-3-GRDR-orbit-V1.0', 'PDS3-Data-Set', 'JNO-J-MWR-3-GRDR-orbit-V1.0');

insert into investigation_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-3-grdr-orbit-v1.0', 'urn:nasa:pds:context_pds3:investigation:mission.juno', 'Juno');

insert into instrument_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-3-grdr-orbit-v1.0', 'urn:nasa:pds:context_pds3:instrument:mwr.jno', 'MWR');

insert into node_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-3-grdr-orbit-v1.0', 'urn:nasa:pds:context_pds3:node:node.atmos', 'Atmospheres');

/* Juno UVS Data Sets */

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-uvs-2-edr-v1.0', '1.0', 'JNO-J-UVS-2-EDR-V1.0', 'PDS3-Data-Set', 'JNO-J-UVS-2-EDR-V1.0');

insert into investigation_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-uvs-2-edr-v1.0', 'urn:nasa:pds:context_pds3:investigation:mission.juno', 'Juno');

insert into instrument_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-uvs-2-edr-v1.0', 'urn:nasa:pds:context_pds3:instrument:uvs.jno', 'UVS');

insert into node_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-uvs-2-edr-v1.0', 'urn:nasa:pds:context_pds3:node:node.atmos', 'Atmospheres');


insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-uvs-2-rdr-v1.0', '1.0', 'JNO-J-UVS-2-RDR-V1.0', 'PDS3-Data-Set', 'JNO-J-UVS-2-RDR-V1.0');

insert into investigation_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-uvs-2-rdr-v1.0', 'urn:nasa:pds:context_pds3:investigation:mission.juno', 'Juno');

insert into instrument_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-uvs-2-rdr-v1.0', 'urn:nasa:pds:context_pds3:instrument:uvs.jno', 'UVS');

insert into node_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-uvs-2-rdr-v1.0', 'urn:nasa:pds:context_pds3:node:node.atmos', 'Atmospheres');

/* Juno JADE Data Sets */

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-sw-jad-2-uncalibrated-v1.0', '1.0', 'JNO-SW-JAD-2-UNCALIBRATED-V1.0', 'PDS3-Data-Set', 'JNO-SW-JAD-2-UNCALIBRATED-V1.0');

insert into investigation_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-sw-jad-2-uncalibrated-v1.0', 'urn:nasa:pds:context_pds3:investigation:mission.juno', 'Juno');

insert into instrument_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-sw-jad-2-uncalibrated-v1.0', 'urn:nasa:pds:context_pds3:instrument:jade.jno', 'JADE');

insert into node_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-sw-jad-2-uncalibrated-v1.0', 'urn:nasa:pds:context_pds3:node:node.ppi-ucla', 'PPI');


insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-sw-jad-2-uncalibrated-v1.0', '1.0', 'JNO-J/SW-JAD-2-UNCALIBRATED-V1.0', 'PDS3-Data-Set', 'JNO-J/SW-JAD-2-UNCALIBRATED-V1.0');

insert into investigation_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-sw-jad-2-uncalibrated-v1.0', 'urn:nasa:pds:context_pds3:investigation:mission.juno', 'Juno');

insert into instrument_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-sw-jad-2-uncalibrated-v1.0', 'urn:nasa:pds:context_pds3:instrument:jade.jno', 'JADE');

insert into node_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-sw-jad-2-uncalibrated-v1.0', 'urn:nasa:pds:context_pds3:node:node.ppi-ucla', 'PPI');


insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-sw-jad-3-calibrated-v1.0', '1.0', 'JNO-J/SW-JAD-3-CALIBRATED-V1.0', 'PDS3-Data-Set', 'JNO-J/SW-JAD-3-CALIBRATED-V1.0');

insert into investigation_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-sw-jad-3-calibrated-v1.0', 'urn:nasa:pds:context_pds3:investigation:mission.juno', 'Juno');

insert into instrument_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-sw-jad-3-calibrated-v1.0', 'urn:nasa:pds:context_pds3:instrument:jade.jno', 'JADE');

insert into node_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-sw-jad-3-calibrated-v1.0', 'urn:nasa:pds:context_pds3:node:node.ppi-ucla', 'PPI');


insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-sw-jad-5-pad-v1.0', '1.0', 'JNO-J/SW-JAD-5-PAD-V1.0', 'PDS3-Data-Set', 'JNO-J/SW-JAD-5-PAD-V1.0');

insert into investigation_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-sw-jad-5-pad-v1.0', 'urn:nasa:pds:context_pds3:investigation:mission.juno', 'Juno');

insert into instrument_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-sw-jad-5-pad-v1.0', 'urn:nasa:pds:context_pds3:instrument:jade.jno', 'JADE');

insert into node_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-sw-jad-5-pad-v1.0', 'urn:nasa:pds:context_pds3:node:node.ppi-ucla', 'PPI');


insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-sw-jad-5-plasma-v1.0', '1.0', 'JNO-J/SW-JAD-5-PLASMA-V1.0', 'PDS3-Data-Set', 'JNO-J/SW-JAD-5-PLASMA-V1.0');

insert into investigation_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-sw-jad-5-plasma-v1.0', 'urn:nasa:pds:context_pds3:investigation:mission.juno', 'Juno');

insert into instrument_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-sw-jad-5-plasma-v1.0', 'urn:nasa:pds:context_pds3:instrument:jade.jno', 'JADE');

insert into node_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-sw-jad-5-plasma-v1.0', 'urn:nasa:pds:context_pds3:node:node.ppi-ucla', 'PPI');


insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-sw-jad-5-mom-v1.0', '1.0', 'JNO-J/SW-JAD-5-MOM-V1.0', 'PDS3-Data-Set', 'JNO-J/SW-JAD-5-MOM-V1.0');

insert into investigation_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-sw-jad-5-mom-v1.0', 'urn:nasa:pds:context_pds3:investigation:mission.juno', 'Juno');

insert into instrument_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-sw-jad-5-mom-v1.0', 'urn:nasa:pds:context_pds3:instrument:jade.jno', 'JADE');

insert into node_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-sw-jad-5-mom-v1.0', 'urn:nasa:pds:context_pds3:node:node.ppi-ucla', 'PPI');

/* Juno JEDI Data Sets */

/* Juno FGM Data Sets */

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-sw-3-fgm-cal-v1.0', '1.0', 'JNO-SW-3-FGM-CAL-V1.0', 'PDS3-Data-Set', 'JNO-SW-3-FGM-CAL-V1.0');

insert into investigation_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-sw-3-fgm-cal-v1.0', 'urn:nasa:pds:context_pds3:investigation:mission.juno', 'Juno');

insert into instrument_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-sw-3-fgm-cal-v1.0', 'urn:nasa:pds:context_pds3:instrument:fgm.jno', 'FGM');

insert into node_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-sw-3-fgm-cal-v1.0', 'urn:nasa:pds:context_pds3:node:node.ppi-ucla', 'PPI');


insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-3-fgm-cal-v1.0', '1.0', 'JNO-J-3-FGM-CAL-V1.0', 'PDS3-Data-Set', 'JNO-J-3-FGM-CAL-V1.0');

insert into investigation_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-3-fgm-cal-v1.0', 'urn:nasa:pds:context_pds3:investigation:mission.juno', 'Juno');

insert into instrument_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-3-fgm-cal-v1.0', 'urn:nasa:pds:context_pds3:instrument:fgm.jno', 'FGM');

insert into node_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-3-fgm-cal-v1.0', 'urn:nasa:pds:context_pds3:node:node.ppi-ucla', 'PPI');


insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-2-fgm-n-v1.0', '1.0', 'JNO-J-2-FGM-N-V1.0', 'PDS3-Data-Set', 'JNO-J-2-FGM-N-V1.0');

insert into investigation_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-2-fgm-n-v1.0', 'urn:nasa:pds:context_pds3:investigation:mission.juno', 'Juno');

insert into instrument_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-2-fgm-n-v1.0', 'urn:nasa:pds:context_pds3:instrument:fgm.jno', 'FGM');

insert into node_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-2-fgm-n-v1.0', 'urn:nasa:pds:context_pds3:node:node.ppi-ucla', 'PPI');

/* Juno Waves Data Sets */

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-2-edr-v1.0', '1.0', 'JNO-E/J/SS-WAV-2-EDR-V1.0', 'PDS3-Data-Set', 'JNO-E/J/SS-WAV-2-EDR-V1.0');

insert into investigation_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-2-edr-v1.0', 'urn:nasa:pds:context_pds3:investigation:mission.juno', 'Juno');

insert into instrument_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-2-edr-v1.0', 'urn:nasa:pds:context_pds3:instrument:waves.jno', 'Waves');

insert into node_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-2-edr-v1.0', 'urn:nasa:pds:context_pds3:node:node.ppi-ucla', 'PPI');


insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-3-cdr-srvfull-v1.0', '1.0', 'JNO-E/J/SS-WAV-3-CDR-SRVFULL-V1.0', 'PDS3-Data-Set', 'JNO-E/J/SS-WAV-3-CDR-SRVFULL-V1.0');

insert into investigation_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-3-cdr-srvfull-v1.0', 'urn:nasa:pds:context_pds3:investigation:mission.juno', 'Juno');

insert into instrument_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-3-cdr-srvfull-v1.0', 'urn:nasa:pds:context_pds3:instrument:waves.jno', 'Waves');

insert into node_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-3-cdr-srvfull-v1.0', 'urn:nasa:pds:context_pds3:node:node.ppi-ucla', 'PPI');


insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-3-cdr-bstfull-v1.0', '1.0', 'JNO-E/J/SS-WAV-3-CDR-BSTFULL-V1.0', 'PDS3-Data-Set', 'JNO-E/J/SS-WAV-3-CDR-BSTFULL-V1.0');

insert into investigation_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-3-cdr-bstfull-v1.0', 'urn:nasa:pds:context_pds3:investigation:mission.juno', 'Juno');

insert into instrument_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-3-cdr-bstfull-v1.0', 'urn:nasa:pds:context_pds3:instrument:waves.jno', 'Waves');

insert into node_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-3-cdr-bstfull-v1.0', 'urn:nasa:pds:context_pds3:node:node.ppi-ucla', 'PPI');

/* Juno JunoCam Data Sets */

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-junocam-2-edr-l0-v1.0', '1.0', 'JUNO-J-JUNOCAM-2-EDR-L0-V1.0', 'PDS3-Data-Set', 'JUNO-J-JUNOCAM-2-EDR-L0-V1.0');

insert into investigation_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-junocam-2-edr-l0-v1.0', 'urn:nasa:pds:context_pds3:investigation:mission.juno', 'Juno');

insert into instrument_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-junocam-2-edr-l0-v1.0', 'urn:nasa:pds:context_pds3:instrument:jnc.jno', 'JunoCam');

insert into node_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-junocam-2-edr-l0-v1.0', 'urn:nasa:pds:context_pds3:node:node.imaging', 'Imaging');


insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-junocam-3-rdr-l1a-v1.0', '1.0', 'JUNO-J-JUNOCAM-3-RDR-L1A-V1.0', 'PDS3-Data-Set', 'JUNO-J-JUNOCAM-3-RDR-L1A-V1.0');

insert into investigation_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-junocam-3-rdr-l1a-v1.0', 'urn:nasa:pds:context_pds3:investigation:mission.juno', 'Juno');

insert into instrument_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-junocam-3-rdr-l1a-v1.0', 'urn:nasa:pds:context_pds3:instrument:jnc.jno', 'JunoCam');

insert into node_reference (logical_identifier, reference, title) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-junocam-3-rdr-l1a-v1.0', 'urn:nasa:pds:context_pds3:node:node.imaging', 'Imaging');

/* Juno Radiation Data Sets */

/* LADEE NMS Bundle/Collections */

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:ladee_nms', '1.0', 'LADEE Neutral Mass Spectrometer Data', 'PDS4-Bundle', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:ladee_nms:data_calibrated', '1.0', 'NMS Calibrated Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:ladee_nms:data_derived', '1.0', 'NMS Derived Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:ladee_nms:data_raw', '1.0', 'NMS Raw Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:ladee_nms:document', '1.0', 'LADEE NMS Document Collection', 'PDS4-Collection', NULL);

/* MAVEN Ancillary Bundle/Collections */

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.anc', '1.10', 'MAVEN Ancillary Bundle', 'PDS4-Bundle', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.anc:data.drf.eps', '1.10', 'Electrical Power System DRF Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.anc:data.drf.gnc', '1.10', 'Guidance Navigation and Control DRF Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.anc:data.drf.ngms', '1.10', 'NGIMS instrument data DRF Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.anc:data.drf.pf', '1.10', 'Particle and Fields instrument data DRF Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.anc:data.drf.pte', '1.10', 'Periapsis Timing Estimator DRF Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.anc:data.drf.rs', '1.10', 'Remote Sensing (IUVS) instrument data DRF Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.anc:data.drf.sasm1', '1.10', 'Solar Array Switch Module 1 DRF Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.anc:data.drf.sasm2', '1.10', 'Solar Array Switch Module 2 DRF Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.anc:data.drf.sasm3', '1.10', 'Solar Array Switch Module 3 DRF Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.anc:data.drf.usm1', '1.10', 'Universal Switching Module 1 DRF Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.anc:data.drf.usm2', '1.10', 'Universal Switching Module 2 DRF Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.anc:data.drf.usm3', '1.10', 'Universal Switching Module 3 DRF Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.anc:data.drf.usm4', '1.10', 'Universal Switching Module 4 DRF Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.anc:data.drf.usm5', '1.10', 'Universal Switching Module 5 DRF Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.anc:data.drf.usm6', '1.10', 'Universal Switching Module 6 DRF Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.anc:data.events', '3.2', 'MAVEN Events List Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.anc:data.imu', '1.10', 'Intertial Measurement Unit Data Collection', 'PDS4-Collection', NULL);

/* MAVEN EUV Bundle/Collections */

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.euv', '1.3', 'MAVEN EUV Bundle', 'PDS4-Bundle', NULL);

/* MAVEN EUV Calibrated Data Bundle/Collections */

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.euv.calibrated', '8.0', 'MAVEN EUV Calibrated Data Bundle', 'PDS4-Bundle', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.euv.calibrated:data.bands', '8.0', 'MAVEN EUV calibrated irradiances Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.euv.modelled:data.daily.spectra', '8.0', 'MAVEN EUV calibrated irradiances Data Collection', 'PDS4-Collection', NULL);

/* MAVEN EUV Modelled Data Bundle/Collections */

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.euv.modelled', '6.0', 'MAVEN EUV Modelled Data Bundle', 'PDS4-Bundle', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.euv.modelled:data.daily.spectra', '6.0', 'MAVEN EUV daily averaged modelled irradiance spectra Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.euv.modelled:data.minute.spectra', '6.0', 'MAVEN EUV minute averaged modelled irradiance spectra Data Collection', 'PDS4-Collection', NULL);

/* MAVEN Insitu Key Parameters Data Bundle/Collections */

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.insitu.calibrated', '11.0', 'MAVEN Insitu Key Parameters Data Bundle', 'PDS4-Bundle', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.insitu.calibrated:data.kp', '11.0', 'MAVEN Insitu Key Parameters Data Collection', 'PDS4-Collection', NULL);

/* MAVEN LPW Bundle/Collections */

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.lpw', '1.1', 'MAVEN LPW Bundle', 'PDS4-Bundle', NULL);

/* MAVEN LPW Calibrated Data Bundle/Collections */

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.lpw.calibrated', '4.5', 'MAVEN LPW Calibrated Data Bundle', 'PDS4-Bundle', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.lpw.calibrated:data.lp.iv', '4.5', 'MAVEN LPW Calibrated Current-Voltage Relationships Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.lpw.calibrated:data.mrg.scpot', '4.5', 'MAVEN LPW Spacecraft Potential Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.lpw.calibrated:data.w.e12', '4.5', 'MAVEN LPW Low Frequency Electric-Field Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.lpw.calibrated:data.w.e12bursthf', '4.5', 'MAVEN LPW High Frequency Burst Mode Calibrated Electric-Field Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.lpw.calibrated:data.w.e12burstlf', '4.5', 'MAVEN LPW Low Frequency Burst Mode Calibrated Electric-Field Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.lpw.calibrated:data.w.e12burstmf', '4.5', 'MAVEN LPW Medium Frequency Burst Mode Calibrated Electric-Field Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.lpw.calibrated:data.w.specact', '4.5', 'MAVEN LPW Calibrated Active Mode Spectra Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.lpw.calibrated:data.w.specpas', '4.5', 'MAVEN LPW Calibrated Passive Mode Spectra Data Collection', 'PDS4-Collection', NULL);

/* MAVEN LPW Derived Data Bundle/Collections */

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.lpw.derived', '4.5', 'MAVEN LPW Derived Data Bundle', 'PDS4-Bundle', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.lpw.derived:data.lp.nt', '3.5', 'MAVEN LPW Derived Electron Temperature Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.lpw.derived:data.w.n', '4.5', 'MAVEN LPW Derived Electron Density Data Collection', 'PDS4-Collection', NULL);

/* MAVEN LPW Raw Data Bundle/Collections */

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.lpw.raw', '2.5', 'MAVEN LPW Raw Data Bundle', 'PDS4-Bundle', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.lpw.raw:data.act', '2.5', 'MAVEN LPW Raw Wave Active Sub-Cycle Potentials Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.lpw.raw:data.adr', '2.5', 'MAVEN LPW Active DAC Readback Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.lpw.raw:data.atr', '2.5', 'MAVEN LPW Active Table Readback Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.lpw.raw:data.euv', '2.5', 'MAVEN Raw EUV Packet Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.lpw.raw:data.hsbmhf', '2.5', 'MAVEN LPW Raw High Frequency High Speed Burst Mode (HSBM) Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.lpw.raw:data.hsbmlf', '2.5', 'MAVEN LPW Raw Low Frequency High Speed Burst Mode (HSBM) Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.lpw.raw:data.hsbmmf', '2.5', 'MAVEN LPW Raw Medium Frequency High Speed Burst Mode (HSBM) Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.lpw.raw:data.hsk', '2.5', 'MAVEN LPW Housekeeping Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.lpw.raw:data.pas', '2.5', 'MAVEN LPW Raw Wave Passive Sub-Cycle Potentials Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.lpw.raw:data.spechfact', '2.5', 'MAVEN LPW Raw Wave Active Sub-Cycle Onboard FFT in High Frequency Range Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.lpw.raw:data.spechfpas', '2.5', 'MAVEN LPW Raw Wave Passive Sub-Cycle Onboard FFT in High Frequency Range Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.lpw.raw:data.speclfact', '2.5', 'MAVEN LPW Raw Wave Active Sub-Cycle Onboard FFT in Low Frequency Range Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.lpw.raw:data.speclfpas', '2.5', 'MAVEN LPW Raw Wave Passive Sub-Cycle Onboard FFT in Low Frequency Range Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.lpw.raw:data.specmfact', '2.5', 'MAVEN LPW Raw Wave Active Sub-Cycle Onboard FFT in Medium Frequency Range Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.lpw.raw:data.specmfpas', '2.5', 'MAVEN LPW Raw Wave Passive Sub-Cycle Onboard FFT in Medium Frequency Range Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.lpw.raw:data.swp1', '2.5', 'MAVEN LPW Raw Langmuir Probe Sensor 1 Sub-Cycle Sweep Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.lpw.raw:data.swp2', '2.5', 'MAVEN LPW Raw Langmuir Probe Sensor 2 Sub-Cycle Sweep Data Collection', 'PDS4-Collection', NULL);

/* MAVEN MAG Calibrated Data Bundle/Collections */

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.mag.calibrated', '2.3', 'MAVEN MAG Calibrated Data Bundle', 'PDS4-Bundle', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.mag.calibrated:data.pc', '2.3', 'Tabulated vector magnetic field vs. time in planetocentric coordinates Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.mag.calibrated:data.pl', '2.3', 'Tabulated vector magnetic field vs. time in payload coordinates Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.mag.calibrated:data.ss', '2.3', 'Tabulated vector magnetic field vs. time in Sun-state coordinates Data Collection', 'PDS4-Collection', NULL);

/* MAVEN SEP Calibrated Data Bundle/Collections */

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.sep.calibrated', '1.9', 'MAVEN SEP Calibrated Data Bundle', 'PDS4-Bundle', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.sep.calibrated:data.anc', '1.8', 'MAVEN SEP Ancillary Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.sep.calibrated:data.counts', '1.9', 'MAVEN SEP Raw Ion and Electron Counts Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.sep.calibrated:data.spec', '1.9', 'MAVEN SEP Calibrated Ion and Electron Spectra Data Collection', 'PDS4-Collection', NULL);

/* MAVEN STATIC Calibrated Data Bundle/Collections */

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.static.c', '2.6', 'MAVEN STATIC Calibrated Data Bundle', 'PDS4-Bundle', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.static.c:data.2a_hkp', '2.6', 'MAVEN STATIC Housekeeping Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.static.c:data.c0_64e2m', '2.6', 'MAVEN STATIC Calibrated Energy Flux: 64 Energy x 2 Mass Bins Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.static.c:data.c2_32e32m', '2.0', 'MAVEN STATIC Calibrated Energy Flux: 32 Energy x 32 Mass Bins Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.static.c:data.c4_4e64m', '2.0', 'MAVEN STATIC Calibrated Energy Flux: 4 Energy X 64 Mass Bins Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.static.c:data.c6_32e64m', '2.6', 'MAVEN STATIC Calibrated Energy Flux: 32 Energy X 64 Mass Bins Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.static.c:data.c8_32e16d', '2.6', 'MAVEN STATIC Calibrated Energy Flux: 32 Energy X 16 Solid Angle Bins Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.static.c:data.ca_16e4d16a', '2.6', 'MAVEN STATIC Calibrated Energy Flux: 16 Energy X 64 Solid Angle Bins Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.static.c:data.cc_32e8d32m', '2.0', 'MAVEN STATIC Calibrated Energy Flux: 32 Energy X 8 Solid Angle X 32 Mass Bins Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.static.c:data.cd_32e8d32m', '2.0', 'MAVEN STATIC Calibrated Energy Flux: 32 Energy X 8 Solid Angle X 32 Mass Bins Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.static.c:data.ce_16e4d16a16m', '2.0', 'MAVEN STATIC Calibrated Energy Flux: 16 Energy X 64 Solid Angle Bins Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.static.c:data.cf_16e4d16a16m', '2.0', 'MAVEN STATIC Calibrated Energy Flux: 16 Energy X 64 Solid Angle X 16 Mass Bins Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.static.c:data.cf_16e4d16a16m', '2.0', 'MAVEN STATIC Calibrated Energy Flux: 16 Energy X 64 Solid Angle X 16 Mass Bins Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.static.c:data.da_1r', '2.6', 'MAVEN STATIC Event Rate Data Selected Channel Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.static.c:data.db_1024tof', '2.6', 'MAVEN STATIC Raw Time-of-Flight Data Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.static.c:data.d0_32e4d16a8m', '2.6', 'MAVEN STATIC Calibrated Energy Flux: 16 Energy X 64 Solid Angle X 8 Mass Bins Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.static.c:data.d1_32e4d16a8m', '2.6', 'MAVEN STATIC Calibrated Energy Flux: 16 Energy X 64 Solid Angle X 8 Mass Bins Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.static.c:data.d4_4d16a2m', '2.6', 'MAVEN STATIC Calibrated Energy Flux: 64 Solid Angle X 2 Mass Bins Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.static.c:data.d6_events', '2.6', 'MAVEN STATIC Raw Event Data Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.static.c:data.d7_fsthkp', '2.6', 'MAVEN STATIC Fast Housekeeping Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.static.c:data.d8_12r1e', '2.6', 'MAVEN STATIC Event Rate Data: 12 Rate Channels Summed Over a Single Spin Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.static.c:data.d9_12r64e', '2.6', 'MAVEN STATIC Event Rate Data: 12 Rate Channels Summed Over Multiple Spins Data Collection', 'PDS4-Collection', NULL);

/* MAVEN SWEA Calibrated Data Bundle/Collections */

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.swea.calibrated', '3.6', 'MAVEN SWEA Calibrated Data Bundle', 'PDS4-Bundle', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.swea.calibrated:data.arc_3d', '3.5', 'MAVEN SWEA Archive Rate 3D Electron Distributions Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.swea.calibrated:data.arc_pad', '3.5', 'MAVEN SWEA Archive Rate Electron Pitch Angle Distributions Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.swea.calibrated:data.svy_3d', '3.5', 'MAVEN SWEA Survey Rate 3D Electron Distributions Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.swea.calibrated:data.svy_pad', '3.5', 'MAVEN SWEA Survey Rate Electron Pitch Angle Distributions Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.swea.calibrated:data.svy_spec', '3.5', 'MAVEN SWEA Survey Rate Omni-Directional Electron Energy Spectra Data Collection', 'PDS4-Collection', NULL);

/* MAVEN SWIA Calibrated Data Bundle/Collections */

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.swia.calibrated', '1.11', 'MAVEN SWIA Calibrated Data Bundle', 'PDS4-Bundle', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.swia.calibrated:data.coarse_arc_3d', '1.11', 'MAVEN SWIA Calibrated Coarse Archive 3D Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.swia.calibrated:data.coarse_svy_3d', '1.11', 'MAVEN SWIA Calibrated Coarse Survey 3D Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.swia.calibrated:data.fine_arc_3d', '1.11', 'MAVEN SWIA Calibrated Fine Archive 3D Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.swia.calibrated:data.fine_svy_3d', '1.11', 'MAVEN SWIA Calibrated Fine Survey 3D Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.swia.calibrated:data.onboard_svy_mom', '1.11', 'MAVEN SWIA Calibrated Onboard Survey Moment Data Collection', 'PDS4-Collection', NULL);

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:maven.swia.calibrated:data.onboard_svy_spec', '1.11', 'MAVEN SWIA Calibrated Onboard Survey Spectra Data Collection', 'PDS4-Collection', NULL);

/*
** delivery
*/

/* Juno Gravity Deliveries */

insert into delivery (logical_identifier, version_id, name, start_date_time, stop_date_time, source, target, due_date) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-rss-1-ocru-v1.0', '1.0', 'Perijove 1/2', '2016-07-04T00:00:00', '2016-10-19T23:59:59', 'JSOC', 'Atmospheres Node', '2017-06-20');

insert into delivery (logical_identifier, version_id, name, start_date_time, stop_date_time, source, target, due_date) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-rss-1-ocru-v1.0', '1.0', 'Perijove 3/4', '2016-10-20T00:00:00', '2017-02-02T23:59:59', 'JSOC', 'Atmospheres Node', '2017-08-28');


insert into delivery (logical_identifier, version_id, name, start_date_time, stop_date_time, source, target, due_date) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-rss-1-jugr-v1.0', '1.0', 'Perijove 1/2', '2016-07-04T00:00:00', '2016-10-19T23:59:59', 'JSOC', 'Atmospheres Node', '2017-06-20');

insert into delivery (logical_identifier, version_id, name, start_date_time, stop_date_time, source, target, due_date) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-rss-1-jugr-v1.0', '1.0', 'Perijove 3/4', '2016-10-20T00:00:00', '2017-02-02T23:59:59', 'JSOC', 'Atmospheres Node', '2017-08-28');

/* Juno JIRAM Deliveries */

insert into delivery (logical_identifier, version_id, name, start_date_time, stop_date_time, source, target, due_date) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-jiram-2-edr-v1.0', '1.0', 'Perijove 1/2', '2016-07-04T00:00:00', '2016-10-19T23:59:59', 'JSOC', 'Atmospheres Node', '2017-06-20');

insert into delivery (logical_identifier, version_id, name, start_date_time, stop_date_time, source, target, due_date) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-jiram-2-edr-v1.0', '1.0', 'Perijove 3/4', '2016-10-20T00:00:00', '2017-02-02T23:59:59', 'JSOC', 'Atmospheres Node', '2017-08-28');


insert into delivery (logical_identifier, version_id, name, start_date_time, stop_date_time, source, target, due_date) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-jiram-3-edr-v1.0', '1.0', 'Perijove 1/2', '2016-07-04T00:00:00', '2016-10-19T23:59:59', 'JSOC', 'Atmospheres Node', '2017-06-20');

insert into delivery (logical_identifier, version_id, name, start_date_time, stop_date_time, source, target, due_date) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-jiram-3-edr-v1.0', '1.0', 'Perijove 3/4', '2016-10-20T00:00:00', '2017-02-02T23:59:59', 'JSOC', 'Atmospheres Node', '2017-08-28');

/* Juno MWR Deliveries */

insert into delivery (logical_identifier, version_id, name, start_date_time, stop_date_time, source, target, due_date) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-2-edr-orbit-v1.0', '1.0', 'Perijove 1/2', '2016-07-04T00:00:00', '2016-10-19T23:59:59', 'JSOC', 'Atmospheres Node', '2017-06-20');

insert into delivery (logical_identifier, version_id, name, start_date_time, stop_date_time, source, target, due_date) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-2-edr-orbit-v1.0', '1.0', 'Perijove 3/4', '2016-10-20T00:00:00', '2017-02-02T23:59:59', 'JSOC', 'Atmospheres Node', '2017-08-28');


insert into delivery (logical_identifier, version_id, name, start_date_time, stop_date_time, source, target, due_date) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-3-irdr-orbit-v1.0', '1.0', 'Perijove 1/2', '2016-07-04T00:00:00', '2016-10-19T23:59:59', 'JSOC', 'Atmospheres Node', '2017-06-20');

insert into delivery (logical_identifier, version_id, name, start_date_time, stop_date_time, source, target, due_date) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-3-irdr-orbit-v1.0', '1.0', 'Perijove 3/4', '2016-10-20T00:00:00', '2017-02-02T23:59:59', 'JSOC', 'Atmospheres Node', '2017-08-28');


insert into delivery (logical_identifier, version_id, name, start_date_time, stop_date_time, source, target, due_date) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-3-grdr-orbit-v1.0', '1.0', 'Perijove 1/2', '2016-07-04T00:00:00', '2016-10-19T23:59:59', 'JSOC', 'Atmospheres Node', '2017-06-20');

insert into delivery (logical_identifier, version_id, name, start_date_time, stop_date_time, source, target, due_date) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-3-grdr-orbit-v1.0', '1.0', 'Perijove 3/4', '2016-10-20T00:00:00', '2017-02-02T23:59:59', 'JSOC', 'Atmospheres Node', '2017-08-28');

/* Juno UVS Deliveries */

insert into delivery (logical_identifier, version_id, name, start_date_time, stop_date_time, source, target, due_date) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-uvs-2-edr-v1.0', '1.0', 'Perijove 1/2', '2016-07-04T00:00:00', '2016-10-19T23:59:59', 'JSOC', 'Atmospheres Node', '2017-06-20');

insert into delivery (logical_identifier, version_id, name, start_date_time, stop_date_time, source, target, due_date) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-uvs-2-rdr-v1.0', '1.0', 'Perijove 3/4', '2016-10-20T00:00:00', '2017-02-02T23:59:59', 'JSOC', 'Atmospheres Node', '2017-08-28');

/* Juno JADE Deliveries */

insert into delivery (logical_identifier, version_id, name, start_date_time, stop_date_time, source, target, due_date) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-sw-jad-2-uncalibrated-v1.0', '1.0', 'Perijove 1/2', '2016-07-04T00:00:00', '2016-10-19T23:59:59', 'JSOC', 'PPI Node', '2017-06-20');

insert into delivery (logical_identifier, version_id, name, start_date_time, stop_date_time, source, target, due_date) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-sw-jad-2-uncalibrated-v1.0', '1.0', 'Perijove 3/4', '2016-10-20T00:00:00', '2017-02-02T23:59:59', 'JSOC', 'PPI Node', '2017-08-28');


insert into delivery (logical_identifier, version_id, name, start_date_time, stop_date_time, source, target, due_date) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-sw-jad-2-uncalibrated-v1.0', '1.0', 'Perijove 1/2', '2016-07-04T00:00:00', '2016-10-19T23:59:59', 'JSOC', 'PPI Node', '2017-06-20');

insert into delivery (logical_identifier, version_id, name, start_date_time, stop_date_time, source, target, due_date) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-sw-jad-2-uncalibrated-v1.0', '1.0', 'Perijove 3/4', '2016-10-20T00:00:00', '2017-02-02T23:59:59', 'JSOC', 'PPI Node', '2017-08-28');


insert into delivery (logical_identifier, version_id, name, start_date_time, stop_date_time, source, target, due_date) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-sw-jad-3-calibrated-v1.0', '1.0', 'Perijove 1/2', '2016-07-04T00:00:00', '2016-10-19T23:59:59', 'JSOC', 'PPI Node', '2017-06-20');

insert into delivery (logical_identifier, version_id, name, start_date_time, stop_date_time, source, target, due_date) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-sw-jad-3-calibrated-v1.0', '1.0', 'Perijove 3/4', '2016-10-20T00:00:00', '2017-02-02T23:59:59', 'JSOC', 'PPI Node', '2017-08-28');

/* Juno FGM Deliveries */

insert into delivery (logical_identifier, version_id, name, start_date_time, stop_date_time, source, target, due_date) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-3-fgm-cal-v1.0', '1.0', 'Perijove 1/2', '2016-07-04T00:00:00', '2016-10-19T23:59:59', 'JSOC', 'PPI Node', '2017-06-20');

insert into delivery (logical_identifier, version_id, name, start_date_time, stop_date_time, source, target, due_date) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-3-fgm-cal-v1.0', '1.0', 'Perijove 3/4', '2016-10-20T00:00:00', '2017-02-02T23:59:59', 'JSOC', 'PPI Node', '2017-08-28');

/* Juno Waves Deliveries */

insert into delivery (logical_identifier, version_id, name, start_date_time, stop_date_time, source, target, due_date) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-2-edr-v1.0', '1.0', 'Perijove 1/2', '2016-07-04T00:00:00', '2016-10-19T23:59:59', 'JSOC', 'PPI Node', '2017-06-20');

insert into delivery (logical_identifier, version_id, name, start_date_time, stop_date_time, source, target, due_date) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-2-edr-v1.0', '1.0', 'Perijove 3/4', '2016-10-20T00:00:00', '2017-02-02T23:59:59', 'JSOC', 'PPI Node', '2017-08-28');


insert into delivery (logical_identifier, version_id, name, start_date_time, stop_date_time, source, target, due_date) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-3-cdr-srvfull-v1.0', '1.0', 'Perijove 1/2', '2016-07-04T00:00:00', '2016-10-19T23:59:59', 'JSOC', 'PPI Node', '2017-06-20');

insert into delivery (logical_identifier, version_id, name, start_date_time, stop_date_time, source, target, due_date) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-3-cdr-srvfull-v1.0', '1.0', 'Perijove 3/4', '2016-10-20T00:00:00', '2017-02-02T23:59:59', 'JSOC', 'PPI Node', '2017-08-28');


insert into delivery (logical_identifier, version_id, name, start_date_time, stop_date_time, source, target, due_date) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-3-cdr-bstfull-v1.0', '1.0', 'Perijove 1/2', '2016-07-04T00:00:00', '2016-10-19T23:59:59', 'JSOC', 'PPI Node', '2017-06-20');

insert into delivery (logical_identifier, version_id, name, start_date_time, stop_date_time, source, target, due_date) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-3-cdr-bstfull-v1.0', '1.0', 'Perijove 3/4', '2016-10-20T00:00:00', '2017-02-02T23:59:59', 'JSOC', 'PPI Node', '2017-08-28');

/* Juno JunoCam Deliveries */

/* Juno Radiation Deliveries */

/*
** role
*/

/* Juno Investigation Team */

insert into role (electronic_mail_address, reference) values ('sbolton@swri.edu', 'urn:nasa:pds:context_pds3:investigation:mission.juno');
insert into role (electronic_mail_address, reference) values ('william-kurth@uiowa.edu', 'urn:nasa:pds:context_pds3:investigation:mission.juno');
insert into role (electronic_mail_address, reference) values ('dgell@swri.edu', 'urn:nasa:pds:context_pds3:investigation:mission.juno');
insert into role (electronic_mail_address, reference) values ('pmokashi@swri.edu', 'urn:nasa:pds:context_pds3:investigation:mission.juno');

/* Juno Gravity Instrument Team */

insert into role (electronic_mail_address, reference) values ('sami.w.asmar@jpl.nasa.gov', 'urn:nasa:pds:context_pds3:instrument:gravity.jno');
insert into role (electronic_mail_address, reference) values ('dustin.r.buccino@jpl.nasa.gov', 'urn:nasa:pds:context_pds3:instrument:gravity.jno');

/* Juno JIRAM Instrument Team */

insert into role (electronic_mail_address, reference) values ('alberto.adriani@ifsi-roma.inaf.it', 'urn:nasa:pds:context_pds3:instrument:jiram.jno');
insert into role (electronic_mail_address, reference) values ('raffaella.noschese@iaps.inaf.it', 'urn:nasa:pds:context_pds3:instrument:jiram.jno');

/* Juno MWR Instrument Team */

insert into role (electronic_mail_address, reference) values ('michael.a.janssen@jpl.nasa.gov', 'urn:nasa:pds:context_pds3:instrument:mwr.jno');
insert into role (electronic_mail_address, reference) values ('edwin.sarkissian@jpl.nasa.gov', 'urn:nasa:pds:context_pds3:instrument:mwr.jno');

/* Juno JADE Instrument Team */

insert into role (electronic_mail_address, reference) values ('philip.valek@swri.org', 'urn:nasa:pds:context_pds3:instrument:jade.jno');
insert into role (electronic_mail_address, reference) values ('rob.wilson@lasp.colorado.edu', 'urn:nasa:pds:context_pds3:instrument:jade.jno');

/* Juno JEDI Instrument Team */

insert into role (electronic_mail_address, reference) values ('barry.mauk@jhuapl.edu', 'urn:nasa:pds:context_pds3:instrument:jedi.jno');
insert into role (electronic_mail_address, reference) values ('chris.paranicas@jhuapl.edu', 'urn:nasa:pds:context_pds3:instrument:jedi.jno');

/* Juno FGM Instrument Team */

insert into role (electronic_mail_address, reference) values ('jack.connerney@nasa.gov', 'urn:nasa:pds:context_pds3:instrument:fgm.jno');
insert into role (electronic_mail_address, reference) values ('patricia.j.lawton.1@gsfc.nasa.gov', 'urn:nasa:pds:context_pds3:instrument:fgm.jno');

/* Juno Waves Instrument Team */

insert into role (electronic_mail_address, reference) values ('william-kurth@uiowa.edu', 'urn:nasa:pds:context_pds3:instrument:waves.jno');
insert into role (electronic_mail_address, reference) values ('patricia.j.lawton.1@gsfc.nasa.gov', 'urn:nasa:pds:context_pds3:instrument:waves.jno');

/* Juno JunoCam Instrument Team */

insert into role (electronic_mail_address, reference) values ('cjhansen@psi.edu', 'urn:nasa:pds:context_pds3:instrument:jnc.jno');
insert into role (electronic_mail_address, reference) values ('lipkaman@msss.com', 'urn:nasa:pds:context_pds3:instrument:jnc.jno');

/* Juno Radiation Instrument Team */

insert into role (electronic_mail_address, reference) values ('heidi.n.becker@jpl.nasa.gov', 'urn:nasa:pds:context_pds3:instrument:radiation.jno');
insert into role (electronic_mail_address, reference) values ('ingrid.daubar@jpl.nasa.gov', 'urn:nasa:pds:context_pds3:instrument:radiation.jno');

/* Atmospheres Node Team */

insert into role (electronic_mail_address, reference) values ('lhuber@nmsu.edu', 'urn:nasa:pds:context_pds3:node:node.atmos');
insert into role (electronic_mail_address, reference) values ('rbeebe@nmsu.edu', 'urn:nasa:pds:context_pds3:node:node.atmos');

/* PPI Node Team */

insert into role (electronic_mail_address, reference) values ('jmafi@igpp.ucla.edu', 'urn:nasa:pds:context_pds3:node:node.ppi-ucla');

/* Imaging Node Team */

insert into role (electronic_mail_address, reference) values ('rafael.alanis@jpl.nasa.gov', 'urn:nasa:pds:context_pds3:node:node.imaging');

/* Engineering (Admin) Node Team */

insert into role (electronic_mail_address, reference) values ('sean.hardman@jpl.nasa.gov', 'urn:nasa:pds:context_pds3:node:node.en');

/*
** user
*/

insert into user (electronic_mail_address, name) values ('alberto.adriani@ifsi-roma.inaf.it', 'Alberto Adriani');
insert into user (electronic_mail_address, name) values ('barry.mauk@jhuapl.edu', 'Barry Mauk');
insert into user (electronic_mail_address, name) values ('btrantham@swri.edu', 'Brad Trantham');
insert into user (electronic_mail_address, name) values ('chris.paranicas@jhuapl.edu', 'Chris Paranicas');
insert into user (electronic_mail_address, name) values ('chris-piker@uiowa.edu', 'Chris Piker');
insert into user (electronic_mail_address, name) values ('cjhansen@psi.edu', 'Candy Hansen');
insert into user (electronic_mail_address, name) values ('dgell@swri.edu', 'Dave Gell');
insert into user (electronic_mail_address, name) values ('dustin.r.buccino@jpl.nasa.gov', 'Dustin Buccino');
insert into user (electronic_mail_address, name) values ('edwin.Sarkissian@jpl.nasa.gov', 'Edwin Sarkissian');
insert into user (electronic_mail_address, name) values ('heidi.n.becker@jpl.nasa.gov', 'Heidi Becker');
insert into user (electronic_mail_address, name) values ('ingrid.daubar@jpl.nasa.gov', 'Ingrid Daubar');
insert into user (electronic_mail_address, name) values ('jack.connerney@nasa.gov', 'Jack Connerney');
insert into user (electronic_mail_address, name) values ('jmafi@igpp.ucla.edu', 'Joe Mafi');
insert into user (electronic_mail_address, name) values ('lipkaman@msss.com', 'Leslie Lipkaman');
insert into user (electronic_mail_address, name) values ('lhuber@nmsu.edu', 'Lyle Huber');
insert into user (electronic_mail_address, name) values ('michael.a.janssen@jpl.nasa.gov', 'Michael Janssen');
insert into user (electronic_mail_address, name) values ('patricia.j.lawton.1@gsfc.nasa.gov', 'Pat Lawton');
insert into user (electronic_mail_address, name) values ('philip.valek@swri.org', 'Phil Valek');
insert into user (electronic_mail_address, name) values ('pmokashi@swri.edu', 'Pratchet Mokashi');
insert into user (electronic_mail_address, name) values ('rafael.alanis@jpl.nasa.gov', 'Rafael Alanis');
insert into user (electronic_mail_address, name) values ('raffaella.noschese@iaps.inaf.it', 'Raffaella Noschese');
insert into user (electronic_mail_address, name) values ('rbeebe@nmsu.edu', 'Reta Beebe');
insert into user (electronic_mail_address, name) values ('rgladstone@swri.edu', 'Randy Gladstone');
insert into user (electronic_mail_address, name) values ('rob.wilson@lasp.colorado.edu', 'Rob Wilson');
insert into user (electronic_mail_address, name) values ('sami.w.asmar@jpl.nasa.gov', 'Sami Asmar');
insert into user (electronic_mail_address, name) values ('sbolton@swri.edu', 'Scott Bolton');
insert into user (electronic_mail_address, name) values ('sean.hardman@jpl.nasa.gov', 'Sean Hardman');
insert into user (electronic_mail_address, name) values ('william-kurth@uiowa.edu', 'Bill Kurth');

/*
** Test Data
*/

/*
** archive_status
*/

insert into archive_status (logical_identifier, version_id, status_date_time, status, electronic_mail_address, comment) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-e-rss-1-eagr-v1.0', '1.0', '2017-10-01T00:00:00', 'IN PEER REVIEW', 'sean.hardman@jpl.nasa.gov', 'This is a test.');

insert into archive_status (logical_identifier, version_id, status_date_time, status, electronic_mail_address, comment) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-e-rss-1-eagr-v1.0', '1.0', '2017-10-16T00:00:00', 'ARCHIVED', 'sean.hardman@jpl.nasa.gov', 'This is a test.');

insert into archive_status (logical_identifier, version_id, status_date_time, status, electronic_mail_address, comment) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-rss-1-ocru-v1.0', '1.0', '2017-10-16T00:00:00', 'ARCHIVED', 'sean.hardman@jpl.nasa.gov', 'This is a test.');

/*
** certification_status
*/

insert into certification_status (logical_identifier, version_id, status_date_time, status, electronic_mail_address, comment) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-e-rss-1-eagr-v1.0', '1.0', '2017-10-16T00:00:00', 'CERTIFIED', 'sean.hardman@jpl.nasa.gov', 'This is a test.');

insert into certification_status (logical_identifier, version_id, status_date_time, status, electronic_mail_address, comment) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-rss-1-ocru-v1.0', '1.0', '2017-10-16T00:00:00', 'CERTIFIED', 'sean.hardman@jpl.nasa.gov', 'This is a test.');

/*
** doi
*/

/* Update registration_date to reflect EST. */

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:ladee_nms:data_calibrated', '1.0', '10.17189/1408892', '2017-11-15T09:44:42', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Aladee_nms%3Adata_calibrated&amp;version=1.0', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1408892.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:ladee_nms:data_raw', '1.0', '10.17189/1408893', '2017-11-15T09:50:39', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Aladee_nms%3Adata_raw&amp;version=1.0', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1408893.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:ladee_nms:document', '1.0', '10.17189/1408894', '2017-11-15T09:52:41', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Aladee_nms%3Adocument&amp;version=1.0', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1408894.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:ladee_nms:data_derived', '1.0', '10.17189/1408897', '2017-11-15T10:34:56', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Aladee_nms%3Adata_derived&amp;version=1.0', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1408897.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:ladee_nms', '1.0', '10.17189/1408898', '2017-11-15T10:36:06', 'https://pds.jpl.nasa.gov/ds-view/pds/viewBundle.jsp?identifier=urn%3Anasa%3Apds%3Aladee_nms&amp;version=1.0', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1408898.');

update doi set doi = '10.17189/1408898', registration_date = '2017-11-20T10:27:37', site_url = 'https://pds.jpl.nasa.gov/ds-view/pds/viewBundle.jsp?identifier=urn%3Anasa%3Apds%3Aladee_nms&amp;version=1.0', electronic_mail_address = 'sean.hardman@jpl.nasa.gov', comment = 'Updated by Ron Joyner. OSTI ID: 1408898.' where logical_identifier = 'urn:nasa:pds:ladee_nms' and version_id = '1.0';

update doi set doi = '10.17189/1408892', registration_date = '2017-11-20T11:02:53', site_url = 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Aladee_nms%3Adata_calibrated&amp;version=1.0', electronic_mail_address = 'sean.hardman@jpl.nasa.gov', comment = 'Updated by Ron Joyner. OSTI ID: 1408892.' where logical_identifier = 'urn:nasa:pds:ladee_nms:data_calibrated' and version_id = '1.0';

update doi set doi = '10.17189/1408897', registration_date = '2017-11-20T11:06:25', site_url = 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Aladee_nms%3Adata_derived&amp;version=1.0', electronic_mail_address = 'sean.hardman@jpl.nasa.gov', comment = 'Updated by Ron Joyner. OSTI ID: 1408897.' where logical_identifier = 'urn:nasa:pds:ladee_nms:data_derived' and version_id = '1.0';

update doi set doi = '10.17189/1408893', registration_date = '2017-11-20T11:07:19', site_url = 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Aladee_nms%3Adata_raw&amp;version=1.0', electronic_mail_address = 'sean.hardman@jpl.nasa.gov', comment = 'Updated by Ron Joyner. OSTI ID: 1408893.' where logical_identifier = 'urn:nasa:pds:ladee_nms:data_raw' and version_id = '1.0';

update doi set doi = '10.17189/1408894', registration_date = '2017-11-20T11:07:56', site_url = 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Aladee_nms%3Adocument&amp;version=1.0', electronic_mail_address = 'sean.hardman@jpl.nasa.gov', comment = 'Updated by Ron Joyner. OSTI ID: 1408894.' where logical_identifier = 'urn:nasa:pds:ladee_nms:document' and version_id = '1.0';

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.lpw', '1.1', '10.17189/1410658', '2017-11-30T11:21:54', 'https://pds.jpl.nasa.gov/ds-view/pds/viewBundle.jsp?identifier=urn%3Anasa%3Apds%3Amaven.lpw&amp;version=1.1', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1410658.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.static.c:data.c2_32e32m', '2.0', '10.17189/1410660', '2017-11-30T11:27:21', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.static.c%3Adata.c2_32e32m&amp;version=2.0', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1410660.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.static.c:data.c4_4e64m', '2.0', '10.17189/1410661', '2017-11-30T11:30:12', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.static.c%3Adata.c4_4e64m&amp;version=2.0', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1410661.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.static.c:data.cc_32e8d32m', '2.0', '10.17189/1410662', '2017-11-30T11:32:38', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.static.c%3Adata.cc_32e8d32m&amp;version=2.0', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1410662.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.static.c:data.cd_32e8d32m', '2.0', '10.17189/1410663', '2017-11-30T11:32:38', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.static.c%3Adata.cd_32e8d32m&amp;version=2.0', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1410663.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.static.c:data.ce_16e4d16a16m', '2.0', '10.17189/1410664', '2017-11-30T11:36:41', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.static.c%3Adata.ce_16e4d16a16m&amp;version=2.0', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1410664.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.static.c:data.cf_16e4d16a16m', '2.0', '10.17189/1410665', '2017-11-30T11:38:49', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.static.c%3Adata.cf_16e4d16a16m&amp;version=2.0', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1410665.');

update doi set doi = '10.17189/1410665', registration_date = '2017-11-30T12:20:00', site_url = 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.static.c%3Adata.cf_16e4d16a16m&amp;version=2.0', electronic_mail_address = 'sean.hardman@jpl.nasa.gov', comment = 'Updated by Ron Joyner. OSTI ID: 1410665.' where logical_identifier = 'urn:nasa:pds:maven.static.c:data.cf_16e4d16a16m' and version_id = '2.0';

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.anc', '1.10', '10.17189/1414170', '2017-12-20T09:44:36', 'https://pds.jpl.nasa.gov/ds-view/pds/viewBundle.jsp?identifier=urn%3Anasa%3Apds%3Amaven.anc&amp;version=1.10', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414170.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.euv.calibrated', '8.0', '10.17189/1414172', '2017-12-20T09:49:55', 'https://pds.jpl.nasa.gov/ds-view/pds/viewBundle.jsp?identifier=urn%3Anasa%3Apds%3Amaven.euv.calibrated&amp;version=8.0', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414172.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.euv.modelled', '6.0', '10.17189/1414173', '2017-12-20T09:51:08', 'https://pds.jpl.nasa.gov/ds-view/pds/viewBundle.jsp?identifier=urn%3Anasa%3Apds%3Amaven.euv.modelled&amp;version=6.0', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414173.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.insitu.calibrated', '11.0', '10.17189/1414174', '2017-12-20T09:51:42', 'https://pds.jpl.nasa.gov/ds-view/pds/viewBundle.jsp?identifier=urn%3Anasa%3Apds%3Amaven.insitu.calibrated&amp;version=11.0', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414174.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.lpw.calibrated', '4.5', '10.17189/1414175', '2017-12-20T09:52:21', 'https://pds.jpl.nasa.gov/ds-view/pds/viewBundle.jsp?identifier=urn%3Anasa%3Apds%3Amaven.lpw.calibrated&amp;version=4.5', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414175.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.lpw.derived', '4.5', '10.17189/1414176', '2017-12-20T09:53:28', 'https://pds.jpl.nasa.gov/ds-view/pds/viewBundle.jsp?identifier=urn%3Anasa%3Apds%3Amaven.lpw.derived&amp;version=4.5', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414176.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.lpw.raw', '2.5', '10.17189/1414177', '2017-12-20T09:53:59', 'https://pds.jpl.nasa.gov/ds-view/pds/viewBundle.jsp?identifier=urn%3Anasa%3Apds%3Amaven.lpw.raw&amp;version=2.5', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414177.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.mag.calibrated', '2.3', '10.17189/1414178', '2017-12-20T09:54:30', 'https://pds.jpl.nasa.gov/ds-view/pds/viewBundle.jsp?identifier=urn%3Anasa%3Apds%3Amaven.mag.calibrated&amp;version=2.3', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414178.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.static.c', '2.6', '10.17189/1414180', '2017-12-20T09:55:55', 'https://pds.jpl.nasa.gov/ds-view/pds/viewBundle.jsp?identifier=urn%3Anasa%3Apds%3Amaven.static.c&amp;version=2.6', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414180.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.swea.calibrated', '3.6', '10.17189/1414181', '2017-12-20T09:56:50', 'https://pds.jpl.nasa.gov/ds-view/pds/viewBundle.jsp?identifier=urn%3Anasa%3Apds%3Amaven.swea.calibrated&amp;version=3.6', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414181.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.swia.calibrated', '1.11', '10.17189/1414182', '2017-12-20T09:57:21', 'https://pds.jpl.nasa.gov/ds-view/pds/viewBundle.jsp?identifier=urn%3Anasa%3Apds%3Amaven.swia.calibrated&amp;version=1.11', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414182.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.anc:data.drf.eps', '1.10', '10.17189/1414183', '2017-12-20T09:57:59', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.anc%3Adata.drf.eps&amp;version=1.10', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414183.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.anc:data.drf.gnc', '1.10', '10.17189/1414184', '2017-12-20T09:58:51', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.anc%3Adata.drf.gnc&amp;version=1.10', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414184.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.anc:data.drf.ngms', '1.10', '10.17189/1414185', '2017-12-20T09:59:25', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.anc%3Adata.drf.ngms&amp;version=1.10', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414185.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.anc:data.drf.pf', '1.10', '10.17189/1414186', '2017-12-20T10:00:05', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.anc%3Adata.drf.pf&amp;version=1.10', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414186.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.anc:data.drf.pte', '1.10', '10.17189/1414187', '2017-12-20T10:00:45', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.anc%3Adata.drf.pte&amp;version=1.10', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414187.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.anc:data.drf.rs', '1.10', '10.17189/1414188', '2017-12-20T10:01:25', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.anc%3Adata.drf.rs&amp;version=1.10', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414188.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.anc:data.drf.sasm1', '1.10', '10.17189/1414189', '2017-12-20T10:02:00', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.anc%3Adata.drf.sasm1&amp;version=1.10', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414189.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.anc:data.drf.sasm2', '1.10', '10.17189/1414190', '2017-12-20T10:02:36', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.anc%3Adata.drf.sasm2&amp;version=1.10', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414190.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.anc:data.drf.sasm3', '1.10', '10.17189/1414191', '2017-12-20T10:03:09', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.anc%3Adata.drf.sasm3&amp;version=1.10', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414191.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.anc:data.drf.usm1', '1.10', '10.17189/1414192', '2017-12-20T10:03:46', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.anc%3Adata.drf.usm1&amp;version=1.10', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414192.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.anc:data.drf.usm2', '1.10', '10.17189/1414194', '2017-12-20T10:04:43', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.anc%3Adata.drf.usm2&amp;version=1.10', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414194.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.anc:data.drf.usm3', '1.10', '10.17189/1414195', '2017-12-20T10:05:15', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.anc%3Adata.drf.usm3&amp;version=1.10', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414195.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.anc:data.drf.usm4', '1.10', '10.17189/1414196', '2017-12-20T10:05:46', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.anc%3Adata.drf.usm4&amp;version=1.10', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414196.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.anc:data.drf.usm5', '1.10', '10.17189/1414197', '2017-12-20T10:06:19', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.anc%3Adata.drf.usm5&amp;version=1.10', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414197.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.anc:data.drf.usm6', '1.10', '10.17189/1414198', '2017-12-20T10:07:02', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.anc%3Adata.drf.usm6&amp;version=1.10', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414198.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.anc:data.events', '3.2', '10.17189/1414200', '2017-12-20T10:07:39', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.anc%3Adata.events&amp;version=3.2', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414200.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.anc:data.imu', '1.10', '10.17189/1414201', '2017-12-20T10:08:27', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.anc%3Adata.imu&amp;version=1.10', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414201.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.lpw.raw:data.act', '2.5', '10.17189/1414202', '2017-12-20T10:09:00', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.lpw.raw%3Adata.act&amp;version=2.5', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414202.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.lpw.raw:data.adr', '2.5', '10.17189/1414203', '2017-12-20T10:09:41', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.lpw.raw%3Adata.adr&amp;version=2.5', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414203.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.lpw.raw:data.atr', '2.5', '10.17189/1414204', '2017-12-20T10:10:18', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.lpw.raw%3Adata.atr&amp;version=2.5', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414204.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.lpw.raw:data.euv', '2.5', '10.17189/1414205', '2017-12-20T10:10:50', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.lpw.raw%3Adata.euv&amp;version=2.5', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414205.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.lpw.raw:data.hsbmhf', '2.5', '10.17189/1414206', '2017-12-20T10:11:28', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.lpw.raw%3Adata.hsbmhf&amp;version=2.5', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414206.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.lpw.raw:data.hsbmlf', '2.5', '10.17189/1414207', '2017-12-20T10:12:06', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.lpw.raw%3Adata.hsbmlf&amp;version=2.5', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414207.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.lpw.raw:data.hsbmmf', '2.5', '10.17189/1414208', '2017-12-20T10:12:44', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.lpw.raw%3Adata.hsbmmf&amp;version=2.5', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414208.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.lpw.raw:data.hsk', '2.5', '10.17189/1414209', '2017-12-20T10:13:17', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.lpw.raw%3Adata.hsk&amp;version=2.5', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414209.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.lpw.raw:data.pas', '2.5', '10.17189/1414210', '2017-12-20T10:13:52', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.lpw.raw%3Adata.pas&amp;version=2.5', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414210.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.lpw.raw:data.spechfact', '2.5', '10.17189/1414211', '2017-12-20T10:14:26', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.lpw.raw%3Adata.spechfact&amp;version=2.5', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414211.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.lpw.raw:data.spechfpas', '2.5', '10.17189/1414212', '2017-12-20T10:15:21', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.lpw.raw%3Adata.spechfpas&amp;version=2.5', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414212.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.lpw.raw:data.speclfact', '2.5', '10.17189/1414213', '2017-12-20T10:15:58', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.lpw.raw%3Adata.speclfact&amp;version=2.5', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414213.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.lpw.raw:data.speclfpas', '2.5', '10.17189/1414215', '2017-12-20T10:16:36', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.lpw.raw%3Adata.speclfpas&amp;version=2.5', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414215.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.lpw.raw:data.specmfact', '2.5', '10.17189/1414216', '2017-12-20T10:17:11', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.lpw.raw%3Adata.specmfact&amp;version=2.5', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414216.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.lpw.raw:data.swp1', '2.5', '10.17189/1414217', '2017-12-20T10:17:47', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.lpw.raw%3Adata.swp1&amp;version=2.5', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414217.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.static.c:data.2a_hkp', '2.6', '10.17189/1414219', '2017-12-20T10:19:06', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.static.c%3Adata.2a_hkp&amp;version=2.6', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414219.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.sep.calibrated:data.anc', '1.8', '10.17189/1414220', '2017-12-20T10:19:44', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.sep.calibrated%3Adata.anc&amp;version=1.8', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414220.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.swea.calibrated:data.arc_3d', '3.5', '10.17189/1414221', '2017-12-20T10:19:44', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.swea.calibrated%3Adata.arc_3d&amp;version=3.5', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414221.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.swea.calibrated:data.arc_pad', '3.5', '10.17189/1414222', '2017-12-20T10:20:55', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.swea.calibrated%3Adata.arc_pad&amp;version=3.5', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414222.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.euv.calibrated:data.bands', '8.0', '10.17189/1414223', '2017-12-20T10:21:30', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.euv.calibrated%3Adata.bands&amp;version=8.0', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414223.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.static.c:data.c0_64e2m', '2.6', '10.17189/1414224', '2017-12-20T10:22:06', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.static.c%3Adata.c0_64e2m&amp;version=2.6', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414224.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.static.c:data.c8_32e16d', '2.6', '10.17189/1414225', '2017-12-20T10:22:43', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.static.c%3Adata.c8_32e16d&amp;version=2.6', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414225.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.static.c:data.c6_32e64m', '2.6', '10.17189/1414226', '2017-12-20T10:23:50', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.static.c%3Adata.c6_32e64m&amp;version=2.6', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414226.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.static.c:data.ca_16e4d16a', '2.6', '10.17189/1414227', '2017-12-20T10:24:35', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.static.c%3Adata.ca_16e4d16a&amp;version=2.6', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414227.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.sep.calibrated:data.spec', '1.9', '10.17189/1414228', '2017-12-20T10:25:11', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.sep.calibrated%3Adata.spec&amp;version=1.9', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414228.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.swia.calibrated:data.coarse_arc_3d', '1.11', '10.17189/1414229', '2017-12-20T10:25:45', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.swia.calibrated%3Adata.coarse_arc_3d&amp;version=1.11', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414229.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.static.c:data.d0_32e4d16a8m', '2.6', '10.17189/1414230', '2017-12-20T10:26:21', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.static.c%3Adata.d0_32e4d16a8m&amp;version=2.6', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414230.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.swia.calibrated:data.coarse_svy_3d', '1.11', '10.17189/1414231', '2017-12-20T10:27:17', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.swia.calibrated%3Adata.coarse_svy_3d&amp;version=1.11', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414231.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.static.c:data.d1_32e4d16a8m', '2.6', '10.17189/1414232', '2017-12-20T10:28:18', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.static.c%3Adata.d1_32e4d16a8m&amp;version=2.6', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414232.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.static.c:data.d4_4d16a2m', '2.6', '10.17189/1414233', '2017-12-20T10:28:58', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.static.c%3Adata.d4_4d16a2m&amp;version=2.6', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414233.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.static.c:data.d6_events', '2.6', '10.17189/1414234', '2017-12-20T10:29:30', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.static.c%3Adata.d6_events&amp;version=2.6', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414234.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.static.c:data.d7_fsthkp', '2.6', '10.17189/1414235', '2017-12-20T10:30:10', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.static.c%3Adata.d6_events&amp;version=2.6', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414235.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.static.c:data.d8_12r1e', '2.6', '10.17189/1414236', '2017-12-20T10:30:43', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.static.c%3Adata.d8_12r1e&amp;version=2.6', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414236.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.static.c:data.d9_12r64e', '2.6', '10.17189/1414237', '2017-12-20T10:31:14', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.static.c%3Adata.d9_12r64e&amp;version=2.6', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414237.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.static.c:data.da_1r', '2.6', '10.17189/1414238', '2017-12-20T10:31:46', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.static.c%3Adata.da_1r&amp;version=2.6', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414238.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.static.c:data.db_1024tof', '2.6', '10.17189/1414239', '2017-12-20T10:32:19', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.static.c%3Adata.db_1024tof&amp;version=2.6', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414239.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.swia.calibrated:data.fine_arc_3d', '1.11', '10.17189/1414240', '2017-12-20T10:32:52', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.swia.calibrated%3Adata.fine_arc_3d&amp;version=1.11', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414240.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.insitu.calibrated:data.kp', '11.0', '10.17189/1414242', '2017-12-20T10:34:03', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.insitu.calibrated%3Adata.kp&amp;version=11.0', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414242.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.lpw.calibrated:data.lp.iv', '4.5', '10.17189/1414243', '2017-12-20T10:34:39', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.lpw.calibrated%3Adata.lp.iv&amp;version=4.5', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414243.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.lpw.derived:data.lp.nt', '3.5', '10.17189/1414244', '2017-12-20T10:35:12', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.lpw.derived%3Adata.lp.nt&amp;version=3.5', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414244.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.lpw.calibrated:data.mrg.scpot', '4.5', '10.17189/1414245', '2017-12-20T10:35:49', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.lpw.calibrated%3Adata.mrg.scpot&amp;version=4.5', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414245.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.swia.calibrated:data.onboard_svy_mom', '1.11', '10.17189/1414246', '2017-12-20T10:36:28', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.swia.calibrated%3Adata.onboard_svy_mom&amp;version=1.11', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414246.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.mag.calibrated:data.pc', '2.3', '10.17189/1414247', '2017-12-20T10:37:05', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.mag.calibrated%3Adata.pc&amp;version=2.3', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414247.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.swia.calibrated:data.onboard_svy_spec', '1.11', '10.17189/1414248', '2017-12-20T10:39:25', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.swia.calibrated%3Adata.onboard_svy_spec&amp;version=1.11', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414248.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.mag.calibrated:data.pl', '2.3', '10.17189/1414249', '2017-12-20T10:40:13', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.mag.calibrated%3Adata.pl&amp;version=2.3', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414249.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.sep.calibrated:data.counts', '1.9', '10.17189/1414250', '2017-12-20T10:40:46', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.sep.calibrated%3Adata.counts&amp;version=1.9', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414250.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.mag.calibrated:data.ss', '2.3', '10.17189/1414251', '2017-12-20T10:41:16', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.mag.calibrated%3Adata.ss&amp;version=2.3', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414251.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.swea.calibrated:data.svy_3d', '3.5', '10.17189/1414252', '2017-12-20T10:41:57', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.swea.calibrated%3Adata.svy_3d&amp;version=3.5', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414252.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.swea.calibrated:data.svy_pad', '3.5', '10.17189/1414253', '2017-12-20T10:42:28', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.swea.calibrated%3Adata.svy_pad&amp;version=3.5', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414253.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.swea.calibrated:data.svy_spec', '3.5', '10.17189/1414254', '2017-12-20T10:43:11', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.swea.calibrated%3Adata.svy_spec&amp;version=3.5', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414254.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.lpw.calibrated:data.w.e12', '4.5', '10.17189/1414255', '2017-12-20T10:43:43', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.lpw.calibrated%3Adata.w.e12&amp;version=4.5', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414255.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.lpw.calibrated:data.w.e12bursthf', '4.5', '10.17189/1414256', '2017-12-20T10:44:19', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.lpw.calibrated%3Adata.w.e12bursthf&amp;version=4.5', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414256.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.lpw.calibrated:data.w.e12burstlf', '4.5', '10.17189/1414257', '2017-12-20T10:44:50', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.lpw.calibrated%3Adata.w.e12burstlf&amp;version=4.5', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414257.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.lpw.derived:data.w.n', '4.5', '10.17189/1414258', '2017-12-20T10:45:28', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.lpw.derived%3Adata.w.n&amp;version=4.5', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414258.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.lpw.calibrated:data.w.specact', '4.5', '10.17189/1414259', '2017-12-20T10:46:00', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.lpw.calibrated%3Adata.w.specact&amp;version=4.5', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414259.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.lpw.calibrated:data.w.specpas', '4.5', '10.17189/1414260', '2017-12-20T10:46:35', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.lpw.calibrated%3Adata.w.specpas&amp;version=4.5', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414260.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.euv.modelled:data.daily.spectra', '6.0', '10.17189/1414261', '2017-12-20T10:47:06', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.euv.modelled%3Adata.daily.spectra&amp;version=6.0', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414261.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.euv.modelled:data.minute.spectra', '6.0', '10.17189/1414262', '2017-12-20T10:47:36', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.euv.modelled%3Adata.minute.spectra&amp;version=6.0', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414262.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.lpw.raw:data.specmfpas', '2.5', '10.17189/1414270', '2017-12-20T13:37:09', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.lpw.raw%3Adata.specmfpas&amp;version=2.5', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414270.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.lpw.calibrated:data.w.e12burstmf', '4.5', '10.17189/1414271', '2017-12-20T13:40:27', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.lpw.calibrated%3Adata.w.e12burstmf&amp;version=4.5', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414271.');

/* 
insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.anc:data.drf.ngms', '1.10', '10.17189/1414440', '2017-12-21T16:04:31', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.anc%3Adata.drf.ngms&amp;version=1.10', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414440.');

insert into doi (logical_identifier, version_id, doi, registration_date, site_url, electronic_mail_address, comment) values ('urn:nasa:pds:maven.anc:data.drf.ngms', '1.10', '10.17189/1414442', '2017-12-21T16:08:24', 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.anc%3Adata.drf.ngms&amp;version=1.10', 'sean.hardman@jpl.nasa.gov', 'Added by Ron Joyner. OSTI ID: 1414442.');
*/

update doi set doi = '10.17189/1414185', registration_date = '2017-12-21T16:09:37', site_url = 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.anc%3Adata.drf.ngms&amp;version=1.10', electronic_mail_address = 'sean.hardman@jpl.nasa.gov', comment = 'Updated by Ron Joyner. OSTI ID: 1414185.' where logical_identifier = 'urn:nasa:pds:maven.anc:data.drf.ngms' and version_id = '1.10';

update doi set doi = '10.17189/1410658', registration_date = '2018-01-10T10:20:51', site_url = 'https://pds.jpl.nasa.gov/ds-view/pds/viewBundle.jsp?identifier=urn%3Anasa%3Apds%3Amaven.lpw&amp;version=1.1', electronic_mail_address = 'sean.hardman@jpl.nasa.gov', comment = 'Updated by Ron Joyner. OSTI ID: 1410658.' where logical_identifier = 'urn:nasa:pds:maven.lpw' and version_id = '1.1';

update doi set doi = '10.17189/1410660', registration_date = '2018-01-10T10:22:03', site_url = 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.static.c%3Adata.c2_32e32m&amp;version=2.0', electronic_mail_address = 'sean.hardman@jpl.nasa.gov', comment = 'Updated by Ron Joyner. OSTI ID: 1410660.' where logical_identifier = 'urn:nasa:pds:maven.static.c:data.c2_32e32m' and version_id = '2.0';

update doi set doi = '10.17189/1410661', registration_date = '2018-01-10T10:22:51', site_url = 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.static.c%3Adata.c4_4e64m&amp;version=2.0', electronic_mail_address = 'sean.hardman@jpl.nasa.gov', comment = 'Updated by Ron Joyner. OSTI ID: 1410661.' where logical_identifier = 'urn:nasa:pds:maven.static.c:data.c4_4e64m' and version_id = '2.0';

update doi set doi = '10.17189/1410662', registration_date = '2018-01-10T10:23:29', site_url = 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.static.c%3Adata.cc_32e8d32m&amp;version=2.0', electronic_mail_address = 'sean.hardman@jpl.nasa.gov', comment = 'Updated by Ron Joyner. OSTI ID: 1410662.' where logical_identifier = 'urn:nasa:pds:maven.static.c:data.cc_32e8d32m' and version_id = '2.0';

update doi set doi = '10.17189/1410663', registration_date = '2018-01-10T10:24:05', site_url = 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.static.c%3Adata.cd_32e8d32m&amp;version=2.0', electronic_mail_address = 'sean.hardman@jpl.nasa.gov', comment = 'Updated by Ron Joyner. OSTI ID: 1410663.' where logical_identifier = 'urn:nasa:pds:maven.static.c:data.cd_32e8d32m' and version_id = '2.0';

update doi set doi = '10.17189/1410664', registration_date = '2018-01-10T10:24:37', site_url = 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.static.c%3Adata.ce_16e4d16a16m&amp;version=2.0', electronic_mail_address = 'sean.hardman@jpl.nasa.gov', comment = 'Updated by Ron Joyner. OSTI ID: 1410664.' where logical_identifier = 'urn:nasa:pds:maven.static.c:data.ce_16e4d16a16m' and version_id = '2.0';

update doi set doi = '10.17189/1410665', registration_date = '2018-01-10T10:25:14', site_url = 'https://pds.jpl.nasa.gov/ds-view/pds/viewCollection.jsp?identifier=urn%3Anasa%3Apds%3Amaven.static.c%3Adata.cf_16e4d16a16m&amp;version=2.0', electronic_mail_address = 'sean.hardman@jpl.nasa.gov', comment = 'Updated by Ron Joyner. OSTI ID: 1410665.' where logical_identifier = 'urn:nasa:pds:maven.static.c:data.cf_16e4d16a16m' and version_id = '2.0';

/*
** nssdca_status
*/

insert into nssdca_status (logical_identifier, version_id, status_date_time, nssdca_identifier, electronic_mail_address, comment) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-e-rss-1-eagr-v1.0', '1.0', '2017-10-16T00:00:00', 'N00000', 'sean.hardman@jpl.nasa.gov', 'This is a test.');

insert into nssdca_status (logical_identifier, version_id, status_date_time, nssdca_identifier, electronic_mail_address, comment) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-rss-1-ocru-v1.0', '1.0', '2017-10-16T00:00:00', 'N00001', 'sean.hardman@jpl.nasa.gov', 'This is a test.');

