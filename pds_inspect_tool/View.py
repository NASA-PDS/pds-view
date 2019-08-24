# Copyright (c) 2019, California Institute of Technology ("Caltech").  
# U.S. Government sponsorship acknowledged.
#
# All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
#
# * Redistributions of source code must retain the above copyright notice,
#   this list of conditions and the following disclaimer.
# * Redistributions must reproduce the above copyright notice, this list of
#   conditions and the following disclaimer in the documentation and/or other
#   materials provided with the distribution.
# * Neither the name of Caltech nor its operating division, the Jet Propulsion
#   Laboratory, nor the names of its contributors may be used to endorse or
#   promote products derived from this software without specific prior written
#   permission.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
# AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
# ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
# LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
# CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
# SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
# INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
# CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
# ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
# POSSIBILITY OF SUCH DAMAGE.

# View.py - View class presents the data items from a model on the user screeen
import os
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
import matplotlib.patches as patches
from matplotlib.figure import Figure
import matplotlib.pyplot as plt
import matplotlib.cm as cm
from matplotlib.widgets import RectangleSelector
from matplotlib.backends.backend_qt4agg import (
    FigureCanvasQTAgg as FigureCanvas,
    NavigationToolbar2QT)

from PIL import Image

# Safe import of PowerNorm (available in MPL v1.4+)
try:
    from matplotlib.colors import PowerNorm
except ImportError:
    PowerNorm = None

import seaborn as sns

import Model

import Translate

import searchTableDialog
import searchLabelDialog

from . import resource_path

MAC = "qt_mac_set_native_menubar" in dir()
__version__ = "1.0.1"


