// Copyright 2009-2016, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government sponsorship acknowledged.
// Any commercial use must be negotiated with the Office of Technology Transfer
// at the California Institute of Technology.
//
// This software is subject to U. S. export control laws and regulations
// (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
// is subject to U.S. export control laws and regulations, the recipient has
// the responsibility to obtain export licenses or other export authority as
// may be required before exporting such information to foreign countries or
// providing access to foreign nationals.
//
// $Id: LDIFGenerator.java $
package gov.nasa.pds.security;

import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class LDIFGenerator {
	String ldapHost;
	int ldapPort;
	String ldapUser;
	String ldapPass;
	
	String target;
	private List<PersEntry> persEntries;
	boolean isDirectory;
	final static String pwToOverwrite = "changeMe";
	private final static String CHECKSUM = "checksum";
    private final static String ENTRY = "//Standard_Product_Member_Entry";
    private final static String FILENAME = "directory_path_name";
    
	public static final String PERS_ROOT_TAG = "Product_Context";

	List<String> affilMembers = new ArrayList<String>();
	List<String> guestMembers = new ArrayList<String>();

	PrintWriter ldifFile = null;
	PrintWriter deleteLdifFile = null;
	String ldifFilename = "pds_personnel.ldif";
	String ldifFilename_delete = "pds_personnel_delete.ldif";
	
	//enum PDS_AFFIL_ATTRS
	String[] PDS_AFFIL_ATTRS = { "logical_identifier", "name", "affiliation_type", "alternate_telephone_number", 
		"electronic_mail_address", "institution_name", "postal_address_text", 
		"registration_date", "sort_name", "telephone_number" };
	
	String[] LDAP_AFFIL_ATTRS = { "uid", "fullName", "affiliationType", "alternateTelephoneNumber",
			"mail", "institutionName", "postalAddress",
			"registrationDate", "sortName", "telephoneNumber" };
	
	//enum PDS_GUEST_ATTRS 
	String[] PDS_GUEST_ATTRS = { "logical_identifier", "name", "registration_date", "sort_name" };
	
	String[] LDAP_GUEST_ATTRS = { "uid", "fullName", "registrationDate", "sortName" };
	
	LDIFGenerator (String[] args) {
	    ldapHost = "pds-dev";
		//ldapHost = "pdsdev2.jpl.nasa.gov";
		ldapPort = 389;
		ldapUser = "cn=Manager";
		ldapPass = "adminadmin";
		isDirectory = false;
		set(args);
	}
	
	private void set(String[] args) {
		for (int i=0; i<args.length; i++) {
			if (args[i].toLowerCase().startsWith("-t")) {
				target = args[i+1];
			}
			else if (args[i].toLowerCase().startsWith("-s")) {
				ldapHost = args[i+1];
			}
			else if (args[i].toLowerCase().startsWith("-n")) {
				ldapPort = Integer.parseInt(args[i+1]);
			}
			else if (args[i].toLowerCase().startsWith("-u")) {
				ldapUser = args[i+1];
			}
			else if (args[i].toLowerCase().startsWith("-p")) {
				ldapPass = args[i+1];
			}
		}
		
		File t = new File(target);
		if (t.isDirectory())
			isDirectory = true;

    	try {
    		parse(t, isDirectory);
		}
		catch (Exception e) {
           e.printStackTrace();
		}
	}

	void parse(File persFile, boolean dir) throws XPathExpressionException, SAXException, ParserConfigurationException, IOException {
        persEntries = new ArrayList<PersEntry>();
		String parent = persFile.getPath();

		if (dir) {
		   System.out.println("in Parse() method with dir flag=true   parent = " + parent);
		   File[] listOfFiles = persFile.listFiles();
		   for (int i=0; i<listOfFiles.length; i++) {
		      if (listOfFiles[i].isFile()) {
			     System.out.println("File " + listOfFiles[i].getName());

				 String filename = listOfFiles[i].getName();

                 File label = new File(filename);
                 if (!label.isAbsolute()) {
                    label = new File(parent, label.toString());
                 }
                 persEntries.add(new PersEntry(label, ""));
			  }
			  else {
			     System.out.println("Direcotry " + listOfFiles[i].getName());
			  }
		   }
		} 
		else {
			System.out.println("in else.....parse()....");
			String filename = persFile.getAbsolutePath();
			if (filename.contains("\\"))
				filename = filename.replace("\\", "/");

			System.out.println("filename = " + filename);
			File label = new File(filename);
			if(!label.isAbsolute()) {
				label = new File(parent, label.toString());
			}

			persEntries.add(new PersEntry(label, ""));
		}
	}

	void process() throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		XMLExtractor xmlExtractor;
		System.out.println("in process method");
		ldifFile = new PrintWriter(ldifFilename);
		for (int i=0; i<persEntries.size(); i++) {
			File persFile = persEntries.get(i).getFile();
			System.out.println("file[" + i + "] = " + persFile.getName());
			xmlExtractor = new XMLExtractor(persFile);
			
			String root = xmlExtractor.getDocNode().getNodeName();
			if (!root.startsWith(PERS_ROOT_TAG)) {
				System.err.println("[" + persFile + "] File is not a PDS Personnel File. Missing \'" + PERS_ROOT_TAG + 
						"\' root tag.");
				break;
			}
			
			System.out.println("\n******File[" + i + "] = " + persFile);
			processPersFile(persFile);	
		}
		
		// add members to right group
		addPDSAffilMembers();
		ldifFile.println();
		addPDSGuestMembers();
		ldifFile.close();


		deleteLdifFile = new PrintWriter(ldifFilename_delete);
		for (int j=0; j<affilMembers.size(); j++) {
            deleteLdifFile.println("dn: " + affilMembers.get(j) + "\nchangetype: delete\n");
        }
        for (int j=0; j<guestMembers.size(); j++) {
            deleteLdifFile.println("dn: " + guestMembers.get(j) + "\nchangetype: delete\n");
        }
	    deleteLdifFile.close();
	}

	void processPersFile(File persFile) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {

		/* 
		 * sample ldif entries
		 * 
		 * dn: uid=aingersoll,ou=people,dc=pdsdev2,dc=jpl,dc=nasa,dc=gov
		 * changetype: modify
		 * uid: aingersoll
		 * cn: Andrew P. Ingersoll
		 * sn: Ingersoll
		 * userPassword: NewPDS31a
		 * givenName: Andrew
		 * mail: API@GPS.CALTECH.EDU
		 * facsimileTelephoneNumber: 626-585-1917
		 * telephoneNumber: 626-395-6167
		 * postalAddress: CALIFORNIA INSTITUTE OF TECHNOLOGY
		 *                    DIV. OF GEOLOGICAL and PLANETARY SCIENCES
		 *                    MAIL CODE 150-21
		 *                    1200 E. CALIFORNIA BLVD.
		 *                    PASADENA, CA 91125
		 * fullName: Andrew P. Ingersoll
		 * sortName: INGERSOLL, ANDREW P.
		 * institutionName: CALIFORNIA INSTITUE OF TECHNOLOGY
		 * nodeName: Planetary_Atmospheres
		 * registrationDate: 1996-09-26T00:00:00
		 * role: NODE ADVISOR
		 * affiliationType: NODE ADVISOR
		 * alternateTelephoneNumber: null
		 * objectClass: person
		 * objectClass: inetorgperson
		 * objectClass: organizationalperson
		 * objectClass: top
		 * objectClass: PDSPerson
		 */
		XMLExtractor xmlExtractor = new XMLExtractor(persFile);
		String root = xmlExtractor.getDocNode().getNodeName();

		Node rootNode = xmlExtractor.getDocNode();
		NodeList nodeList = rootNode.getChildNodes();
		for (int j = 0; j < nodeList.getLength(); j++) {
			Node node = nodeList.item(j);
			String nodeName = node.getNodeName();
			System.out.println("node name = " + nodeName);

			int count = 1;
			String name = null;
			if (nodeName.equals("PDS_Affiliate")) {

				// only add when the object is not exists
				// result: 32 No such object
				// ldapsearch -x -h pdsdev2.jpl.nasa.gov -p 389 -b uid=aingersoll,ou=people,dc=pdsdev2,dc=jpl,dc=nasa,dc=gov

				/*
			//System.out.println("PDS_AFFIL_ATTRS[0] = " + xmlExtractor.getValueFromDoc(PDS_AFFIL_ATTRS[0]));
			NodeList list = xmlExtractor.getNodesFromDoc("//" + PDS_AFFIL_ATTRS[0]);
			for (int i=0; i<list.getLength(); i++) {
				System.out.println("nodename = " + list.item(i).getNodeName() +
						"     value = " + xmlExtractor.getValueFromDoc("//" + PDS_AFFIL_ATTRS[0]));
			}
				 */

				String dn = null;
				for (int i=0; i<PDS_AFFIL_ATTRS.length; i++) {
					//System.out.printf("PDS_AFFIL_ATTRS %s = %s\n", i, PDS_AFFIL_ATTRS[i]);
					String value = xmlExtractor.getValueFromDoc("//"+PDS_AFFIL_ATTRS[i]);
					value = value.trim();

					if (PDS_AFFIL_ATTRS[i].equals("logical_identifier")) {
						value = value.substring(value.lastIndexOf("personnel.")+10).toLowerCase();
						dn = new String(LDAP_AFFIL_ATTRS[i] + "=" + value + ",ou=people,dc="+ldapHost+ ",dc=jpl,dc=nasa,dc=gov");
						System.out.println("dn: " + dn);
						affilMembers.add(dn);
						ldifFile.println("dn: " + dn);
						ldifFile.println("changetype: add");
					}
					else if (PDS_AFFIL_ATTRS[i].equals("name")) {
						name = value;
					}
					//System.out.println(PDS_AFFIL_ATTRS[i] + " = " + value + "    : " + LDAP_AFFIL_ATTRS[i]);
					System.out.println(LDAP_AFFIL_ATTRS[i] + ": " + value);
					ldifFile.println(LDAP_AFFIL_ATTRS[i] + ": " + value);
				}


				//generate the ldif file with the entries
				//then use ldapmodify to insert
				//   % ldapmodify -p 389 -h pdsdev2.jpl.nasa.gov -D "cn=Directory Manager" -w adminadmin -c -a -f <file from above step>
			}
			else if (nodeName.equals("PDS_Guest")) {
				/*
			//System.out.println("PDS_AFFIL_ATTRS[0] = " + xmlExtractor.getValueFromDoc(PDS_AFFIL_ATTRS[0]));
			NodeList list = xmlExtractor.getNodesFromDoc("//" + PDS_AFFIL_ATTRS[0]);
			for (int i=0; i<list.getLength(); i++) {
				System.out.println("nodename = " + list.item(i).getNodeName() +
						"     value = " + xmlExtractor.getValueFromDoc("//" + PDS_AFFIL_ATTRS[0]));
			}
				 */

				String dn = null;			
				for (int i=0; i<PDS_GUEST_ATTRS.length; i++) {
					String value = xmlExtractor.getValueFromDoc("//"+PDS_GUEST_ATTRS[i]);
					value = value.trim();

					if (PDS_GUEST_ATTRS[i].equals("logical_identifier")) {
						value = value.substring(value.lastIndexOf("personnel.")+10).toLowerCase();
						dn = new String(LDAP_AFFIL_ATTRS[i] + "=" + value + ",ou=people,dc="+ldapHost+",dc=jpl,dc=nasa,dc=gov");
						System.out.println("dn: " + dn);
						guestMembers.add(dn);
						ldifFile.println("dn: " + dn);
						ldifFile.println("changetype: add");
					}
					else if (PDS_GUEST_ATTRS[i].equals("name")) {
						name = value;
					}
					//System.out.println(PDS_AFFIL_ATTRS[i] + " = " + value + "    : " + LDAP_AFFIL_ATTRS[i]);
					System.out.println(LDAP_GUEST_ATTRS[i] + ": " + value);
					ldifFile.println(LDAP_GUEST_ATTRS[i] + ": " + value);
				}
			}
			else 
				continue;

			ldifFile.println("cn: " + name);
			System.out.println("cn = " + name);
			if (name.contains(" ")) {
				ldifFile.println("sn: " + name.substring(name.lastIndexOf(" ")+1));
				ldifFile.println("givenName: " + name.substring(0, name.indexOf(" ")));
			}
			else {
				ldifFile.println("sn: " + name);
				ldifFile.println("givenName: " + name);
			}
			ldifFile.println("userPassword: "+pwToOverwrite);
			ldifFile.println("objectClass: top");
			ldifFile.println("objectClass: PDSPerson");
			ldifFile.println("objectClass: person");
			ldifFile.println("objectClass: inetorgperson");
			ldifFile.println("objectClass: organizationalperson\n");
		}
	}

	void addPDSAffilMembers() {
		 /*
		 dn: cn=PDS_Affiliate,ou=groups,dc=pdsdev,dc=jpl,dc=nasa,dc=gov
		 objectClass: groupofuniquenames
		 objectClass: top
		 cn: PDS_Affiliate
		 uniqueMember: uid=acochran,ou=people,dc=pdsdev,dc=jpl,dc=nasa,dc=gov
		 */
		if (affilMembers.size()==0)
			return;
		System.out.println("\n\nAssign to the member PDS_AFFIL");
		ldifFile.println("dn: cn=PDS_Affiliate,ou=groups,dc="+ldapHost+",dc=jpl,dc=nasa,dc=gov\n" + 
		    "changetype: add\n" +
			"objectClass: groupofuniquenames\n" + 
			"objectClass: top\ncn: PDS_Affiliate");
		for (int j=0; j<affilMembers.size(); j++) {
			System.out.println("uniqueMember: " + affilMembers.get(j));
			ldifFile.println("uniqueMember: " + affilMembers.get(j));
		}
	}
	
	void addPDSGuestMembers() {
		/* 
		 * dn: cn=PDS_Guest,ou=groups,dc=pdsdev2,dc=jpl,dc=nasa,dc=gov
		   objectClass: groupofuniquenames
	       objectClass: top
	       cn: PDS_Guest
		 * uniqueMember: uid=aingersoll,ou=people,dc=pdsdev2,dc=jpl,dc=nasa,dc=gov
		 */
		if (guestMembers.size()==0)
			return;
		System.out.println("\n\nAssign to the member PDS_Guest");
		ldifFile.println("dn: cn=PDS_Guest,ou=groups,dc="+ldapHost+",dc=jpl,dc=nasa,dc=gov\n" +
		    "changetype: add\n" +
		    "objectClass: groupofuniquenames\n" +
		    "objectClass: top\ncn: PDS_Guest");
		for (int j=0; j<guestMembers.size(); j++) {
			System.out.println("uniqueMember: " + guestMembers.get(j));
			ldifFile.println("uniqueMember: " + guestMembers.get(j));
		}
	}
	
	public static void main(String[] args) {
		LDIFGenerator ldifGenerator;
		
		if (args.length == 0) {
			System.out.println("\nUsage: java gov.nasa.pds.security.LDIFGenerator -t <dir | file> " + 
					"-s <ldap server host> -n <ldap port> -u <ldap user> -p <ldap password>");
			System.exit(0);
		}
		
		try {
			ldifGenerator = new LDIFGenerator(args);
			ldifGenerator.process();
			
		} catch (Exception e) {
			System.err.println(e.getMessage() + "\n");
			e.printStackTrace();
		} 
		/*catch (SAXException e) {
			e.printStackTrace();
		}
		*/
	}
}
