# encoding: utf-8
# Copyright 2011–2016 California Institute of Technology. ALL RIGHTS
# RESERVED. U.S. Government Sponsorship acknowledged.

'''PDS Registry network communication classes'''

from contextlib import closing
from pds.registry.model.classes import (
    Service, ServiceBinding, SpecificationLink, Slot, ExtrinsicObject, Association, Package
)
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
        url = self.url + path + (u'?' + urlencode(params) if params else u'')
        request = Request(url, data=None, headers=_standardHeaders)
        request.get_method = lambda: method
        if json:
            assert method != 'GET'
            request.add_data(json)
            request.add_header(u'Content-type', 'application/json')
            request.add_header(u'Content-length', unicode(len(json)))
        with closing(urlopen(request)) as f:
            return anyjson.deserialize(f.read()) if method == 'GET' else None
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
            accessURI=d['accessURI'],
            specificationLinks=self._createSpecificationLinks(d['guid'], d.get('specificationLinks', [])),
            targetBinding=d.get('targetBinding', None)
        ) for d in s])
    def _createExtrinsic(self, d):
        '''Create an extrinsic from a post-JSON-quantized dictionary ``d``.'''
        return ExtrinsicObject(
            contentVersion=d.get('contentVersion', None),
            description=d.get('description', None),
            guid=d['guid'],
            home=d['home'],
            lid=d['lid'],
            mimeType=d.get('mimeType', None),
            name=d['name'],
            objectType=d.get('objectType', None),
            slots=self._createSlots(d.get('slots', [])),
            status=d.get('status', None),
            versionName=d.get('versionName', None),
        )
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
            serviceBindings=self._createServiceBindings(serviceGUID, d.get('serviceBindings', []))
        )
    def _createPackage(self, d):
        '''Create a package from post-JSON-quantized dictionary ``d``.'''
        if 'objectType' not in d: raise ValueError('No "objectType')
        if d['objectType'] != 'RegistryPackage': raise ValueError('Expected "Association" but got "%s"' % d['objectType'])
        packageGUID = d['guid']
        return Package(
            guid=packageGUID,
            lid=d['lid'],
            home=d['home'],
            slots=self._createSlots(d.get('slots', [])),
            name=d['name'],
            status=d.get('status', None),
            description=d.get('description', None),
            versionName=d.get('versionName', None)
        )
    def _createAssociation(self, d):
        '''Create an Association from a post-JSON-quantized dictionary ``d``.'''
        if 'objectType' not in d: raise ValueError('No "objectType"')
        if d['objectType'] != 'Association': raise ValueError('Expected an "Assocation" but got "%s"' % d['objectType'])
        associationGUID = d['guid']
        return Association(
            guid=associationGUID,
            lid=d['lid'],
            home=d['home'],
            slots=self._createSlots(d.get('slots', [])),
            name=d['name'],
            status=d.get('status', None),
            description=d.get('description', None),
            versionName=d.get('versionName', None),
            source=d.get('sourceObject', None),
            target=d.get('targetObject', None),
            associationType=d.get('associationType', None)
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
            'versionName':          i.versionName,
        } for i in bindings]
    def _serializeExtrinsic(self, extrinsic):
        '''Serialize an ExtrinsicObject into JSON.'''
        return anyjson.serialize({
            'contentVersion':   extrinsic.contentVersion,
            'description':      extrinsic.description,
            'guid':             extrinsic.guid,
            'home':             extrinsic.home,
            'lid':              extrinsic.lid,
            'mimeType':         extrinsic.mimeType,
            'name':             extrinsic.name,
            'objectType':       extrinsic.objectType,
            'slots':            self._mapSlots(extrinsic.slots),
            'versionName':      extrinsic.versionName,
        })
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
            'versionName':      service.versionName,
        })
    def _serializePackage(self, package):
        '''Serialize a Package into JSON.'''
        return anyjson.serialize({
            'description':      package.description,
            'guid':             package.guid,
            'home':             package.home,
            'lid':              package.lid,
            'name':             package.name,
            'objectType':       package.objectType,
            'slots':            self._mapSlots(package.slots),
            'versionName':      package.versionName,
        })
    def _serializeAssociation(self, association):
        '''Serialize an Association into JSON.'''
        return anyjson.serialize({
            'associationType':  association.associationType,
            'description':      association.description,
            'guid':             association.guid,
            'home':             association.home,
            'lid':              association.lid,
            'name':             association.name,
            'objectType':       association.objectType,
            'slots':            self._mapSlots(association.slots),
            'sourceObject':     association.source,
            'targetObject':     association.target,
            'versionName':      association.versionName,
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
        answer = self._callServer('/services', dict(start=start+1, rows=rows))  # Why is it one-based indexing? Lame.
        return [self._createService(i) for i in answer.get('results', [])]
    def getExtrinsics(self, start=0, rows=20):
        '''Retrieve extrinsics registered with the registry service, starting at index ``start`` in the
        services list and retrieving no more than ``rows`` worth.

        >>> import pds.registry.net.tests.base
        >>> rs = PDSRegistryClient('testscheme:/rs')
        >>> extrinsics = rs.getExtrinsics()
        >>> len(extrinsics)
        3
        >>> extrinsics[0].guid, extrinsics[1].guid, extrinsics[2].guid
        (u'egg-1.0', u'spam-1.0', u'bacon-1.0')
        >>> extrinsics = rs.getExtrinsics(1, 1)
        >>> len(extrinsics)
        1
        >>> extrinsics[0].guid
        u'spam-1.0'
        '''
        answer = self._callServer('/extrinsics', dict(start=start+1, rows=rows))  # Why is it one-based indexing? Lame.
        return [self._createExtrinsic(i) for i in answer.get('results', [])]
    def getPackages(self, start=0, rows=20):
        '''Retrieve packages registered with the registry service, starting at index ``start`` in the
        package list and retrieving no more than ``rows`` worth.

        >>> import pds.registry.net.tests.base
        >>> rs = PDSRegistryClient('testscheme:/rs')
        >>> packages = rs.getPackages()
        >>> len(packages)
        3
        >>> packages[0].guid, packages[1].guid, packages[2].guid
        (u'urn:pkg:sml', u'urn:pkg:med', u'urn:pkg:lrg')
        >>> packages = rs.getPackages(1, 1)
        >>> len(packages)
        1
        >>> packages[0].guid
        u'urn:pkg:med'
        '''
        answer = self._callServer('/packages', dict(start=start+1, rows=rows))  # Why is it … oh nevermind
        return [self._createPackage(i) for i in answer.get('results', [])]
    def getAssociations(self, start=0, rows=20, source=None, target=None):
        '''Retrieve associations registered with the registry service, starting at index ``start`` in
        the associations list and retrieving no more than ``rows`` worth.  Optionally, you can search
        by a source UUID, target UUID, or both.

        >>> import pds.registry.net.tests.base
        >>> rs = PDSRegistryClient('testscheme:/rs')
        >>> associations = rs.getAssociations()
        >>> len(associations)
        3
        >>> associations[0].guid, associations[1].guid, associations[2].guid
        (u'urn:anatomyid:ass', u'urn:anatomyid:but', u'urn:anatomyid:boo')
        >>> associations = rs.getAssociations(1, 1)
        >>> len(associations)
        1
        >>> associations[0].guid
        u'urn:anatomyid:but'
        >>> associations = rs.getAssociations(source=u'urn:uuid:8007636f-adcd-416e-a75b-e954814bd953')
        >>> len(associations)
        3
        >>> associations[0].guid, associations[1].guid, associations[2].guid
        (u'urn:anatomyid:ass', u'urn:anatomyid:but', u'urn:anatomyid:boo')
        >>> associations = rs.getAssociations(target=u'urn:uuid:2cdad332-f667-4e8b-814a-4b67624c4e2a')
        >>> len(associations)
        1
        >>> associations[0].guid
        u'urn:anatomyid:ass'
        '''
        params = {
            'start': start + 1,  # Why is it one-based indexing?  Lame.
            'rows': rows,
        }
        if source is not None: params['sourceObject'] = source
        if target is not None: params['targetObject'] = target
        answer = self._callServer('/associations', params)
        return [self._createAssociation(i) for i in answer.get('results', [])]
    def getExtrinsicByLidvid(self, lidvid):
        '''Retrieve an extrinsic given a ``lidvid``, which is a string of the form ``LOGICAL-ID::VERSION-NAME``.

        >>> rs = PDSRegistryClient('testscheme:/rs')
        >>> extrinsic = rs.getExtrinsicByLidvid('bacon::1.0')
        >>> extrinsic.lid, extrinsic.versionName
        (u'bacon', u'1.0')
        '''
        lid, vid = lidvid.split('::')
        answer = self._callServer('/extrinsics', dict(lid=lid, versionName=vid))
        if answer.get('numFound', 0) == 0:
            return None
        return self._createExtrinsic(answer['results'][0])
    def getService(self, guid):
        '''Retrieve a service with a known ``guid``, or None if ``guid`` is not found.

        >>> import pds.registry.net.tests.base
        >>> rs = PDSRegistryClient('testscheme:/rs')
        >>> lush = rs.getService('urn:sk:radio:lush')
        >>> lush.guid, lush.home, lush.lid
        (u'urn:sk:radio:lush:2.1', u'http://localhost:8080/registry-service', u'urn:uuid:0b2aebc3-dde7-4453-901d-9b3f9660dfc2')
        >>> lush.versionName, lush.objectType
        (u'1.0', 'Service')
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
            if ex.code == httplib.NOT_FOUND:
                return None
            else:
                raise ex
    def getPackage(self, guid):
        '''Retrieve a package with a known ``guid`` or None if ``guid`` is not found.

        >>> import pds.registry.net.tests.base
        >>> rs = PDSRegistryClient('testscheme:/rs')
        >>> pkg = rs.getPackage('urn:pkg:sml')
        >>> pkg.guid, pkg.lid, pkg.name
        (u'urn:pkg:sml', u'sml', u'Small Package')
        >>> pkg.status, pkg.description, pkg.versionName
        (u'Submitted', u'The size of this package is rather small.', u'1.0')
        >>> len(pkg.slots)
        1
        >>> slot = pkg.slots.pop()
        >>> slot.name, slot.values
        (u'hirsuteness', [u'wiry'])
        '''
        try:
            answer = self._callServer('/packages/%s' % guid)
            if 'RegistryPackage' != answer.get('objectType', None): return None
            return self._createPackage(answer)
        except HTTPError, ex:
            if ex.code == httplib.NOT_FOUND:
                return None
            else:
                raise ex
    def getAssociation(self, guid):
        '''Retrieve an association with a known ``guid``, or None if ``guid`` is not found.

        >>> import pds.registry.net.tests.base
        >>> rs = PDSRegistryClient('testscheme:/rs')
        >>> ass = rs.getAssociation('urn:anatomyid:ass')
        >>> ass.guid, ass.lid, ass.name
        (u'urn:anatomyid:ass', u'ass1', u'ass')
        >>> ass.status, ass.description, ass.versionName
        (u'Probed', u'This is the rear association', u'1.0')
        >>> ass.source, ass.target
        (u'urn:uuid:8007636f-adcd-416e-a75b-e954814bd953', u'urn:uuid:2cdad332-f667-4e8b-814a-4b67624c4e2a')
        >>> ass.associationType, ass.objectType
        (u'urn:registry:AssociationType:HasRear', 'Association')
        >>> len(ass.slots)
        1
        >>> slot = ass.slots.pop()
        >>> slot.name, slot.values
        (u'targetObjectType', [u'ExtrinsicObject'])
        '''
        try:
            answer = self._callServer('/associations/%s' % guid)
            if 'Association' != answer.get('objectType', None): return None
            return self._createAssociation(answer)
        except HTTPError, ex:
            if ex.code == httplib.NOT_FOUND:
                return None
            else:
                raise ex
    def getExtrinsicByLID(self, lid, earliest=False):
        '''Retrieve an extrinsic by its logical identifier, ``lid``; by default the latest version
        is returned, if found.  To get the earliest version, set ``earliest`` to True.

        >>> import pds.registry.net.tests.base
        >>> rs = PDSRegistryClient('testscheme:/rs')
        >>> ext = rs.getExtrinsicByLID('egg')
        >>> ext.guid, ext.home, ext.lid
        (u'egg-1.0', u'http://localhost:5634/registry', u'egg')
        >>> ext.versionName, ext.objectType
        (u'1.0', u'Product')
        >>> ext.name, ext.description
        (u'Egg v1.0', u'You usually have egg with bacon or with spam.')
        >>> ext.contentVersion, ext.mimeType
        (u'88.88', u'x-application/albumin')
        >>> len(ext.slots)
        2
        >>> slots = list(ext.slots); slots.sort(); [(i.name, i.values) for i in slots]
        [(u'preparation', [u'scrambled', u'poached']), (u'seasoning', [u'salt', u'pepper', u'hot sauce'])]
        >>> ext2 = rs.getExtrinsicByLID('egg', earliest=True)
        >>> ext == ext2
        True
        >>> unknown = rs.getExtrinsicByLID('non-exisitent')
        >>> unknown is None
        True
        '''
        try:
            # Registry Service is bizarrely inconsistent.  Retrieving an extrinsic by guid, or by lid/earliest,
            # or by lid/latest, gives back a JSON dict that represents the extrinsic.  However, retrieving just
            # by lid (without /earliest or /latest) returns a JSON dict with 'start' set to null, "numFound" set
            # to null, and a one-item sequence "results" that contains the extrinsic.
            if earliest:
                answer = self._callServer(u'/extrinsics/logicals/{}/earliest'.format(lid))
            else:
                answer = self._callServer(u'/extrinsics/logicals/{}'.format(lid))
                answer = answer['results'][0]
            return self._createExtrinsic(answer)
        except HTTPError, ex:
            if ex.code == httplib.NOT_FOUND:
                return None
            else:
                raise ex
    def getExtrinsic(self, guid):
        '''Retrieve an extrinsic with a known ``guid``, or None if ``guid`` is not found.

        >>> import pds.registry.net.tests.base
        >>> rs = PDSRegistryClient('testscheme:/rs')
        >>> ext = rs.getExtrinsic('egg-1.0')
        >>> ext.guid, ext.home, ext.lid
        (u'egg-1.0', u'http://localhost:5634/registry', u'egg')
        >>> ext.versionName, ext.objectType
        (u'1.0', u'Product')
        >>> ext.name, ext.description
        (u'Egg v1.0', u'You usually have egg with bacon or with spam.')
        >>> ext.contentVersion, ext.mimeType
        (u'88.88', u'x-application/albumin')
        >>> len(ext.slots)
        2
        >>> slots = list(ext.slots); slots.sort(); [(i.name, i.values) for i in slots]
        [(u'preparation', [u'scrambled', u'poached']), (u'seasoning', [u'salt', u'pepper', u'hot sauce'])]
        >>> unknown = rs.getExtrinsic('non-exisitent-1.0')
        >>> unknown is None
        True
        '''
        try:
            answer = self._callServer('/extrinsics/%s' % guid)
            return self._createExtrinsic(answer)
        except HTTPError, ex:
            if ex.code == httplib.NOT_FOUND:
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
    def putPackage(self, package):
        '''Send Package ``package`` into the registry.

        >>> import pds.registry.net.tests.base
        >>> from pds.registry.model.classes import Package
        >>> rs = PDSRegistryClient('testscheme:/rs')
        >>> pkg = Package(u'urn:pkg:tiny', u'tiny', u'testscheme:/rs', set(), u'Tiny Package', u'Submitted', u'Tiny', u'1.0')
        >>> rs.putPackage(pkg)
        >>> pkg = Package(u'urn:pkg:huge', u'huge', u'testscheme:/rs', set(), u'Huge Package', u'Submitted', u'Huge', u'1.0')
        >>> rs.putPackage(pkg)
        '''
        # If it doesn't exist, POST to the /packages path.  But if it does exist, PUT to the existing
        # /packages/guid path.  FIXME: Yes, there is a race here.
        json = self._serializePackage(package)
        existing = self.getPackage(package.guid)
        if existing:
            self._callServer('/packages/%s' % package.guid, params=None, json=json, method='PUT')
        else:
            self._callServer('/packages', params=None, json=json, method='POST')
    def putExtrinsic(self, extrinsic):
        '''Send ExtrinsicObject ``extrinsic`` into the Registry.

        >>> import pds.registry.net.tests.base
        >>> from pds.registry.model.classes import ExtrinsicObject
        >>> rs = PDSRegistryClient('testscheme:/rs')
        >>> ext = ExtrinsicObject(u'egg-1.0', u'egg', u'testscheme:/rs', set(), u'New Egg', u'Approved', u'Freshly laid', u'1.0',
        ... u'79.99', u'x-application/breakfast')
        >>> rs.putExtrinsic(ext)
        >>> ext = ExtrinsicObject(u'spam-1.0', u'spam', u'testscheme:/rs', set(), u'Spam')
        >>> rs.putExtrinsic(ext)
        '''
        # If the extrinsic doesn't exist, POST it to /extrinsics; but if it does exist, POST
        # it to the existing /extrinsics/logicals/lid path. FIXME: Yes, there is a race here.
        json = self._serializeExtrinsic(extrinsic)
        existing = self.getExtrinsicByLID(extrinsic.lid)
        if existing:
            self._callServer(u'/extrinsics/logicals/{}'.format(extrinsic.lid), params=None, json=json, method='POST')
        else:
            self._callServer(u'/extrinsics', params=None, json=json, method='POST')
    def putAssociation(self, association):
        '''Send the Association object ``association`` into the Registry.

        >>> import pds.registry.net.tests.base
        >>> from pds.registry.model.classes import Association
        >>> rs = PDSRegistryClient('testscheme:/rs')
        >>> ass = Association(u'ass-1', u'ass', u'testscheme:/rs', set(), u'ass', u'Probed', u'Assoc', u'1.0', u'rec', u'anu', 'urn:con')
        >>> rs.putAssociation(ass)
        >>> ass = Association(u'urn:anatomyid:ass', 'ass')
        >>> rs.putAssociation(ass)
        '''
        # If the association doesn't exist, POST it to /associations; but if it does exist, PUT
        # it to the existing /associations/guid path. FIXME: Yes, there is a race here.
        json = self._serializeAssociation(association)
        existing = self.getAssociation(association.guid)
        if existing:
            self._callServer('/associations/%s' % association.guid, params=None, json=json, method='PUT')
        else:
            self._callServer('/associations', params=None, json=json, method='POST')
    def deleteService(self, serviceGUID):
        '''Delete the service with UUID ``serviceGUID`` from the Registry.

        >>> import pds.registry.net.tests.base
        >>> rs = PDSRegistryClient('testscheme:/rs')
        >>> rs.deleteService('urn:sk:radio:lush')
        '''
        self._callServer('/services/%s' % serviceGUID, params=None, json=None, method='DELETE')
    def deleteExtrinsic(self, extrinsicGUID):
        '''Delete the extrinsic with UUID ``extrinsicGUID`` from the Registry.

        >>> import pds.registry.net.tests.base
        >>> rs = PDSRegistryClient('testscheme:/rs')
        >>> rs.deleteExtrinsic('egg-1.0')
        '''
        self._callServer('/extrinsics/%s' % extrinsicGUID, params=None, json=None, method='DELETE')
    def deletePackage(self, packageGUID):
        '''Delete the package with UUID ``packageGUID`` from the Registry.

        >>> import pds.registry.net.tests.base
        >>> rs = PDSRegistryClient('testscheme:/rs')
        >>> rs.deletePackage('urn:pkg:sml')
        '''
        self._callServer('/packages/%s/members' % packageGUID, params=None, json=None, method='DELETE')
        self._callServer('/packages/%s' % packageGUID, params=None, json=None, method='DELETE')
    def deleteAssocation(self, associationGUID):
        '''Delete the association with the UUID ``associationGUID`` from the Registry.

        >>> import pds.registry.net.tests.base
        >>> rs = PDSRegistryClient('testscheme:/rs')
        >>> rs.deleteAssocation('urn:anatomyid:ass')
        '''
        self._callServer('/associations/%s' % associationGUID, params=None, json=None, method='DELETE')
    def updatePackageStatus(self, packageGUID, status):
        '''Update the status for the package with the UUID ``packageGUID`` to ``status``.
        Status is typically one of ``approve``, ``deprecate``, or ``undeprecate``.

        ("Undeprecate"?  Surely we could've come up with a better antonym.  Say, "revalue", "reappraise"?)

        >>> import pds.registry.net.tests.base
        >>> rs = PDSRegistryClient('testscheme:/rs')
        >>> rs.updatePackageStatus('urn:pkg:sml', 'deprecate')
        >>> rs.updatePackageStatus('urn:pkg:sml', 'undeprecate')
        '''
        self._callServer('/packages/%s/members/%s' % (packageGUID, status), params=None, json=None, method='POST')
        self._callServer('/packages/%s/%s' % (packageGUID, status), params=None, json=None, method='POST')


# Demonstration with actual PDS Registry Service:
def main():
    c = PDSRegistryClient('http://localhost:8080/registry')
    serviceSlots = set([Slot('bpm', ['140'], 'int'), Slot('genre', ['goa', 'psy'], 'enum')])
    bindingSlots = set([Slot('strength', ['strong'])])
    linkSlots = set([Slot('broken', ['true'], 'bool'), Slot('with-icon', ['false', 'maybe'], 'huh?')])
    service = Service('urn:sk:global:guid:1', 'urn:sk:logical:1', 'http://localhost:8080/registry-service', serviceSlots, 'T.H.E. SERVICE', 'submitted', 'It is indeed THE service.', 'One Point Oh Point One')
    binding = ServiceBinding('urn:sk:global:guid:1:1', 'http://endpoint.com/', service.guid, service.home, bindingSlots, 'T.H.E. BINDING', 'submitted', 'It is quite the binding.', 'Two Point Oh Point Oh', 'http://endpoint.com/')
    link = SpecificationLink('urn:sk:global:guid:1:1:1', 'urn:sk:logical:link:1', binding.guid, 'urn:ietf:rfc:1136', service.home, linkSlots, 'T.H.E. Specification', 'submitted', 'Woo woo', 'Three', 'Use it wisely', ['or', 'not'])
    binding.specificationLinks.add(link)
    service.serviceBindings.add(binding)
    c.putService(service)
    c.deleteService(service.guid)

if __name__ == '__main__':
    main()
