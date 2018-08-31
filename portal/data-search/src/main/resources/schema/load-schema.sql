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
** This script populates the Data Search schema.
*/

/*
** investigation_tools
*/

/* Apollo */

insert into investigation_tools (investigation, title, description, url, rank) values ('Apollo', 'Apollo Analyst''s Notebook at the Geosciences Node', 'Provides access to data from the Apollo program''s six manned missions to the lunar surface.', 'http://an.rsl.wustl.edu/apollo/', 1);


insert into investigation_tools (investigation, title, description, url, rank) values ('Apollo', 'Apollo Archive at the Geosciences Node', 'The Apollo Archive page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering Apollo data.', 'http://pds-geosciences.wustl.edu/missions/apollo/index.htm', 101);

insert into investigation_tools (investigation, title, description, url, rank) values ('Apollo', 'SPICE Data Holdings at the Navigation and Ancillary Information Facility Node', 'Supports Apollo SPICE product browse and download, and subsequent science data analysis.', 'https://naif.jpl.nasa.gov/naif/data_archived.html', 102);

/* Cassini */

insert into investigation_tools (investigation, title, description, url, rank) values ('Cassini', 'OPUS at the Ring-Moon Systems Node', 'Supports Cassini (ISS, UVIS, VIMS), all with enhanced geometric metadata enabling more detailed searches, including surface search constraints for the planet, satellites and rings (e.g., latitudes and longitudes), and expanded sets of viewing and illumination constraints. Browse results and download, including calibrated versions for ISS.', 'https://tools.pds-rings.seti.org/opus#/missionid=Cassini', 1);

insert into investigation_tools (investigation, title, description, url, rank) values ('Cassini', 'PDS Image Atlas at the Cartography and Imaging Sciences Node', 'Supports Cassini ISS NAC and WAC, VIMS-IR and VIS, and RADAR image product search, browse and download.', 'https://pds-imaging.jpl.nasa.gov/search/?fq=ATLAS_MISSION_NAME%3Acassini&q=*%3A*', 2);

insert into investigation_tools (investigation, title, description, url, rank) values ('Cassini', 'Map-A-Planet 2 (MAP2) at the Cartography and Imaging Sciences Node', 'Supports Cassini ISS global image product mosaic projection, clipping, processing, and download.', 'https://astrogeology.usgs.gov/tools/map-a-planet-2', 3);

insert into investigation_tools (investigation, title, description, url, rank) values ('Cassini', 'Planetary Image Locator Tool (PILOT) at the Cartography and Imaging Sciences Node', 'Supports Cassini ISS NAC and WAC, VIMS-IR and VIS map-based image product locator tool using uniform image geometry to search and download.', 'https://pilot.wr.usgs.gov/', 4);


insert into investigation_tools (investigation, title, description, url, rank) values ('Cassini', 'Cassini Archive at the Planetary Atmospheres Node', 'The Cassini Archive page provides details on the investigation phases, instruments and targets associated with the archive as well as additional resources for discovering Cassini data.', 'https://pds-atmospheres.nmsu.edu/data_and_services/atmospheres_data/Cassini/Cassini.html', 101);

insert into investigation_tools (investigation, title, description, url, rank) values ('Cassini', 'Huygens Archive at the Planetary Atmospheres Node', 'The Huygens Archive page provides details on the instruments and targets associated with the archive as well as additional resources for discovering Huygens data.', 'https://pds-atmospheres.nmsu.edu/data_and_services/atmospheres_data/Huygens/Huygens.html', 102);

insert into investigation_tools (investigation, title, description, url, rank) values ('Cassini', 'Cassini-Huygens Archive at the Planetary Plasma Interactions Node', 'The Cassini-Huygens Archive page provides details on the instruments and targets associated with the archive as well as additional resources for discovering Cassini data.', 'https://pds-ppi.igpp.ucla.edu/mission/Cassini-Huygens', 103);

insert into investigation_tools (investigation, title, description, url, rank) values ('Cassini', 'Dust Data at the Small Bodies Node', 'Supports Cassini CDA and HRD product browse and download.', 'https://sbn.psi.edu/pds/archive/cassini.html', 104);

insert into investigation_tools (investigation, title, description, url, rank) values ('Cassini', 'SPICE Data Holdings at the Navigation and Ancillary Information Facility Node', 'Supports Cassini SPICE product browse and download, and subsequent science data analysis.', 'https://naif.jpl.nasa.gov/naif/data_archived.html', 105);

/* Chandrayaan-1 */

insert into investigation_tools (investigation, title, description, url, rank) values ('Chandrayaan-1', 'Lunar Orbital Data Explorer (ODE) at the Geosciences Node', 'Supports search, browse, and download of select PDS data products from ISRO''s Chandrayaan-1 mission, including Moon Mineralogy Mapper (M3) and Mini-RF data.', 'http://ode.rsl.wustl.edu/moon', 1);

insert into investigation_tools (investigation, title, description, url, rank) values ('Chandrayaan-1', 'PDS Image Atlas at the Cartography and Imaging Sciences Node', 'Supports Moon Mineralogy Mapper (M3) image product search, browse and download.', 'https://pds-imaging.jpl.nasa.gov/search/?fq=ATLAS_MISSION_NAME%3Achandrayaan-1&q=*%3A*', 2);

insert into investigation_tools (investigation, title, description, url, rank) values ('Chandrayaan-1', 'Planetary Image Locator Tool (PILOT) at the Cartography and Imaging Sciences Node', 'Supports Moon Mineralogy Mapper (M3) map-based image product locator tool using uniform image geometry to search and download.', 'https://pilot.wr.usgs.gov/', 3);


insert into investigation_tools (investigation, title, description, url, rank) values ('Chandrayaan-1', 'Chandrayaan-1 Archive at the Geosciences Node', 'The Chandrayaan-1 Archive page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering Chandrayaan-1 data.', 'http://pds-geosciences.wustl.edu/missions/chandrayaan1/default.htm', 101);

/* Clementine */

insert into investigation_tools (investigation, title, description, url, rank) values ('Clementine', 'Lunar Orbital Data Explorer (ODE) at the Geosciences Node', 'Supports search, browse, and download of select PDS data products from the Clementine mission, including camera, LIDAR and radio science data.', 'http://ode.rsl.wustl.edu/moon', 1);

insert into investigation_tools (investigation, title, description, url, rank) values ('Clementine', 'PDS Image Atlas at the Cartography and Imaging Sciences Node', 'Supports Clementine HIRES, LWIR, NIR, UVVIS, a-star, and b-star image product search, browse and download.', 'https://pds-imaging.jpl.nasa.gov/search/?fq=ATLAS_MISSION_NAME%3Aclementine&q=*%3A*', 2);

insert into investigation_tools (investigation, title, description, url, rank) values ('Clementine', 'Map-A-Planet 2 (MAP2) at the Cartography and Imaging Sciences Node', 'Supports Clementine UVVIS and NIR global image product mosaic projection, clipping, processing, and download.', 'https://astrogeology.usgs.gov/search/results?q=MAP2&k1=target&v1=Moon', 3);

insert into investigation_tools (investigation, title, description, url, rank) values ('Clementine', 'Planetary Image Locator Tool (PILOT) at the Cartography and Imaging Sciences Node', 'Supports Clementine HIRES, LWIR, NIR, and UVIS map-based image product locator tool using uniform image geometry to search and download.', 'https://pilot.wr.usgs.gov/', 4);


insert into investigation_tools (investigation, title, description, url, rank) values ('Clementine', 'SPICE Data Holdings at the Navigation and Ancillary Information Facility Node', 'Supports Clementine SPICE product browse and download, and subsequent science data analysis.', 'https://naif.jpl.nasa.gov/naif/data_archived.html', 101);

/* Dawn */

insert into investigation_tools (investigation, title, description, url, rank) values ('Dawn', 'Small Bodies Image Browser at the Small Bodies Node', 'Supports Dawn Mission product browse and download for Ceres and Vesta data.', 'https://sbib.psi.edu/', 1);


insert into investigation_tools (investigation, title, description, url, rank) values ('Dawn', 'Dawn Data Archive at the Small Bodies Node', 'Supports Dawn Mission product browse and download for Ceres and Vesta data.', 'https://sbn.psi.edu/pds/resource/dawn/', 101);

insert into investigation_tools (investigation, title, description, url, rank) values ('Dawn', 'SPICE Data Holdings at the Navigation and Ancillary Information Facility Node', 'Supports Dawn SPICE product browse and download, and subsequent science data analysis.', 'https://naif.jpl.nasa.gov/naif/data_archived.html', 102);

/* Deep Impact */

insert into investigation_tools (investigation, title, description, url, rank) values ('Deep Impact', 'Deep Impact Data Archive at the Small Bodies Node', 'Supports Deep Impact Mission product browse and download for 9P/Tempel 1 (Cruise & Encounter) data.', 'https://pds-smallbodies.astro.umd.edu/data_sb/missions/deepimpact/index.shtml', 101);

insert into investigation_tools (investigation, title, description, url, rank) values ('Deep Impact', 'SPICE Data Holdings at the Navigation and Ancillary Information Facility Node', 'Supports Deep Impact SPICE product browse and download, and subsequent science data analysis.', 'https://naif.jpl.nasa.gov/naif/data_archived.html', 102);

/* Deep Space 1 */

insert into investigation_tools (investigation, title, description, url, rank) values ('Deep Space 1', 'Deep Space 1 Data Archive at the Small Bodies Node', 'Supports Deep Space 1 Mission product browse and download for 19P/Borrelly and cometary dust data.', 'https://pds-smallbodies.astro.umd.edu/data_sb/missions/deepspace1/index.shtml', 101);

insert into investigation_tools (investigation, title, description, url, rank) values ('Deep Space 1', 'Deep Space 1 Archive at the Planetary Plasma Interactions Node', 'The Deep Space 1 Archive page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering Deep Space 1 data.', 'https://pds-ppi.igpp.ucla.edu/mission/Deep_Space_1', 102);

insert into investigation_tools (investigation, title, description, url, rank) values ('Deep Space 1', 'SPICE Data Holdings at the Navigation and Ancillary Information Facility Node', 'Supports Deep Space 1 SPICE product browse and download, and subsequent science data analysis.', 'https://naif.jpl.nasa.gov/naif/data_archived.html', 103);

/* EPOXI */

insert into investigation_tools (investigation, title, description, url, rank) values ('EPOXI', 'EPOXI Data Archive at the Small Bodies Node', 'Supports EPOXI Mission product browse and download for 103P/Hartley 2 encounter, C/Garrad (2009 P1) and C/ISON (2012 S1) data.', 'https://pds-smallbodies.astro.umd.edu/data_sb/missions/epoxi/index.shtml', 101);

insert into investigation_tools (investigation, title, description, url, rank) values ('EPOXI', 'SPICE Data Holdings at the Navigation and Ancillary Information Facility Node', 'Supports EPOXI SPICE product browse and download, and subsequent science data analysis.', 'https://naif.jpl.nasa.gov/naif/data_archived.html', 102);

/* Galileo */

insert into investigation_tools (investigation, title, description, url, rank) values ('Galileo', 'PDS Image Atlas at the Cartography and Imaging Sciences Node', 'Supports Galileo NIMS and SSI image product search, browse and download.', 'https://pds-imaging.jpl.nasa.gov/search/?fq=ATLAS_MISSION_NAME%3Agalileo&q=*%3A*', 1);

insert into investigation_tools (investigation, title, description, url, rank) values ('Galileo', 'Map-A-Planet 2 (MAP2) at the Cartography and Imaging Sciences Node', 'Supports Galileo SSI global image product mosaic projection, clipping, processing, and download.', 'https://astrogeology.usgs.gov/tools/map-a-planet-2', 2);

