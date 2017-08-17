package gov.nasa.pds.tracking.tracking;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import gov.nasa.pds.tracking.tracking.db.Delivery;
import gov.nasa.pds.tracking.tracking.db.Product;
import gov.nasa.pds.tracking.tracking.db.Reference;
import gov.nasa.pds.tracking.tracking.db.Role;
import gov.nasa.pds.tracking.tracking.db.Submission;
import gov.nasa.pds.tracking.tracking.db.SubmissionStatus;
import gov.nasa.pds.tracking.tracking.db.User;;

/**
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */

public class Mytest {

	public static Logger logger = Logger.getLogger(Mytest.class);

	/*
	 *  
	 */
	public Mytest() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void getUsers() {
		// Get all users in the user table
		User user;
		try {
			user = new User();
			List<User> users = user.getUsers();
			logger.info(" ============== number of users: " + users.size() + " =====================");
			Iterator<User> itr = users.iterator();
			int count = 1;
			while (itr.hasNext()) {
				User u = itr.next();
				logger.info("User " + count + " = " + u.getUserEmail() + " : " + u.getUserName());
				count++;
			}

		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		}
	}
	public void getUserRoles(String email) {
		// Get user roles for the user (email)
		User user;
		try {
			user = new User();
			List<User> users = user.getUserRole(email);
			logger.info(" ============== number roles of the user: " + users.size() + " =====================");
			Iterator<User> itr = users.iterator();
			int count = 1;
			while (itr.hasNext()) {
				User u = itr.next();
				logger.info("User " + count + " = " + u.getUserEmail() + " : " + u.getUserName() + " : " + u.getReference());
				count++;
			}

		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		}
	}
	private void getProducts(String type) {
		// Get all Products in the product table
		Product prod;
		try {
			prod = new Product();
			
			List<Product> prods = prod.getProducts(type);
			
			logger.info(" ============== number of products: " + prods.size() + " =====================");
			Iterator<Product> itr = prods.iterator();
			int count = 1;
			while (itr.hasNext()) {
				Product p = itr.next();
				logger.info("Product " + count + ":\n " + p.getIdentifier() + " : " + p.getType() + " : " + p.getTitle());
				count++;
			}

		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		}

	}

	private void getProductDeliveries(String log_identifer, String version) {
		
		Delivery del;
		try {
			del = new Delivery();
			
			List<Delivery> dels = del.getProductDeliveries(log_identifer, version);
			
			logger.info(" ============== number of deliveries for " + log_identifer + ", " + version + ": " + dels.size() + " =====================");
			
			Iterator<Delivery> itr = dels.iterator();
			int count = 1;
			while (itr.hasNext()) {
				Delivery d = itr.next();
				logger.info("Delivery " + count + ":\n " + d.getName() + " : " + d.getLogIdentifier() + " : "
						+ d.getDelIdentifier() + " : " + d.getVersion());
				count++;
			}
		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		}

	}
	private void getDeliveryStatus(String identifier) {
		SubmissionStatus status;
		try {
			status = new SubmissionStatus();
			
			List<SubmissionStatus> statuses = status.getDeliveryStatus(identifier);
			
			logger.info(" ============== number of delivery status for " + identifier + " : " + statuses.size() + " =====================");
			
			Iterator<SubmissionStatus> itr = statuses.iterator();
			int count = 1;
			while (itr.hasNext()) {
				SubmissionStatus s = itr.next();
				logger.info("Delivery status " + count + ":\n " + s.getDel_identifier() + " : " + s.getStatus() + " : "
						+ s.getSubmissionDate() + " : " + s.getEmail());
				count++;
			}
		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		}
		
	}

	private void getProductReferences(String log_identifer) {
		
		Reference ref;
		try {
			ref = new Reference();
			
			List<Reference> refs = ref.getProductReferences(log_identifer);
			
			logger.info(" ============== number of references for " + log_identifer + " : " + refs.size() + " =====================");
			
			Iterator<Reference> itr = refs.iterator();
			int count = 1;
			while (itr.hasNext()) {
				Reference r = itr.next();
				logger.info("references " + count + ":\n " + r.getLog_identifier() + " : " + r.getReference() + " : "
						+ r.getType());
				count++;
			}
		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		}

	}
	
