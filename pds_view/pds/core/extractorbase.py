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

#!/usr/bin/env python
# encoding: utf-8
"""
extractorbase.py

Created by Ryan Matthew Balfanz on 2009-05-28.

Copyright (c) 2009 Ryan Matthew Balfanz. All rights reserved.
"""


import unittest


class ExtractorError(Exception):
	"""Base class for exceptions raised by ``ExtractorBase`` and its subclasses."""

	def __init__(self, *args, **kwargs):
		super(ExtractorError, self).__init__(*args, **kwargs)


class ExtractorBase(object):
	"""The base class from which various extractors shall derive.
	
	Programs may define their own extractors by creating a new extractor.
	
	Any subclass should override the ``extract`` method, otherwise a NotImplementedError is raised.
	"""
	def __init__(self, *args, **kwargs):
		super(ExtractorBase, self).__init__(*args, **kwargs)
		pass
		
	def extract(self, *args, **kwargs):
		"""This method should be overwritten by a subclass."""
		raise NotImplementedError


class ExtractorTests(unittest.TestCase):
	"""Unit tests for class ExtractorBase"""
	def setUp(self):
		self.eb = ExtractorBase()
		
	def test_not_implemented(self):
		"""Method ``extract`` must be overloaded"""
		self.assertRaises(NotImplementedError, self.eb.extract)


if __name__ == '__main__':
	unittest.main()