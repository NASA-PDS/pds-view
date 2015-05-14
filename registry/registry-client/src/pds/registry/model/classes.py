# encoding: utf-8
# Copyright 2011 California Institute of Technology. ALL RIGHTS
# RESERVED. U.S. Government Sponsorship acknowledged.

'''PDS Registry Client: class implementations.'''


class Identifiable(object):
    '''
    The Identifiable class is the common super class for most classes in the
    information model.  Information model Classes whose instances have a
    unique identity are descendants of the Identifiable Class.
    
    Example of use:

    >>> i = Identifiable('guid', 'urn:home', slots=set())
    >>> i.guid
    'guid'
    >>> i.home
    'urn:home'
    >>> i.slots
    set([])

    Note that the globally unique identifier, or GUID, is all that should
    matter when we compare or hash Identifiable objects::

    >>> a, b = Identifiable('guid1', 'urn:alpha'), Identifiable('guid1', 'urn:beta')
    >>> a.home == b.home
    False
    >>> a == b
    True
    '''
    def __init__(self, guid, home=None, slots=None):
        '''Initialize an Identifiable object with a globally unique guid, optional home, and
        set of describing slots.'''
        self._guid, self.home = guid, home
        if slots is not None:
            if not isinstance(slots, set):
                raise TypeError('slots should be a set, not a %r' % slots.__class__)
            self.slots = slots
        else:
            self.slots = set()
    @property
    def guid(self):
        '''Globally unique and immutable identifier'''
        return self._guid
    def __hash__(self):
        '''Hashing uses the globally unique ID only, as it's globally unique and we shouldn't have to look further.'''
        return hash(self.guid)
    def __cmp__(self, other):
        '''Comparisons compare on the globally unique ID only, as it's globally unique and we shouldn't have to look further.'''
        return cmp(self.guid, other.guid)


class Slot(object):
    ''' 
    Slot instances provide a dynamic way to add arbitrary attributes to
    RegistryObject instances.  This ability to add attributes dynamically to
    RegistryObject instances enables extensibility within the information
    model.
    
    >>> a = Slot(u'http://purl.org/dc/terms/title', ["Bluish Eggs and a Side of Pastrami"],
    ...     slotType='http://www.w3.org/2001/XMLSchema#string')
    >>> b = Slot(u'http://purl.org/dc/terms/title', ["Oh The Destinations You'll Briefly Visit"],
    ...     slotType='http://www.w3.org/2001/XMLSchema#string')
    >>> a != b
    True
    >>> a <= b
    True
    >>> hash(a) != hash(b)
    True
    '''
    def __init__(self, name, values=None, slotType=None):
        '''Initialize a slot with name, values for that name, and optional type'''
        self.name, self.slotType = name, slotType
        if values is not None:
            if not isinstance(values, list):
                raise TypeError('values should be a list, not a %r' % values.__class__)
            self.values = values
        else:
            self.values = []
    def __hash__(self):
        return hash((self.name, self.slotType, reduce(lambda x, y: x ^ y, [hash(i) for i in self.values], 0x55555555)))
    def __cmp__(self, other):
        return cmp((self.name, self.slotType, self.values), (other.name, other.slotType, other.values))
        

class RegistryObject(Identifiable):
    '''
    The RegistryObject class extends the Identifiable class and serves as a
    common super class for most classes in the information model.
    
    There should be no more versionID attribute anymore:
    
    >>> ro = RegistryObject(
    ...     guid=u'urn:test:guid:1', lid=u'urn:test:lid:1', home=u'http://localhost:8080/test',
    ...     slots=set(), name=u'Test Object', status='Accepted', description=u'An object for testing',
    ...     versionName=u'3.0.0', versionID=u'1.0'
    ... )
    Traceback (most recent call last):
    ...
    TypeError: __init__() got an unexpected keyword argument 'versionID'
    '''
    # Constant values for the objectType attribute.
    SERVICE = 'Service'
    SERVICE_BINDING = 'ServiceBinding'
    SPECIFICATION_LINK = 'SpecificationLink'
    EXTRINSIC_OBJECT = 'ExtrinsicObject'
    ASSOCIATION = 'Association'
    def __init__(
        self, guid, lid,
        home=None, slots=None, name=None, objectType=None, status=None, description=None, versionName=None
    ):
        super(RegistryObject, self).__init__(guid, home, slots)
        self._lid = lid
        if objectType:
            self._objectType = objectType
        else:
            self._objectType = 'RegistryObject'
        self.name, self.status, self.description, self.versionName = name, status, description, versionName
    @property
    def lid(self):
        '''Logical Identifier, immutable'''
        return self._lid
    @property
    def objectType(self):
        '''Object type, immutable'''
        return self._objectType


