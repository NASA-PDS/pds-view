# Model - class that provides a uniform interface throught which datat items
# are accessed
import os
import traceback
from pds4_tools import *
from pds4_tools.reader.table_objects import Meta_TableStructure
#from pds4_tools.viewer.widgets.tree import TreeView
import numpy as np
from PyQt4 import QtCore, QtGui
Qt = QtCore.Qt

#from summary_view import open_summary


#structures = pds4_read('../../PDS/SampleImage//virs_cube_64ppd_h02ne.xml')

class SummaryItemsModel(QtCore.QAbstractItemModel):
    def __init__(self, fname):
        print("fname: {}".format(fname))
        if fname == None:
            print("No file to read.")
        else:
            self.fileName = fname
            try:
                self.last_open_dir = os.path.dirname(self.fileName)
                self.structure_list = pds4_read(self.fileName, lazy_load=True, decode_strings=False)

            except Exception as e:
                print("Trouble opening file: %s %s" % (self.fileName, e))

            # get the title
            self.title = 'Data Structure Summary' if len(self.structure_list) > 0 else 'Label'
            self.title += '' if (self.fileName is None) else " for '{0}'".format(self.fileName)


    def get_summary(self, fname=None, from_existing_structures=None):
        id = []
        type = []
        dimension = []
        self.summaryItems = []

        for i, structure in enumerate(self.structure_list):
            id.append(structure.id)
            type.append(structure.type)

            if structure.is_header():
                dimensions_text = '---'
            else:
                dimensions = structure.meta_data.dimensions()
                if structure.is_table():
                    dimensions_text = '{0} cols X {1} rows'.format(dimensions[0], dimensions[1])
                elif structure.is_array():
                    dimensions_text = ' X '.join(str(dim) for dim in dimensions)

            dimension.append(dimensions_text)
            num_of_structures = i + 1

        self.summaryItems.append(id)
        self.summaryItems.append(type)
        self.summaryItems.append(dimension)

        return self.title, self.summaryItems, num_of_structures, dimension


    def get_label(self,index):
        structure_label = self.structure_list[index].label
        label_dict = structure_label.to_dict()
        return label_dict

    def get_table(self, index):

        #TODO check this zero index, may not work for all the different cases
        tableName = str(self.summaryItems[0][index.row()])

        table = self.structure_list[tableName]
        tableType = self.structure_list[tableName].type

        print('tableName: {}'.format(tableName))
        print(type(table))
        title = table.id

        try:
            dimension = table.meta_data.dimensions()
        except AttributeError as e:
            print "No dimenstion is this table."
            dimension = (0,0)

        return table.data, title, dimension, tableType, tableName


#  Allows large numpy arrays to be loaded directly into a tableView
class TwoDImageModel(QtCore.QAbstractTableModel):
    '''
    This class handles the modelling of 2D images
    It is also used for 3D cube data, as the individual slices are 2D images
    '''
    def __init__(self, data, parent=None):
        QtCore.QAbstractTableModel.__init__(self, parent)
        self._data = np.array(data)
        try:
            self.r, self.c = np.shape(self._data)
        except:
            print('Exception: Not a 2D Image Model.')

    def rowCount(self, parent=None):
        return self.r

    def columnCount(self, parent=None):
         return self.c

    # The role tells the model which type of data is being referred to.
    # Qt.DisplayRole indicates the data is to be rendered in the form of text (QString)
    def data(self, index, role=Qt.DisplayRole):
       # print(index.row())
        if index.isValid():
            if role == QtCore.Qt.DisplayRole:
                row_column = tuple([index.row(),index.column()])
                return self._data.item(row_column)
            # This will center Align the data
            elif role == QtCore.Qt.TextAlignmentRole:
                return QtCore.Qt.AlignCenter

        return None



class TableModel(QtCore.QAbstractTableModel):
    '''
    This is the model for other types of tables
        Table Binary, Table Character, Table Delimited
    The structure is flattened so only an index(row) is required
    to display the data.
    '''

    def __init__(self, data, parent=None):
        QtCore.QAbstractTableModel.__init__(self, parent)
        self._data = np.array(data)
        self.groupFinder = []
        self.group_list = []  # the indexes that refer to groups, and a group id
        self.group_set = set()
        self.column_index = 0

        #TODO need to figure our how to read self.sturcture_list[0] in to table (see hello world code)
        # Probalby do it with by making self.structure_list a class variable.

        col_len, self.groupFinder, self.headerDict = self.findGroups()

        temp = np.shape(self._data)
        self.r = temp[0]
        self.c = col_len

        print("IN MODEL")
        print('row count = {}'.format(self.r))
        print('column count = {}'.format(self.c))

    def rowCount(self, parent=None):
        return self.r

    def columnCount(self, parent=None):
        return self.c

    def data(self, index, role=Qt.DisplayRole):
        last_group_id = 1
        if index.isValid():
            if role == QtCore.Qt.DisplayRole:
                data = self.flatten(self._data.item(index.row()))
                write_data = data[index.column()]
                # row_column = tuple([index.row(), index.column()])
                return write_data
                # return self._data.item(row_column)
                self.column_index += 1
            elif role == QtCore.Qt.TextAlignmentRole:
                   return QtCore.Qt.AlignCenter
            # TODO change the color of the grouped
                   #  items in the table
           # elif role == QtCore.Qt.BackgroundRole:
           #     # print self.column_index
           #     if self.isGroup(self.column_index):
           #         return QtCore.Qt.QBrush(Qt.lightGray)
           #     # if index.row() % 2 == 0:
           #     return QtCore.Qt.QBrush(Qt.lightGray)
        return None

    def flatten(self, c):
        newlist = []
        for sublist in c:
            if type(sublist).__module__ == np.__name__:
                array = sublist.tolist()
                for i in array:
                    newlist.append(i)
            else:
                newlist.append(sublist)
        return newlist

    def isGroup(self, col_index):
        if col_index in self.group_set:
            return True
        else:
            return False

    def addToGroupSet(self, index, num_to_add):
        """
        :param index: the placement in the row where the group starts
        :param num_to_add: the length of the group, the indexes to add to the group set
        :param group_id: the particular group we will be changing the color in the display for
        :return:
        """
        row_num = index - 1
        for i in range(row_num, row_num + num_to_add):
            self.group_set.add(i)
        self.group_list.append(index)
        self.group_list.append(self.group_set)

    def make_title_finder(self, list):
        '''
        Make a dictionary with the item name as the key and the are a list of which column is displaying
        this data
        :return: dictionary
        '''
        a = list
        dict = {}
        data = []
        start = 1
        for i in list:
            key = i[0]
            end = start + i[1];
            # make the list of columns associated with the key
            for i in range(start, end):
                data.append(i)
            # update dictionary
            dict[key] = data
            # get ready for the next i
            data = []
            start = end
        return dict

    def findGroups(self):
        '''
        This method finds Groups within the structure
        It then adjusts the column count to accurately represent the number of columns in each row.
        :return:
        '''
        table = self._data
        col_num = 0
        index = 0
        group_num = 0
        self.keys = table.dtype.names
        print"Keys"
        print self.keys
        print"total"
        print table.dtype
        for key in self.keys:
            # print key
            # print(table[key].shape)
            shape = (table[key].shape)
            if len(shape) > 1:
                group_num += 1
                self.groupFinder.append((key, shape[1]))
                self.addToGroupSet(col_num, shape[1])
                col_num += shape[1]
            else:
                self.groupFinder.append((key, 1))
                col_num += 1
            index += 1


        header_dictionary = self.make_title_finder(self.groupFinder)

        return col_num, self.groupFinder, header_dictionary



