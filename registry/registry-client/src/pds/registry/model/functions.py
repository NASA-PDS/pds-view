# encoding: utf-8
# Copyright 2011 California Institute of Technology. ALL RIGHTS
# RESERVED. U.S. Government Sponsorship acknowledged.


def areSpecificationLinksIdentical(a, b):
    '''Tell if two SpecificationLinks ``a`` and ``b`` are identical.  Normally, you need only look
    at the SpecificationLinks' UUIDs, as they uniquely identify each SpecificationLink.  But what
    if you've got two SpecificationLinks from varying data sources, such as a database and a web
    service and they happen to have the same UUIDs?  That's when, like Leonardo DiCaprio in his
    role as "Cobb" would advise you to "go deeper".  This function does that, doing deep field-by-
    field comparisons of every aspect of SpecificationLinks.

    >>> from pds.registry.model.classes import Slot, SpecificationLink
    >>> slots = set([Slot('a', ['a'])])
    >>> a = SpecificationLink('urn:a', 'urn:a', 'urn:s', 'urn:x', 'urn:h', slots, 'a', 'accepted', 'a', '1', 'a', ['a'])
    >>> b = SpecificationLink('urn:a', 'urn:a', 'urn:s', 'urn:x', 'urn:h', slots, 'a', 'accepted', 'a', '1', 'a', ['a', 'b'])
    >>> a is not b
    True
    >>> a == b
    True
    >>> areSpecificationLinksIdentical(a, b)
    False
    >>> a.usageParameters.append('b')
    >>> areSpecificationLinksIdentical(a, b)
    True
    '''
    for fieldName in (
        'guid', 'lid', 'serviceBinding', 'specificationObject', 'home', 'slots', 'name', 'status', 'description',
        'versionName', 'usageDescription', 'usageParameters'
    ):
        if getattr(a, fieldName, None) != getattr(b, fieldName, None): return False
    return True


def areSpecificationBindingsIdentical(a, b):
    '''Tell if two ServiceBindings ``a`` and ``b`` are identical.  This goes beyond comparison of the UUID
    of each which should be sufficient in practice.  However, if you're dealing with potentially identical
    objects yielded from different data sources, then you need to go deeper, Inception-like.

    >>> from pds.registry.model.classes import Slot, ServiceBinding, SpecificationLink
    >>> slots = set([Slot('a', ['a'])])
    >>> a = ServiceBinding('urn:a', 'urn:a', 'urn:s', 'http://a/', slots, 'a', 'accepted', 'a', '1', 'http://x/')
    >>> b = ServiceBinding('urn:a', 'urn:a', 'urn:s', 'http://a/', slots, 'a', 'accepted', 'a', '1', 'http://y/')
    >>> a is not b
    True
    >>> a == b
    True
    >>> areSpecificationBindingsIdentical(a, b)
    False
    >>> b.accessURI = 'http://x/'
    >>> areSpecificationBindingsIdentical(a, b)
    True
    >>> aLink = SpecificationLink('urn:a', 'urn:a', 'urn:a', 'http://spec/', 'http://a/', None, 'a', 'accepted', 'a', '1')
    >>> a.specificationLinks.add(aLink)
    >>> areSpecificationBindingsIdentical(a, b)
    False
    >>> bLink = SpecificationLink('urn:a', 'urn:a', 'urn:a', 'http://spec/', 'http://a/', None, 'a', 'accepted', 'a', '1')
    >>> b.specificationLinks.add(bLink)
    >>> areSpecificationBindingsIdentical(a, b)
    True
    '''
    for fieldName in (
        'guid', 'lid', 'service', 'home', 'name', 'status', 'description', 'versionName',
        'slots', 'accessURI', 'targetBinding'
    ):
        if getattr(a, fieldName, None) != getattr(b, fieldName, None): return False
    aLinks, bLinks = list(a.specificationLinks), list(b.specificationLinks)
    if len(aLinks) != len(bLinks): return False
    aLinks.sort()
    bLinks.sort()
    bIter = iter(bLinks)
    for aLink in aLinks:
        bLink = bIter.next()
        if not areSpecificationLinksIdentical(aLink, bLink): return False
    return True


def areServicesIdentical(a, b):
    '''Tell if two Services ``a`` and ``b`` are identical.  This goes beyond comparison of the UUID
    of each which should be sufficient.  This function enables you to check Service objects that you
    might've quantized from multiple databases, for example.

    >>> from pds.registry.model.classes import Slot, Service, ServiceBinding, SpecificationLink
    >>> a = Service('urn:a', 'urn:a', 'http://a/', set([Slot('a', ['a'])]), 'a', 'accepted', 'a', '1')
    >>> b = Service('urn:a', 'urn:a', 'http://a/', set([Slot('a', ['a'])]), 'a', 'accepted', 'a', '2')
    >>> a is not b
    True
    >>> a == b
    True
    >>> areServicesIdentical(a, b)
    False
    >>> b.versionName = '1'
    >>> areServicesIdentical(a, b)
    True
    >>> aBinding = ServiceBinding('urn:a', 'urn:a', 'urn:a', 'http://a/', None, 'a', 'accepted', 'a', '1', 'http://a/')
    >>> a.serviceBindings.add(aBinding)
    >>> areServicesIdentical(a, b)
    False
    >>> bBinding = ServiceBinding('urn:a', 'urn:a', 'urn:a', 'http://a/', None, 'a', 'accepted', 'a', '1', 'http://a/')
    >>> b.serviceBindings.add(bBinding)
    >>> areServicesIdentical(a, b)
    True
    >>> aLink = SpecificationLink('urn:a', 'urn:a', 'urn:a', 'http://spec/', 'http://a/', None, 'a', 'accepted', 'a', '1')
    >>> aBinding.specificationLinks.add(aLink)
    >>> areServicesIdentical(a, b)
    False
    >>> bLink = SpecificationLink('urn:a', 'urn:a', 'urn:a', 'http://spec/', 'http://a/', None, 'a', 'accepted', 'a', '1')
    >>> bBinding.specificationLinks.add(bLink)
    >>> areServicesIdentical(a, b)
    True
    '''
    for fieldName in ('guid', 'lid', 'home', 'name', 'status', 'description', 'versionName', 'slots'):
        if getattr(a, fieldName, None) != getattr(b, fieldName, None): return False
    aBindings, bBindings = list(a.serviceBindings), list(b.serviceBindings)
    if len(aBindings) != len(bBindings): return False
    aBindings.sort()
    bBindings.sort()
    bIter = iter(bBindings)
    for aBinding in aBindings:
        bBinding = bIter.next()
        if not areSpecificationBindingsIdentical(aBinding, bBinding): return False
    return True