insert into investigation_tools (investigation, title, description, url, rank) values ('Galileo', 'Planetary Image Locator Tool (PILOT) at the Cartography and Imaging Sciences Node', 'Supports Galileo SSI map-based image product locator tool using uniform image geometry to search and download.', 'https://pilot.wr.usgs.gov/', 3);

insert into investigation_tools (investigation, title, description, url, rank) values ('Galileo', 'OPUS at the Ring-Moon Systems Node', 'Supports Galileo for a subset of the SSI observations. Browse results and download.', 'https://tools.pds-rings.seti.org/opus#/missionid=Galileo', 4);


insert into investigation_tools (investigation, title, description, url, rank) values ('Galileo', 'Galileo Archive at the Planetary Plasma Interactions Node', 'The Galileo Archive page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering Galileo data.', 'https://pds-ppi.igpp.ucla.edu/mission/Galileo', 101);

insert into investigation_tools (investigation, title, description, url, rank) values ('Galileo', 'Galileo Orbiter Data Archive at the Small Bodies Node', 'Supports Galileo product browse and download for Ida, Gaspra, small satellites and dust data.', 'https://sbn.psi.edu/pds/archive/galileo.html', 102);

insert into investigation_tools (investigation, title, description, url, rank) values ('Galileo', 'SPICE Data Holdings at the Navigation and Ancillary Information Facility Node', 'Supports Galileo SPICE product browse and download, and subsequent science data analysis.', 'https://naif.jpl.nasa.gov/naif/data_archived.html', 103);

/* Giotto */

insert into investigation_tools (investigation, title, description, url, rank) values ('Giotto', 'Giotto Mission Data Archive at the Small Bodies Node', 'Supports Giotto Mission product browse and download for 1P/Halley and interplanetary dust data.', 'https://pds-smallbodies.astro.umd.edu/data_sb/missions/giotto/index.shtml', 101);

insert into investigation_tools (investigation, title, description, url, rank) values ('Giotto', 'Giotto Extended Mission Data Archive at the Small Bodies Node', 'Supports Giotto Extended Mission product browse and download for 26P/Grigg-Skjellerup data.', 'https://pds-smallbodies.astro.umd.edu/data_sb/missions/gem/index.shtml', 102);

insert into investigation_tools (investigation, title, description, url, rank) values ('Giotto', 'Giotto Archive at the Planetary Plasma Interactions Node', 'The Giotto Archive page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering Giotto data.', 'https://pds-ppi.igpp.ucla.edu/mission/GIOTTO', 103);

insert into investigation_tools (investigation, title, description, url, rank) values ('Giotto', 'SPICE Data Holdings at the Navigation and Ancillary Information Facility Node', 'Supports Giotto SPICE product browse and download, and subsequent science data analysis.', 'https://naif.jpl.nasa.gov/naif/data_archived.html', 104);

/* GRAIL */

insert into investigation_tools (investigation, title, description, url, rank) values ('GRAIL', 'Lunar Orbital Data Explorer (ODE) at the Geosciences Node', 'Supports search, browse, and download of select PDS data products from the Gravity Recovery And Interior Laboratory (GRAIL) mission, including Lunar Gravity Ranging System (LGRS) data.', 'http://ode.rsl.wustl.edu/moon', 1);


insert into investigation_tools (investigation, title, description, url, rank) values ('GRAIL', 'Gravity Recovery And Interior Laboratory (GRAIL) Archive at the Geosciences Node', 'The GRAIL Archive page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering GRAIL data.', 'http://pds-geosciences.wustl.edu/missions/grail/default.htm', 101);

insert into investigation_tools (investigation, title, description, url, rank) values ('GRAIL', 'SPICE Data Holdings at the Navigation and Ancillary Information Facility Node', 'Supports GRAIL SPICE product browse and download, and subsequent science data analysis.', 'https://naif.jpl.nasa.gov/naif/data_archived.html', 102);

/* Hayabusa */

insert into investigation_tools (investigation, title, description, url, rank) values ('Hayabusa', 'Hayabusa Data Archive at the Small Bodies Node', 'Supports Hayabusa Mission product browse and download for Itokawa data.', 'https://pdssbn.astro.umd.edu/data_sb/missions/hayabusa/index.shtml', 101);

insert into investigation_tools (investigation, title, description, url, rank) values ('Hayabusa', 'SPICE Data Holdings at the Navigation and Ancillary Information Facility Node', 'Supports Hayabusa SPICE product browse and download, and subsequent science data analysis.', 'https://naif.jpl.nasa.gov/naif/data_archived.html', 102);

/* Hubble Space Telescope */

insert into investigation_tools (investigation, title, description, url, rank) values ('Hubble Space Telescope', 'OPUS at the Ring-Moon Systems Node', 'Supports solar system observations by the Hubble Space Telescope (ACS, WFC3, WFPC2). Browse results, and links to STScI for downloads.', 'https://tools.pds-rings.seti.org/opus#/missionid=Hubble', 1);

/* IRAS */

insert into investigation_tools (investigation, title, description, url, rank) values ('IRAS', 'Infrared Astronomical Satellite (IRAS) Data Archive at the Small Bodies Node', 'Supports IRAS Mission product browse and download for Asteroids and Interplanetary Dust data.', 'https://sbn.psi.edu/pds/archive/iras.html', 101);

/* Juno */

insert into investigation_tools (investigation, title, description, url, rank) values ('Juno', 'Juno Archive at the Atmospheres Node', 'The Juno Archive page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering Juno data.', 'http://pds-atmospheres.nmsu.edu/data_and_services/atmospheres_data/JUNO/juno.html', 101);

insert into investigation_tools (investigation, title, description, url, rank) values ('Juno', 'Juno Archive at the Planetary Plasma Interactions Node', 'The Juno Archive page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering Juno data.', 'https://pds-ppi.igpp.ucla.edu/mission/JUNO', 102);

insert into investigation_tools (investigation, title, description, url, rank) values ('Juno', 'SPICE Data Holdings at the Navigation and Ancillary Information Facility Node', 'Supports Juno SPICE product browse and download, and subsequent science data analysis.', 'https://naif.jpl.nasa.gov/naif/data_archived.html', 103);

/* LADEE */

insert into investigation_tools (investigation, title, description, url, rank) values ('LADEE', 'Lunar Atmospheres Data Archive at the Atmospheres Node', 'Supports LADEE Mission product browse and download for NMS and UVS data.', 'https://pds-atmospheres.nmsu.edu/data_and_services/atmospheres_data/LADEE/lunar.html', 101);

insert into investigation_tools (investigation, title, description, url, rank) values ('LADEE', 'LADEE Lunar Dust Experiment (LDEX) Data Archive at the Small Bodies Node', 'Supports LADEE Mission product browse and download for LDEX data.', 'https://sbn.psi.edu/pds/resource/ldex.html', 102);

insert into investigation_tools (investigation, title, description, url, rank) values ('LADEE', 'Lunar Atmosphere and Dust Environment Explorer (LADEE) Archive at the Atmospheres Node', 'The LADEE Archive page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering LADEE data.', 'http://pds-atmospheres.nmsu.edu/data_and_services/atmospheres_data/LADEE/mainr.html', 103);

insert into investigation_tools (investigation, title, description, url, rank) values ('LADEE', 'Lunar Atmosphere and Dust Environment Explorer (LADEE) Archive at the Planetary Plasma Interactions Node', 'The LADEE Archive page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering LADEE data.', 'https://pds-ppi.igpp.ucla.edu/mission/LADEE', 104);

insert into investigation_tools (investigation, title, description, url, rank) values ('LADEE', 'SPICE Data Holdings at the Navigation and Ancillary Information Facility Node', 'Supports LADEE SPICE product browse and download, and subsequent science data analysis.', 'https://naif.jpl.nasa.gov/naif/data_archived.html', 105);

/* LCROSS */

insert into investigation_tools (investigation, title, description, url, rank) values ('LCROSS', 'LCROSS Analyst''s Notebook at the Geosciences Node', 'Provides access to data from the Lunar Crater Observation And Sensing Satellite (LCROSS) mission, including camera, spectrometer, and photometer data.', 'http://an.rsl.wustl.edu/lcross/lcrossbrowser/', 1);

insert into investigation_tools (investigation, title, description, url, rank) values ('LCROSS', 'PDS Image Atlas at the Cartography and Imaging Sciences Node', 'Supports Lunar Crater Observation And Sensing Satellite (LCROSS) NIR, MIR, NSP, VIS and VSP image product search, browse and download.', 'https://pds-imaging.jpl.nasa.gov/search/?fq=ATLAS_MISSION_NAME%3Alcross&q=*%3A*', 2);


insert into investigation_tools (investigation, title, description, url, rank) values ('LCROSS', 'Lunar Crater Observation And Sensing Satellite (LCROSS) Archive at the Geosciences Node', 'The LCROSS Archive page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering LCROSS data.', 'http://pds-geosciences.wustl.edu/missions/lcross/default.htm', 101);

/* Lunar Orbiter */

insert into investigation_tools (investigation, title, description, url, rank) values ('Lunar Orbiter', 'Lunar Orbital Data Explorer (ODE) at the Geosciences Node', 'Supports search, browse, and download of select PDS data products from the Lunar Orbiter program, including Lunar Orbiter Camera data.', 'http://ode.rsl.wustl.edu/moon', 1);


insert into investigation_tools (investigation, title, description, url, rank) values ('Lunar Orbiter', 'SPICE Data Holdings at the Navigation and Ancillary Information Facility Node', 'Supports Lunar Orbiters 1-5 SPICE product browse and download, and subsequent science data analysis.', 'https://naif.jpl.nasa.gov/naif/data_archived.html', 101);

/* Lunar Prospector */

insert into investigation_tools (investigation, title, description, url, rank) values ('Lunar Prospector', 'Lunar Orbital Data Explorer (ODE) at the Geosciences Node', 'Supports search, browse, and download of select PDS data products from the Lunar Prospector mission. This includes Gamma Ray, Neutron, and Alpha Particle Spectrometer, Magnetometer, Electron Reflectometer, and Doppler Gravity Experiment data.', 'http://ode.rsl.wustl.edu/moon', 1);


insert into investigation_tools (investigation, title, description, url, rank) values ('Lunar Prospector', 'Lunar Prospector Archive at the Geosciences Node', 'The Lunar Prospector Archive page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering Lunar Prospector data.', 'http://pds-geosciences.wustl.edu/missions/lunarp/index.htm', 101);

insert into investigation_tools (investigation, title, description, url, rank) values ('Lunar Prospector', 'Lunar Prospector Archive at the Planetary Plasma Interactions Node', 'The Lunar Prospector Archive page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering Lunar Prospector data.', 'https://pds-ppi.igpp.ucla.edu/mission/Lunar_Prospector', 102);

insert into investigation_tools (investigation, title, description, url, rank) values ('Lunar Prospector', 'SPICE Data Holdings at the Navigation and Ancillary Information Facility Node', 'Supports Lunar Prospector SPICE product browse and download, and subsequent science data analysis.', 'https://naif.jpl.nasa.gov/naif/data_archived.html', 103);

/* Lunar Reconnaissance Orbiter */

insert into investigation_tools (investigation, title, description, url, rank) values ('Lunar Reconnaissance Orbiter', 'Lunar Orbital Data Explorer (ODE) at the Geosciences Node', 'Supports search, browse, and download of select PDS data products from the Lunar Reconnaissance Orbiter (LRO) mission, including Diviner, LAMP, Mini-RF, LEND, LOLA, and LROC data.', 'http://ode.rsl.wustl.edu/moon', 1);

