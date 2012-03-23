# encoding: utf-8
# Copyright 2011–2012 California Institute of Technology. ALL RIGHTS
# RESERVED. U.S. Government Sponsorship acknowledged.

from setuptools import setup, find_packages
import xml.dom, xml.dom.minidom
import os.path

# Maven 2
# -------
# 
# This is a lonely Python project living in a hostile Java world, so we have to get
# project metadata from a Maven 2 POM_ living alongside setup.py
#
# .. _POM: http://maven.apache.org/pom.html

def _text(node):
    a = []
    _text0(node, a)
    return u''.join(a)
def _text0(node, a):
    if node.nodeType in (xml.dom.Node.CDATA_SECTION_NODE, xml.dom.Node.TEXT_NODE):
        a.append(node.nodeValue)
    for child in node.childNodes:
        _text0(child, a)
def _valueFor(nodeName, parentName, doc):
    return [_text(i) for i in pomDoc.getElementsByTagName(nodeName) if i.parentNode.nodeName == parentName][0]
pomDoc = xml.dom.minidom.parse(os.path.join(os.path.dirname(__file__), 'pom.xml'))
_description = _valueFor('description', 'project', pomDoc)
_url = _valueFor('url', 'project', pomDoc)
_author = _valueFor('name', 'developer', pomDoc)
_authorEmail = _valueFor('email', 'developer', pomDoc)

# The version is no longer included in pom.xml, but in ../pom.xml.  Although to be completely
# Maven-subservient, we should look it up by the parent pom description.  But screw that.
parentPOM = os.path.join(os.path.dirname(__file__), '..', 'pom.xml')
if os.path.isfile(parentPOM):
    pomDoc = xml.dom.minidom.parse(os.path.join(os.path.dirname(__file__), '..', 'pom.xml'))
    _version = _valueFor('version', 'project', pomDoc)
else:
    _version = 'UNKNOWN'


# Package data
# ------------

_name            = 'pds.registry'
_downloadURL     = 'http://oodt.jpl.nasa.gov/dist/pds'
_maintainer      = 'Sean Kelly'
_maintainerEmail = 'sean.kelly@jpl.nasa.gov'
_license         = 'Proprietary'
_namespaces      = ['pds', 'pds.registry']
_zipSafe         = True
_keywords        = 'ebxml registry information model client nasa pds'
_testSuite       = 'pds.registry.tests.test_suite'
_entryPoints     = {}
_requirements = [
    'setuptools',
    'anyjson',
]
_testRequirements = [
    'zope.testing',
]
_classifiers = [
    'Development Status :: 2 - Pre-Alpha',
    'Environment :: Web Environment',
    'Intended Audience :: Science/Research',
    'License :: Other/Proprietary License',
    'Operating System :: OS Independent',
    'Programming Language :: Python',
    'Topic :: Scientific/Engineering :: Astronomy',
    'Topic :: Scientific/Engineering :: Atmospheric Science',
    'Topic :: System :: Distributed Computing',
]
_ignoredModules = [
    'bootstrap',
    'distribute_setup',
    'setup',
]

# Setup Metadata
# --------------

def _read(*rnames):
    return open(os.path.join(os.path.dirname(__file__), *rnames)).read()

_header = '*' * len(_name) + '\n' + _name + '\n' + '*' * len(_name)
_longDescription = _header + '\n\n' + _read('README.txt') + '\n\n' + _read('docs', 'INSTALL.txt') + '\n\n' \
    + _read('docs', 'HISTORY.txt') + '\n\n' + _read('docs', 'LICENSE.txt')
open('doc.txt', 'w').write(_longDescription)

setup(
    author=_author,
    author_email=_authorEmail,
    classifiers=_classifiers,
    description=_description,
    download_url=_downloadURL,
    entry_points=_entryPoints,
    extras_require={'test': _testRequirements},
    include_package_data=True,
    install_requires=_requirements,
    keywords=_keywords,
    license=_license,
    long_description=_longDescription,
    name=_name,
    namespace_packages=_namespaces,
    packages=find_packages('src'),
    package_dir={'': 'src'},
    test_suite=_testSuite,
    url=_url,
    version=_version,
    zip_safe=_zipSafe,
)
