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

package gov.nasa.pds.tracking.email;

import java.io.IOException;
import java.io.PrintWriter; 
import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

  public void init() {
    // reads SMTP server setting from web.xml file
    ServletContext context = getServletContext();
    host = context.getInitParameter("host");
    port = context.getInitParameter("port");
    user = context.getInitParameter("user");
    pass = context.getInitParameter("pass");
    maxMsgNums = Integer.parseInt(context.getInitParameter("max_msg_nums"));
  }

  public void doPost(HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {
    PrintWriter out = response.getWriter();

    String to=request.getParameter("recipient");  
    String subject=request.getParameter("subject");  
    String msg=request.getParameter("content");

    SendEmail mailer;
    if (user!=null && pass!=null)
      mailer = new SendEmail(host, port, user, pass);
    else
      mailer = new SendEmail(host, port);

    mailer.setMaxMsgNums(maxMsgNums);

    if (mailer!=null) {
      ArrayList<String> addressList;
      if (to.contains(",")) {
        addressList = new ArrayList<String>(Arrays.asList(to.split(",")));
        mailer.send(addressList, subject, msg);
      }
      else 
        mailer.send(to, subject, msg);  

      out.print("A message has been sent successfully....");  
    }
    else {
      out.println("Having an issue to instantiate SendEmail class....");
    }
    out.close();  
  }
}