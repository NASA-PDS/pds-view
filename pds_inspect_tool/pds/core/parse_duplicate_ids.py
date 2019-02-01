#!/usr/bin/env python
# encoding: utf-8
"""
parse_duplicate_ids.py
"""
# from common import open_pds
from reader import Reader

class ParseDuplicateIds(object):
    """Parse PDS files into a dictionary.

       Parse file to look for duplicate IDs so they can be included in a list of directories
       Otherwise they would be overwritten and only the last one would show up

    """
    def __init__(self):
        super(ParseDuplicateIds, self).__init__()
        self._reader = Reader()
        self.multiples = {}
        self.duplicates = []

    def parse(self, source):
        """Parse the source PDS data."""
        self.multiples = self._parse_for_multiple_ids(source)
        return self.multiples

    def get_duplicates(self, d):
        if d[0] == 'OBJECT':
            self.duplicates.append(d[1])
        else:
            return

    def get_multiple_keys(self):
        # Find duplicates in list and return a set of values in the list more than once
        doubles = []
        for i in self.duplicates:
            matches = 0
            for j in self.duplicates:
                if i == j:
                    matches += 1
                if matches > 1:
                    doubles.append(i)
        return set(doubles)

    def _parse_for_multiple_ids(self, source):
        """Parse the PDS header.

        Looking for duplicate keys under a heading.
           e.g. {"TABLE": {"COLUMN": .....}, {"COLUMN": .....}}
        """
        # Check for duplicate key

        keys = []
        self.columns = []

        for record in self._reader.read(source):
            self.get_duplicates(record)
        self.multiples_set = self.get_multiple_keys()
        return self.multiples_set
