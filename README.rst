=========
 PDSView
=========

PDSView provides support for visualizing PDS3 and PDS4 data products.


Features
========

• Visualization of PDS3 data products
• Visualization of PDS4 data products


Installation
============

Prebuilt packages of PDSView are available; simply download the version
relevant to your platform from the project's GitHub releases_ page (click
"Assets" under your chosen release version). Details for each platform are
given below.


Linux
-----

Download and unpack ``PDSView-linux.zip`` to extract the executable file
``PDSView``; run this file. Note you'll need to be running the X Window
System. Depending on your window manager, you may be able to drag files from
the file explorer on your platform directly into PDSView's main window.
Otherwise, choose "Open" from the tool's "File" menu to open PDS data
products.


macOS
-----

Download and unpack ``PDSView-macOS.zip`` to extract the executable
``PDSView.app`` which you can drag to your ``/Applications`` folder or other
convenient location. Note that because this is an unsigned_ program, you will
need to control-click (⌃+click) or right-click its icon and choose "Open",
followed by clicking the "Open" button, to start PDSView. The tool is known to
work on the following versions of macOS:

• 10.13.6 "High Sierra"
• 10.14.3–6 "Mojave"

Other releases may or may not work. In addition, drag-and-drop from Finder or
the desktop is not guaranteed to work on macOS at all. In this case, choosing
"Open" from the "File" menu is recommended.


Windows
-------

Two downloads are provided and tested on 64-bit Windows version 10. These
versions are identical except as noted below:

• ``PDSView-Windows-single-exe.zip``. Downloading and unpacking this archive
  yields a single executable file ``PDSView.exe`` that you can double-click to
  launch. Note that it may take a *long* time for the tool to launch and its
  initial window to appear. *Pro*: single, easy to find ``.exe`` file. *Con*:
  Slow time to start the program.
• ``PDSView-dir.zip``. Downloading and unpacking this archive yields a
  directory of many files with a ``PDSView.exe`` file inside. Double-click
  this file to launch; the window will appear much faster. *Pro*: Fast start.
  *Con*: harder to find the ``.exe`` file in a directory littered with
  hundreds of other files.

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
    bin/pip install matplotlib seaborn
    # == Download and extract PDSView ==
    curl -L 'https://github.com/NASA-PDS-Incubator/pds-view/archive/v0.1-beta.tar.gz' | tar xzf -
    cd pds-view-0.1-beta
    # == Install its dependencies ==
    ../bin/python setup.py develop
    # == Run it ==
    env MPLBACKEND=Qt4Agg ../bin/PDSView

Adjust the above commands as needed for your platform.


Documentation
=============

Additional documentation is available in the ``docs`` directory and at
https://pds-cm.jpl.nasa.gov/pds4/preparation/inspect/ … note this is only
available within the Jet Propulsion Laborary.


Translations
============

This product has not been translated into any other languages than US English.


Contribute
==========

• Issue Tracker: https://github.com/NASA-PDS-Incubator/pds-view/issues
• Source Code: https://github.com/NASA-PDS-Incubator/pds-view


Support
=======

If you are having issues, please let us know.  You can reach us at
https://pds.nasa.gov/contact/contact.shtml


License
=======

The project is licensed under the Apache License, version 2.  See the
``LICENSE.txt`` file for details.
