# View.py - View class presents the data items from a model on the user screeen
import os
import platform
import types
import csv
import io
import sys
import re
from collections import OrderedDict
import textwrap

import sip
sip.setapi('QVariant',2)
from PyQt4.QtCore import *
from PyQt4.QtGui import *

import numpy as np

import matplotlib as mpl
from matplotlib.figure import Figure
import matplotlib.pyplot as plt
from matplotlib.backends.backend_qt4agg import (
    FigureCanvasQTAgg as FigureCanvas,
    NavigationToolbar2QT as NavigationToolbar)
# Safe import of PowerNorm (available in MPL v1.4+)
try:
    from matplotlib.colors import PowerNorm

except ImportError:
    PowerNorm = None


from pds4_tools import pds4_read
from pds4_tools.reader.table_objects import Meta_TableStructure
import Model
import Delegate
#import Styles
import icons



MAC = "qt_mac_set_native_menubar" in dir()
__version__ = "1.0.1"


class MainWindow(QMainWindow):

    supportedFileFormats = ['.xml']

    font = QFont()
    font.setPointSize(15)
    font.setFamily('Arial')

    # static variables used by different classes
    hold_position_checked = False
    style_group = 'Default'  # This is used to control the color of groups in the various styles

    def __init__(self, parent=None):
        super(MainWindow, self).__init__(parent)

        self.filename = None
        self.title = ''
        self.index = None
        self.model = None
        self.image = None
        self.image_data = None
        self.cubeData = False
        self.x_tick_visible = True
        self.y_tick_visible = True
        self.color_bar_orientation = 'horizontal'
        self._norm = mpl.colors.Normalize(clip=False)   # Linear normalization
        self._interpolation = 'none'                    # default to no interpolation on images

        self.threeDTabInPlace = False
        self.table_num = 0    # This is used for table arrays and image arrays (e.g 3D Spectral Data)

        self.logo = QLabel()
        self.logo.setPixmap(QPixmap("./Icons/PDS_Inspector_LOGO.png"))
        self.setCentralWidget(self.logo)
        self.setAcceptDrops(True)
        self.platform = 'booboo'

        # determine operating system:
        if sys.platform.startswith('darwin'):
            self.default_theme = "Macintosh"
            self.platform = 'Mac'
        elif sys.platform.startswith('win'):
            self.default_theme = 'Windows'
            self.platform = 'Windows'
        elif sys.platform.startswith('linux'):
            self.default_theme = "Plastique"
            self.platform = 'Linux'

        # This returns a list of all the available themes/styles for the OS
        # It will be used in the setTheme callback to only enable those items in the list
        qlist = QStyleFactory.keys()
        self.theme_list = str(qlist.join(" ")).split(" ")

        self.styleData = ''

        #self.setStyleSheet(self.styleData)
        self.setStyleSheet('')

        # connect to Summary table to recieve a View button clicked signal
        self.connect(self, SIGNAL("SummaryTableTriggered"), self.handleViewClicked)

        # connect to Table widget to recieve row and column information
        self.connect(self, SIGNAL("GetPositionTriggered"), self.handleGetPosition)

        self.tabFrame = Tab()

        self.tabFrame.setEnabled(True)
       # self.tabFrame.setGeometry(10,10,555,550)

        # add tabs
        self.labelTab = QWidget()
        self.tableTab = QWidget()
        self.threeDTableTab = QWidget()
        self.imageTab = QWidget()

        self.tabFrame.addTab(self.labelTab, "Label  ")

#        if self.tabFrame.popOutFlag:
#            self.tabFrame.tBar.setTabButton(0, QTabBar.RightSide, self.tabFrame.popOutButton)
#            self.tabFrame.popOutFlag = False

