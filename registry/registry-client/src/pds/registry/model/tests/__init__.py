# encoding: utf-8
# Copyright 2011 California Institute of Technology. ALL RIGHTS
# RESERVED. U.S. Government Sponsorship acknowledged.

'''PDS Registry Client: information model tests.'''

import unittest
import test_doctests

def test_suite():
    return unittest.TestSuite([
        test_doctests.test_suite(),
        test_classes.test_suite(),
    ])
    
if __name__ == '__main__':
    unittest.main(defaultTest='test_suite')
    
