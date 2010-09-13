package gov.nasa.pds.harvest.crawler.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.jpl.oodt.cas.crawl.action.CrawlerAction;
import gov.nasa.jpl.oodt.cas.crawl.action.CrawlerActionPhases;
import gov.nasa.jpl.oodt.cas.crawl.structs.exceptions.CrawlerActionException;
import gov.nasa.jpl.oodt.cas.metadata.Metadata;
import gov.nasa.pds.harvest.logging.ToolsLogRecord;

public class LogMissingReqMetadataAction extends CrawlerAction {
    private static Logger log = Logger.getLogger(LogMissingReqMetadataAction.class.getName());
    private List<String> reqMetadata;
    private final String ID = "LogMissingReqMetadataAction";
    private final String DESCRIPTION = "Report missing required metadata.";

    public LogMissingReqMetadataAction(List<String> reqMetadata) {
        super();
        this.reqMetadata = new ArrayList<String>();
        this.reqMetadata.addAll(reqMetadata);
        String []phases = {CrawlerActionPhases.POST_INGEST_FAILURE};
        setPhases(Arrays.asList(phases));
        setId(ID);
        setDescription(DESCRIPTION);
    }

    @Override
    public boolean performAction(File product, Metadata productMetadata)
            throws CrawlerActionException {
        boolean passFlag = true;
        for(String key : reqMetadata) {
            if(!productMetadata.containsKey(key)) {
                log.log(new ToolsLogRecord(Level.SEVERE,
                        "Missing required metadata: " + key,
                        product));
                passFlag = false;
            }
        }
        return passFlag;
    }

}