#        else:
#            self.tabFrame.tBar.setTabButton(0, QTabBar.RightSide, self.tabFrame.popInButton)
#            self.tabFrame.popOutFlag = True

        self.tabFrame.addTab(self.tableTab, "Table  ")
        self.tabFrame.addTab(self.imageTab, "Image  ")

        self.labelLayout = QHBoxLayout()
        #self.tableLayout = QFormLayout()

        self.tableLayout = QVBoxLayout()
        self.tableInfoLayout = QHBoxLayout()

        self.threeDTableLayout = QGridLayout()
        #self.threeDTableInfoLayout = QHBoxLayout()

        self.imageLayout = QVBoxLayout()
        self.imageInfoLayout = QHBoxLayout()

        self.tabFrame.setTabOrder(self.labelTab.focusProxy(), self.tableTab.focusProxy())
        self.tabFrame.setMovable(False)

        # This holds the Summary
        self.winLabel = QLabel()

        self.winLabel.setMinimumSize(500, 60)
        self.winLabel.setAlignment(Qt.AlignCenter)
        self.winLabel.setContextMenuPolicy(Qt.ActionsContextMenu)

        self.summaryDockWidget = QDockWidget(self.title, self)
        self.summaryDockWidget.setObjectName("summaryDockWidget")
        self.summaryDockWidget.setAllowedAreas(Qt.RightDockWidgetArea)
        self.summaryDockWidget.setFloating(False)

        # Add a placeholder for the summary and hide it
        self.holder = QWidget()
        self.holder.setMinimumSize(200,150)
        self.summaryDockWidget.setWidget(self.holder)
        self.addDockWidget(Qt.RightDockWidgetArea, self.summaryDockWidget)
        self.summaryDockWidget.hide()

        self.sizeLabel = QLabel()
        self.sizeLabel.setFrameStyle(QFrame.StyledPanel|QFrame.Sunken)
        status = self.statusBar()
        status.setSizeGripEnabled(False)
        status.addPermanentWidget(self.sizeLabel)
        status.showMessage("Ready", 5000)

        self.fileOpenAction = self.createAction("&Open...", self.fileOpen,
                QKeySequence.Open,  icon="fileopen", tip="Open a PDS file")
        self.fileSaveAsAction = self.createAction("Save &As...", self.fileSaveAs,
                icon="filesaveas", tip="Save the image using a new name")

        self.fileSaveAsAction.setDisabled(True)

        self.fileQuitAction = self.createAction("&Quit..", self.close,
                "Ctrl+Q", icon="filequit", tip="Close the application")


        fileMenu = self.menuBar().addMenu("&File")

        self.addActions(fileMenu, (self.fileOpenAction, None, self.fileSaveAsAction,
                                None, self.fileQuitAction))

        # Add the 'Label Options' menu bar items
        self.labelMenu = self.menuBar().addMenu("Label Options")
        labelOptionsGroup = QActionGroup(self, exclusive=True)
        self.label_options = ('Object Label', 'Full Label', 'separator', 'Identification Area', 'Observation Area',
                              'Discipline Area','Mission Area','separator','Display Settings', 'Spectral Characteristics','separator','File info')
        self.labelActionList = {}
        for label in self.label_options:
            if label == 'separator':
                self.labelMenu.addSeparator()
            else:
                self.labelActionList[label] = self.createAction(label, self.selectLabel,
                                     icon='Display ' + label, tip='Display ' + label,
                                     checkable=True, group_action=True)
                labelGroupAction = labelOptionsGroup.addAction(self.labelActionList[label])
                self.labelMenu.addAction(labelGroupAction)

        self.labelActionList['Object Label'].setChecked(True)


        # Add the 'Color Maps' menu bar items
        self.invert_colormap = False

        self.imageMenu = self.menuBar().addMenu("Color Maps")

        viewBasicColorGroup = QActionGroup(self, exclusive=True)
        self.colorMaps = ('gray', 'Reds', 'Greens', 'Blues', 'hot', 'afmhot', 'gist_heat', 'cool', 'coolwarm',
                     'jet', 'rainbow', 'hsv', 'Paired', 'separator', "current selection in 'All'")

        self.colorActionList = {}

        for color in self.colorMaps:
            if color == 'separator':
                self.imageMenu.addSeparator()
            else:
                self.colorActionList[color] = self.createAction(color, self.selectColorMap,
                     icon='show'+color, tip='Display '+color+' in image', checkable=True, group_action=True)
                groupAction = viewBasicColorGroup.addAction(self.colorActionList[color])
                self.imageMenu.addAction(groupAction)

        self.colorActionList['gray'].setChecked(True)   # note: ...setDisabled(True) can be used individually

        self.imageMenu.addSeparator()

        # add an all colormaps pulldown

        allSubMenu = self.imageMenu.addMenu("All")

        self.actionListAll = {}
        viewAllColorGroup = QActionGroup(self, exclusive=True)
        self.allColormaps = sorted(m for m in plt.cm.datad if not m.endswith("_r"))
        for color in self.allColormaps:
            self.actionListAll[color] = self.createAction(color, self.selectColorMap,
                                        icon='show' + color, tip='Display ' + color + ' in image',
                                        checkable=True, group_action=True)
            groupActionAll = viewAllColorGroup.addAction(self.actionListAll[color])
            allSubMenu.addAction(groupActionAll)

        self.actionListAll['gray'].setChecked(True)

        self.imageMenu.addSeparator()

        # add and Invert choice to the menu
        self.invertColorAction = self.createAction("Invert", self.invertColorMap,
                             shortcut="Ctrl+I",icon="invertColormap", tip="Invert the color", checkable=True)

        self.invertColorAction.setChecked(False)

        self.imageMenu.addAction(self.invertColorAction)

        # 'Image Options' menu item
        self.interpolationActionDict = {}
        self.normalizationActionDict = {}

        self.imageOptionsMenu = self.menuBar().addMenu("Image Options")

        # Interpolation
        interpolationSubMenu = self.imageOptionsMenu.addMenu("Interpolation method")
        interpolationGroup = QActionGroup(self, exclusive=True)
        interpolation_options =  ('none', 'nearest', 'bilinear', 'bicubic', 'spline16', 'spline36',
                                  'hanning', 'hamming', 'hermite', 'kaiser', 'quadric', 'catrom',
                                  'gaussian', 'bessel', 'mitchell', 'sinc', 'lanczos')
        for mode in interpolation_options:
            self.interpolationActionDict[mode] = self.createAction(mode, self.setInterpolation,
                                                icon="InterpolationMode",
                                                tip="Select Interpolation Mode", checkable=True,
                                                group_action=True)
            interpolation_actions = interpolationGroup.addAction(self.interpolationActionDict[mode])
            interpolationSubMenu.addAction(interpolation_actions)

        self.interpolationActionDict['none'].setChecked(True)

        self.imageOptionsMenu.addSeparator()

        # Normalization
        normalizationSubMenu = self.imageOptionsMenu.addMenu("Normalize")
        normalizationGroup = QActionGroup(self, exclusive=True)
        norm_options = ('Linear', 'Logarithmic', 'Squared', 'Square Root')
        for mode in norm_options:
            self.normalizationActionDict[mode] = self.createAction(mode, self.setNormalization,
                                                                   icon="NormalizationMode",
                                                                   tip="Select Normalization Mode", checkable=True,
                                                                   group_action=True)
            normalization_actions = normalizationGroup.addAction(self.normalizationActionDict[mode])
            normalizationSubMenu.addAction(normalization_actions)

        self.normalizationActionDict['Linear'].setChecked(True)


        # 'View' menu item
        self.colorbarActionDict = {}
        self.tickActionDict = {}
        self.viewMenu = self.menuBar().addMenu("View")

        colorbarSubMenu = self.viewMenu.addMenu("Colorbar")
        self.cb_removed = False
        colorbarGroup = QActionGroup(self, exclusive=True)
        colorbar_options = ('Horizontal', 'Vertical', 'Hide')
        for cb in colorbar_options:
            self.colorbarActionDict[cb] = self.createAction(cb, self.setColorbar, icon="colorbarOrientation",
                                                       tip="Toggle Colorbar Orientataion", checkable=True, group_action=True)
            cb_actions = colorbarGroup.addAction(self.colorbarActionDict[cb])
            colorbarSubMenu.addAction(cb_actions)

        self.colorbarActionDict['Horizontal'].setChecked(True)

        self.viewMenu.addSeparator()

        tickSubMenu = self.viewMenu.addMenu("Ticks")

        tickGroup = QActionGroup(self, exclusive=True)
        tick_options = ('Show', 'Hide','Show X-Tick', 'Show Y-Tick')
        for tick in tick_options:
            self.tickActionDict[tick] = self.createAction(tick, self.setTicks, icon="hideShowTicks",
                                                          tip="Toggle Ticks Visibility", checkable=True,
                                                          group_action=True)
            tick_actions = tickGroup.addAction(self.tickActionDict[tick])
            tickSubMenu.addAction(tick_actions)

        self.tickActionDict['Show'].setChecked(True)
        self.x_tick_visible = False
        self.y_tick_visible = False

        # Disable image functionality until an image is rendered
        self.imageMenu.setDisabled(True)
        self.viewMenu.setDisabled(True)
        self.imageOptionsMenu.setDisabled(True)

        self.viewMenu.addSeparator()

        # Disable until the 'View' button is pushed
        self.labelMenu.setDisabled(True)

        # Add the 'Styles' menu bar items

        self.stylesActionList = {}
        self.layoutActionList = {}

        self.displayStylesMenu = self.menuBar().addMenu("Display Styles")
        stylesSubMenu = self.displayStylesMenu.addMenu("Styles")
        stylesGroup = QActionGroup(self, exclusive=True)
        self.styles_options = ('Default','Dark Orange')
        for style in self.styles_options:
            if style == 'separator':
                self.stylesMenu.addSeparator()
            else:
                self.stylesActionList[style] = self.createAction(style, self.setStyle,
                                     icon='Set Style: ' + style, tip='Set Style ' + style,
                                     checkable=True, group_action=True)
                stylesGroupAction = stylesGroup.addAction(self.stylesActionList[style])
                stylesSubMenu.addAction(stylesGroupAction)

        self.stylesActionList['Default'].setChecked(True)

        self.displayStylesMenu.addSeparator()

        # Layouts
        layoutSubMenu = self.displayStylesMenu.addMenu("Layouts")
        layoutGroup = QActionGroup(self, exclusive=True)
        layout_options = ("Macintosh", "Windows", "Plastique", "CDE", "Cleanlooks", "Motif", "sgi")
        for layout in layout_options:
            if layout == 'separator':
                self.displayStylesMenu.addSeparator()
            else:
                self.layoutActionList[layout] = self.createAction(layout, self.setLayout,
                                                                 icon='Set Layout: ' + layout, tip='Set Layout ' + layout,
                                                                 checkable=True, group_action=True)
                layoutGroupAction = layoutGroup.addAction(self.layoutActionList[layout])
                layoutSubMenu.addAction(layoutGroupAction)

        for i in layout_options:
            if i not in self.theme_list:
                self.layoutActionList[i].setDisabled(True)

        self.layoutActionList[self.default_theme].setChecked(True)
        self.layoutActionList['sgi'].setDisabled(True)

        self.initalGrayScaleSetting()
        self.setWindowTitle("PDS Inspect Tool")
        self.setWindowIcon(QIcon("./Icons/MagGlass.png"))


    def setLayout(self, layout):
        ret = QApp.app.setStyle(layout)
        if ret == None:
            print('Could not find a theme that should be displaying')

    def setInterpolation(self, mode):
        self._interpolation = mode
        self.draw2dImage(None, redraw=True)

    def setNormalization(self, option):
        print option
        if option == 'Linear':
            self._norm = mpl.colors.Normalize(clip=False)
        elif option == 'Logarithmic':
            self._norm = mpl.colors.LogNorm()
        elif option == 'Squared':
            self._norm = PowerNorm(gamma = 2.0)
        elif option == 'Square Root':
            self._norm = PowerNorm(gamma = 0.5)
        else:
            print('Option out of range')
        self.draw2dImage(None, redraw=True)

    def setColorbar(self, option):
        if not self.cb_removed:
            self.cbar.remove()
        if option == 'Horizontal':
            self.color_bar_orientation = 'horizontal'
            self.cb_removed = False
        elif option == 'Vertical':
            self.color_bar_orientation = 'vertical'
            self.cb_removed = False
        elif option == 'Hide':
            self.color_bar_orientation = 'hide'
            self.cb_removed = True
        self.renderImage(peripheral='ColorBar')
        #self.draw2dImage(None, redraw=True)

    def setTicks(self, selection):
        if selection == 'Show':
            self.x_tick_visible = True
            self.y_tick_visible = True
        elif selection == 'Hide':
            print('HideTicks')
            self.x_tick_visible = False
            self.y_tick_visible = False
        elif selection == 'Show X-Tick':
            self.x_tick_visible = True
            self.y_tick_visible = False
        elif selection == 'Show Y-Tick':
            self.x_tick_visible = False
            self.y_tick_visible = True
        self.renderImage(peripheral='Ticks')


    def setStyle(self, style):
        MainWindow.style_group = style
        if style == 'Default':
            style = ''
        elif style == 'Dark Orange':
            f = open('./styleSheets/darkorange.stylesheet', 'r')
            style = f.read()
            f.close
        else:
            pass
        self.setStyleSheet(style)

        # If there are groups in the table type then color them a
        # different color so they stand out.
        possible_groups = Delegate.possible_groups(self.table_type)
        if possible_groups:
            print("GROUPS")
            self.tableWidget.set_group_colors()
          #  ItemTable.set_group_colors_static(self.tableWidget)
        else:
            print("NO GROUPS")


    def selectLabel(self, label):
        self.drawLabel(self.current_index, self.view_type, label)


    def selectColorMap(self, colorMap):
        self.cmap = colorMap
        if self.invertColorAction.isChecked():
            self.cmap += '_r'
            print('cmap: {}'.format(self.cmap))
        else:
            self.cmap = colorMap
            print('cmap: {}'.format(self.cmap))
        if colorMap in self.allColormaps:
            self.colorActionList["current selection in 'All'"].setDisabled(False)
            self.actionListAll[colorMap].setChecked(True)
        if colorMap not in self.colorMaps:
            self.colorActionList["current selection in 'All'"].setChecked(True)
        else:
            self.colorActionList["current selection in 'All'"].setDisabled(True)
        self.clearLayout(self.imageLayout)
        self.clearLayout(self.imageInfoLayout)
        self.draw2dImage(None, redraw=True)

    def invertColorMap(self):
        # This test is necessary to turn off the inversion of the current selection
        if '_r' in self.cmap:
            self.cmap = self.cmap[:-2]
        self.selectColorMap(self.cmap)

    def initalGrayScaleSetting(self):
        #This is called to reset the checkboxes when a new file is opened.
        self.invertColorAction.setChecked(False)
        self.colorActionList['gray'].setChecked(True)
        self.cmap = 'gray'
        self.colorActionList["current selection in 'All'"].setDisabled(True)

    def createAction(self, text, slot=None, shortcut=None, icon=None, tip=None,
                     checkable=False, group_action=False, signal="triggered()"):
        action = QAction(text, self)
        if icon is not None:
            action.setIcon(QIcon(":/{}.png".format(icon)))
        if shortcut is not None:
            action.setShortcut(shortcut)
        if tip is not None:
            action.setToolTip(tip)
            action.setStatusTip(tip)
        if slot is not None:
            # Need to pass a parameter to the callback for a group action as in (View options)
            if group_action:
                self.connect(action, SIGNAL(signal), lambda:slot(text))
            else:
                self.connect(action, SIGNAL(signal), slot)
        if checkable:
            action.setCheckable(True)
        return action


    def addActions(self, target, actions):
        for action in actions:
            if action is None:
                target.addSeparator()
            else:
                target.addAction(action)

    # Sets defaults for a new file
    def set_defaults(self):
        # clear all the tab layouts and select the label tab
        self.clearLayout(self.labelLayout)
        self.clearLayout(self.tableLayout)
        self.clearLayout(self.tableInfoLayout)
        self.clearLayout(self.threeDTableLayout)
        self.clearLayout(self.imageLayout)
        self.clearLayout(self.imageInfoLayout)
        self.initalGrayScaleSetting()
        self._norm = mpl.colors.Normalize(clip=False)  # Linear normalization
        self._interpolation = 'none'
        self.labelActionList['Object Label'].setChecked(True)
        self.colorActionList['gray'].setChecked(True)
        self.actionListAll['gray'].setChecked(True)
        self.normalizationActionDict['Linear'].setChecked(True)
        self.tickActionDict['Show'].setChecked(True)
        self.colorbarActionDict['Horizontal'].setChecked(True)
        self.interpolationActionDict['none'].setChecked(True)


    # This method call the QFileDialog to get allow selection of a file to open
    def fileOpen(self):
        self.set_defaults()
        # Open File from menu bar
        filename = QFileDialog.getOpenFileNames(self, "Select PDS File")
        if filename:
            fname = map(str, filename)
            self.openSummaryGUI(fname[0])
            self.imageMenu.setDisabled(True)  # Disable image functionality until an image is rendered
            self.viewMenu.setDisabled(True)
            self.imageOptionsMenu.setDisabled(True)
            self.labelMenu.setDisabled(True)  # Disable until the 'View' button is pushed


    def fileSave(self):
        print('Not finished yet')

    #    if self.image == None:
    #        return True
    #    if self.filename is None:
    #        return self.fileSaveAs()
    #    else:
    #        fname, ext = self.filename.split('.')


    def fileSaveAs(self):
       # print('Not finished yet')
       # return

       # if self.image == None:
       #     return True
        fname = self.filename if self.filename is not None else "."
        formats = (["*.{}".format(format.data().decode("ascii").lower())
                    for format in QImageWriter.supportedImageFormats()])
        fname = QFileDialog.getSaveFileName(self,
                                            "Image Changer - Save Image", fname,
                                            "Image files ({})".format(" ".join(formats)))
        if fname:
            if "." not in fname:
                fname += ".png"
            self.filename = fname
            print('FILENAME: {}'.format(self.filename))
            return self.fileSave()
        return False

    def closeEvent(self, event):
        settings = QSettings()
        settings.setValue("LastFile", self.filename)
        settings.setValue("RecentFiles", self.recentFiles or [])
        settings.setValue("MainWindow/Geometry", self.saveGeometry())
        settings.setValue("MainWindow/State", self.saveState())


    def popInOut(self, tab):
        print("Test..")
        self.tab.Out.emit(self)
        print("ing")

    # for dropping a file onto the main logo window
    def dragEnterEvent(self, event):
        if event.mimeData().hasUrls():
            event.accept()
        else:
            event.ignore()

    def dragMoveEvent(self, event):
        if event.mimeData().hasUrls:
            event.setDropAction(Qt.CopyAction)
            event.accept()
        else:
            event.ignore()

    def dropEvent(self, event):
        print("dropping a file.")
        for url in event.mimeData().urls():
            path = url.toLocalFile().toLocal8Bit().data()
            if os.path.isfile(path):
                self.set_defaults()
                self.openSummaryGUI(path)
                self.imageMenu.setDisabled(True)  # Disable image functionality until an image is rendered
                self.viewMenu.setDisabled(True)
                self.imageOptionsMenu.setDisabled(True)
                self.labelMenu.setDisabled(True)  # Disable until the 'View' button is pushed
                #print(path)
        else:
            event.ignore()


    def closeEvent(self, event):
        event.accept()


    def getType(self, index, viewType):
        i = index.row()
        return viewType[i]

    def handleViewClicked(self, index):
        '''
        This is the call back for the "View" button  being selected in the Summary area
        It will draw the label and the associated talbe for the selection
        :param index: a QModelIndex used internally by the model use .row() to get which button is pushed
        :return:
        '''
        self.clearLayout(self.imageLayout)
        # self.summary[1] is a list of all the 'Name' fiels in the Summary
        viewType = self.getType(index, self.summary[1])

        self.labelMenu.setDisabled(False)
        self.drawLabel(index, viewType)
        self.current_index = index  # These are used when label options change within the same file
        self.view_type = viewType
        self.test_for_label()

      #  print('%%%%%%%%%%%%%%%')
      #  print(viewType)


        if viewType == 'Header':
            self.tabFrame.setTabEnabled(1, False)
            self.tabFrame.setTabEnabled(2, False)
            self.tabFrame.setTabEnabled(3, False)
            self.imageMenu.setDisabled(True)
            self.viewMenu.setDisabled(True)
            self.imageOptionsMenu.setDisabled(True)
            self.tabFrame.setCurrentIndex(0)    # open up in the label tab
        elif viewType == 'Array_2D_Image':
            self.tabFrame.setTabEnabled(1, True)
            self.tabFrame.setTabEnabled(2, True)
            self.tabFrame.setTabEnabled(3, True)
            self.drawTable(index)
            self.clearLayout(self.imageLayout)
            self.clearLayout(self.imageInfoLayout)
            self.draw2dImage(None,redraw=False)
            if self.cubeData:
                self.tabFrame.setCurrentIndex(2)  # account for the 3D Table tab
            else:
                self.tabFrame.setCurrentIndex(1)    # open up in the table tab
        elif viewType == 'Array_3D_Spectrum':
            self.drawTable(index)
            self.tabFrame.setTabEnabled(1, True)
            self.tabFrame.setTabEnabled(2, True)
            self.tabFrame.setTabEnabled(3, True)
            self.tabFrame.setCurrentIndex(1)
            self.tabFrame.setCurrentIndex(1)  # open up in the table tab
            self.cubeData = True
        else:  # Table Data with no images
            self.imageMenu.setDisabled(True)
            self.viewMenu.setDisabled(True)
            self.imageOptionsMenu.setDisabled(True)
            self.tabFrame.setTabEnabled(1, True)
            self.tabFrame.setTabEnabled(2, False)
            self.tabFrame.setTabEnabled(3, False)
            self.drawTable(index)
            self.tabFrame.setCurrentIndex(1)  # open up in the table tab


    def test_for_label(self):
        # This is called everytime a view is changed from the Summary GUI area
        # It enables the label areas that are present for that view, and disables those that are not.
        for label in self.label_options:
            if label == 'separator':
                continue
            ret = self.model.get_label(self.current_index.row(), label)
            if ret == None:
                self.labelActionList[label].setDisabled(True)
            else:
                self.labelActionList[label].setDisabled(False)

    def drawLabel(self, index, viewType, labelType = 'Object Label'):
        '''
        This the method that displays the Label information
        It passes the label information to 'labelWidget' which is then added to the 'labelLayout' which
        is drawn to the labelTab
        :param index: a QModelIndex used internally by the model
        :param viewType: the type of object (described in the object label)
        :param labelType: the part of the label to display
        :return:
        '''
        if labelType == None:
            self.labelActionList[labelType].setDisabled(True)
        label = self.model.get_label(index.row(), labelType)
        label_dict = label.to_dict()
        print(labelType)
        labelWidget = LabelFrame(label_dict, self.dimension, index, viewType, labelType)
        self.clearLayout(self.labelLayout)
        self.labelLayout.addWidget(labelWidget)
        self.labelTab.setLayout(self.labelLayout)

    def handleGetPosition(self, position):
        '''

        :param position: position of the cursor in the table
        :return:
        '''
        self.rowDisplay.setEnabled(True)
        self.columnDisplay.setEnabled(True)
        # refresh the widgets
        self.rowDisplay.clear()
        self.columnDisplay.clear()
        # Write the coordinates
        self.rowDisplay.insert(str(position[0]))
        self.columnDisplay.insert(str(position[1]))
        # Disable the widgets so they can not be editted
        self.rowDisplay.setEnabled(False)
        self.columnDisplay.setEnabled(False)


    def drawTableArray(self, dimension, table):
        '''
        This method sets up the buttons for is for the '3D Table' tab.  The buttons are to select individual bands
        in a Spectral_Cube_Object.  Clicking a button will update the 'Table' and 'Image' tabs.
        :param dimension: the number of file to make buttons for
        :param table:
        :return:
        '''

        #print("DIMENSION: {}".format(dimension))
        self.num_tables = dimension
        #Add the 3D table tab then move the other tabs to accommodate the 2nd position
        self.tabFrame.addTab(self.threeDTableTab, "3D Table")
        self.tabFrame.setMovable(True)
        self.tabFrame.tabBar().moveTab(1,2)
        self.tabFrame.tabBar().moveTab(1,2)
        self.tabFrame.tabBar().moveTab(3,1)
        self.tabFrame.setMovable(False)
        self.threeDTabInPlace = True
        self.clearLayout(self.threeDTableLayout)
        buttons = {}

        len_last_row = dimension % 10
        rows = dimension/10
        count = 1
        # Lay out the button
        for i in range(rows):
            for j in range(10):
                # make rows of buttons
                buttons[i,j] = QPushButton('Band {}'.format(count))
                # This give the button a unique name used in the callback to determine which button was clicked
                buttons[i,j].setObjectName('%d' % count)
                self.threeDTableLayout.addWidget(buttons[(i,j)], i,j)
                buttons[i, j].clicked.connect(lambda: self.handle3DTableButtonClicked(table))
                count += 1
        # Pick up the left over buttons
        for j in range(len_last_row):
            buttons[rows+1, j] = QPushButton('Band {}'.format(count))
            buttons[rows+1, j].setObjectName('%d' % count)
            self.threeDTableLayout.addWidget(buttons[rows+1,j], rows+1, j)
            buttons[rows+1, j].clicked.connect(lambda: self.handle3DTableButtonClicked(table))
            count += 1
        self.tabFrame.setTabEnabled(3, True)
        self.threeDTableLayout.SetFixedSize
        self.threeDTableLayout.setSpacing(15)
        self.threeDTableLayout.setVerticalSpacing(15)
        self.threeDTableLayout.setSizeConstraint(QLayout.SetFixedSize)
        self.threeDTableTab.setLayout(self.threeDTableLayout)

    def handle3DTableButtonClicked(self, data):
        self.full_table = data  # This is used in a callback for next and previous tables
     #   try:
        sending_button = self.sender()
        button_number = int(sending_button.objectName())
        print('Button number: {}'.format(button_number))
        #set up the table for rendering

        self.drawIndexedTable(data, button_number-1)
        self.tabFrame.setCurrentIndex(2)
       # except:
       #     print("3D table array button click did not work")

    def drawIndexedTable(self,table,index):
        print('INDEXED')
        # TODO This is assuming that all the tables are the same size and the same type
        # Pretty lame assumption, have to figure out how to get to each one
        row_col = self.dimension[1].split('X')
        num_rows = int(row_col[0])
        num_columns = int(row_col[1])
        self.table_num = index

        # TODO Need to study the GroupTest.py to make sure I access all I need properly
        table_type = self.summary[1][1]  # this is the type of each array

        title = self.summary[0][0]  # this is the title of the 3D table
        print(table_type)

        self.tableWidget = ItemTable(self, table[index], title, num_rows, num_columns, self.table_type)

        # If this is not cleared another table will be added
        # Will allow a button that allows multiple tables to be drawn and undocked for comparison
        self.clearLayout(self.tableLayout)

        self.renderTable(title, self.table_num)
        self.drawIndexedImage(table, index)

    def drawIndexedImage(self,table,index):
      #  print('INDEXED Image')
        table_type = self.summary[1][1]  # this is the type of each array
        if table_type == 'Array_2D_Image':
            self.clearLayout(self.imageLayout)
            self.clearLayout(self.imageInfoLayout)
            self.draw2dImage(self.full_table, index, redraw=False)

        title = self.summary[0][0]  # this is the title of the 3D table


    def drawTable(self, index):
        '''
        :param index:
        :return:
        '''
        self.table, self.title, dimension, self.table_type, self.table_name = self.model.get_table(index)
        if dimension == (0,0):
            self.tableTab.setDisabled(True)
        else:
            self.tableTab.setDisabled(False)
            # If we have an 'Array_3D_Specturm" we have to make a new tab with a button for each element
            if self.table_type == 'Array_3D_Spectrum':
                self.drawTableArray(dimension[0], self.table)
                return
            num_rows    = dimension[0]
            num_columns = dimension[1]
            self.tableWidget = ItemTable(self, self.table, self.title, num_rows, num_columns, self.table_type)

            print('TITLE : {}'.format(self.title))
         #
            self.clearLayout(self.tableLayout)
            self.renderTable(self.table_name)
           # self.tableWidget.resizeTable()


    def renderTable(self, title, table_num = -1):
        '''
        :param title: The name of title taken from the Label
        :param table_num: Used only idenfitying and rendering individual tables in 3D Spectrum data
        :return:
        '''
        # Add row, column and Title information above the table
        self.undocked_title = title
        self.table_num = table_num
        rowLabel = QLabel('Row ')
        rowLabel.setSizePolicy(QSizePolicy.Fixed, QSizePolicy.Fixed)

        self.rowDisplay = QLineEdit()
        self.rowDisplay.setMaxLength(7)

        self.rowDisplay.setAlignment(Qt.AlignRight)
        self.rowDisplay.setEnabled(False)
        self.rowDisplay.setFixedWidth(50)
        self.rowDisplay.setSizePolicy(QSizePolicy.Fixed, QSizePolicy.Fixed)

        columnLabel = QLabel('Column ')
        columnLabel.setSizePolicy(QSizePolicy.Fixed, QSizePolicy.Fixed)

        self.columnDisplay = QLineEdit()
        self.columnDisplay.setMaxLength(7)

        self.columnDisplay.setAlignment(Qt.AlignRight)
        self.columnDisplay.setEnabled(False)
        self.columnDisplay.setFixedWidth(50)
        self.columnDisplay.setSizePolicy(QSizePolicy.Fixed, QSizePolicy.Fixed)

        # If this is a member of a 3D array, display the table number
        # Account for indexing of arrays starting at 0, while the display starts at 1
        self.table_num += 1
        self.title = QLabel(title)

        self.checkBox = QCheckBox('Hold Slider Positions')
        if (self.table_num) > 0:
            self.title = QLabel(title)
            self.checkBox = QCheckBox('Hold Slider Positions')
            tableLabel = QLabel('Table ')
            tableLabel.setSizePolicy(QSizePolicy.Fixed, QSizePolicy.Fixed)
            self.tableDisplay = QLineEdit()
            self.tableDisplay.setMaxLength(7)
            self.tableDisplay.setAlignment(Qt.AlignLeft)
            #self.tableDisplay.setEnabled(False)
            self.tableDisplay.setFixedWidth(50)
            self.tableDisplay.setSizePolicy(QSizePolicy.Fixed, QSizePolicy.Fixed)


            self.checkBox.stateChanged.connect(self.holdSliderPositions)
            self.checkBox.setChecked(MainWindow.hold_position_checked)
            self.checkBox.sizeHint()

            self.previousTable = QPushButton('<')
            self.previousTable.setFixedWidth(70)
            self.previousTable.clicked.connect(lambda: self.changeTable(self.table_num-2))
            self.nextTable = QPushButton('>')
            self.nextTable.setFixedWidth(70)
            self.nextTable.clicked.connect(lambda: self.changeTable(self.table_num))

            self.tableInfoLayout.addWidget(self.checkBox)
            self.tableInfoLayout.addWidget(self.title)
            self.tableInfoLayout.addWidget(self.previousTable)
            self.tableInfoLayout.addWidget(self.nextTable)
            self.tableInfoLayout.addWidget(tableLabel)
            self.tableInfoLayout.addWidget(self.tableDisplay)
            self.tableDisplay.setText(str(self.table_num))
        else:
           # if self.tableWidget.table_type == 'Array_3D_Spectrum':
           #     self.checkBox.setChecked(MainWindow.hold_position_checked)

            self.tableInfoLayout.setAlignment(Qt.AlignCenter)
            self.tableInfoLayout.addWidget(self.title)

        self.tableInfoLayout.addWidget(rowLabel)
        self.tableInfoLayout.addWidget(self.rowDisplay)
        self.tableInfoLayout.addWidget(columnLabel)
        self.tableInfoLayout.addWidget(self.columnDisplay)

        self.tableInfoLayout.setSpacing(5)
        self.tableInfoLayout.stretch(0)
        self.tableInfoLayout.sizeHint()

        self.tableLayout.addLayout(self.tableInfoLayout)