	private void getProductRoleUsers(String log_identifer, String role) {
		
		User user;
		try {
			user = new User();
			
			List<User> users = user.getProductRoleUsers(log_identifer, role);
			
			logger.info(" ============== number of users for " + log_identifer + " and " + role + ": " + users.size() + " =====================");
			
			Iterator<User> itr = users.iterator();
			int count = 1;
			while (itr.hasNext()) {
				User u = itr.next();
				logger.info("User " + count + ":\n " + u.getUserName() + " : " + u.getUserEmail());
				count++;
			}
		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		}

	}

	public static void main(String[] args) {
		try {
			Mytest test = new Mytest();
/*
			// ************************************** Delivery Queries *************************
			 User QueryQuery – Query the user table for a list of users.

			Input: N/A
			Output: electronic_mail_address, name
			SQL:
			SELECT electronic_mail_address, name
			FROM user
			ORDER BY electronic_mail_address

			test.getUsers();
			
			 User Role Query – Query the user and role tables for a list of roles for a given user.

			Input: electronic_mail_address (required)
			Output: electronic_mail_address, name, reference
			SQL:
			SELECT u.electronic_mail_address, u.name, r.reference
			FROM user u, role r
			WHERE u.electronic_mail_address = ‘<electronic_mail_address>’ AND u.electronic_mail_address = r.electronic_mail_address
			ORDER BY reference
 
			test.getUserRoles("sean.hardman@jpl.nasa.gov");
			test.getUserRoles("rafael.alanis@jpl.nasa.gov");
			
			 Delivery Query – Query the delivery table for a list of deliveries for a given product.

			Input: logical_identifier (required), version_id (required)
			Output: delivery_identifier, logical_identifier, version_id, name, start_date_time, stop_date_time, source, target, due_date
			SQL:
			SELECT delivery_identifier, logical_identifier, version_id, name, start_date_time, stop_date_time, source, target, due_date
			FROM delivery
			WHERE logical_identifier = ‘<logical_identifier>’
			AND version_id = ‘<version_id>’
			ORDER BY due_date
 			*/
			test.getProductDeliveries("urn:nasa:pds:context_pds3:data_set:data_set.juno-j-rss-1-jugr-v1.0", "1.0");
			/*
			 Delivery Status Query – Query the submission_status table for the status progression of a delivery for a given product.

			Input: delivery_identifier (required)
			Output: delivery_identifier, submission_date_time, status_date_time, status, electronic_mail_address, comment
			SQL:
			SELECT delivery_identifier, submission_date_time, status_date_time, status, electronic_mail_address, comment
			FROM submission_status
			WHERE delivery_identifier = ‘<delivery_identifier>’
			ORDER BY status_date_time
			
			test.getDeliveryStatus("31");
			
			 Product Query – Query the product table for a list of products by type.

			Input: type (optional)
			Output: logical_identifier, version_id, title, type, alternate_id
			SQL:
			SELECT logical_identifier, version_id, title, type, alternate_id
			FROM product
			WHERE type = ‘<type>’
			ORDER BY title
			
			test.getProducts(null);
			test.getProducts("PDS3-Data-Set");
			
			 Product Reference Query Query – Query the reference table for a list of product references.

			Input: logical_identifier (required)
			Output: logical_identifier, reference, type
			SQL:
			SELECT logical_identifier, reference, type
			FROM reference
			WHERE logical_identifier = ‘<logical_identifier>’
			ORDER BY type
			
			test.getProductReferences("urn:nasa:pds:context_pds3:data_set:data_set.juno-j-rss-1-jugr-v1.0");
			
			 Product Role Query– Query the product, reference, role and user tables for a list of users with the Investigation role for a product.
			
			Input: logical_identifier (required), type (required)
			Output: electronic_mail_address, name
			SQL:
			SELECT u.electronic_mail_address, u.name, re.type
			FROM product p, reference re, role ro, user u
			WHERE p.logical_identifier = ‘<logical_identifier>’
			AND p.logical_identifier = re.logical_identifier
			AND re.type = ‘<type>’
			AND re.reference = ro.reference
			AND ro.electronic_mail_address = u.electronic_mail_address
			ORDER BY electronic_mail_address
			*/
			//test.getProductRoleUsers("urn:nasa:pds:context_pds3:data_set:data_set.juno-j-rss-1-jugr-v1.0", "Investigation");
			
			// ************************************ Delivery Inserts/Updates **************************

			/* Delivery Insert – Insert a delivery record into the delivery table for a given product.

			Input: logical_identifier (required), version_id (required), name (required), start_date_time (required), stop_date_time (required), source (required), target (required), due_date (required)
			Output: delivery_identifier
			SQL:
			INSERT INTO delivery (logical_identifier, version_id, name, start_date_time, stop_date_time, source, target, due_date) VALUES (‘<logical_identifier>’, ‘<version_id>’, ‘<name>’, ‘<start_date_time>’, ‘<stop_date_time>’, ‘<source>’, ‘<target>’, ‘<due_date>’)
			*/
			
			//int delIdentifier = test.InsertDelivery("urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-3-cdr-bstfull-v1.0", "1.0", "dany test", "2016-10-20T00:00:00", "2017-02-02T23:59:59", "JSOC", "PPI Node", "2017-08-31");
			//logger.info("delivery identifier of the inserted delivery: " + delIdentifier);
			
			/* Delivery Update – Update a delivery record in the delivery table for a given product.

			Input: delivery_identifier (required), name (required), start_date_time (required), stop_date_time (required), source (required), target (required), due_date (required)
			Output: N/A
			SQL:
			UPDATE delivery SET name = ‘<name>’, start_date_time = ‘<start_date_time>’, stop_date_time = ‘<stop_date_time>’, source = ‘<source>’, target = ‘<target>’, due_date = ‘<due_date>’
			WHERE delivery_identifier = ‘<delivery_identifier>’
			*/
			
			//test.updateDelivery("Dan Yu test", "2016-10-20T00:00:00", "2017-02-02T23:59:59", "JSOC", "PPI Node", "2017-09-31", "32");
			
			/* Product Insert – Insert a product record into the product table for a given product.

			Input: logical_identifier (required), version_id (required), title (required), type (required), alternate_id (optional)
			Output:N/A
			SQL:
			INSERT INTO product (logical_identifier, version_id, title, type, alternate_id) VALUES (‘<logical_identifier>’, ‘<version_id>’, ‘<title>’, ‘<type>’, ‘<alternate_id>’)
			*/
			
			//test.InsertProduct("urn:nasa:pds:context_pds3:data_set:data_set.juno-j-jiram-3-rdr-v1.0", "test 1.0", "dany_JUNO-J-JIRAM-3-RDR-V1.0", "PDS3-Data-Set", "JUNO-J-JIRAM-3-RDR-V1.0");
			//test.InsertProduct("urn:nasa:pds:context_pds3:data_set:data_set.juno-j-jiram-3-rdr-v1.0", "test 2.0", "dany_noAltId_JUNO-J-JIRAM-3-RDR-V1.0", "PDS3-Data-Set", null);
			
			/* Product Update –Update a product record in the product table for a given product.

			Input:logical_identifier (required), version_id (required), title (required), type (required), alternate_id (optional)
			Output:N/A
			SQL:
			UPDATE product SET title = ‘<title>’, type = ‘<type>’, alternate_id = ‘<alternate_id>’
			WHERE logical_identifier = ‘<logical_identifier>’
			AND version_id = ‘<version_id>’
			*/
			
			//test.updateProduct("urn:nasa:pds:context_pds3:data_set:data_set.juno-j-jiram-3-rdr-v1.0", "test 1.0", "danyUpdate_JUNO-J-JIRAM-3-RDR-V1.0", "PDS3-Data-Set", "JUNO-J-JIRAM-3-RDR-V1.0");
			//test.updateProduct("urn:nasa:pds:context_pds3:data_set:data_set.juno-j-jiram-3-rdr-v1.0", "test 2.0", "danyUpdate_noAltId_JUNO-J-JIRAM-3-RDR-V1.0", "PDS3-Data-Set", null);
			
			/* Reference Insert – Insert a reference record into the reference table for a given product.

			Input: logical_identifier (required), reference (required), type (required)
			Output: N/A
			SQL:
			INSERT INTO reference (logical_identifier, reference, type) VALUES (‘<logical_identifier>’, ‘<reference>’, ‘<type>’)
			*/
			
			//test.insertReference("urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-2-edr-v1.0", "DanY_Atmospheres", "danyu test");
			
			/* Reference Update – Update a reference record into the reference table for a given product.

			Input: logical_identifier (required), reference (required), type (required)
			Output: N/A
			SQL:
			UPDATE reference SET type = ‘<type>’
			WHERE logical_identifier = ‘<logical_identifier>’
			AND reference = ‘<reference>’
			*/
			
			//test.updateReference("urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-2-edr-v1.0", "DanY_Atmospheres", "danyu update test");
			
			/* Role Insert – Insert a role record into the role table for a given user.

			Input: electronic_mail_address (required), reference (required)
			Output: N/A
			SQL:
			INSERT INTO role (electronic_mail_address, reference) VALUES (‘<electronic_mail_address>’, ‘<reference>’)
			*/
			
			//test.insertRole("danyu@jpl.nasa.gov", "DanY_Atmospheres");
			
			/* Submission Insert – Insert a submission record into the submission and submission_status tables for a given delivery.

			Input: delivery_identifier (required), status_date_time (required), status (required), electronic_mail_address (required), comment (optional)
			Output: N/A
			SQL:
			INSERT INTO submission (delivery_identifier, submission_date_time) VALUES (‘<delivery_identifier>’, ‘<current_date_time>’)

			INSERT INTO submission_status (delivery_identifier, submission_date_time, status_date_time, status, electronic_mail_address, comment) VALUES (‘<delivery_identifier>’, ‘<current_date_time from first insert>’, ‘<status_date_time>’, ‘<status>’, ‘<electronic_mail_address>’, ‘<comment>’)
			*/
			//test.insertSubmission(31, "2017-08-17T11:23:59");
			//test.insertSubmissionStatus(31, "2017-08-17T11:23:59", "2017-08-17T11:24:59", "Test", "danyu@jpl.nasa.gov", "test Dan Yu");
			//test.insertSubmission(32, "2017-08-17T11:25:59");
			//test.insertSubmissionStatus(32, "2017-08-17T11:25:59", "2017-08-17T11:26:59", "Test", "danyu@jpl.nasa.gov", null);
			
			/* Submission Update – Update a submission record in the submission_status table for a given delivery.

			Input: delivery_identifier (required), submission_date_time (required), status_date_time (required), status (required), electronic_mail_address (required), comment (optional)
			Output: N/A
			SQL:
			UPDATE submission_status SET status_date_time = ‘<status_date_time>’, status = ‘<status>’, electronic_mail_address = ‘<electronic_mail_address>’, comment = ‘<comment>’
			WHERE delivery_identifier = ‘<delivery_identifier>’
			AND submission_date_time = ‘<submission_date_time>’
			*/
			
			test.updateSubmissionStatus(31, "2017-08-17T11:23:59", "2017-08-18T11:24:59", "Test update", "danyu@jpl.nasa.gov", "update test Dan Yu");
			test.updateSubmissionStatus(32, "2017-08-17T11:25:59", "2017-08-18T11:26:59", "Test update", "danyu@jpl.nasa.gov", null);
			
			/*
			User Insert – Insert a user record into the user table for a given user.

			Input: electronic_mail_address (required), name (required)
			Output: N/A
			SQL"
			INSERT INTO user (electronic_mail_address, name) VALUES (‘<electronic_mail_address>’, ‘<name>’)
			*/
			//test.insertUser("danyu@jpl.nasa.gov", "Da Yu");
			
			/* User Update – Update a user record into the user table for a given user.

			Input: electronic_mail_address (required), name (required)
			Output: N/A
			SQL:
			UPDATE user SET name = ‘<name>’
			WHERE electronic_mail_address = ‘<electronic_mail_address >’
			*/
			//test.updateUser("danyu@jpl.nasa.gov", "Dan Yu");

			
		} catch (Exception e) {
			logger.error(e);
		}

	}

