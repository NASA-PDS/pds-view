# encoding: utf-8
# Copyright 2011 California Institute of Technology. ALL RIGHTS
# RESERVED. U.S. Government Sponsorship acknowledged.

'''PDS Registry Client: documentation tests for network communications.'''

import unittest, doctest

def test_suite():
    return unittest.TestSuite([
        doctest.DocTestSuite(module='pds.registry.net.classes', optionflags=doctest.ELLIPSIS | doctest.REPORT_ONLY_FIRST_FAILURE),
    ])
    
if __name__ == '__main__':
    unittest.main(defaultTest='test_suite')
    
