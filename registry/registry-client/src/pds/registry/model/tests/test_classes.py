# encoding: utf-8
# Copyright 2011 California Institute of Technology. ALL RIGHTS
# RESERVED. U.S. Government Sponsorship acknowledged.

'''PDS Registry Client: tests for information model classes.'''

import unittest
from pds.registry.model.classes import Identifiable, Slot, RegistryObject
from pds.registry.model.classes import Service, ServiceBinding, SpecificationLink

class IdentifiableTest(unittest.TestCase):
    '''Test case for the Identifiable class.'''
    def testAttributes(self):
        '''Ensure that the expected attributes get set during initialization.'''
        a = Identifiable(guid='123', home=u'http://home.com/', slots=set())
        self.assertEquals('123', a.guid)
        self.assertEquals(u'http://home.com/', a.home)
        self.assertEquals(set(), a.slots)
    def testReadOnlyAttributes(self):
        '''Check that the guid element is read-only'''
        a = Identifiable(guid='123', home=u'http://home.com/', slots=set())
        try:
            a.guid = '456'
            self.fail('Could reassign guid')
        except AttributeError: pass
    def testComparisons(self):
        '''Make certain comparisons work as expected.'''
        a, b, c = Identifiable('1'), Identifiable('2'), Identifiable('1')
        self.assertEquals(a, a)
        self.assertEquals(a, c)
        self.assertNotEqual(a, b)
        self.assertTrue(a <= a)
        self.assertTrue(a <= b)
        self.assertTrue(a < b)
    def testHashing(self):
        u'''See to it that hashing works—more or less'''
        a, b, c = Identifiable('1'), Identifiable('2'), Identifiable('1')
        self.assertEquals(hash(a), hash(c))
        self.assertNotEqual(hash(a), hash(b))


class SlotTest(unittest.TestCase):
    '''Test case for the Slot class.'''
    def testAttributes(self):
        '''See to it that attributes get initialized (in the most appropriate fashion) from the initializer'''
        a = Slot(name=u'urn:pds:cheese:texture', values=[u'Runny', u'Rubbery'], slotType='xsd:string')
        self.assertEquals(u'urn:pds:cheese:texture', a.name)
        self.assertEquals('xsd:string', a.slotType)
        self.assertEquals(2, len(a.values))
        self.assertEquals(u'Runny', a.values[0])
        self.assertEquals(u'Rubbery', a.values[1])
    def testComparisons(self):
        '''Check that we can compare slots faithfully'''
        a = Slot('urn:pds:cheese:texture', [u'Rubbery'], 'xsd:string')
        b = Slot('urn:pds:cheese:texture', [u'Runny'], 'xsd:string')
        c = Slot('urn:pds:cheese:texture', [u'Rubbery'], 'xsd:string')
        self.assertEquals(a, a)
        self.assertEquals(a, c)
        self.assertNotEqual(a, b)
        self.assertTrue(a <= a)
        self.assertTrue(a <= b)
        self.assertTrue(a < b)
    def testHashing(self):
        '''Establish that hashing is reasonable'''
        a = Slot('urn:pds:cheese:texture', [u'Rubbery'], 'xsd:string')
        b = Slot('urn:pds:cheese:texture', [u'Runny'], 'xsd:string')
        c = Slot('urn:pds:cheese:texture', [u'Rubbery'], 'xsd:string')
        self.assertEquals(hash(a), hash(c))
        self.assertNotEqual(hash(a), hash(b))

    
class RegistryObjectTest(unittest.TestCase):
    '''Test case for the RegistryObject class.'''
    def testAttributes(self):
        '''Safeguard the initialization parameters get assigned to expected attributes'''
        a = RegistryObject(
            guid='123', lid='urn:pds:lid1', home=u'http://home.com/', slots=set(), name=u'Eric', objectType='test-object',
            status='submitted', description=u'A testing registry object—no need to panic.', versionName='1.0', versionID='1.0.0'
        )
        self.assertEquals('123', a.guid)
        self.assertEquals(u'http://home.com/', a.home)
        self.assertEquals(set(), a.slots)
        self.assertEquals('urn:pds:lid1', a.lid)
        self.assertEquals(u'Eric', a.name)
        self.assertEquals('test-object', a.objectType)
        self.assertEquals('submitted', a.status)
        self.assertEquals(u'A testing registry object—no need to panic.', a.description)
        self.assertEquals('1.0', a.versionName)
        self.assertEquals('1.0.0', a.versionID)
    def testReadOnlyAttributes(self):
        '''Establish that the lid and objectType attributes are read-only'''
        a = RegistryObject(
            guid='123', lid='urn:pds:lid1', home=u'http://home.com/', slots=set(), name=u'Eric', objectType='test-object',
            status='submitted', description=u'A testing registry object—no need to panic.', versionName='1.0', versionID='1.0.0'
        )
        try:
            a.lid = 'urn:pds:lid2'
            self.fail('Could reassign lid')
        except AttributeError: pass
        try:
            a.objectType = 'untest-object'
            self.fail('Could reassign objectType')
        except AttributeError: pass


