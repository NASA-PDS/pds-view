# View.py - View class presents the data items from a model on the user screeen
import os
import platform
import types
import csv
import io
import sys
from collections import OrderedDict
import textwrap

import sip
sip.setapi('QVariant',2)
from PyQt4.QtCore import *
from PyQt4.QtGui import *
from pds4_tools import pds4_read
from pds4_tools.reader.table_objects import Meta_TableStructure
import Model
import Delegate
#import Styles
import numpy as np

MAC = "qt_mac_set_native_menubar" in dir()
__version__ = "1.0.1"


class MainWindow(QMainWindow):

    supportedFileFormats = ['.xml']

    font = QFont()
    font.setPointSize(15)
    font.setFamily('Arial')

    hold_position_checked = False

    def __init__(self, parent=None):
        super(MainWindow, self).__init__(parent)

        self.filename = None
        self.title = ''
        self.index = None
        self.model = None

        self.threeDTabInPlace = False

        self.logo = QLabel()
        self.logo.setPixmap(QPixmap("./Icons/PDS_Inspector_LOGO.png"))
        self.setCentralWidget(self.logo)
        self.setAcceptDrops(True)

        # connect to Summary table to recieve a View button clicked signal
        self.connect(self, SIGNAL("SummaryTableTriggered"), self.handleViewClicked)

        # connect to Table widget to recieve row and column information
        self.connect(self, SIGNAL("GetPositionTriggered"), self.handleGetPosition)

        self.tabFrame = Tab()

        self.tabFrame.setEnabled(True)
        self.tabFrame.setGeometry(10,10,555,550)
        self.tabFrame.setStyleSheet("background-color: rgb(240, 255, 255)")

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
        #self.threeDTableInfoLayout = QHBoxLayout

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

        fileOpenAction = self.createAction("&Open...", self.fileOpen,
                QKeySequence.Open, "fileopen",
                "Open a PDS file")

        self.fileMenu = self.menuBar().addMenu("&File")

        self.fileMenuActions = (fileOpenAction, None)