insert into investigation_tools (investigation, title, description, url, rank) values ('Lunar Reconnaissance Orbiter', 'PDS Image Atlas at the Cartography and Imaging Sciences Node', 'Supports Lunar Reconnaissance Orbiter (LRO) LAMP and LROC image product search, browse and download.', 'https://pds-imaging.jpl.nasa.gov/search/?fq=ATLAS_MISSION_NAME%3A%22lunar%20reconnaissance%20orbiter%22&q=*%3A*', 2);

insert into investigation_tools (investigation, title, description, url, rank) values ('Lunar Reconnaissance Orbiter', 'Map-A-Planet 2 (MAP2) at the Cartography and Imaging Sciences Node', 'Supports Lunar Reconnaissance Orbiter (LRO) LOLA and LROC global image product mosaic projection, clipping, processing, and download.', 'https://astrogeology.usgs.gov/search/results?q=MAP2&k1=target&v1=Moon', 3);

insert into investigation_tools (investigation, title, description, url, rank) values ('Lunar Reconnaissance Orbiter', 'Planetary Image Locator Tool (PILOT) at the Cartography and Imaging Sciences Node', 'Supports Lunar Reconnaissance Orbiter (LRO) LROC map-based image product locator tool using uniform image geometry to search and download.', 'https://pilot.wr.usgs.gov/', 4);


insert into investigation_tools (investigation, title, description, url, rank) values ('Lunar Reconnaissance Orbiter', 'Lunar Reconnaissance Orbiter (LRO) Archive at the Geosciences Node', 'The LRO Archive page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering LRO data.', 'http://pds-geosciences.wustl.edu/missions/lro/default.htm', 101);

insert into investigation_tools (investigation, title, description, url, rank) values ('Lunar Reconnaissance Orbiter', 'Lunar Reconnaissance Orbiter (LRO) Archive at the Planetary Plasma Interactions Node', 'The LRO Archive page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering LRO data.', 'https://pds-ppi.igpp.ucla.edu/mission/Lunar_Reconnaissance_Orbiter', 102);

insert into investigation_tools (investigation, title, description, url, rank) values ('Lunar Reconnaissance Orbiter', 'SPICE Data Holdings at the Navigation and Ancillary Information Facility Node', 'Supports Lunar Reconnaissance Orbiter SPICE product browse and download, and subsequent science data analysis.', 'https://naif.jpl.nasa.gov/naif/data_archived.html', 103);

/* Magellan */

insert into investigation_tools (investigation, title, description, url, rank) values ('Magellan', 'PDS Image Atlas at the Cartography and Imaging Sciences Node', 'Supports Magellan SAR image product search, browse and download.', 'https://pds-imaging.jpl.nasa.gov/search/?fq=ATLAS_MISSION_NAME%3Amagellan&q=*%3A*', 1);

insert into investigation_tools (investigation, title, description, url, rank) values ('Magellan', 'Venus Orbital Data Explorer (ODE) at the Geosciences Node', 'Supports search, browse, and download of select PDS data products from the Magellan mission, including radar system and radio science subsystem data.', 'http://ode.rsl.wustl.edu/venus', 2);

insert into investigation_tools (investigation, title, description, url, rank) values ('Magellan', 'Map-A-Planet 2 (MAP2) at the Cartography and Imaging Sciences Node', 'Supports Magellan SAR global image product mosaic projection, clipping, processing, and download.', 'https://astrogeology.usgs.gov/search/results?q=MAP2&k1=target&v1=Venus', 3);


insert into investigation_tools (investigation, title, description, url, rank) values ('Magellan', 'Magellan Archive at the Geosciences Node', 'The Magellan Archive page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering Magellan data.', 'http://pds-geosciences.wustl.edu/missions/magellan/index.htm', 101);

insert into investigation_tools (investigation, title, description, url, rank) values ('Magellan', 'Magellan Archive at the Planetary Plasma Interactions Node', 'The Magellan Archive page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering Magellan data.', 'https://pds-ppi.igpp.ucla.edu/mission/Magellan', 102);

insert into investigation_tools (investigation, title, description, url, rank) values ('Magellan', 'SPICE Data Holdings at the Navigation and Ancillary Information Facility Node', 'Supports Magellan SPICE product browse and download, and subsequent science data analysis.', 'https://naif.jpl.nasa.gov/naif/data_archived.html', 103);

/* Mariner */

insert into investigation_tools (investigation, title, description, url, rank) values ('Mariner', 'Mariner Archive at the Planetary Plasma Interactions Node', 'The Mariner Archive page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering Mariner data.', 'https://pds-ppi.igpp.ucla.edu/mission/Mariner_10', 101);

insert into investigation_tools (investigation, title, description, url, rank) values ('Mariner', 'SPICE Data Holdings at the Navigation and Ancillary Information Facility Node', 'Supports Mariner SPICE product browse and download, and subsequent science data analysis.', 'https://naif.jpl.nasa.gov/naif/data_archived.html', 105);

/* Mars Exploration Rover */

insert into investigation_tools (investigation, title, description, url, rank) values ('Mars Exploration Rover', 'MER Analyst''s Notebook at the Geosciences Node', 'Provides access to the Mars Exploration Rover (MER) Spirit and Opportunity data archives by integrating sequence information, science data, and documentation into standard web-accessible pages to facilitate mission "replay."', 'http://an.rsl.wustl.edu/mer/', 1);

insert into investigation_tools (investigation, title, description, url, rank) values ('Mars Exploration Rover', 'PDS Image Atlas at the Cartography and Imaging Sciences Node', 'Supports Mars Exploration Rover (MER) HAZCAM, MI, NAVCAM, and PANCAM image product search, browse and download.', 'https://pds-imaging.jpl.nasa.gov/search/?fq=ATLAS_MISSION_NAME%3A%22mars%20exploration%20rover%22&q=*%3A*', 2);


insert into investigation_tools (investigation, title, description, url, rank) values ('Mars Exploration Rover', 'Mars Exploration Rover (MER) Archive at the Geosciences Node', 'The MER Archive page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering MER data.', 'http://pds-geosciences.wustl.edu/missions/mer/index.htm', 101);

insert into investigation_tools (investigation, title, description, url, rank) values ('Mars Exploration Rover', 'SPICE Data Holdings at the Navigation and Ancillary Information Facility Node', 'Supports Mars Exploration Rovers (Opportunity and Spirit) SPICE product browse and download, and subsequent science data analysis.', 'https://naif.jpl.nasa.gov/naif/data_archived.html', 102);

/* Mars Express */

insert into investigation_tools (investigation, title, description, url, rank) values ('Mars Express', 'Mars Orbital Data Explorer (ODE) at the Geosciences Node', 'Supports search, browse, and download of select PDS data products from the Mars Express (MEX) mission, including HRSC, MARSIS, PFS, and OMEGA data.', 'http://ode.rsl.wustl.edu/mars/', 1);

insert into investigation_tools (investigation, title, description, url, rank) values ('Mars Express', 'Map-A-Planet 2 (MAP2) at the Cartography and Imaging Sciences Node', 'Supports Mars Express (MEX) HRSC global image product mosaic projection, clipping, processing, and download.', 'https://astrogeology.usgs.gov/search/results?q=MAP2&k1=target&v1=Mars', 2);

insert into investigation_tools (investigation, title, description, url, rank) values ('Mars Express', 'Planetary Image Locator Tool (PILOT) at the Cartography and Imaging Sciences Node', 'Supports Mars Express (MEX) HRSC map-based image product locator tool using uniform image geometry to search and download.', 'https://pilot.wr.usgs.gov/', 3);


insert into investigation_tools (investigation, title, description, url, rank) values ('Mars Express', 'Mars Express (MEX) Archive at the Geosciences Node', 'The MEX Archive page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering MEX data.', 'http://pds-geosciences.wustl.edu/missions/mars_express/default.htm', 101);

insert into investigation_tools (investigation, title, description, url, rank) values ('Mars Express', 'Mars Express (MEX) Archive at the Planetary Plasma Interactions Node', 'The MEX Archive page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering MEX data.', 'https://pds-ppi.igpp.ucla.edu/mission/Mars_Express', 102);

insert into investigation_tools (investigation, title, description, url, rank) values ('Mars Express', 'SPICE Data Holdings at the Navigation and Ancillary Information Facility Node', 'Supports Mars Express SPICE product browse and download, and subsequent science data analysis.', 'https://naif.jpl.nasa.gov/naif/data_archived.html', 103);

/* Mars Global Surveyor */

insert into investigation_tools (investigation, title, description, url, rank) values ('Mars Global Surveyor', 'Mars Orbital Data Explorer (ODE) at the Geosciences Node', 'Supports search, browse, and download of select PDS data products from the Mars Global Surveyor (MGS) mission, including TES, MOC and MOLA data.', 'http://ode.rsl.wustl.edu/mars/', 1);

insert into investigation_tools (investigation, title, description, url, rank) values ('Mars Global Surveyor', 'PDS Image Atlas at the Cartography and Imaging Sciences Node', 'Supports Mars Global Surveyor (MGS) MOC-NA and WA image product search, browse and download.', 'https://pds-imaging.jpl.nasa.gov/search/?fq=ATLAS_MISSION_NAME%3A%22mars%20global%20surveyor%22&q=*%3A*', 2);

insert into investigation_tools (investigation, title, description, url, rank) values ('Mars Global Surveyor', 'Map-A-Planet 2 (MAP2) at the Cartography and Imaging Sciences Node', 'Supports Mars Global Surveyor (MGS) MOC-WA, MOLA TES albedo and thermal global image product mosaic projection, clipping, processing, and download.', 'https://astrogeology.usgs.gov/search/results?q=MAP2&k1=target&v1=Mars', 3);

insert into investigation_tools (investigation, title, description, url, rank) values ('Mars Global Surveyor', 'Planetary Image Locator Tool (PILOT) at the Cartography and Imaging Sciences Node', 'Supports Mars Global Surveyor (MGS) MOC-NA and WA map-based image product locator tool using uniform image geometry to search and download.', 'https://pilot.wr.usgs.gov/', 4);

insert into investigation_tools (investigation, title, description, url, rank) values ('Mars Global Surveyor', 'TES Search at the Atmospheres Node', 'Supports Mars Global Surveyor (MGS) TES product search and download.', 'https://atmos.nmsu.edu/tes.html', 5);


insert into investigation_tools (investigation, title, description, url, rank) values ('Mars Global Surveyor', 'Mars Global Surveyor (MGS) Archive at the Geosciences Node', 'The MGS Archive page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering MGS data.', 'http://pds-geosciences.wustl.edu/missions/mgs/index.htm', 101);

insert into investigation_tools (investigation, title, description, url, rank) values ('Mars Global Surveyor', 'Mars Global Surveyor (MGS) Archive at the Planetary Plasma Interactions Node', 'The MGS Archive page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering MGS data.', 'https://pds-ppi.igpp.ucla.edu/mission/Mars_Global_Surveyor', 102);

insert into investigation_tools (investigation, title, description, url, rank) values ('Mars Global Surveyor', 'SPICE Data Holdings at the Navigation and Ancillary Information Facility Node', 'Supports Mars Global Surveyor SPICE product browse and download, and subsequent science data analysis.', 'https://naif.jpl.nasa.gov/naif/data_archived.html', 103);

/* Mars Odyssey */

insert into investigation_tools (investigation, title, description, url, rank) values ('Mars Odyssey', 'Mars Orbital Data Explorer (ODE) at the Geosciences Node', 'Supports search, browse, and download of select PDS data products from the Mars Odyssey mission, including GRS and THEMIS data.', 'http://ode.rsl.wustl.edu/mars/', 1);

