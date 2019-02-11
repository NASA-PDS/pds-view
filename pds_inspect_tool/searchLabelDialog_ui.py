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

# -*- coding: utf-8 -*-

# Form implementation generated from reading ui file 'searchLabelDialog.ui'
#
# Created by: PyQt4 UI code generator 4.12
#
# WARNING! All changes made in this file will be lost!

from PyQt4 import QtCore, QtGui

try:
    _fromUtf8 = QtCore.QString.fromUtf8
except AttributeError:
    def _fromUtf8(s):
        return s

try:
    _encoding = QtGui.QApplication.UnicodeUTF8
    def _translate(context, text, disambig):
        return QtGui.QApplication.translate(context, text, disambig, _encoding)
except AttributeError:
    def _translate(context, text, disambig):
        return QtGui.QApplication.translate(context, text, disambig)

class Ui_SearchLabelDialog(object):
    def setupUi(self, SearchLabelDialog):
        SearchLabelDialog.setObjectName(_fromUtf8("SearchLabelDialog"))
        SearchLabelDialog.setWindowModality(QtCore.Qt.NonModal)
        SearchLabelDialog.resize(649, 519)
        sizePolicy = QtGui.QSizePolicy(QtGui.QSizePolicy.Expanding, QtGui.QSizePolicy.Expanding)
        sizePolicy.setHorizontalStretch(1)
        sizePolicy.setVerticalStretch(1)
        sizePolicy.setHeightForWidth(SearchLabelDialog.sizePolicy().hasHeightForWidth())
        SearchLabelDialog.setSizePolicy(sizePolicy)
        SearchLabelDialog.setMaximumSize(QtCore.QSize(10000, 10000))
        font = QtGui.QFont()
        font.setFamily(_fromUtf8("Helvetica"))
        font.setPointSize(14)
        font.setBold(False)
        font.setItalic(False)
        font.setWeight(50)
        SearchLabelDialog.setFont(font)
        SearchLabelDialog.setContextMenuPolicy(QtCore.Qt.DefaultContextMenu)
        SearchLabelDialog.setModal(True)
        self.gridLayout = QtGui.QGridLayout(SearchLabelDialog)
        self.gridLayout.setObjectName(_fromUtf8("gridLayout"))
        self.horizontal_line_top = QtGui.QFrame(SearchLabelDialog)
        self.horizontal_line_top.setFrameShape(QtGui.QFrame.HLine)
        self.horizontal_line_top.setFrameShadow(QtGui.QFrame.Sunken)
        self.horizontal_line_top.setObjectName(_fromUtf8("horizontal_line_top"))
        self.gridLayout.addWidget(self.horizontal_line_top, 7, 0, 1, 3)
        self.value_label = QtGui.QLabel(SearchLabelDialog)
        self.value_label.setObjectName(_fromUtf8("value_label"))
        self.gridLayout.addWidget(self.value_label, 1, 0, 1, 1)
        self.value_line_edit = QtGui.QLineEdit(SearchLabelDialog)
        font = QtGui.QFont()
        font.setItalic(False)
        self.value_line_edit.setFont(font)
        self.value_line_edit.setToolTip(_fromUtf8(""))
        self.value_line_edit.setObjectName(_fromUtf8("value_line_edit"))
        self.gridLayout.addWidget(self.value_line_edit, 3, 0, 1, 2)
        self.results_label = QtGui.QLabel(SearchLabelDialog)
        self.results_label.setObjectName(_fromUtf8("results_label"))
        self.gridLayout.addWidget(self.results_label, 8, 0, 1, 1)
        self.search_button = QtGui.QPushButton(SearchLabelDialog)
        self.search_button.setObjectName(_fromUtf8("search_button"))
        self.gridLayout.addWidget(self.search_button, 20, 2, 1, 1)
        self.cancel_button = QtGui.QPushButton(SearchLabelDialog)
        self.cancel_button.setObjectName(_fromUtf8("cancel_button"))
        self.gridLayout.addWidget(self.cancel_button, 20, 1, 1, 1)
        self.results_textEdit = QtGui.QTextEdit(SearchLabelDialog)
        self.results_textEdit.setObjectName(_fromUtf8("results_textEdit"))
        self.gridLayout.addWidget(self.results_textEdit, 17, 0, 1, 3)
        self.line_2 = QtGui.QFrame(SearchLabelDialog)
        self.line_2.setFrameShape(QtGui.QFrame.HLine)
        self.line_2.setFrameShadow(QtGui.QFrame.Sunken)
        self.line_2.setObjectName(_fromUtf8("line_2"))
        self.gridLayout.addWidget(self.line_2, 18, 0, 1, 3)
        self.case_sensitive_check_box = QtGui.QCheckBox(SearchLabelDialog)
        self.case_sensitive_check_box.setChecked(True)
        self.case_sensitive_check_box.setObjectName(_fromUtf8("case_sensitive_check_box"))
        self.gridLayout.addWidget(self.case_sensitive_check_box, 3, 2, 1, 1)

        self.retranslateUi(SearchLabelDialog)
        QtCore.QMetaObject.connectSlotsByName(SearchLabelDialog)

    def retranslateUi(self, SearchLabelDialog):
        SearchLabelDialog.setWindowTitle(_translate("SearchLabelDialog", "Find Text in Currently Selected Label", None))
        self.value_label.setText(_translate("SearchLabelDialog", "Search for:", None))
        self.results_label.setText(_translate("SearchLabelDialog", "Results", None))
        self.search_button.setText(_translate("SearchLabelDialog", "Search", None))
        self.cancel_button.setText(_translate("SearchLabelDialog", "Done", None))
        self.case_sensitive_check_box.setText(_translate("SearchLabelDialog", "  Case Sensitive", None))


if __name__ == "__main__":
    import sys
    app = QtGui.QApplication(sys.argv)
    SearchLabelDialog = QtGui.QDialog()
    ui = Ui_SearchLabelDialog()
    ui.setupUi(SearchLabelDialog)
    SearchLabelDialog.show()
    sys.exit(app.exec_())

