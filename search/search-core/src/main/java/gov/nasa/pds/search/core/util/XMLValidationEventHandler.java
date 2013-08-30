/**
 * 
 */
package gov.nasa.pds.search.core.util;

import gov.nasa.pds.search.core.logging.ToolsLevel;
import gov.nasa.pds.search.core.logging.ToolsLogRecord;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

/**
 * @author jpadams
 *
 */
public class XMLValidationEventHandler implements ValidationEventHandler {
    private static Logger log = Logger.getLogger(
            XMLValidationEventHandler.class.getName());

    public boolean handleEvent(ValidationEvent event) {
        Level level = ToolsLevel.ALL;
        if(event.getSeverity() == ValidationEvent.ERROR
                || event.getSeverity() == ValidationEvent.FATAL_ERROR) {
            level = ToolsLevel.SEVERE;
        } else if(event.getSeverity() == ValidationEvent.WARNING) {
            level = ToolsLevel.WARNING;
        }
        log.log(new ToolsLogRecord(level, event.getMessage(),
                event.getLocator().getURL().toString(),
                event.getLocator().getLineNumber()));

        return false;
    }
}
