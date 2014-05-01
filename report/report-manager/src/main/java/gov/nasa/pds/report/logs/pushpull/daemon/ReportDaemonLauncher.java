// Copyright 2013, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
// Any commercial use must be negotiated with the Office of Technology Transfer
// at the California Institute of Technology.
//
// This software is subject to U. S. export control laws and regulations
// (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
// is subject to U.S. export control laws and regulations, the recipient has
// the responsibility to obtain export licenses or other export authority as
// may be required before exporting such information to foreign countries or
// providing access to foreign nationals.
//
// $Id$
package gov.nasa.pds.report.logs.pushpull.daemon;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.rmi.registry.LocateRegistry;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.oodt.cas.pushpull.config.Config;
import org.apache.oodt.cas.pushpull.config.DaemonInfo;
import org.apache.oodt.cas.pushpull.config.RemoteSpecs;
import org.apache.oodt.cas.pushpull.daemon.Daemon;
import org.apache.oodt.cas.pushpull.daemon.DaemonLauncherMBean;
import org.apache.oodt.cas.pushpull.daemon.DaemonManager;

/**
 * @author jpadams
 * @version $Revision$
 *
 * This is an almost exact copy of the PushPull DaemonLauncher
 * added method hasRunningDaemons in order to monitor the running
 * daemon processes
 * 
 */
public class ReportDaemonLauncher implements DaemonLauncherMBean {

    /* our log stream */
    private static final Logger LOG = Logger.getLogger(ReportDaemonLauncher.class
            .getName());

    private Config config;

    private RemoteSpecs rs;

    private int rmiRegistryPort;

    private int nextDaemonId;

    private DaemonManager dm;

    private LinkedList<Daemon> activeDaemonList;

    private LinkedList<File> sitesFiles;

    private File propertiesFile;

    private MBeanServer mbs;

    public ReportDaemonLauncher(int rmiRegistryPort, File propertiesFile,
            LinkedList<File> sitesFiles) {
        this.rmiRegistryPort = rmiRegistryPort;
        nextDaemonId = 0;
        dm = new DaemonManager();
        activeDaemonList = new LinkedList<Daemon>();
        this.sitesFiles = sitesFiles;
        this.propertiesFile = propertiesFile;
        registerJMX();
        this.configure();
    }

    private void registerJMX() {
        try {
            LocateRegistry.createRegistry(this.rmiRegistryPort);
            mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName name = new ObjectName(
                    "org.apache.oodt.cas.pushpull.daemon:type=DaemonLauncher"
                            + this.rmiRegistryPort);
            mbs.registerMBean(this, name);
        } catch (Exception e) {
            LOG.log(Level.SEVERE,
                    "Failed to register DaemonLauncher as a MBean Object : "
                            + e.getMessage());
        }
    }

    private synchronized void configure() {
        try {
            LOG.log(Level.INFO, "Configuring DaemonLauncher. . .");
            config = new Config();
            config.loadConfigFile(propertiesFile);
            rs = new RemoteSpecs();
            for (File sitesFile : sitesFiles) {
                LOG
                        .log(Level.INFO, "Loading SiteInfo file '" + sitesFile
                                + "'");
                rs.loadRemoteSpecs(sitesFile);
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to configure DaemonLauncher : "
                    + e.getMessage());
        }
    }

    public synchronized void launchDaemons() {
        LOG.log(Level.INFO, "Launching Daemons . . .");
        for (DaemonInfo daemonInfo : rs.getDaemonInfoList()) {
            int curDaemonID = this.getNextDaemonId();
            try {
                Daemon daemon = new Daemon(this.rmiRegistryPort, curDaemonID,
                        this.config, daemonInfo, rs.getSiteInfo());
                LOG.log(Level.INFO, "Creating Daemon with ID = " + curDaemonID);
                daemon.registerDaemonListener(dm);
                daemon.startDaemon();
                activeDaemonList.add(daemon);
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Failed to create Daemon with ID = "
                        + curDaemonID + " : " + e.getMessage());
            }
        }
    }

    private synchronized int getNextDaemonId() {
        while (this.dm.getUsedIDs().contains(++this.nextDaemonId))
            ;
        return this.nextDaemonId;
    }

    public void refreshDaemons() {
        this.killAllDaemons();

        LOG.log(Level.INFO, "Refreshing Daemons . . .");
        this.configure();
        launchDaemons();
    }

    public synchronized void killAllDaemons() {
        LOG.log(Level.INFO, "Killing current Daemons . . .");
        this.nextDaemonId = 0;
        for (Daemon daemon : this.activeDaemonList) {
            if (!daemon.getHasBeenToldToQuit())
                daemon.quit();
        }
        activeDaemonList.clear();
    }

    public String[] viewDaemonWaitingList() {
        return this.dm.getQueueList();
    }
    
    public boolean hasRunningDaemons() {
        return this.dm.hasRunningDaemons();
    }

    public synchronized void quit() {
        this.killAllDaemons();
        this.notify();
    }

    public static void main(String[] args) {

        int rmiRegPort = -1;
        File propertiesFile = null;
        LinkedList<File> sitesFiles = new LinkedList<File>();

        for (int i = 0; i < args.length; ++i) {
            if (args[i].equals("--rmiRegistryPort"))
                rmiRegPort = Integer.parseInt(args[++i]);
            else if (args[i].equals("--propertiesFile"))
                propertiesFile = new File(args[++i]);
            else if (args[i].equals("--remoteSpecsFile"))
                sitesFiles.add(new File(args[++i]));
        }

        ReportDaemonLauncher daemonLauncher = new ReportDaemonLauncher(rmiRegPort,
                propertiesFile, sitesFiles);

        daemonLauncher.launchDaemons();
        synchronized (daemonLauncher) {
            try {
                daemonLauncher.wait();
            } catch (Exception e) {
            }
        }
    }

}