class MainWindow(QMainWindow):

    supportedFileFormats = ['.xml']

    font = QFont()
    font.setPointSize(14)
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
        self.rgb_data_flag = False
        self.rgb_image = None
        # Tick state variables
        self.x_tick_visible = True
        self.y_tick_visible = True
        self.current_ticks_state = 'Show'
        # must initialize to the same as current_ticks_state to maintain proper interaction between
        # the tick controls in the pulldowns and on the taskbar PDS-740
        self.ticks_last_state = 'Show'
        self.last_visible_state = 'Show'   # PDS-740
        self.color_bar_orientation = 'hide'
        self.last_visible_cb_state = ''
        self.color_bar_last_state = 'horizontal'

        self._norm = mpl.colors.Normalize(clip=False)   # Linear normalization
        self._interpolation = 'none'                    # default to no interpolation on images
        self.current_view = -1       # determines the tab display config on first click and thereafter
        self.label_index = 0
        self.table_index = 1
        self.three_d_table_index = 1
        self.image_index = 2
        self.summary_index = 3
        self.three_d_table = False
        self.last_lab = ''
        # alternating 4 shades of gray are used to distinguish multiple groups in the table
        self.group_bg_dict_default = {0: (229, 231, 233), 1: (202, 207, 210), 2: (215, 219, 221),
                                 3: (189, 195, 199)}
        self.group_bg_dict_dark = {0: (150, 150, 150), 1: (175, 175, 175), 2: (162, 162, 162),
                          3: (187, 187, 187)}
        self.group_color_dict = self.group_bg_dict_default

        self.group_text_color_default = (53, 53, 53)
        self.group_bg_dict_dark_orange = (53, 47, 43)
        self.group_bg_dict_dark_blue = (33, 37, 53)
        self.group_bg_dict_neon = (0, 0, 0)
        self.group_text_color = self.group_text_color_default  # set default value when we first start

        settings = QSettings()
        self.recentFiles = settings.value("RecentFiles") or []

        self.threeDTabInPlace = False
        self.table_num = 0    # This is used for table arrays and image arrays (e.g 3D Spectral Data)

        # variables used for bounding box selection in the table.
        self.start_x = 0
        self.start_y = 0
        self.width = 0
        self.height = 0

        self.tableWidget = None
        self.labelWidget = None

        self.clear_bounding_box = False
        self.indices = None  # selected indices from the table

        self.bounding_box_rectangle = None   # This is used to match an image selection to the table
        self.allow_rect = False
        self.active_histogram = False

        self.image_types = ('Array', 'Array_2D_Image', 'Array_3D_Spectrum', 'Array_3D_Image', 'Array_2D_Map')

        self.logo = QLabel()
        try:
            self.logo.setPixmap(QPixmap(resource_path("./Icons/PDS_Logo_2.png")))
        except:
            print('Could not read logo')
        self.setCentralWidget(self.logo)
        self.setAcceptDrops(True)
        self.platform = 'BooBoo the Bear'

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

        # Right now just use the default
        # TODO configurable style
        self.setStyleSheet('')

        # connect to Summary table to recieve a 'View' button clicked signal
        self.connect(self, SIGNAL("SummaryTableTriggered"), self.handle_view_clicked)

        self.tabFrame = Tab()

        self.tabFrame.setEnabled(True)
        # self.tabFrame.setGeometry(10,10,555,550)

        # add tabs
        self.labelTab = QWidget()
        self.tableTab = QWidget()
        self.threeDTableTab = QWidget()
        self.rgbImageTab  = QWidget()
        self.imageTab = QWidget()
        self.summaryTab = QWidget()

        #self.tabFrame.addTab(self.labelTab, "Label  ")
        #self.tabFrame.addTab(self.tableTab, "Table  ")
        #self.tabFrame.addTab(self.imageTab, "Image  ")
        #self.tabFrame.addTab(self.summaryTab, "Summary")

        self.summaryLayout = QHBoxLayout()

        self.labelLayout = QHBoxLayout()

        self.tableLayout = QVBoxLayout()

        self.tableInfoLayout = QHBoxLayout()

        self.threeDTableLayout = QGridLayout()
        # self.threeDTableInfoLayout = QHBoxLayout()

        self.rgbTableLayout = QGridLayout()

        # image options used for display and saving images
        self.image_width = 0
        self.image_height = 0
        self.dpi = 72.0

        self.imageLayout = QVBoxLayout()

        self._settings = {'dpi': self.dpi, 'axes': Model._AxesProperties(), 'selected_axis': 0,
                          'is_rgb': False, 'rgb_bands': (None, None, None)}
        self.figure = Figure((10.0, 8.0), dpi=self._settings['dpi'])
        self.imageWidget = FigureCanvas(self.figure)

        self.axes = None

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

        self.fileOpenAction = self.create_action("&Open...", self.file_open,
                                                 QKeySequence.Open, icon="folderopen.svg", tip="Open")
        self.fileSaveTableAsAction = self.create_action("Save Table As csv",self.file_save_table_as,
                                                        icon="saveCsv.svg", tip="Save Table as csv file.")

        self.fileSaveImageAsAction = self.create_action("Save Image As...", self.save_image_as,
                                                        icon="saveImage.svg", tip="Save image to file")

        # Disable until a file with an image is picked, and the 'View' button is clicked
        self.fileSaveImageAsAction.setDisabled(True)

        self.fileQuitAction = self.create_action("&Quit..", self.close_app,
                                                 "Ctrl+Q", icon="close.svg", tip="Close the application")

        self.fileMenu = self.menuBar().addMenu("&File")

        self.add_actions(self.fileMenu, (self.fileOpenAction, None, self.fileSaveTableAsAction,
                                         self.fileSaveImageAsAction, None, self.fileQuitAction))

        # Add the 'Label Options' menu bar items
        self.labelMenu = self.menuBar().addMenu("Label Options")
        label_options_group = QActionGroup(self, exclusive=True)

        # label_options is a ('Label Type', icon) tuple
        self.label_options = (('Object Label', 'objectLabel.svg'), ('Full Label', 'fullLabel.svg'), ('separator', ''),
                              ('Identification Area',''), ('Observation Area', ''), ('Discipline Area', ''),
                              ('Mission Area', ''), ('separator', ''), ('Display Settings', ''),
                              ('Spectral Characteristics', ''), ('separator', ''), ('File info', ''),
                              ('File Area Observational', ''),('separator', ''),
                              ('Statistics', ''), ('Reference List', ''), ('separator', ''))
        self.labelActionList = {}
        for label in self.label_options:
            if label[0] == 'separator':
                self.labelMenu.addSeparator()
            else:
                self.labelActionList[label[0]] = self.create_action(label[0], self.select_label,
                                                                    icon=label[1], tip=label[0],
                                                                    checkable=True, group_action=True)
                label_group_action = label_options_group.addAction(self.labelActionList[label[0]])
                self.labelMenu.addAction(label_group_action)

        # add Search Label, which has it's own callback
        self.searchLabelAction = self.create_action("&Search Label...", self.search_highlight_label,
                                                    "Ctrl+L", icon="searchLabel.svg",
                                                    tip="Search Label.")

        self.labelMenu.addAction(self.searchLabelAction)

        self.labelActionList['Object Label'].setChecked(True)

        self.tableMenu = self.menuBar().addMenu("Table Options")
        # Search Table
        self.searchHighlightTableAction = self.create_action("&Search Table...", self.search_highlight_table,
                                                             "Ctrl+T", icon="searchTable.svg",
                                                             tip="Search Table.")
        # Show selection bounding box
        self.showBoundingBoxAction = self.create_action("Show rectangular table selection on image", self.get_selection,
                                                        icon="ShowSelection",
                                                        tip="Draw bounding box on image of selected table data")

        self.hideBoundingBoxAction = self.create_action("Hide bounding box of table selection", self.hide_selection,
                                                        icon="HideSelection",
                                                        tip="Hide bounding box on image of selected table data")

        self.nextCubeDataFileAction = self.create_action("Select next band", self.get_next_file,
                                                         icon="nextCubeLayer.svg",
                                                         tip="Next band and image.")
        self.lastCubeDataFileAction = self.create_action("Select previous band", self.get_last_file,
                                                         icon="lastCubeLayer.svg",
                                                         tip="Previous band and image.")
        # Render Histogram of table data
        self.histogramAction = self.create_action('Histogram', self.render_histogram,
                                                  icon="histogram.svg",
                                                  tip='Histogram of table.')

        self.kdeHistogramAction = self.create_action('KDE Histogram', self.render_kde_histogram,
                                                     icon="kdeHistogram.svg",
                                                     tip="'Kernel Density Estimation' histogram of table.")

        self.colorbarAction = self.create_action('Toggle Colorbar', self.toggle_colorbar,
                                                     icon="colorbar.svg",
                                                     tip="Toggle Colorbar")

        self.tickAction = self.create_action('Toggle Ticks', self.toggle_ticks,
                                                 icon="ticks.svg",
                                                 tip="Toggle Ticks")


        self.tableMenu.addAction(self.searchHighlightTableAction)
        self.tableMenu.addSeparator()
        self.tableMenu.addAction(self.lastCubeDataFileAction)
        self.tableMenu.addAction(self.nextCubeDataFileAction)
        self.tableMenu.addSeparator()
        self.tableMenu.addAction(self.showBoundingBoxAction)
        self.tableMenu.addAction(self.hideBoundingBoxAction)
        self.tableMenu.addSeparator()
        self.tableMenu.addAction(self.histogramAction)
        self.tableMenu.addAction(self.kdeHistogramAction)
        self.hideBoundingBoxAction.setDisabled(True)
        self.nextCubeDataFileAction.setDisabled(True)
        self.lastCubeDataFileAction.setDisabled(True)
        self.histogramAction.setDisabled(True)
        self.kdeHistogramAction.setDisabled(True)
        self.colorbarAction.setDisabled(True)
        self.tickAction.setDisabled(True)

        # 'Image Options' menu item
        self.colorbarActionDict = {}
        self.tickActionDict = {}
        self.interpolationActionDict = {}
        self.normalizationActionDict = {}

        self.imageOptionsMenu = self.menuBar().addMenu("Image Options")

        # Interpolation
        self.interpolation_sub_menu = self.imageOptionsMenu.addMenu("Interpolation method")
        interpolation_group = QActionGroup(self, exclusive=True)
        interpolation_options =  ('none', 'nearest', 'bilinear', 'bicubic', 'spline16', 'spline36',
                                  'hanning', 'hamming', 'hermite', 'kaiser', 'quadric', 'catrom',
                                  'gaussian', 'bessel', 'mitchell', 'sinc', 'lanczos')
        for mode in interpolation_options:
            self.interpolationActionDict[mode] = self.create_action(mode, self.set_interpolation,
                                                                    icon="InterpolationMode",
                                                                    tip="Select Interpolation Mode", checkable=True,
                                                                    group_action=True)
            interpolation_actions = interpolation_group.addAction(self.interpolationActionDict[mode])
            self.interpolation_sub_menu.addAction(interpolation_actions)

        self.interpolationActionDict['none'].setChecked(True)

        self.imageOptionsMenu.addSeparator()

        colorbar_sub_menu = self.imageOptionsMenu.addMenu("Colorbar")
        self.cb_removed = True
        colorbar_group = QActionGroup(self, exclusive=True)
        colorbar_options = ('horizontal', 'vertical', 'hide')
        for cb in colorbar_options:
            self.colorbarActionDict[cb] = self.create_action(cb, self.set_colorbar, icon="colorbar.svg",
                                                             tip="Toggle Colorbar Orientataion", checkable=True, group_action=True)
            cb_actions = colorbar_group.addAction(self.colorbarActionDict[cb])
            colorbar_sub_menu.addAction(cb_actions)

        self.colorbarActionDict['hide'].setChecked(True)

        self.imageOptionsMenu.addSeparator()

        tick_sub_menu = self.imageOptionsMenu.addMenu("Ticks")

        tick_group = QActionGroup(self, exclusive=True)
        tick_options = ('Show', 'Hide', 'Show X-Tick', 'Show Y-Tick')
        self.ticks_removed = False
        for tick in tick_options:
            self.tickActionDict[tick] = self.create_action(tick, self.set_ticks, icon="ticks.svg",
                                                           tip="Toggle Ticks Visibility", checkable=True,
                                                           group_action=True)
            tick_actions = tick_group.addAction(self.tickActionDict[tick])
            tick_sub_menu.addAction(tick_actions)

        self.tickActionDict['Show'].setChecked(True)
        self.x_tick_visible = False
        self.y_tick_visible = False

        self.imageOptionsMenu.addSeparator()

        # Normalization
        self.normalization_sub_menu = self.imageOptionsMenu.addMenu("Normalize")
        normalization_group = QActionGroup(self, exclusive=True)
        norm_options = ('Linear', 'Logarithmic', 'Squared', 'Square Root')
        for mode in norm_options:
            self.normalizationActionDict[mode] = self.create_action(mode, self.set_normalization,
                                                                    icon="NormalizationMode",
                                                                    tip="Select Normalization Mode", checkable=True,
                                                                    group_action=True)
            normalization_actions = normalization_group.addAction(self.normalizationActionDict[mode])
            self.normalization_sub_menu.addAction(normalization_actions)
        # set default
        self.normalizationActionDict['Linear'].setChecked(True)
        if PowerNorm is None:
            # Unable to import PowerNorm
            self.normalizationActionDict['Squared'].setEnabled(False)
            self.normalizationActionDict['Square Root'].setEnabled(False)

        self.imageOptionsMenu.addSeparator()

        # Show mouse selected bounding box and make selection in the table
        self.showMouseSelectionAction = self.create_action("Highlight mouse selection in table", self.get_mouse_selection,
                                                           icon="ShowMouseSelection",
                                                           tip="Draw bounding box and clear corresponding table selection")

        self.hideMouseSelectionAction = self.create_action("Clear bounding box and table selection", self.clear_mouse_selection,
                                                           icon="HideMouseSelection",
                                                           tip="Clear bounding box and corresponding table selection")

        self.imageOptionsMenu.addAction(self.showMouseSelectionAction)
        self.imageOptionsMenu.addAction(self.hideMouseSelectionAction)
        self.hideMouseSelectionAction.setDisabled(True)

        # Add the 'Color Maps' menu bar items
        self.invert_colormap = False

        self.colorMapMenu = self.menuBar().addMenu("Color Maps")

        view_basic_color_group = QActionGroup(self, exclusive=True)
        self.colorMaps = ('gray', 'Reds', 'Greens', 'Blues', 'hot', 'afmhot', 'gist_heat', 'cool', 'coolwarm',
                          'jet', 'rainbow', 'hsv', 'Paired', 'separator', "current selection in 'All'")

        self.colorActionList = {}

        for color in self.colorMaps:
            if color == 'separator':
                self.colorMapMenu.addSeparator()
            else:
                self.colorActionList[color] = self.create_action(color, self.select_color_map,
                                                                 icon='show'+color, tip='Display '+color+' in image',
                                                                 checkable=True, group_action=True)
                group_action = view_basic_color_group.addAction(self.colorActionList[color])
                self.colorMapMenu.addAction(group_action)

        self.colorActionList['gray'].setChecked(True)   # note: ...setDisabled(True) can be used individually

        self.colorMapMenu.addSeparator()

        # add an all colormaps pulldown

        all_sub_menu = self.colorMapMenu.addMenu("All")

        self.actionListAll = {}
        view_all_color_group = QActionGroup(self, exclusive=True)
        self.allColormaps = sorted(m for m in plt.cm.datad if not m.endswith("_r"))
        for color in self.allColormaps:
            self.actionListAll[color] = self.create_action(color, self.select_color_map,
                                                           icon='show' + color, tip='Display ' + color + ' in image',
                                                           checkable=True, group_action=True)
            group_action_all = view_all_color_group.addAction(self.actionListAll[color])
            all_sub_menu.addAction(group_action_all)

        self.actionListAll['gray'].setChecked(True)

        self.colorMapMenu.addSeparator()

        # add and Invert choice to the menu
        self.invertColorAction = self.create_action("Invert", self.invert_color_map, shortcut="Ctrl+I",
                                                    icon="invertColormap", tip="Invert the color", checkable=True)

        self.invertColorAction.setChecked(False)

        self.colorMapMenu.addAction(self.invertColorAction)

        # Add the 'Display Styles' menu bar items
        self.stylesActionList = {}
        self.layoutActionList = {}

        self.displayStylesMenu = self.menuBar().addMenu("Display Styles")
        styles_sub_menu = self.displayStylesMenu.addMenu("Styles")
        styles_group = QActionGroup(self, exclusive=True)
        self.styles_options = ('Default', 'Dark Orange', 'Dark Blue', 'Neon Lights')
        for style in self.styles_options:
            if style == 'separator':
                self.stylesMenu.addSeparator()
            else:
                self.stylesActionList[style] = self.create_action(style, self.setStyle,
                                                                  icon='Set Style: ' + style, tip='Set Style ' + style,
                                                                  checkable=True, group_action=True)
                styles_group_action = styles_group.addAction(self.stylesActionList[style])
                styles_sub_menu.addAction(styles_group_action)

        self.stylesActionList['Default'].setChecked(True)

        self.displayStylesMenu.addSeparator()

        # Layouts
        layout_sub_menu = self.displayStylesMenu.addMenu("Layouts")
        layout_group = QActionGroup(self, exclusive=True)
        layout_options = ("Macintosh", "Windows", "Plastique", "CDE", "Cleanlooks", "Motif", "sgi")
        for layout in layout_options:
            if layout == 'separator':
                self.displayStylesMenu.addSeparator()
            else:
                self.layoutActionList[layout] = self.create_action(layout, self.set_theme,
                                                                   icon='Set Layout: ' + layout, tip='Set Layout ' + layout,
                                                                   checkable=True, group_action=True)
                layout_group_action = layout_group.addAction(self.layoutActionList[layout])
                layout_sub_menu.addAction(layout_group_action)

        for i in layout_options:
            if i not in self.theme_list:
                self.layoutActionList[i].setDisabled(True)

        self.layoutActionList[self.default_theme].setChecked(True)
        self.layoutActionList['sgi'].setDisabled(True)

        # 'View' menu item
        self.toolBarActionList = {}
        self.screenOptionActionList = {}

        self.viewMenu = self.menuBar().addMenu("View Options")
        # ToolBar options (submenu)
        tool_bar_sub_menu = self.viewMenu.addMenu("Tool Bar")
        tool_bar_group = QActionGroup(self, exclusive=True)
        toolbar_options = ('Hide Toolbar', 'Show Toolbar')
        for option in toolbar_options:
            if option == 'separator':
                self.viewMenu.addSeparator()
            else:
                self.toolBarActionList[option] = self.create_action(option, self.set_tool_bar_options,
                                                                    icon='view', tip='Change Tool Bar Options: '+ option,
                                                                    checkable=True, group_action=True)
                tool_bar_group_action = tool_bar_group.addAction(self.toolBarActionList[option])
                tool_bar_sub_menu.addAction(tool_bar_group_action)

        self.toolBarActionList['Show Toolbar'].setChecked(True)

        self.viewMenu.addSeparator()

        # Screen option (submenu)
        screen_option_sub_menu = self.viewMenu.addMenu("Screen Options")
        screen_option_group = QActionGroup(self, exclusive=True)
        screen_options = ('Full Screen', 'Normal', 'Minimize', 'Maximize')
        for screen in screen_options:
            if screen == 'separator':
                self.viewMenu.addSeparator()
            else:
                self.screenOptionActionList[screen] = self.create_action(screen, self.set_screen_options,
                                                                         icon='screen', tip='Set Screen Options: '+ screen,
                                                                         checkable=True, group_action=True)
                screen_option_group_action = screen_option_group.addAction(self.screenOptionActionList[screen])
                screen_option_sub_menu.addAction(screen_option_group_action)

        self.screenOptionActionList['Normal'].setChecked(True)

        self.viewMenu.addSeparator()

        # Disable until the 'View' button is pushed
        self.tableMenu.setDisabled(True)
        self.labelMenu.setDisabled(True)
        self.colorMapMenu.setDisabled(True)

        # Disable image functionality until an image is rendered
        self.viewMenu.setDisabled(True)
        self.imageOptionsMenu.setDisabled(True)
        self.displayStylesMenu.setDisabled(True)

        # Tool Bar definitions
        self.mainToolBar = self.addToolBar('Hide Toolbar')
        self.mainToolBar.setObjectName('MainToolBar')

        #  File actions,search actions, and label actions
        #  Note use of labelActionList for the two label selection options in the toolbar
        self.add_actions(self.mainToolBar, (self.fileOpenAction, self.fileSaveTableAsAction, self.fileSaveImageAsAction,
                                            self.fileQuitAction, None, self.searchHighlightTableAction,
                                            self.searchLabelAction, None, self.labelActionList['Object Label'],
                                            self.labelActionList['Full Label'], None))

        self.fileSaveTableAsAction.setDisabled(True)
        self.fileSaveImageAsAction.setDisabled(True)
        self.searchLabelAction.setDisabled(True)
        self.searchHighlightTableAction.setDisabled(True)
        self.labelActionList['Object Label'].setDisabled(True)
        self.labelActionList['Full Label'].setDisabled(True)


        self.hold_slider_checkBox = QCheckBox('Hold Slider Positions')
        self.hold_slider_checkBox.setToolTip('Allows drilling into specific locations.')
        self.mainToolBar.addWidget(self.hold_slider_checkBox)

        self.add_actions(self.mainToolBar, (self.lastCubeDataFileAction,
                                            self.nextCubeDataFileAction))

        self.tableLabel = QLabel('Band ')
        self.tableLabel.setToolTip('Current Band Selected')
        self.tableLabel.setDisabled(True)
        self.tableLabel.setSizePolicy(QSizePolicy.Fixed, QSizePolicy.Fixed)
        self.tableNumDisplay = QLineEdit()
        self.tableNumDisplay.setMaxLength(7)
        self.tableNumDisplay.setAlignment(Qt.AlignLeft)
        self.tableNumDisplay.setDisabled(True)
        self.tableNumDisplay.setFixedWidth(50)
        self.tableNumDisplay.setSizePolicy(QSizePolicy.Fixed, QSizePolicy.Fixed)
        self.tableNumDisplay.returnPressed.connect(self.table_num_line_edit_entry)
        self.tableNumDisplay.clear()

        self.mainToolBar.addWidget(self.tableLabel)
        self.mainToolBar.addWidget(self.tableNumDisplay)

        self.dpiLabel = QLabel('dpi ')
        self.dpiLabel.setToolTip('Dots per inch.')
        self.dpiLabel.setDisabled(True)
        self.dpiLabel.setSizePolicy(QSizePolicy.Fixed, QSizePolicy.Fixed)
        self.dpiDisplay = QLineEdit()
        self.dpiDisplay.setMaxLength(7)
        self.dpiDisplay.setAlignment(Qt.AlignLeft)
        self.dpiDisplay.setDisabled(True)
        self.dpiDisplay.setFixedWidth(50)
        self.dpiDisplay.setSizePolicy(QSizePolicy.Fixed, QSizePolicy.Fixed)
        self.dpiDisplay.returnPressed.connect(self.dpi_line_edit_entry)
        self.tableNumDisplay.clear()

        self.hold_slider_checkBox.stateChanged.connect(self.hold_slider_positions)
        self.hold_slider_checkBox.setChecked(MainWindow.hold_position_checked)
        self.hold_slider_checkBox.sizeHint()
        self.hold_slider_checkBox.setDisabled(True)
        self.hold_slider_checkBox.setChecked(False)

        # add the histograms to the main tool bar
        self.add_actions(self.mainToolBar, (None, self.histogramAction, self.kdeHistogramAction, None,
                                            self.colorbarAction, self.tickAction))

        self.mainToolBar.addSeparator()

        self.mainToolBar.addWidget(self.dpiLabel)
        self.mainToolBar.addWidget(self.dpiDisplay)

        self.mainToolBar.addSeparator()

        self.rgb_combo = QComboBox()
        self.rgb_combo.addItems(['RGB', 'Red', 'Green', 'Blue', 'RedGreen', 'RedBlue', 'GreenBlue'])
        self.rgb_combo.currentIndexChanged.connect(self.rgb_options)
        self.mainToolBar.addWidget(self.rgb_combo)
        self.rgb_combo.setDisabled(True)

        # Hide main toolbar until a file is loaded.
        self.mainToolBar.hide()
        self.initial_gray_scale_setting()
        self.setWindowTitle("PDS Inspect Tool")
        self.setWindowIcon(QIcon(resource_path("./Icons/MagGlass.png")))

    def close_app(self):
        sys.exit()

    def set_screen_options(self, option):
        if option == 'Full Screen':
            self.showFullScreen()
        elif option == 'Normal':
            self.showNormal()
        elif option == 'Minimize':
            self.showMinimized()
        elif option == 'Maximize':
            self.showMaximized()

    def set_tool_bar_options(self, option):
        if option == 'Hide Toolbar':
            self.mainToolBar.hide()
        else:
            self.mainToolBar.show()

    def set_theme(self, style):
        ret = QApp.app.setStyle(style)
        self.theme = style
        if ret == None:
            print('Could not find a theme that should be displaying')

    def set_interpolation(self, mode):
        self._interpolation = mode
        # print("X: from set_interpolation()")
        self.draw_2d_image(None, redraw=True)

    def set_normalization(self, option):
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
        # print("X: from set_normaalization()")
        self.draw_2d_image(None, redraw=True)


    def toggle_colorbar(self):
        '''
        This is used in the toolbar to toggle the colorbar
        :return:
        '''
        current = self.color_bar_orientation
        display_states = ('horizontal', 'vertical')
        if current in display_states:
            self.set_colorbar('hide')
        else:
            if self.color_bar_last_state == 'hide':
                self.color_bar_last_state = self.last_visible_cb_state   # PDS-740 fix corner case
            self.set_colorbar(self.color_bar_last_state)
        self.color_bar_last_state = current

    def set_colorbar(self, option):
        if not self.cb_removed:           # This prevents multiple color bars from being rendered
            self.imageWidget.cbar.remove()
        if option == 'horizontal':
            self.color_bar_orientation = 'horizontal'
            self.cb_removed = False
            self.last_visible_cb_state = 'horizontal'
        elif option == 'vertical':
            self.color_bar_orientation = 'vertical'
            self.cb_removed = False
            self.last_visible_cb_state = 'vertical'
        elif option == 'hide':
            self.color_bar_orientation = 'hide'
            self.cb_removed = True
        if self.three_d_table:
            self.draw_2d_image(None, rgb=self.rgb_data_flag, redraw=True)
        else:
            self.draw_2d_image(None, rgb=self.rgb_data_flag, redraw=False)
        # This call with the added parameter is part of PDS-740 where tick state was not preserved
        self.set_ticks(self.current_ticks_state, "colorbar")
        self.colorbarActionDict[self.color_bar_orientation].setChecked(True)

    def toggle_ticks(self):
        '''
        This is used on the toolbar to simply toggle on and off the saved state.
        :return:
        '''
        current = self.current_ticks_state
        visible_states= ('Show', 'Show X-Tick', 'Show Y-Tick')
        if current in visible_states:
            self.set_ticks('Hide')
        else:
            if self.ticks_last_state == 'Hide':
                self.ticks_last_state = self.last_visible_state
            self.set_ticks(self.ticks_last_state, "callback")
        self.ticks_last_state = current

    def set_ticks(self, selection, callType = "callback"):  # callType parameter added PDS-740
        if selection == 'Show':
            self.x_tick_visible = True
            self.y_tick_visible = True
            self.last_visible_state = 'Show'
        elif selection == 'Hide':
            self.x_tick_visible = False
            self.y_tick_visible = False
        elif selection == 'Show X-Tick':
            self.x_tick_visible = True
            self.y_tick_visible = False
            self.last_visible_state = 'Show X-Tick'
        elif selection == 'Show Y-Tick':
            self.x_tick_visible = False
            self.y_tick_visible = True
            self.last_visible_state = 'Show Y-Tick'

        if callType == 'callback':  # Avoid multiple renders if call is from set_colorbar() PDS-740
            if self.three_d_table:
                self.draw_2d_image(None, rgb=self.rgb_data_flag, redraw=True)
            else:
                self.draw_2d_image(None, rgb=self.rgb_data_flag, redraw=False)

        self.render_image(peripheral='Ticks')
        self.current_ticks_state = selection
        self.tickActionDict[self.current_ticks_state].setChecked(True)

    def rgb_options(self, option):
        rgbArray = np.zeros((self.height, self.width, self.num_of_channels), 'uint8')  # Initialize the array with zeros
        if option == 0:  # RGB
            rgbArray[..., 0] = self.table[0]
            rgbArray[..., 1] = self.table[1]
            rgbArray[..., 2] = self.table[2]
        elif option == 1:  # Red
            rgbArray[..., 0] = self.table[0]
        elif option == 2:  # Green
            rgbArray[..., 1] = self.table[0]
        elif option == 3:  # Blue
            rgbArray[..., 2] = self.table[0]
        elif option == 4:  # Red Green
            rgbArray[..., 0] = self.table[0]
            rgbArray[..., 1] = self.table[1]
        elif option == 5:  # Red Blue
            rgbArray[..., 0] = self.table[0]
            rgbArray[..., 2] = self.table[1]
        elif option == 6:  # Green Blue
            rgbArray[..., 1] = self.table[0]
            rgbArray[..., 2] = self.table[1]

        self.rgb_image = Image.fromarray(rgbArray)
        self.draw_2d_image(None, rgb=self.rgb_data_flag, redraw=True)

    def setStyle(self, style):
        style_sheet = ''
        MainWindow.style_group = style

        if style == 'Default':
            self.group_color_dict = self.group_bg_dict_default
            self.group_text_color = self.group_text_color_default
        elif style == 'Dark Orange':
            f = open(resource_path('./styleSheets/darkorange.stylesheet', 'r'))
            style_sheet = f.read()
            f.close
            self.group_color_dict = self.group_bg_dict_dark
            self.group_text_color = self.group_bg_dict_dark_orange
        elif style == 'Dark Blue':
            f = open(resource_path('./styleSheets/darkblue.stylesheet', 'r'))
            style_sheet = f.read()
            f.close
            self.group_color_dict = self.group_bg_dict_dark
            self.group_text_color = self.group_bg_dict_dark_blue
        elif style == 'Neon Lights':
            f = open(resource_path('./styleSheets/neonlights.stylesheet', 'r'))
            style_sheet = f.read()
            f.close
            self.group_color_dict = self.group_bg_dict_dark
            self.group_text_color = self.group_bg_dict_neon
        else:
            print('Should not be here.n (set_style())')
        self.setStyleSheet(style_sheet)
        self.set_model_style()

    def set_model_style(self):
        self.tableWidget.tableModel.setTableStyle(MainWindow.style_group, self.group_color_dict, self.group_text_color)

    def select_label(self, label):
        self.draw_label(self.current_index, self.view_type, label)

    def select_color_map(self, colorMap):
        self.cmap = colorMap
        if self.invertColorAction.isChecked():
            self.cmap += '_r'
            # ('cmap: {}'.format(self.cmap))
        else:
            self.cmap = colorMap
        if colorMap in self.allColormaps:
            self.colorActionList["current selection in 'All'"].setDisabled(False)
            self.actionListAll[colorMap].setChecked(True)
        if colorMap not in self.colorMaps:
            self.colorActionList["current selection in 'All'"].setChecked(True)
        else:
            self.colorActionList["current selection in 'All'"].setDisabled(True)
        self.clear_layout(self.imageLayout)
        self.draw_2d_image(None, redraw=True)

    def invert_color_map(self):
        # This test is necessary to turn off the inversion of the current selection
        if '_r' in self.cmap:
            self.cmap = self.cmap[:-2]
        self.select_color_map(self.cmap)

    def initial_gray_scale_setting(self):
        # This is called to reset the checkboxes when a new file is opened.
        self.invertColorAction.setChecked(False)
        self.colorActionList['gray'].setChecked(True)
        self.cmap = 'gray'
        self.colorActionList["current selection in 'All'"].setDisabled(True)

    def create_action(self, text, slot=None, shortcut=None, icon=None, tip=None,
                      checkable=False, group_action=False, signal="triggered()"):
        action = QAction(text, self)
        if icon is not None:
            action.setIcon(QIcon(resource_path("./Icons/{}".format(icon))))
        if shortcut is not None:
            action.setShortcut(shortcut)
        if tip is not None:
            action.setToolTip(tip)
            action.setStatusTip(tip)
        if slot is not None:
            # Need to pass a parameter to the callback for a group action as in (View options)
            if group_action:
                self.connect(action, SIGNAL(signal), lambda: slot(text))
            else:
                self.connect(action, SIGNAL(signal), slot)
        if checkable:
            action.setCheckable(True)
        return action

    def add_actions(self, target, actions):
        for action in actions:
            if action is None:
                target.addSeparator()
            else:
                target.addAction(action)

    # Sets defaults for a new file
    def set_defaults(self):
        # clear all the tab layouts and select the label tab
        self.clear_layout(self.labelLayout)
        self.clear_layout(self.tableLayout)
        self.clear_layout(self.tableInfoLayout)
        self.clear_layout(self.threeDTableLayout)
        self.clear_layout(self.rgbTableLayout)
        self.clear_layout(self.imageLayout)
        self.initial_gray_scale_setting()
        self._norm = mpl.colors.Normalize(clip=False)  # Linear normalization
        self._interpolation = 'none'
        self.labelActionList['Object Label'].setChecked(True)
        self.colorActionList['gray'].setChecked(True)
        self.actionListAll['gray'].setChecked(True)
        self.normalizationActionDict['Linear'].setChecked(True)
        self.tickActionDict['Show'].setChecked(True)
        self.colorbarActionDict['hide'].setChecked(True)
        self.interpolationActionDict['none'].setChecked(True)

    def file_save_table_as(self):
        '''
        Save the table as a csv file.
        :return:
        '''
        fname = self.filename if self.filename is not None else "."
        fname = QFileDialog.getSaveFileName(self,
                                            "Save Table as CVS file", fname,
                                            'CSV(*.csv"=)')
        if fname:
            if "." not in fname:
                fname += ".csv"
            self.filename = fname
            # print('FILENAME: {}'.format(self.filename))
            self.tableWidget.tableModel.write_table_to_csv(fname)
        return False


    def search_highlight_table(self):
        search_table_gui = searchTableDialog.SearchTableDialog(self.table, self.tableWidget)
        search_table_gui.exec_()

    def search_highlight_label(self):
        # Start the dialog and return the indices to select
        search_label_gui = searchLabelDialog.SearchLabelDialog(LabelFrame.parent_child_dict, self.labelWidget)
        search_label_gui.exec_()

    def _fspath(self, path):
        """Return the path representation of a path-like object.
        If str or bytes is passed in, it is returned unchanged. Otherwise the
        os.PathLike interface is used to get the path representation. If the
        path representation is not str or bytes, TypeError is raised. If the
        provided path is not str, bytes, or os.PathLike, TypeError is raised.
        """
        if isinstance(path, (str, bytes)):
            return path

        # Work from the object's type to match method resolution of other magic
        # methods.
        path_type = type(path)
        try:
            path_repr = path_type.__fspath__(path)
        except AttributeError:
            if hasattr(path_type, '__fspath__'):
                raise
            else:
                raise TypeError("expected str, bytes or os.PathLike object, "
                                "not " + path_type.__name__)
        if isinstance(path_repr, (str, bytes)):
            return path_repr
        else:
            raise TypeError("expected {}.__fspath__() to return str or bytes, "
                            "not {}".format(path_type.__name__,
                                            type(path_repr).__name__))

    def save_image_as(self):
        # if self.image == None:
        #     return True

        fname = self.filename if self.filename is not None else "."
        extensions = (["*.{}".format(format.data().decode("ascii").lower())
                    for format in QImageWriter.supportedImageFormats()])
        # print("Image files ({})".format(" ".join(extensions)))

        fname = QFileDialog.getSaveFileName(self,
                                            "Save Image as:", fname,
                                            selectedFilter="Image files ({})".format(" ".join(extensions)))

        fname = self._fspath(str(fname))

        if fname:
            if "." not in fname:
                fname += ".png"
            self.filename = fname
            # print('FILENAME: {}'.format(self.filename))
            self.figure.savefig(fname=self.filename, dpi=self._settings['dpi'], format='png', bbox_inches='tight',
                                pad_inches=0)
           # return self.file_save()
        return False

    def addRecentFile(self, fname):
        # print('ADDING recent file to list. filename: {}'.format(fname))
        if fname is None:
            return
        if fname not in self.recentFiles:
            self.recentFiles = [fname] + self.recentFiles[:8]
        # for i in self.recentFiles:
        #    print i

    def update_file_menu(self):
        self.fileMenu.addSeparator()
        self.fileMenu.addSeparator()
        # print('recent files')
        # print self.recentFiles
        self.fileMenu.clear()
        self.addActions(self.fileMenu, self.fileMenuActions[:-1])
        current = self.filename
        recentFiles = []
        for fname in self.recentFiles:
            if fname != current and QFile.exists(fname):
                recentFiles.append(fname)
        if recentFiles:
            self.fileMenu.addSeparator()
            for i, fname in enumerate(recentFiles):
                action = QAction(QIcon(":/icon.png"), "&{} {}".format(i + 1, QFileInfo(fname).fileName()), self)
                action.setData(fname)
                self.connect(action, SIGNAL("triggered()"),
                             self.load_file)
                self.fileMenu.addAction(action)
        self.fileMenu.addSeparator()
        self.fileMenu.addAction(self.fileMenuActions[-1])

    def load_file(self, fname=None):
        if fname is None:
            action = self.sender()
            if isinstance(action, QAction):
                fname = action.data()
                if not self.okToContinue():
                    return
            else:
                return
        if fname:
            self.filename = None
            self.addRecentFile(fname)
            # print("Got into load_file()")

    def closeEvent(self, event):
        # print'Close Event.'
        settings = QSettings()
        # settings.setValue("LastFile", self.filename)
        settings.setValue("RecentFiles", self.recentFiles or [])
        # TODO make this save stylesheet
        # settings.setValue("MainWindow/Geometry", self.saveGeometry())
        # settings.setValue("MainWindow/State", self.saveState())
        event.accept()


    def file_message_box(self, message, fname):
        msg = message + '\n\n' + fname
        message_box = QMessageBox()
        message_box.setIcon(QMessageBox.Critical)
        message_box.setTextFormat(3)
        message_box.setText('Open File Problem')
        message_box.setInformativeText(msg)
        message_box.setStandardButtons(QMessageBox.Ok)
        message_box.setWindowModality(Qt.ApplicationModal)
        self.set_style_sheet(message_box)
        message_box.setWindowTitle(QString("File Problem"))
        message_box.exec_()

    def pds_file_check(self, full_path):
        '''
        Checks to see if the file has an extension (unfortunately PDS3 can use any extension they want a check for
        specific extensions cannot be made).  If the extension is .xml then the file is considered a PDS4 file and the
        path is returned, if not the file path is passed to the translate class to make a new .xml file from the PDS3
        label and the path to that .xml file is returned.
        :param full_path: full path to the file
        :return: full path the the file (original .xml or translated .xml)
        '''
        current_pds3_extensions = ['LBL', 'IMG', 'TAB', 'DAT', 'CSV']
        if self.platform == 'Windows':
            fname = full_path.split('\\')[-1]
        else:
            fname = full_path.split('/')[-1]
        if '.' not in fname:
            self.file_message_box("Missing file extension in", fname)
            return 'problem with file'
        # get the extension
        extension = fname.split('.')[-1].upper()

        if extension == 'XML':
            # print('Full Path: '.format(full_path))
            return full_path
        elif extension in current_pds3_extensions:
            translate = Translate.Translate(full_path)
            path_to_xml = translate.convert_to_pds4()
            # print("Path to xml")
            # print(path_to_xml)
            return path_to_xml
        else:
            self.file_message_box("Did not find a PDS3 extension that can  be translated in:", fname)
            return 'problem with file'

    def file_open(self):
        '''
        Open a file Dialog from the pulldown 'file' menu'.
        Configure functionality of various widgets
        :return:
        '''
        self.set_defaults()
        # Open File from menu bar
        file_dialog = QFileDialog(self, "Select .xml to access PDS data in files.")
        file_dialog.setNameFilter("Xml file (*.xml)")
        filename = file_dialog.getOpenFileNames()

        file_with_path = self.pds_file_check(map(str, filename)[0])  # map from QStringObject to list of one string.
        if file_with_path == 'problem with file':
            self.file_open()   # call this function again

        self.mainToolBar.show()
        self.open_summary_gui(file_with_path)
        self.summaryTab.setDisabled(False)
        self.colorMapMenu.setDisabled(True)  # Disable image functionality until an image is rendered
        self.viewMenu.setDisabled(False)
        self.imageOptionsMenu.setDisabled(True)   # Disable image functionality until an image is rendered
        self.tableMenu.setDisabled(True)
        self.labelMenu.setDisabled(True)  # Disable until the 'Label' tab is selected
        self.displayStylesMenu.setDisabled(False)
        self.searchLabelAction.setDisabled(True)
        self.searchHighlightTableAction.setDisabled(True)
        self.labelActionList['Object Label'].setDisabled(True)
        self.labelActionList['Full Label'].setDisabled(True)
        self.fileSaveTableAsAction.setDisabled(True)  # disable table saving until 'View' is pressed.
        self.fileSaveImageAsAction.setDisabled(True)  # disable image saving until 'View' is pressed.
        self.hold_slider_checkBox.setChecked(False)
        self.hold_slider_checkBox.setDisabled(True)
        self.nextCubeDataFileAction.setDisabled(True)
        self.lastCubeDataFileAction.setDisabled(True)
        self.tableLabel.setDisabled(True)
        self.tableNumDisplay.setDisabled(True)
        self.rgb_combo.setDisabled(True)

        self.current_view = -1

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
        '''
        Drop of pds file onto the running program to open the file
        Check for bad files and possible PDS3 to PDS4 translation.
        Configure the widgets for a new file opening
        :param event: Drop file event
        :return:
        '''
        for url in event.mimeData().urls():
            path = url.toLocalFile().toLocal8Bit().data()
            if os.path.isfile(path):
                # Check file for possible translation
                file_with_path = self.pds_file_check(path)
                if file_with_path == 'problem with file':
                    event.ignore()
                else:
                    self.set_defaults()
                    self.mainToolBar.show()
                    # print('file with path: {}'.format(file_with_path))
                    self.open_summary_gui(file_with_path)
                    self.summaryTab.setDisabled(False)
                    self.colorMapMenu.setDisabled(True)  # Disable image functionality until an image is rendered
                    self.viewMenu.setDisabled(False)
                    self.imageOptionsMenu.setDisabled(True)
                    self.tableMenu.setDisabled(True)
                    self.labelMenu.setDisabled(True)  # Disable until the 'View' button is pushed
                    self.displayStylesMenu.setDisabled(False)
                    self.searchLabelAction.setDisabled(True)
                    self.searchHighlightTableAction.setDisabled(True)
                    self.labelActionList['Object Label'].setDisabled(True)
                    self.labelActionList['Full Label'].setDisabled(True)
                    self.fileSaveTableAsAction.setDisabled(True)  # disable table saving until 'View' is pressed.
                    self.fileSaveImageAsAction.setDisabled(True)  # disable image saving until 'View' is pressed.
                    self.hold_slider_checkBox.setChecked(False)
                    self.hold_slider_checkBox.setDisabled(True)
                    self.nextCubeDataFileAction.setDisabled(True)
                    self.lastCubeDataFileAction.setDisabled(True)
                    self.tableLabel.setDisabled(True)
                    self.tableNumDisplay.setDisabled(True)
                    self.rgb_combo.setDisabled(True)
                    self.current_view = -1
                    self.addRecentFile(self.filename)
            else:
                event.ignore()

    def get_type(self, index, viewType):
        i = index.row()
        return viewType[i]

    def get_selection(self):
        '''
        This finds the selection coordinates in the table necessary for drawing a bounding box over the selected pixels
        :return:
        start_x = starting x coordinate for the rectangle based on the lower left corner
        start_y = starting y coordinate for the rectangle based on the lower left corner
        width   = width of the rectangle (number of columns)
        heigth  = heigth of the rectange (number of rows)
        '''
        rows = set()
        columns = set()
        self.indices = self.tableWidget.tableSelectionModel.selectedIndexes()

        if self.indices:
            for index in self.indices:
                rows.add(index.row())
                columns.add(index.column())
            self.start_x = min(columns) + 1
            self.start_y = max(rows) + 1
            # print("BB startx, starty: {} {}".format(self.start_x, self.start_y))
            self.width = max(columns) - self.start_x
            self.height = (self.start_y - min(rows)) * -1
            # print('({},{}), width = {}, height = {}'.format(self.start_x, self.start_y, self.width, -self.height))
            self.clear_bounding_box = False
            self.draw_2d_image(self, rgb=self.rgb_data_flag, redraw=True, drawBoundingBox=True)
            # change view to 'image'

            if self.view_type == 'Array_3D_Image' or self.view_type == 'Array_3D_Spectrum':
                self.tabFrame.setCurrentIndex(self.image_index + 1)
            else:
                self.tabFrame.setCurrentIndex(self.image_index)

            self.hideBoundingBoxAction.setDisabled(False)
            self.showBoundingBoxAction.setDisabled(True)
            # PDs 370 - Prevent this from being turned on when Array-3D_Image
            if self.view_type != 'Array_3D_Image':
                self.showMouseSelectionAction.setDisabled(True)
        else:
            results = 'Nothing selected in table.'
            QMessageBox.information(self, 'Selection Highlight', results)

    def hide_selection(self):
        if self.indices:
            self.clear_bounding_box = True
            self.draw_2d_image(self, rgb=self.rgb_data_flag, redraw=True, drawBoundingBox=False)
            self.hideBoundingBoxAction.setDisabled(True)
            self.showBoundingBoxAction.setDisabled(False)
            self.showMouseSelectionAction.setDisabled(False)
            self.tableWidget.clearSelection()
        else:
            results = 'Nothing selected in table.'
            QMessageBox.information(self, 'Selection Highlight', results)

    def check_for_selection(self):
        '''
        This is used to clear previous selections if a new bounding box is dragged out
        :return:
        '''
        # print('ok checking')
        # print('bounding_box_rectange is : {}'.format(self.bounding_box_rectangle))
        if self.bounding_box_rectangle is not None:
            self.bounding_box_rectangle.remove()
            self.allow_rect = True  # allow rectangle drawing over the 2d image
            self.bounding_box_rectangle = None

    def make_selection(self, x1, y1, x2, y2):
        # print('Using make_selection from MainWindow class.')
        # clear current selections
        self.tableWidget.clearSelection()

        parent = QModelIndex()
        # I don't understand why I have to swap x,y pairs to get the correct topLeft and bottomRight
        # But it works - perhaps because of the origin coordinates
        x1, y1 = y1, x1
        x2, y2 = y2, x2
        # print("(%3.2f, %3.2f) ---> (%3.2f, %3.2f)" % (x1, y1, x2, y2))
        topLeft = self.tableWidget.tableModel.index(x1, y1, parent)
        bottomRight = self.tableWidget.tableModel.index(x2, y2, parent)
        selection = QItemSelection(topLeft, bottomRight)
        self.tableWidget.tableSelectionModel.select(selection, QItemSelectionModel.SelectCurrent)
        # Move view in the table to the top left corner of the selection
        self.tableWidget.verticalSliderBar.setSliderPosition(x1-2)
        self.tableWidget.horizontalSliderBar.setSliderPosition(y1-2)

    def line_select_callback(self, eclick, erelease):
        # Used to make a selection based on a bounding box dragged on the image
        # print('Calling make_selection from line_select_callback in  MainWindowclass.')
        # print('ok, select callback hit')
        # print('x: .{}'.format(eclick.xdata))
        # print('y: .{}'.format(erelease.ydata))
        x1, y1 = eclick.xdata, eclick.ydata
        x2, y2 = erelease.xdata, erelease.ydata

        # print("(%3.2f, %3.2f) --> (%3.2f, %3.2f)" % (x1, y1, x2, y2))
        # print(" The button you used were: %s %s" % (eclick.button, erelease.button))
        self.bounding_box_rectangle = plt.Rectangle((min(x1, x2), min(y1, y2)), np.abs(x1 - x2), np.abs(y1 - y2),
                                                    linewidth=1, edgecolor='r', facecolor='none',
                                                    alpha=.8, fill=False)
        # print('coordinates for rect: {},{} : {},{}'.format(min(x1, x2), min(y1, y2), np.abs(x1 - x2), np.abs(y1 - y2)))
        self.axes.add_patch(self.bounding_box_rectangle)
        # self.tableWidget.make_selection(int(x1), int(y1), int(x2), int(y2))
        self.make_selection(int(x1), int(y1), int(x2), int(y2))

    def clear_table_selection(self):
        self.tableWidget.clearSelection()

    def get_mouse_selection(self):
        self.check_for_selection()         # check for and clear any current mouse selection
        self.allow_rect = True             # allow rectangle drawing over the 2d image
        self.draw_2d_image(None, rgb=self.rgb_data_flag, redraw=False)
        self.hideMouseSelectionAction.setDisabled(False)
        self.showMouseSelectionAction.setDisabled(True)
        self.showBoundingBoxAction.setDisabled(True)

    def clear_mouse_selection(self):
        if self.bounding_box_rectangle is not None:
            self.clear_table_selection()
            self.bounding_box_rectangle.remove()
            self.allow_rect = False
            # print("draw_2d_image called from clear_mouse_selection")
            # print("X: from clear_mouse_selection()")
            self.draw_2d_image(None, rgb=self.rgb_data_flag, redraw=False)
            self.hideMouseSelectionAction.setDisabled(True)
            self.showMouseSelectionAction.setDisabled(False)
            self.showBoundingBoxAction.setDisabled(False)

    def reset_state(self):
        self.hideMouseSelectionAction.setDisabled(True)
        self.showMouseSelectionAction.setDisabled(False)
        self.hideBoundingBoxAction.setDisabled(False)
        self.showBoundingBoxAction.setDisabled(False)

    def configure_options(self, pds_type):
        ''' Selectively disable options that cause crashes with
            these types of files.  NOTE: Need to reinforce for
            multiple files with a file loaded.
        '''
        # # Selectively disable options for Array_3D_Image also disabled Color Map Option PDS-740
        if pds_type == 'Array_3D_Spectrum':
            self.colorMapMenu.setDisabled(False)
            self.imageOptionsMenu.setDisabled(False)
            # Disable selectively under imageOptionsMenu
            self.interpolation_sub_menu.setDisabled(False)
            self.normalization_sub_menu.setDisabled(False)
            self.showMouseSelectionAction.setDisabled(True)
            self.hideMouseSelectionAction.setDisabled(True)
        elif pds_type == 'Array_3D_Image':
            self.imageOptionsMenu.setDisabled(False)
            self.interpolation_sub_menu.setDisabled(True)
            self.normalization_sub_menu.setDisabled(True)
            self.colorMapMenu.setDisabled(True)
        elif pds_type == 'Array_2D_Image':
            self.colorMapMenu.setDisabled(False)
            self.imageOptionsMenu.setDisabled(False)
            self.interpolation_sub_menu.setDisabled(False)
            self.normalization_sub_menu.setDisabled(False)
            self.showMouseSelectionAction.setDisabled(False)
            self.hideMouseSelectionAction.setDisabled(False)

    def handle_view_clicked(self, index):
        '''
        This is the call back for the "View" button  being selected in the Summary area
        It will draw the label and the associated table for the selection
        :param index: a QModelIndex used internally by the model uses .row() to get which button is pushed
        :return:
        '''
        # disable multiple file icons in toolbar (they are enabled when a table is selected)
        self.hold_slider_checkBox.setDisabled(True)
        self.nextCubeDataFileAction.setDisabled(True)
        self.lastCubeDataFileAction.setDisabled(True)
        self.tableLabel.setText(QString('Band '))
        self.tableLabel.setDisabled(True)
        self.tableNumDisplay.setDisabled(True)
        self.tableNumDisplay.setText(str(''))

        self.clear_layout(self.imageLayout)
        # self.summary[1] is a list of all the 'Name' fiels in the Summary
        viewType = self.get_type(index, self.summary[1])

        self.tableMenu.setDisabled(False)
        self.labelMenu.setDisabled(False)
        self.searchLabelAction.setDisabled(False)
        self.searchHighlightTableAction.setDisabled(False)
        self.labelActionList['Object Label'].setDisabled(False)
        self.labelActionList['Object Label'].setDisabled(False)
        self.fileSaveTableAsAction.setDisabled(False)  # enable table to be saved.
        self.fileSaveImageAsAction.setDisabled(False)  # enable image saving until turned off my 'non' image file
        self.draw_label(index, viewType)
        self.current_index = index  # These are used when label options change within the same file
        self.view_type = viewType
        self.array_3d_Spectrum = False
        # reset desired state on change
        self.reset_state()

        self.test_for_label()
        # self.adjust_availability()

        if viewType == 'Header':
            self.rgb_data_flag = False
            self.configure_tabs(True, False, True, self.label_index)
            self.tabFrame.setTabEnabled(1, False)
            self.tabFrame.setTabEnabled(2, False)
            self.tabFrame.setTabEnabled(3, True)
            self.colorMapMenu.setDisabled(True)
            self.viewMenu.setDisabled(False)
            self.imageOptionsMenu.setDisabled(True)
            self.tableMenu.setDisabled(True)
            self.labelMenu.setDisabled(True)
            self.tabFrame.setCurrentIndex(self.label_index)    # open up in the label tab

        elif viewType == 'Array_2D_Image' or viewType == 'Array_2D_Map':
            self.rgb_data_flag = False
            if self.cubeData:
                self.tabFrame.setCurrentIndex(self.image_index + 1)  # account for the 3D Table tab
                self.current_view = self.image_index + 1
                self.configure_tabs(True, False, True, self.current_view)
            else:
                self.tabFrame.setCurrentIndex(self.image_index)  # open up in the image tab
                self.current_view = self.image_index
                self.configure_tabs(True, True, True, self.current_view)

            self.configure_options('Array_2D_Image')
            self.draw_table(index)
            self.clear_layout(self.imageLayout)
            self.draw_2d_image(None, rgb=self.rgb_data_flag, redraw=False)

        elif viewType == 'Array_3D_Image':
            self.rgb_data_flag = True
            self.tabFrame.setCurrentIndex(self.table_index)  # Open in RGB Table tab
            self.current_view = self.image_index + 1
            self.configure_tabs(True, True, True, self.current_view)
            self.configure_options('Array_3D_Image')
            self.draw_table(index)
            self.clear_layout(self.imageLayout)
            self.draw_2d_image(None, rgb=self.rgb_data_flag, redraw=False)
            self.rgb_combo.setDisabled(False)

        elif viewType == 'Array_3D_Spectrum':
            self.array_3d_Spectrum = 'True'
            self.set_tab_state(viewType)
            self.rgb_data_flag = False
            self.draw_table(index)
            self.configure_tabs(True, True, True, self.three_d_table_index)
            self.configure_options('Array_3D_Spectrum')
            self.tabFrame.setTabEnabled(3, True)
            self.tabFrame.setCurrentIndex(self.table_index)

        else:  # Table Data with no image
            # print("SEEM TO HAVE FOUND THE RIGHT PLACE, viewtype is: {}".format(viewType))
            self.rgb_data_flag = False
            self.colorMapMenu.setDisabled(True)
            self.viewMenu.setDisabled(False)
            self.imageOptionsMenu.setDisabled(True)
            self.showBoundingBoxAction.setDisabled(True)
            self.hideBoundingBoxAction.setDisabled(True)
            self.tickAction.setDisabled(True)
            self.colorbarAction.setDisabled(True)
            self.fileSaveImageAsAction.setDisabled(True)
            self.configure_tabs(True, True, False, self.table_index)
            self.draw_table(index)

            self.tabFrame.setCurrentIndex(self.table_index)  # open up in the table tab

    def configure_tabs(self, stateOne, stateTwo, stateThree,  view):
        """
        Enable or disable tabs based on data that is being viewed
        :param stateOne: Sets the state of tab1
        :param stateTwo: Sets the state of tab2
        :param stateThree: Sets the state of tab3
        :param view: Tab to open
        :return:
        """
        self.tabFrame.setTabEnabled(0, stateOne)
        self.tabFrame.setTabEnabled(1, stateTwo)
        self.tabFrame.setTabEnabled(2, stateThree)
        self.tabFrame.setTabEnabled(3, True)
        self.current_view = view

    def test_for_label(self, none_returned = []):
        # This is called when a view is changed from the Summary GUI area
        # It enables the label areas that are present for that view, and disables those that are not.

        for label in self.label_options:
            if label[0] == 'separator':
                continue
            ret = self.model.get_label(self.current_index.row(), label[0])

            # print("LABELS RETURNED:  {}".format(ret))

            if ret is None:
                self.labelActionList[label[0]].setDisabled(True)
            else:
                self.labelActionList[label[0]].setDisabled(False)

        if not self.labelWidget.get_empty_label_list():
            return
        else:
            self.select_label('Full Label')  # Decide what we have based on the full lable
            self.adjust_availability()

    def adjust_availability(self):
        '''
        This disables categories in the Label pulldown with a value of 'none'
        :return:
        '''
        # These are the categories to disable
        disable_categories = self.labelWidget.get_empty_label_list()
        if not disable_categories:
            print('list not made yet')
        for cat in disable_categories:
            cat = str(cat.replace('_', ' '))
            self.labelActionList[cat].setDisabled(True)
        # Restore default label
        self.select_label('Object Label')


    def draw_label(self, index, viewType, labelType ='Object Label'):
        '''
        This the method that displays the Label information
        It passes the label information to 'labelWidget' which is then added to the 'labelLayout' which
        is drawn to the labelTab
        :param index: a QModelIndex used internally by the model
        :param viewType: the type of object (described in the object label)
        :param labelType: the part of the label to display
        :return:
        '''

        label = self.model.get_label(index.row(), labelType)

        label_dict = label.to_dict()

        self.labelWidget = LabelFrame(label_dict, self.dimension, index, viewType, labelType)
        self.clear_layout(self.labelLayout)
        self.labelLayout.addWidget(self.labelWidget)
        self.labelTab.setLayout(self.labelLayout)

        self.labelDockWidget = QDockWidget(self.title, self)
        self.labelDockWidget.setObjectName("labelDockWidget")
        # Make the dock widget floatable, but not closeable
        self.labelDockWidget.setFeatures(QDockWidget.DockWidgetFloatable | QDockWidget.DockWidgetMovable)

        self.labelDockWidget.setAllowedAreas(Qt.RightDockWidgetArea | Qt.LeftDockWidgetArea)
        self.labelDockWidget.setFloating(False)

        self.labelDockWidget.setWidget(self.labelWidget)
        self.addDockWidget(Qt.RightDockWidgetArea, self.labelDockWidget)
        self.labelLayout.addWidget(self.labelDockWidget)

    def draw_rgb_image_array(self, dimension, table):
        self.individual_table_dimension = dimension
        color = ('Red', 'Green', 'Blue')
        bg = ('(128, 0, 0)', '(0, 128, 0)', '(0, 0, 128)')
        buttons = {}
        self.clear_layout(self.rgbTableLayout)
        count = 1

        for i in range(3):
            buttons[i] = QPushButton(color[i])
            buttons[i].setObjectName('%d' % count)
            buttons[i].setStyleSheet("background-color: rgb{}; color: rgb(255,255,255)".format(bg[i]))

            self.rgbTableLayout.addWidget(buttons[i], 1, i)
            buttons[i].clicked.connect(lambda: self.handle_3d_table_button_clicked(table))
            count += 1

        self.rgbTableLayout.SetFixedSize
        self.rgbTableLayout.setSpacing(15)
        self.rgbTableLayout.setVerticalSpacing(15)
        self.rgbTableLayout.setSizeConstraint(QLayout.SetFixedSize)
        self.rgbImageTab.setLayout(self.rgbTableLayout)

    def draw_table_array(self, dimension, table):
        '''
        This method sets up the buttons for the '3D Table' tab.  The buttons are to select individual bands
        in a Spectral_Cube_Object.  Clicking a button will update the 'Table' and 'Image' tabs.
        :param dimension: the number of file to make buttons for
        :param table:
        :return:
        '''

        # print("DIMENSION: {}".format(dimension))
        # Individual table dimension is needed when we draw the table in draw_indexed_table()
        # This is a convenient place to pick it up.  Need when there are multiple PDS types in one file
        self.num_tables = dimension[0]
        self.individual_table_dimension = dimension

        # Add the 3D table tab then move the other tabs to accommodate the 2nd position
        # self.tabFrame.setMovable(True)
        # self.tabFrame.tabBar().moveTab(1,2)  # move
        # self.tabFrame.tabBar().moveTab(1,2)
        # self.tabFrame.tabBar().moveTab(3,1)
        # self.tabFrame.setMovable(False)
        # self.threeDTabInPlace = True
        self.clear_layout(self.threeDTableLayout)
        buttons = {}

        self.num_of_bands = dimension[0]

        self.clear_layout(self.threeDTableLayout)

        len_last_row = self.num_of_bands % 10
        rows = self.num_of_bands/10
        count = 1

        # Layout for buttons for a variable number of bands
        for i in range(rows):
            for j in range(10):
                # make rows of buttons
                buttons[i,j] = QPushButton('Band {}'.format(count))
                # This give the button a unique name used in the callback to determine which button was clicked
                buttons[i,j].setObjectName('%d' % count)
                self.threeDTableLayout.addWidget(buttons[(i,j)], i,j)
                buttons[i, j].clicked.connect(lambda: self.handle_3d_table_button_clicked(table))
                count += 1
        # Pick up the left over buttons
        for j in range(len_last_row):
            buttons[rows+1, j] = QPushButton('Band {}'.format(count))
            buttons[rows+1, j].setObjectName('%d' % count)
            self.threeDTableLayout.addWidget(buttons[rows+1,j], rows+1, j)
            buttons[rows+1, j].clicked.connect(lambda: self.handle_3d_table_button_clicked(table))
            count += 1

        self.threeDTableLayout.SetFixedSize
        self.threeDTableLayout.setSpacing(15)
        self.threeDTableLayout.setVerticalSpacing(15)
        self.threeDTableLayout.setSizeConstraint(QLayout.SetFixedSize)
        self.threeDTableTab.setLayout(self.threeDTableLayout)

    def handle_3d_table_button_clicked(self, data):
        # Turn on multiple table functionality on the toolbar
        self.hold_slider_checkBox.setDisabled(False)
        self.nextCubeDataFileAction.setDisabled(False)
        self.lastCubeDataFileAction.setDisabled(False)
        self.tableLabel.setDisabled(False)
        self.tableNumDisplay.setDisabled(False)

        table_index_for_cube_data = self.table_index + 1   # This reflects the current table index
        self.full_table = data  # This is used in a callback for next and previous tables
        self.tabFrame.setTabEnabled(2, True)
        self.tabFrame.setTabEnabled(3, True)
        sending_button = self.sender()
        button_number = int(sending_button.objectName())

        self.tableNumDisplay.setText(str(button_number))

        # print('Button number: {}'.format(button_number))
        # set up the table for rendering
        self.draw_indexed_table(data, button_number - 1)
        self.tabFrame.setCurrentIndex(table_index_for_cube_data)  # open up in the table tab

    def draw_indexed_table(self, table, index):
        self.three_d_table = True

        num_rows = int(self.individual_table_dimension[1])
        num_columns = int(self.individual_table_dimension[2])
        self.table_num = index

        # TODO Need to study the GroupTest.py to make sure I access all I need properly
        table_type = self.summary[1][1]  # this is the type of each array

        title = self.summary[0][0]  # this is the title of the 3D table
        # print(table_type)

        self.tableWidget = ItemTable(self, table[index], title, num_rows, num_columns, self.table_type)

        self.tableWidget.verticalSliderBar.valueChanged.connect(self.slider_moved)
        self.tableWidget.horizontalSliderBar.valueChanged.connect(self.slider_moved)

        self.enable_disable_image_options()

        self.set_model_style()

        # If this is not cleared another table will be added
        # Will allow a button that allows multiple tables to be drawn and undocked for comparison
        self.clear_layout(self.tableLayout)

        self.render_table(title, self.table_num)

        self.draw_indexed_image(table, index)
        # print('return from draw_indexed_image')
        self.current_view = self.three_d_table_index

    def slider_moved(self):
        '''
        Clear the 'Hold Slider Positions' checkbox if slider is manually moved
        :return:
        '''
        self.hold_slider_checkBox.setChecked(False)

    def draw_indexed_image(self, table, index):
        # TODO move this to _init_ after you're sure you've got them all
        #image_types = ('Array_2D_Image', 'Array_3D_Image', 'Array_3D_Spectrum', 'Array_2D_Map')
        if self.table_type in self.image_types:
            self.clear_layout(self.imageLayout)
            self.draw_2d_image(self.full_table, index=index, rgb=self.rgb_data_flag, redraw=False)
            self.current_view = self.image_index + 1

        title = self.summary[0][0]  # this is the title of the 3D table


    def draw_table(self, index):
        '''
        :param index:
        :return:
        '''
        self.table, self.title, dimension, self.table_type, self.table_name = self.model.get_table(index)

        # print('DEBUG')
        # print('table: {}'.format(self.table))
        # print(len(self.table))
        # print('title: {}'.format(self.title))
        # print('dimension: {}'.format(self.dimension))
        # print(type(self.dimension))
        # print('table_type: {}'.format(self.table_type))
        # print('table_name: {}'.format(self.table_name))

        if self.rgb_data_flag:
            self.tableLabel.setText(QString('Channels '))
            # print('tried to change tableLabel')
            self.num_of_channels = dimension[0]
            self.height = dimension[1]
            self.width = dimension[2]

            rgbArray = np.zeros((self.height, self.width, self.num_of_channels), 'uint8')
            rgbArray[..., 0] = self.table[0]
            rgbArray[..., 1] = self.table[1]
            rgbArray[..., 2] = self.table[2]

            self.rgb_image = Image.fromarray(rgbArray)

        # print('TEST dimension: {}'.format(self.tableWidget.mixed_table_shape))
        # print('table_type: {}'.format(self.table_type))
        # print('table_name: {}'.format(self.table_name))

        if dimension == (0, 0):
            self.tableTab.setDisabled(True)
        else:
            self.tableTab.setDisabled(False)
            # If we have an 'Array_3D_Specturm" we have to make a new tab with a button for each element
            if self.table_type == 'Array_3D_Spectrum':
                self.draw_table_array(dimension, self.table)
                # self.current_view = self.three_d_table_index
                return

            elif self.table_type == 'Array_3D_Image':
                self.draw_rgb_image_array(dimension, self.table)

            # check for one dimensional array
            if len(dimension) == 1:
                num_rows = dimension[0]
                num_columns = 1
            else:
                num_rows    = dimension[0]
                num_columns = dimension[1]

            # print(num_rows, num_columns)

            if self.rgb_data_flag:
                # Initialize to the first table in the rbg table array (red)
                self.tableWidget = ItemTable(self, self.table[0], self.title, num_rows, num_columns, self.table_type)
                self.num_tables = len(self.table)
                self.full_table = self.table
            else:
                self.tableWidget = ItemTable(self, self.table, self.title, num_rows, num_columns, self.table_type)

            # print('ok')
            # print(self.tableWidget.shape)
            # print(self.tableWidget.table_type)
            # print(self.tableWidget.table_data_type)
            # print(self.table)
            # print('**************************')
            # print(self.tableWidget.table)

            self.enable_disable_image_options()

            self.set_model_style()

            # print('TITLE : {}'.format(self.title))
            # print('TABLE TYPE : {}'.format(self.table_type))
            #
            self.clear_layout(self.tableLayout)
            self.render_table(self.table_name)

    def enable_disable_image_options(self):
        '''
        Enable or Disabled certain menu/toolbar options for image functionality
        :return:
        '''
        if str(self.tableWidget.table_type) in self.tableWidget.homogeneous_type_files:
            self.histogramAction.setDisabled(False)
            self.kdeHistogramAction.setDisabled(False)
            self.colorbarAction.setDisabled(False)
            self.tickAction.setDisabled(False)
            self.dpiLabel.setDisabled(False)
            self.dpiDisplay.setDisabled(False)
            # print('DPI: {}'.format(self.dpi))
            self.dpiDisplay.insert(str(self.dpi))
        else:
            self.histogramAction.setDisabled(True)
            self.kdeHistogramAction.setDisabled(True)
            self.colorbarAction.setDisabled(True)
            self.tickAction.setDisabled(True)
            self.dpiLabel.setDisabled(True)
            self.dpiDisplay.setDisabled(True)

    def handle_search_button(self):
        # print(self.edit_box.text())
        val = self.edit_box.text()
        val = str(val)

        a = self.table
        # print('Val: {}'.format(val))
        matches = zip(*np.where(a == int(val)))
        # Swap each tuple pair using a generator to memory is conserved in huge arrays
        # This is so the (x,y) values match what the user sees in the table
        display_matches = []
        gen = ((item[1]+1, item[0]+1) for item in matches)
        for j in range(len(matches)):
            display_matches.append(gen.next())
        # print(matches)
        # print('')

        # print(sorted(display_matches))

        parent = QModelIndex()

        if matches:
            results = 'Found %d matches.' % len(matches)
            # clear current selections
            self.tableWidget.clearSelection()
            QMessageBox.information(self, 'Search Results', results)
            for i in matches:
                x = i[1]
                y = i[0]
                # Note: (y,x) identification in the selection Model
                top_left = self.tableWidget.tableModel.index(y, x, parent)
                bottom_right = self.tableWidget.tableModel.index(y, x, parent)
                selection = QItemSelection(top_left, bottom_right)
                self.tableWidget.selectionModel.select(selection, QItemSelectionModel.Select)
        else:
            self.tableWidget.clearSelection()
            results = 'No matches found.'
            QMessageBox.information(self, 'Search Results', results)

        return



    def render_table(self, title, table_num = -1):
        '''
        :param title: The name of title taken from the Label
        :param table_num: Used only idenfitying and rendering individual tables in 3D Spectrum data
        :return:
        '''
        # Add row, column and Title information above the table
        # SEARCH BUTTON REMOVED it is now in a dialog
        #  self.search_button = QPushButton('Search', self)
        #  self.edit_box = QLineEdit(self)

        #  self.search_button.clicked.connect(self.handle_search_button)

        # print(self.tableWidget.table_type)
        # print(self.tableWidget.rows)
        # print(self.tableWidget.shape)
        # print(self.tableWidget.column_keys)

        self.title = title
        self.table_num = table_num

        # print('+++++++++++++++++++++++++++++++++++++++++++++')
        # print(type(self.tableWidget.tableModel._data))
        # print(self.tableWidget.tableModel._data)

        # If this is a member of a 3D spectrum array or cube data, display the table number
        # Account for indexing of arrays starting at 0, while the display starts at 1
        self.table_num += 1

        if self.tableWidget.table_type == 'Array_3D_Spectrum':
            self.hold_slider_checkBox.setChecked(MainWindow.hold_position_checked)
        if self.tableWidget.table_type == 'Array_3D_Image':
            self.hold_slider_checkBox.setChecked(MainWindow.hold_position_checked)
        self.tableInfoLayout.setAlignment(Qt.AlignCenter)

        self.tableInfoLayout.setSpacing(5)
        self.tableInfoLayout.stretch(0)
        self.tableInfoLayout.sizeHint()

        self.tableLayout.addLayout(self.tableInfoLayout)

        self.tableDockWidget = QDockWidget(self.title, self)
        self.tableDockWidget.setObjectName("tableDockWidget")
        self.tableDockWidget.setAllowedAreas(Qt.TopDockWidgetArea)
        # set the dock widget to be able to float, but not able to close
        self.tableDockWidget.setFeatures(QDockWidget.DockWidgetFloatable | QDockWidget.DockWidgetMovable)

        self.tableDockWidget.featuresChanged.connect(self.table_dock_changed)

        self.tableDockWidget.setWidget(self.tableWidget)

        self.addDockWidget(Qt.RightDockWidgetArea, self.tableDockWidget)

        self.tableLayout.addWidget(self.tableDockWidget)

        self.tableTab.setLayout(self.tableLayout)

