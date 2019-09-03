#!/usr/bin/env python
# -*- coding: utf-8 -*-

# Copyright (c) 2019, California Institute of Technology ("Caltech").
# U.S. Government sponsorship acknowledged.
#
# All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
#
# * Redistributions of source code must retain the above copyright notice,
#   this list of conditions and the following disclaimer.
# * Redistributions must reproduce the above copyright notice, this list of
#   conditions and the following disclaimer in the documentation and/or other
#   materials provided with the distribution.
# * Neither the name of Caltech nor its operating division, the Jet Propulsion
#   Laboratory, nor the names of its contributors may be used to endorse or
#   promote products derived from this software without specific prior written
#   permission.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
# AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
# ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
# LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
# CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
# SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
# INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
# CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
# ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
# POSSIBILITY OF SUCH DAMAGE.

from codecs import open
from os import path
from setuptools import setup, find_packages

here = path.abspath(path.dirname(__file__))

with open(path.join(here, 'README.rst'), encoding='utf-8') as readme_file:
    readme = readme_file.read()

# PyPI expectes reStructuredText, but the changelog is apparently in Markdown.
# Oh well!
with open(path.join(here, 'CHANGELOG.md'), encoding='utf-8') as changes_file:
    changes = changes_file.read().replace('.. :changelog:', '')

requirements = [
    'setuptools',                     # All modern setup.py's should require setuptools
    'matplotlib==2.2.3',              # 3.0.0 and later require Python 3
    'backports.functools_lru_cache',  # matplotlib fails to declare this dependency
    'seaborn==0.9.0',                 # May as well pin this version too for future resiliency
    'pillow',                         # Needed by PDSView
    'airspeed',                       # Needed by PDSView
    'PDS4-tools==1.0',                # Needed by PDSView
    'six',                            # Needed for PyInstaller
]

setup(
    name='PDSView',
    version='0.4.0-dev',
    description="PDSView views (and inspects) PDS data",
    long_description=readme + '\n\n' + changes,
    author="Sean Hardman,Jim Hofman",
    author_email='Sean.Hardman@jpl.nasa.gov,James.E.Hofman@jpl.nasa.gov',
    url='https://github.com/NASA-PDS-Incubator/pds-view',
    entry_points={
        # This should be `gui_scripts` but I get no script generated
        'console_scripts': ['PDSView=pds_view.View:main']
    },
    packages=find_packages(exclude=['contrib', 'docs', 'tests']),
    package_data={
        # If any package contains *.txt or *.rst files, include them:
        '': ['*.txt', '*.rst']
    },
    include_package_data=True,
    install_requires=requirements,
    license="Apache License 2.0",
    classifiers=[
        'Development Status :: 4 - Beta',
        'Intended Audience :: Developers',
        'License :: OSI Approved :: Apache Software License',
        'Natural Language :: English',
        "Programming Language :: Python :: 2",
        'Programming Language :: Python :: 2.7',
        'Programming Language :: Python :: 3',
        'Programming Language :: Python :: 3.3',
        'Programming Language :: Python :: 3.4',
        'Programming Language :: Python :: 3.5',
    ]
)
