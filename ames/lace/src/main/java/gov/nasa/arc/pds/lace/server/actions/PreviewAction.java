package gov.nasa.arc.pds.lace.server.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.inject.Inject;
import javax.inject.Provider;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import gov.nasa.arc.pds.lace.server.BaseAction;
import gov.nasa.arc.pds.lace.server.LabelContentsServiceImpl;
import gov.nasa.arc.pds.lace.server.project.ProjectManager;



@Results({
	@Result(
			name = "success",
			type = "stream",
			params = {
					"contentType", "${type}",
					"inputName", "stream",
					"bufferSize", "1024",
					"contentDisposition", "filename=\"${filename}\""
	}),
	  
	@Result(name="error", location="error.jsp"),
})
@SuppressWarnings("serial")
public class PreviewAction extends BaseAction {
	 
	private Provider<ProjectManager> projectManagerProvider;
	private Provider<LabelContentsServiceImpl> serviceProvider;
	 
	private String type = "text/plain";
	private String fileName;
	private InputStream stream;
	private File xmlFile;
	
	@Inject
	public PreviewAction(
			Provider<ProjectManager> projectManagerProvider,
			Provider<LabelContentsServiceImpl> serviceProvider
	) {
		this.projectManagerProvider = projectManagerProvider;
		this.serviceProvider = serviceProvider;
	}
	
	public String getType() {
        return this.type;
    }
	
	public String getFileName() {
		return this.fileName;
	}
     
    public InputStream getStream() {
        return this.stream;
    }
	
	@Override
	public String executeInner() throws Exception {
		
		String userID = serviceProvider.get().getUser();
		String location = serviceProvider.get().getLocation();
		
		//Get the label file to stream
		ProjectManager manager = projectManagerProvider.get();
		this.xmlFile = manager.getLabelFile(userID, location);
		this.fileName = xmlFile.getName();
		
		stream = new FileInputStream(this.xmlFile);
		
		// if stream is null for some reason, redirect to error page
		if(stream == null)
			return "error";
		else
			return "success";
	}
}
