// Copyright 2016-2017, by the California Institute of Technology.
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
// $Id: $

package gov.nasa.pds.email;

import java.io.IOException;
import java.io.PrintWriter; 
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.net.URLDecoder;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import gov.nasa.pds.email.SendEmail;

/**
 * A servlet that takes message details from user and send it as a new e-mail
 * through an SMTP server.
 * 
 * @author www.codejava.net
 * 
 */
@WebServlet("/EmailSendingServlet")
public class EmailSendingServlet extends HttpServlet {

	private String host;
	private String port;
	private String user;
	private String pass;
	private int maxMsgNums = 200;
	final static String encType = "UTF-8";

	public void init() {
		// reads SMTP server setting from web.xml file
		ServletContext context = getServletContext();
		host = context.getInitParameter("host");
		port = context.getInitParameter("port");
		user = context.getInitParameter("user");
		pass = context.getInitParameter("pass");
		maxMsgNums = Integer.parseInt(context.getInitParameter("max_msg_nums"));
	}

	static String extractPostRequestBody(HttpServletRequest request) throws IOException {
		if ("POST".equalsIgnoreCase(request.getMethod())) {
			Scanner s = new Scanner(request.getInputStream(), encType).useDelimiter("\\A");
			return s.hasNext() ? s.next() : "";
		}
		return "";
	}

	public void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");  
		String to = "", subject = "", msg = "";

		String reqBody = extractPostRequestBody(request);	
		String[] items = reqBody.split("&");
		for (int i=0; i<items.length; i++) {
			if (items[i].startsWith("recipients")) {
				// TODO TODO: do we need to trim the string????
				to = items[i].substring(items[i].indexOf("=")+1);
				// Decode HTML encoded special characters back to normal characters
				to = URLDecoder.decode(to, encType);
			}
			if (items[i].startsWith("subject")) {
				subject = items[i].substring(items[i].indexOf("=")+1);
				subject = URLDecoder.decode(subject, encType);
			}
			if (items[i].startsWith("content")) {
				msg = items[i].substring(items[i].indexOf("=")+1);
				msg = URLDecoder.decode(msg, encType); 
			}
		}
		//System.out.println("recipient = '" + to + "'    subject = " + subject + "    msg = " + msg);

		String resultMessage = "";
		try {
			SendEmail mailer;
			
			//System.out.println("username = *" + user + "*      password = *" + pass + "*");
			if (user.equals("") && pass.equals("")) {
				mailer = new SendEmail(host, port);
				System.out.println("set mailer with host, port...");
			} 
			else {
				if (user!=null && pass!=null) {
					mailer = new SendEmail(host, port, user, pass);
					System.out.println("set mailer with host, port, user, pass...");
				}
				else { 
					mailer = new SendEmail(host, port);
					System.out.println("set mailer with host, port...");
				}
			}
			mailer.setMaxMsgNums(maxMsgNums);

			if (mailer!=null) {
				ArrayList<String> addressList;
				if (to.contains(",") || to.contains("%2C")) {
					if (to.contains("%2C")) {
						to = to.replace("%2C",",");
					}
					addressList = new ArrayList<String>(Arrays.asList(to.split(",")));
					mailer.send(addressList, subject, msg);
					System.out.println("mailing to multiple users....");
				}
				else {
					mailer.send(to, subject, msg);  
					System.out.println("mailing to one user....");
				}

				resultMessage = "The e-mail was sent successfully";			
				//resultMessage += "\nTO: " + to
				//		      + "\nSUBJECT: " + subject 
				//		      + "\nMessage: " + msg + "\n";
				System.out.println("A message has been sent successfully....");  
			}
			else {
				System.out.println("Having an issue to instantiate SendEmail class....");
				resultMessage = "There is an error to instantiate SendEmail().";
			}
		}catch (Exception ex) {
			ex.printStackTrace();
			resultMessage = "There were an error: " + ex.getMessage();
		} finally {
			request.setAttribute("Message", resultMessage);
			getServletContext().getRequestDispatcher("/Result.jsp").forward(
					request, response);
		}
	}
}