class ExtrinsicObject(RegistryObject):
    '''The ExtrinsicObject class is the primary metadata class for a RepositoryItem.'''
    def __init__(
        self, guid, lid,
        home=None, slots=None, name=None, status=None, description=None, versionName=None,
        contentVersion=None, mimeType=None, objectType=None
    ):
        super(ExtrinsicObject, self).__init__(
            guid, lid, home, slots, name, objectType if objectType is not None else RegistryObject.EXTRINSIC_OBJECT, status,
            description, versionName
        )
        self.contentVersion, self.mimeType = contentVersion, mimeType


class Association(RegistryObject):
    '''An Association associates two RegistryObjects'''
    def __init__(
        self, guid, lid,
        home=None, slots=None, name=None, status=None, description=None, versionName=None,
        source=None, target=None, associationType=None, objectType=None
    ):
        super(Association, self).__init__(
            guid, lid, home, slots, name, objectType if objectType is not None else RegistryObject.ASSOCIATION,
            description, versionName
        )
        self.source, self.target, self.associationType = source, target, associationType


class Service(RegistryObject):
    '''Service instances describe services, such as web services.'''
    def __init__(
        self, guid, lid,
        home=None, slots=None, name=None, status=None, description=None, versionName=None,
        serviceBindings=None
    ):
        super(Service, self).__init__(
            guid, lid, home, slots, name, RegistryObject.SERVICE, status, description, versionName
        )
        if serviceBindings is not None:
            if not isinstance(serviceBindings, set):
                raise TypeError('serviceBindings should be a set, not a %r' % serviceBindings.__class__)
            self.serviceBindings = serviceBindings
        else:
            self.serviceBindings = set()


class ServiceBinding(RegistryObject):
    '''
    ServiceBinding instances are RegistryObjects that represent technical
    information on a specific way to access a Service instance.  An example is
    where a ServiceBinding is defined for each protocol that may used to access
    the service.
    '''
    def __init__(
        self, guid, lid, service,
        home=None, slots=None, name=None, status=None, description=None, versionName=None,
        accessURI=None, specificationLinks=None, targetBinding=None
    ):
        super(ServiceBinding, self).__init__(
            guid, lid, home, slots, name, RegistryObject.SERVICE_BINDING, status, description, versionName
        )
        self._service = service
        self.accessURI, self.targetBinding = accessURI, targetBinding
        if specificationLinks is not None:
            if not isinstance(specificationLinks, set):
                raise TypeError('specificationLinks should be a set, not a %r' % specificationLinks.__class__)
            self.specificationLinks = specificationLinks
        else:
            self.specificationLinks = set()
    @property
    def service(self):
        '''GUID of the service for this binding, immutable'''
        return self._service


class SpecificationLink(RegistryObject):
    '''
    A SpecificationLink provides the linkage between a ServiceBinding and one
    of its technical specifications that describes how to use the service
    using the ServiceBinding.  For example, a ServiceBinding MAY have
    SpecificationLink instances that describe how to access the service using
    a technical specification such as a WSDL document or a CORBA IDL document.
    '''
    def __init__(
        self, guid, lid, serviceBinding, specificationObject,
        home=None, slots=None, name=None, status=None, description=None, versionName=None,
        usageDescription=None, usageParameters=None        
    ):
        super(SpecificationLink, self).__init__(
            guid, lid, home, slots, name, RegistryObject.SPECIFICATION_LINK, status, description, versionName,
        )
        self._serviceBinding = serviceBinding
        self.specificationObject, self.usageDescription = specificationObject, usageDescription
        if usageParameters is not None:
            if not isinstance(usageParameters, list):
                raise TypeError('usageParameters should be a list, not a %r' % usageParameters.__class__)
            self.usageParameters = usageParameters
        else:
            self.usageParameters = []
    @property
    def serviceBinding(self):
        '''GUID of the service binding for this specification link, immutable'''
        return self._serviceBinding
    