############## T3st########################
        title = '+'
        self.tableDockWidget = QDockWidget(title, self)
        self.tableDockWidget.setObjectName("tableDockWidget")
        self.tableDockWidget.setAllowedAreas(Qt.RightDockWidgetArea)

        self.tableDockWidget.featuresChanged.connect(self.tableDockChanged)


        self.tableDockWidget.setWindowIcon(QIcon("./Icons/MagGlass.png"))
        self.tableDockWidget.setWidget(self.tableWidget)
        self.addDockWidget(Qt.RightDockWidgetArea, self.tableDockWidget)

        self.tableLayout.addWidget(self.tableDockWidget)

       # self.tableLayout.addWidget(self.tableWidget)
        self.tableTab.setLayout(self.tableLayout)

       # print('00000000')
       # print title
       # print(self.tableDockWidget.isFloating())
       # print('1111111')


    def tableDockChanged(self):
        print("HERE: I want to change the title")


    @staticmethod
    def getCheckBoxState(self):
        return MainWindow.hold_position_checked


    def changeTable(self, index):
        '''
        This is the callback for the arrow buttons on the table display for 3D images
        The arrows direct you to the next or previous band.
        :param index: Index into the structure of band data.
        :return:
        '''
        if index == -1:
            print("Already at the first table.")
            return
        elif index >= self.num_tables:
            print("Already at the last table.")
        else:
            self.drawIndexedTable(self.full_table, index)
            #TODO  make sure the band images render ok
            self.draw2dImage(self.full_table, index, redraw=False)

    def holdSliderPositions(self, state):
        if state == Qt.Checked:
            ItemTable.sliderYVal = self.tableWidget.verticalSliderBar.value()
            ItemTable.sliderXVal = self.tableWidget.horizontalSliderBar.value()
            MainWindow.hold_position_checked = True
        elif state == Qt.Unchecked:
            #print("Unchecked button")
            ItemTable.sliderYVal = 0
            ItemTable.sliderXVal = 0
            MainWindow.hold_position_checked = False
        else:
            print('Trouble with holdSliderPosition method')


   # def freeze_display(self):
   #     if self.imageWidget is not None:
   #        self.imageWidget.


    # TODO break image stuff into it's own class or multiple methods for differnt types of images
    def draw2dImage(self, image_array, index = -1, redraw=False):

        if redraw == False:
            if image_array == None:  # called from single image data structure
                self.image_data = self.table
            else:
                # Render individual 'band' of 3d Spectrum data
                self.image_data = image_array[index]

        self._settings = {'dpi': 80., 'axes': Model._AxesProperties(), 'selected_axis': 0,
                          'is_rgb': False, 'rgb_bands': (None, None, None)}
        self.clearLayout(self.imageLayout)
        self.clearLayout(self.imageInfoLayout)
        self.figure = Figure((10.0, 8.0), dpi=self._settings['dpi'])

        self.imageWidget = FigureCanvas(self.figure)

        self.figure.clear()

        self.axes = self.figure.add_subplot(111)

        self.axes.autoscale_view(True,True,True)
        self.axes.set_xlabel(self.summary[0][0])   #This works for a title

      #  print(type(image_data))
      #  print(image_data.shape)

       # height, width = self.image_data.shape
       # print('WIDTH: {}   HEIGHT: {}'.format(width,height))
        _norm = mpl.colors.Normalize(clip=False)
        #_norm = mpl.colors.Normalize()   # Linear
        #_norm = mpl.colors.LogNorm()   # Log
        #_norm = PowerNorm(gamma = 2.0) # squared
        #_norm = PowerNorm(gamma = 0.5) # square root

        _norm.vmin = np.ma.min(self.image_data)
        _norm.vmax = np.ma.max(self.image_data)

        print("Image data min max: {} / {}".format(_norm.vmin, _norm.vmax))

        #self.image_data = self.image_data.T
        self._origin = 'lower'

        self.image = self.axes.imshow(self.image_data, origin=self._origin, interpolation=self._interpolation,
                                      norm=self._norm, aspect='equal', cmap = self.cmap)

        if not self.cb_removed:
            self.cbar = self.figure.colorbar(self.image, orientation=self.color_bar_orientation)

        self.renderImage()