insert into investigation_tools (investigation, title, description, url, rank) values ('Mars Odyssey', 'PDS Image Atlas at the Cartography and Imaging Sciences Node', 'Supports Mars Odyssey THEMIS-IR and THEMIS-VIS image product search, browse and download.', 'https://pds-imaging.jpl.nasa.gov/search/?fq=ATLAS_MISSION_NAME%3A%222001%20mars%20odyssey%22&q=*%3A*', 2);

insert into investigation_tools (investigation, title, description, url, rank) values ('Mars Odyssey', 'Map-A-Planet 2 (MAP2) at the Cartography and Imaging Sciences Node', 'Supports Mars Odyssey THEMIS global image product mosaic projection, clipping, processing, and download.', 'https://astrogeology.usgs.gov/search/results?q=MAP2&k1=target&v1=Mars', 3);

insert into investigation_tools (investigation, title, description, url, rank) values ('Mars Odyssey', 'Planetary Image Locator Tool (PILOT) at the Cartography and Imaging Sciences Node', 'Supports Mars Odyssey THEMIS-IR and THEMIS VIS map-based image product locator tool using uniform image geometry to search and download.', 'https://pilot.wr.usgs.gov/', 4);

insert into investigation_tools (investigation, title, description, url, rank) values ('Mars Odyssey', 'SPHERE at the Atmospheres Node', 'Supports Mars Odyssey Accelerometer product search and download.', 'http://atmos.nmsu.edu:8080/pds/', 5);


insert into investigation_tools (investigation, title, description, url, rank) values ('Mars Odyssey', 'Mars Odyssey Archive at the Geosciences Node', 'The 2001 Mars Odyssey Archive page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering 2001 Mars Odyssey data.', 'http://pds-geosciences.wustl.edu/missions/odyssey/index.htm', 101);

insert into investigation_tools (investigation, title, description, url, rank) values ('Mars Odyssey', 'Mars Odyssey Archive at the Planetary Plasma Interactions Node', 'The 2001 Mars Odyssey Archive page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering 2001 Mars Odyssey data.', 'https://pds-ppi.igpp.ucla.edu/mission/2001_MARS_ODYSSEY', 102);

insert into investigation_tools (investigation, title, description, url, rank) values ('Mars Odyssey', 'SPICE Data Holdings at the Navigation and Ancillary Information Facility Node', 'Supports Mars Odyssey SPICE product browse and download, and subsequent science data analysis.', 'https://naif.jpl.nasa.gov/naif/data_archived.html', 103);

/* Mars Pathfinder */

insert into investigation_tools (investigation, title, description, url, rank) values ('Mars Pathfinder', 'PDS Image Atlas at the Cartography and Imaging Sciences Node', 'Supports Mars Pathfinder (MPF) APXS, IMP and RVR image product search, browse and download.', 'https://pds-imaging.jpl.nasa.gov/search/?fq=ATLAS_MISSION_NAME%3A%22mars%20pathfinder%22&q=*%3A*', 1);


insert into investigation_tools (investigation, title, description, url, rank) values ('Mars Pathfinder', 'Mars Pathfinder (MPF) Archive at the Geosciences Node', 'The MGS Archive page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering MGS data.', 'http://pds-geosciences.wustl.edu/missions/mpf/index.htm', 101);

/* Mars Reconnaissance Orbiter */

insert into investigation_tools (investigation, title, description, url, rank) values ('Mars Reconnaissance Orbiter', 'Mars Orbital Data Explorer (ODE) at the Geosciences Node', 'Supports search, browse, and download of select PDS data products from the Mars Reconnaissance Orbiter (MRO) mission, including CRISM, HiRISE, CTX, SHARAD, MCS, and Gravity/Radio Science data.', 'http://ode.rsl.wustl.edu/mars/', 1);

insert into investigation_tools (investigation, title, description, url, rank) values ('Mars Reconnaissance Orbiter', 'PDS Image Atlas at the Cartography and Imaging Sciences Node', 'Supports Mars Reconnaissance Orbiter (MRO) CTX, HIRISE, and MARCI image product search, browse and download.', 'https://pds-imaging.jpl.nasa.gov/search/?fq=ATLAS_MISSION_NAME%3A%22mars%20reconnaissance%20orbiter%22&q=*%3A*', 2);

insert into investigation_tools (investigation, title, description, url, rank) values ('Mars Reconnaissance Orbiter', 'Map-A-Planet 2 (MAP2) at the Cartography and Imaging Sciences Node', 'Supports Mars Reconnaissance Orbiter (MRO) HRSC and MOLA global image product mosaic projection, clipping, processing, and download.', 'https://astrogeology.usgs.gov/search/results?q=MAP2&k1=target&v1=Mars', 3);

insert into investigation_tools (investigation, title, description, url, rank) values ('Mars Reconnaissance Orbiter', 'Planetary Image Locator Tool (PILOT) at the Cartography and Imaging Sciences Node', 'Supports Mars Reconnaissance Orbiter (MRO) CTX, HIRISE, HIRISE OBS, and MARCI map-based image product locator tool using uniform image geometry to search and download.', 'https://pilot.wr.usgs.gov/', 4);


insert into investigation_tools (investigation, title, description, url, rank) values ('Mars Reconnaissance Orbiter', 'Mars Reconnaissance Orbiter (MRO) Archive at the Geosciences Node', 'The MRO Archive page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering MRO data.', 'http://pds-geosciences.wustl.edu/missions/mro/default.htm', 101);

insert into investigation_tools (investigation, title, description, url, rank) values ('Mars Reconnaissance Orbiter', 'SPICE Data Holdings at the Navigation and Ancillary Information Facility Node', 'Supports Mars Reconnaissance Orbiter SPICE product browse and download, and subsequent science data analysis.', 'https://naif.jpl.nasa.gov/naif/data_archived.html', 102);

/* Mars Science Laboratory */

insert into investigation_tools (investigation, title, description, url, rank) values ('Mars Science Laboratory', 'MSL Analyst''s Notebook at the Geosciences Node', 'Provides access to the Mars Science Laboratory (MSL) Curiosity data archives by integrating sequence information, science data, and documentation into standard web-accessible pages to facilitate mission "replay."', 'http://an.rsl.wustl.edu/msl', 1);

insert into investigation_tools (investigation, title, description, url, rank) values ('Mars Science Laboratory', 'PDS Image Atlas at the Cartography and Imaging Sciences Node', 'Supports Mars Science Laboratory (MSL) HAZCAM, MAHLI, MARDI, MASTCAM, and NAVCAM image product search, browse and download.', 'https://pds-imaging.jpl.nasa.gov/search/?fq=ATLAS_MISSION_NAME%3A%22mars%20science%20laboratory%22&q=*%3A*', 2);


insert into investigation_tools (investigation, title, description, url, rank) values ('Mars Science Laboratory', 'Mars Science Laboratory (MSL) Archive at the Geosciences Node', 'The MSL Archive page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering MSL data.', 'http://pds-geosciences.wustl.edu/missions/msl/index.htm', 101);

insert into investigation_tools (investigation, title, description, url, rank) values ('Mars Science Laboratory', 'Mars Science Laboratory (MSL) Archive at the Planetary Plasma Interactions Node', 'The MSL Archive page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering MSL data.', 'https://pds-ppi.igpp.ucla.edu/mission/Mars_Science_Laboratory', 102);

insert into investigation_tools (investigation, title, description, url, rank) values ('Mars Science Laboratory', 'SPICE Data Holdings at the Navigation and Ancillary Information Facility Node', 'Supports Mars Science Laboratory SPICE product browse and download, and subsequent science data analysis.', 'https://naif.jpl.nasa.gov/naif/data_archived.html', 103);

/* MAVEN */

insert into investigation_tools (investigation, title, description, url, rank) values ('MAVEN', 'Mars Atmosphere & Volatile Evolution Mission (MAVEN)  Archive at the Atmospheres Node', 'The MAVEN Archive page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering MAVEN data.', 'http://pds-atmospheres.nmsu.edu/data_and_services/atmospheres_data/MAVEN/maven_main.html', 101);

insert into investigation_tools (investigation, title, description, url, rank) values ('MAVEN', 'Mars Atmosphere & Volatile Evolution Mission (MAVEN)  Archive at the Planetary Plasma Interactions Node', 'The MAVEN Archive page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering MAVEN data.', 'https://pds-ppi.igpp.ucla.edu/mission/MAVEN', 102);

insert into investigation_tools (investigation, title, description, url, rank) values ('MAVEN', 'SPICE Data Holdings at the Navigation and Ancillary Information Facility Node', 'Supports MAVEN SPICE product browse and download, and subsequent science data analysis.', 'https://naif.jpl.nasa.gov/naif/data_archived.html', 103);

/* MESSENGER */

insert into investigation_tools (investigation, title, description, url, rank) values ('MESSENGER', 'Mercury Orbital Data Explorer (ODE) at the Geosciences Node', 'Supports search, browse, and download of select PDS data products from the MESSENGER mission, including GRS, NS, XRS, MASCS, MDIS, MLA, and radio science data.', 'http://ode.rsl.wustl.edu/mercury/', 1);

insert into investigation_tools (investigation, title, description, url, rank) values ('MESSENGER', 'PDS Image Atlas at the Cartography and Imaging Sciences Node', 'Supports MESSENGER MDIS-WAC and MDIS-NAC image product search, browse and download.', 'https://pds-imaging.jpl.nasa.gov/search/?fq=ATLAS_MISSION_NAME%3Amessenger&q=*%3A*', 2);

insert into investigation_tools (investigation, title, description, url, rank) values ('MESSENGER', 'Map-A-Planet 2 (MAP2) at the Cartography and Imaging Sciences Node', 'Supports MESSENGER MDIS global image product mosaic projection, clipping, processing, and download.', 'https://astrogeology.usgs.gov/search/results?q=MAP2&k1=target&v1=Mercury', 3);

insert into investigation_tools (investigation, title, description, url, rank) values ('MESSENGER', 'Planetary Image Locator Tool (PILOT) at the Cartography and Imaging Sciences Node', 'Supports MESSENGER MDIS-NAC and MDIS-WAC map-based image product locator tool using uniform image geometry to search and download.', 'https://pilot.wr.usgs.gov/', 4);


insert into investigation_tools (investigation, title, description, url, rank) values ('MESSENGER', 'MErcury Surface, Space ENvironment, GEochemistry, and Ranging (MESSENGER) Archive at the Geosciences Node', 'The MESSENGER Archive page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering MESSENGER data.', 'http://pds-geosciences.wustl.edu/missions/messenger/index.htm', 101);

insert into investigation_tools (investigation, title, description, url, rank) values ('MESSENGER', 'MErcury Surface, Space ENvironment, GEochemistry, and Ranging (MESSENGER) Archive at the Planetary Plasma Interactions Node', 'The MESSENGER Archive page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering MESSENGER data.', 'https://pds-ppi.igpp.ucla.edu/mission/MESSENGER', 102);

insert into investigation_tools (investigation, title, description, url, rank) values ('MESSENGER', 'SPICE Data Holdings at the Navigation and Ancillary Information Facility Node', 'Supports MESSENGER SPICE product browse and download, and subsequent science data analysis.', 'https://naif.jpl.nasa.gov/naif/data_archived.html', 105);

/* Mid-Course Space Experiment */

insert into investigation_tools (investigation, title, description, url, rank) values ('Mid-Course Space Experiment', 'Mid-Course Space Experiment (MSX) Data Archive at the Small Bodies Node', 'Supports MSX Mission product browse and download for Asteroids, Comets and Interplanetary Dust data.', 'https://sbn.psi.edu/pds/archive/msx.html', 101);

/* Near Earth Asteroid Rendezvous */

insert into investigation_tools (investigation, title, description, url, rank) values ('Near Earth Asteroid Rendezvous', 'Small Bodies Image Browser at the Small Bodies Node', 'Supports NEAR Shoemaker Mission product browse and download for 433 Eros data.', 'https://sbib.psi.edu/', 1);


