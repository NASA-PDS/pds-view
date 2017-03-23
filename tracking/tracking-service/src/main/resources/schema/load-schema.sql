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
** product
*/

/* JUNO-E-RSS-1-EAGR-V1.0 */

insert into product (logical_identifier, version_id, title, type, alternate_id, investigation_reference, instrument_reference, node_reference) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-e-rss-1-eagr-v1.0', '1.0', 'JUNO-E-RSS-1-EAGR-V1.0', 'Product_Data_Set_PDS3', 'JUNO-E-RSS-1-EAGR-V1.0', 'urn:nasa:pds:context_pds3:investigation:mission.juno', 'urn:nasa:pds:context_pds3:instrument:gravity.jno', 'urn:nasa:pds:context_pds3:node:node.atmos');

/*
** delivery
*/

/* JUNO-E-RSS-1-EAGR-V1.0 */

insert into delivery (logical_identifier, version_id, name, start_date_time, stop_date_time, source, target, due_date) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-e-rss-1-eagr-v1.0', '1.0', 'Delivery 1', '2016-11-15T16:00:00', '2016-11-29T15:00:00', 'Instrument Team', 'JSOC', '2017-05-20');

/*
** role
*/

/* JUNO-E-RSS-1-EAGR-V1.0 */

insert into role (logical_identifier, electronic_mail_address, role) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-e-rss-1-eagr-v1.0', 'sbolton@swri.edu', 'Project Team');
insert into role (logical_identifier, electronic_mail_address, role) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-e-rss-1-eagr-v1.0', 'william-kurth@uiowa.edu', 'Project Team');
insert into role (logical_identifier, electronic_mail_address, role) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-e-rss-1-eagr-v1.0', 'dgell@swri.edu', 'Project Team');
insert into role (logical_identifier, electronic_mail_address, role) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-e-rss-1-eagr-v1.0', 'pmokashi@swri.edu', 'Project Team');

insert into role (logical_identifier, electronic_mail_address, role) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-e-rss-1-eagr-v1.0', 'sami.w.asmar@jpl.nasa.gov', 'Instrument Team');
insert into role (logical_identifier, electronic_mail_address, role) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-e-rss-1-eagr-v1.0', 'dustin.r.buccino@jpl.nasa.gov', 'Instrument Team');

insert into role (logical_identifier, electronic_mail_address, role) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-e-rss-1-eagr-v1.0', 'lhuber@nmsu.edu', 'Node Team');
insert into role (logical_identifier, electronic_mail_address, role) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-e-rss-1-eagr-v1.0', 'rbeebe@nmsu.edu', 'Node Team');

insert into role (logical_identifier, electronic_mail_address, role) values ('urn:nasa:pds:context_pds3:data_set:data_set.juno-e-rss-1-eagr-v1.0', 'sean.hardman@jpl.nasa.gov', 'Admin Team');

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


