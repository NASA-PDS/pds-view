/**
 * Copyright 2010-2017, by the California Institute of Technology.
 * 
 * The object class represents Delivery table.
 *  
 */
package gov.nasa.pds.tracking.tracking.db;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.log4j.Logger;

@XmlRootElement(name = "delivery")
/**
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */
public class Delivery implements Serializable  {

		private static final long serialVersionUID = 1L;
	  
		public static Logger logger = Logger.getLogger(Delivery.class);
		
		private String log_identifier = null;
		private int del_identifier = 0;
		private String version = null;
		private String name = null;
		private String start = null;
		private String stop = null;
		private String source = null;
		private String target = null;
		private String dueDate = null;
		
		public Delivery(String logIdentifier, int delIdentifier, String ver, String name, String startTime, String stopTime, String src, String tgt, String dueD){
			
			this.log_identifier = logIdentifier;
			this.del_identifier = delIdentifier;
			this.version = ver;
			this.name = name;
			this.start = startTime;
			this.stop = stopTime;
			this.source = src;
			this.target = tgt;
			this.dueDate = dueD;
		}
		/**
		 * @return the log_identifier
		 */
		public String getLogIdentifier() {
			return log_identifier;
		}

		/**
		 * @param log_identifier, the log_identifier to set
		 */
		public void setLogIdentifier(String log_identifier) {
			this.log_identifier = log_identifier;
		}

		/**
		 * @return the del_identifier
		 */
		public int getDelIdentifier() {
			return del_identifier;
		}

		/**
		 * @param del_identifier, the del_identifier to set
		 */
		public void setDelIdentifier(int del_identifier) {
			this.del_identifier = del_identifier;
		}

		/**
		 * @return the version
		 */
		public String getVersion() {
			return version;
		}

		/**
		 * @param version, the version to set
		 */
		public void setVersion(String version) {
			this.version = version;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name, the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @return the start
		 */
		public String getStart() {
			return start;
		}

		/**
		 * @param start, the start to set
		 */
		public void setStart(String start) {
			this.start = start;
		}

		/**
		 * @return the stop
		 */
		public String getStop() {
			return stop;
		}

		/**
		 * @param stop, the stop to set
		 */
		public void setStop(String stop) {
			this.stop = stop;
		}

		/**
		 * @return the source
		 */
		public String getSource() {
			return source;
		}

		/**
		 * @param source, the source to set
		 */
		public void setSource(String source) {
			this.source = source;
		}

		/**
		 * @return the target
		 */
		public String getTarget() {
			return target;
		}

		/**
		 * @param target, the target to set
		 */
		public void setTarget(String target) {
			this.target = target;
		}

		/**
		 * @return the dueDate
		 */
		public String getDueDate() {
			return dueDate;
		}

		/**
		 * @param dueDate, the dueDate to set
		 */
		public void setDueDate(String dueDate) {
			this.dueDate = dueDate;
		}

	public Delivery(){
		// TODO Auto-generated constructor stub
	}
}
