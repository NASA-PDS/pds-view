/*
**  Copyright 2018, by the California Institute of Technology.
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
** This script creates the database objects for the Data Search schema.
*/

create table investigation_tools (
  investigation varchar(255) not null,
  title varchar(255) not null,
  description varchar(1024) not null,
  url varchar(255) not null,
  rank int not null
) ENGINE=InnoDB;

create table target_tools (
  target varchar(255) not null,
  title varchar(255) not null,
  description varchar(1024) not null,
  url varchar(255) not null,
  rank int not null
) ENGINE=InnoDB;
