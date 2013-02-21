package org.qf.qcri.humane.tweetcollector.servlet;

import java.io.IOException;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
import me.prettyprint.cassandra.service.template.ColumnFamilyUpdater;
import me.prettyprint.cassandra.service.template.ThriftColumnFamilyTemplate;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.exceptions.HectorException;
import me.prettyprint.hector.api.factory.HFactory;
import org.qf.qcri.humane.tweetcollector.persist.cassandra.CassandraPersistentCollectionTask;
import org.qf.qcri.humane.tweetcollector.persist.cassandra.CassandraSchema;
import org.qf.qcri.humane.tweetcollector.twitter.TweetCollector;

/*
 * CollectorTaskServlet handles the new colletion task start request. This initializes the new collection task.
 */
public class CollectorTaskServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static String COLLECTION_TASK_ID;
	public static String COLLECTION_ID;
    Thread t;
    public static TweetCollector collector;
	private boolean abort = false;
	public CollectorTaskServlet()
	{
		
	}
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		log("Initialized the Collection Task servlet");
	}


	  public void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String collectiontaskname = "";
		String track = "";
		String geo ="";
		String startdate="";
		String enddate="";
		String collectiontaskid="";
		String collectionname="";
		try {
			// parses the request's content to extract file data
			COLLECTION_ID= request.getParameter("collectionid");
			collectionname= request.getParameter("collectionname");
			collectiontaskname = request.getParameter("ctname");
			collectiontaskid=request.getParameter("collectiontaskid");
			track = request.getParameter("track");
			geo = request.getParameter("geo");
			startdate = request.getParameter("startdate");
			enddate = request.getParameter("enddate");
			
			COLLECTION_TASK_ID = collectiontaskid;
			
			CassandraPersistentCollectionTask cpct= new CassandraPersistentCollectionTask(CassandraSchema.DEFAULT_KEYSPACE_NAME,CassandraSchema.COLUMNFAMILY_NAME_COLLECTION_TASK);
			String strtoPrint=COLLECTION_ID+":"+collectionname+COLLECTION_TASK_ID+":"+collectiontaskname+":"+track+":"+geo;
			System.out.println(strtoPrint);
			cpct.set(COLLECTION_ID,COLLECTION_TASK_ID, collectionname,collectiontaskname, startdate, enddate, track, geo);
			collector = (TweetCollector)getServletContext().getAttribute(TweetCollector.class.getName());
			if(collector!=null) {
			}
			
			
			collector= new TweetCollector(COLLECTION_TASK_ID);
			 t = new Thread(collector);
			
			System.out.println("Starting collector thread...");
			while (!abort)
			{
			t.start();
			
			request.setAttribute("message", "success");
			Thread.sleep(500);
			
			}
			
			
		
			
			
			
			
			
		} catch (Exception ex) {
			
		}
		
		getServletContext().getRequestDispatcher("/landingpage.jsp").forward(request, response);
	  }
	  
	  public void getTaskInfo()
	  { CassandraPersistentCollectionTask cpct= new CassandraPersistentCollectionTask(CassandraSchema.DEFAULT_KEYSPACE_NAME,CassandraSchema.COLUMNFAMILY_NAME_COLLECTION_TASK);
		  TreeMap<Long, String> collection= cpct.get(COLLECTION_ID);
			System.out.println(collection);
			 for(Map.Entry<Long,String> entry: collection.entrySet())
			 {
				System.out.println(entry.getValue());
				
				
			 }
			
		  
	  }
	  
	  public synchronized void stopCollecting(String key) {
		  	Cluster cluster = HFactory.getOrCreateCluster("Test Cluster", "localhost:9160");
			 Keyspace keyspace = HFactory.createKeyspace("twitterresearch", cluster);
			 ColumnFamilyTemplate<String, String> template =new ThriftColumnFamilyTemplate <String, String>(keyspace,"collectiontask",StringSerializer.get(),StringSerializer.get());
			 ColumnFamilyUpdater<String, String> updater = template.createUpdater(key);
			 Calendar cal = Calendar.getInstance();
			 String sTime = cal.getTime().toString();
			 updater.setString("EDate", sTime);
			
			 try {
			     template.update(updater);
			     t.interrupt();
			     collector.abortCollection();
			     
			     try {
					TweetCollector collectorN= new TweetCollector(COLLECTION_TASK_ID);
					collectorN.abortCollection();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			    
			 } catch (HectorException e) {
				 e.printStackTrace();
			     // do something ...
			 }
			 	//System.out.println(Thread.currentThread().activeCount());
			 
				abort = true;
				//request.setAttribute("message", "success");
				getServletContext().getRequestDispatcher("/landingpage.jsp");
				
		}

		@SuppressWarnings("unused")
		private synchronized boolean isAborted() {
			return abort;
		}

	
	
}