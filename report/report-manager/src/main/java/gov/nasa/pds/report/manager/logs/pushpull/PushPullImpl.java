package gov.nasa.pds.report.manager.logs.pushpull;

import gov.nasa.pds.report.manager.logs.pushpull.daemon.ReportDaemonLauncher;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.logging.Logger;

import org.apache.oodt.cas.pushpull.daemon.DaemonLauncher;

public class PushPullImpl implements PushPull {
    /* our log stream */
    private static final Logger LOG = Logger.getLogger(PushPullImpl.class
            .getName());
    
	protected int port;
	protected File propertiesFile;
	protected LinkedList<File> sitesFiles;
	
	// TODO Known issue with this. Severe error thrown when trying to register as MBean
	protected DaemonLauncher daemonLauncher;
	
	/**
	 * --rmiRegistryPort ${DAEMONLAUNCHER_PORT} \
     * --propertiesFile ${CAS_PP_RESOURCES}/push_pull_framework.properties \
     * --remoteSpecsFile ${CAS_PP_RESOURCES}/conf/RemoteSpecs.xml 
	 * @author jpadams
	 * @version $Revision:$
	 * @throws FileNotFoundException 
	 *
	 */
	public PushPullImpl() throws FileNotFoundException {
		this(-1, null, new LinkedList<File>());
	}

	public PushPullImpl(int port, File propertiesFile, File sitesFile) throws FileNotFoundException {
		this(port, propertiesFile, new LinkedList<File>(Arrays.asList(sitesFile)));
	}

	public PushPullImpl(int port, File propertiesFile, LinkedList<File> sitesFiles) throws FileNotFoundException {
		
		this.propertiesFile = propertiesFile;
		this.sitesFiles = sitesFiles;
		
		setPort(port);
	}

	@Override
	public void pull() throws Exception {
        daemonLauncher = new DaemonLauncher(port,
                propertiesFile, sitesFiles);
		
        //Thread myThread = new Thread(new Runnable() {

        //    public void run() {
		try {


        daemonLauncher.launchDaemons();
        
        synchronized (daemonLauncher) {
            try {
            	daemonLauncher.wait();
            	//while (daemonLauncher.checkStatus()) {
            	// TODO This still doesn't work when running via CLI, but it works in testing
            	//while (daemonLauncher.hasRunningDaemons() ) {
            		//LOG.log(ToolsLevel.WARNING, "Waiting for " + daemonLauncher.viewDaemonWaitingList().length + " daemons to complete...");
            	//}
            	daemonLauncher.quit();
            } catch (Exception e) {
            	e.printStackTrace();
            }
            
        	//while (daemonLauncher.viewDaemonWaitingList().length > 0) {
        		//LOG.log(ToolsLevel.WARNING, "Waiting for " + daemonLauncher.viewDaemonWaitingList().length + " daemons to complete...");
        	//}
        	//daemonLauncher.quit();
        }
        
    	//while (daemonLauncher.viewDaemonWaitingList().length > 0) {
			//LOG.log(ToolsLevel.WARNING, "Waiting for " + daemonLauncher.viewDaemonWaitingList().length + " daemons to complete...");
		//}
		//daemonLauncher.quit();
        
		} catch (NullPointerException e) {
			throw new Exception("Error in PushPull.DaemonLauncher. See log output.");
		}
       //     }
       // });
	}
	
	@Override
	public int getNumFilesPulled() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void setPort(int port) {
		if (port == -1) {
			this.port = DEFAULT_PORT;
		} else {
			this.port = port;
		}
	}
	
	@Override
	public int getPort() {
		return this.port;
	}

	@Override
	public void setPropertiesFile(File propertiesFile) {
		this.propertiesFile = propertiesFile;	
	}

	@Override
	public File getPropertiesFile() {
		return this.propertiesFile;
	}

	@Override
	public void setSitesFiles(LinkedList<File> sitesFiles) {
		this.sitesFiles = sitesFiles;		
	}
	
	@Override
	public void addSitesFile(File sitesFile) {
		this.sitesFiles.add(sitesFile);
	}

	@Override
	public LinkedList<File> getRemoteSpecsFile() {
		return this.sitesFiles;
	}
	
}