insert into investigation_tools (investigation, title, description, url, rank) values ('Near Earth Asteroid Rendezvous', 'Near Earth Asteroid Rendezvous (NEAR) Data Archive at the Small Bodies Node', 'Supports NEAR Mission product browse and download for Eros, Mathilde and C/Hyakutake data.', 'https://sbn.psi.edu/pds/resource/near/', 101);

insert into investigation_tools (investigation, title, description, url, rank) values ('Near Earth Asteroid Rendezvous', 'Near Earth Asteroid Rendezvous (NEAR) Archive at the Planetary Plasma Interactions Node', 'The NEAR Archive page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering NEAR data.', 'https://pds-ppi.igpp.ucla.edu/mission/Near_Earth_Asteroid_Rendezvous', 102);

insert into investigation_tools (investigation, title, description, url, rank) values ('Near Earth Asteroid Rendezvous', 'SPICE Data Holdings at the Navigation and Ancillary Information Facility Node', 'Supports NEAR SPICE product browse and download, and subsequent science data analysis.', 'https://naif.jpl.nasa.gov/naif/data_archived.html', 103);

/* New Horizons */

insert into investigation_tools (investigation, title, description, url, rank) values ('New Horizons', 'PDS Image Atlas at the Cartography and Imaging Sciences Node', 'Supports New Horizons LORRI image product search, browse and download.', 'https://pds-imaging.jpl.nasa.gov/search/?fq=ATLAS_MISSION_NAME%3A%22new%20horizons%22&q=*%3A*', 1);

insert into investigation_tools (investigation, title, description, url, rank) values ('New Horizons', 'Map-A-Planet 2 (MAP2) at the Cartography and Imaging Sciences Node', 'Supports New Horizons LORRI and MVIC global image product mosaic projection, clipping, processing, and download.', 'https://astrogeology.usgs.gov/tools/map-a-planet-2', 2);

insert into investigation_tools (investigation, title, description, url, rank) values ('New Horizons', 'OPUS at the Ring-Moon Systems Node', 'Supports New Horizons LORRI with enhanced geometric metadata enabling more detailed searches, including surface search constraints for the planet and satellites (e.g., latitudes and longitudes), and expanded sets of viewing and illumination constraints. Support for MVIC with mission provided metadata. Browse results and download.', 'https://tools.pds-rings.seti.org/opus#/missionid=New+Horizons', 3);


insert into investigation_tools (investigation, title, description, url, rank) values ('New Horizons', 'New Horizons data at the Small Bodies Node', 'Provides access to data from the entire mission (Post-launch checkout, Jupiter flyby, Pluto cruise, & Pluto encounter).', 'https://pds-smallbodies.astro.umd.edu/data_sb/missions/newhorizons/index.shtml', 101);

insert into investigation_tools (investigation, title, description, url, rank) values ('New Horizons', 'New Horizons Archive at the Planetary Plasma Interactions Node', 'The New Horizons Archive page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering New Horizons data.', 'https://pds-ppi.igpp.ucla.edu/mission/New_Horizons', 102);

insert into investigation_tools (investigation, title, description, url, rank) values ('New Horizons', 'New Horizons Jupiter Encounter at the Atmospheres Node', 'The New Horizons Jupiter Encounter page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering New Horizons data.', 'http://pds-atmospheres.nmsu.edu/data_and_services/atmospheres_data/NewHorizons/NH_Jupiter.html', 103);

insert into investigation_tools (investigation, title, description, url, rank) values ('New Horizons', 'SPICE Data Holdings at the Navigation and Ancillary Information Facility Node', 'Supports New Horizons SPICE product browse and download, and subsequent science data analysis.', 'https://naif.jpl.nasa.gov/naif/data_archived.html', 104);

/* Phoenix */

insert into investigation_tools (investigation, title, description, url, rank) values ('Phoenix', 'Phoenix Analyst''s Notebook at the Geosciences Node', 'Provides access to the Phoenix data archives by integrating sequence information, science data, and documentation into standard web-accessible pages to facilitate mission "replay."', 'http://an.rsl.wustl.edu/phx', 1);

insert into investigation_tools (investigation, title, description, url, rank) values ('Phoenix', 'PDS Image Atlas at the Cartography and Imaging Sciences Node', 'Supports Phoenix OM, RAC and SSI image product search, browse and download.', 'https://pds-imaging.jpl.nasa.gov/search/?fq=ATLAS_MISSION_NAME%3Aphoenix&q=*%3A*', 2);


insert into investigation_tools (investigation, title, description, url, rank) values ('Phoenix', 'Phoenix Archive at the Atmospheres Node', 'The Phoenix Archive page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering Phoenix data.', 'https://atmos.nmsu.edu/data_and_services/atmospheres_data/phoenix/phoenix.html', 101);

/* Pioneer */

insert into investigation_tools (investigation, title, description, url, rank) values ('Pioneer', 'Pioneer 10 Archive at the Planetary Plasma Intractions Node', 'The Pioneer 10 Archive page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering Pioneer 10 data.', 'https://pds-ppi.igpp.ucla.edu/mission/Pioneer_10', 101);

insert into investigation_tools (investigation, title, description, url, rank) values ('Pioneer', 'Pioneer 11 Archive at the Planetary Plasma Intractions Node', 'The Pioneer 11 Archive page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering Pioneer 10 data.', 'https://pds-ppi.igpp.ucla.edu/mission/Pioneer_11', 102);

insert into investigation_tools (investigation, title, description, url, rank) values ('Pioneer', 'Pioneer Venus Archive at the Planetary Plasma Intractions Node', 'The Pioneer Venus Archive page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering Pioneer 10 data.', 'https://pds-ppi.igpp.ucla.edu/mission/Pioneer_Venus', 103);

insert into investigation_tools (investigation, title, description, url, rank) values ('Pioneer', 'SPICE Data Holdings at the Navigation and Ancillary Information Facility Node', 'Supports Pioneer 10/11/12 and Pioneer Venus Orbiter SPICE product browse and download, and subsequent science data analysis.', 'https://naif.jpl.nasa.gov/naif/data_archived.html', 105);

/* Rosetta */

insert into investigation_tools (investigation, title, description, url, rank) values ('Rosetta', 'Rosetta Data Archive at the Small Bodies Node', 'Supports Rosetta Mission product browse and download for 67P/Churyumov-Gerasimenko, 2867 Steins, 21Lutetia, 9P/Tempel 1 Cruise and Earth & Mars (includes data on Jupiter) swingby data.', 'https://pds-smallbodies.astro.umd.edu/data_sb/missions/rosetta/index.shtml', 101);

insert into investigation_tools (investigation, title, description, url, rank) values ('Rosetta', 'SPICE Data Holdings at the Navigation and Ancillary Information Facility Node', 'Supports Rosetta SPICE product browse and download, and subsequent science data analysis.', 'https://naif.jpl.nasa.gov/naif/data_archived.html', 102);

/* Stardust */

insert into investigation_tools (investigation, title, description, url, rank) values ('Stardust', 'Stardust Data Archive at the Small Bodies Node', 'Supports Stardust Mission product browse and download for 81P/Wild 2, 5535 Annefrank and cometary dust data.', 'https://pds-smallbodies.astro.umd.edu/data_sb/missions/stardust/index.shtml', 101);

insert into investigation_tools (investigation, title, description, url, rank) values ('Stardust', 'SPICE Data Holdings at the Navigation and Ancillary Information Facility Node', 'Supports Stardust SPICE product browse and download, and subsequent science data analysis.', 'https://naif.jpl.nasa.gov/naif/data_archived.html', 102);

/* Stardust-NExT */

insert into investigation_tools (investigation, title, description, url, rank) values ('Stardust-NExT', 'Stardust-NExT Data Archive at the Small Bodies Node', 'Supports Stardust-NExT Mission product browse and download for 9P/Tempel 1 data.', 'https://pds-smallbodies.astro.umd.edu/data_sb/missions/stardustnext/index.shtml', 101);

/* Ulysses */

insert into investigation_tools (investigation, title, description, url, rank) values ('Ulysses', 'Ulysses Archive at the Planetary Plasma Interactions Node', 'The Ulysses Archive page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering Ulysses data.', 'https://pds-ppi.igpp.ucla.edu/mission/Ulysses', 101);

insert into investigation_tools (investigation, title, description, url, rank) values ('Ulysses', 'Ulysses Dust Data Archive at the Small Bodies Node', 'Supports Ulysses Mission product browse and download for dust data.', 'https://sbn.psi.edu/pds/resource/udds.html', 102);

/* Vega */

insert into investigation_tools (investigation, title, description, url, rank) values ('Vega', 'Vega 1 Archive at the Planetary Plasma Interactions Node', 'The Vega 1 Archive page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering Vega 1 data.', 'https://pds-ppi.igpp.ucla.edu/mission/Vega_1', 101);

insert into investigation_tools (investigation, title, description, url, rank) values ('Vega', 'Vega 2 Archive at the Planetary Plasma Interactions Node', 'The Vega 2 Archive page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering Vega 2 data.', 'https://pds-ppi.igpp.ucla.edu/mission/Vega_2', 102);

insert into investigation_tools (investigation, title, description, url, rank) values ('Vega', 'SPICE Data Holdings at the Navigation and Ancillary Information Facility Node', 'Supports Vega 1/2 SPICE product browse and download, and subsequent science data analysis.', 'https://naif.jpl.nasa.gov/naif/data_archived.html', 103);

/* Viking Lander */

insert into investigation_tools (investigation, title, description, url, rank) values ('Viking Lander', 'PDS Image Atlas at the Cartography and Imaging Sciences Node', 'Supports Viking Lander camera image product search, browse and download.', 'https://pds-imaging.jpl.nasa.gov/search/?fq=ATLAS_MISSION_NAME%3A%22viking%20lander%22&q=*%3A*', 1);


insert into investigation_tools (investigation, title, description, url, rank) values ('Viking Lander', 'Viking Lander Archive at the Geosciences Node', 'The Viking Lander Archive page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering Viking Lander data.', 'http://pds-geosciences.wustl.edu/missions/vlander/index.htm', 101);

/* Viking Orbiter */

insert into investigation_tools (investigation, title, description, url, rank) values ('Viking Orbiter', 'PDS Image Atlas at the Cartography and Imaging Sciences Node', 'Supports Viking Orbiter VIS image product search, browse and download.', 'https://pds-imaging.jpl.nasa.gov/search/?fq=ATLAS_MISSION_NAME%3A%22viking%20orbiter%22&q=*%3A*', 1);

insert into investigation_tools (investigation, title, description, url, rank) values ('Viking Orbiter', 'Mars Orbital Data Explorer (ODE) at the Geosciences Node', 'Supports search, browse, and download of select PDS data products from the Viking Orbiter mission, including Visual Imaging Subsystem (VIS) camera data.', 'http://ode.rsl.wustl.edu/mars/', 2);

insert into investigation_tools (investigation, title, description, url, rank) values ('Viking Orbiter', 'Map-A-Planet 2 (MAP2) at the Cartography and Imaging Sciences Node', 'Supports Viking Orbiter VIS global image product mosaic projection, clipping, processing, and download.', 'https://astrogeology.usgs.gov/search/results?q=MAP2&k1=target&v1=Mars', 3);

insert into investigation_tools (investigation, title, description, url, rank) values ('Viking Orbiter', 'Planetary Image Locator Tool (PILOT) at the Cartography and Imaging Sciences Node', 'Supports Viking Orbiter VIS-1A, VIS-2B, VIS-1B, VIS-2A map-based image product locator tool using uniform image geometry to search and download.', 'https://pilot.wr.usgs.gov/', 4);


