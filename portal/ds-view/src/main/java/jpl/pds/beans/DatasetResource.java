// Copyright 2000-2002 California Institute of Technology.  ALL RIGHTS RESERVED.
// U.S. Government Sponsorship acknowledged.
//

package jpl.pds.beans;

/** Information about a resource associated with a dataset
 *
 * @author Thuy Tran
 */
public class DatasetResource {

	String resName;
	String resLink;
	String resClass;

	public DatasetResource (String name, String link, String clazz) {
		resName = name;
		resLink = link;
		resClass = clazz;
	}

	public DatasetResource () {
		resName = "";
		resLink = "";
		resClass = "";
	}

	public String getResourceName () {
		return resName;
	}

	public String getResourceLink () {
		return resLink;
	}

	public String getResourceClass () {
		return resClass;
	}

	public void setResourceName(String name) {
		resName = name;
	}

	public void setResourceLink (String link) {
		resLink = link;
	}

	public void setResourceClass (String clazz) {
		resClass = clazz;
	}
} 
 
