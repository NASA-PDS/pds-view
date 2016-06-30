PDS_VERSION_ID = PDS3
LABEL_REVISION_NOTE               = "D.M. OLSON, 2007-08-08,
                                     M.K. GORDON, 2008-07-31"
RECORD_TYPE = STREAM

DATA_SET_ID = "HST-S/SA-WFPC2-4-ASTROM2005-V1.0"

OBJECT = TEXT
  PUBLICATION_DATE = 2007-08-30
    NOTE = "This volume contains ASCII table files giving the positions
  of fourteen Saturnian satellites based on HST images taken during the
  period 1996-2005."
  END_OBJECT = TEXT
END

                      GIANT PLANET SATELLITE ASTROMETRY
            Saturn Satellite Astrometry with  WFPC2: 1996-2005

1.  INTRODUCTION

This volume contains astrometric data for fourteen Saturn satellites.
The data were obtained using the Wide Field Planetary Camera Mod 2
(WFPC2) on the Hubble Space Telescope (HST) over the period 1996-
2005. This volume contains only derived products.

2.  VOLUME FORMAT

This volume has been formatted according to the ISO 9660 Level 2
Interchange Standard.  This format is compatible with a large variety
of computer systems (e.g.  Sun, DEC, Macintosh, PC).  For further
information, refer to the ISO 9660 Standard Document, RF# ISO
9660-1988, 15 April 1988.

3. FILE FORMATS

3.1 FILE EXTENSIONS

Files on this volume are named according to PDS standards, where the
1-3 characters after the period indicate the file type. The file types
used on this volume are:

Binary files:
    SPICE Ephemeris files              *.bsp
    Supplemental Postscript files      *.PS
    Adobe PDF documents                *.PDF
    

ASCII text files:
    PDS labels                          *.LBL
    ASCII tables and indices            *.TAB
    PDS catalog files                   *.CAT
    Text documentation files            *.TXT

3.2 PDS LABELS

Essentially every file in this data set is described by an associated
PDS label. (See ERRATA.TXT for a list of any exceptions). Most files
are described by a detached label, which has the same name as the
corresponding file but an extension '.LBL'. Text files (*.TXT, *.CAT)
have attached labels. See the PDS Standards Reference (JPL D-7669) for
a complete description of the PDS label format. This document can be
obtained on line at:
        http://pds.jpl.nasa.gov/documents/

3.3 ASCII TEXT FILES

Different popular operating systems use different standards for how
ASCII text files are formatted. On Unix systems, lines are terminated
by a <LF> (linefeed character, control-J, ASCII 10). On Macintosh
computers, lines are terminated by a <CR> (carriage return character,
control-M, ASCII 13). On PCs, lines are terminated by a <CR><LF> pair.
PDS standards require all text files to use <CR><LF> line termination.
If the PDS label indicates INTERCHANGE_FORMAT = ASCII or RECORD_TYPE =
STREAM, then this line termination is in use.

On occasion, users on Unix and Macintosh computers may need to change
the line termination on some text files before they can use them. This
can be handled via text editors or a variety of utilities. For
example, the Unix tr (translate) command can be used to change
carriage returns to blanks: tr "\015" " " <oldfile.txt >newfile.txt

Some data files provided on this volume are ASCII tables. These
contain comma separated values in fixed width columns.  For each data
file, a detached PDS label is provided containing additional
parameters.


4.  VOLUME CONTENTS

4.1 DATA SELECTION

Only reduced data are provided in this data set. Raw HST data may be
obtained from the Space Telescope Science Institute.

In the all of the filenames we have incorporated a portion of the 
original filenames. The suffix 'REV8' refers to an internal 
revision number used by the primary investigator. Also, In the 
SORCDATA subdirectory the files also have another suffix, 'GO',
this refers to global offsets applied to the raw data. Details
of this can be found in FRENCHETAL2006.


4.2 DIRECTORY STRUCTURE

This Volume is organized in a directory tree as follows:

root directory
|
|-AAREADME.TXT          This file.
|
|-ERRATA.TXT            A file that reports any known errors and
|                       describes any known deviations from PDS
|                       standards.
|
|-VOLDESC.CAT           A PDS volume object, providing a high-level
|                       description of the volume and its contents.
|
|-DATA                  Directories containing astrometric data in
| |                     ASCII table files.
| |
| |-EASYDATA/
| | |-x_REV8.TAB        Astrometric data for one satellite, where
| | |                   the x is replaced by the name of the satellite.
| | |
| | |-x_REV8.LBL        Detached PDS labels for the above.
| |
| |-SORCDATA/
| | |-x_yy_TABLE_REV8_GO.TXT 
| | |                   Original text file supplied by R. French.
| | |                   x is replaced by the satellite name and
| | |                   yy is either PC_, WF_, or absent altogether.
| | |
| | |-x_yy_TABLE_REV8_GO.LBL 
| | |                   Detached PDS labels for the above.
| | |
| |-DATAINFO.TXT        Description of the files in this directory
| |                     tree.
| 
|
|-CATALOG/              A directory containing PDS catalog
| |                     information, providing high-level descriptions
| |                     of the data, instruments, etc.
| |
| |-CATINFO.TXT         Description of the files in this directory.
| |
| |-DATASET.CAT         PDS catalog file describing this data set.
| |
| |-INST.CAT            Catalog file describing WFPC2.
| |
| |-INSTHOST.CAT        Catalog file describing HST.
| |
| |-MISSION.CAT         Catalog file describing the observing
| |                     campaign.
| |
| |-PERSON.CAT          Catalog file describing the personnel involved
| |                     in acquiring and archiving this data set.
| |
| |-REF.CAT             Catalog file containing relevant bibliographic
|                       references.
|
|-DOCUMENT/             A directory containing supplemental 
| |                     documentation.
| |                     
| |-x_REV8_RMS_PLOT.*   Plots of residuals of right ascension and 
| |                     declination for particular satelite 
| |                     provided in pdf and postscript versions. 
| |                     The x is replaced by the  satellite name.
| |                         
| |-x_REV8_RMS_PLOT.LBL Combined detached PDS labels for the above.
|
|
|-INDEX/                A directory containing indices of the files in
| |                     this data set.
| |
| |-INDXINFO.TXT        Description of the files in this directory.
| |
| |-INDEX.TAB           A table of all the data files on this volume.
| |
| |-INDEX.LBL           A detached PDS label for the above, defining
|                       each column in the table.
|
|-SPICE                 Directory containing SPICE ephemeris data for
| |                     the HST for the period covered in this data
| |                     set.
| |
| |-1990-01-01_2006-12-31.bsp  
| |                     SPICE SPKfile giving HST ephemeris data
| |                     that includes the observing period for 
| |                     this data set.
| |  
| |-1990-01-01_2006-12-31.LBL            
| |                     A detached PDS label for the above.
| |

5.  CITING THIS DATA SET

The following is the recommended information to include in a journal
citation of this data set:

  "French, R.G., C.A. McGhee, M. Frey, R. Hock, S. Rounds, R. 
  Jacobson, and A. Verbiscer, Saturn Satellite Astrometry with WFPC2: 
  1996-2005, HST-S/SA-WFPC2-4-ASTROM2005-V1.0, 
  USA_NASA_PDS_ASTROM_0101, NASA Planetary Data System, 2008."

6.  CONTACT INFORMATION

For questions concerning this volume set contact the PDS Rings Node:

  Mitch Gordon
  SETI INSTITUTE
  515 N. WHISMAN
  ROAD MOUNTAIN VIEW, CA 94043
  2765230341
  mgordon@seti.org

  or

  Mark Showalter
  SETI INSTITUTE
  515 N. WHISMAN ROAD
  MOUNTAIN VIEW, CA 94043
  650-604-3382
  mshowalter@seti.org
  http://pds-rings.seti.org/showalter.html

Additional assistance with the data archiving was provided by:

  Neil Heather
  SETI INSTITUTE
  515 N. WHISMAN ROAD
  MOUNTAIN VIEW, CA 94043
  nheather@seti.org

and

  Daniel Olson
  SETI Institute
  515 N. Whisman Rd
  Mountain View, Ca 94043
  dolson@seti.org

The Principal Investigator who carried out the observations archived
in this data set is:

  Richard G. French
  Astronomy Department
  Wellesley College
  Wellesley, MA 02481
  rfrench@wellesley.edu


======================================================================
ACKNOWLEDGMENTS
======================================================================

These data completed peer review in June 2008.  Members of the
peer review panel were: Nick Cooper, Mike Evans, Richard French, 
Robert Jacobson, Ron Joyner, Colleen McGhee.

======================================================================
DISCLAIMER
======================================================================

Although considerable care has gone into making this volume set,
errors are both possible and likely.  Users of the data are advised to
exercise the same caution as they would when dealing with any other
unknown data set.

Reports of errors or difficulties would be appreciated.  Please
contact Mitch Gordon or Mark Showalter at the addresses listed above.