insert into investigation_tools (investigation, title, description, url, rank) values ('Viking Orbiter', 'Viking Orbiter Archive at the Geosciences Node', 'The Viking Orbiter Archive page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering Viking Orbiter data.', 'http://pds-geosciences.wustl.edu/missions/viking/index.htm', 101);

insert into investigation_tools (investigation, title, description, url, rank) values ('Viking Orbiter', 'SPICE Data Holdings at the Navigation and Ancillary Information Facility Node', 'Supports Viking Orbiter SPICE product browse and download, and subsequent science data analysis.', 'https://naif.jpl.nasa.gov/naif/data_archived.html', 105);

/* Voyager */

insert into investigation_tools (investigation, title, description, url, rank) values ('Voyager', 'OPUS at the Ring-Moon Systems Node', 'Supports Voyager ISS, all encounters with enhanced geometric metadata enabling more detailed searches, including surface search constraints for the planet, satellites and rings (e.g., latitudes and longitudes), and expanded sets of viewing and illumination constraints. Browse results and download, including calibrated versions.', 'https://tools.pds-rings.seti.org/opus#/missionid=Voyager', 1);

insert into investigation_tools (investigation, title, description, url, rank) values ('Voyager', 'PDS Image Atlas at the Cartography and Imaging Sciences Node', 'Supports Voyager ISS-NAC, ISS-WAC, and VG-ISS image product search, browse and download.', 'https://pds-imaging.jpl.nasa.gov/search/?fq=ATLAS_MISSION_NAME%3Avoyager&q=*%3A*', 2);

insert into investigation_tools (investigation, title, description, url, rank) values ('Voyager', 'Map-A-Planet 2 (MAP2) at the Cartography and Imaging Sciences Node', 'Supports Voyager ISS global image product mosaic projection, clipping, processing, and download.', 'https://astrogeology.usgs.gov/tools/map-a-planet-2', 3);

insert into investigation_tools (investigation, title, description, url, rank) values ('Voyager', 'Planetary Image Locator Tool (PILOT) at the Cartography and Imaging Sciences Node', 'Supports Voyager ISS-NAC and WAC map-based image product locator tool using uniform image geometry to search and download.', 'https://pilot.wr.usgs.gov/', 4);


insert into investigation_tools (investigation, title, description, url, rank) values ('Voyager', 'Voyager Archive at the Atmospheres Node', 'The Voyager Archive page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering Voyager data.', 'https://pds-atmospheres.nmsu.edu/data_and_services/atmospheres_data/Voyager/voyager.html', 101);

insert into investigation_tools (investigation, title, description, url, rank) values ('Voyager', 'Voyager Archive at the Planetary Plasma Interactions Node', 'The Voyager Archive page provides details on the investigation, instruments and targets associated with the archive as well as additional resources for discovering Voyager data.', 'https://pds-ppi.igpp.ucla.edu/mission/Voyager', 102);

insert into investigation_tools (investigation, title, description, url, rank) values ('Voyager', 'SPICE Data Holdings at the Navigation and Ancillary Information Facility Node', 'Supports Voyager 1/2 SPICE product browse and download, and subsequent science data analysis.', 'https://naif.jpl.nasa.gov/naif/data_archived.html', 103);

/*
** target_tools
*/

/* Comets/Asteroids */

insert into target_tools (target, title, description, url, rank) values ('Comets/Asteroids', 'Ferret at the Small Bodies Node', 'Search tool to find targets and their associated data products in the PDS Small Bodies Node (currently only for PDS3 data).', 'https://sbnapps.psi.edu/ferret/', 1);

insert into target_tools (target, title, description, url, rank) values ('Comets/Asteroids', 'Small Bodies Image Browser at the Small Bodies Node', 'Supports Dawn, NEAR Shoemaker product browse and download for Ceres, Vesta and Eros data.', 'https://sbib.psi.edu/', 2);

insert into target_tools (target, title, description, url, rank) values ('Comets/Asteroids', 'Planetary Image Locator Tool (PILOT) at the Cartography and Imaging Sciences Node', 'Supports Dawn map-based image product locator tool using uniform image geometry to search and download of Ceres and Vesta data.', 'https://pilot.wr.usgs.gov', 3);

insert into target_tools (target, title, description, url, rank) values ('Comets/Asteroids', 'Map-A-Planet 2 (MAP2) at the Cartography and Imaging Sciences Node', 'Supports Dawn global image mosaic projection, clipping, processing and download of Ceres and Vesta data.', 'https://astrogeology.usgs.gov/tools/map-a-planet-2', 4);


insert into target_tools (target, title, description, url, rank) values ('Comets/Asteroids', 'Comet Data at the Small Bodies Node', 'Supports multiple investigations for product browse and download of comet data.', 'https://pdssbn.astro.umd.edu/data_sb/target_comets.shtml', 101);

insert into target_tools (target, title, description, url, rank) values ('Comets/Asteroids', 'Asteroid Data at the Small Bodies Node', 'Supports multiple investigations for product browse and download of asteroid data.', 'https://sbn.psi.edu/pds/archive/asteroids.html', 102);

insert into target_tools (target, title, description, url, rank) values ('Comets/Asteroids', 'Comet Data Holdings at the Planetary Plasma Interactions Node', 'Supports Galileo, International Cometary Explorer and Vega 1/2 product browse and download.', 'https://pds-ppi.igpp.ucla.edu/search/?t=Comet&facet=TARGET_NAME', 103);

insert into target_tools (target, title, description, url, rank) values ('Comets/Asteroids', 'Asteroid Data Holdings at the Planetary Plasma Interactions Node', 'Supports Galileo and NEAR product browse and download.', 'https://pds-ppi.igpp.ucla.edu/search/?t=Asteroid&facet=TARGET_NAME', 104);

insert into target_tools (target, title, description, url, rank) values ('Comets/Asteroids', 'SPICE Data Holdings at the Navigation and Ancillary Information Facility Node', 'Supports Dawn, Deep Impact, Deep Space 1, Giotto, Hayabusa, NEAR, OSIRIS-REx, Rosetta, Stardust and Vega 1/2 SPICE product browse and download, and subsequent science data analysis.', 'https://naif.jpl.nasa.gov/naif/data_archived.html', 105);

insert into target_tools (target, title, description, url, rank) values ('Earth System', 'NEAR Mission Data Holdings at the Geosciences Node', 'Supports multiple investigations for product browse and download.', 'http://pds-geosciences.wustl.edu/missions/near/index.htm', 106);

/* Mercury */

insert into target_tools (target, title, description, url, rank) values ('Mercury', 'Mercury Orbital Data Explorer (ODE) at the Geosciences Node', 'Supports search, browse, and download of select PDS data products from the MESSENGER mission, including GRS, NS, XRS, MASCS, MDIS, MLA, and radio science data.', 'http://ode.rsl.wustl.edu/mercury/', 1);

insert into target_tools (target, title, description, url, rank) values ('Mercury', 'PDS Image Atlas at the Cartography and Imaging Sciences Node', 'Supports MESSENGER image product search and download.', 'https://pds-imaging.jpl.nasa.gov/search/?fq=TARGET_NAME%3Amercury&fq=ATLAS_MISSION_NAME%3Amessenger&q=*%3A*', 2);

insert into target_tools (target, title, description, url, rank) values ('Mercury', 'Map-A-Planet 2 (MAP2) at the Cartography and Imaging Sciences Node', 'Supports MESSENGER global image mosaic projection, clipping, processing and download.', 'https://astrogeology.usgs.gov/search/results?q=MAP2&k1=target&v1=Mercury', 3);

insert into target_tools (target, title, description, url, rank) values ('Mercury', 'Planetary Image Locator Tool (PILOT) at the Cartography and Imaging Sciences Node', 'Supports Mariner 10 and MESSENGER map-based image product locator tool using uniform image geometry to search and download.', 'https://pilot.wr.usgs.gov', 4);


insert into target_tools (target, title, description, url, rank) values ('Mercury', 'Mercury Data Holdings at the Planetary Plasma Interactions Node', 'Supports Mariner 10 and MESSENGER product browse and download.', 'https://pds-ppi.igpp.ucla.edu/search/?t=Mercury&facet=TARGET_NAME', 101);

insert into target_tools (target, title, description, url, rank) values ('Mercury', 'MESSENGER Mission Data Holdings at the Geosciences Node', 'Supports multiple investigations for product browse and download.', 'http://pds-geosciences.wustl.edu/missions/messenger/index.htm', 102);

insert into target_tools (target, title, description, url, rank) values ('Mercury', 'SPICE Data Holdings at the Navigation and Ancillary Information Facility Node', 'Supports Mariner 10 and MESSENGER SPICE product browse and download, and subsequent science data analysis.', 'https://naif.jpl.nasa.gov/naif/data_archived.html', 103);

/* Venus */

insert into target_tools (target, title, description, url, rank) values ('Venus', 'PDS Image Atlas at the Cartography and Imaging Sciences Node', 'Supports Magellan, Galileo, Cassini, and Messenger image, radar, and imaging spectrometer product search, browse and download.', 'https://pds-imaging.jpl.nasa.gov/search/?fq=TARGET_NAME%3Avenus&q=*%3A*', 1);

insert into target_tools (target, title, description, url, rank) values ('Venus', 'Planetary Image Locator Tool (PILOT) at the Cartography and Imaging Sciences Node', 'Supports Galileo, Cassini, Mariner 10, and MESSENGER map-based image product locator tool using uniform image geometry to search and download.', 'https://pilot.wr.usgs.gov', 2);

insert into target_tools (target, title, description, url, rank) values ('Venus', 'Venus Orbital Data Explorer (ODE) at the Geosciences Node', 'Supports search, browse, and download of select PDS data products from the Magellan and MESSENGER missions. The Venus ODE also supports Earth-based Venus radar observations.', 'http://ode.rsl.wustl.edu/venus/', 3);

insert into target_tools (target, title, description, url, rank) values ('Venus', 'Map-A-Planet 2 (MAP2) at the Cartography and Imaging Sciences Node', 'Supports Magellan global image mosaic projection, clipping, processing and download.', 'https://astrogeology.usgs.gov/search/results?q=MAP2&k1=target&v1=Venus', 4);

insert into target_tools (target, title, description, url, rank) values ('Venus', 'OPUS at the Ring-Moon Systems Node', 'Supports a small number of long range Cassini (ISS ,UVIS,VIMS) observations. Browse results and download including calibrated ISS.', 'https://tools.pds-rings.seti.org/opus#/planet=Venus', 5);


insert into target_tools (target, title, description, url, rank) values ('Venus', 'Venus Data Holdings at the Planetary Plasma Interactions Node', 'Supports Cassini, Galileo, MESSENGER, Pioneer Venus Orbiter (PVO) and Venera 15/16 product browse and download.', 'https://pds-ppi.igpp.ucla.edu/search/?t=Venus&facet=TARGET_NAME', 101);

insert into target_tools (target, title, description, url, rank) values ('Venus', 'Magellan Mission Data Holdings at the Geosciences Node.', 'Supports multiple investigations for product browse and download.', 'http://pds-geosciences.wustl.edu/missions/magellan/index.htm', 102);

insert into target_tools (target, title, description, url, rank) values ('Venus', 'SPICE Data Holdings at the Navigation and Ancillary Information Facility Node', 'Supports Magellan, Mariner 2, Pioneer Venus Orbiter and Venus Express SPICE product browse and download, and subsequent science data analysis.', 'https://naif.jpl.nasa.gov/naif/data_archived.html', 103);

/* Earth System */