#        self.summaryLayout.addWidget(self.summaryDockWidget)

#        self.summaryTab.setLayout(self.summaryLayout)

        # print('00000000')
        # print self.title
        # print(self.tableDockWidget.isFloating())
        # print('1111111')

    def table_dock_changed(self):
        print("HERE: I may want to do someting with the rest of the window")

    @staticmethod
    def get_check_box_state(self):
        return MainWindow.hold_position_checked

    def get_next_file(self):
        if self.table_num >= self.num_tables:  # roll over
            self.table_num = 0
            self.change_table(self.table_num)
        elif self.table_num > 0:
            self.change_table(self.table_num)
        # Update toolbar display
        self.tableNumDisplay.setText(str(self.table_num))

    def get_last_file(self):
        # print('enter get_last_file(), self.table_num is {}'.format(self.table_num))
        if self.table_num == 1:    # this is incremented in render_image()
            self.table_num = self.num_tables
            # print('rollover case in get_last_file(), self.table_num is {}'.format(self.table_num))
            self.change_table(self.table_num - 1)    # roll over
        elif self.table_num >= 0:
            # print('regular case in get_last_file(), self.table_num is {}'.format(self.table_num))
            self.change_table(self.table_num - 2)
        # Update toolbar display
        self.tableNumDisplay.setText(str(self.table_num))

    def windows_message_box(self, message):
        message_box = QMessageBox()
        message_box.setText("{} not yet implemented for Windows Operating system.".format(message))
        message_box.setWindowTitle("Not Yet Implemented")
        message_box.setStandardButtons(QMessageBox.Ok)
        message_box.setWindowModality(Qt.ApplicationModal)
        self.set_style_sheet(message_box)
        # x, y = MyHeader.get_mouse_position(self)
        # message_box.move(x + 300, y + 300)
        retVal = message_box.exec_()

    def check_for_active_histogram(self):
        '''
        Allow toggling between kde and regular histograms
        :return:
        '''
        if self.active_histogram:
            self.hist.close()
            self.active_histogram = False

    def render_histogram(self):
        if self.platform == 'Windows':
            self.windows_message_box('Histogram')
        else:
            self.check_for_active_histogram()
            self.hist = Histogram(self.table, self.title, kernel_density_estimation=False)
            self.hist.show()
            self.active_histogram = True

    def render_kde_histogram(self):
        if self.platform == 'Windows':
            self.windows_message_box('KDE Histogram')
        else:
            self.check_for_active_histogram()
            self.hist = Histogram(self.table, self.title, kernel_density_estimation=True)
            self.hist.show()
            self.active_histogram = True

    def table_num_line_edit_entry(self):
        '''
        This is the callback after entering data in the Table line edit box
        :return:
        '''
        index = self.tableNumDisplay.text()
        # ("index: {}".format(index))
        table_index = int(index) - 1    # account for 0 indexing in the table
        self.change_table(table_index)

    def dpi_line_edit_entry(self):
        '''
        This is the callback after entering data in the 'dpi' edit box
        :return:
        '''
        dpi = self.dpiDisplay.text()

        #  TODO make the text_entry only accept numeric values
        self.dpi = float(dpi)
        # print("X: from dpi_line_edit_entry()")
        self.draw_2d_image(None, rgb=self.rgb_data_flag, redraw=True)



    def change_table(self, index):
        '''
        This is the callback for the arrow buttons on the table display for 3D images
        The arrows direct you to the next or previous band.
        :param index: Index into the structure of band data.
        :return:
        '''
        # print("In change_table(), index: {} passed.".format(index))
        # print("self.table is: {}".format(self.table_num))
        # print("Num_tables is: {}".format(self.num_tables))

        # print('Index passed: {}, should correlate with that table'.format(index))

        self.draw_indexed_table(self.full_table, index)  # this call will render the proper image as well

    def hold_slider_positions(self, state):
        if state == Qt.Checked:
            ItemTable.sliderYVal = self.tableWidget.verticalSliderBar.value()
            ItemTable.sliderXVal = self.tableWidget.horizontalSliderBar.value()
            MainWindow.hold_position_checked = True
        elif state == Qt.Unchecked:
            ItemTable.sliderYVal = 0
            ItemTable.sliderXVal = 0
            MainWindow.hold_position_checked = False
        else:
            print('Trouble with holdSliderPosition method')


    # TODO figure out how to freeze/thaw the display for image updates

    def draw_2d_image(self, image_array, index = -1, rgb=False, redraw=False, drawBoundingBox=False):
        if redraw == False:
            if image_array is None:   # called from single image data structure
                self.image_data = self.table
                # print(self.image_data.shape)
            else:
                # Render individual 'band' of 3d Spectrum data
                self.image_data = image_array[index]
                # print('ARRAY data')
                # print(self.image_data.shape)

        # self._settings = {'dpi': self.dpi, 'axes': Model._AxesProperties(), 'selected_axis': 0,
        #                   'is_rgb': False, 'rgb_bands': (None, None, None)}
        self.clear_layout(self.imageLayout)
        self.figure = Figure((10.0, 8.0), dpi=self._settings['dpi'])
        self.imageWidget = FigureCanvas(self.figure)

        self.figure.clear()

        self.axes = self.figure.add_subplot(111)

        self.axes.autoscale_view(True, True, True)
        self.axes.set_xlabel(self.title)

                # Add Toolbar to widget
        nav_toolbar = NavigationToolbar(self.imageWidget, self)
        toolbar = nav_toolbar

        self.imageLayout.addWidget(toolbar)
        #  print(type(image_data))
        #  print(image_data.shape)

        _norm = mpl.colors.Normalize(clip=False)

        _norm.vmin = np.ma.min(self.image_data)
        _norm.vmax = np.ma.max(self.image_data)
        ave = np.ma.average(self.image_data)

        # print("Image data min max: {} / {}".format(_norm.vmin, _norm.vmax))
        # print("Average value: {}".format(ave))

        # self.image_data = self.image_data.T
        self._origin = 'lower'

        if rgb:
            self.image = self.axes.imshow(self.rgb_image, interpolation=self._interpolation,
                                          norm=self._norm, aspect='equal', cmap=self.cmap)
        else:
            self.image = self.axes.imshow(self.image_data, origin=self._origin, interpolation=self._interpolation,
                                          norm=self._norm, aspect='equal', cmap=self.cmap)

        if drawBoundingBox or not self.clear_bounding_box:
            self.rect = patches.Rectangle((self.start_x, self.start_y), self.width, self.height,
                                          linewidth=1, edgecolor='r', facecolor='none')
            self.axes.add_patch(self.rect)

        if self.clear_bounding_box:
            self.rect.set_visible(False)

        if not self.cb_removed:
            self.imageWidget.cbar = self.figure.colorbar(self.image, orientation=self.color_bar_orientation)

        self.current_view = self.image_index
        # print('call render_image from draw_2d_image'))
        self.render_image()

        # print("allow_rect: {}".format(self.allow_rect))
        if self.allow_rect:
            # This is used to drag a bounding box on the image from which a table selection will be made
            self.rs = RectangleSelector(self.axes, self.line_select_callback,
                                        drawtype='box', useblit=False, button=[1],
                                        minspanx=2, minspany=2, spancoords='pixels',
                                        interactive=True)

    def is_floating(self):
        # print('got to top of isFloating')
        # print(self.imageDockWidget.isFloating())
        # self.imageDockWidget.setFloating(True)
        # print('tried to change the status')
        # print(self.imageDockWidget.isFloating())
        # print('did it work?')
        if self.imageDockWidget.isFloating():
            # print('got here to setting isFloating')
            self.imageDockWidget.setFloating(True)
        else:
            self.imageDockWidget.setFloating(False)

    def render_image(self, peripheral=None):
        '''
        :param peripheral: This flag tells the methods to only change the ticks or colorbar
        :return:
        '''

        if peripheral == None:  # Only render these when redrawing the image.
            self.viewMenu.setDisabled(False)
            self.fileSaveImageAsAction.setDisabled(False)
            self.imageDockWidget = QDockWidget(self.title, self)
            self.imageDockWidget.setObjectName("imageDockWidget")
            # Make the dock widget floatable, but not closeable
            self.imageDockWidget.setFeatures(QDockWidget.DockWidgetFloatable | QDockWidget.DockWidgetMovable)

            self.imageDockWidget.setWidget(self.imageWidget)
            self.addDockWidget(Qt.RightDockWidgetArea, self.imageDockWidget)
            self.imageLayout.addWidget(self.imageDockWidget)
            #print("IN RENDERIMAGE:peripheral==NONE")

        elif peripheral == 'ColorBar':
            # print("GOT TO THE RIGHT PLACE")
            # print(self.color_bar_orientation)
            if self.color_bar_orientation != 'hide':
                # the colorbar was removed to change the orientation, so add it back
                #self.cbar = self.figure.colorbar(self.image, orientation=self.color_bar_orientation)
                self.imageWidget.cbar = self.figure.colorbar(self.image, orientation=self.color_bar_orientation)
        elif peripheral == 'Ticks':
            self.axes.get_xaxis().set_visible(self.x_tick_visible)
            self.axes.get_yaxis().set_visible(self.y_tick_visible)
        else:
            pass
        # print('calling is_floating()')

        self.is_floating()
        self.imageDockWidget.sizeHint()
        self.imageWidget.setWindowTitle(QString("Yogi Bear"))
        self.imageTab.setLayout(self.imageLayout)


    def Yogi(self):
        print("Smarter than the average Bear.")

    def BooBoo(self):
        print("I'm a cute little fellow.")