#                                fileSaveAsAction, fileQuitAction, None)
        self.connect(self.fileMenu, SIGNAL("aboutToShow()"),
                     self.updateFileMenu)

        settings = QSettings()
        self.recentFiles = settings.value("RecentFiles") or []
        self.restoreGeometry(settings.value("MainWindow/Geometry",
                                            QByteArray()))
        self.restoreState(settings.value("MainWindow/State",
                                        QByteArray()))

        self.setWindowTitle("PDS Inspector")
        self.setWindowIcon(QIcon("./Icons/MagGlass.png"))


        self.updateFileMenu()

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
                self.clearLayout(self.labelLayout)
                self.clearLayout(self.tableLayout)
                self.clearLayout(self.threeDTableLayout)
                self.openFileGUI(path)
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
        # self.summary[1] is a list of all the 'Name' fiels in the Summary
        viewType = self.getType(index, self.summary[1])
        self.drawView(index, viewType)

        if viewType == 'Header':
            self.tabFrame.setTabEnabled(1, False)
            self.tabFrame.setTabEnabled(2, False)
        elif viewType == 'Array_2D_Image':
            self.tabFrame.setTabEnabled(1, True)
            self.tabFrame.setTabEnabled(2, True)
            self.drawTable(index)
        elif viewType == 'Array_3d_Spectrum':
            self.drawTable(index)
            self.tabFrame.setTabEnabled(1, True)
            self.tabFrame.setTabEnabled(2, True)

        else:  # Table Data with no images
            self.tabFrame.setTabEnabled(1, True)
            self.tabFrame.setTabEnabled(2, False)
            self.drawTable(index)


    def drawView(self, index, viewType):
        '''
        This the method that displays the Label information
        It passes the label information to 'labelWidget' which is then added to the 'labelLayout' which
        is drawn to the labelTab
        :param index: a QModelIndex used internally by the model
        :return:
        '''
        label_dict = self.model.get_label(index.row())
        labelWidget = LabelFrame(label_dict, self.dimension, index, viewType)
        self.clearLayout(self.labelLayout)
        #clear any existing layouts on the scene
        self.labelLayout.addWidget(labelWidget)
        self.labelTab.setLayout(self.labelLayout)

    def handleGetPosition(self, position):
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
                buttons[i,j] = QPushButton('Table {}'.format(count))
                # This give the button a unique name used in the callback to determine which button was clicked
                buttons[i,j].setObjectName('%d' % count)
                self.threeDTableLayout.addWidget(buttons[(i,j)], i,j)
                buttons[i, j].clicked.connect(lambda: self.handle3DTableButtonClicked(table))
                count += 1
        # Pick up the left over buttons
        for j in range(len_last_row):
            buttons[rows+1, j] = QPushButton('Table {}'.format(count))
            buttons[rows+1, j].setObjectName('%d' % count)
            self.threeDTableLayout.addWidget(buttons[rows+1,j], rows+1, j)
            buttons[rows+1, j].clicked.connect(lambda: self.handle3DTableButtonClicked(table))
            count += 1
        self.tabFrame.setTabEnabled(3, True)
        self.threeDTableLayout.SetFixedSize
        self.threeDTableLayout.setSpacing(5)
        self.threeDTableLayout.setVerticalSpacing(5)
        self.threeDTableLayout.setSizeConstraint(QLayout.SetFixedSize)
        self.threeDTableTab.setLayout(self.threeDTableLayout)

    def handle3DTableButtonClicked(self, data):
        self.full_table = data  # This is used in a callback for next and previous tables
     #   try:
        sending_button = self.sender()
        button_number = int(sending_button.objectName())
        print('Button number: {}'.format(button_number))
        #set up the table for rendering
        #print(data[button_number-1])

        print("2")
        print(type(data))
        print(type(button_number))

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

        # TODO Need to study the GroupTest.py to make sure I access all I need properly
        table_type = self.summary[1][1]  # this is the type of each array

        title = self.summary[0][0]  # this is the title of the 3D table

        self.tableWidget = ItemTable(self, table[index], title, num_rows, num_columns, self.table_type)
        self.clearLayout(self.tableLayout)

        self.renderTable(title, table_num = index)


    def drawTable(self, index):
        '''

        :param index:
        :return:
        '''
        table, title, dimension, self.table_type, table_name = self.model.get_table(index)
        if dimension == (0,0):
            self.tableTab.setDisabled(True)
        else:
            self.tableTab.setDisabled(False)
            # If we have an 'Array_3D_Specturm" we have to make a new tab with a button for each element
            if self.table_type == 'Array_3D_Spectrum':
                self.drawTableArray(dimension[0], table)
                return
            num_rows    = dimension[0]
            num_columns = dimension[1]
            self.tableWidget = ItemTable(self, table, title, num_rows, num_columns, self.table_type)
            print('TITLE : {}'.format(title))
            self.clearLayout(self.tableLayout)
            self.renderTable(table_name)


    def renderTable(self, title, table_num = -1):
        '''
        :param title: The name of title taken from the Lable
        :param table_num: Used only idenfitying and rendering individual tables in 3D Spectrum data
        :return:
        '''
        # Add row, column and Title information above the table
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
        table_num += 1
        self.title = QLabel(title)
        self.title.setStyleSheet("""
                  font: 17pt Helvetica;
        """)
        self.checkBox = QCheckBox('Hold Slider Positions')
        if (table_num) > 0:
            # This checkbox
            self.title = QLabel(title)
            self.title.setStyleSheet("""
                          font: 17pt Helvetica;
                """)
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
            self.previousTable.clicked.connect(lambda: self.changeTable(table_num-2))
            self.nextTable = QPushButton('>')
            self.nextTable.setFixedWidth(70)
            self.nextTable.clicked.connect(lambda: self.changeTable(table_num))

            self.tableInfoLayout.addWidget(self.checkBox)
            self.tableInfoLayout.addWidget(self.title)
            self.tableInfoLayout.addWidget(self.previousTable)
            self.tableInfoLayout.addWidget(self.nextTable)
            self.tableInfoLayout.addWidget(tableLabel)
            self.tableInfoLayout.addWidget(self.tableDisplay)
            self.tableDisplay.setText(str(table_num))
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
        self.tableLayout.addWidget(self.tableWidget)
        self.tableTab.setLayout(self.tableLayout)


    @staticmethod
    def getCheckBoxState(self):
        return MainWindow.hold_position_checked


    def changeTable(self, index):
        if index == -1:
            print("Already at the first table.")
            return
        elif index >= self.num_tables:
            print("Already at the last table.")
        else:
            self.drawIndexedTable(self.full_table, index)


    def holdSliderPositions(self, state):
        if state == Qt.Checked:
            ItemTable.sliderYVal = self.tableWidget.verticalSliderBar.value()
            ItemTable.sliderXVal = self.tableWidget.horizontalSliderBar.value()
            MainWindow.hold_position_checked = True
        elif state == Qt.Unchecked:
            print("Unchecked button")
            ItemTable.sliderYVal = 0
            ItemTable.sliderXVal = 0
            MainWindow.hold_position_checked = False
        else:
            print'Trouble with holdSliderPosition method'


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

    def createAction(self, text, slot=None, shortcut=None, icon=None,
                         tip=None, checkable=False, signal="triggered()"):
        action = QAction(text, self)
        if icon is not None:
            action.setIcon(QIcon(":/{}.png".format(icon)))
        if shortcut is not None:
            action.setShortcut(shortcut)
        if tip is not None:
            action.setToolTip(tip)
            action.setStatusTip(tip)
        if slot is not None:
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

    # This method call the QFileDialog to get allow selection of a file to open
    def fileOpen(self):
        # Open File from menu bar
        filename = QFileDialog.getOpenFileNames(self, "Select PDS File")
        fname = map(str, filename)
        self.openFileGUI(fname[0])

    # This opents the summary table Gui to allow further selection
    def openFileGUI(self,fname):
        print("fname: {}".format(fname))
        self.setCentralWidget(self.tabFrame)
        # get the summary data
        self.model = Model.SummaryItemsModel(fname)
        self.title, self.summary, self.num_structs, self.dimension = self.model.get_summary()
        #print self.summary
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
        self.summaryTable.setMinimumSize(550, height)
        self.summaryTable.setSummaryData(self.header, self.summary)
        self.summaryDockWidget.setWindowTitle(self.title)
        self.summaryDockWidget.setWindowIcon(QIcon("./Icons/MagGlass.png"))
        self.summaryDockWidget.setWidget(self.summaryTable)
        self.addDockWidget(Qt.RightDockWidgetArea, self.summaryDockWidget)
        self.summaryDockWidget.show()


    def updateFileMenu(self):
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
                action = QAction(QIcon(":/icon.png"),
                         "&{} {}".format(i + 1, QFileInfo(
                         fname).fileName()), self)
                action.setData(fname)

                self.fileMenu.addAction(action)
        self.fileMenu.addSeparator()
        self.fileMenu.addAction(self.fileMenuActions[-1])


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
    def __init__(self, dict, dimension, index, viewType):
        '''

        :param dict: The Label dictionary that all the data will be drawn from
        :param dimension: The dimension of any table or image associated with the file
        :param index: The Model index, use it's row() method to get which button was pushed
        :param firstName: Thhe first 'Name' field - used to identify any 'Header' that may be part of the file
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
        self.tree.setStyleSheet("""
                  border: 20 px solid black;
                  border-radius: 10px;
                  background-color: lightcyan;
                  font: 12pt Comic Sans MS;
        """)

        self.tree.expandAll()
        self.tree.setEditTriggers(QAbstractItemView.NoEditTriggers)
        self.tree.setSelectionMode(QAbstractItemView.NoSelection)

    # Some descriptions in files have the new line character (\n) others do not
    # if the descriptions is over 70 characters, add new line at as close to 70 as possible.
    # Then return the new string.
    def checkLength(self, string):
        line_max_len = 70
        new_lines = []
        new_string = ''
       # print (string)
        if len(string) > line_max_len:
            if '\n' in string:
                # File is ok
                string.strip()   # remove excess blanks
                #print('ok')
                return string
            else:
                new_lines = textwrap.wrap(string,line_max_len)
                new_string = '\n'.join(str(line) for line in new_lines)
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
        for key, value in dict.iteritems():
            # Test for descriptions without '/n'
            if key == 'description':
                value = self.checkLength(value)
            if key == 'data_type':
                self.dataType.append(value)
                print('data_Type')
            if key == 'name':
                # Test of we came accross a group name that has no dataType
                if len(self.dataType) == len(self.name):
                    self.name.append(value)
                    print('name')
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
        else:
            for i in range(len(self.name)):
                LabelFrame.nameDataTypeDict[self.name[i]] = self.dataType[i]

    @staticmethod
    def getLabelNameDataType():
        print LabelFrame.nameDataTypeDict
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

        stylesheet = "QHeaderView::section{Background-color:rgb(176,224,230);border-radius:24px;padding:24py;}"
        self.setStyleSheet(stylesheet)

        self.setShowGrid(False)
        self.verticalHeader().setVisible(False)
        self.head = self.horizontalHeader()
        self.head.setStretchLastSection(True)
        self.head.setResizeMode(0, QHeaderView.ResizeToContents)
        self.head.setResizeMode(1,QHeaderView.ResizeToContents)
        self.head.setResizeMode(2, QHeaderView.ResizeToContents)
        self.head.setResizeMode(3, QHeaderView.ResizeToContents)


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
        data = args[0]
        title = args[1]
        rows = args[2]
        columns = args[3]
        self.table_type = args[4]
        self.showHeaders = False
        self.newKey = ''
        self.image_types = ['Array_2D_Image', 'Array_3D_Spectrum']

        self.nameDataTypeDict = LabelFrame.getLabelNameDataType()

        #print(data.shape)
        #print(data)
        QTableView.__init__(self, parent = None)
        #print("Table type: {}".format(self.table_type))
        #tableModel = Model.TwoDImageModel(data)

        tableModel = Delegate.assignTableModel(data, self.table_type)

        #try:
        #    tableModel = Delegate.assignTableModel(data, self.table_type)
        #except :
        #    print('Table model not identified properly, check the delegate')
        #    return

        if self.table_type not in self.image_types:
            # get the column header information
            self.header_map = tableModel.headerDict
            # print self.header_map
            self.showHeaders = True
            # Use a custom horizontalHeader that give back cursor position when
            # the mouse button is released on the header.  It is used to place
            # the message box.
            myHeader = MyHeader(Qt.Horizontal, self)
            self.setHorizontalHeader(myHeader)

        self.setModel(tableModel)
        self.installEventFilter(self)

        self.setStyleSheet("""
            background-color: rgb(240, 255, 255);
            gridline-color: rgb(0, 0, 0);
            font: 15pt Helvetica;
            alternate-background-color: rgb(255, 255, 255);
        """)

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
                        print("newKey")
                        print self.newKey
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
            qApp.clipboard().setText(stream.getvalue())



class TextWindow(QTextBrowser):
    textFont = QFont()
    textFont.setPointSize(14)
    textFont.setFamily('Arial')

    def __init__(self, *args):
        QTextBrowser.__init__(self, *args)
        self.setText("Hi Jim")


def main():
    app = QApplication(sys.argv)
    app.setOrganizationName("Jet Propulsion Laboratory")
    app.setOrganizationDomain("jpl.nasa.gov")
    app.setApplicationName("PDS Inspector")
#    app.setWindowIcon(".ICON/boo.png")

    start = MainWindow()
    app.setStyle("Plastique")
    start.show()
    app.exec_()

main()