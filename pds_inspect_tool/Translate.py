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

import pprint
import time
import datetime
import ntpath

import sys
sys.path.append("..")

from airspeed import CachingFileLoader
from pds.core.common import open_pds
from pds.core.parse_duplicate_ids import ParseDuplicateIds
from pds.core.parser_jh import Parser_jh
from pds.core.parser import Parser

class Translate():
    def __init__(self, file_to_convert):
        self.data_pointers = ['^IMAGE', '^TABLE', '^SERIES', '^SPREADSHEET']

        self.ptr_object_dict = {}
        self.ptr_offset_dict = {}

        self.object_type = ''

        ts = time.time()
        iso_date = datetime.datetime.fromtimestamp(ts).strftime('%Y-%m-%d')

        self.full_path = file_to_convert

        # need to get the filename alone without an extension fo the self.generate dictionary
        #if sys.platform.startswith('win'):
        #    file_name = self.full_path.split('\\')[-1]
        #else:
        #    file_name = self.full_path.split('/')[-1]

        self.dir, self.file_name = ntpath.split(self.full_path)
        # print(dir, file_name)

        self.file_name_no_ext = self.file_name.split('.')[0]

        self.generate = {'file_name': self.file_name_no_ext, 'current_date_utc': iso_date, 'model_version': '1.11.0.0'}

        parse_ids = ParseDuplicateIds()
        duplicate_ids = parse_ids.parse(open_pds(self.full_path))

        if duplicate_ids:
            parser = Parser_jh(duplicate_ids, log="./logfile")
        else:
            parser = Parser(log="./logfile")

        self.labels = parser.parse(open_pds(self.full_path))

        # Get RECORD_BYTES value that may be required later
        for key in self.labels:
            if key == 'RECORD_BYTES':
                self.record_bytes = int(self.labels[key])

    def add_to_file_dict(self, file_name, object, dict_name):
        ''' Sets up a dictionary with each key being a filename referenced in a PRT_
            The values are lists made up of object names associated with the dictionary passed in.
        '''
        if not dict_name:  # empty dictionary
            dict_name[file_name] = []
            dict_name[file_name].append(object)
        else:  # look for existing entry
            if file_name in dict_name.keys():  # check to see if the key already exists
                dict_name[file_name].append(object)
            else:
                # add a new elememt to the dict
                dict_name[file_name] = []
                dict_name[file_name].append(object)

    def get_object(self, name):
        obj_name = name.split('_')[-1]
        return obj_name.replace('^', '')

    def get_offset(self, offset):
        split_list = offset.split(',')
        return split_list[1].split('<')[0]

    def represents_int(self, s):
        try:
            int(s)
            return True
        except ValueError:
            return False

    def add_to_file_dict(self, file_name, object, dict_name):
        ''' Sets up a dictionary with each key being a filename referenced in a PRT_
            The values are lists made up of object names associated with the dictionary passed in.
        '''
        if not dict_name:  # empty dictionary
            dict_name[file_name] = []
            dict_name[file_name].append(object)
        else:  # look for existing entry
            if file_name in dict_name.keys():  # check to see if the key already exists
                dict_name[file_name].append(object)
            else:
                # add a new elememt to the dict
                dict_name[file_name] = []
                dict_name[file_name].append(object)

    def convert_to_pds4(self):
        # Make all ^ objects in PDS3 file compliant with PRS4 PTR_
        for key in self.labels.keys():
            if '^' in key:
                pointer_fname = self.file_name  # default to the file name of the label
                associated_object = self.get_object(key)
                # print('*************')
                # print(" '^' in {}".format(key))
                if self.labels[key][0] == '(' and self.labels[key][-1] == ')':  # test for leading and trailing parenthesis
                    no_parenthesis = self.labels[key][1:-1]  # strip out first and last characters
                    self.labels[key] = no_parenthesis
                # Check for additional leading description words in definition (e.g. LABEL_TABLE)
                if '_' in key:
                    temp_key = key.split('_')
                    ptr_key = '^' + temp_key[-1]
                else:
                    ptr_key = key

                self.labels[key] = self.labels[key].replace(' ', '')  # take out all blanks in the string
                self.labels[key] = self.labels[key].replace('"', '')  # take out any double quotes
                self.labels[key] = self.labels[key].replace("'", '')  # take out any sing quotes
                # print("label key: {}".format(self.labels[key]))

                if ptr_key in self.data_pointers:

                    if '<BYTES>' in self.labels[key]:  # handle (^IMAGE = 600 <BYTES>)
                        # print(self.labels[key])
                        if (len(self.labels[key].split(','))) == 1:  # case we need to alter
                            new_string = self.file_name + ',' + self.labels[key]
                            self.labels[key] = new_string
                        else:  # case ("INDEX.TAB",<20BYTES>) no changes needed
                            pointer_fname = self.labels[key].split(',')[0]
                    else:
                        if (len(self.labels[key].split(','))) == 1:  # case ^IMAGE = 12 or ^INDEX_TABLE = "INDEX.TAB"
                            if self.represents_int(self.labels[key]):
                                bytes = self.record_bytes * (int(self.labels[key]) - 1)
                                new_string = self.file_name + ',' + str(bytes) + '<BYTES>'
                                self.labels[key] = new_string
                            else:
                                new_string = self.labels[key] + ',0<BYTES>'
                                pointer_fname = self.labels[key]
                                self.labels[key] = new_string
                        elif (len(self.labels[key].split(','))) == 2:  # case ^SERIES = ("C100306.DAT", 2)
                            # print('6')
                            vals = self.labels[key].split(',')
                            bytes = self.record_bytes * (int(vals[1]) - 1)
                            new_string = vals[0] + ',' + str(bytes) + '<BYTES>'
                            pointer_fname = vals[0]
                            self.labels[key] = new_string
                            # print('%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%')
                            #print(new_string, self.labels[key])
                            # print(4)
                        else:
                            # print('7')
                            print('some kind of error')

                    offset = self.get_offset(self.labels[key])

                new_key = key.replace('^', 'PTR_')  # change pds3 '^' to pds4 'PTR_' in the key
                self.object_type = new_key.split('_')[-1] + '_0'
                if 'HEADER' in self.object_type:
                    continue
                # print('object type: {}'.format(self.object_type))
                self.labels[new_key] = self.labels.pop(key)

                # add to file_object dictionary.
                # Must do it this way because dict[key][nth field in <value>]  syntax is not recognized in Velocity
                # TODO Note: perhaps a better way of doing this is to set up one dictionary as:
                # TODO       ptr_object_offset_dict = {"<fname>": [object, object_offset]
                self.add_to_file_dict(pointer_fname, associated_object, self.ptr_object_dict)
                self.add_to_file_dict(pointer_fname, int(offset), self.ptr_offset_dict)

        # pprint.pprint(label1.keys())
        # pprint.pprint(self.labels)
        # pprint.pprint(self.ptr_object_dict)
        # pprint.pprint(self.ptr_offset_dict)

        # TODO "strip all leading and trailing whitespace in dict. = done
        # TODO "Strip double quotes from entries in dict.  - done

        loader = CachingFileLoader("./templates")
        template = loader.load_template("InspectTemplate.vm")
        map = {'label': self.labels, 'str': str, 'generate': self.generate, 'ptr_object_map': self.ptr_object_dict,
               'ptr_offset_map': self.ptr_offset_dict, 'object_placeholder': self.object_type}

        out = template.merge(map, loader=loader)
        # print(type(out))
        # pprint.pprint(out)

        pds4_xml_file = self.dir + '/' + self.file_name_no_ext + '.xml'

        with open(pds4_xml_file, 'w') as f:
            f.write(out.encode('utf8'))

        return pds4_xml_file