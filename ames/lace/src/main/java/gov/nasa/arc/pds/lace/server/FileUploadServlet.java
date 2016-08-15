package gov.nasa.arc.pds.lace.server;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
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
@SuppressWarnings("serial")
@Singleton
public class FileUploadServlet extends HttpServlet {

	// upload settings
    private static final int MEMORY_THRESHOLD   = 1024 * 1024 * 3;  // 3MB
    private static final int MAX_FILE_SIZE      = 1024 * 1024 * 40; // 40MB
    private static final int MAX_REQUEST_SIZE   = 1024 * 1024 * 50; // 50MB

	private ServerConfiguration serverConfig;

	/**
	 * Creates a new instance with a given server configuration.
	 *
	 * @param serverConfig the server configuration
	 */
	@Inject
	public FileUploadServlet(ServerConfiguration serverConfig) {
		this.serverConfig = serverConfig;
	}

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
        File uploadDir = serverConfig.getUploadRoot();

        try {
            // parses the request's content to extract file data
            List<FileItem> formItems = upload.parseRequest(request);

            PrintWriter out = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), "UTF-8"));

            if (formItems != null && formItems.size() > 0) {
                // iterates over form's fields
                for (FileItem item : formItems) {
                    // processes only fields that are not form fields
                    if (!item.isFormField()) {
                        String fileName = new File(item.getName()).getName();
                        File storeFile = new File(uploadDir, fileName);

                        // saves the file on disk
                        item.write(storeFile);
                        out.println(storeFile.getAbsolutePath());

                        // Make sure file is writable, so that it can be manipulated by
                        // scripts not running as the web container user.
                        storeFile.setWritable(true, false);

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
