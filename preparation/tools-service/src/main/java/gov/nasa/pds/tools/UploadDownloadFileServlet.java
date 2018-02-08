package gov.nasa.pds.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

@WebServlet("/UploadDownloadFileServlet")
public class UploadDownloadFileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private ServletFileUpload uploader = null;
	@Override
	public void init() throws ServletException{
		DiskFileItemFactory fileFactory = new DiskFileItemFactory();
		File filesDir = (File) getServletContext().getAttribute("FILES_DIR_FILE");
		fileFactory.setRepository(filesDir);
		this.uploader = new ServletFileUpload(fileFactory);
	}
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		String fileName = request.getParameter("fileName");
    	if(fileName == null || fileName.equals("")){
    		throw new ServletException("File Name can't be null or empty");
    	}
    	File file = new File(fileName);
    	if(!file.exists()){
    		throw new ServletException("File doesn't exists on server.");
    	}
    	System.out.println("File location on server::"+file.getAbsolutePath());
    	ServletContext ctx = getServletContext();
    	InputStream fis = new FileInputStream(file);
    	String mimeType = ctx.getMimeType(file.getAbsolutePath());
    	response.setContentType(mimeType != null? mimeType:"application/octet-stream");
    	response.setContentLength((int) file.length()); 	
    	String tmpFileName = file.getName();
    	response.setHeader("Content-Disposition", "attachment; filename=\"" + tmpFileName + "\"");

    	ServletOutputStream os = response.getOutputStream();
    	byte[] bufferData = new byte[1024];
    	int read=0;
    	while((read = fis.read(bufferData))!= -1){
    		os.write(bufferData, 0, read);
    	}
    	os.flush();
    	os.close();
    	fis.close();
    	System.out.println("File: " + tmpFileName + " downloaded at client successfully");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(!ServletFileUpload.isMultipartContent(request)){
			throw new ServletException("Content type is not multipart/form-data");
		}
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.write("<html><head></head><body>");
		String baseUrl = null;
		try {
			List<FileItem> fileItemsList = uploader.parseRequest(request);
			Iterator<FileItem> fileItemsIterator = fileItemsList.iterator();
			String fileToTransform = null;
			while(fileItemsIterator.hasNext()){
				FileItem fileItem = fileItemsIterator.next();
				System.out.println("FieldName="+fileItem.getFieldName());
				System.out.println("FileName="+fileItem.getName());
				System.out.println("ContentType="+fileItem.getContentType());
				System.out.println("Size in bytes="+fileItem.getSize());

				File file = new File(request.getServletContext().getAttribute("FILES_DIR")+File.separator+fileItem.getName());
				System.out.println("Absolute Path at server="+file.getAbsolutePath());
				fileItem.write(file);
	
				if ( ( request.getServerPort() == 80 ) ||
						( request.getServerPort() == 443 ) )
					baseUrl = request.getScheme() + "://" +
							request.getServerName() + request.getContextPath();
				else
					baseUrl = request.getScheme() + "://" +
							request.getServerName() + ":" + request.getServerPort() +
							request.getContextPath();
				System.out.println("******baseUrl = " + baseUrl);
				
				if (file.getName().endsWith(".xml") || file.getName().endsWith(".LBL"))
					fileToTransform = file.getAbsolutePath();
				
				if (fileItemsList.size()<2) 
					fileToTransform = file.getAbsolutePath();
				out.write(fileItem.getName() + " uploaded successfully.<br/>");
			}
		    response.sendRedirect(baseUrl + "/index.jsp?baseUrl=" + baseUrl+"&fileName=" + fileToTransform);

		} catch (FileUploadException e) {
			out.write("Exception in uploading file.");
		} catch (Exception e) {
			out.write("Exception in uploading file.");
		}		
		out.write("</body></html>");
		out.close();
		System.out.println("File uploaded successfully.");
	}
}