// This software was developed by the Object Oriented Data Technology task of the Science
// Data Engineering group of the Engineering and Space Science Directorate of the Jet
// Propulsion Laboratory of the National Aeronautics and Space Administration, an
// independent agency of the United States Government.
// 
// This software is copyrighted (c) 2000 by the California Institute of Technology.  All
// rights reserved.
// 
// Redistribution and use in source and binary forms, with or without modification, is not
// permitted under any circumstance without prior written permission from the California
// Institute of Technology.
//
// THIS SOFTWARE IS PROVIDED BY THE AUTHORS AND CONTRIBUTORS ``AS IS'' AND ANY EXPRESS OR
// IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
// THE AUTHOR OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
// EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
// HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
// OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//

package jpl.pds.beans;

import java.sql.*;
import java.util.*;
import jpl.pds.util.*;
import javax.naming.*;
import javax.sql.*;


/** The search bean handles searching.
 *
 * This class is used by the PDS data set view web interface to retrieve
 * values for building the search parameter pull-down lists.
 *
 * @author Thuy Tran
 * @author Cyndi Atkinson
 */
public class GetTargetSearchParams {
	private static String[] msnname = new String[]{};
	private static String[] instname = new String[]{}; 
	private static String[] insthostname = new String[]{};
	private static String[] targname = new String[]{};
	private static String[] insttype = new String[]{};
	private static String[] targtype = new String[]{};
	private static String[] dsname = new String[]{};
	private static String[] dsid = new String[]{};
	private static String[] dataobjtype = new String[]{};
	private static String[] insthosttype = new String[]{};
	private static String[] nodename = new String[]{};
	private static String[] archivestat = new String[]{};
	
   static String dontDisplay = "|UNKNOWN|UNK|NA|N/A|";

	/** Query the database to build the pull-down lists
	 */
	static Connection connection = null;
	static {
		try {
			/*Class.forName("com.sybase.jdbc2.jdbc.SybDriver");
			Connection connection = DriverManager.getConnection(
				"jdbc:sybase::Tds:" + sysProps.getDBServer() + sysProps.getDB(),
				"jdbc:sybase::Tds:" + dbserver + db,
				sysProps.getDBLogin(), sysProps.getDBLogin());*/
			javax.naming.Context init = new javax.naming.InitialContext();
			javax.naming.Context env = (Context)init.lookup("java:comp/env");
			DataSource ds = (DataSource) env.lookup("jdbc/pdsprofile");
			connection = ds.getConnection();
			Statement statement = connection.createStatement();
			/*
			** Build the dsid list
			*/
			System.err.println("Building dsid list");
         // filter out superseded dsids
			// ResultSet rs = statement.executeQuery("select distinct dsid from dsinfo where dsid is not null order by dsid");
         // block SUPERSEDED archivestat - per S. Hughes
         ResultSet rs = statement.executeQuery("select distinct dsid from dsinfo where dsid is not null and archivestat !='SUPERSEDED' order by dsid");

			List list = new ArrayList();
			list.add("All");
			while (rs.next()) {
				list.add(DisplayOptions.CapFirstLetter(rs.getString(1).trim()));
			
			}
			dsid = (String[]) list.toArray(dsid);
			rs.close();
			/*
			** Build the dsname list
			*/
			System.err.println("Building dsname list");
			rs = statement.executeQuery("select distinct dsname from dsinfo where dsname is not null order by dsname");
			list = new ArrayList();
			list.add("All");
			while (rs.next()) {
				list.add(DisplayOptions.CapFirstLetter(rs.getString(1).trim()));
			
			}
			dsname = (String[]) list.toArray(dsname);
			rs.close();
			/*
			** Build the msnname list
			*/
			System.err.println("Building msnname list");
			rs = statement.executeQuery("select distinct msnname from msninfo where msnname is not null order by msnname");
			list = new ArrayList();
			while (rs.next()) {
				list.add(DisplayOptions.CapFirstLetter(rs.getString(1).trim()));
			
			}
			msnname = (String[]) list.toArray(msnname);
			rs.close();
			/*
			** Build the targname list
			*/
			System.err.println("Building targname list");
			rs = statement.executeQuery("select distinct targname from targetinfo where targname is not null order by listord,targname");
			list = new ArrayList();
			//list.add("All");
			while (rs.next()) {
				list.add(DisplayOptions.CapFirstLetter(rs.getString(1).trim()));
			
			}
			targname = (String[]) list.toArray(targname);
			rs.close();
			/*
			** Build the tartype list
			*/
			System.err.println("Building targtype list");
			rs = statement.executeQuery("select distinct targtype from targetinfo where targtype is not null order by targtype");
			list = new ArrayList();
			list.add("All");
			while (rs.next()) {
				list.add(DisplayOptions.CapFirstLetter(rs.getString(1).trim()));
			
			}
			targtype = (String[]) list.toArray(targtype);
			rs.close();
			/*
			** Build the instname list
			*/
			System.err.println("Building instname list");
			rs = statement.executeQuery("select distinct instname from instinfo where instname is not null order by instname");
			list = new ArrayList();
			while (rs.next()) {
				list.add(DisplayOptions.CapFirstLetter(rs.getString(1).trim()));
			
			}
			instname = (String[]) list.toArray(instname);
			rs.close();
			/*
			** Build the insthostname list
			*/
			System.err.println("Building insthostname list");
			rs = statement.executeQuery("select distinct insthostname from hostinfo where insthostname is not null order by insthostname");
			list = new ArrayList();
			while (rs.next()) {
				list.add(DisplayOptions.CapFirstLetter(rs.getString(1).trim()));
			
			}
			insthostname = (String[]) list.toArray(insthostname);
			rs.close();
			/*
			** Build the nodename list
			*/
			System.err.println("Building nodename list");
			rs = statement.executeQuery("select distinct nodename from nodeinfo where nodename is not null order by nodename");
			list = new ArrayList();
			list.add("All");
			while (rs.next()) {
				list.add(DisplayOptions.CapFirstLetter(rs.getString(1).trim()));
			}
			nodename = (String[]) list.toArray(nodename);
			rs.close();
			/*
			** Build the insttype list
			*/
			System.err.println("Building insttype list");
			rs = statement.executeQuery("select distinct insttype from instinfo where insttype is not null order by insttype");
			list = new ArrayList();
			list.add("All");
			while (rs.next()) {
				list.add(DisplayOptions.CapFirstLetter(rs.getString(1).trim()));
			}
			insttype= (String[]) list.toArray(insttype);
			rs.close();
			/*
			** Build the dataobjtype list
			*/
			System.err.println("Building dataobjtype list");
			rs = statement.executeQuery("select distinct dataobjtype from dsinfo where dataobjtype is not null order by dataobjtype");
			list = new ArrayList();
			list.add("All");
			while (rs.next()) {
				list.add(DisplayOptions.CapFirstLetter(rs.getString(1).trim()));
			}
			dataobjtype= (String[]) list.toArray(dataobjtype);
			rs.close();
			/*
			** Build the insthosttype list
			*/
			System.err.println("Building insthosttype list");
			rs = statement.executeQuery("select distinct insthosttype from hostinfo where insthosttype is not null order by insthosttype");
			list = new ArrayList();
			list.add("All");
			while (rs.next()) {
				list.add(DisplayOptions.CapFirstLetter(rs.getString(1).trim()));
			}
			insthosttype = (String[]) list.toArray(insthosttype);
			rs.close();
			/*
			** Build the archivestat list
			*/
			System.err.println("Building archivestat list");
			//rs = statement.executeQuery("select distinct archivestat from dsinfo where archivestat is not null order by archivestat");
         // block SUPERSEDED archivestat
         rs = statement.executeQuery("select distinct archivestat from dsinfo where archivestat is not null and archivestat != 'SUPERSEDED' order by archivestat");

			list = new ArrayList();
			list.add("All");
			while (rs.next()) {
				list.add(DisplayOptions.CapFirstLetter(rs.getString(1).trim()));
			}
			archivestat = (String[]) list.toArray(archivestat);
			rs.close();
			statement.close();
			//connection.close(); 
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}finally {
  			if (connection != null){
				try {
    				connection.close();
  				} catch (SQLException ignore) {}
			}
		}
	}