#################################################

    # clear all the layouts before writing a new one
    def clear_layout(self, layout):
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
                # print("Clear layout: " + str(item))
                self.clear_layout(item.layout())
            # remove the item from layout
            layout.removeItem(item)

    # This opens the summary table Gui to allow further selection
    def open_summary_gui(self, fname):
        # print("fname: {}".format(fname))
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

        # Note: If there is a 'Header' type it will be in self.summary[1][0]

        # This table widget will be added to the dock widget

        self.header = ['Name', 'Type', 'Dimension', 'Select']

        # Set the size of the table based on the data filling it
        # Note: self is passed so the SummaryTable can emit a signal back to the MainWindow
        self.summaryTable = SummaryTable(self, len(self.summary[1]), len(self.summary) + 1)

        # Test if this is a 3D array, and needs a 3D Table tab
        # If not remove the tab in case there was one from a previous file
        if self.threeDTabInPlace:
            self.tabFrame.removeTab(1)
            self.threeDTabInPlace = False

        self.summaryTable.set_summary_data(self.header, self.summary)
        self.summaryTable.setTableWidth()
        self.summaryDockWidget.setWindowTitle(self.title)
        self.summaryDockWidget.setWidget(self.summaryTable)
        self.addDockWidget(Qt.RightDockWidgetArea, self.summaryDockWidget)
        self.summaryDockWidget.sizeHint()
        self.summaryTable.sizeHint()
        self.summaryDockWidget.show()
        self.summaryLayout.addWidget(self.summaryDockWidget)
        self.summaryTab.setLayout(self.summaryLayout)
        self.tabFrame.setCurrentIndex(self.summary_index)

        self.file_type = self.summary[1][0]
        if self.file_type == 'Header':
            self.file_type = self.summary[1][1]
        self.set_tab_state(self.file_type)

    def set_tab_state(self, file_type, update=False):
        self.cubeData = False
        self.tabFrame.setCurrentIndex(3)
        for i in range(self.tabFrame.count()):
            self.tabFrame.removeTab(i)
            page = self.tabFrame.currentIndex()
        self.tabFrame.removeTab(0)   # Not sure why I have to do this to clear the tabs


        # print("File Type: {}".format(file_type))

        # Rewrite the tabs each time a file is opened
        self.tabFrame.addTab(self.labelTab, "Label  ")
        self.tabFrame.addTab(self.tableTab, "Table  ")
        self.tabFrame.addTab(self.imageTab, "Image  ")
        self.tabFrame.addTab(self.summaryTab, "Summary  ")
        self.configure_tabs(False, False, False, self.summary_index )
        self.tabFrame.setTabEnabled(self.summary_index, True)

        if file_type == 'Array_3D_Spectrum':
            self.tabFrame.addTab(self.threeDTableTab, "3D Table")
            # Add the 3D table tab then move the other tabs to accommodate the 2nd position
            self.tabFrame.setMovable(True)
            self.tabFrame.tabBar().moveTab(4, 1)
            self.tabFrame.setMovable(False)
            self.threeDTabInPlace = True
            self.cubeData = True
            if update:
                self.configure_tabs(True, True, True, self.summary_index + 1)
            else:
                self.configure_tabs(False, False, False, self.summary_index+1)

        elif file_type == 'Array_3D_Image':
            #print("SET RGB TAB STATE")
            self.tabFrame.addTab(self.rgbImageTab, "RGB Table")
            # Add the 3D table tab then move the other tabs to accommodate the 2nd position
            self.tabFrame.setMovable(True)
            self.tabFrame.tabBar().moveTab(4, 1)
            self.tabFrame.setMovable(False)
            self.threeDTabInPlace = True
            self.cubeData = False
            self.configure_tabs(False, False, False, self.summary_index + 1)

    def set_style_sheet(self, messageBox):
        if MainWindow.style_group == 'Dark Orange':
            messageBox.setStyleSheet("""
                .QMessageBox{
                    Background-color: QLinearGradient(x1: 0, y1: 0, x2: 0, y2: 1, stop: 0 #727272, stop: 0.1 #7f7f7f, stop: 0.5 #8b8b8b, stop: 0.9 #989898, stop: 1 #a5a5a5);
                    font: italic 14pt;
                    border: 1px solid #ffaa00;
                }
                .QPushButton{
                    color: #b1b1b1;
                    background-color: QLinearGradient(x1: 0, y1: 0, x2: 0, y2: 1, stop: 0 #565656, stop: 0.1 #525252, stop: 0.5 #4e4e4e, stop: 0.9 #4a4a4a, stop: 1 #464646);
                    border-width: 1px;
                    border-color: #1e1e1e;
                    border-style: solid;
                    border-radius: 6;
                    padding: 3px;
                    font-size: 12px;

                    padding-left: 15px;
                    padding-right: 15px;
                }

                .QPushButton:hover{
                     border: 2px solid QLinearGradient( x1: 0, y1: 0, x2: 0, y2: 1, stop: 0 #ffa02f, stop: 1 #d7801a);
                }
            """)
        elif MainWindow.style_group == 'Dark Blue':
            messageBox.setStyleSheet("""
                .QMessageBox{
                    Background-color: QLinearGradient(x1: 0, y1: 0, x2: 0, y2: 1, stop: 0 #727272, stop: 0.1 #7f7f7f, stop: 0.5 #8b8b8b, stop: 0.9 #989898, stop: 1 #a5a5a5);
                    font: italic 14pt;
                    border: 1px solid #31c6f7;
                }
                .QPushButton{
                    color: #b1b1b1;
                    background-color: QLinearGradient(x1: 0, y1: 0, x2: 0, y2: 1, stop: 0 #565656, stop: 0.1 #525252, stop: 0.5 #4e4e4e, stop: 0.9 #4a4a4a, stop: 1 #464646);
                    border-width: 1px;
                    border-color: #1e1e1e;
                    border-style: solid;
                    border-radius: 6;
                    padding: 3px;
                    font-size: 12px;

                    padding-left: 15px;
                    padding-right: 15px;
                }

                .QPushButton:hover{
                     border: 2px solid QLinearGradient( x1: 0, y1: 0, x2: 0, y2: 1, stop: 0 #31c6f7, stop: 1 #27a5cf);
                }
            """)
        else:
            messageBox.setStyleSheet("""
                .QMessageBox{ 
                     Background-color: QLinearGradient(x1: 0, y1: 0, x2: 0, y2: 1, stop: 0 #727272, stop: 0.1 #7f7f7f, stop: 0.5 #8b8b8b, stop: 0.9 #989898, stop: 1 #a5a5a5);
                    font: italic 14pt;
                    border: 1px solid #31c6f7;
                }
            """)


