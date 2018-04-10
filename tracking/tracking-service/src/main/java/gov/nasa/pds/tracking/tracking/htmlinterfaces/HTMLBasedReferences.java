package gov.nasa.pds.tracking.tracking.htmlinterfaces;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.log4j.Logger;
import gov.nasa.pds.tracking.tracking.db.Reference;
import gov.nasa.pds.tracking.tracking.utils.HtmlConstants;
 
@Path("html/references")
public class HTMLBasedReferences {
	
	public static Logger logger = Logger.getLogger(HTMLBasedReferences.class);
	
	private final String PAGESTART_STRONE = "<h1>Tracking Service</h1><h2>References</h2>";
	private final String PAGESTART_STRTWO = "<div>" +
			  "<table border=\"1\" style=\"width: 90%;border-spacing: 0; font:normal; font-size: 10\" >" +
			  "<tr align=\"center\">" +
			  "<td width=\"40%\"><b>" + Reference.LOG_IDENTIFIERCOLUMN + "</b></td>"+
			  "<td width=\"30%\"><b>" + Reference.REFERENCECOLUMN + "</b></td>" +
			  "<td width=\"30%\"><b>" + Reference.TITLECOLUMN + "</b></td>" +
			  "</tr>";
	
	
    @GET
    @Produces("text/html")
    public String defaultUsers() {

    	StringBuilder sb = new StringBuilder();
    	sb.append(PAGESTART_STRONE);
    	

		Reference ref;
		try {
			//Instrument Reference

			sb.append("<h3>" + Reference.INST_TABLENAME + "</h3>" + PAGESTART_STRTWO);
			ref = new Reference();
			List<Reference> refInsts = ref.getProductAllReferences(Reference.INST_TABLENAME);
						
			logger.info("number of Instrument Reference: "  + refInsts.size());
			Iterator<Reference> itr = refInsts.iterator();
			int countInst = 1;
			
			while(itr.hasNext()) {
				Reference r = itr.next();
		        logger.debug("instrument Reference " + countInst + ":\n " + r.getLog_identifier() + " : " + r.getReference());
		         
		        sb.append("<tr>" +
		    			  "<td>" + r.getLog_identifier() + "</td>" +	  					 
			              "<td>" + r.getReference() + "</td>" +
			              "<td>" + r.getTitle() + "</td>" +
			              "</tr>");
		    }
			sb.append("</table></div>");
			
			
			//investigation_reference

			sb.append("<h3>" + Reference.INVES_TABLENAME + "</h3>" + PAGESTART_STRTWO);
			List<Reference> refInves = ref.getProductAllReferences(Reference.INVES_TABLENAME);
			
			logger.info("number of Investigation Reference: "  + refInves.size());
			itr = refInves.iterator();
			int countInve = 1;
			
			while(itr.hasNext()) {
				Reference r = itr.next();
		        logger.debug("investigation Reference " + countInve + ":\n " + r.getLog_identifier() + " : " + r.getReference());
		         
		        sb.append("<tr>" +
		    			  "<td>" + r.getLog_identifier() + "</td>" +	  					 
			              "<td>" + r.getReference() + "</td>" +
			              "<td>" + r.getTitle() + "</td>" +
			              "</tr>");
		    }
			sb.append("</table></div>");
			
			//Node Reference

			sb.append("<h3>" + Reference.NODE_TABLENAME + "</h3>" + PAGESTART_STRTWO);
			ref = new Reference();
			List<Reference> refNodes = ref.getProductAllReferences(Reference.NODE_TABLENAME);
						
			logger.info("number of Node Reference: "  + refNodes.size());
			itr = refNodes.iterator();
			int countNode = 1;
			
			while(itr.hasNext()) {
				Reference r = itr.next();
		        logger.debug("node Reference " + countNode + ":\n " + r.getLog_identifier() + " : " + r.getReference());
		         
		        sb.append("<tr>" +
		    			  "<td>" + r.getLog_identifier() + "</td>" +	  					 
			              "<td>" + r.getReference() + "</td>" +
			              "<td>" + r.getTitle() + "</td>" +
			              "</tr>");
		    }
			sb.append("</table></div>");

			
		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		}
 
        return HtmlConstants.PAGE_BEGIN + sb.toString() + HtmlConstants.PAGE_END;
    }
    
    @Path("{id : (.+)?}/{refType : (.+)?}")
    @GET
    @Produces("text/html")
    public String userProductRole(@PathParam("id") String id, @PathParam("refType") String refType){
    	StringBuilder sb = new StringBuilder();
    	sb.append(PAGESTART_STRONE);
 
		Reference ref;
		try {

			sb.append("<h3>" + refType + "</h3>" + PAGESTART_STRTWO);
			ref = new Reference();
			List<Reference> refs = ref.getProductReferences(id, refType);;
						
			logger.info("number of " + refType + ": "  + refs.size());
			Iterator<Reference> itr = refs.iterator();
			int count = 1;
			
			while(itr.hasNext()) {
				Reference r = itr.next();
		        logger.debug(refType + " " + count + ":\n " + r.getLog_identifier() + " : " + r.getReference());
		         
		        sb.append("<tr>" +
		    			  "<td>" + r.getLog_identifier() + "</td>" +	  					 
			              "<td>" + r.getReference() + "</td>" +
			              "<td>" + r.getTitle() + "</td>" +
			              "</tr>");
		    }			
		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		}    			        
        sb.append("</table></div>");
 
        return HtmlConstants.PAGE_BEGIN + sb.toString() + HtmlConstants.PAGE_END;
    }
}