##############################


    def renderImage(self, peripheral=None):
        '''

        :param peripheral: This flag tells the methods to only change the ticks or colorbar
        :return:
        '''

        if peripheral == None:  # Only render these when redrawing the image.
            self.imageMenu.setDisabled(False)
            self.viewMenu.setDisabled(False)
            self.imageOptionsMenu.setDisabled(False)
            self.previousImage = QPushButton('<')
            self.previousImage.setFixedWidth(70)
            self.previousImage.clicked.connect(lambda: self.changeTable(self.table_num - 2))
            self.previousImage.clicked.connect(lambda: self.BooBoo())
            self.nextImage = QPushButton('>')
            self.nextImage.setFixedWidth(70)
            # self.nextImage.clicked.connect(lambda: self.changeTable(self.table_num - 2))
            self.nextImage.clicked.connect(lambda: self.Yogi())
            #if (table_num) > 0:

            self.imageInfoLayout.addWidget(self.previousImage)
            self.imageInfoLayout.addWidget(self.nextImage)

            self.imageInfoLayout.setSpacing(5)
            self.imageInfoLayout.stretch(0)
            self.imageInfoLayout.sizeHint()

            self.imageLayout.addLayout(self.imageInfoLayout)
            self.imageTab.setLayout(self.imageLayout)
        elif peripheral == 'ColorBar':
            if self.color_bar_orientation != 'hide':
                # the colorbar was removed to change the orientation, so add it back
                self.cbar = self.figure.colorbar(self.image, orientation=self.color_bar_orientation)
        elif peripheral == 'Ticks':
            self.axes.get_xaxis().set_visible(self.x_tick_visible)
            self.axes.get_yaxis().set_visible(self.y_tick_visible)
        else:
            pass

        self.imageWidget.setWindowTitle(QString("PyQt"))
        self.imageWidget.draw()

        self.imageLayout.addWidget(self.imageWidget)
        self.imageTab.setLayout(self.imageLayout)




    def Yogi(self):
        print("Smarter than the average Bear.")

    def BooBoo(self):
        print("I'm a cute little fellow.")

