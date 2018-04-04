/**
 * Copyright 2010-2017, by the California Institute of Technology.
 */
package gov.nasa.pds.tracking.tracking.jsoninterfaces;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import gov.nasa.pds.tracking.tracking.db.Reference;

/**
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */
@Path("json/references")
public class JSONBasedReferences {

	public static Logger logger = Logger.getLogger(JSONBasedReferences.class);

	@GET
    @Produces("application/json")
    public Response defaultReferences() throws JSONException {
 
        JSONObject jsonReferences = new JSONObject();
        JSONObject jsonInstReference = new JSONObject();
        JSONObject jsonInveReference = new JSONObject();
        JSONObject jsonNodeReference = new JSONObject();
        
        JSONObject jsonReference = new JSONObject();
        
        Reference ref;
		try {
			//Instrument Reference
			ref = new Reference();
			List<Reference> refInsts = ref.getProductAllReferences(Reference.INST_TABLENAME);
						
			logger.info("number of Instrument Reference: "  + refInsts.size());
			Iterator<Reference> itr = refInsts.iterator();
			int countInst = 1;
			
			while(itr.hasNext()) {
				Reference r = itr.next();
		        logger.debug("instrument Reference " + countInst + ":\n " + r.getLog_identifier() + " : " + r.getReference());
		         
		        jsonReference = new JSONObject();
		        jsonReference.put(Reference.LOG_IDENTIFIERCOLUMN, r.getLog_identifier());
		        jsonReference.put(Reference.REFERENCECOLUMN, r.getReference());
		        jsonReference.put(Reference.TITLECOLUMN, r.getTitle());
		        jsonInstReference.append("instrument", jsonReference);
		        countInst++;
		    }
			
			
	         //investigation_reference
			ref = new Reference();
			List<Reference> refInves = ref.getProductAllReferences(Reference.INVES_TABLENAME);
						
			logger.info("number of Investigation Reference: "  + refInves.size());
			itr = refInves.iterator();
			int countInve = 1;
			
			while(itr.hasNext()) {
				Reference r = itr.next();
		        logger.debug("instrument Reference " + countInve + ":\n " + r.getLog_identifier() + " : " + r.getReference());
		         
		        jsonReference = new JSONObject();		         
		        jsonReference.put(Reference.LOG_IDENTIFIERCOLUMN, r.getLog_identifier());
		        jsonReference.put(Reference.REFERENCECOLUMN, r.getReference());
		        jsonReference.put(Reference.TITLECOLUMN, r.getTitle());
		        jsonInveReference.append("investigation", jsonReference);
		        countInve++;
		    }
	         
	         //node_reference
			ref = new Reference();
			List<Reference> refNodes = ref.getProductAllReferences(Reference.NODE_TABLENAME);
						
			logger.info("number of Node Reference: "  + refNodes.size());
			itr = refNodes.iterator();
			int countNode = 1;
			
			while(itr.hasNext()) {
				Reference r = itr.next();
		        logger.debug("node Reference " + countNode + ":\n " + r.getLog_identifier() + " : " + r.getReference());
		         
		        jsonReference = new JSONObject();		         
		        jsonReference.put(Reference.LOG_IDENTIFIERCOLUMN, r.getLog_identifier());
		        jsonReference.put(Reference.REFERENCECOLUMN, r.getReference());
		        jsonReference.put(Reference.TITLECOLUMN, r.getTitle());
		        jsonNodeReference.append("Node", jsonReference);
		        countNode++;
		    }

	         jsonReferences.append("References", jsonInstReference);
	         jsonReferences.append("References", jsonInveReference);
	         jsonReferences.append("References", jsonNodeReference);
			
		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		}
        String result = "" + jsonReferences.toString(4);
        return Response.status(200).entity(result).build();
    }

	@Path("{id : (.+)?}/{refType : (.+)?}")
    @GET
    @Produces("application/json")
    public Response getReferences(@PathParam("id") String id, @PathParam("refType") String refTableName)  throws JSONException {
		
		JSONObject jsonRefs = new JSONObject();
        
        JSONObject jsonRef = new JSONObject();
        
        Reference ref;
		try {
			ref = new Reference();
			List<Reference> refs = ref.getProductReferences(id, refTableName);
			logger.info("number of refTableName: "  + refs.size());
			Iterator<Reference> itr = refs.iterator();
			int count = 1;
			
			while(itr.hasNext()) {
				Reference r = itr.next();
				logger.debug(refTableName + " " + count + ":\n " + r.getLog_identifier() + " : " + r.getReference());
		         
				jsonRef = new JSONObject();		         
				jsonRef.put(Reference.LOG_IDENTIFIERCOLUMN, r.getLog_identifier());
				jsonRef.put(Reference.REFERENCECOLUMN, r.getReference());
				jsonRef.put(Reference.TITLECOLUMN, r.getTitle());

				jsonRefs.append(refTableName, jsonRef);
		        count++;
		    }
			
		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		}
        String result = "" + jsonRefs.toString(4);
        return Response.status(200).entity(result).build();
	}
}
