package org.qf.qcri.humane.tweetcollector.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.qf.qcri.humane.tweetcollector.persist.cassandra.CassandraPersistentCollection;
import org.qf.qcri.humane.tweetcollector.persist.cassandra.CassandraSchema;

public class CollectorServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static String COLLECTION_ID;
	public static String UNIVERSAL_TYPE;
	public static String SUPER_TYPE;
	public static String EVENT_TYPE;
	public static String COLLECTION_NAME;
	public static String COLLECTION_CATEGORY;
	public static String CONSUMER_KEY;
	public static String CONSUMER_SECRET;
	public static String AUTHENTICATION_TYPE;
	public static String AUTHENTICATION_SECRET;
    Thread t;
	public CollectorServlet()
	{
		
	}
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		log("Initialized the Collection servlet");
	}


	  public void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			// parses the request's content to extract file data
			COLLECTION_NAME = request.getParameter("name");
			COLLECTION_CATEGORY = request.getParameter("category");
			if (COLLECTION_CATEGORY.equals("Others"))
			{
				UNIVERSAL_TYPE = "Others";
				SUPER_TYPE = "Others";
				EVENT_TYPE = "Others";
			}
			String[] cat=COLLECTION_CATEGORY.split("/");
			UNIVERSAL_TYPE = cat[0];
			SUPER_TYPE = cat[1];
			EVENT_TYPE = cat[2];
			CONSUMER_KEY = request.getParameter("ck");
			CONSUMER_SECRET = request.getParameter("cs");
			AUTHENTICATION_TYPE = request.getParameter("at");
			AUTHENTICATION_SECRET = request.getParameter("as");
			COLLECTION_ID= request.getParameter("collectionid");
			//System.out.println(queryDef);
			
			
			CassandraPersistentCollection cpc= new CassandraPersistentCollection(CassandraSchema.DEFAULT_KEYSPACE_NAME,CassandraSchema.COLUMNFAMILY_NAME_COLLECTION);
			cpc.set(COLLECTION_ID, COLLECTION_NAME, UNIVERSAL_TYPE, SUPER_TYPE, EVENT_TYPE, CONSUMER_KEY, CONSUMER_SECRET, AUTHENTICATION_TYPE, AUTHENTICATION_SECRET);
			
			request.setAttribute("collectionid", COLLECTION_ID);
			

		
			
			
		} catch (Exception ex) {
			
		}
		
		getServletContext().getRequestDispatcher("/collectiontask.jsp").forward(request, response);
	  }
	
	
}