#################################################



    # clear all the layouts before writing a new one
    def clearLayout(self, layout):
        for i in reversed(range(layout.count())):
            item = layout.itemAt(i)

            if isinstance(item, QWidgetItem):
               # print("Clear widget: " + str(item)
                item.widget().close()
                # or
                # item.widget().setParent(None)
            elif isinstance(item, QSpacerItem):
                print("Clear spacer: " + str(item))
                # no need to do extra stuff
            else:
                #print("Clear layout: " + str(item))
                self.clearLayout(item.layout())
            # remove the item from layout
            layout.removeItem(item)

    # This opents the summary table Gui to allow further selection
    def openSummaryGUI(self,fname):
        print("fname: {}".format(fname))
        self.setCentralWidget(self.tabFrame)
        # get the summary data
        self.model = Model.SummaryItemsModel(fname)
        self.title, self.summary, self.num_structs, self.dimension = self.model.get_summary()

        # Get the length of the summary data to use as a minimum value in the window
        length = 0

        display_summary = []
        for i in range(len(self.summary)):
            # add leading and trailing blanks
            display_summary.append(' ' + self.summary[i][0] + ' ')
            length += len(self.summary[i][0]) + 2

        # Use a scale factor to match string lenth to pixels
        sf = 11

        self.horizontalHeaderLength = (length) * sf

        # Note: If there is a 'Header' type it will be in self.summary[1][0]
        # If this is not a
        # This table widget will be added to the dock widget
        self.header = ['Name', 'Type', 'Dimension', 'Select']
        # Set the size of the table based on the data filling it
        # Note self is passed so the SummaryTable can emit a signal back to the MainWindow
        self.summaryTable = SummaryTable(self, len(self.summary[1]), len(self.summary) + 1)

        # Test if this is a 3D array, and needs a 3D Table tab
        # If not remove the tab in case there was one from a previous file
        if self.threeDTabInPlace:
            self.tabFrame.removeTab(1)
            self.threeDTabInPlace = False

        height = (self.num_structs * 30 ) + 25
        #TODO compute the width based on the number of characters in the titles
        self.summaryTable.setMinimumSize(self.horizontalHeaderLength, height)
        self.summaryTable.setMaximumSize(self.horizontalHeaderLength, height)

