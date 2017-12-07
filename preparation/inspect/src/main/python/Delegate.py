# Delegate.py - modified controller model
#import os
from pds4_tools import *
from PyQt4 import QtCore, QtGui
Qt = QtCore.Qt


import Model

# Delegate to control custom overwritting of columns in tables
# which have groups.  Groups will be given a unique color so they
# will be obvious in the table
#class BackgroundColorDelegate()

class GroupDelegate(QtGui.QItemDelegate):
    """
    A delegate that repaints groups in tables with a new background color
    This allows the groups to be obvious in the table display
    """
    def __init__(self, parent = None, bg_color = None):
        # The parent is not an optional argument for the delegate as
        # we need to reference it in the paint() method
        self.bg = bg_color
        QtGui.QItemDelegate.__init__(self, parent)

    def paint(self, painter, option, index):
        painter.save()

        # set background color
        painter.setBrush(QtGui.QColor.fromRgb(self.bg[0], self.bg[1], self.bg[2]))
        painter.drawRect(option.rect)
        # TODO pass pen color
        painter.setPen(QtGui.QPen(QtCore.Qt.black))
        value = index.data(QtCore.Qt.DisplayRole)
        text = str(value)
        painter.drawText(option.rect, QtCore.Qt.AlignLeft | QtCore.Qt.AlignCenter, text)
        painter.restore()


class ResizeTableDelegate(QtGui.QItemDelegate):
    '''
    This delegate is used to resize the table to the size of the column title,
    it also re-paints the numberic title to the name/title of the field.
    It also works in reverse.
    '''
    def __init__(self, parent=None, width = None):
        QtGui.QItemDelegate.__init__(self, parent)
        self.width = width
        print ("in delegate")

    def sizeHint(self, option, index):
        print ("in sizeHint")
        size = super(ResizeTableDelegate, self).sizeHint(option, index)
        size.setWidth(self.width)



def sizeHint(self, option, index):
    size = super(ResizeTableDelegate, self).sizeHint(option, index)
    if index.column() == self.stretch_column:
        total_width = self.table.viewport().size().width()
        calc_width = size.width()
        for i in range(self.table.columnCount()):
            if i != index.column():
                option_ = QtGui.QStyleOptionViewItem()
                index_ = self.table.model().index(index.row(), i)
                self.initStyleOption(option_, index_)
                size_ = self.sizeHint(option_, index_)
                calc_width += size_.width()
        if calc_width < total_width:
            size.setWidth(size.width() + total_width - calc_width + 20)
    return size



def assignTableModel(data, data_type):
    if data_type == 'Array_2D_Image':
        #print("Length of data: {}".format(len(data)))
        return Model.TwoDImageModel(data)
    elif data_type == 'Array_3D_Spectrum':
        print 'Array_3D_Spectrum'
       # print("Length of data: {}".format(len(data)))
        return Model.TwoDImageModel(data)
    elif data_type == 'Table_Character':
        return Model.TableModel(data)
    else:
        possible_groups = True
        return Model.TableModel(data)

# Returns true if there may be groups in this type of data
def possible_groups(data_type):
    data_type_with_groups = ('Table_Character', 'Table_Binary', 'Table_Delimited')
    if data_type in data_type_with_groups:
        return True
    else:
        return False