class NavigationToolbar(NavigationToolbar2QT):
    # only display the buttons we need
    toolitems = [t for t in NavigationToolbar2QT.toolitems if
                 t[0] in ('Home', 'Pan', 'Zoom', 'Save')]

class Tab(QTabWidget):
    def __init__(self, parent=None):
        #QTabWidget.__init__(self)
        super(Tab, self).__init__(parent)

        self.setMovable(True)
        self.sizeHint()

#       self.tBar = self.tabBar()
#       self.installEventFilter(self)


class DockContents(QWidget):
    _sizehint = None

    def setSizeHint(self, width, height):
        self._sizehint = QSize(width, height)

    def sizeHint(self):
        # ('sizeHint:', self._sizehint)
        if self._sizehint is not None:
            return self._sizehint


class Histogram(QWidget):
    def __init__(self, table, title, kernel_density_estimation=True):
        self.data = table
        self.data = self.data.flatten()

        sns.set_style('darkgrid')
        sns.distplot(self.data, kde=kernel_density_estimation)

        plt.xlabel('Value')
        if kernel_density_estimation:
            plt.ylabel('Density')
        else:
            plt.ylabel('Frequency')
        plt.title(title)

    def show(self):
        plt.show(block=False)

    def close(self):
        plt.close()


class LabelFrame(QWidget, QObject):
    '''
    This class will set up the Label Tab contents
    '''
    parent_child_dict = {}
    def __init__(self, dict, dimension, index, viewType, displayType):
        '''
        :param dict: The Label dictionary that all the data will be drawn from
        :param dimension: The dimension of any table or image associated with the file
        :param index: The Model index, use it's row() method to get which button was pushed
        :param viewType: The first 'Name' field - used to identify any 'Header' that may be part of the file
        :param displayType: The type of label display, 'object', 'full', 'mission_area', 'discipline_area' ..
        '''
        QWidget.__init__(self)

        self.pc_dict_key = 0

        self.dimension = dimension
        self.tree = QTreeView(self)
        layout = QVBoxLayout(self)
        layout.addWidget(self.tree)
        self.setGeometry(200, 80, 850, 720)
        self.name = []
        self.dataType = []
        self.index = index
        self.viewType = viewType
        self.label_categories = ('Object_Label', 'Full_Label', 'Identification_Area', 'Observation_Area',
                                 'Discipline_Area', 'Mission_Area', 'Display Settings', 'Spectral_Characteristics',
                                 'File', 'File_Area_Observational', 'Statistics', 'Reference_List')
        self.empty_labels = []
        root_model = QStandardItemModel()
        self.tree.setModel(root_model)

        self.populate_tree(dict, root_model.invisibleRootItem(), self.name, self.dataType)

        self.tree.setHeaderHidden(True)

        if displayType is not 'full_label':
            self.tree.expandAll()

        # set up a selection model
        self.treeSelectionModel = self.tree.selectionModel()

        # TODO Make the tree selectable so when a table column is double clicked the label item will be selected
        self.tree.setSelectionMode(QAbstractItemView.ExtendedSelection)

    def check_length(self, string):
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

    def populate_tree(self, dict, parent, name, dataType):
        '''
        This recursive method obtains all the data in the nested OrderedDict's
        contained in the Label Dictionary that is passed in.
        This is also used to get label data to be displayed in message boxes (self.parent_child_dict) when columns
        are double clicked.
        :param dict: The Label Dictionary that is made up of nested OrderedDict's
        :param parent: This is the heading that can be clicked to open or close the heirarchy
        :return:
        '''
        #TODO - iteritems() may not work in Python3 - they use iter()  use iterkey()
        new_value = ''  # used to hold modified descriptions data
        try:
            for key, value in dict.iteritems():
                # Test for descriptions without '/n'
                if key == 'description':
                    new_value = self.check_length(value)
                    value = new_value
                if key == 'data_type':
                    self.dataType.append(new_value)
                if key == 'name':
                    # Test of we came accross a group name that has no dataType
                    if len(self.dataType) == len(self.name):
                        self.name.append(value)
                if isinstance(value, OrderedDict):
                    heading = QStandardItem('{}'.format(key))  # get the label heading/parent
                    # This is the beginning of a new Orderedict so start at new entry in the parent_child_dict
                    self.pc_dict_key += 1
                    LabelFrame.parent_child_dict[self.pc_dict_key] = []
                    # The first member in the list is the heading/parent, this cannot be used as a key because it
                    # is not unique
                    LabelFrame.parent_child_dict[self.pc_dict_key].append(str(heading.text()))
                    # This is for the QTreeView in the Label tab
                    parent.appendRow(heading)
                    self.populate_tree(value, heading, self.name, self.dataType)
                elif isinstance(value, list):
                    for i in value:
                        heading = QStandardItem('{}'.format(key))
                        # print('HEADING: {}'.format(heading.text()))
                        self.pc_dict_key += 1
                        LabelFrame.parent_child_dict[self.pc_dict_key] = []  # got to a new title so start a new list
                        LabelFrame.parent_child_dict[self.pc_dict_key].append(str(heading.text()))
                        # This is for the QTreeView in the Label tab
                        parent.appendRow(heading)
                        self.populate_tree(i, heading, self.name, self.dataType)
                else:
                    field = QString('{0}: {1}'.format(key, value))
                    # Test for a major category with a 'None' value (e.g. Mission_Area : None)
                    # Such categories must be grayed out on the Label pull down
                    if self.check_for_none(field):
                        # print("222222222222222222222")
                        # print(field)
                        #for i in self.empty_labels:
                        #    print(str(i))
                        message_box = QMessageBox()

                        #message_box.information(QMessageBox, "Blah BLah")
                        message_box.setText( "Category '{}' has no data to display.".format(field))
                        message_box.setWindowTitle("No Category Information")
                        message_box.setStandardButtons(QMessageBox.Ok)
                        message_box.setWindowModality(Qt.ApplicationModal)
                        self.set_style_sheet(message_box)
                        x, y = MyHeader.get_mouse_position(self)
                        # print(x, y)
                        message_box.move(x+300, y+300)
                        return
                    # print(field)
                    # print LabelFrame.parent_child_dict
                    # append to the list of children under the heading/parent
                    LabelFrame.parent_child_dict[self.pc_dict_key].append(str(field))
                    # This is for the QTreeView in the Label tab
                    child = QStandardItem(field)
                    parent.appendRow(child)
        except AttributeError as error:
            message_box = QMessageBox()
            # message_box.information(QMessageBox, "Blah BLah")
            message_box.setText("Unable to find category while parsing.")
            message_box.setWindowTitle("ERROR: ()".format(error))
            message_box.setStandardButtons(QMessageBox.Ok)
            message_box.setWindowModality(Qt.ApplicationModal)
            self.set_style_sheet(message_box)
            x, y = MyHeader.get_mouse_position(self)
            # print(x, y)
            message_box.move(x + 300, y + 300)

    def check_for_none(self, field):
        '''
        In the label a category may simply be labeled as 'None'
        In this case it has to be caught and acted upon as if it was not even in the label
        Here it is caught and processing of populate_tree() continues, while a list of these catagories
        is captured so they can be disabled in the Label pulldown when the options are rendered.
        :param field:
        :return:
        '''

        get_value = field.split(': ')
        # print('1111111111111111')
        # print(get_value)
        if get_value[1] == 'None' and get_value[0] in self.label_categories:
            self.empty_labels.append(get_value[0])
            return True
        else:
            return False


    # Use method to return the list of categories in the label that have a value of 'None'
    def get_empty_label_list(self):
        return self.empty_labels

    @staticmethod
    def get_selection_model(self):
        return LabelFrame.selectionModel

    @staticmethod
    def get_parent_child_dict():
        # print(LabelFrame.parent_child_dict)
        return LabelFrame.parent_child_dict

    def set_style_sheet(self, messageBox):
        if MainWindow.style_group == 'Dark Orange':
            messageBox.setStyleSheet("""
                .QMessageBox{
                    Background-color: QLinearGradient(x1: 0, y1: 0, x2: 0, y2: 1, stop: 0 #727272, stop: 0.1 #7f7f7f, stop: 0.5 #8b8b8b, stop: 0.9 #989898, stop: 1 #a5a5a5);
                    font: italic 14pt;
                    border: 1px solid #ffaa00;
                }
                .QPushButton{
                    color: #b1b1b1;
                    background-color: QLinearGradient(x1: 0, y1: 0, x2: 0, y2: 1, stop: 0 #565656, stop: 0.1 #525252, stop: 0.5 #4e4e4e, stop: 0.9 #4a4a4a, stop: 1 #464646);
                    border-width: 1px;
                    border-color: #1e1e1e;
                    border-style: solid;
                    border-radius: 6;
                    padding: 3px;
                    font-size: 12px;

                    padding-left: 15px;
                    padding-right: 15px;
                }

                .QPushButton:hover{
                     border: 2px solid QLinearGradient( x1: 0, y1: 0, x2: 0, y2: 1, stop: 0 #ffa02f, stop: 1 #d7801a);
                }
            """)
        elif MainWindow.style_group == 'Dark Blue':
            messageBox.setStyleSheet("""
                .QMessageBox{
                    Background-color: QLinearGradient(x1: 0, y1: 0, x2: 0, y2: 1, stop: 0 #727272, stop: 0.1 #7f7f7f, stop: 0.5 #8b8b8b, stop: 0.9 #989898, stop: 1 #a5a5a5);
                    font: italic 14pt;
                    border: 1px solid #31c6f7;
                }
                .QPushButton{
                    color: #b1b1b1;
                    background-color: QLinearGradient(x1: 0, y1: 0, x2: 0, y2: 1, stop: 0 #565656, stop: 0.1 #525252, stop: 0.5 #4e4e4e, stop: 0.9 #4a4a4a, stop: 1 #464646);
                    border-width: 1px;
                    border-color: #1e1e1e;
                    border-style: solid;
                    border-radius: 6;
                    padding: 3px;
                    font-size: 12px;

                    padding-left: 15px;
                    padding-right: 15px;
                }

                .QPushButton:hover{
                     border: 2px solid QLinearGradient( x1: 0, y1: 0, x2: 0, y2: 1, stop: 0 #31c6f7, stop: 1 #27a5cf);
                }
            """)



