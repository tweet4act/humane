package org.qf.qcri.humane.login.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, java.io.IOException {

		try
		{	    
		     String uname= request.getParameter("uname");
		     String pwd= request.getParameter("pass");
		
			   		    
		     if (uname.equals("admin") && pwd.equals("admin"))
		     {
			        

		        
		          getServletContext().getRequestDispatcher("/landingpage.jsp").forward(request, response);
		     }
			        
		     else 
		     
		    	 getServletContext().getRequestDispatcher("/badLogin.jsp").forward(request, response);
		}	
		catch (Throwable theException) 	    
		{
		     System.out.println(theException); 
		}
     }
}