#        print('&&&&&&&&&&&')
#        print(display_summary)
#        print(self.summary)
        self.summaryTable.setSummaryData(self.header, self.summary)
        self.summaryDockWidget.setWindowTitle(self.title)
        self.summaryDockWidget.setWindowIcon(QIcon("./Icons/MagGlass.png"))
        self.summaryDockWidget.setWidget(self.summaryTable)
        self.addDockWidget(Qt.RightDockWidgetArea, self.summaryDockWidget)
        self.summaryDockWidget.show()


class Tab(QTabWidget):
#    Out = pyqtSignal(QWidget)
#    In = pyqtSignal(QWidget)
    def __init__(self, parent=None):
        #QTabWidget.__init__(self)
        super(Tab, self).__init__(parent)

        self.popOutFlag = True
        self.setMovable(True)
        self.sizeHint()

        # TODO - allow tabs to undock and redock
#        self.popOutButton = QPushButton("-")
#        self.popOutButton.setFixedWidth(12)
#        self.popOutButton.setFixedHeight(12)


        ##self.popOutButton.clicked.connect(lambda: self.Out.emit(self))
#        self.popOutButton.clicked.connect(self.popInOut)

#        self.popInButton = QPushButton("+")
#       self.popInButton.setFixedWidth(12)
#        self.popInButton.setFixedHeight(12)
        #self.popInButton.clicked.connect(lambda: self.In.emit(self))
#        self.popInButton.clicked.connect(self.popInOut)

#        self.tBar = self.tabBar()

#       self.installEventFilter(self)