insert into target_tools (target, title, description, url, rank) values ('Earth System', 'Lunar Orbital Data Explorer (ODE) at the Geosciences Node', 'Provides search, display, and download tools for the PDS data archives of the Lunar Reconnaissance Orbiter (LRO), Clementine, Lunar Prospector, Chandrayaan-1, and GRAIL missions to Earth''s moon. The Lunar ODE also supports restored Lunar Orbiter data as well as Earth-based lunar radar observations.', 'http://ode.rsl.wustl.edu/moon', 1);

insert into target_tools (target, title, description, url, rank) values ('Earth System', 'PDS Image Atlas at the Cartography and Imaging Sciences Node', 'Supports Clementine, Cassini, Galileo, LRO, MSL, and Messenger image and imaging spectrometer product search, browse and download.', 'https://pds-imaging.jpl.nasa.gov/search/?fq=TARGET_NAME%3Aearth&q=*%3A*', 2);

insert into target_tools (target, title, description, url, rank) values ('Earth System', 'Map-A-Planet 2 (MAP2) at the Cartography and Imaging Sciences Node', 'Supports Clementine, LRO, New Horizons, Apollo, Lunar Orbiter and SELENE/Kaguya global image mosaic projection, clipping, processing and download.', 'https://astrogeology.usgs.gov/search/results?q=MAP2&k1=target&v1=Moon', 3);

insert into target_tools (target, title, description, url, rank) values ('Earth System', 'Planetary Image Locator Tool (PILOT) at the Cartography and Imaging Sciences Node', 'Supports Cassini, Chandrayaan-1, Clementine, Galileo, LRO, MRO, and MESSENGER map-based image product locator tool using uniform image geometry to search and download.', 'https://pilot.wr.usgs.gov', 4);

insert into target_tools (target, title, description, url, rank) values ('Earth System', 'LCROSS Analyst''s Notebook at the Geosciences Node', 'Provides access to data from the Lunar Crater Observation And Sensing Satellite (LCROSS) mission, including camera, spectrometer, and photometer data.', 'http://an.rsl.wustl.edu/lcross/lcrossbrowser/', 5);

insert into target_tools (target, title, description, url, rank) values ('Earth System', 'Apollo Analyst''s Notebook at the Geosciences Node', 'Provides access to data from the Apollo program''s six manned missions to the lunar surface.', 'http://an.rsl.wustl.edu/apollo/', 6);

insert into target_tools (target, title, description, url, rank) values ('Earth System', 'OPUS at the Ring-Moon Systems Node', 'Supports a small number of Cassini ISS and HST WFPC2 observations.  Browse and download including calibrated Cassini ISS. Browse with links to STScI for HST downloads.', 'https://tools.pds-rings.seti.org/opus#/planet=Earth', 7);


insert into target_tools (target, title, description, url, rank) values ('Earth System', 'Lunar Data Holdings at the Geosciences Node', 'Supports multiple investigations for product browse and download.', 'http://pds-geosciences.wustl.edu/dataserv/moon.html', 101);

insert into target_tools (target, title, description, url, rank) values ('Earth System', 'Earth Data Holdings at the Geosciences Node', 'Supports multiple investigations for product browse and download.', 'http://pds-geosciences.wustl.edu/dataserv/earth.html', 102);

insert into target_tools (target, title, description, url, rank) values ('Earth System', 'Earth (Moon) Data Holdings at the Planetary Plasma Interactions Node', 'Supports Cassini, Galileo, Juno, Lunar Prospector, Lunar Reconnaissance Orbiter (LRO), MESSENGER and NEAR product browse and download.', 'https://pds-ppi.igpp.ucla.edu/search/?t=Earth(Moon)&facet=TARGET_NAME', 103);

insert into target_tools (target, title, description, url, rank) values ('Earth System', 'SPICE Data Holdings at the Navigation and Ancillary Information Facility Node', 'Supports Apollo, Clementine, GRAIL, LADEE, LRO, Lunar Prospector, Lunur Orbiters, Kaguya and SMART-1 SPICE product browse and download, and subsequent science data analysis.', 'https://naif.jpl.nasa.gov/naif/data_archived.html', 104);

/* Mars System */

insert into target_tools (target, title, description, url, rank) values ('Mars System', 'Mars Orbital Data Explorer (ODE) at the Geosciences Node', 'Supports search, browse, and download of PDS data products from the Mars Reconnaissance Orbiter, Mars Express, Mars Global Surveyor, Mars Odyssey, and Viking Orbiter missions.', 'http://ode.rsl.wustl.edu/mars/', 1);

insert into target_tools (target, title, description, url, rank) values ('Mars System', 'PDS Image Atlas at the Cartography and Imaging Sciences Node', 'Supports Mars Odyssey, Mars Exploration Rover, Mars Global Surveyor, Mars Pathfinder, Mars Reconnaissance Orbiter, Mars Science Laboratory, Phoenix, Viking Lander, Viking Orbiter, and Messenger image and imaging spectrometer product search, browse, and download.', 'https://pds-imaging.jpl.nasa.gov/search/?fq=TARGET_NAME%3Amars&q=*%3A*', 2);

insert into target_tools (target, title, description, url, rank) values ('Mars System', 'Map-A-Planet 2 (MAP2) at the Cartography and Imaging Sciences Node', 'Supports Mars Reconnaissance Orbiter, Mars Science Laboratory, Viking, Mars Global Surveyor, Mars Express and Odyssey global image mosaic projection, clipping, processing and download.', 'https://astrogeology.usgs.gov/search/results?q=MAP2&k1=target&v1=Mars', 3);

insert into target_tools (target, title, description, url, rank) values ('Mars System', 'Planetary Image Locator Tool (PILOT) at the Cartography and Imaging Sciences Node', 'Supports Mars Express, Mars Global Surveyor, Mars Reconnaissance Orbiter, MESSENGER, Odyssey and Viking Orbiter map-based image product locator tool using uniform image geometry to search and download.', 'https://pilot.wr.usgs.gov', 4);

insert into target_tools (target, title, description, url, rank) values ('Mars System', 'MSL Analyst''s Notebook at the Geosciences Node', 'Provides access to the Mars Science Laboratory Curiosity mission data archives by integrating sequence information, science data, and documentation into standard web-accessible pages to facilitate mission "replay".', 'http://an.rsl.wustl.edu/msl', 5);

insert into target_tools (target, title, description, url, rank) values ('Mars System', 'MER Analyst''s Notebook at the Geosciences Node', 'Provides access to the Mars Exploration Rover Spirit and Opportunity mission data archives by integrating sequence information, science data, and documentation into standard web-accessible pages to facilitate mission "replay".', 'http://an.rsl.wustl.edu/mer', 6);

insert into target_tools (target, title, description, url, rank) values ('Mars System', 'Phoenix Analyst''s Notebook at the Geosciences Node', 'Provides access to the Phoenix mission data archives by integrating sequence information, science data, and documentation into standard web-accessible pages to facilitate mission "replay".', 'http://an.rsl.wustl.edu/phx', 7);

insert into target_tools (target, title, description, url, rank) values ('Mars System', 'OPUS at the Ring-Moon Systems Node', 'Supports HST (ACS, WFC3, WFPC2). Browse results and links to STScI for HST downloads.', 'https://tools.pds-rings.seti.org/opus#/planet=Mars', 8);


insert into target_tools (target, title, description, url, rank) values ('Mars System', 'Mars Data Holdings at the Geosciences Node', 'Supports multiple investigations for product browse and download.', 'http://pds-geosciences.wustl.edu/missions/mep/index.htm', 101);

insert into target_tools (target, title, description, url, rank) values ('Mars System', 'Mars Data Holdings at the Planetary Plasma Interactions Node', 'Supports MAVEN, Mariner 9, Mars Express (MEX), Mars Global Surveyor (MGS), Mars Odyssey, Mars Science Laboratory (MSL) and Ulysses product browse and download.', 'https://pds-ppi.igpp.ucla.edu/search/?t=Mars&facet=TARGET_NAME', 102);

insert into target_tools (target, title, description, url, rank) values ('Mars System', 'Mars Atmospheric Data Archive at the Atmospheres Node', 'Supports browse and download of Mars atmospheric data based on missions, individual instruments or on physical parameters.', 'http://pds-atmospheres.nmsu.edu/data_and_services/atmospheres_data/MARS/data_archive.html', 103);

insert into target_tools (target, title, description, url, rank) values ('Mars System', 'Planetary Satellite Data at the Small Bodies Node', 'Supports data sets containing planetary satellite data for browse and download.', 'https://sbn.psi.edu/pds/archive/sat.html', 104);

insert into target_tools (target, title, description, url, rank) values ('Mars System', 'SPICE Data Holdings at the Navigation and Ancillary Information Facility Node', 'Supports ExoMars, InSight, Mariner 9, Mars Climate Orbiter, Mars Exploration Rovers, Mars Express, Mars Global Surveyor, Mars Odyssey, Mars Pathfinder, Mars Polar Lander, Mars Reconnaissance Orbiter, Mars Science Laboratory, MAVEN, Nozomi, Phobos-88, Phoenix, Phobos Sample Return and Viking 1/2 SPICE product browse and download, and subsequent science data analysis.', 'https://naif.jpl.nasa.gov/naif/data_archived.html', 105);

/* Jupiter System */

insert into target_tools (target, title, description, url, rank) values ('Jupiter System', 'PDS Image Atlas at the Cartography and Imaging Sciences Node', 'Supports Cassini, Galileo, MGS, MSL, MESSENGER, New Horizons and Voyager image, radar, and imaging spectrometer product search, browse and download.', 'https://pds-imaging.jpl.nasa.gov/search/?fq=TARGET_NAME%3Ajupiter&q=*%3A*', 1);

insert into target_tools (target, title, description, url, rank) values ('Jupiter System', 'OPUS at the Ring-Moon Systems Node', 'Supports New Horizons (LORRI), Cassini (ISS, UVIS, VIMS), and Voyager (ISS), all with enhanced geometric metadata which enables more detailed searches, including surface search constraints for the planet, satellites and rings (e.g., latitudes and longitudes).  Also supports LORRI (MVIC) and HST (ACS,WFC3,WFPC2). Browse and download including calibrated Cassini ISS and Voyager ISS. Links to STScI for HST downloads.', 'https://tools.pds-rings.seti.org/opus#/planet=JUPITER', 2);

insert into target_tools (target, title, description, url, rank) values ('Jupiter System', 'Planetary Image Locator Tool (PILOT) at the Cartography and Imaging Sciences Node', 'Supports Cassini, Galileo, MESSENGER, and Voyager map-based image product locator tool using uniform image geometry to search and download.', 'https://pilot.wr.usgs.gov', 3);

insert into target_tools (target, title, description, url, rank) values ('Jupiter System', 'Map-A-Planet 2 (MAP2) at the Cartography and Imaging Sciences Node', 'Supports Galileo and Voyager global image mosaic projection, clipping, processing and download.', 'https://astrogeology.usgs.gov/tools/map-a-planet-2', 4);


insert into target_tools (target, title, description, url, rank) values ('Jupiter System', 'Jupiter Data Holdings at the Planetary Plasma Interactions Node', 'Supports Cassini, Galileo, Juno, New Horizons, Pioneer 10/11, Ulysses and Voyager 1/2 product browse and download.', 'https://pds-ppi.igpp.ucla.edu/search/?t=Jupiter&facet=TARGET_NAME', 101);

insert into target_tools (target, title, description, url, rank) values ('Jupiter System', 'Jupiter Data Archive at the Planetary Atmospheres Node', 'Supports access to Cassini, Galileo, Juno, New Horizons, Pioneer and Voyager product browse and download.', 'https://pds-atmospheres.nmsu.edu/data_and_services/atmospheres_data/JUPITER/matrice.html', 102);

