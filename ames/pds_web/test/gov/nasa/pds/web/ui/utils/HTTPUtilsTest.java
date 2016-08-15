package gov.nasa.pds.web.ui.utils;

import gov.nasa.pds.web.ui.actions.BaseTestAction;
import gov.nasa.pds.web.ui.actions.misc.Home;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.mock.web.MockHttpServletRequest;

// TODO: not sure what to do with most of these tests beyond making sure methods
// return something not null
public class HTTPUtilsTest extends BaseTestAction {

	private Home home;

	@Override
	protected void clearAction() {
		this.home = null;
	}

	public void init() throws Exception {
		this.home = createAction(Home.class);
		this.home.execute();
	}

	public void testGetRequest() throws Exception {
		init();
		HttpServletRequest req = HTTPUtils.getRequest();
		assertNotNull(req);
		assertEquals("http", req.getScheme()); //$NON-NLS-1$
	}

	public void testGetServletContext() throws Exception {
		init();
		ServletContext cont = HTTPUtils.getServletContext();
		assertNotNull(cont);
	}

	public void testGetSession() throws Exception {
		init();
		HttpSession sess = HTTPUtils.getSession();
		assertNotNull(sess);
	}

	public void testGetSessionId() throws Exception {
		init();
		String sessId = HTTPUtils.getSessionId();
		assertNotNull(sessId);
	}

	public void testGetRootPath() throws Exception {
		init();
		String rootPath = HTTPUtils.getRootPath();
		assertNotNull(rootPath);
		// TODO: way to establish path is correct?
	}

	public void testGetTempDir() throws Exception {
		init();
		File tempDir = HTTPUtils.getTempDir();
		assertNotNull(tempDir);
		// TODO: way to establish path is correct?
	}

	public void testGetSessionTempDir() throws Exception {
		init();
		File sessionTempDir = HTTPUtils.getSessionTempDir();
		assertNotNull(sessionTempDir);
		// TODO: way to establish path is correct?
	}

	public void testCleanSessionTempDir() throws Exception {
		init();
		HTTPUtils.cleanSessionTempDir();
		File tempDir = HTTPUtils.getSessionTempDir();
		File asdf = new File(tempDir, "asdf"); //$NON-NLS-1$
		asdf.mkdir();
		assertEquals(1, tempDir.listFiles().length);
		HTTPUtils.cleanSessionTempDir();
		tempDir = HTTPUtils.getSessionTempDir();
		assertEquals(0, tempDir.listFiles().length);
	}

	@SuppressWarnings("nls")
	public void testGetURL() throws Exception {
		MockHttpServletRequest req = new MockHttpServletRequest("GET",
				"/test.php");
		req.setQueryString("test=test");
		req.setScheme("http");
		req.setServerPort(80);
		req.setServerName("www.holisticmonkey.com");

		assertEquals("http://www.holisticmonkey.com/test.php?test=test",
				HTTPUtils.getFullURL(req));

		req.setServerPort(8080);
		assertEquals("http://www.holisticmonkey.com:8080/test.php?test=test",
				HTTPUtils.getFullURL(req));

		req.setQueryString(null);
		assertEquals("http://www.holisticmonkey.com:8080/test.php", HTTPUtils
				.getFullURL(req));

	}
}
