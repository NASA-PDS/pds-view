package gov.nasa.arc.pds.lace.server;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * Implements a servlet for writing a file sent from the client to
 * a location on the server.
 */
public class FileUploadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	// upload settings
    private static final int MEMORY_THRESHOLD   = 1024 * 1024 * 3;  // 3MB
    private static final int MAX_FILE_SIZE      = 1024 * 1024 * 40; // 40MB
    private static final int MAX_REQUEST_SIZE   = 1024 * 1024 * 50; // 50MB

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		super.doGet(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		if (!ServletFileUpload.isMultipartContent(request)) {
		      response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
		          "The request content type is not supported by the servlet.");
		      return;
		}

		 // Configures upload settings
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // Sets memory threshold - beyond which files are stored in disk
        factory.setSizeThreshold(MEMORY_THRESHOLD);
        // Sets temporary location to store files
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

        ServletFileUpload upload = new ServletFileUpload(factory);

        // Sets maximum size of upload file
        upload.setFileSizeMax(MAX_FILE_SIZE);

        // Sets maximum size of request (include file + form data)
        upload.setSizeMax(MAX_REQUEST_SIZE);

        // Constructs the directory path (which is relative to
        // application's directory) to store upload file
        String uploadPath = getServletContext().getRealPath("") + File.separator + "upload";

        // Creates the directory if it does not exist
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        try {
            // parses the request's content to extract file data
            @SuppressWarnings("unchecked")
            List<FileItem> formItems = upload.parseRequest(request);

            PrintWriter out = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), "UTF-8"));

            if (formItems != null && formItems.size() > 0) {
                // iterates over form's fields
                for (FileItem item : formItems) {
                    // processes only fields that are not form fields
                    if (!item.isFormField()) {
                        String fileName = new File(item.getName()).getName();
                        String filePath = uploadPath + File.separator + fileName;
                        File storeFile = new File(filePath);

                        // saves the file on disk
                        item.write(storeFile);
                        out.println(filePath);
                        response.setStatus(HttpServletResponse.SC_CREATED);
                    }
                }

                response.setContentType("text/html");
                response.getWriter().print("Done uploading!");
                response.flushBuffer();
            }
        } catch (Exception ex) {
		      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "There was an error: " + ex.getMessage());
        }
     }
}
