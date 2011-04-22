# encoding: utf-8
# Copyright 2011 California Institute of Technology. ALL RIGHTS
# RESERVED. U.S. Government Sponsorship acknowledged.

'''PDS Registry Client: documentation tests for information model.'''

import unittest, doctest

def test_suite():
    return unittest.TestSuite([
        doctest.DocTestSuite(module='pds.registry.model.classes'),
    ])
    
if __name__ == '__main__':
    unittest.main(defaultTest='test_suite')
    
