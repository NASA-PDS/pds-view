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
** This script deletes the content from the Tracking Service schema.
*/

delete from archive_status;
delete from certification_status;
delete from delivery;
delete from doi;
delete from instrument_reference;
delete from investigation_reference;
delete from node_reference;
delete from nssdca_status;
delete from product;
delete from releases;
delete from role;
delete from submission;
delete from submission_status;
delete from user;
