===================
 PDS Inspect Tool
===================


PDS Inspect Tool

The PDS Inspect Tool provides support for visualizing PDS3 and PDS4 data
products.

The documentation including release notes, installation and operation
of the software should be online at:

http://pds-cm.jpl.nasa.gov/pds4/preparation/inspect/

In order to create a complete distribution package, execute the
following commands:

% python setup.py bdist

The distribution can then be found in the 'dist' directory.

In order to create installers compatible with certain platforms, see below.


Platform Specific Installations
===============================

You can create installers for the PDS Inspect Tool for specific platforms.
To do so, you will need to build the installer on the target platform. In
other words, you can't build the Windows installer on a Linux box.


Requirements
------------

Regardless of platform, the requirements are the same. First, install Python
2.7 with Tkinter support. Then install PyQt4 into the Python 2.7 installation.
Then, make a virtualenv from *that* installation, letting the virtualenv
have access to PyQt4 by passing ``--system-site-packages``::

    virtualenv --system-site-packages /tmp/mypython  (macOS, Linux, other Unix-like)
    virtualenv.exe --system-site-packages \tmp\mypython  (Windows)

(If your Python 2.7 installation didn't come with ``vitualenv``, you may have
to install it separately.) Inside that virutalenv, install ``matplotlib``,
``seaborn``, and PDS4_tools-1.0; on macOS, Linux, or other Unix-like systems::

    cd /tmp/mypython
    bin/pip install matplotlib seaborn
    bin/pip install http://pdssbn.astro.umd.edu/ftp/tools/readpds_python/1.0/PDS4_tools-1.0.zip

On Windows::

    cd \tmp\mypython
    Scripts\pip.exe install matplotlib seaborn
    Scripts\pip.exe install http://pdssbn.astro.umd.edu/ftp/tools/readpds_python/1.0/PDS4_tools-1.0.zip


Building Out
------------

Building the platform-specific installers leverages Buildout_.  The Buildout
bootstrapper script, ``boostrap.py`` is included with the PDS Inspect Tool
source.  Simply do the following (adjusting paths and platform-specifics as
needed):

1. Use the Python from your vitualenv to bootstrap:
   ``/tmp/mypython/bin/python2.7 bootstrap.py``
2. Build out: ``bin/buildout``

Then run the platform-specific installation:

For Windows
    ``bin/buildout install windows``. This will create
    ``dist/PDS-Inspect-Tool.exe`` which Windows users can double-click
    to run.
For macOS
    ``bin/buildout install macos``. This will create
    ``dist/PDS-Inspect-Tool.app`` which macOS users can double-click
    to run.
For Linux
    ``bin/buildout install linux``. This will create
    ``dist/PDS-Inspect-Tool`` which Linux users can run.




.. _Buildout: https://buildout.org/
