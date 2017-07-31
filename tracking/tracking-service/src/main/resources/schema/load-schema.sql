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
** product/reference
*/

/* Juno Gravity Data Sets */

/*
insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-e-rss-1-eagr-v1.0', '1.0', 'JUNO-E-RSS-1-EAGR-V1.0', 'PDS3-Data-Set', 'JUNO-E-RSS-1-EAGR-V1.0');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-e-rss-1-eagr-v1.0', 'urn:nasa:pds:context_pds3:investigation:mission.juno', 'Investigation');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-e-rss-1-eagr-v1.0', 'urn:nasa:pds:context_pds3:instrument:gravity.jno', 'Instrument');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-e-rss-1-eagr-v1.0', 'urn:nasa:pds:context_pds3:node:node.atmos', 'Node');
*/


insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-e-rss-1-eagr-v1.0', '1.0', 'JUNO-E-RSS-1-EAGR-V1.0', 'PDS3-Data-Set', 'JUNO-E-RSS-1-EAGR-V1.0');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-e-rss-1-eagr-v1.0', 'Juno', 'Investigation');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-e-rss-1-eagr-v1.0', 'Gravity', 'Instrument');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-e-rss-1-eagr-v1.0', 'Atmospheres', 'Node');


insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-rss-1-ocru-v1.0', '1.0', 'JUNO-J-RSS-1-OCRU-V1.0', 'PDS3-Data-Set', 'JUNO-J-RSS-1-OCRU-V1.0');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-rss-1-ocru-v1.0', 'Juno', 'Investigation');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-rss-1-ocru-v1.0', 'Gravity', 'Instrument');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-rss-1-ocru-v1.0', 'Atmospheres', 'Node');


insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-rss-1-jugr-v1.0', '1.0', 'JUNO-J-RSS-1-JUGR-V1.0', 'PDS3-Data-Set', 'JUNO-J-RSS-1-JUGR-V1.0');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-rss-1-jugr-v1.0', 'Juno', 'Investigation');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-rss-1-jugr-v1.0', 'Gravity', 'Instrument');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-rss-1-jugr-v1.0', 'Atmospheres', 'Node');

/* Juno JIRAM Data Sets */

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-l-jiram-2-edr-v1.0', '1.0', 'JUNO-L-JIRAM-2-EDR-V1.0', 'PDS3-Data-Set', 'JUNO-L-JIRAM-2-EDR-V1.0');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-l-jiram-2-edr-v1.0', 'Juno', 'Investigation');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-l-jiram-2-edr-v1.0', 'JIRAM', 'Instrument');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-l-jiram-2-edr-v1.0', 'Atmospheres', 'Node');


insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-l-jiram-3-rdr-v1.0', '1.0', 'JUNO-L-JIRAM-3-RDR-V1.0', 'PDS3-Data-Set', 'JUNO-L-JIRAM-3-RDR-V1.0');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-l-jiram-3-rdr-v1.0', 'Juno', 'Investigation');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-l-jiram-3-rdr-v1.0', 'JIRAM', 'Instrument');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-l-jiram-3-rdr-v1.0', 'Atmospheres', 'Node');


insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-jiram-2-edr-v1.0', '1.0', 'JUNO-J-JIRAM-2-EDR-V1.0', 'PDS3-Data-Set', 'JUNO-J-JIRAM-2-EDR-V1.0');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-jiram-2-edr-v1.0', 'Juno', 'Investigation');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-jiram-2-edr-v1.0', 'JIRAM', 'Instrument');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-jiram-2-edr-v1.0', 'Atmospheres', 'Node');


insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-jiram-3-rdr-v1.0', '1.0', 'JUNO-J-JIRAM-3-RDR-V1.0', 'PDS3-Data-Set', 'JUNO-J-JIRAM-3-RDR-V1.0');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-jiram-3-rdr-v1.0', 'Juno', 'Investigation');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-jiram-3-rdr-v1.0', 'JIRAM', 'Instrument');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-j-jiram-3-rdr-v1.0', 'Atmospheres', 'Node');

/* Juno MWR Data Sets */

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-2-edr-cruise-v1.0', '1.0', 'JNO-J-MWR-2-EDR-cruise-V1.0', 'PDS3-Data-Set', 'JNO-J-MWR-2-EDR-cruise-V1.0');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-2-edr-cruise-v1.0', 'Juno', 'Investigation');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-2-edr-cruise-v1.0', 'MWR', 'Instrument');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-2-edr-cruise-v1.0', 'Atmospheres', 'Node');


insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-3-irdr-cruise-v1.0', '1.0', 'JNO-J-MWR-3-IRDR-cruise-V1.0', 'PDS3-Data-Set', 'JNO-J-MWR-3-IRDR-cruise-V1.0');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-3-irdr-cruise-v1.0', 'Juno', 'Investigation');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-3-irdr-cruise-v1.0', 'MWR', 'Instrument');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-3-irdr-cruise-v1.0', 'Atmospheres', 'Node');


insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-3-grdr-cruise-v1.0', '1.0', 'JNO-J-MWR-3-GRDR-cruise-V1.0', 'PDS3-Data-Set', 'JNO-J-MWR-3-GRDR-cruise-V1.0');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-3-grdr-cruise-v1.0', 'Juno', 'Investigation');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-3-grdr-cruise-v1.0', 'MWR', 'Instrument');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-3-grdr-cruise-v1.0', 'Atmospheres', 'Node');


insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-2-edr-orbit-v1.0', '1.0', 'JNO-J-MWR-2-EDR-orbit-V1.0', 'PDS3-Data-Set', 'JNO-J-MWR-2-EDR-orbit-V1.0');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-2-edr-orbit-v1.0', 'Juno', 'Investigation');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-2-edr-orbit-v1.0', 'MWR', 'Instrument');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-2-edr-orbit-v1.0', 'Atmospheres', 'Node');


insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-3-irdr-orbit-v1.0', '1.0', 'JNO-J-MWR-3-IRDR-orbit-V1.0', 'PDS3-Data-Set', 'JNO-J-MWR-3-IRDR-orbit-V1.0');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-3-irdr-orbit-v1.0', 'Juno', 'Investigation');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-3-irdr-orbit-v1.0', 'MWR', 'Instrument');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-3-irdr-orbit-v1.0', 'Atmospheres', 'Node');


insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-3-grdr-orbit-v1.0', '1.0', 'JNO-J-MWR-3-GRDR-orbit-V1.0', 'PDS3-Data-Set', 'JNO-J-MWR-3-GRDR-orbit-V1.0');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-3-grdr-orbit-v1.0', 'Juno', 'Investigation');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-3-grdr-orbit-v1.0', 'MWR', 'Instrument');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-mwr-3-grdr-orbit-v1.0', 'Atmospheres', 'Node');

/* Juno UVS Data Sets */

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-uvs-2-edr-v1.0', '1.0', 'JNO-J-UVS-2-EDR-V1.0', 'PDS3-Data-Set', 'JNO-J-UVS-2-EDR-V1.0');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-uvs-2-edr-v1.0', 'Juno', 'Investigation');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-uvs-2-edr-v1.0', 'UVS', 'Instrument');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-uvs-2-edr-v1.0', 'Atmospheres', 'Node');


insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-uvs-2-rdr-v1.0', '1.0', 'JNO-J-UVS-2-RDR-V1.0', 'PDS3-Data-Set', 'JNO-J-UVS-2-RDR-V1.0');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-uvs-2-rdr-v1.0', 'Juno', 'Investigation');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-uvs-2-rdr-v1.0', 'UVS', 'Instrument');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-uvs-2-rdr-v1.0', 'Atmospheres', 'Node');

/* Juno JADE Data Sets */

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-sw-jad-2-uncalibrated-v1.0', '1.0', 'JNO-SW-JAD-2-UNCALIBRATED-V1.0', 'PDS3-Data-Set', 'JNO-SW-JAD-2-UNCALIBRATED-V1.0');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-sw-jad-2-uncalibrated-v1.0', 'Juno', 'Investigation');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-sw-jad-2-uncalibrated-v1.0', 'JADE', 'Instrument');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-sw-jad-2-uncalibrated-v1.0', 'Atmospheres', 'Node');


insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-sw-jad-2-uncalibrated-v1.0', '1.0', 'JNO-J/SW-JAD-2-UNCALIBRATED-V1.0', 'PDS3-Data-Set', 'JNO-J/SW-JAD-2-UNCALIBRATED-V1.0');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-sw-jad-2-uncalibrated-v1.0', 'Juno', 'Investigation');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-sw-jad-2-uncalibrated-v1.0', 'JADE', 'Instrument');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-sw-jad-2-uncalibrated-v1.0', 'Atmospheres', 'Node');


insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-sw-jad-3-calibrated-v1.0', '1.0', 'JNO-J/SW-JAD-3-CALIBRATED-V1.0', 'PDS3-Data-Set', 'JNO-J/SW-JAD-3-CALIBRATED-V1.0');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-sw-jad-3-calibrated-v1.0', 'Juno', 'Investigation');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-sw-jad-3-calibrated-v1.0', 'JADE', 'Instrument');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-sw-jad-3-calibrated-v1.0', 'Atmospheres', 'Node');


insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-sw-jad-5-pad-v1.0', '1.0', 'JNO-J/SW-JAD-5-PAD-V1.0', 'PDS3-Data-Set', 'JNO-J/SW-JAD-5-PAD-V1.0');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-sw-jad-5-pad-v1.0', 'Juno', 'Investigation');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-sw-jad-5-pad-v1.0', 'JADE', 'Instrument');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-sw-jad-5-pad-v1.0', 'Atmospheres', 'Node');


insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-sw-jad-5-plasma-v1.0', '1.0', 'JNO-J/SW-JAD-5-PLASMA-V1.0', 'PDS3-Data-Set', 'JNO-J/SW-JAD-5-PLASMA-V1.0');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-sw-jad-5-plasma-v1.0', 'Juno', 'Investigation');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-sw-jad-5-plasma-v1.0', 'JADE', 'Instrument');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-sw-jad-5-plasma-v1.0', 'Atmospheres', 'Node');


insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-sw-jad-5-mom-v1.0', '1.0', 'JNO-J/SW-JAD-5-MOM-V1.0', 'PDS3-Data-Set', 'JNO-J/SW-JAD-5-MOM-V1.0');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-sw-jad-5-mom-v1.0', 'Juno', 'Investigation');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-sw-jad-5-mom-v1.0', 'JADE', 'Instrument');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-sw-jad-5-mom-v1.0', 'Atmospheres', 'Node');

/* Juno JEDI Data Sets */

/* Juno FGM Data Sets */

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-sw-3-fgm-cal-v1.0', '1.0', 'JNO-SW-3-FGM-CAL-V1.0', 'PDS3-Data-Set', 'JNO-SW-3-FGM-CAL-V1.0');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-sw-3-fgm-cal-v1.0', 'Juno', 'Investigation');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-sw-3-fgm-cal-v1.0', 'FGM', 'Instrument');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-sw-3-fgm-cal-v1.0', 'Atmospheres', 'Node');


insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-3-fgm-cal-v1.0', '1.0', 'JNO-J-3-FGM-CAL-V1.0', 'PDS3-Data-Set', 'JNO-J-3-FGM-CAL-V1.0');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-3-fgm-cal-v1.0', 'Juno', 'Investigation');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-3-fgm-cal-v1.0', 'FGM', 'Instrument');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-3-fgm-cal-v1.0', 'Atmospheres', 'Node');


insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-2-fgm-n-v1.0', '1.0', 'JNO-J-2-FGM-N-V1.0', 'PDS3-Data-Set', 'JNO-J-2-FGM-N-V1.0');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-2-fgm-n-v1.0', 'Juno', 'Investigation');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-2-fgm-n-v1.0', 'FGM', 'Instrument');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-j-2-fgm-n-v1.0', 'Atmospheres', 'Node');

/* Juno Waves Data Sets */

insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-2-edr-v1.0', '1.0', 'JNO-E/J/SS-WAV-2-EDR-V1.0', 'PDS3-Data-Set', 'JNO-E/J/SS-WAV-2-EDR-V1.0');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-2-edr-v1.0', 'Juno', 'Investigation');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-2-edr-v1.0', 'Waves', 'Instrument');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-2-edr-v1.0', 'Atmospheres', 'Node');


insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-3-cdr-srvfull-v1.0', '1.0', 'JNO-E/J/SS-WAV-3-CDR-SRVFULL-V1.0', 'PDS3-Data-Set', 'JNO-E/J/SS-WAV-3-CDR-SRVFULL-V1.0');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-3-cdr-srvfull-v1.0', 'Juno', 'Investigation');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-3-cdr-srvfull-v1.0', 'Waves', 'Instrument');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-3-cdr-srvfull-v1.0', 'Atmospheres', 'Node');