	private void updateUser(String email, String name) {
		User user;
		try {
			user = new User();
			user.updateUser(email, name);
		} catch (ClassNotFoundException | SQLException e) {
			
			e.printStackTrace();
		}
		
	}

	private void insertUser(String email, String name) {
		User user;
		try {
			user = new User();
			user.insertUser(email, name);
		} catch (ClassNotFoundException | SQLException e) {
			
			e.printStackTrace();
		}
		
	}

	private void updateSubmissionStatus(int deliveryIdentifier, String subDateTime, String statusDateTime, String status, String email,
			String comment) {
		SubmissionStatus submS;
		try {
			submS = new SubmissionStatus();
			submS.updateSubmissionStatus(deliveryIdentifier, subDateTime, statusDateTime, status, email, comment);
		} catch (ClassNotFoundException | SQLException e) {
			
			e.printStackTrace();
		}
	}
	
	private void insertSubmissionStatus(int deliveryIdentifier, String subDateTime, String statusDateTime, String status, String email,
			String comment) {
		SubmissionStatus submS;
		try {
			submS = new SubmissionStatus();
			submS.insertSubmissionStatus(deliveryIdentifier, subDateTime, statusDateTime, status, email, comment);
		} catch (ClassNotFoundException | SQLException e) {
			
			e.printStackTrace();
		}
	}

