# encoding: utf-8
# Copyright 2011 California Institute of Technology. ALL RIGHTS
# RESERVED. U.S. Government Sponsorship acknowledged.

'''PDS Registry Client: tests.'''

import unittest
import model.tests
import net.tests

def test_suite():
    return unittest.TestSuite([
        model.tests.test_suite(),
        net.tests.test_suite(),
    ])
    
if __name__ == '__main__':
    unittest.main(defaultTest='test_suite')
    