insert into target_tools (target, title, description, url, rank) values ('Jupiter System', 'Planetary Satellite Data at the Small Bodies Node', 'Supports data sets containing planetary satellite data for browse and download.', 'https://sbn.psi.edu/pds/archive/sat.html', 103);

insert into target_tools (target, title, description, url, rank) values ('Jupiter System', 'SPICE Data Holdings at the Navigation and Ancillary Information Facility Node', 'Supports Cassini, Galileo, Juno, New Horizons, Pioneer 10/11 and Voyager 1/2 SPICE product browse and download, and subsequent science data analysis.', 'https://naif.jpl.nasa.gov/naif/data_archived.html', 104);

/* Saturn System */

insert into target_tools (target, title, description, url, rank) values ('Saturn System', 'OPUS at the Ring-Moon Systems Node', 'Supports Cassini (ISS, UVIS, VIMS) and Voyager (ISS), all with enhanced geometric metadata which enables more detailed searches, including surface search constraints for the planet, satellites and rings (e.g., latitudes and longitudes). Also supports HST (ACS, WFC3, WFPC2). Browse and download including calibrated Cassini ISS and Voyager ISS. Links to STScI for HST downloads.', 'https://tools.pds-rings.seti.org/opus#/planet=Saturn', 1);

insert into target_tools (target, title, description, url, rank) values ('Saturn System', 'PDS Image Atlas at the Cartography and Imaging Sciences Node', 'Supports Cassini and Voyager image, radar, and imaging spectrometer product search, browse and download.', 'https://pds-imaging.jpl.nasa.gov/search/?fq=TARGET_NAME%3Asaturn&q=*%3A*', 2);

insert into target_tools (target, title, description, url, rank) values ('Saturn System', 'Planetary Image Locator Tool (PILOT) at the Cartography and Imaging Sciences Node', 'Supports Cassini, MESSENGER and Voyager map-based image product locator tool using uniform image geometry to search and download.', 'https://pilot.wr.usgs.gov', 3);

insert into target_tools (target, title, description, url, rank) values ('Saturn System', 'Map-A-Planet 2 (MAP2) at the Cartography and Imaging Sciences Node', 'Supports Cassini and Voyager global image mosaic projection, clipping, processing and download.', 'https://astrogeology.usgs.gov/tools/map-a-planet-2', 4);


insert into target_tools (target, title, description, url, rank) values ('Saturn System', 'Saturn Data Holdings at the Planetary Plasma Interactions Node', 'Supports Cassini, Pioneer 11 and Voyager 1/2 product browse and download.', 'https://pds-ppi.igpp.ucla.edu/search/?t=Saturn&facet=TARGET_NAME', 101);

insert into target_tools (target, title, description, url, rank) values ('Saturn System', 'Saturnian Bodies Holdings at the Planetary Atmospheres Node', 'Supports access to Cassini Archive and data by Saturnian body.', 'https://pds-atmospheres.nmsu.edu/data_and_services/atmospheres_data/Cassini/saturn_bodies_page.html', 102);

insert into target_tools (target, title, description, url, rank) values ('Saturn System', 'Planetary Satellite Data at the Small Bodies Node', 'Supports data sets containing planetary satellite data for browse and download.', 'https://sbn.psi.edu/pds/archive/sat.html', 103);

insert into target_tools (target, title, description, url, rank) values ('Saturn System', 'SPICE Data Holdings at the Navigation and Ancillary Information Facility Node', 'Supports Cassini, Galileo, Juno, New Horizons, Pioneer 10/11 and Voyager 1/2 SPICE product browse and download, and subsequent science data analysis.', 'https://naif.jpl.nasa.gov/naif/data_archived.html', 104);

/* Uranus System */

insert into target_tools (target, title, description, url, rank) values ('Uranus System', 'PDS Image Atlas at the Cartography and Imaging Sciences Node', 'Supports Voyager image product search, browse and download.', 'https://pds-imaging.jpl.nasa.gov/search/?fq=TARGET_NAME%3Auranus&fq=ATLAS_MISSION_NAME%3Avoyager&q=*%3A*', 1);

insert into target_tools (target, title, description, url, rank) values ('Uranus System', 'OPUS at the Ring-Moon Systems Node', 'Supports Voyager (ISS) with enhanced geometric metadata. Also supports HST (ACS, WFC3, WFPC2). Browse and download including calibrated Voyager ISS. Browse with links to STScI for HST.', 'https://tools.pds-rings.seti.org/opus#/planet=Uranus', 2);

insert into target_tools (target, title, description, url, rank) values ('Uranus System', 'Planetary Image Locator Tool (PILOT) at the Cartography and Imaging Sciences Node', 'Supports Voyager map-based image product locator tool using uniform image geometry to search and download.', 'https://pilot.wr.usgs.gov', 3);


insert into target_tools (target, title, description, url, rank) values ('Uranus System', 'Uranus Data Holdings at the Planetary Plasma Interactions Node', 'Supports Voyager 2 product browse and download.', 'https://pds-ppi.igpp.ucla.edu/search/?t=Uranus&facet=TARGET_NAME', 101);

insert into target_tools (target, title, description, url, rank) values ('Uranus System', 'Planetary Satellite Data at the Small Bodies Node', 'Supports data sets containing planetary satellite data for browse and download.', 'https://sbn.psi.edu/pds/archive/sat.html', 102);

insert into target_tools (target, title, description, url, rank) values ('Uranus System', 'SPICE Data Holdings at the Navigation and Ancillary Information Facility Node', 'Supports Cassini, Galileo, Juno, New Horizons, Pioneer 10/11 and Voyager 1/2 SPICE product browse and download, and subsequent science data analysis.', 'https://naif.jpl.nasa.gov/naif/data_archived.html', 103);

/* Neptune System */

insert into target_tools (target, title, description, url, rank) values ('Neptune System', 'PDS Image Atlas at the Cartography and Imaging Sciences Node', 'Supports Voyager image product search, browse and download.', 'https://pds-imaging.jpl.nasa.gov/search/?fq=TARGET%3Aneptune&q=*%3A*', 1);

insert into target_tools (target, title, description, url, rank) values ('Neptune System', 'OPUS at the Ring-Moon Systems Node', 'Supports Voyager ISS with enhanced geometric metadata. Also supports HST (ACS, WFC3, WFPC2). Browse and download including calibrated Voyager ISS. Browse with links to STScI for HST.', 'https://tools.pds-rings.seti.org/opus#/planet=Neptune', 2);

insert into target_tools (target, title, description, url, rank) values ('Neptune System', 'Planetary Image Locator Tool (PILOT) at the Cartography and Imaging Sciences Node', 'Supports Voyager map-based image product locator tool using uniform image geometry to search and download.', 'https://pilot.wr.usgs.gov', 3);


insert into target_tools (target, title, description, url, rank) values ('Neptune System', 'Neptune Data Holdings at the Planetary Plasma Interactions Node', 'Supports Voyager 2 product browse and download.', 'https://pds-ppi.igpp.ucla.edu/search/?t=Neptune&facet=TARGET_NAME', 101);

insert into target_tools (target, title, description, url, rank) values ('Neptune System', 'Planetary Satellite Data at the Small Bodies Node', 'Supports data sets containing planetary satellite data for browse and download.', 'https://sbn.psi.edu/pds/archive/sat.html', 102);

insert into target_tools (target, title, description, url, rank) values ('Neptune System', 'SPICE Data Holdings at the Navigation and Ancillary Information Facility Node', 'Supports Cassini, Galileo, Juno, New Horizons, Pioneer 10/11 and Voyager 1/2 SPICE product browse and download, and subsequent science data analysis.', 'https://naif.jpl.nasa.gov/naif/data_archived.html', 103);

/* Pluto System */

insert into target_tools (target, title, description, url, rank) values ('Pluto System', 'OPUS at the Ring-Moon Systems Node', 'Supports New Horizons (LORRI) and Voyager (ISS), both with enhanced geometric metadata. Also supports HST (ACS, WFC3, WFPC2). Browse and download including calibrated Voyager ISS. Browse with links to STScI for HST.', 'https://tools.pds-rings.seti.org/opus#/planet=Pluto', 1);

insert into target_tools (target, title, description, url, rank) values ('Pluto System', 'PDS Image Atlas at the Cartography and Imaging Sciences Node', 'Supports New Horizons Lorri image product search and download.', 'https://pds-imaging.jpl.nasa.gov/search/?fq=TARGET_NAME%3Apluto&fq=ATLAS_MISSION_NAME%3A%22new%20horizons%22&q=*%3A*', 2);

insert into target_tools (target, title, description, url, rank) values ('Pluto System', 'Map-A-Planet 2 (MAP2) at the Cartography and Imaging Sciences Node', 'Supports New Horizons global image mosaic projection, clipping, processing and download.', 'https://astrogeology.usgs.gov/tools/map-a-planet-2', 3);

insert into target_tools (target, title, description, url, rank) values ('Pluto System', 'Ferret at the Small Bodies Node', 'Search tool to find targets and their associated data products in the PDS Small Bodies Node (currently only for PDS3 data).', 'https://sbnapps.psi.edu/ferret/', 4);


insert into target_tools (target, title, description, url, rank) values ('Pluto System', 'New Horizons data at the Small Bodies Node', 'Provides access to data from the entire mission (Post-launch checkout, Jupiter flyby, Pluto cruise, & Pluto encounter).', 'https://pds-smallbodies.astro.umd.edu/data_sb/missions/newhorizons/index.shtml', 101);

insert into target_tools (target, title, description, url, rank) values ('Pluto System', 'Transneptunian, Centaur, and Pluto Data Sets at the Small Bodies Node', 'Supports multiple investigations for data browse and download.', 'https://sbn.psi.edu/pds/archive/tnos.html', 102);

insert into target_tools (target, title, description, url, rank) values ('Pluto System', 'SPICE Data Holdings at the Navigation and Ancillary Information Facility Node', 'Supports Cassini, Galileo, Juno, New Horizons, Pioneer 10/11 and Voyager 1/2 SPICE product browse and download, and subsequent science data analysis.', 'https://naif.jpl.nasa.gov/naif/data_archived.html', 103);

/* KBOs */

insert into target_tools (target, title, description, url, rank) values ('KBOs', 'Ferret at the Small Bodies Node', 'Search tool to find targets and their associated data products in the PDS Small Bodies Node (currently only for PDS3 data).', 'https://sbnapps.psi.edu/ferret/', 1);

insert into target_tools (target, title, description, url, rank) values ('KBOs', 'Transneptunian, Centaur, and Pluto Data Sets at the Small Bodies Node', 'Supports multiple investigations for data browse and download.', 'https://sbn.psi.edu/pds/archive/tnos.html', 101);

/* Dust */

insert into target_tools (target, title, description, url, rank) values ('Dust', 'Dust Archive at the Small Bodies Node', 'Supports Cassini, Deep Space 1, Galileo, IRAS, MSX, New Horizons, LADEE, Rosetta, Stardust and Ulysses dust data browse and download.', 'https://sbn.psi.edu/pds/archive/dust.html', 101);

insert into target_tools (target, title, description, url, rank) values ('Dust', 'Dust Data Holdings at the Planetary Plasma Interactions Node', 'Supports Galileo and Ulysses product browse and download.', 'https://pds-ppi.igpp.ucla.edu/search/?t=Dust&facet=TARGET_NAME', 102);

/* Solar Wind */

insert into target_tools (target, title, description, url, rank) values ('Solar Wind', 'Solar Wind Data Holdings at the Planetary Plasma Interactions Node', 'Supports Cassini, Juno, MESSENGER, NEAR, New Horizons, Vega 1/2 and Voyager 1/2 product browse and download.', 'https://pds-ppi.igpp.ucla.edu/search/?t=Solar_Wind&facet=TARGET_NAME', 101);
