import re
import ast
from PyQt4.QtCore import *
from PyQt4.QtGui import *
import numpy as np
import matplotlib.pyplot as plt


import searchTableDialog_ui

MAC = True
try:
    from PyQt4.QtGui import qt_mac_set_native_menubar
except ImportError:
    MAC = False


class SearchTableDialog(QDialog, searchTableDialog_ui.Ui_SearchTableDialog):

    def __init__(self, table, tableWidget, parent=None):
        super(SearchTableDialog, self).__init__(parent)
        self.setupUi(self)

        self.tw = tableWidget

        self.table = table
        self.matches = []
        self.indices = []

        self.plot = plt

        self.value = ''
        self.start = ''
        self.end = ''

        self.data_type = ''

        self.range = ()
        self.select_state = {'single': False, 'start': False, 'end': False}

        self.search_range = False

        # mute color of results textEdit box
        p = self.results_textEdit.palette()
        p.setColor(QPalette.Base, QColor(228, 228, 228))
        self.results_textEdit.setPalette(p)


        # Disable 'Locations' and 'Distribution' buttons until search values are entered
        self.locations_button.setDisabled(False)
        self.distribution_button.setDisabled(True)

        # Callbacks for buttons clicked
        self.cancel_button.clicked.connect(self.reject)
        self.search_button.clicked.connect(self.handle_search_clicked)
        self.locations_button.clicked.connect(self.show_results_in_table)
        self.distribution_button.clicked.connect(self.show_distribution)
        self.table_value = ''
        self.state = ''

        self.cb_index = {'integer': 0, 'float': 1, 'string': 2}
        self.combo_box_selection = self.cb_index['integer']

        self.comboBox.currentIndexChanged.connect(self.handle_combo_box_changed)
        self.value_line_edit.setFocus()

        self.set_combo_box_state()

    def handle_combo_box_changed(self):
        '''
        Get the comboBox selection type then prepare for 'string' by making it clear that quotes are expected
        Also disable range searches for strings
        :return:
        '''
        self.combo_box_selection = self.comboBox.currentIndex()
        print(self.combo_box_selection)
        if self.combo_box_selection == self.cb_index['string']:
            # self.value_line_edit.setText('""')
            # self.value_line_edit.setCursorPosition(1)
            self.value_line_edit.setFocus()
            print('set validator.')
            v = QRegExp('[ -~]+')
            validator = QRegExpValidator(v, self.value_line_edit)
            self.value_line_edit.setValidator(validator)
            self.start_value_line_edit.setDisabled(True)
            self.end_value_line_edit.setDisabled(True)
        else:
            self.value_line_edit.clear()
            self.value_line_edit.setCursorPosition(0)
            self.value_line_edit.setFocus()
            self.start_value_line_edit.setDisabled(False)
            self.end_value_line_edit.setDisabled(False)

    def set_combo_box_state(self):
        self.combo_box_data_type = self.get_table_type()
        if self.combo_box_data_type is not None:
            if self.combo_box_data_type in self.tw.int_data_types:
                self.state = 'integer'
                self.comboBox.setCurrentIndex(self.cb_index['integer'])
                self.comboBox.setDisabled(self.cb_index['float'])
                self.comboBox.setDisabled(self.cb_index['string'])
                # set validator to integer
                print('Using Integer validator')
                self.value_line_edit.setValidator(QIntValidator())
                self.value_line_edit.setMaxLength(20)

            elif self.combo_box_data_type in self.tw.float_data_types:
                self.state = 'float'
                self.comboBox.setCurrentIndex(self.cb_index['float'])
                self.comboBox.setDisabled(self.cb_index['integer'])
                self.comboBox.setDisabled(self.cb_index['string'])
                # set validator to double/float
                print('Using Double validator')
                self.value_line_edit.setValidator(QDoubleValidator())

            elif self.combo_box_data_type[0] is 'S':
                self.state = 'string'
                print("Validator should work for any entry.")
                self.comboBox.setCurrentIndex(self.cb_index['string'])
                self.comboBox.setDisabled(self.cb_index['integer'])
                self.comboBox.setDisabled(self.cb_index['float'])
                print('Using String Validator')
                self.value_line_edit.setValidator(0)
            elif self.combo_box_data_type is 'mixed':
                self.comboBox.setCurrentIndex(self.cb_index['integer'])
                self.comboBox.setEnabled(self.cb_index['integer'])
                self.comboBox.setEnabled(self.cb_index['float'])
                self.comboBox.setEnabled(self.cb_index['string'])
                self.value_line_edit.setValidator(QIntValidator())
                self.value_line_edit.setMaxLength(20)

    @pyqtSlot()
    def handle_search_clicked(self):
        # decide if it is a single value search or a range search
        if self.value_line_edit.text() != '':
            self.select_state['single'] = True
        if self.start_value_line_edit.text() != '':
            self.select_state['start'] = True
        if self.end_value_line_edit.text() != '':
            self.select_state['end'] = True

        # Handle various cases, first make sure at least one QLineEdit has some text
        if not any(self.select_state.values()):
            results = 'Nothing entered.'
            QMessageBox.information(self, 'Enter a value to search for', results)
            return

        # Test for select single value
        if self.select_state['single'] and not (self.select_state['start'] or self.select_state['end']):
            self.handle_single_search()
            return
        elif self.select_state['single']:
            results = "Cannot search for both Single Value and Range.\n \nSingle value search run."
            QMessageBox.information(self, 'Unclear selection', results)
            self.start_value_line_edit.clear()
            self.end_value_line_edit.clear()
            self.clear_selection_state()
            self.handle_single_search()
            return

        # Test for range selection
        if not self.select_state['single'] and (self.select_state['start'] and self.select_state['end']):
            self.handle_range_search()
            return
        elif not self.select_state['single']:
            results = "Range search needs both start and end values."
            QMessageBox.information(self, 'Full selection not made', results)
            self.clear_selection_state()
            return

    def clear_selection_state(self):
        self.select_state['single'] = False
        self.select_state['start'] = False
        self.select_state['end'] = False
        # clear the line edit widgets
        self.value_line_edit.clear()
        self.start_value_line_edit.clear()
        self.end_value_line_edit.clear()

    @property
    def test_for_cube(self):
        # When cube data is searched it returns 3 element tuples (x, y, z)
        # The first element is not needed since are are not searching the entire cube
        # If it is not a cube slice, return
        if len(self.matches) == 2:
            return self.matches
        print('before: {}'.format(self.matches))
        print(type(self.matches))
        temp = []
        # generator to grab the last two elements of the tuple
        gen = (item[-2:] for item in self.matches)
        for i in range(len(self.matches)):
            temp.append(gen.next())
        print("TEMP matches: {}".format(temp))
        return temp

    def check_for_numpy_tags(self, type_id):
        # strip tag if it is there
        num_py_tags = ('>', '<')
        if type_id[0] in num_py_tags:
            return type_id[1:]
        else:
            return type_id

    def get_table_type(self):
        print(self.tw.column_keys)
        print('table type in dialog: {}'.format(self.tw.table_type))
        print('data type in numpy: {}'.format(self.tw.table_data_type))
        # num_py_tags = ('>', '<')
        self.data_type = str(self.tw.table_data_type)
        if str(self.tw.table_type) in self.tw.homogeneous_type_files:
            return self.check_for_numpy_tags(self.data_type)
        elif self.data_type[1:14] == 'numpy.record,':
            return 'mixed'
        else:
            print('Fell through get_table_type() in searchTableDialog.py .')


    def find_in_mixed_file(self, t_type):

        #self.set_validator()

        numpy_type_string = self.data_type
        # strip out the desired fields into a list
        # print('Start: {}'.format(self.data_type))
        numpy_type_list = numpy_type_string[:-2].split('[')  # produces a list of 2 string'
        headings = numpy_type_list[1]
        headings = '[' + headings + ']'
        # convert sting that looks likes tuples of headings e.g ('INDEX', 'S4) to actual tuples
        col_types = ast.literal_eval(headings)

        # print(col_types)
        # print(type(col_types))
        # print(col_types[0])
        # print(col_types[0][1])
        # Make 3 list of column numbers of different types: integer, float, and sting
        integers, floats, strings = [], [], []
        for i in range(len(col_types)):
            test_val = self.check_for_numpy_tags(col_types[i][1])
            if test_val in self.tw.int_data_types:
                integers.append(i)
            elif test_val in self.tw.float_data_types:
                floats.append(i)
            else:
                strings.append(i)
        print('ints: {}'.format(integers))
        print('floats: {}'.format(floats))
        print('strings: {}'.format(strings))


        coordinates = self.find(strings)
        # print(self.value)
        # print(self.table[0])
        # print(self.table[0][0])
        # print(type(self.table[1][0]))
        print('Combo_box_selection: {}'.format(self.combo_box_selection))

        for i in range(len(self.table)):
            if self.combo_box_selection is self.cb_index['integer']:
                if not integers:
                    results = 'No integers types found in table.'
                    QMessageBox.information(self, 'Search Results', results)
                    return -1
                else:
                    for j in integers:
                        if self.table[i][j] == int(self.value):
                            self.matches.append((i, j))

            elif self.combo_box_selection is self.cb_index['float']:
                for j in floats:
                    val = str(self.value)   # QStings do not have find() cast to string need below
                    val = val[:-1]
                    # print('Val: {}'.format(self.value))
                    precision = len(val[val.find('.'):])
                    if val in repr(self.table[i][j]):
                    #if self.table[i][j] == int(self.value):
                        self.matches.append((i, j))

            elif self.combo_box_selection is self.cb_index['string']:
                print('String Search')
                for j in strings:
                    if self.value in repr(self.table[i][j]):
                        self.matches.append((i, j))

                print(self.value)

        #for i in range(len(self.table)):
        #    print(self.table[i])
        #float_table = self.table[:floats]
        #print("FLOAT?", float_table)
       # string_table = self.table[strings]
       # print("String?", string_table)
        #print(self.table[:, 0])

        print(self.matches)



    def find(self, find):
        print(self.data_type)


    def find_in_homogeneous_file(self, t_type):
        tbl = self.table
        if t_type in self.tw.int_data_types:
            self.matches = zip(*np.where(tbl == int(self.value)))
        elif t_type in self.tw.float_data_types:
            val = str(self.value)  # QStings do not have find() cast to string
            val = val[:-1]
            print('Val: {}'.format(val))

            precision = len(val[val.find('.'):])
            print('precision is: {}'.format(precision))
            val = float(self.value)
            # print('looking to match: {}'.format(value))
            # print("TO")
            self.matches = zip(*np.where(np.around(tbl, decimals=precision) == val))
            print("Matches")
            print(self.matches)
            self.matches = self.test_for_cube
        else:     #string
            self.matches = zip(*np.where(tbl == self.value))
        #self.test_for_cube

    @pyqtSlot()
    def handle_single_search(self):
        self.search_range = False
        self.value = str(self.value_line_edit.text())
        print('SEARCH')
        print(type(self.value_line_edit))
        self.value = self.value.strip()  # get rid of '\n'
        t_type = self.get_table_type()
        print('TABLE TYPE: {}'.format(t_type))
        print('FROM CALL: {}'.format(self.table.shape))
        if t_type is not None:
            if t_type is not 'mixed':
               self.find_in_homogeneous_file(t_type)
            else:
                print('MIXED')
                self.find_in_mixed_file(t_type)
            self.display_search_results()
            self.clear_selection_state()
            self.distribution_button.setDisabled(False)
        else:
            print('Fell through handle_single_search()  in searchTableDialog.py .')

    @pyqtSlot()
    def handle_range_search(self):
        self.search_range = True
        self.matches = []
        self.start = self.start_value_line_edit.text()
        self.end = self.end_value_line_edit.text()
        # print("{}, {}".format(self.start, self.end))
        a = self.table
        start = int(self.start)
        end = int(self.end)
        # test values swap start and end if start greater than end
        if int(start) > int(end):
            start, end = end, start
        # Put the values into a list
        for find in range(start, end + 1):
            found = zip(*np.where(a == int(find)))
            self.matches = self.matches + found
        self.display_search_results()
        self.clear_selection_state()
        self.distribution_button.setDisabled(False)

    def display_search_results(self):
        display_matches = []
        # Swap each tuple pair using a generator so memory is conserved in large arrays
        print('type: {}'.format(type(self.matches)))
        gen = ((item[0] + 1, item[1] + 1) for item in self.matches)
        for j in range(len(self.matches)):
            display_matches.append(gen.next())
        display_matches = sorted(display_matches)
        print('matches: {}'.format(self.matches))
        print('display: {}'.format(display_matches))
        if self.matches:
            # enable buttons since there are results
            self.locations_button.setDisabled(False)
            self.distribution_button.setDisabled(False)
            if self.search_range:
                search = 'for range of (%s - %s ) ' % (self.start, self.end)
            else:
                search = 'for value %s ' % self.value
            results = 'Found %d matches ' % len(self.matches) + search + 'at table coordinates (row,column) ' \
                                                                         'shown below. \n'
            # Write results to the textEdit widget
            self.results_textEdit.clear()
            # Distinguish between results and coordinates with different colors
            self.results_textEdit.setTextColor(QColor(0, 77, 77))
            self.results_textEdit.insertPlainText(results + '\n')
            self.results_textEdit.setTextColor(QColor(51, 0, 0))
            self.results_textEdit.insertPlainText(", ".join(str(coord) for coord in display_matches))
            # clear the current table selections
            self.tw.clearSelection()
        else:
            self.tw.clearSelection()
            results = 'No matches found for: %s' % str(self.value)
            QMessageBox.information(self, 'Search Results', results)

    def show_results_in_table(self):

        #self.matches = [(0,0), (1,1), (8,2), (3,20)]

        parent = QModelIndex()
        self.tw.clearSelection()
        for i in self.matches:
            x = i[1]
            y = i[0]
            # Note: (y,x) identification in the selection Model
            top_left = self.tw.tableModel.index(y, x, parent)
            bottom_right = self.tw.tableModel.index(y, x, parent)
            selection = QItemSelection(top_left, bottom_right)
            self.tw.tableSelectionModel.select(selection, QItemSelectionModel.Select)

    def show_distribution(self):
        results = "Sorry Distribution' option is not yet implemented."
        QMessageBox.information(self, 'Not implemented yet.', results)

    def get_indices(self):
        return self.matches