class LabelFrame(QWidget, QObject):
    '''
    This class will set up the Label Tab contents
    '''
    nameDataTypeDict = {}
    def __init__(self, dict, dimension, index, viewType, displayType):
        '''
        :param dict: The Label dictionary that all the data will be drawn from
        :param dimension: The dimension of any table or image associated with the file
        :param index: The Model index, use it's row() method to get which button was pushed
        :param viewType: The first 'Name' field - used to identify any 'Header' that may be part of the file
        :param displayType: The type of label display, 'object', 'full', 'mission_area', 'discipline_area' ..
        '''
        QWidget.__init__(self)

        self.dimension = dimension
        self.tree = QTreeView(self)
        layout = QVBoxLayout(self)
        layout.addWidget(self.tree)
        self.setGeometry(200, 80, 850, 720)
        self.name = []
        self.dataType = []
        self.index = index
        self.viewType = viewType


        root_model = QStandardItemModel()
        self.tree.setModel(root_model)

        self.populateTree(dict, root_model.invisibleRootItem(), self.name, self.dataType)
        self.make_name_dataType_dict()

        self.tree.setHeaderHidden(True)

        if displayType is not 'full_label':
            self.tree.expandAll()


        self.tree.setEditTriggers(QAbstractItemView.NoEditTriggers)
        self.tree.setSelectionMode(QAbstractItemView.NoSelection)


    def checkLength(self, string):
        '''
        checkLength() of unicode 'Description' field as taken from the xlm file.
        if the descriptions is over 70 characters:
            Format the line to be properly displayed as
            70 character lines with a slight indentation

        :param string: Unicode - the description as parsed from the xml file
        :return: the modified string
        '''
        line_max_len = 70

        if len(string) > line_max_len:
            string = string.strip()             # remove blanks on each side
            string = re.sub(' +', ' ', string)  # substitute a single blank for multiple blanks
            string = string.replace('\n', '\n   ')  # Add a slight indentations for each line

            if '\n' in string:
                return string
            else:
                # add '\n' to make the proper lines
                new_lines = textwrap.wrap(string,line_max_len)
                new_string = '\n   '.join(str(line) for line in new_lines)
                return new_string

    def populateTree(self, dict, parent, name, dataType):
        '''
        This method uses recursion to obtain all the data from the nested OrderedDict's
        in the Label Dictionary that is passed in
        This is also used to get data to be displayed in message boxes (data_type)
            This is associated with the name field which is used as a key with data_type being the data
        :param dict: The Label Dictionary that is made up of nested OrderedDict's
        :param parent: This is the heading that can be clicked to open or close the heirarchy
        :return:
        '''
        #TODO - iteritems() may not work in Python3 - they use iter()  use iterkey()
        new_value = ''  # used to hold modified descriptions data
        for key, value in dict.iteritems():
            # Test for descriptions without '/n'
            if key == 'description':
                new_value = self.checkLength(value)
                value = new_value

            if key == 'data_type':
                self.dataType.append(new_value)
            if key == 'name':
                # Test of we came accross a group name that has no dataType
                if len(self.dataType) == len(self.name):
                    self.name.append(value)
            if isinstance(value, OrderedDict):
                heading = QStandardItem('{}'.format(key))
                parent.appendRow(heading)
                self.populateTree(value, heading, self.name, self.dataType)
            elif isinstance(value, list):
                for i in value:
                    heading = QStandardItem('{}'.format(key))
                    parent.appendRow(heading)
                    self.populateTree(i, heading, self.name, self.dataType)
            else:
                field = QString('{0}: {1}'.format(key, value))
                child = QStandardItem(field)
                # child.setFont()
                parent.appendRow(child)

    def make_name_dataType_dict(self):
        #test for a VIEW that does not have an associated table - e.g. HEADER
        #   index is returned form the model and will be the first button (where a header will show up
        #   firstName is the first 'name' in the structure, so if the first one is 'Header' we have not table or image
        if self.viewType == 'Header':
            LabelFrame.nameDataTypeDict = {}
   #     else:
   #         print('before loop')
   #         print(self.name)
   #         for i in range(len(self.name)):
   #             print('((((((((')
   #             print(i)
   #             print('))))))))')
   #             LabelFrame.nameDataTypeDict[self.name[i]] = self.dataType[i]
   #         print(LabelFrame.nameDataTypeDict)

    @staticmethod
    def getLabelNameDataType():
      #  print LabelFrame.nameDataTypeDict
        return LabelFrame.nameDataTypeDict


class SummaryTable(QTableWidget):
    tableFont = QFont()
    tableFont.setPointSize(14)
    tableFont.setFamily('Arial')

    headerFont = QFont()
    headerFont.setPointSize(17)
    headerFont.setFamily('Arial')

    def __init__(self, f_arg, *args):
        self.object = f_arg   # Needed so it is possible to emit a signal to the main window
        QTableWidget.__init__(self, *args)

    #    stylesheet = "QHeaderView::section{Background-color:rgb(0,0,100);border-radius:24px;padding:24py;}"
    #    self.setStyleSheet(stylesheet)

        self.setShowGrid(False)

        self.verticalHeader().setVisible(False)
        self.head = self.horizontalHeader()
        self.head.setStretchLastSection(True)
        #self.resizeRowsToContents()
        self.head.setResizeMode(0, QHeaderView.ResizeToContents)
        self.head.setResizeMode(1,QHeaderView.ResizeToContents)
        self.head.setResizeMode(2, QHeaderView.ResizeToContents)
        self.head.setResizeMode(3, QHeaderView.ResizeToContents)
        self.resizeColumnsToContents()
        self.sizeHint()


    def setSummaryData(self, header, data):
        for row in range(len(data[0])):
            for col in range(len(data)+1):
                if col < (len(data)):
                    item = QTableWidgetItem(data[col][row])
                    item.setTextAlignment(Qt.AlignHCenter)
                    item.setFont(self.tableFont)
                    item.setFlags(Qt.ItemIsEnabled)  # do not allow the cell to be edited
                    self.head.setResizeMode(row, QHeaderView.ResizeToContents)
                    self.setItem(row, col, item)
                else:
                    self.btn_sell = QPushButton('View')
                    #self.btn_sell.setFixedWidth(80)
                    self.btn_sell.clicked.connect(self.handleButtonClicked)
                    self.setCellWidget(row, col, self.btn_sell)
                    self.update()

        self.setHorizontalHeaderLabels(header)
        for i in range(len(header)):
            self.horizontalHeaderItem(i).setFont(self.headerFont)


    def sizeHint(self):
        horizontal = self.horizontalHeader()
        vertical = self.verticalHeader()
        frame = self.frameWidth() * 2
        return QSize(horizontal.length() + vertical.width() + frame,
                 vertical.length() + horizontal.height() + frame)


    @pyqtSlot()
    def handleButtonClicked(self):
        button = self.sender()
        index = self.indexAt(button.pos())
        self.update(index)
        assert (index.isValid())
        # This signal is emitted to the Main Window and index is passed to the callback
        self.object.emit(SIGNAL("SummaryTableTriggered"), index)


class MyHeader(QHeaderView):
    '''
    This class adds a couple of methods to the the horizontalHeader object.
    These methods work together to return the mouse position when the mouse
    is released in the horipntal header.
    '''
    xPos = 0
    yPos = 0
    def __init__(self, orientation,  parent=None):
        QHeaderView.__init__(self, orientation,  parent)
        self.cursor = None
        print("using new class")

    def mouseReleaseEvent(self, QMouseEvent):
        self.cursor = QCursor()
        point = self.cursor.pos()
        MyHeader.xPos = point.x()
        MyHeader.yPos = point.y()

    @staticmethod
    def getMousePosition(self):
        return MyHeader.xPos, MyHeader.yPos