	/** Constructor.
	 *
	 * This bean is constructed by the web server.
	 */
	public GetTargetSearchParams() { }

	public static String[] getDatasetId() {
		return dsid;
	}

	public static String[] getDatasetName() {
		return dsname;
	}

/*
	public static String[] getMissionName() {
		return msnname;
	}
*/


   /* to allow displaying mission aliases
      construct 2-dimentional array (display value/alias, database value)
   */
   public static String[][] getMissionNamePair() {
      int i = 0;
      List msnname_alias = new ArrayList();

      // build a msnname alias list bases on the msnname plus some aliases
      // when needed. The aliases are to be displayed on the pulldown list.
      // Sort the list and create the array pair with real msnname as values
      for (i=0; i<msnname.length; i++) {
         if (dontDisplay.indexOf(msnname[i])<0)
             msnname_alias.add(msnname[i]);
      }

      // manually add in aliases for now. per S. Hughes
      msnname_alias.add("Odyssey");
      msnname_alias.add("Mars Odyssey");
      msnname_alias.add("Clementine 1");
      Collections.sort(msnname_alias);

      String[][] msnname_pair = new String[msnname_alias.size()][2];

      for (i=0; i<msnname_alias.size(); i++) {
         msnname_pair[i][0]=(String)msnname_alias.get(i);

         // convert aliases to associating database values at the
         // 2nd part of the 2-dimention array
         if (msnname_alias.get(i).equals("Odyssey") ||
             msnname_alias.get(i).equals("Mars Odyssey")) {
            msnname_pair[i][1]="2001 Mars Odyssey";
         }
         else if (msnname_alias.get(i).equals("Clementine 1")) {
            msnname_pair[i][1]="Deep Space Program Science Experiment";
         }
         else
            msnname_pair[i][1]=(String)msnname_alias.get(i);
      }

      msnname_alias.clear();

      return msnname_pair;
   }

	public static String[] getTargetName() {
		return targname;
	}

	public static String[] getTargetType() {
		return targtype;
	}

	public static String[] getInstrumentName() {
		return instname;
	}

	public static String[] getInstrumentHostName() {
		return insthostname;
	}

	public static String[] getNodeName() {
		return nodename;
	}


	public static String[] getArchiveStat() {
		return archivestat;
	}

	public static String[] getInstrumentHostType() {
		return insthosttype;
	}

	public static String[] getInstrumentType() {
		return insttype;
	}

	public static String[] getDataObjectType() {
		return dataobjtype;
	}

	/** Command line invocation.
	 *
	 * @param argv Command-line arguments.
	 */
	public static void main(String[] argv) {
		//Properties p = System.getProperties();
		//p.list(System.out);
		try {
			GetTargetSearchParams bean = new GetTargetSearchParams();
		} catch (Exception ex) {
			System.err.println("Exception " + ex.getClass().getName() + ": " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}
		System.exit(0);
	}
}
