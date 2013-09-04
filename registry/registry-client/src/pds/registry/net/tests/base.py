# encoding: utf-8
# Copyright 2011 California Institute of Technology. ALL RIGHTS
# RESERVED. U.S. Government Sponsorship acknowledged.

'''PDS Registry: network base communications'''

import urllib2, httplib, pkg_resources
from StringIO import StringIO

_svcs = {}
for name in ('insult', 'lush', 'pds'):
    _svcs[name] = pkg_resources.resource_string(__name__, 'services/%s.json' % name)

_testData = {
    'services':                 '{"start":1,"numFound":3,"results":[%s,%s,%s]}' % (_svcs['lush'], _svcs['insult'], _svcs['pds']),
    'services?start=1&rows=1':  '{"start":1,"numFound":3,"results":[%s]}' % _svcs['lush'],
    'services?start=1&rows=2':  '{"start":1,"numFound":3,"results":[%s,%s]}' % (_svcs['lush'], _svcs['insult']),
    'services?start=1&rows=3':  '{"start":1,"numFound":3,"results":[%s,%s,%s]}' % (_svcs['lush'], _svcs['insult'], _svcs['pds']),
    'services?start=1&rows=20': '{"start":1,"numFound":3,"results":[%s,%s,%s]}' % (_svcs['lush'], _svcs['insult'], _svcs['pds']),
    'services?start=2&rows=1':  '{"start":2,"numFound":3,"results":[%s]}' % _svcs['insult'],
    'services?start=2&rows=2':  '{"start":2,"numFound":3,"results":[%s,%s]}' % (_svcs['insult'], _svcs['pds']),
    'services?start=2&rows=3':  '{"start":2,"numFound":3,"results":[%s,%s]}' % (_svcs['insult'], _svcs['pds']),
    'services?start=2&rows=20': '{"start":2,"numFound":3,"results":[%s,%s]}' % (_svcs['insult'], _svcs['pds']),
    'services?start=3&rows=1':  '{"start":3,"numFound":3,"results":[%s]}' % _svcs['pds'],
    'services?start=3&rows=2':  '{"start":3,"numFound":3,"results":[%s]}' % _svcs['pds'],
    'services?start=3&rows=3':  '{"start":3,"numFound":3,"results":[%s]}' % _svcs['pds'],
    'services?start=3&rows=20': '{"start":3,"numFound":3,"results":[%s]}' % _svcs['pds'],
    'services?start=4&rows=1':  '{"start":4,"numFound":3,"results":[]}',
    'services/urn:sk:radio:lush': _svcs['lush'],
    'services/urn:sk:services:insults:0': _svcs['insult'],
    'services/urn:uuid:0f142be7-e4ab-4495-8a03-aa926ffcc5d3': _svcs['pds'],
}

class _TestHandlerError(urllib2.HTTPError):
    '''Simplified HTTPError that defaults the ``hdrs`` and ``fp``.'''
    def __init__(self, url, code, msg):
        super(_TestHandlerError, self).__init__(url, code, msg, hdrs=None, fp=None)

class _TestHandler(urllib2.BaseHandler):
    '''A test web server that serves up test data for testing. Test.'''
    acceptableTypes = frozenset(['application/json', 'application/*', '*/*'])
    def testscheme_open(self, req):
        fullURL = req.get_full_url()
        if not req.get_selector().startswith('/rs'):
            raise _TestHandlerError(fullURL, httplib.NOT_FOUND, "The testscheme doesn't handle anything outside of /rs")
        kind = req.get_selector()[4:]
        if not kind:
            raise _TestHandlerError(fullURL, httplib.NOT_FOUND, 'The testscheme needs something beyond /rs, like /rs/services')
        clientTypes = frozenset(req.get_header('Accept', 'application/json').split(','))
        if len(self.acceptableTypes & clientTypes) == 0:
            raise _TestHandlerError(fullURL, httplib.NOT_ACCEPTABLE, 'The testscheme can only return application/json')
        if kind not in _testData:
            raise _TestHandlerError(fullURL, httplib.NOT_FOUND, 'Sorry, not found')
        return StringIO(_testData[kind])

urllib2.install_opener(urllib2.build_opener(_TestHandler))
