To compile icons.qrc into icons.py.

This compiles the images for icons so they can be imported into the code to be 
displayed by the GUI.

pyrcc4 is the program to run to do this.  It comes with the PyQt or PySide installation
and is run in the following manner.

pyrcc4 /<path to icons.qrc>/icons.qrc -o /<path to destination of .py file>/icons.py

Then simply add:

import icons  and the new icons will be there.