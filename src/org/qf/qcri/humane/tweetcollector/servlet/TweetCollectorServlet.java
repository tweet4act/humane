package org.qf.qcri.humane.tweetcollector.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.ArrayList;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
import me.prettyprint.cassandra.service.template.ColumnFamilyUpdater;
import me.prettyprint.cassandra.service.template.ThriftColumnFamilyTemplate;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.exceptions.HectorException;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.ColumnQuery;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import me.prettyprint.hector.api.query.QueryResult;
import org.qf.qcri.humane.tweetcollector.persist.cassandra.CassandraPersistentEventCounters;
import org.qf.qcri.humane.tweetcollector.persist.cassandra.CassandraSchema;
import org.qf.qcri.humane.tweetcollector.persist.cassandra.LogCollectionActivity;
import org.qf.qcri.humane.tweetcollector.twitter.TweetCollector;
import org.qf.qcri.humane.tweetcollector.util.ReadWriteProperties;

import twitter4j.TwitterException;

public class TweetCollectorServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static String EVENT_ID;
    Thread t;
	private long sleepTime;
	private boolean abort = false;
	public TweetCollectorServlet()
	{
		System.out.println("I am inside constructor of tweetcollector servlet");
	}
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		log("Initialized the Tweet Collector status servlet");
	}
	
    /**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 
	  public void doGet(HttpServletRequest request,
              HttpServletResponse response)
throws ServletException, IOException {
		
		  try{ 
			  LogCollectionActivity LogActivity = new LogCollectionActivity("twitterresearch","collectionlog");
			  StringBuffer msg= LogActivity.getLogCollectionData("collectionlog", EVENT_ID);
			  request.setAttribute("message", msg);
		  		} catch (Exception ex) {
		  			request.setAttribute("message", "There was an error: " + ex.getMessage());
		  		}
		  	getServletContext().getRequestDispatcher("/admin.jsp").forward(request, response);

}
*/

	  public void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		boolean initialized = false;
		try {
			// parses the request's content to extract file data
			String universal = request.getParameter("universal");
			String supr = request.getParameter("super");
			String etype = request.getParameter("etype");
			String name = request.getParameter("name");
			String track = request.getParameter("track");
			String geo = request.getParameter("geo");
			String ck = request.getParameter("ck");
			String cs = request.getParameter("cs");
			String at = request.getParameter("at");
			String as = request.getParameter("as");
			String queryDef = track+","+geo+","+ck+","+cs+","+at+","+as;
			System.out.println(queryDef);
			Calendar cal = Calendar.getInstance();
			EVENT_ID = "ev"+System.currentTimeMillis();
			Date Start_time=cal.getTime(); 
			boolean dup= checkDuplicate(universal,supr,etype,name,track,geo,ck,cs,at,as);
			System.out.println("duplicate value is"+dup);
			if (!dup)
			{	
			ReadWriteProperties rp = new ReadWriteProperties(universal,supr,etype,name,track,geo,ck,cs,at,as);
			LogCollectionActivity LogActivity = new LogCollectionActivity("twitterresearch","collectionlog");
			LogActivity.set(EVENT_ID,universal,supr,etype,name,track,geo,ck,cs, at, as, Start_time.toString(), "Currently Active");
			TweetCollector collector = (TweetCollector)getServletContext().getAttribute(TweetCollector.class.getName());
			if(collector!=null) {
				initialized = true;
				//we can do the book keeping work here
				
			}
			
			// start the collection thread
			
			collector= new TweetCollector(EVENT_ID);
			 t = new Thread(collector);
			
			System.out.println("Starting collector thread...");
			while (!isAborted()) {
			t.start();
			request.setAttribute("message", "success");
			Thread.sleep(200);
			
			
			}
			
			}//end of !dup
			

			
		} catch (Exception ex) {
			
		}
		getServletContext().getRequestDispatcher("/admin.jsp").forward(request, response);
	  }
	
	private boolean checkDuplicate(String universal, String supr, String etype,
			String name, String track, String geo, String ck, String cs,
			String at, String as) {
		// TODO Auto-generated method stub
		StringSerializer ss = StringSerializer.get();
		Keyspace keyspace;
		LogCollectionActivity lca= new LogCollectionActivity("twitterresearch","collectionlog");
		CassandraSchema cassandraSchema = new CassandraSchema("twitterresearch");
		keyspace = cassandraSchema.getKeyspace();
		RangeSlicesQuery<String, String, String> rangeSlicesQuery = HFactory.createRangeSlicesQuery(keyspace, ss, ss, ss);
		rangeSlicesQuery.setColumnFamily("collectionlog");
		rangeSlicesQuery.setRange("", "", true, 1);
		QueryResult<OrderedRows<String, String, String>> result = rangeSlicesQuery.execute();
		String uni=null;
		String sup=null;
		String event=null;
		String eventname=null;
		String crit=null;
		String loc=null;
		String stime=null;
		String etime=null;
		for (Row <String, String, String> row : result.get().getList() ) {
			 uni=row.getColumnSlice().getColumnByName("ACategory").getValue();
			 sup=row.getColumnSlice().getColumnByName("BType").getValue();
			 event= row.getColumnSlice().getColumnByName("EType").getValue();
			 eventname= row.getColumnSlice().getColumnByName("EName").getValue();
			 crit=row.getColumnSlice().getColumnByName("Filtering criteria on keywords").getValue();
			 loc= row.getColumnSlice().getColumnByName("Filtering criteria on Location").getValue();
			 stime= row.getColumnSlice().getColumnByName("Starttime").getValue();
			 etime= row.getColumnSlice().getColumnByName("StopTime").getValue();
			 }
		System.out.println("event name is passed as parameter "+ name);
		System.out.println("event name isretrieved "+ eventname);
		if (universal.equals(uni) && supr.equals(sup) && etype.equals(event) && name.equals(eventname) && track.equals(crit) && geo.equals(loc))
		{
			return true;
		}

		return false;
	}



	public synchronized void stopCollecting(String key) {
		LogCollectionActivity LogActivity = new LogCollectionActivity("twitterresearch","collectionlog");
		Cluster cluster = HFactory.getOrCreateCluster("Test Cluster", "localhost:9160");
		 Keyspace keyspace = HFactory.createKeyspace("twitterresearch", cluster);
		 ColumnFamilyTemplate<String, String> template =new ThriftColumnFamilyTemplate <String, String>(keyspace,"collectionlog",StringSerializer.get(),StringSerializer.get());
		 ColumnFamilyUpdater<String, String> updater = template.createUpdater(key);
		 Calendar cal = Calendar.getInstance();
		 String sTime = cal.getTime().toString();
		 updater.setString("StopTime", sTime);
		
		 try {
		     template.update(updater);
		 } catch (HectorException e) {
			 e.printStackTrace();
		     // do something ...
		 }
		 	//System.out.println(Thread.currentThread().activeCount());
		 
			t.interrupt();
			this.abort = true;
			
	}

	private synchronized boolean isAborted() {
		return this.abort;
	}

}