class SummaryTable(QTableWidget):
    tableFont = QFont()
    tableFont.setPointSize(14)
    tableFont.setFamily('Arial')

    headerFont = QFont()
    headerFont.setPointSize(14)
    headerFont.setFamily('Arial')

    def __init__(self, f_arg, *args):
        self.object = f_arg   # Needed so it is possible to emit a signal to the main window
        QTableWidget.__init__(self, *args)

        self.setShowGrid(False)
        self.verticalHeader().setVisible(False)
        self.head = self.horizontalHeader()
        self.sizeHint()

    def set_summary_data(self, header, data):
        for row in range(len(data[0])):
            for col in range(len(data) + 1):
                if col < (len(data)):
                    item = QTableWidgetItem(data[col][row])
                    item.setTextAlignment(Qt.AlignHCenter)
                    item.setFont(self.tableFont)
                    item.setFlags(Qt.ItemIsEnabled)  # do not allow the cell to be edited
                    self.head.setResizeMode(row, QHeaderView.ResizeToContents)
                    self.setItem(row, col, item)
                else:
                    self.btn_sell = QPushButton('View')
                    self.btn_sell.setFixedWidth(80)
                    self.btn_sell.setStyleSheet("background-color: rgb(0,150,0); color: rgb(255,255,255)")
                    #  self.head.setResizeMode(3, 90)
                    self.btn_sell.clicked.connect(self.handle_view_button_clicked)
                    self.setCellWidget(row, col, self.btn_sell)
                    self.update()

        # Set Horizontal header labels
        self.setHorizontalHeaderLabels(header)
        for i in range(len(header)):
            self.horizontalHeaderItem(i).setFont(self.headerFont)

    def setTableWidth(self):
        width = self.verticalHeader().width()
        width += self.horizontalHeader().length()
        # print('Width: {}'.format(width))
        if self.verticalScrollBar().isVisible():
            width += self.verticalScrollBar().width()
        width += self.frameWidth() * 20
        self.resize(width, 50)

    @pyqtSlot()
    def handle_view_button_clicked(self):
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
    def __init__(self, orientation, parent=None):
        QHeaderView.__init__(self, orientation,  parent)
        self.cursor = None

    def mouseReleaseEvent(self, QMouseEvent):
        self.cursor = QCursor()
        point = self.cursor.pos()
        MyHeader.xPos = point.x()
        MyHeader.yPos = point.y()

    @staticmethod
    def get_mouse_position(self):
        return MyHeader.xPos, MyHeader.yPos


