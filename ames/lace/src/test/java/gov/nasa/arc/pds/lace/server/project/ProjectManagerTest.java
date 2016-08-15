package gov.nasa.arc.pds.lace.server.project;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import gov.nasa.arc.pds.lace.server.ServerConfiguration;
import gov.nasa.arc.pds.lace.server.project.ProjectManager.UserRegistrationState;
import gov.nasa.arc.pds.lace.shared.project.ProjectItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.inject.Inject;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

@Guice(modules={ProjectConfigModule.class})
public class ProjectManagerTest {

	private static final String PROJECT_ROOT_WITH_USERS = "src/test/resources/project-tests/project-root-with-users";

	/** User ID corresponding to directory "user1-no-projects". */
	private static final String USER1 = "user1";
	private static final String USER1_WRONG_CASE = "UsEr1";

	/** User ID corresponding to directory "user2-with-projects". */
	private static final String USER2 = "user2@nowhere.com";

	/** A user ID that is not authorized to use the application. */
	private static final String USER3_NOT_AUTHORIZED = "user3@nowhere.com";

	/** A user ID for a nonexistent user. */
	private static final String NO_SUCH_USER = "nobody@nowhere.com";

	/** A user ID for a new user to create. */
	private static final String NEW_USER = "newuser@wherever.com";

	@Inject
	private ServerConfiguration serverConfig;

	@Inject
	private ProjectManager manager;

	private File tempDir;

	@BeforeMethod
	public void init() {
		String tempDirName = getClass().getSimpleName() + "-temp";
		tempDir = new File(new File(System.getProperty("java.io.tmpdir")), tempDirName);
		if (tempDir.exists()) {
			recursivelyDelete(tempDir);
		}
		tempDir.mkdir();
		(new File(tempDir, "projects")).mkdir();
		(new File(tempDir, "upload")).mkdir();
	}

	@AfterMethod
	public void cleanup() {
		recursivelyDelete(tempDir);
	}

	private void recursivelyDelete(final File f) {
		if (f.isDirectory()) {
			for (File child : f.listFiles()) {
				recursivelyDelete(child);
			}
		}

		f.delete();
	}

	@Test
	public void testGetUserRoot() throws FileNotFoundException, IOException {
		serverConfig.setDataRoot(new File(PROJECT_ROOT_WITH_USERS));

		assertEquals(manager.getUserRoot(USER1), new File(PROJECT_ROOT_WITH_USERS, "projects/user1-no-projects"));
		assertTrue(manager.isUserAuthorized(USER1));
		assertEquals(manager.getUserRoot(USER1_WRONG_CASE), new File(PROJECT_ROOT_WITH_USERS, "projects/user1-no-projects"));
		assertTrue(manager.isUserAuthorized(USER1_WRONG_CASE));

		assertEquals(manager.getUserRoot(USER2), new File(PROJECT_ROOT_WITH_USERS, "projects/user2-with-projects"));
		assertTrue(manager.isUserAuthorized(USER2));

		assertEquals(manager.getUserRoot(USER3_NOT_AUTHORIZED), new File(PROJECT_ROOT_WITH_USERS, "projects/user3-not-authorized"));
		assertFalse(manager.isUserAuthorized(USER3_NOT_AUTHORIZED));

		assertNull(manager.getUserRoot(NO_SUCH_USER));
	}

	@Test
	public void testGetItemsForUserNoItems() throws FileNotFoundException, IOException {
		serverConfig.setDataRoot(new File(PROJECT_ROOT_WITH_USERS));
		ProjectItem[] items = manager.getProjectItems(USER1, "");
		assertEquals(items.length, 0);
	}

	@Test
	public void testGetItemsForUserWithItems() throws FileNotFoundException, IOException {
		serverConfig.setDataRoot(new File(PROJECT_ROOT_WITH_USERS));
		ProjectItem[] items = manager.getProjectItems(USER2, "");
		assertEquals(items.length, 2);
		assertEquals(items[0].getType(), ProjectItem.Type.LABEL);
		assertEquals(items[1].getType(), ProjectItem.Type.LABEL);
	}

	@Test
	public void testCreateUser() throws FileNotFoundException, IOException {
		serverConfig.setDataRoot(tempDir);
		manager.createUserIfNeccesary(NEW_USER);

		assertFalse(manager.isUserAuthorized(NEW_USER));

		File root = manager.getUserRoot(NEW_USER);
		assertTrue(root.exists() && root.isDirectory());
		ProjectItem[] items = manager.getProjectItems(NEW_USER, "");
		assertEquals(items.length, 0);

		manager.setUserRegistrationState(NEW_USER, UserRegistrationState.APPROVED);
		assertTrue(manager.isUserAuthorized(NEW_USER));
	}

	@Test
	public void testCreateDeleteItem() throws FileNotFoundException, IOException {
		serverConfig.setDataRoot(tempDir);
		manager.createUserIfNeccesary(NEW_USER);
		String location = manager.createNewLabel(NEW_USER, "label1");

		File folder = manager.getItemFolder(NEW_USER, location);
		assertTrue(folder.isDirectory());

		File propertiesFile = new File(folder, "item.properties");
		assertTrue(propertiesFile.isFile());

		manager.deleteProjectItem(NEW_USER, location);
		assertFalse(folder.exists());
	}

	@Test(dataProvider="UserStateTests")
	public void testGetUserState(String userID, UserRegistrationState expectedState) throws FileNotFoundException, IOException {
		serverConfig.setDataRoot(new File(PROJECT_ROOT_WITH_USERS));
		UserRegistrationState actualState = manager.getUserRegistrationState(userID);
		assertEquals(actualState, expectedState);
	}

	@DataProvider(name="UserStateTests")
	private Object[][] getUserStateTests() {
		return new Object[][] {
				// user ID, expected state
				{ "no-such-user@nowhere.com", UserRegistrationState.UNREGISTERED },
				{ "user3@nowhere.com", UserRegistrationState.AWAITING_APPROVAL },
				{ "user4@nowhere.com", UserRegistrationState.APPROVED },
				{ "user5@nowhere.com", UserRegistrationState.DENIED },
		};
	}

	@Test
	public void testItemAttributes() throws FileNotFoundException, IOException {
		serverConfig.setDataRoot(tempDir);
		manager.createUserIfNeccesary(NEW_USER);
		String location = manager.createNewLabel(NEW_USER, "label1");

		assertNull(manager.getProjectItemAttribute(NEW_USER, location, "x"));

		manager.setProjectItemAttribute(NEW_USER, location, "x", "value");
		assertEquals(manager.getProjectItemAttribute(NEW_USER, location, "x"), "value");

		manager.removeProjectItemAttribute(NEW_USER, location, "x");
		assertNull(manager.getProjectItemAttribute(NEW_USER, location, "x"));
	}

}