	private void insertSubmission(int deliveryIdentifier, String subDateTime) {
		Submission subm;
		try {
			subm = new Submission();
			subm.insertSubmission(deliveryIdentifier, subDateTime);
		} catch (ClassNotFoundException | SQLException e) {
			
			e.printStackTrace();
		}
		
	}

	private void insertRole(String email, String reference) {
		Role role;
		try {
			role = new Role();
			role.insertRole(email, reference);
		} catch (ClassNotFoundException | SQLException e) {
			
			e.printStackTrace();
		}
		
	}

	private void updateReference(String logicalIdentifier, String reference, String type) {
		Reference ref;
		try {
			ref = new Reference();
			ref.updateReference(logicalIdentifier, reference, type);
		} catch (ClassNotFoundException | SQLException e) {
			
			e.printStackTrace();
		}
	}

	private void insertReference(String logicalIdentifier, String reference, String type) {
		Reference ref;
		try {
			ref = new Reference();
			ref.insertReference(logicalIdentifier, reference, type);
		} catch (ClassNotFoundException | SQLException e) {
			
			e.printStackTrace();
		}
		
	}

	private void updateProduct(String logicalIdentifier, String versionId, String title, String type, String alternateId) {
		Product product;
		try {
			product = new Product();
			product.updateProduct(logicalIdentifier, versionId, title, type, alternateId);
		} catch (ClassNotFoundException | SQLException e) {
			
			e.printStackTrace();
		}
	}

	private void InsertProduct(String logicalIdentifier, String versionId, String title, String type, String alternateId) {
		Product product;
		try {
			product = new Product();
			product.insertProduct(logicalIdentifier, versionId, title, type, alternateId);
		} catch (ClassNotFoundException | SQLException e) {
			
			e.printStackTrace();
		}
		
	}

	private void updateDelivery(String name, String startDateTime, String stopDateTime,
			String source, String target, String dueDate, String DelIdentifier) {
		Delivery del;
		try {
			del = new Delivery();
			del.updateDelivery(name, startDateTime, stopDateTime,
					source, target, dueDate, DelIdentifier);
		} catch (ClassNotFoundException | SQLException e) {
			
			e.printStackTrace();
		}		
	}

	private int InsertDelivery(String logicalIdentifier, String versionId, String name, String startDateTime, String stopDateTime,
			String source, String target, String dueDate) {
		Delivery del;
		int Del_identifier = -1;
		try {
			del = new Delivery();
			Del_identifier = del.insertDelivery(logicalIdentifier, versionId, name, startDateTime, stopDateTime,
					source, target, dueDate);
		} catch (ClassNotFoundException | SQLException e) {
			
			e.printStackTrace();
		}
		return Del_identifier;
	}

}
