# encoding: utf-8
# Copyright 2011–2016 California Institute of Technology. ALL RIGHTS
# RESERVED. U.S. Government Sponsorship acknowledged.

'''PDS Registry: network base communications'''

import urllib2, httplib, pkg_resources
from StringIO import StringIO

_svcs = {}
for name in ('insult', 'lush', 'pds'):
    _svcs[name] = pkg_resources.resource_string(__name__, 'services/%s.json' % name)
_exts = {}
for name in ('egg', 'spam', 'bacon'):
    _exts[name] = pkg_resources.resource_string(__name__, 'extrinsics/%s.json' % name)
_ass = {}
for name in ('ass', 'but', 'boo'):
    _ass[name] = pkg_resources.resource_string(__name__, 'associations/%s.json' % name)
_pkg = {}
for name in ('sml', 'med', 'lrg'):
    _pkg[name] = pkg_resources.resource_string(__name__, 'packages/%s.json' % name)

_testData = {
    'extrinsics':                '{"start":1,"numFound":3,"results":[%s,%s,%s]}' % (_exts['egg'], _exts['spam'], _exts['bacon']),
    'extrinsics?start=1&rows=1': '{"start":1,"numFound":3,"results":[%s]}' % _exts['egg'],
    'extrinsics?start=1&rows=2': '{"start":1,"numFound":3,"results":[%s,%s]}' % (_exts['egg'], _exts['spam']),
    'extrinsics?start=1&rows=3': '{"start":1,"numFound":3,"results":[%s,%s,%s]}' % (_exts['egg'], _exts['spam'], _exts['bacon']),
    'extrinsics?start=1&rows=20':'{"start":1,"numFound":3,"results":[%s,%s,%s]}' % (_exts['egg'], _exts['spam'], _exts['bacon']),
    'extrinsics?start=2&rows=1': '{"start":2,"numFound":3,"results":[%s]}' % _exts['spam'],
    'extrinsics?start=2&rows=2': '{"start":2,"numFound":3,"results":[%s,%s]}' % (_exts['spam'], _exts['bacon']),
    'extrinsics?start=2&rows=3': '{"start":2,"numFound":3,"results":[%s,%s]}' % (_exts['spam'], _exts['bacon']),
    'extrinsics?start=2&rows=20':'{"start":2,"numFound":3,"results":[%s,%s]}' % (_exts['spam'], _exts['bacon']),
    'extrinsics?start=3&rows=1': '{"start":3,"numFound":3,"results":[%s]}' % _exts['bacon'],
    'extrinsics?start=3&rows=2': '{"start":3,"numFound":3,"results":[%s]}' % _exts['bacon'],
    'extrinsics?start=3&rows=3': '{"start":3,"numFound":3,"results":[%s]}' % _exts['bacon'],
    'extrinsics?start=3&rows=20':'{"start":3,"numFound":3,"results":[%s]}' % _exts['bacon'],
    'extrinsics?start=4&rows=1': '{"start":4,"numFound":3,"results":[]}',
    'extrinsics/egg-1.0':                   _exts['egg'],
    'extrinsics/logicals/egg':              '{"start":null,"numFound":null,"results":[%s]}' % _exts['egg'], # WHY?
    'extrinsics/logicals/egg/earliest':     _exts['egg'],
    'extrinsics/logicals/egg/latest':       _exts['egg'],
    'extrinsics/spam-1.0':                  _exts['spam'],
    'extrinsics/logicals/spam':             '{"start":null,"numFound":null,"results":[%s]}' % _exts['spam'],
    'extrinsics/logicals/spam/earliest':    _exts['spam'],
    'extrinsics/logicals/spam/latest':      _exts['spam'],
    'extrinsics/bacon-1.0':                 _exts['bacon'],
    'extrinsics/logicals/bacon':            '{"start":null,"numFound":null,"results":[%s]}' % _exts['bacon'],
    'extrinsics/logicals/bacon/earliest':   _exts['bacon'],
    'extrinsics/logicals/bacon/latest':     _exts['bacon'],
    'extrinsics?lid=bacon&versionName=1.0': '{"start":1,"numFound":1,"results":[%s]}' % _exts['bacon'],
    'extrinsics?versionName=1.0&lid=bacon': '{"start":1,"numFound":1,"results":[%s]}' % _exts['bacon'],
    'services':                  '{"start":1,"numFound":3,"results":[%s,%s,%s]}' % (_svcs['lush'], _svcs['insult'], _svcs['pds']),
    'services?start=1&rows=1':   '{"start":1,"numFound":3,"results":[%s]}' % _svcs['lush'],
    'services?start=1&rows=2':   '{"start":1,"numFound":3,"results":[%s,%s]}' % (_svcs['lush'], _svcs['insult']),
    'services?start=1&rows=3':   '{"start":1,"numFound":3,"results":[%s,%s,%s]}' % (_svcs['lush'], _svcs['insult'], _svcs['pds']),
    'services?start=1&rows=20':  '{"start":1,"numFound":3,"results":[%s,%s,%s]}' % (_svcs['lush'], _svcs['insult'], _svcs['pds']),
    'services?start=2&rows=1':   '{"start":2,"numFound":3,"results":[%s]}' % _svcs['insult'],
    'services?start=2&rows=2':   '{"start":2,"numFound":3,"results":[%s,%s]}' % (_svcs['insult'], _svcs['pds']),
    'services?start=2&rows=3':   '{"start":2,"numFound":3,"results":[%s,%s]}' % (_svcs['insult'], _svcs['pds']),
    'services?start=2&rows=20':  '{"start":2,"numFound":3,"results":[%s,%s]}' % (_svcs['insult'], _svcs['pds']),
    'services?start=3&rows=1':   '{"start":3,"numFound":3,"results":[%s]}' % _svcs['pds'],
    'services?start=3&rows=2':   '{"start":3,"numFound":3,"results":[%s]}' % _svcs['pds'],
    'services?start=3&rows=3':   '{"start":3,"numFound":3,"results":[%s]}' % _svcs['pds'],
    'services?start=3&rows=20':  '{"start":3,"numFound":3,"results":[%s]}' % _svcs['pds'],
    'services?start=4&rows=1':   '{"start":4,"numFound":3,"results":[]}',
    'services/urn:sk:radio:lush': _svcs['lush'],
    'services/urn:sk:services:insults:0': _svcs['insult'],
    'services/urn:uuid:0f142be7-e4ab-4495-8a03-aa926ffcc5d3': _svcs['pds'],
    'associations': '{"start":1,"numFound":3,"results":[%s,%s,%s]}' % (_ass['ass'],_ass['but'],_ass['boo']),
    'associations?start=1&rows=1': '{"start":1,"numFound":3,"results":[%s]}' % _ass['ass'],
    'associations?start=1&rows=2': '{"start":1,"numFound":3,"results":[%s,%s]}' % (_ass['ass'],_ass['but']),
    'associations?start=1&rows=3': '{"start":1,"numFound":3,"results":[%s,%s,%s]}' % (_ass['ass'],_ass['but'],_ass['boo']),
    'associations?start=1&rows=20':'{"start":1,"numFound":3,"results":[%s,%s,%s]}' % (_ass['ass'],_ass['but'],_ass['boo']),
    'associations?start=2&rows=1': '{"start":2,"numFound":3,"results":[%s]}' % _ass['but'],
    'associations?start=2&rows=2': '{"start":2,"numFound":3,"results":[%s,%s]}' % (_ass['but'],_ass['boo']),
    'associations?start=2&rows=3': '{"start":2,"numFound":3,"results":[%s,%s]}' % (_ass['but'],_ass['boo']),
    'associations?start=2&rows=20':'{"start":2,"numFound":3,"results":[%s,%s]}' % (_ass['but'],_ass['boo']),
    'associations?start=3&rows=1': '{"start":3,"numFound":3,"results":[%s]}' % _ass['boo'],
    'associations?start=3&rows=2': '{"start":3,"numFound":3,"results":[%s]}' % _ass['boo'],
    'associations?start=3&rows=3': '{"start":3,"numFound":3,"results":[%s]}' % _ass['boo'],
    'associations?start=3&rows=20':'{"start":3,"numFound":3,"results":[%s]}' % _ass['boo'],
    'associations?start=4&rows=1': '{"start":4,"numFound":3,"results":[]}',
    'associations/urn:anatomyid:ass': _ass['ass'],
    'associations/urn:anatomyid:but': _ass['but'],
    'associations/urn:anatomyid:boo': _ass['boo'],
    'associations?start=1&sourceObject=urn%3Auuid%3A8007636f-adcd-416e-a75b-e954814bd953&rows=20':
        '{"start":1,"numFound":3,"results":[%s,%s,%s]}' % (_ass['ass'], _ass['but'], _ass['boo']),
    'associations?start=1&rows=20&targetObject=urn%3Auuid%3A2cdad332-f667-4e8b-814a-4b67624c4e2a':
        '{"start":1,"numFound":1,"results":[%s]}' % _ass['ass'],
    'packages': '{"start":1,"numFound":3,"results":[%s,%s,%s]}' % (_pkg['sml'],_pkg['med'],_pkg['lrg']),
    'packages?start=1&rows=1': '{"start":1,"numFound":3,"results":[%s]}' % _pkg['sml'],
    'packages?start=1&rows=2': '{"start":1,"numFound":3,"results":[%s,%s]}' % (_pkg['sml'],_pkg['med']),
    'packages?start=1&rows=3': '{"start":1,"numFound":3,"results":[%s,%s,%s]}' % (_pkg['sml'],_pkg['med'],_pkg['lrg']),
    'packages?start=1&rows=20':'{"start":1,"numFound":3,"results":[%s,%s,%s]}' % (_pkg['sml'],_pkg['med'],_pkg['lrg']),
    'packages?start=2&rows=1': '{"start":2,"numFound":3,"results":[%s]}' % _pkg['med'],
    'packages?start=2&rows=2': '{"start":2,"numFound":3,"results":[%s,%s]}' % (_pkg['med'],_pkg['lrg']),
    'packages?start=2&rows=3': '{"start":2,"numFound":3,"results":[%s,%s]}' % (_pkg['med'],_pkg['lrg']),
    'packages?start=2&rows=20':'{"start":2,"numFound":3,"results":[%s,%s]}' % (_pkg['med'],_pkg['lrg']),
    'packages?start=3&rows=1': '{"start":3,"numFound":3,"results":[%s]}' % _pkg['lrg'],
    'packages?start=3&rows=2': '{"start":3,"numFound":3,"results":[%s]}' % _pkg['lrg'],
    'packages?start=3&rows=3': '{"start":3,"numFound":3,"results":[%s]}' % _pkg['lrg'],
    'packages?start=3&rows=20':'{"start":3,"numFound":3,"results":[%s]}' % _pkg['lrg'],
    'packages?start=4&rows=1': '{"start":4,"numFound":3,"results":[]}',
    'packages/urn:pkg:sml': _pkg['sml'],
    'packages/urn:pkg:med': _pkg['med'],
    'packages/urn:pkg:lrg': _pkg['lrg'],
    'packages/urn:pkg:sml/deprecate': '',
    'packages/urn:pkg:sml/members': '',
    'packages/urn:pkg:sml/members/deprecate': '',
    'packages/urn:pkg:sml/members/undeprecate': '',
    'packages/urn:pkg:sml/undeprecate': '',
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
            raise _TestHandlerError(fullURL, httplib.NOT_FOUND, 'Sorry, "%s" not found' % fullURL)
        return StringIO(_testData[kind])

urllib2.install_opener(urllib2.build_opener(_TestHandler))
