package gov.nasa.pds.tracking.tracking;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import gov.nasa.pds.tracking.tracking.db.ArchiveStatus;
import gov.nasa.pds.tracking.tracking.db.CertificationStatus;
import gov.nasa.pds.tracking.tracking.db.Delivery;
import gov.nasa.pds.tracking.tracking.db.Doi;
import gov.nasa.pds.tracking.tracking.db.NssdcaStatus;
import gov.nasa.pds.tracking.tracking.db.Product;
import gov.nasa.pds.tracking.tracking.db.Reference;
import gov.nasa.pds.tracking.tracking.db.Releases;
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
				logger.info("references " + count + ":\n " 
						+ r.getLog_identifier() + " : " 
						+ r.getReference() + " : "
						+ r.getTitle() + " : "
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

			// ************************************** 9.2 Delivery Queries *************************
			/**********************************************************
			 User Query – Query the user table for a list of users.
			 Input: N/A
			 Output: electronic_mail_address, name
			***********************************************************/
			test.getUsers();
			
			/**********************************************************
			User Role Query – Query the user and role tables for a list of roles for a given user.
			Input: electronic_mail_address (required)
			Output: electronic_mail_address, name, reference
			***********************************************************/
			test.getUserRoles("sean.hardman@jpl.nasa.gov");			
			//test.getUserRoles("rafael.alanis@jpl.nasa.gov");
			
			/**********************************************************
			 Delivery Query – Query the delivery table for a list of deliveries for a given product.
			Input: logical_identifier (required), version_id (required)
			Output: delivery_identifier, logical_identifier, version_id, name, start_date_time, stop_date_time, source, target, due_date
			***********************************************************/
			test.getProductDeliveries("urn:nasa:pds:context_pds3:data_set:data_set.juno-j-rss-1-jugr-v1.0", "1.0");
			
			/**********************************************************
			 Delivery Status Query – Query the submission_status table for the status progression of a delivery for a given product.
			Input: delivery_identifier (required)
			Output: delivery_identifier, submission_date_time, status_date_time, status, electronic_mail_address, comment
			***********************************************************/
			//test.getDeliveryStatus("31");
			
			/**********************************************************
			 Product Query – Query the product table for a list of products by type.
			Input: type (optional)
			Output: logical_identifier, version_id, title, type, alternate_id
			***********************************************************/
			//test.getProducts(null);
			//test.getProducts("PDS3-Data-Set");
			
			/**********************************************************
			 Product Reference Query Query – Query the reference table for a list of product references.
			Input: logical_identifier (required)
			Output: logical_identifier, reference, title, type
			***********************************************************/
			test.getProductReferences("urn:nasa:pds:context_pds3:data_set:data_set.juno-j-rss-1-jugr-v1.0");
			
			/**********************************************************
			 Product Role Query– Query the product, reference, role and user tables for a list of users with the Investigation role for a product.			
			Input: logical_identifier (required), type (required)
			Output: electronic_mail_address, name
			***********************************************************/			
			test.getProductRoleUsers("urn:nasa:pds:context_pds3:data_set:data_set.juno-j-rss-1-jugr-v1.0", "Investigation");
			
			
			// ************************************ 9.1 Delivery Inserts/Updates **************************
			/**********************************************************
			Delivery Insert – Insert a delivery record into the delivery table for a given product.
			Input: logical_identifier (required), version_id (required), name (required), start_date_time (required), stop_date_time (required), source (required), target (required), due_date (required)
			Output: delivery_identifier
			***********************************************************/
			//int delIdentifier = test.InsertDelivery("urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-3-cdr-bstfull-v1.0", "1.0", "dany test", "2016-10-20T00:00:00", "2017-02-02T23:59:59", "JSOC", "PPI Node", "2017-08-31");
			//logger.info("delivery identifier of the inserted delivery: " + delIdentifier);
			
			/**********************************************************
			Delivery Update – Update a delivery record in the delivery table for a given product.
			Input: delivery_identifier (required), name (required), start_date_time (required), stop_date_time (required), source (required), target (required), due_date (required)
			Output: N/A
			***********************************************************/
			//test.updateDelivery("Dan Yu test", "2016-10-20T00:00:00", "2017-02-02T23:59:59", "JSOC", "PPI Node", "2017-09-31", "32");
			/**********************************************************
			Product Insert – Insert a product record into the product table for a given product.
			Input: logical_identifier (required), version_id (required), title (required), type (required), alternate_id (optional)
			***********************************************************/
			//test.InsertProduct("urn:nasa:pds:context_pds3:data_set:data_set.juno-j-jiram-3-rdr-v1.0", "test 1.0", "dany_JUNO-J-JIRAM-3-RDR-V1.0", "PDS3-Data-Set", "JUNO-J-JIRAM-3-RDR-V1.0");
			//test.InsertProduct("urn:nasa:pds:context_pds3:data_set:data_set.juno-j-jiram-3-rdr-v1.0", "test 2.0", "dany_noAltId_JUNO-J-JIRAM-3-RDR-V1.0", "PDS3-Data-Set", null);
			
			/**********************************************************
			Product Update –Update a product record in the product table for a given product.
			Input:logical_identifier (required), version_id (required), title (required), type (required), alternate_id (optional)
			Output:N/A
			***********************************************************/
			//test.updateProduct("urn:nasa:pds:context_pds3:data_set:data_set.juno-j-jiram-3-rdr-v1.0", "test 1.0", "danyUpdate_JUNO-J-JIRAM-3-RDR-V1.0", "PDS3-Data-Set", "JUNO-J-JIRAM-3-RDR-V1.0");
			//test.updateProduct("urn:nasa:pds:context_pds3:data_set:data_set.juno-j-jiram-3-rdr-v1.0", "test 2.0", "danyUpdate_noAltId_JUNO-J-JIRAM-3-RDR-V1.0", "PDS3-Data-Set", null);
			
			/**********************************************************
			Reference Insert – Insert a reference record into the reference table for a given product.
			Input: logical_identifier (required), reference (required), title (required), type (required)
			***********************************************************/
			test.insertReference("urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-2-edr-v1.0", "DanY_Atmospheres", "Juno", "danyu test");

			/**********************************************************
			Reference Update – Update a reference record into the reference table for a given product.
			Input: logical_identifier (required), reference (required), title (required), type (required)
			Output: N/A
			***********************************************************/
			test.updateReference("urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-2-edr-v1.0", "DanY_Atmospheres", "Juno", "danyu update test");
			
			/**********************************************************
			Role Insert – Insert a role record into the role table for a given user.
			Input: electronic_mail_address (required), reference (required)
			***********************************************************/
			test.insertRole("danyu@jpl.nasa.gov", "DanY_Atmospheres");
			
			/**********************************************************
			Submission Insert – Insert a submission record into the submission and submission_status tables for a given delivery.
			Input: delivery_identifier (required), status_date_time (required), status (required), electronic_mail_address (required), comment (optional)
			Output: N/A
			***********************************************************/			
			/*test.insertSubmission(31, "2017-08-17T11:23:59");
			test.insertSubmissionStatus(31, "2017-08-17T11:23:59", "2017-08-17T11:24:59", "Test", "danyu@jpl.nasa.gov", "test Dan Yu");
			test.insertSubmission(32, "2017-08-17T11:25:59");
			test.insertSubmissionStatus(32, "2017-08-17T11:25:59", "2017-08-17T11:26:59", "Test", "danyu@jpl.nasa.gov", null);*/
			
			/**********************************************************
			Submission Update – Update a submission record in the submission_status table for a given delivery.
			Input: delivery_identifier (required), submission_date_time (required), status_date_time (required), status (required), electronic_mail_address (required), comment (optional)
			Output: N/A
			***********************************************************/
			//test.updateSubmissionStatus(31, "2017-08-17T11:23:59", "2017-08-18T11:24:59", "Test update", "danyu@jpl.nasa.gov", "update test Dan Yu");
			//test.updateSubmissionStatus(32, "2017-08-17T11:25:59", "2017-08-18T11:26:59", "Test update", "danyu@jpl.nasa.gov", null);
			
			/**********************************************************
			User Insert – Insert a user record into the user table for a given user.
			Input: electronic_mail_address (required), name (required)
			Output: N/A
			***********************************************************/
			//test.insertUser("danyu@jpl.nasa.gov", "Da Yu");
			
			/**********************************************************
			User Update – Update a user record into the user table for a given user.
			Input: electronic_mail_address (required), name (required)
			Output: N/A
			***********************************************************/
			//test.updateUser("danyu@jpl.nasa.gov", "Dan Yu");
			
			
			// ************************************ 9.3	Status Inserts/Updates **************************
			/**********************************************************
			Archive Status Insert – Insert an archive status record into the archive_status table for a given product.
			Input: logical_identifier (required), version_id (required), status (required), electronic_mail_address (required), comment (optional)
			Output: N/A
			***********************************************************/
			//ArchiveStatus as = new ArchiveStatus();
			//as.insertArchiveStatus("urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-2-edr-v1.0", "1.0", "2017-09-21T12:24:59", "Test", "danyu@jpl.nasa.gov", "insert latest test");
			//as.insertArchiveStatus("logical_identifier", "version", "date", "status", "mail", null);
			
			/**********************************************************			
			Certification Status Insert – Insert a certification status record into the certification_status table for a given product.
			Input: logical_identifier (required), version_id (required), status (required), electronic_mail_address (required), comment (optional)
			Output: N/A
			***********************************************************/
			/*CertificationStatus cs = new CertificationStatus();
			cs.insertCertificationStatus("urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-2-edr-v1.0", "1.0", "2017-09-21T11:24:59", "Test", "danyu@jpl.nasa.gov", "insert CS test");
			cs.insertCertificationStatus("urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-2-edr-v1.0", "1.0", "2017-09-21T12:24:59", "Test", "danyu@jpl.nasa.gov", "insert latest CS test");
			cs.insertCertificationStatus("logical_identifier", "version", "date", "status", "mail", null);*/
			
			/**********************************************************			
			DOI Insert – Insert a DOI status record into the doi table for a given product.
			Input: logical_identifier (required), version_id (required), doi (required), registration_date (required), site_url (required), electronic_mail_address (required), comment (optional)
			Output: N/A
			***********************************************************/
			/*Doi doi = new Doi();
			doi.insertDOI("logical_identifier", "ver", "doi", "date", "url", "email", "comment");
			doi.insertDOI("logical_identifier", "ver", "doi", "date", "url", "email", null);*/
			
			/**********************************************************
			DOI Update – Update a DOI status record (site_url) in the doi table for a given product.
			Input: logical_identifier (required), version_id (required), site_url (required), electronic_mail_address (required), comment (optional)
			Output: N/A
			***********************************************************/
			/*doi.update("logical_identifier", "ver", "url", "electronic_mail_address", "comment");
			doi.update("logical_identifier", "ver", "url", "electronic_mail_address", null);*/
			
			/**********************************************************
			NSSDCA Status Insert – Insert a NSSDCA status record into the nssdca_status table for a given product.
			Input: logical_identifier (required), version_id (required), nssdca_identifier (required), electronic_mail_address (required), comment (optional)
			Output: N/A
			***********************************************************/
			/*NssdcaStatus ns = new NssdcaStatus();
			ns.insertNssdcaStatus("logical_identifier", "ver", "date", "nssdca_identifier", "mail", "comment");
			ns.insertNssdcaStatus("logical_identifier", "ver", "date", "nssdca_identifier", "mail", null);*/
			
			/**********************************************************
			Release Insert – Insert a release record into the releases table for a given product.
			Input: logical_identifier (required), version_id (required), release_date_time (required), name (required), description (required), electronic_mail_address (required), comment (optional)
			Output: N/A
			***********************************************************/
			/*Releases rl = new Releases();
			rl.insesrtReleases("logical_identifier", "ver", "date", "name", "descrip", "email", "comment");
			rl.insesrtReleases("logical_identifier", "ver", "date", "name", "descrip", "email", null);*/
			
			// ************************************ 9.4	Status Queries **************************
			/**********************************************************
			Archive Status Query – Query the archive_status table for the latest archive status of a given product.
			Input: logical_identifier (required), version_id (required)
			Output: status_date_time, status, electronic_mail_address, comment
			***********************************************************/
			ArchiveStatus latestAS = new ArchiveStatus();
			latestAS = latestAS.getLatestArchiveStatus("urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-2-edr-v1.0", "1.0");
			logger.info("Archive Status: " + latestAS.getStatus());
			logger.info("Email: " + latestAS.getEmail());
			logger.info("Comment: " + latestAS.getComment());
			logger.info("Date: " + latestAS.getDate());
			
			/**********************************************************
			Archive Status List Query – Query the archive_status table for the archive status progression of a given product.
			Input: logical_identifier (required), version_id (required)
			Output: status_date_time, status, electronic_mail_address, comment
			***********************************************************/
			List<ArchiveStatus> asList = new ArrayList<ArchiveStatus>();
			ArchiveStatus aStatus = new ArchiveStatus();
			asList = aStatus.getArchiveStatusList("urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-2-edr-v1.0", "1.0");
			
			Iterator<ArchiveStatus> itrAS = asList.iterator();
			int countAS = 1;
			while (itrAS.hasNext()) {
				ArchiveStatus s = itrAS.next();
				logger.info("Archive Status " + countAS + ":\n " + s.getStatus() + " : " + s.getDate()
				 + " : " + s.getEmail() + " : " + s.getComment());
				countAS++;
			}
			
			/**********************************************************
			Certification Status Query – Query the certification_status table for the latest certification status of a given product.
			Input: logical_identifier (required), version_id (required)
			Output: status_date_time, status, electronic_mail_address, comment
			***********************************************************/
			CertificationStatus latestCS = new CertificationStatus();
			latestCS = latestCS.getLatestCertificationStatus("urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-2-edr-v1.0", "1.0");
			logger.info("Certification Status: " + latestCS.getStatus());
			logger.info("Email: " + latestCS.getEmail());
			logger.info("Comment: " + latestAS.getComment());
			logger.info("Date: " + latestCS.getDate());
			
			/**********************************************************
			Certification Status List Query – Query the certification_status table for the certification status progression of a given product.
			Input: logical_identifier (required), version_id (required)
			Output: status_date_time, status, electronic_mail_address, comment
			***********************************************************/
			List<CertificationStatus> csList;
			
			csList = (new CertificationStatus()).getCertificationStatusList("urn:nasa:pds:context_pds3:data_set:data_set.jno-e-j-ss-wav-2-edr-v1.0", "1.0");
			
			Iterator<CertificationStatus> itrCS = csList.iterator();
			int countCS = 1;
			while (itrCS.hasNext()) {
				CertificationStatus s = itrCS.next();
				logger.info("Certification Status " + countCS + ":\n " + s.getStatus() + " : " + s.getDate()
				 + " : " + s.getEmail() + " : " + s.getComment());
				countCS++;
			}
			
			/**********************************************************
			DOI Query – Query the doi table for the DOI and associated information of a given product.
			Input: logical_identifier (required), version_id (required)
			Output: doi, registration_date, site_url, electronic_mail_address, comment
			***********************************************************/
			/*List<Doi> doiList;
			doiList = (new Doi()).getDOIList("logical_identifier", "ver");
			
			Iterator<Doi> itrDOI = doiList.iterator();
			int countDOI = 1;
			while (itrDOI.hasNext()) {
				Doi d = itrDOI.next();
				logger.info("DOI " + countDOI + ":\n " + d.getDoi() + " : " + d.getDate()
				 + " : " + d.getUrl() + " : " + d.getEmail()+ " : " + d.getComment());
				countDOI++;
			}*/
			
			/**********************************************************
			NSSDCA Query – Query the nssdca table for the NSSDCA information of a given product.
			Input: logical_identifier (required), version_id (required)
			Output: status_date_time, nssdca_identifier, electronic_mail_address, comment
			***********************************************************/
			/*List<NssdcaStatus> nssdcaList;
			nssdcaList = (new NssdcaStatus()).getNssdcaStatusList("logical_identifier", "ver");
			
			Iterator<NssdcaStatus> itrNssdca = nssdcaList.iterator();
			int countNssdca = 1;
			while (itrNssdca.hasNext()) {
				NssdcaStatus n = itrNssdca.next();
				logger.info("Nssdca Status " + countNssdca + ":\n " + n.getDate() + " : " + n.getNssdca()
				  + " : " + n.getEmail()+ " : " + n.getComment());
				countNssdca++;
			}*/
			
			/**********************************************************
			Release Query – Query the releases table for the latest release of a given product.
			Input: logical_identifier (required), version_id (required)
			Output: release_date_time, name, description, electronic_mail_address, comment
			***********************************************************/
			/*Releases rel;
			rel = (new Releases()).getLatestReleases("logical_identifier", "ver");
			logger.info("Date: " + rel.getDate());
			logger.info("Name: " + rel.getName());
			logger.info("Description: " + rel.getDescription());
			logger.info("Email: " + rel.getEmail());
			logger.info("Comment: " + rel.getComment());*/
			
			/**********************************************************
			Release List Query – Query the releases table for the release progression of a given product.
			Input: logical_identifier (required), version_id (required)
			Output: release_date_time, name, description, electronic_mail_address, comment
			***********************************************************/
			/*List<Releases> relList;
			relList = (new Releases()).getReleasesList("logical_identifier", "ver");
			
			Iterator<Releases> itrRel = relList.iterator();
			int countRel = 1;
			while (itrRel.hasNext()) {
				Releases r = itrRel.next();
				logger.info("Releases " + countRel + ":\n " + r.getDate() + " : " + r.getName()
				 + " : " + r.getDescription() + " : " + r.getEmail() + " : " + r.getComment());
				countRel++;
			}*/

			
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

	private void updateReference(String logicalIdentifier, String reference, String title, String type) {
		Reference ref;
		try {
			ref = new Reference();
			ref.updateReference(logicalIdentifier, reference, title, type);
		} catch (ClassNotFoundException | SQLException e) {
			
			e.printStackTrace();
		}
	}

	private void insertReference(String logicalIdentifier, String reference, String title, String type) {
		Reference ref;
		try {
			ref = new Reference();
			ref.insertReference(logicalIdentifier, reference, title, type);
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
