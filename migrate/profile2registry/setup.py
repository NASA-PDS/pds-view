# encoding: utf-8
# Copyright 2013 California Institute of Technology. ALL RIGHTS
# RESERVED. U.S. Government Sponsorship acknowledged.

from setuptools import setup, find_packages
import os.path

# Package data
# ------------

_name            = 'pds.profile2registry'
_version         = '1.0.1'
_description     = 'Install PDS information stored in OODT Profile Servers into PDS 2010 Registry'
_url             = 'http://pypi.python.org/python/pds.profile2registry'
_downloadURL     = 'http://oodt.jpl.nasa.gov/dist/pds'
_author          = 'Sean Kelly'
_authorEmail     = 'sean.kelly@jpl.nasa.gov'
_maintainer      = 'Sean Kelly'
_maintainerEmail = 'sean.kelly@jpl.nasa.gov'
_license         = 'Proprietary'
_namespaces      = ['pds']
_zipSafe         = True
_keywords        = 'pds nasa planetary data profile conversion oodt'
_testSuite       = 'pds.profile2registry.tests.test_suite'
_entryPoints     = {
    'console_scripts': ['profile2registry=pds.profile2registry:main']
}
_requirements = [
    'setuptools',
    'pds.registry',
    'oodt',
    'lxml',
]
_extras = {
    'test': []
}
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


# Setup Metadata
# --------------

def _read(*rnames):
    return open(os.path.join(os.path.dirname(__file__), *rnames)).read()

_header = '*' * len(_name) + '\n' + _name + '\n' + '*' * len(_name)
_longDescription = _header + '\n\n' + _read('README.rst') + '\n\n' + _read('docs', 'INSTALL.txt') + '\n\n' \
    + _read('docs', 'HISTORY.txt') + '\n\n' + _read('docs', 'LICENSE.txt')
open('doc.txt', 'w').write(_longDescription)

setup(
    author=_author,
    author_email=_authorEmail,
    classifiers=_classifiers,
    description=_description,
    download_url=_downloadURL,
    entry_points=_entryPoints,
    extras_require=_extras,
    include_package_data=True,
    install_requires=_requirements,
    keywords=_keywords,
    license=_license,
    long_description=_longDescription,
    name=_name,
    namespace_packages=_namespaces,
    packages=find_packages('src', exclude=['bootstrap']),
    package_dir={'': 'src'},
    test_suite=_testSuite,
    url=_url,
    version=_version,
    zip_safe=_zipSafe,
)