class ItemTable(QTableView):
    sliderXVal = 0
    sliderYVal = 0

    def __init__(self, f_arg, *args ):
        self.object = f_arg  # Needed so it is possible to emit a signal to the main window
        self.table = args[0]
        title = args[1]
        rows = args[2]
        columns = args[3]
        self.table_type = args[4]
        self.showHeaders = False
        self.newKey = ''
        self.image_types = ['Array_2D_Image', 'Array_3D_Spectrum']
        self.nameDataTypeDict = LabelFrame.getLabelNameDataType()
        #print(self.table.shape)
        #print(self.table)
        QTableView.__init__(self, parent = None)
        #print("Table type: {}".format(self.table_type))
        #tableModel = Model.TwoDImageModel(self.table)



        self.tableModel = Delegate.assignTableModel(self.table, self.table_type)
        possible_groups = Delegate.possible_groups(self.table_type)
        print(self.table_type)
        print("possible_groups: {}".format(possible_groups))
        if possible_groups:   # Test for groups, there may be groups in this data type

            self.set_group_colors()

        if self.table_type not in self.image_types:
            # get the column header information
            self.header_map = self.tableModel.headerDict
            # print self.header_map
            self.showHeaders = True
            # Use a custom horizontalHeader that give back cursor position when
            # the mouse button is released on the header.  It is used to place
            # the message box.
            myHeader = MyHeader(Qt.Horizontal, self)
            self.setHorizontalHeader(myHeader)

        self.setModel(self.tableModel)
        self.installEventFilter(self)

        self.sizeHint()
        self.setShowGrid(True)


        # This will be use to display the title of the data of the columnn number selected
        self.horizontalHeader().sectionDoubleClicked.connect(self.displayColumnTitle)
        self.verticalHeader().setVisible(True)

        self.verticalSliderBar = self.verticalScrollBar()
        self.horizontalSliderBar = self.horizontalScrollBar()

        # Get the slider values of the table
        # Use these values accross instances with the static method below
        ItemTable.sliderXVal, ItemTable.sliderYVal = self.getSliderValues(self)
        self.verticalSliderBar.setValue(ItemTable.sliderYVal)
        self.horizontalSliderBar.setValue(ItemTable.sliderXVal)

        self.doubleClicked.connect(self.getIndex)


    def set_group_colors(self):
        '''
        ItemTable.set_group_colors()
        This is called once at intialization and sets the group colors to the default value gray scale
        It is also used in the call back for changes made in the 'Style' Menu.
        There are 4 colors for each style.  If there are more than 4 groups in the file the pattern simply repeats.
        '''

        group_bg_dict_default = {0: (229, 231, 233), 1: (202, 207, 210), 2: (215, 219, 221),  # 4 shades of gray
                                 3: (189, 195, 199)}
        group_bg_dict_darkOrange = {0: (150, 150, 150), 1: (175, 175, 175), 2: (162, 162, 162),
                                    3: (187, 187, 187)}

        if MainWindow.style_group == 'Default':
            group_color_dict = group_bg_dict_default
        elif MainWindow.style_group == 'Dark Orange':
            group_color_dict = group_bg_dict_darkOrange
        else:
            group_color_dict = group_bg_dict_default

        group_dict =  group_color_dict
        # Returns sets of columns numbers representing where the groups are
        group_sets = self.tableModel.getGroupSet()

        for i in range(len(group_sets)):
            color = group_dict[i % 4 ]   # this will repeat the color pattern of 4 colors
            for group in group_sets[i]:
                self.setItemDelegateForColumn(group, Delegate.GroupDelegate(self, color))


    # Use this static method to update the slider position for all new instances
    # This allows the user to set the position if he uses the checkbox
    @staticmethod
    def getSliderValues(self):
        return ItemTable.sliderXVal, ItemTable.sliderYVal


    def displayColumnTitle(self, index):

        if self.showHeaders:
            column = index + 1
            messageBox = QMessageBox()
            messageBox.setIcon(QMessageBox.NoIcon)
            self.selectColumn(index)

            # Get the header information
            for key in self.header_map.iterkeys():
                print key

                if column in self.header_map[key]:
                    title = "'" + key + "'"  # add single quotes to the title
                    if len(self.header_map[key]) > 1:
                        groupIndex = column - self.header_map[key][0]
                        title = title + '\n(repitition ' + str(groupIndex + 1) + ')'
                    # Check for the case where there is a longer comma separated group name
                    if ',' in str(key):
                        self.newKey = key.split(',')
                        self.newKey = self.newKey[1].strip()
                    else:
                        self.newKey = key
                    if self.newKey in self.nameDataTypeDict:
                        title = title + '\n Data Type: ' + self.nameDataTypeDict[self.newKey]

            messageBox.setText("Column Header {}:\n{}".format(column, title))
            messageBox.setWindowTitle("Column Title")
            messageBox.setStandardButtons(QMessageBox.Ok)
            messageBox.setWindowModality(Qt.ApplicationModal)

            x, y = MyHeader.getMousePosition(self)
            messageBox.move(x, y)
            retVal = messageBox.exec_()

    def sizeHint(self):
        horizontal = self.horizontalHeader()
        vertical = self.verticalHeader()
        frame = self.frameWidth() * 2
        return QSize(horizontal.length() + vertical.width() + frame,
                 vertical.length() + horizontal.height() + frame)

    # Method to display cell coordinates on a double click above the cell
    @pyqtSlot()
    def getIndex(self):
        try:
            widgetPosition = self.mapFromGlobal(QCursor.pos())
        except:
            print("Trouble reading cursor position.")
        test = self.indexAt(widgetPosition)
        if test.isValid:  # test is a QModelIndex class'
            position = (test.row(), test.column())
            self.object.emit(SIGNAL("GetPositionTriggered"), position)
        else:
            print("not a valid cell")


    # This is used for copying and pasting elements in the table
    def eventFilter(self, source, event):
        if (event.type() == QEvent.KeyPress and
                event.matches(QKeySequence.Copy)):
            self.copySelection()
            return True
        return super(ItemTable, self).eventFilter(source, event)


    def copySelection(self):
        selection = self.selectedIndexes()
        if selection:
            rows = sorted(index.row() for index in selection)
            columns = sorted(index.column() for index in selection)
            rowcount = rows[-1] - rows[0] + 1
            colcount = columns[-1] - columns[0] + 1
            table = [[''] * colcount for _ in range(rowcount)]
            for index in selection:
                row = index.row() - rows[0]
                column = index.column() - columns[0]
                table[row][column] = index.data()
            stream = io.BytesIO()
            csv.writer(stream).writerows(table)
            QApp.app.clipboard().setText(stream.getvalue())

    def resizeTable(self):
        width = 50
        for i in range(100):
            if (i % 2) == 0:
                width = 10
                self.setItemDelegateForColumn(i, Delegate.ResizeTableDelegate(self, width))
            else:
                width = 100
                self.setItemDelegateForColumn(i, Delegate.ResizeTableDelegate(self, width))




class TextWindow(QTextBrowser):
    textFont = QFont()
    textFont.setPointSize(14)
    textFont.setFamily('Arial')

    def __init__(self, *args):
        QTextBrowser.__init__(self, *args)
        self.setText("Hi Boo Boo")

class QApp(QApplication):
    app = None
    def __init__(self):
        self.application = QApplication(sys.argv)
        self.application.setOrganizationName("Jet Propulsion Laboratory")
        self.application.setOrganizationDomain("jpl.nasa.gov")
        self.application.setApplicationName("PDS Inspect Tool")
        mw = MainWindow()
        self.application.setStyle("macintosh")
        mw.show()

    # This is used to change the 'Theme' with app.setStyle() in a callback
    @staticmethod
    def getQApp(self):
        QApp.app = self.application
        return QApp.app


def main():
    a = QApp()
    app = a.getQApp(a)
    sys.exit(app.exec_())

main()