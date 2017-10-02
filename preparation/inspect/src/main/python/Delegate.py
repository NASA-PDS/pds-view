# Delegate.py - modified controller model
#import os
#from pds4_tools import *
#from PyQt4 import QtCore, QtGui
#Qt = QtCore.Qt


import Model



def assignTableModel(data, data_type):

    print("delegate")
    print(type(data))
    print(type(data_type))

    if data_type == 'Array_2D_Image':
        #print("Length of data: {}".format(len(data)))
        return Model.TwoDImageModel(data)
    elif data_type == 'Array_3D_Spectrum':
        print 'Array_3D_Spectrum'
        print("Length of data: {}".format(len(data)))
       # print data
        return Model.TwoDImageModel(data)
    elif data_type == 'Table_Character':
        return Model.TableModel(data)
    else:
        return Model.TableModel(data)