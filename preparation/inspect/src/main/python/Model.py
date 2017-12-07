# Model - class that provides a uniform interface throught which datat items
# are accessed
import os

from pds4_tools import *
from pds4_tools.reader.table_objects import Meta_TableStructure
# from pds4_tools.viewer.widgets.tree import TreeView
import numpy as np
from PyQt4 import QtCore, QtGui

Qt = QtCore.Qt

import Delegate


# from summary_view import open_summary


# structures = pds4_read('../../PDS/SampleImage//virs_cube_64ppd_h02ne.xml')

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
        num_of_structures = 0

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

    def get_display_settings_for_lid(self, local_identifier, label):
        """ Search a PDS4 label for Display_Settings of a data structure with local_identifier.

        Parameters
        ----------
        local_identifier : str or unicode
            The local identifier of the data structure to which the display settings belong.
        label : Label or ElementTree Element
            Label for a PDS4 product with-in which to look for the display settings.

        Returns
    -------
        Label, ElementTree Element or None
            Found Display_Settings section with same return type as *label*, or None if not found.
        """

        matching_display = None

        # Find all the Display Settings classes in the label
        displays = label.findall('.//disp:Display_Settings')
        if not displays:
            return None

        # Find the particular Display Settings for this LID
        for display in displays:

            # Look in both PDS and DISP namespace due to standards changes in the display dictionary
            lid_disp = display.findtext('.//disp:local_identifier_reference')
            lid_pds = display.findtext('.//local_identifier_reference')

            if local_identifier in (lid_disp, lid_pds):
                matching_display = display
                break

        return matching_display

    def get_spectral_characteristics_for_lid(self, local_identifier, label):
        """ Search a PDS4 label for Spectral_Characteristics of a data structure with local_identifier.

        Parameters
    ----------
       local_identifier : str or unicode
            The local identifier of the data structure to which the spectral characteristics belong.
        label : Label or ElementTree Element
            Label for a PDS4 product with-in which to look for the spectral characteristics.

        Returns
    -------
        Label,  ElementTree Element or None
            Found Spectral_Characteristics section with same return type as *label*, or None if not found.
        """

        matching_spectral = None

        # Find all the Spectral Characteristics classes in the label
        spectra = label.findall('.//sp:Spectral_Characteristics')
        if not spectra:
            return None

        # Find the particular Spectral Characteristics for this LID
        for spectral in spectra:

            # There may be multiple local internal references for each spectral data object sharing these
            # characteristics. Also look in both PDS and SP namespace due to standards changes in the spectral
            # dictionary
            references = spectral.findall('sp:Local_Internal_Reference') + \
                         spectral.findall('Local_Internal_Reference')

            for reference in references:

                # Look in both PDS and DISP namespace due to standards changes in the display dictionary
                lid_sp = reference.findtext('sp:local_identifier_reference')
                lid_pds = reference.findtext('local_identifier_reference')

                if local_identifier in (lid_sp, lid_pds):
                    matching_spectral = spectral
                    break

        return matching_spectral

    # Return the the label based on the display type
    def get_label(self, index, display_type):

        label = None
        object_local_identifier = None

        structure_label = self.structure_list[index].label
        full_label = self.structure_list[index].full_label

        if structure_label is not None:
            object_local_identifier = structure_label.findtext('local_identifier')

        # Retrieve which label should be shown
        if display_type == 'Full Label':
            label = full_label

        elif display_type == 'Identification Area':
            label = full_label.find('.//Identification_Area')

        elif display_type == 'Observation Area':
            label = full_label.find('.//Observation_Area')

        elif display_type == 'Discipline Area':
            label = full_label.find('.//Discipline_Area')

        elif display_type == 'Mission Area':
            label = full_label.find('.//Mission_Area')

        elif display_type == 'File info':
            label = full_label.find('.//File')

        elif structure_label is not None:

            if display_type == 'Object Label':
                label = structure_label

            elif display_type == 'Display Settings':
                label = self.get_display_settings_for_lid(object_local_identifier, full_label)

            elif display_type == 'Spectral Characteristics':
                label = self.get_spectral_characteristics_for_lid(object_local_identifier, full_label)

        # label_dict = label.to_dict()
        # return label_dict
        return label

    def get_table(self, index):

        # TODO check this zero index, may not work for all the different cases
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
            dimension = (0, 0)

        return table.data, title, dimension, tableType, tableName


class TwoDImage():
    def __init__(self, data, parent=None):
        QtCore.QAbstractTableModel.__init__(self, parent)
        self._data = np.array(data)

    def get_image_data(self):
        return self._data


