package gov.nasa.pds.tools;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class FileLocationContextListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent servletContextEvent) {
    	String rootPath = System.getProperty("catalina.home");
		ServletContext ctx = servletContextEvent.getServletContext();

		if (ctx.getInitParameter("tempfile.rootdir")!=null) {
		   rootPath = ctx.getInitParameter("tempfile.rootdir");
		}
		else 
		   rootPath = "/tmp";

		String relativePath = null;
		if (ctx.getInitParameter("tempfile.filedir")!=null)
    	   relativePath = ctx.getInitParameter("tempfile.filedir");

        File file = null;
		if (relativePath!=null) {
    	   file = new File(rootPath + File.separator + relativePath);
    	   ctx.setAttribute("FILES_DIR", rootPath + File.separator + relativePath);
		}
		else {
		   file = new File(rootPath);
		   ctx.setAttribute("FILES_DIR", rootPath);
		}

    	if (!file.exists()) file.mkdirs();

    	System.out.println("File Directory created to be used for storing files. Directory: " + file.toString());
    	ctx.setAttribute("FILES_DIR_FILE", file);
    }

	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		//do cleanup if needed
	}

}
