package org.qf.qcri.humane.login.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginServlet extends HttpServlet {

	/**
	 * This servlet class is used by the humane's loginPage.jsp code to check 
	 * the credentials provided by the user 
	 */
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, java.io.IOException {

		try
		{	    
		     String uname= request.getParameter("uname");
		     String pwd= request.getParameter("pass");
		    
		    // for the time being we support only one username (admin) and one password credential(admin)
		    // later we are going to change this part with a more sophisticated user account management module 

			   		    
		     if (uname.equals("admin") && pwd.equals("admin"))
		     {
			        

		         // if credential is correct as expected then redirect the request to the landingpage.jsp

		          getServletContext().getRequestDispatcher("/landingpage.jsp").forward(request, response);
		     }
			        
		     else 
		     	 // if credential is wrong then redirect to a blank page with error message

		    	 getServletContext().getRequestDispatcher("/badLogin.jsp").forward(request, response);
		}	
		catch (Throwable theException) 	    
		{
		     System.out.println(theException); 
		}
     }
}

