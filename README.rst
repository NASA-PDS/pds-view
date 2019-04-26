===================
 PDS Inspect Tool
===================

The PDS Inspect Tool provides support for visualizing PDS3 and PDS4 data
products.


Features
========

• Visualization of PDS3 data products
• Visualization of PDS4 data products
• Drag-and-drop of files for easy opening and exploration (on certain
  platforms only)


Installation
============

Prebuilt packages of the PDS Inspect Tool are available; simply download the
version relevant to your platform from the project's GitHub releases_ page
(click "Assets" under your chosen release version). Details for each platform
are given below.


Linux
-----

Download and unpack ``PDS-Inspect-Tool-linux.zip`` to extract the executable
file ``PDS-Inspect-Tool``; run this file. Note you'll need to be running the X
Window System. Depending on your window manager, you may be able to drag files
from the file explorer on your platform directly into the PDS Inspect Tool's
main window. Otherwise, choose "Open" from the tool's "File" menu to open PDS
data products.


macOS
-----

Download and unpack ``PDS-Inspect-Tool-macOS.zip`` to extract the executable
``PDS-Inspect-Tool.app`` which you can drag to your ``/Applications`` folder
or other convenient location. Note that because this is an unsigned_ program,
you will need to control-click (⌃+click) or right-click its icon and choose
"Open", followed by clicking the "Open" button, to start the PDS Inspect Tool.
The tool is known to work on the following versions of macOS:

• 10.13.6 "High Sierra"
• 10.14.3 "Mojave"
• 10.14.4 "Mojave"

Other releases may or may not work. In addition, drag-and-drop from Finder or
the desktop is not guaranteed to work on macOS at all. In this case, choosing
"Open" from the "File" menu is recommended.


Windows
-------

Two downloads are provided and tested on 64-bit Windows version 10. These versions are identical except as noted below:

• ``PDS-Inspect-Tool-Windows-single-exe.zip``. Downloading and unpacking this
  archive yields a single executable file ``PDS-Inspect-Tool.exe`` that you
  can double-click to launch. Note that it may take a *long* time for the tool
  to launch and its initial window to appear. *Pro*: single, easy to find
  ``.exe`` file. *Con*: Slow time to start the program.
• ``PDS-Inspect-Tool-Windows-dir.zip``. Downloading and unpacking this archive
  yields a directory of many files with a ``PDS-Inspect-Tool.exe`` file
  inside. Double-click this file to launch; the window will appear much
  faster. *Pro*: Fast start. *Con*: harder to find the ``.exe`` file in a
  directory littered with hundreds of other files.

Which one you use is a matter of preference. Regardless, drag-and-drop from
the Windows desktop or file Explorer works fine. (Other versions of Windows
are untested; we would love to hear back with your success stories.)


Other Platforms
---------------

For platforms not covered above, you can download the source code from the
releases_ page (click "Assets" under the appropriate release version) as
either a ``zip`` or a ``tar.gz`` archive. Your system will require the
following:

• `Python 2.7`_
• Qt4_
• PyQt4_
• `PDS4 Tools`_

See your system documentation or packaging manager to install these
dependencies. (A Python "virtualenv" is recommended to keep from polluting
your system Python with extraneous packages.) Once installed, you can run the
tool from the unpacked archive using the prepared Python.

For example, a Unix-like (including macOS-like) system could do the following::

    # == Install Python 2.7, Qt4, and PyQt4 per platform-specifics ==
    # (commands not shown here)
    # == Create a virtualenv sandbox and install PDS4 tools into it ==
    virtualenv --system-site-packages /tmp/mypython
    cd /tmp/mypthon
    bin/pip install http://pdssbn.astro.umd.edu/ftp/tools/readpds_python/1.0/PDS4_tools-1.0.zip
    # == Download and extract the PDS Inspect Tool ==
    curl -L 'https://github.com/NASA-PDS-Incubator/pds-inspect-tool/archive/v0.1-beta.tar.gz' | tar xzf -
    cd pds-inspect-tool-0.1-beta
    # == Install its dependencies ==
    ../bin/python setup.py develop
    # == Run it ==
    env MPLBACKEND=Qt4Agg ../bin/PDS-Inspect-Tool

Adjust the above commands as needed for your platform.


Documentation
=============

Additional documentation is available in the ``docs`` directory and at
https://pds-cm.jpl.nasa.gov/pds4/preparation/inspect/


Translations
============

This product has not been translated into any other languages than US English.


Contribute
==========

• Issue Tracker: https://github.com/NASA-PDS-Incubator/pds-inspect-tool/issues
• Source Code: https://github.com/NASA-PDS-Incubator/pds-inspect-tool


Support
=======

If you are having issues, please let us know.  You can reach us at
https://pds.nasa.gov/contact/contact.shtml


License
=======

The project is licensed under the Apache License, version 2.  See the
``LICENSE.txt`` file for details.



.. References:
.. _unsigned: https://support.apple.com/kb/ph25088?locale=en_US
.. _releases: https://github.com/NASA-PDS-Incubator/pds-inspect-tool/releases
.. _`Python 2.7`: https://www.python.org/downloads/
.. _Qt4: https://www.qt.io
.. _PyQt4: https://www.riverbankcomputing.com/software/pyqt/download
.. _`PDS4 Tools`: http://sbndev.astro.umd.edu/wiki/Python_PDS4_Tools