class ItemTable(QTableView):
    sliderXVal = 0
    sliderYVal = 0

    def __init__(self, f_arg, *args ):
        self.object = f_arg  # Needed so it is possible to emit a signal to the main window
        self.table = args[0]
        self.table_data_type = self.table.dtype
        title = args[1]
        self.rows = args[2]
        self.columns = args[3]
        self.shape = self.table.shape
        self.table_type = args[4]
        self.column_keys = self.table.dtype.names
        self.showHeaders = False
        self.newKey = ''
        self.image_types = ('Array', 'Array_2D_Image', 'Array_3D_Spectrum', 'Array_3D_Image', 'Array_2D_Map')
        self.int_data_types = ('b1', 'i1', 'i2', 'i4', 'i8', 'u1', 'u2', 'u4', 'u8',
                               'uint1', 'uint2', 'uint4', 'uint8',)
        self.float_data_types = ('f2', 'f4', 'f8', 'float16', 'float32', 'float64')
        self.homogeneous_type_files = ('Array', 'Array_2D_Image', 'Array_3D_Spectrum', 'Array_3D_Image', 'Array_2D_Map')
        self.mixed_type_files = ('Table_Delimited', 'Table_Binary', 'Table_Character', 'Table_Delimited')
        self.mixed_table_shape = ()
        self.image_file = False

        QTableView.__init__(self, parent=None)

        self.tableModel = Model.assignTableModel(self.table, self.table_type)

        if self.table_type not in self.image_types:  # No image in this file
            self.image_file = False
            # get the column header information
            # key = title : value = column number
            # print('From table model - the headerDictionary')
            # print(self.tableModel.headerDict)
            # print("")
            self.header_title_map = self.tableModel.headerDict
            # Make a list to be used with the column title display option
            self.header_title_list = list(self.header_title_map.keys())
            self.showHeaders = True
            # Use a custom horizontalHeader that give back cursor position when
            # the mouse button is released on the header.  It is used to place
            # the message box.
            my_header = MyHeader(Qt.Horizontal, self)
            self.setHorizontalHeader(my_header)
        else:
            self.image_file = True

        self.setModel(self.tableModel)
        self.tableSelectionModel = self.selectionModel()
        self.installEventFilter(self)
        self.index = None

        self.sizeHint()
        self.setShowGrid(True)

        # This will be use to display the title of the data of the columnn number selected
        self.horizontalHeader().sectionDoubleClicked.connect(self.display_numeric_column_title)
        self.horizontalHeader().setToolTip("1) Double-click to see label information.\n"
                                           "2) When resizing column: 'Right click' to lock size.")
        self.verticalHeader().setVisible(True)

        self.verticalSliderBar = self.verticalScrollBar()
        self.horizontalSliderBar = self.horizontalScrollBar()

        # Get the slider values of the table
        # Use these values accross instances with the static method below
        ItemTable.sliderXVal, ItemTable.sliderYVal = self.get_slider_values(self)
        self.verticalSliderBar.setValue(ItemTable.sliderYVal)
        self.horizontalSliderBar.setValue(ItemTable.sliderXVal)

        self.doubleClicked.connect(self.get_index)

    # Use this static method to update the slider position for all new instances
    # This allows the user to set the position if he uses the checkbox
    @staticmethod
    def get_slider_values(self):
        return ItemTable.sliderXVal, ItemTable.sliderYVal

    @staticmethod
    def get_selection_model(self):
        return ItemTable.selectionModel

    def format_label_info(self, label_dict):
        entry = '\n'
        for i in label_dict:
            item = i
            if 'description' in i:
                if len(i) > 50:
                    item = item.replace('\n', ' ')
                    item = item.replace('   ', '  ')
                    item = item.replace('  ', ' '
                    )

            entry = entry + '\n ' + item

        return entry

    def set_style_sheet(self, messageBox):
        if MainWindow.style_group == 'Dark Orange':
            messageBox.setStyleSheet("""
                .QMessageBox{
                    Background-color: QLinearGradient(x1: 0, y1: 0, x2: 0, y2: 1, stop: 0 #727272, stop: 0.1 #7f7f7f, stop: 0.5 #8b8b8b, stop: 0.9 #989898, stop: 1 #a5a5a5);
                    font: italic 14pt;
                    border: 1px solid #ffaa00;
                }
                .QPushButton{
                    color: #b1b1b1;
                    background-color: QLinearGradient(x1: 0, y1: 0, x2: 0, y2: 1, stop: 0 #565656, stop: 0.1 #525252, stop: 0.5 #4e4e4e, stop: 0.9 #4a4a4a, stop: 1 #464646);
                    border-width: 1px;
                    border-color: #1e1e1e;
                    border-style: solid;
                    border-radius: 6;
                    padding: 3px;
                    font-size: 12px;

                    padding-left: 15px;
                    padding-right: 15px;
                }

                .QPushButton:hover{
                     border: 2px solid QLinearGradient( x1: 0, y1: 0, x2: 0, y2: 1, stop: 0 #ffa02f, stop: 1 #d7801a);
                }
            """)
        elif MainWindow.style_group == 'Dark Blue':
            messageBox.setStyleSheet("""
                .QMessageBox{
                    Background-color: QLinearGradient(x1: 0, y1: 0, x2: 0, y2: 1, stop: 0 #727272, stop: 0.1 #7f7f7f, stop: 0.5 #8b8b8b, stop: 0.9 #989898, stop: 1 #a5a5a5);
                    font: italic 14pt;
                    border: 1px solid #31c6f7;
                }
                .QPushButton{
                    color: #b1b1b1;
                    background-color: QLinearGradient(x1: 0, y1: 0, x2: 0, y2: 1, stop: 0 #565656, stop: 0.1 #525252, stop: 0.5 #4e4e4e, stop: 0.9 #4a4a4a, stop: 1 #464646);
                    border-width: 1px;
                    border-color: #1e1e1e;
                    border-style: solid;
                    border-radius: 6;
                    padding: 3px;
                    font-size: 12px;

                    padding-left: 15px;
                    padding-right: 15px;
                }

                .QPushButton:hover{
                     border: 2px solid QLinearGradient( x1: 0, y1: 0, x2: 0, y2: 1, stop: 0 #31c6f7, stop: 1 #27a5cf);
                }
            """)

    def display_numeric_column_title(self, index):
        '''
        "displayNumericColumnTitle" is a callback method that is activated when the user
        double clicks on a numeric column header field.  It results in a message box begin
        displayed that shows the "Item Name/Title, repetition number (if it is part of a group),
        and data type".
        :param index: The column number that is clicked in the header of the column
        :return:
        '''
        if self.showHeaders:
            column = index + 1
            title = ''
            messageBox = QMessageBox()
            messageBox.setIcon(QMessageBox.NoIcon)
            # Get the header information
            # Key is column tile, value is column number
            for key in self.header_title_map.iterkeys():
                if column in self.header_title_map[key]:
                    title = "'" + key + "'"  # add single quotes to the title
                    if len(self.header_title_map[key]) > 1:
                        group_index = column - self.header_title_map[key][0]
                        title = title + '\n(repitition ' + str(group_index + 1) + ')'
                    # Check for the case where there is a longer comma separated group name
                    if ',' in str(key):
                        self.newKey = key.split(',')
                        self.newKey = self.newKey[1].strip()
                    else:
                        self.newKey = key

                    for k in LabelFrame.parent_child_dict:
                        name = 'name: ' + self.newKey
                        if name in LabelFrame.parent_child_dict[k]:
                            label_info = self.format_label_info(LabelFrame.parent_child_dict[k])
                    title = title + '\n' + label_info

            messageBox.setText("Column  {}:\n{}".format(column, title))
            messageBox.setWindowTitle("Column Header Information")
            messageBox.setStandardButtons(QMessageBox.Ok)
            messageBox.setWindowModality(Qt.ApplicationModal)
            # TODO - put stylesheets in their own file and import them
            self.set_style_sheet(messageBox)

            x, y = MyHeader.get_mouse_position(self)
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
    def get_index(self):
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
            self.copy_selection()
            return True
        return super(ItemTable, self).eventFilter(source, event)

    def copy_selection(self):
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

    def make_selection(self, x1, y1, x2, y2):
        # print('Using make_selection from ItemTable class.')
        parent = QModelIndex()
        top_left = self.model.index(x1, y1, parent)
        bottom_right = self.model.index(x2, y2, parent)
        selection = QItemSelection(top_left, bottom_right)
        self.selectModel(selection, QItemSelectionModel.Select)

class color:
   PURPLE = '\033[95m'
   CYAN = '\033[96m'
   DARKCYAN = '\033[36m'
   BLUE = '\033[94m'
   GREEN = '\033[92m'
   YELLOW = '\033[93m'
   RED = '\033[91m'
   BOLD = '\033[1m'
   UNDERLINE = '\033[4m'
   END = '\033[0m'

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
        self.mw = MainWindow()
        self.application.setStyle("macintosh")
        self.theme = 'macintosh'
        self.mw.show()

    def full_screen(self):
        self.mw.Maximize(True)

    def min_screen(self):
        self.mw.Maximize(False)

    # This is used to change the 'Theme' with app.setStyle() in a callback
    @staticmethod
    def getQApp(self):
        QApp.app = self.application
        return QApp.app


def main():
    a = QApp()
    app = a.getQApp(a)
    ret = app.exec_()
    sys.exit(ret)

main()
