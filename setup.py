#!/usr/bin/env python
# -*- coding: utf-8 -*-

from codecs import open
from os import path
from setuptools import setup, find_packages

here = path.abspath(path.dirname(__file__))

with open(path.join(here, 'README.rst'), encoding='utf-8') as readme_file:
    readme = readme_file.read()

with open(path.join(here, 'CHANGES.txt'), encoding='utf-8') as changes_file:
    changes = changes_file.read().replace('.. :changelog:', '')

requirements = [
    # TODO: put package requirements here
    'py27-pip'
]

setup(
    name='PDS-Inspect-Tool',
    version='0.4.0-dev',
    description="PDS Inspect Tool",
    long_description=readme + '\n\n' + changes,
    author="Sean Hardman,Jim Hofman",
    author_email='Sean.Hardman@jpl.nasa.gov,James.E.Hofman@jpl.nasa.gov',
    url='https://github.jpl.nasa.gov/PDSEN/pds-inspect-tool',
    packages=find_packages(exclude=['contrib', 'docs', 'tests']),
    entry_points={
        'console_scripts':[
            'pds-inspect-tool=pds_inspect_tool.View:main',
            ],
        },
    include_package_data=True,
    install_requires=requirements,
    license="MIT",
    classifiers=[
        'Development Status :: 4 - Beta',
        'Intended Audience :: Developers',
        'License :: OSI Approved :: MIT License',
        'Natural Language :: English',
        "Programming Language :: Python :: 2",
        'Programming Language :: Python :: 2.7',
        'Programming Language :: Python :: 3',
        'Programming Language :: Python :: 3.3',
        'Programming Language :: Python :: 3.4',
        'Programming Language :: Python :: 3.5',
    ]
)
