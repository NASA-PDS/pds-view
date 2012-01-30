// Copyright 2009, by the California Institute of Technology.
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
// $Id$ 


package gov.nasa.pds.citool.diff;

import java.util.List;

public class DiffRecord {
	private String info;
	private List<String> fromSource;
	private List<String> fromTarget;
	
	public DiffRecord(String info, List<String> fromSource, List<String> fromTarget) {
		this.info = info;
		this.fromSource = fromSource;
		this.fromTarget = fromTarget;
	}
	
	public String getInfo() {return info;}
	
	public List<String> getFromSource() {return fromSource;}
	
	public List<String> getFromTarget() {return fromTarget;}
}

