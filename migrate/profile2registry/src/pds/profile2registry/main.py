# encoding: utf-8
# Copyright 2013 California Institute of Technology. ALL RIGHTS
# RESERVED. U.S. Government Sponsorship acknowledged.

# p2r ID → Writes ID in XML to stdout
# p2r [opt] --output=OUTFILE ID → Writes ID to in XML to given OUTFILE
# p2r [opt] --registry=URL ID → Registers ID with the Registry Service at URL
#
# [opt]ions are:
# --verbose - Work verbosely
# --profile=URL - Override URL to profile service
# --help - Buh.

from contextlib import closing
from lxml import etree
from oodt.profile import Profile
from pds.registry.model import ExtrinsicObject, Slot
from pds.registry.net import PDSRegistryClient
import sys, argparse, logging, oodt, urllib, urllib2, xml.dom.minidom, os.path

# Default profile server
PROFILE_URL = u'http://starbrite.jpl.nasa.gov/q'

# XML Namespace
REGISTRY_NS_URI = u'http://registry.pds.nasa.gov'
REGISTRY = u'{{{}}}'.format(REGISTRY_NS_URI)
NSMAP = {None: REGISTRY_NS_URI}

# Preset values for the ExtrinsicObject
INFORMATION_MODEL_VERSION = u'1.1.0.1'
PRODUCT_CLASS             = u'Product_Proxy_PDS3'

# Log format
LOG_FORMAT = u'%(levelname)-8s %(message)s'

# Functions

def _parseArgs():
    parser = argparse.ArgumentParser(
        description=u'Convert a profile with the given ID into a PDS Registry Service extrinsic. '
        u'Specify the ID as the sole command-line positional argument. With no other options, this will '
        u'write the extrinsic in XML to the standard output.  Use -o to specify an output file.  Or use '
        u'-r to register the extrinsic with a given Registry Service.  NOTE: if an ID matches more than '
        u'one profile, it won\'t be written to stdout (no options), multiple XML files will be created '
        u'(-o option), or multiple versions will be registered (-r option).'
    )
    parser.add_argument('-o', '--output', type=argparse.FileType('w'),
        help='Write converted profile ID as XML to OUTPUT file; specify "-" for standard output')
    parser.add_argument('-r', '--registry', metavar='URL', help='Register converted profile ID with Registry Service at URL')
    parser.add_argument('-p', '--profile', metavar='URL', default=PROFILE_URL,
        help='Access the profile service at URL instead of the default "%s"' % PROFILE_URL)
    parser.add_argument('-v', '--verbose', action='store_true', default=False, help='Be noisy')
    parser.add_argument('id', metavar='ID', help='Profile ID to convert')
    return parser.parse_args()


def _getProfiles(profileID, profileURL):
    logging.info(u'Getting profiles %s from %s', profileID, profileURL)
    params = {
        u'type': u'profile',
        u'object': u'JPL.PDS.MasterProd',
        u'keywordQuery': u'identifier = %s' % profileID,
    }
    logging.debug(u'Params are %r', params)
    profiles, con = [], None
    logging.debug(u'Opening connection to %s', profileURL)
    with closing(urllib2.urlopen(profileURL, urllib.urlencode(params))) as con:
        d = xml.dom.minidom.parse(con)
        logging.debug(u'Parsed XML with minidom, got %r; looking for "profile" elements', d)
        for i in d.documentElement.getElementsByTagName(u'profile'):
            profiles.append(Profile(node=i))
        logging.debug(u'# profiles created: %d', len(profiles))
    return profiles


