#!/usr/bin/env python
# encoding: utf-8
"""
parser.py

Created by Ryan Matthew Balfanz on 2009-05-27.

Copyright (c) 2009 Ryan Matthew Balfanz. All rights reserved.
"""

# In Python 2.5,
# the with statement is only allowed when the with_statement feature has been enabled.
# It will always be enabled in Python 2.6.
from __future__ import with_statement, print_function

import logging
import sys
import unittest

# from common import open_pds
from reader import Reader


class ParserError(Exception):
    """Base class for exceptions in this module."""

    def __init__(self, *args, **kwargs):
        super(ParserError, self).__init__(*args, **kwargs)


class DuplicateKeyError(ParserError):
    """docstring for DuplicateKeyError"""

    def __init__(self, *args, **kwargs):
        super(DuplicateKeyError, self).__init__(*args, **kwargs)


class IOError(ParserError):
    """Exception raised on I/O errors in this module."""

    def __init__(self, *args, **kwargs):
        super(IOError, self).__init__(*args, **kwargs)


class ParserNode(object):
    """A tree-like node structure to maintain structure within PDS labels."""

    def __init__(self, children=None, parent=None):
        super(ParserNode, self).__init__()
        if not children:
            children = {}

        self.children = children
        self.parent = parent


class Parser_jh(object):
    """Parse PDS files into a dictionary.

    Instances of this module are reusable.

    Parsing a PDS data product results in a dictionary whose keys correspond to unchanged PDS labels --
    i.e. label['RECORD_TYPE'] is not the same as label['record_type'].
    Grouped labels are stored as nested dictionaries, nesting may be arbitrarily deep.
    Internally, these groups are called *containers* and must be in {OBJECT, GROUP}.

    This module makes use of assertions to find bugs and detect poorly formatted files.
    As usual, when an assertion fails an AssertionError is raised. This type of behavior may not be desired
    or expected, since it will halt execution, especially when addressing multiple files in a production environment.
    Assertions are not checked in -O mode, use that to temporarily override this behavior.

    Future versions may do away with assertions altogether and utilize the logging facility.
    Logging is not very mature at this stage, but usable.
    Although this feature is not supported at this time, a future version may perform automatic type conversion
    as per the PDS specification.

    Simple Usage Example

    >>> from parser import Parser
    >>> pdsParser = Parser()
    >>> for f in ['file1.lbl', 'file2.lbl']:
    >>> 	labelDict = pdsParser.parse(open(f, 'rb'))
    """

    def __init__(self, dup_ids = [], log=None):
        """Initialize a reusable instance of the class."""
        super(Parser_jh, self).__init__()
        self._reader = Reader()
        self.dup_ids = dup_ids

        self.log = log
        if log:
            self._init_logging()

    def _init_logging(self):
        """Initialize logging."""
        # Set the message format.
        format = logging.Formatter("%(levelname)s:%(name)s:%(asctime)s:%(message)s")

        # Create the message handler.
        stderr_hand = logging.StreamHandler(sys.stderr)
        stderr_hand.setLevel(logging.DEBUG)
        stderr_hand.setFormatter(format)

        # Create a handler for routing to a file.
        logfile_hand = logging.FileHandler(self.log + '.log')
        logfile_hand.setLevel(logging.DEBUG)
        logfile_hand.setFormatter(format)

        # Create a top-level logger.
        self.log = logging.getLogger(self.log)
        self.log.setLevel(logging.DEBUG)
        self.log.addHandler(logfile_hand)
        self.log.addHandler(stderr_hand)

        self.log.debug('Initializing logger')

    def __str__(self):
        """Print a friendly, user readable representation of an instance."""
        strItems = []
        strItems.append('PDSParser: %s' % (repr(self),))
        return '\n'.join(strItems)

    def parse(self, source):
        """Parse the source PDS data."""
        if self.log: self.log.debug("Parsing '%s'" % (source.name,))
        self._labels = self._parse_header(source)
        if self.log: self.log.debug("Parsed %d top-level labels" % (len(self._labels)))
        return self._labels

    def remove_double_quotes(self, d):
        """Remove double quotes from the output."""
        for key, val in d.iteritems():
            if isinstance(val, dict):
                for k, v in val.iteritems():
                    if '"' in v:
                        d[key] = v.replace('"', '')
            else:
               if '"' in val:
                    d[key] = val.replace('"', '')
        return d

    def strip_dict(self, d):
        """Remove whitespace between all elements of the dictionary."""
        return {key: self.strip_dict(value) if isinstance(value, dict) else value.strip()
                for key, value in d.items()}

    def _parse_header(self, source):
        """Parse the PDS header.

        For grouped data, supported containers belong to {'OBJECT', 'GROUP'}.
        Unidentified containers will be parsed as simple labels and will not create a child dictionary.
        """
        if self.log: self.log.debug('Parsing header')
        CONTAINERS = {'OBJECT': 'END_OBJECT', 'GROUP': 'END_GROUP'}
        CONTAINERS_START = CONTAINERS.keys()
        CONTAINERS_END = CONTAINERS.values()

        root = ParserNode({}, None)
        currentNode = root
        expectedEndQueue = []

        self.columns = []

        for record in self._reader.read(source):
            k, v = record[0], record[1]
            assert k == k.strip() and v == v.strip(), 'Found extraneous whitespace near %s and %s' % (k, v)
            if k in CONTAINERS_START:
                expectedEndQueue.append((CONTAINERS[k], v))
                currentNode = ParserNode({}, currentNode)
            elif k in CONTAINERS_END:
                try:
                    expectedEndQueue.pop()   # get the next one
                    newParent = currentNode.parent
                    newParent.children[v] = currentNode.children
                    if self.dup_ids and v in self.dup_ids:
                        self.key = v
                        holder = dict(newParent.children[v])
                        self.columns.append(holder)
                    currentNode = newParent

                except IndexError:
                    # Verifiy that we are back at the root.
                    assert currentNode.parent is None, 'Parent node is not None.'
            else:
                assert not k.startswith('END_'), 'Detected a possible uncaught nesting %s.' % (k,)
                currentNode.children[k] = v
        assert not expectedEndQueue, 'Detected hanging chads, very gory... %s' % (expectedEndQueue,)

        assert currentNode.parent is None, 'Parent is not None, did not make it back up the tree'
        if 'TABLE' in root.children:
            root.children['TABLE'][self.key] = self.columns

        # Remove any double quotes
        child_values = self.remove_double_quotes(root.children)

        # Now remove all the whitespace
       # child_values = self.strip_dict(child_values)

        return child_values


class ParserTests(unittest.TestCase):
    """Unit tests for class Parser"""

    def setUp(self):
        pass

    def test_no_exceptions(self):
        """Check that all test files are parsed without any Exception"""
        import os

        from common import open_pds

        test_data_dir = '../../../test_data/'
        pds_parser = Parser(log="Parser_Unit_Tests")
        for root, dirs, files in os.walk(test_data_dir):
            for name in files:
                filename = os.path.join(root, name)
                try:
                    labels = pds_parser.parse(open_pds(filename))
                # labels = pdsparser.labels # Old usage, depriciated.
                except Exception, e:
                    # Re-raise the exception, causing this test to fail.
                    raise
                else:
                    # The following is executed if and when control flows off the end of the try clause.
                    assert True


if __name__ == '__main__':
    # unittest.main()

    from common import open_pds

    filename = '../../../testfiles/FHA01118.LBL'
    pds_parser = Parser()
    labels = pds_parser.parse(open_pds(filename))
    # labels = pds_parser.labels # Old usage, depriciated.
    print("Label Keys")
    print(labels.keys())