class _RegistryObjectTestCase(unittest.TestCase):
    '''
    An abstract test case for subclasses of RegistryObject.  Includes a
    built-in test for the object type of the subclasses.
    '''
    def _createInstance(self):
        '''
        Create a canonical instance of the RegistryObject subclass suitable for testing.  Subclasses must
        override this method.
        '''
        raise NotImplementedError('Subclasses of %s must implement _createInstance' % self.__class__.__name__)
    def _getObjectType(self):
        '''
        Test for canonical object type in the PDS dialect of the ebXML classification scheme.  Subclasses
        must override this method.
        '''
        raise NotImplementedError('Subclasses of %s must implement _getObjectType' % self.__class__.__name__)
    def testObjectType(self):
        '''Ensure instances of a RegistryObject subclass have the expected objectType setting.'''
        instance = self._createInstance()
        self.assertEquals(self._getObjectType(), instance.objectType)


class ServiceTest(_RegistryObjectTestCase):
    '''Test case for the Service class.'''
    def _createInstance(self):
        return Service(
            guid='456', lid='urn:pds:service1', home=u'http://home.com/', slots=set(), name=u'Cheese Shop', status='submitted',
            description=u'A test service—still no need to panic.', versionName='1.0', versionID='1.0.0', serviceBindings=set()
        )
    def _getObjectType(self):
        return 'Service'
    def testAttributes(self):
        '''Check that attributes get set the way we like'''
        a = self._createInstance()
        self.assertEquals(set(), a.serviceBindings)


class ServiceBindingTest(_RegistryObjectTestCase):
    '''Test case for the ServiceBinding class'''
    def _createInstance(self):
        return ServiceBinding(
            guid='789', lid='urn:pds:service1', service='456', home=u'http://home.com/', slots=set(), name=u'Cheese Shop',
            status='submitted', description=u'A test service—still no need to panic.', versionName='1.0', versionID='1.0.0',
            accessURI=u'http://services.com/service', specificationLinks=set(), targetBinding='778899'
        )
    def _getObjectType(self):
        return 'ServiceBinding'
    def testAttributes(self):
        '''See to it that our attributes get initialized'''
        a = self._createInstance()
        self.assertEquals('456', a.service)
        self.assertEquals(u'http://services.com/service', a.accessURI)
        self.assertEquals(set(), a.specificationLinks)
        self.assertEquals('778899', a.targetBinding)
    def testReadOnlyAttributes(self):
        '''Make sure that read only attributes are indeed read-only'''
        a = self._createInstance()
        try:
            a.service = '445566'
            self.fail('Could reassign service')
        except AttributeError: pass


class SpecificationLinkTest(_RegistryObjectTestCase):
    '''Test case for the SpecificationLink class'''
    def _createInstance(self):
        return SpecificationLink(
            guid='1138', lid='urn:pds:speclink1', serviceBinding='789', specificationObject='123', home=u'http://home.com/',
            slots=set(), name=u'Order Counter', status='submitted', description=u'Using the Order Counter to shop for cheese.',
            versionName='1.0', versionID='1.0.0', usageDescription=u'Arrive at counter, ask for cheese, exchange currency—duh.',
            usageParameters=[
                u'cheese: what kind of cheese you want to buy',
                u'currency: what money you have to exchange for cheese'
            ]
        )
    def _getObjectType(self):
        return 'SpecificationLink'
    def testAttrbiutes(self):
        '''Confirm that initialization parameters are assigned to attributes named just the way we like'''
        a = self._createInstance()
        self.assertEquals('789', a.serviceBinding)
        self.assertEquals('123', a.specificationObject)
        self.assertEquals(u'Arrive at counter, ask for cheese, exchange currency—duh.', a.usageDescription)
        self.assertEquals(2, len(a.usageParameters))
        self.assertEquals(u'cheese: what kind of cheese you want to buy', a.usageParameters[0])
        self.assertEquals(u'currency: what money you have to exchange for cheese', a.usageParameters[1])
    def testReadOnlyAttributes(self):
        '''Establish that the serviceBinding attribute is immutable'''
        a = self._createInstance()
        try:
            a.serviceBinding = 'new'
            self.fail('Could reassign serviceBinding')
        except AttributeError: pass


def test_suite():
    return unittest.TestSuite([
        unittest.makeSuite(IdentifiableTest),
        unittest.makeSuite(SlotTest),
        unittest.makeSuite(RegistryObjectTest),
        unittest.makeSuite(ServiceTest),
        unittest.makeSuite(ServiceBindingTest),
        unittest.makeSuite(SpecificationLinkTest),
    ])


if __name__ == '__main__':
    unittest.main(defaultTest='test_suite')
    
