# encoding: utf-8
# Copyright 2011 California Institute of Technology. ALL RIGHTS
# RESERVED. U.S. Government Sponsorship acknowledged.

'''PDS Registry network communication classes'''

from urllib import urlencode
from urllib2 import Request, urlopen
from contextlib import closing
from pds.registry.model.classes import Service, ServiceBinding, SpecificationLink, Slot
import anyjson

_standardHeaders = {
    u'Accept':       u'application/json',
    u'Content-type': u'application/json',
}

class PDSRegistryClient(object):
    '''Client connection to a PDS Registry Server.'''
    def __init__(self, url):
        '''Initialize a registry client with the ``url`` to the server.'''
        self.url = url
    def _callServer(self, path='', params=None, json=None):
        ''''''
        url = self.url + path + (params and u'?' + urlencode(params) or u'')
        request = Request(url, data=None, headers=_standardHeaders)
        if json:
            request.add_data(json)
            request.add_header(u'Content-length', unicode(len(json)))
        with closing(urlopen(request)) as f:
            return anyjson.deserialize(f.read())
    def _createSlots(self, s):
        '''Create a set of Slots from the given post-JSON-quantized sequence ``s``.'''
        return set([Slot(i['name'], i['values'], i.get('slotType', None)) for i in s])
    def _createSpecificationLinks(self, serviceBindingGUID, s):
        '''Create a set of SpecificationLinks from the ginve post-JSON-quantized sequence ``s`` that
        belong to the ServiceBinding with GUID ``serviceBindingGUID``.'''
        return set([SpecificationLink(
            guid=d['guid'],
            lid=d.get('lid', None),
            serviceBinding=serviceBindingGUID,
            specificationObject=d['specificationObject'],
            home=d.get('home', None),
            slots=self._createSlots(d.get('slots', [])),
            name=d['name'],
            status=d.get('status', None),
            description=d.get('description', None),
            versionName=d.get('versionName', None),
            versionID=d.get('versionId', None),
            usageDescription=d.get('usageDescription', None),
            usageParameters=d.get('usageParameters', [])
        ) for d in s])
    def _createServiceBindings(self, serviceGUID, s):
        '''Create a set of ServiceBindings from the given post-JSON-quantized sequence ``s`` that
        belong to the Service with GUID ``serviceGUID``.'''
        return set([ServiceBinding(
            guid=d['guid'],
            lid=d.get('lid', None),
            service=serviceGUID,
            home=d.get('home', None),
            slots=self._createSlots(d.get('slots', [])),
            name=d['name'],
            status=d.get('status', None),
            description=d.get('description', None),
            versionName=d.get('versionName', None),
            versionID=d.get('versionId', None),
            accessURI=d['accessURI'],
            specificationLinks=self._createSpecificationLinks(d['guid'], d.get('specificationLinks', [])),
            targetBinding=d.get('targetBinding', None)
        ) for d in s])
    def _createService(self, d):
        '''Create a service from a post-JSON-quantized dictionary ``d``.'''
        if 'objectType' not in d: raise ValueError('No "objectType"')
        if d['objectType'] != 'Service': raise ValueError('Expected a "Service" but got "%s"' % d['objectType'])
        serviceGUID = d['guid']
        return Service(
            guid=serviceGUID,
            lid=d['lid'],
            home=d['home'],
            slots=self._createSlots(d.get('slots', [])),
            name=d['name'],
            status=d.get('status', None),
            description=d.get('description', None),
            versionName=d.get('versionName', None),
            versionID=d.get('versionId', None),
            serviceBindings=self._createServiceBindings(serviceGUID, d.get('serviceBindings', []))
        )
    def getServices(self, start=0, rows=20):
        '''Retrieve services registered with the registry service, starting at index ``start`` in the
        services list and retrieving no more than ``rows`` worth.
        
        >>> import pds.registry.net.tests.base
        >>> rs = PDSRegistryClient('testscheme:/rs')
        >>> services = rs.getServices()
        >>> len(services)
        3
        >>> services[0].guid, services[1].guid, services[2].guid
        (u'urn:sk:radio:lush:2.1', u'urn:sk:services:insults:0', u'urn:uuid:0f142be7-e4ab-4495-8a03-aa926ffcc5d3')
        >>> services = rs.getServices(1, 1)
        >>> len(services)
        1
        >>> services[0].guid
        u'urn:sk:services:insults:0'
        '''
        answer = self._callServer('/services', dict(start=start+1, rows=rows)) # Why is it one-based indexing? Lame.
        return [self._createService(i) for i in answer.get('results', [])]
    def getService(self, guid):
        '''Retrieve a service with a known ``guid``.
        
        >>> import pds.registry.net.tests.base
        >>> rs = PDSRegistryClient('testscheme:/rs')
        >>> lush = rs.getService('urn:sk:radio:lush')
        >>> lush.guid, lush.home, lush.lid
        (u'urn:sk:radio:lush:2.1', u'http://localhost:8080/registry-service', u'urn:uuid:0b2aebc3-dde7-4453-901d-9b3f9660dfc2')
        >>> lush.versionName, lush.versionID, lush.objectType
        (u'1.0', u'2.1', 'Service')
        >>> lush.name ,lush.description
        (u'Lush Radio Service', u'Provides live streams for tuning the Lush radio station at Soma FM.')
        >>> len(lush.slots)
        2
        >>> slots = list(lush.slots); slots.sort(); [(i.name, i.values) for i in slots]
        [(u'genres', [u'chillout', u'vocal trance', u'chillout dreams']), (u'max-bpm', [u'90'])]
        >>> len(lush.serviceBindings)
        2
        >>> bindings = list(lush.serviceBindings); bindings.sort(); binding = bindings[1]
        >>> binding.accessURI, binding.name, binding.description
        (u'http://somafm.com/play/lush/mp3', u'128k MP3 Lush Stream', u'The main Lush stream (and hence the most popular).')
        >>> binding.guid, binding.objectType
        (u'urn:uuid:c400fc6e-c9a5-428c-adbf-6cd8f2237cc3', 'ServiceBinding')
        >>> len(binding.slots)
        1
        >>> slot = iter(binding.slots).next()
        >>> slot.name, slot.values
        (u'comment', [u'Gah, MP3 is 20 years old. Use AAC+!'])
        >>> len(binding.specificationLinks)
        1
        >>> sl = iter(binding.specificationLinks).next()
        >>> sl.name, sl.description, sl.specificationObject
        (u'RSTP', u'Real Time Streaming Protocol', u'urn:ietf:rfc:2326')
        >>> sl.objectType, sl.guid
        ('SpecificationLink', u'urn:uuid:5de615ab-242d-463d-b0dc-1f6efeaae0ee')
        '''
        answer = self._callServer('/services/%s' % guid)
        if 'Service' != answer.get('objectType', None): return None
        return self._createService(answer)
    
