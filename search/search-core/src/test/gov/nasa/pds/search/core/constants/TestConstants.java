//	Copyright 2009-2012, by the California Institute of Technology.
//	ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
//	Any commercial use must be negotiated with the Office of Technology 
//	Transfer at the California Institute of Technology.
//	
//	This software is subject to U. S. export control laws and regulations 
//	(22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software 
//	is subject to U.S. export control laws and regulations, the recipient has 
//	the responsibility to obtain export licenses or other export authority as 
//	may be required before exporting such information to foreign countries or 
//	providing access to foreign nationals.
//	
//	$Id$
//

package gov.nasa.pds.search.core.constants;

import org.junit.Ignore;

/**
 * 
 * @author jpadams
 */
@Ignore
public final class TestConstants {
	/** Relative Config Directory Path for testing locally **/
	public static final String CONFIG_DIR_RELATIVE="./src/main/resources/conf/";
	
	/** Relative Facet Directory Path for testing locally **/
	public static final String FACET_DIR_RELATIVE="./src/main/resources/facets/";
	
	/** Relative Search Service Directory Path for testing locally **/
	public static final String SEARCH_HOME_RELATIVE="./target/test";
	
	/** Registry URL for PDS3 Context Products used for testing purposes **/
	//public static final String PDS3_REGISTRY_URL="http://pdsdev.jpl.nasa.gov:8080/registry";
	public static final String PDS3_REGISTRY_URL="http://pdsbeta.jpl.nasa.gov:8080/registry";
	
	/** PSA Registry URL used for testing purposes **/
	public static final String PSA_REGISTRY_URL="http://pdsdev.jpl.nasa.gov:8080/registry-psa";
	
	/** Search Service Solr URL **/
	public static final String SOLR_SERVER_URL="http://pdsdev.jpl.nasa.gov:8080/search-service/pds";
	
}
