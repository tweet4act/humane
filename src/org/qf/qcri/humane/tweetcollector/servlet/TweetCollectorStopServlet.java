package org.qf.qcri.humane.tweetcollector.servlet;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class TweetCollectorStopServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		boolean initialized = false;
		try {
			
			String interrupted = request.getParameter("interupt");
			String key = request.getParameter("latest");
			if (interrupted.equals("interrupted"))
			{
				
				CollectorTaskServlet ts = new CollectorTaskServlet();
			
				ts.stopCollecting(key);
				
				
			}
			}
			catch (Exception ex) {
			//request.setAttribute("message", "There was an error: " + ex.getMessage());
		}
		
		
	}

}