insert into product (logical_identifier, version_id, title, type, alternate_id) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-3-cdr-bstfull-v1.0', '1.0', 'JNO-E/J/SS-WAV-3-CDR-BSTFULL-V1.0', 'PDS3-Data-Set', 'JNO-E/J/SS-WAV-3-CDR-BSTFULL-V1.0');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-3-cdr-bstfull-v1.0', 'Juno', 'Investigation');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-3-cdr-bstfull-v1.0', 'Waves', 'Instrument');

insert into reference (logical_identifier, reference, type) values ('urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-3-cdr-bstfull-v1.0', 'Atmospheres', 'Node');

/* Juno JunoCam Data Sets */

/* Juno Radiation Data Sets */

/*
** delivery
*/

/* Juno Gravity Deliveries */

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

/* Juno JADE Deliveries */

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

insert into role (electronic_mail_address, reference) values ('sbolton@swri.edu', 'Juno');
insert into role (electronic_mail_address, reference) values ('william-kurth@uiowa.edu', 'Juno');
insert into role (electronic_mail_address, reference) values ('dgell@swri.edu', 'Juno');
insert into role (electronic_mail_address, reference) values ('pmokashi@swri.edu', 'Juno');

/* Juno Gravity Instrument Team */

insert into role (electronic_mail_address, reference) values ('sami.w.asmar@jpl.nasa.gov', 'Gravity');
insert into role (electronic_mail_address, reference) values ('dustin.r.buccino@jpl.nasa.gov', 'Gravity');

/* Juno JIRAM Instrument Team */

insert into role (electronic_mail_address, reference) values ('alberto.adriani@ifsi-roma.inaf.it', 'JIRAM');
insert into role (electronic_mail_address, reference) values ('raffaella.noschese@iaps.inaf.it', 'JIRAM');

/* Juno MWR Instrument Team */

insert into role (electronic_mail_address, reference) values ('michael.a.janssen@jpl.nasa.gov', 'MWR');
insert into role (electronic_mail_address, reference) values ('edwin.sarkissian@jpl.nasa.gov', 'MWR');

/* Juno JADE Instrument Team */

insert into role (electronic_mail_address, reference) values ('philip.valek@swri.org', 'JADE');
insert into role (electronic_mail_address, reference) values ('rob.wilson@lasp.colorado.edu', 'JADE');

/* Juno JEDI Instrument Team */

insert into role (electronic_mail_address, reference) values ('barry.mauk@jhuapl.edu', 'JEDI');
insert into role (electronic_mail_address, reference) values ('chris.paranicas@jhuapl.edu', 'JEDI');

/* Juno FGM Instrument Team */

insert into role (electronic_mail_address, reference) values ('jack.connerney@nasa.gov', 'FGM');
insert into role (electronic_mail_address, reference) values ('patricia.j.lawton.1@gsfc.nasa.gov', 'FGM');

/* Juno Waves Instrument Team */

insert into role (electronic_mail_address, reference) values ('william-kurth@uiowa.edu', 'Waves');
insert into role (electronic_mail_address, reference) values ('patricia.j.lawton.1@gsfc.nasa.gov', 'Waves');

/* Juno JunoCam Instrument Team */

insert into role (electronic_mail_address, reference) values ('cjhansen@psi.edu', 'JunoCam');
insert into role (electronic_mail_address, reference) values ('lipkaman@msss.com', 'JunoCam');

/* Juno Radiation Instrument Team */

insert into role (electronic_mail_address, reference) values ('heidi.n.becker@jpl.nasa.gov', 'Radiation');
insert into role (electronic_mail_address, reference) values ('ingrid.daubar@jpl.nasa.gov', 'Radiation');

/* Atmospheres Node Team */

insert into role (electronic_mail_address, reference) values ('lhuber@nmsu.edu', 'Atmospheres');
insert into role (electronic_mail_address, reference) values ('rbeebe@nmsu.edu', 'Atmospheres');

/* PPI Node Team */

insert into role (electronic_mail_address, reference) values ('jmafi@igpp.ucla.edu', 'PPI');

/* Imaging Node Team */

insert into role (electronic_mail_address, reference) values ('rafael.alanis@jpl.nasa.gov', 'Imaging');

/* Engineering (Admin) Node Team */

insert into role (electronic_mail_address, reference) values ('sean.hardman@jpl.nasa.gov', 'Engineering');

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
** submission
*/


