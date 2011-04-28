# encoding: utf-8
# Copyright 2011 California Institute of Technology. ALL RIGHTS
# RESERVED. U.S. Government Sponsorship acknowledged.

'''PDS Registry network communication classes'''

from contextlib import closing
from pds.registry.model.classes import Service, ServiceBinding, SpecificationLink, Slot
from urllib import urlencode
from urllib2 import Request, urlopen, HTTPError
import anyjson, httplib

_standardHeaders = {
    u'Accept':       u'application/json',
    u'Content-type': u'application/json',
}

class PDSRegistryClient(object):
    '''Client connection to a PDS Registry Server.'''
    def __init__(self, url):
        '''Initialize a registry client with the ``url`` to the server.'''
        self.url = url
    def _callServer(self, path='', params=None, json=None, method='GET'):
        ''''''
        url = self.url + path + (params and u'?' + urlencode(params) or u'')
        request = Request(url, data=None, headers=_standardHeaders)
        request.get_method = lambda: method
        if json:
            assert method != 'GET'
            request.add_data(json)
            request.add_header(u'Content-type', 'application/json')
            request.add_header(u'Content-length', unicode(len(json)))
        with closing(urlopen(request)) as f:
            return method == 'GET' and anyjson.deserialize(f.read()) or None
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
    def _mapSlots(self, slots):
        '''Map a set of Slots into a structure acceptable to JSON.
        '''
        return [dict(name=i.name, slotType=i.slotType, values=i.values) for i in slots]
    def _mapSpecificationLinks(self, links):
        '''Map a set of SpecificationLinks into a structure acceptable to JSON'''
        return [{
            'description':          i.description,
            'guid':                 i.guid,
            'home':                 i.home,
            'lid':                  i.lid,
            'name':                 i.name,
            'objectType':           i.objectType,
            'serviceBinding':       i.serviceBinding,
            'slots':                self._mapSlots(i.slots),
            'specificationObject':  i.specificationObject,
            'usageDescription':     i.usageDescription,
            'usageParameters':      i.usageParameters,
            'versionId':            i.versionID,
            'versionName':          i.versionName,
        } for i in links]
    def _mapServiceBindings(self, bindings):
        '''Map a set of ServiceBindings into a structure acceptable to JSON'''
        return [{
            'accessURI':            i.accessURI,
            'description':          i.description,
            'guid':                 i.guid,
            'home':                 i.home,
            'lid':                  i.lid,
            'name':                 i.name,
            'objectType':           i.objectType,
            'service':              i.service,
            'slots':                self._mapSlots(i.slots),
            'specificationLinks':   self._mapSpecificationLinks(i.specificationLinks),
            'versionId':            i.versionID,
            'versionName':          i.versionName,
        } for i in bindings]
    def _serializeService(self, service):
        '''Serialize a Service into JSON.'''
        return anyjson.serialize({
            'description':      service.description,
            'guid':             service.guid,
            'home':             service.home,
            'lid':              service.lid,
            'name':             service.name,
            'objectType':       service.objectType,
            'serviceBindings':  self._mapServiceBindings(service.serviceBindings),
            'slots':            self._mapSlots(service.slots),
            'versionId':        service.versionID,
            'versionName':      service.versionName,
        })
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
        '''Retrieve a service with a known ``guid``, or None if ``guid`` is not found.
        
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
        >>> unknown = rs.getService('urn:this:does:not:exist')
        >>> unknown is None
        True
        '''
        try:
            answer = self._callServer('/services/%s' % guid)
            if 'Service' != answer.get('objectType', None): return None
            return self._createService(answer)
        except HTTPError, ex:
            # TODO: We should return None in the case of a 404, but the PDS Registry Service currently
            # returns 500 for not found.  When https://oodt.jpl.nasa.gov/jira/browse/PDS-29 is
            # fixed, fix this below:
            if ex.code in (httplib.INTERNAL_SERVER_ERROR, httplib.NOT_FOUND):
                return None
            else:
                raise ex
    def putService(self, service):
        '''Send Service ``service`` into the Registry.
        
        >>> import pds.registry.net.tests.base
        >>> from pds.registry.model.classes import Service
        >>> rs = PDSRegistryClient('testscheme:/rs')
        >>> service = Service(u'urn:services:new-service', u'urn:services:new-service', u'testscheme:/rs', set(), u'New Service')
        >>> rs.putService(service)
        >>> service = Service(u'urn:sk:radio:lush', u'urn:sk:radio:lush', u'testscheme:/rs', set(), u'Lush Radio Service')
        >>> rs.putService(service)
        '''
        # If it doesn't exist, POST to the /services path, but if it does exist, PUT to
        # the existing /services/guid path. FIXME: Yes, there is a race here.
        json = self._serializeService(service)
        existing = self.getService(service.guid)
        if existing:
            self._callServer('/services/%s' % service.guid, params=None, json=json, method='PUT')
        else:
            self._callServer('/services', params=None, json=json, method='POST')
    def deleteService(self, serviceGUID):
        '''Delete the service with UUID ``serviceGUID`` from the Registry.
        
        >>> import pds.registry.net.tests.base
        >>> rs = PDSRegistryClient('testscheme:/rs')
        >>> rs.deleteService('urn:sk:radio:lush')
        '''
        self._callServer('/services/%s' % serviceGUID, params=None, json=None, method='DELETE')

# Demonstration with actual PDS Registry Service:
def main():
    c = PDSRegistryClient('http://localhost:8080/registry-service/registry')
    serviceSlots = set([Slot('bpm', ['140'], 'int'), Slot('genre', ['goa', 'psy'], 'enum')])
    bindingSlots = set([Slot('strength', ['strong'])])
    linkSlots = set([Slot('broken', ['true'], 'bool'), Slot('with-icon', ['false', 'maybe'], 'huh?')])
    service = Service('urn:sk:global:guid:1', 'urn:sk:logical:1', 'http://localhost:8080/registry-service', serviceSlots, 'T.H.E. SERVICE', 'submitted', 'It is indeed THE service.', 'One Point Oh Point One', '1.0.1')
    binding = ServiceBinding('urn:sk:global:guid:1:1', 'http://endpoint.com/', service.guid, service.home, bindingSlots, 'T.H.E. BINDING', 'submitted', 'It is quite the binding.', 'Two Point Oh Point Oh', '2.0.0', 'http://endpoint.com/')
    link = SpecificationLink('urn:sk:global:guid:1:1:1', 'urn:sk:logical:link:1', binding.guid, 'urn:ietf:rfc:1136', service.home, linkSlots, 'T.H.E. Specification', 'submitted', 'Woo woo', 'Three', '3', 'Use it wisely', ['or', 'not'])
    binding.specificationLinks.add(link)
    service.serviceBindings.add(binding)
    c.putService(service)
    c.deleteService(service.guid)

if __name__ == '__main__':
    main()