class TwoDImageModel(QtCore.QAbstractTableModel):
    '''
    This class handles the modelling of 2D image table data
    Allows large nump[y array to be loaded directly into a tableView
    It is also used for 3D cube data, as the individual slices are 2D images
    It is for the display of pixel data in a table not for image display
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
                row_column = tuple([index.row(), index.column()])
                return self._data.item(row_column)
            # This will center Align the data
            elif role == QtCore.Qt.TextAlignmentRole:
                return QtCore.Qt.AlignCenter

        return None


###########################






###########################
#class TableModel(QtCore.QAbstractItemModel):

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

        # TODO need to figure our how to read self.sturcture_list[0] in to table (see hello world code)
        # Probalby do it with by making self.structure_list a class variable.

        col_len, self.groupFinder, self.headerDict = self.findGroups()

        temp = np.shape(self._data)
        self.r = temp[0]
        self.c = col_len

        #  print("IN MODEL")
        #  print('row count = {}'.format(self.r))
        #  print('column count = {}'.format(self.c))

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
                return write_data
                self.column_index += 1
            elif role == QtCore.Qt.TextAlignmentRole:
                return QtCore.Qt.AlignCenter


    def flatten(self, row):
        '''
        :param row:
        :return:
        This method takes a row of data and checks if there is a nested nparray in the line representing a group
        If a group exists it is mapped sequentially to the next set of columns, thus 'flattening' the row.
        In View.py these groups are given another color so they are very apparent
        '''
        newlist = []

        for sublist in row:
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
        """
        group = set()
        row_num = index
        for i in range(row_num, row_num + num_to_add):
            group.add(i)
        self.group_list.append(group)

    def getGroupSet(self):
        return self.group_list

    def make_title_finder(self, list):
        '''
        Make a dictionary with the item name as the key.
        The value of the key is a list of the columns (if a group) or the column under which it will be displayed.
        for example: {'INDEX': [1], 'TIME': [2], 'DURATION': [3], 'MODE': [4],
                      'GROUP_0, ELECTRON COUNTS': [5, 6, 7, 8, 9, 10, 11, 12, 13, 14]
                      'GROUP_1, ION COUNTS': [15, 16, 17, 18, 19, 20, 21, 22, 23, 24]}
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
        #  print"Keys"
        #  print self.keys
        #  print"total"
        #  print table.dtype
        for key in self.keys:
            # print key
            # print(table[key].shape)
            shape = (table[key].shape)
            print(shape, key)
            group_id = 1
            # shape will only be greater than 1 if there is a group
            if len(shape) > 1:
                group_num += 1
                self.groupFinder.append((key, shape[1]))
                print(self.groupFinder)
                self.addToGroupSet(col_num, shape[1])
                col_num += shape[1]

            else:
                self.groupFinder.append((key, 1))
                col_num += 1
            index += 1

        header_dictionary = self.make_title_finder(self.groupFinder)

        # print(col_num)
        print(self.groupFinder)
        print(header_dictionary)
        return col_num, self.groupFinder, header_dictionary


class ImageModel():
    def __init__(self, parent=None):
        # Create basic data view window
        # super(ImageModel, self).__init__(parent)
        print("Maybe do something here")


class _AxesProperties(object):
    """ Helper class containing data about axes being displayed """

    def __init__(self):
        self.axes_properties = []

    def __getitem__(self, index):

        items = self.axes_properties[index]
        print('items')
        print(items)

        return items

    def __len__(self):
        print('axes length: {}'.format(len(self.axes_properties)))
        return len(self.axes_properties)

    def add_axis(self, name, type, sequence_number, slice, length):

        axis_properties = {'name': name,
                           'type': type,
                           'sequence_number': sequence_number,
                           'slice': slice,
                           'length': length}

        self.axes_properties.append(axis_properties)

    # Finds an axis by property key and value
    def find(self, key, value):

        match = next((d for d in self.axes_properties if d[key] == value), None)

        if match is not None:
            match = match.copy()
        print('match is: {}'.format(match))
        print(self.axes_properties)
        return match

    # Finds the index of an axis by property key and value
    def find_index(self, key, value):

        match = next((i for i, d in enumerate(self.axes_properties) if d[key] == value), None)
        return match

    # For axis having index, sets a key and a value
    def set(self, index, key, value):
        self.axes_properties[index][key] = value

    def copy(self):

        axes_properties = _AxesProperties()

        for axis in self.axes_properties:
            axes_properties.add_axis(axis['name'],
                                     axis['type'],
                                     axis['sequence_number'],
                                     axis['slice'],
                                     axis['length'])

        return axes_properties
