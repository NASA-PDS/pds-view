package gov.nasa.pds.web.ui.utils;

import gov.nasa.arc.pds.tools.util.FileUtils;
import gov.nasa.arc.pds.tools.util.StrUtils;
import gov.nasa.pds.web.BaseProcess;
import gov.nasa.pds.web.ui.actions.dataManagement.ValidationProcess;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

public class HTTPUtils {
	
	private static Map<String, BaseProcess> processMap = Collections.synchronizedMap(new HashMap<String, BaseProcess>());
	private static Map<String, Set<String>> resultIDsForSession = Collections.synchronizedMap(new HashMap<String, Set<String>>());

	// TODO: make methods for working with url strings including manipulating
	// get params and doing encoding

	public static HttpServletRequest getRequest() {
		return ServletActionContext.getRequest();
	}

	public static ServletContext getServletContext() {
		return ServletActionContext.getServletContext();
	}

	public static HttpSession getSession() {
		return getRequest().getSession();
	}

	public static String getSessionId() {
		return getSession().getId();
	}

	public static String getSessionId(HttpSession session) {
		return session.getId();
	}

	public static String getRootPath() {
		final ServletContext servletContext = getServletContext();
		return getRootPath(servletContext);
	}

	public static String getRootPath(final ServletContext servletContext) {
		final String rootPath = servletContext.getRealPath("/"); //$NON-NLS-1$
		return rootPath;
	}
	
	/**
	 * Gets a temporary file within a parent directory named by the process ID.
	 * 
	 * @param procID the process ID
	 * @param name the name of the temporary file
	 * @return a temporary file with the given name
	 */
	public static File getProcessTempFile(String procID, String name) {
		File processTempDir = new File(getTempDir(), procID);
		processTempDir.mkdir();
		return new File(processTempDir, name);
	}

	// get reasonable global temp dir
	public static File getTempDir() {
		final ServletContext servletContext = getServletContext();
		return getTempDir(servletContext);
	}

	public static File getTempDir(final ServletContext servletContext) {
		// get path to temp directory, creating folders as needed
		final String rootPath = getRootPath(servletContext);
		final File temp = new File(rootPath, "temp"); //$NON-NLS-1$
		// make sure file exists
		temp.mkdirs();
		return temp;
	}

	// get reasonable temp directory for user's session
	public static File getSessionTempDir() {
		return getSessionTempDir(getSession());
	}

	public static File getSessionTempDir(HttpSession session) {
		final File temp = getTempDir(session.getServletContext());

		// get session id
		final String sessionId = getSessionId(session);

		final File userTempDir = new File(temp, sessionId);
		userTempDir.mkdir();
		return userTempDir;
	}

	public static void cleanSessionTempDir() {
		final File tempDir = getSessionTempDir();
		FileUtils.forceDeleteAll(tempDir);
	}

	@SuppressWarnings("unchecked")
	public static void storeProcess(final BaseProcess process) {
//		final HttpSession session = getSession();
//		System.out.println("In HTTPUtils.storeProcess(), session is " + session.getId());
//		Map<String, BaseProcess> processMap = (Map<String, BaseProcess>) session
//				.getAttribute(BaseAction.PROCESS_KEY);
//		if (processMap == null) {
//			processMap = new HashMap<String, BaseProcess>();
//			session.setAttribute(BaseAction.PROCESS_KEY, processMap);
//		}
		System.out.println("Storing process with ID '" + process.getID() + "'");
		processMap.put(process.getID(), process);
//		if (session.getAttribute(BaseAction.PROCESS_KEY) == null) {
//			System.out.println("Process map is null in HTTPUtils.storeProcess()");
//		}
	}

	@SuppressWarnings("unchecked")
	public static BaseProcess getProcess(final String processId) {
//		final HttpSession session = getSession();
//		System.out.println("In HTTPUtils.getProcess(), session is " + session.getId());
//		Map<String, BaseProcess> processMap = (Map<String, BaseProcess>) session
//				.getAttribute(BaseAction.PROCESS_KEY);
//		if (processMap == null) {
//			// throw exception?
//			System.out.println("Process map is null in HTTPUtils.getProcess()");
//			return null;
//		}
		System.out.println("Retrieving process with ID '" + processId + "'");
		BaseProcess process = processMap.get(processId);
		if (process!=null && (process instanceof ValidationProcess)) {
			addResultIDToSession(((ValidationProcess) process).getResultID());
		}
		return process;
	}

	public static String getRequestIP() {
		return getRequest() == null ? null : getRequest().getRemoteAddr();
	}

	public static String getFullURL(final HttpServletRequest req) {
		if (req == null) {
			return null;
		}
		String out = ""; //$NON-NLS-1$

		final String query = req.getQueryString();
		final String scheme = req.getScheme();
		final String server = req.getServerName();
		final int port = req.getServerPort();
		final String url = req.getRequestURI();

		out = scheme + "://" + server; //$NON-NLS-1$
		if (port != 80) {
			out += ":" + port; //$NON-NLS-1$
		}
		out += url;
		if (!StrUtils.nullOrEmpty(query)) {
			out += "?" + query; //$NON-NLS-1$
		}
		return out;
	}

	public static String getFullURL() {
		HttpServletRequest req = getRequest();
		return getFullURL(req);
	}
	
	private static void addResultIDToSession(String resultID) {
		Set<String> ids = resultIDsForSession.get(getSessionId());
		if (ids == null) {
			ids = new HashSet<String>();
			resultIDsForSession.put(getSessionId(), ids);
		}
		
		ids.add(resultID);
	}
	
	public static void deleteFilesForSession(String sessionID) {
		Set<String> ids = resultIDsForSession.get(sessionID);
		final File tempDir = HTTPUtils.getTempDir();
//		final File sessionTempDir = HTTPUtils.getSessionTempDir();
		
		for (String id : ids) {
			final File resultFile = new File(tempDir, id);
			if (resultFile.exists()) {
				resultFile.delete();
			}
		}
	}

	
	/* Function: Deletes the directory in pds_web/WebContent/temp
     * 
     * This function was added in response to not been able to delete any temporary tables. The issue was
     * that ServletActionContext.getServletContext(); returns null since "ServletActionContext.getServletContext() 
     * is only legal to call from within a Struts action. The SessionListener (which implements javax.servlet.http.HttpSessionListener), 
     * is called by the J2EE web container, not by Struts, so cannot get the servlet context that way."
     * 
     * So a session listener such as SessionListener.java should call this function rather than deleteFilesForSession(String sessionID)
     * as follows:
     * 
     * HTTPUtils.deleteFilesForSession(event.getSession().getId(),event.getSession().getServletContext());
     */
    public static void deleteFilesForSession(String sessionID, ServletContext servletContext) {
        Set<String> ids = resultIDsForSession.get(sessionID);
        final File tempDir = getTempDir(servletContext);
 
        if(ids != null) {
            for (String id : ids) {
                final File resultFile = new File(tempDir, id);
                if (resultFile.exists()) {
                    resultFile.delete();
                }
            }
        }
    }

	
	
	// get value from process - throw invalid proc

	// set value in process - when do we assign the proc? what happens if we
	// don't have a proc?

	// TODO: clean up a session

	// TODO: move session management functions like adding and removing filters
	// and junk here

}