def _toExtrinsicObjects(profiles):
    logging.info(u'Converting profiles "%s" to extrinsics', u', '.join([p.resAttr.title for p in profiles]))
    extrinsics = []
    for versionNum, profile in enumerate(profiles, start=1):
        lid = u'urn:nasa:pds:' + profile.resAttr.identifier.replace(u'/', u'-').lower()
        name = profile.resAttr.title
        logging.debug(u'Logical ID="%s", name="%s"', lid, name)
        slots = set((
            Slot(u'access_url', profile.resAttr.locations),
            Slot(u'product_class', [PRODUCT_CLASS]),
            Slot(u'information_model_version', [INFORMATION_MODEL_VERSION]),
            Slot(u'version_id', [u'{}.0'.format(versionNum)]),
        ))
        logging.debug(u'Created basic slots, converting profile elements into slots')
        for elem in profile.profElements.itervalues():
            slots.add(Slot(elem.name, [i.strip() for i in elem.getValues()]))
        logging.debug(u'Total slots: %d; creating ExtrinsicObject', len(slots))
        # Let registry service assign guid, that's why 1st arg is None
        extrinsics.append(ExtrinsicObject(None, lid, home=None, slots=slots, name=name, objectType=PRODUCT_CLASS))
    return extrinsics


def _writeExtrinsicAsXML(extrinsic, output):
    logging.info(u'Writing extrinsic as XML')
    root = etree.Element(
        REGISTRY + u'extrinsicObject', nsmap=NSMAP, lid=extrinsic.lid, name=extrinsic.name,
        objectType=extrinsic.objectType
    )
    for attrName in ('contentVersion', 'description', 'home', 'mimeType', 'status', 'versionName'):
        value = getattr(extrinsic, attrName, None)
        if value is not None:
            root.set(unicode(attrName), value)
    for slot in extrinsic.slots:
        slotElem = etree.SubElement(root, REGISTRY + u'slot', nsmap=NSMAP, name=slot.name)
        for value in slot.values:
            valueElem = etree.SubElement(slotElem, REGISTRY + u'value', nsmap=NSMAP)
            valueElem.text = value
    doc = etree.ElementTree(root)
    logging.debug(u'Writing pretty XML to %r', output)
    doc.write(output, encoding='UTF-8', standalone=True, xml_declaration=True, pretty_print=True)


def _registerExtrinsics(extrinsics, url):
    logging.info(u'Connecting to registry client at %s', url)
    con = PDSRegistryClient(url)
    for extrinsic in extrinsics:
        logging.debug(u'Calling "putExtrinsic" on %s', extrinsic.lid)
        con.putExtrinsic(extrinsic)
    logging.debug(u'All done.')


def main(argv=sys.argv):
    '''Doooo eeeet.'''
    args = _parseArgs()
    if args.verbose:
        logging.basicConfig(level=logging.DEBUG, format=LOG_FORMAT)
    else:
        logging.basicConfig(level=logging.WARN, format=LOG_FORMAT)
    profiles = _getProfiles(args.id, args.profile)
    if len(profiles) == 0:
        print >>sys.stderr, u'No profiles found for ID %s' % args.id
        sys.exit(1)
    extrinsics = _toExtrinsicObjects(profiles)
    # No other arguments?  Write XML to stdout
    if args.registry is None and args.output is None:
        if len(extrinsics) > 1:
            print >>sys.stderr, u'Got {} matches for "{}"; can\'t write to stdout'.format(len(extrinsics), args.id)
            sys.exit(1)
        _writeExtrinsicAsXML(extrinsics[0], sys.stdout)
    if args.output is not None:
        if len(extrinsics) == 1:
            _writeExtrinsicAsXML(extrinsics[0], args.output)
        else:
            d, fn = os.path.dirname(args.output.name), os.path.basename(args.output.name)
            args.output.close()
            dot = fn.rfind('.')
            if dot == -1:
                prefix, suffix = os.path.join(d, fn), ''
            else:
                prefix, suffix = os.path.join(d, fn[0:dot]), fn[dot:]
            for versionNum, extrinsic in enumerate(extrinsics, start=1):
                target = u'{}-{}.0{}'.format(prefix, versionNum, suffix)
                with open(target, 'wb') as out:
                    _writeExtrinsicAsXML(extrinsics[0], out)
    if args.registry is not None:
        _registerExtrinsics(extrinsics, args.registry)
    sys.exit(0)


if __name__ == '__main__':
    main()
    
    
# http://starbrite.jpl.nasa.gov/q?type=profile&object=JPL.PDS.MasterProd&keywordQuery=identifier+%3D+GO-J/JSA-SSI-2-REDR-V1.0:G1G0030