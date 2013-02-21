package org.qf.qcri.humane.login.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginServlet extends HttpServlet {

	/**
	 * This servlet is responsible for handling the login credential
	 */
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, java.io.IOException {

		try
		{	    
		     String uname= request.getParameter("uname");
		     String pwd= request.getParameter("pass");
		
			 /*
			  * for the time being it is hard coded to support only one account admin-admin.
			  * later more sophisticated account handling code can be appended here.
			  */
		     
		     
		     if (uname.equals("admin") && pwd.equals("admin"))
		     {
			        

		        //redirect to landingpage.jsp, if the credentials are correct
		          getServletContext().getRequestDispatcher("/landingpage.jsp").forward(request, response);
		     }
			        
		     else 
		    	 //otherwise redirect to other error message page
		    	 getServletContext().getRequestDispatcher("/badLogin.jsp").forward(request, response);
		}	
		catch (Throwable theException) 	    
		{
		     System.out.println(theException); 
		}
     }
}

