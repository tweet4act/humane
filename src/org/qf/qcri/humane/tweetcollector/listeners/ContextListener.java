package org.qf.qcri.humane.tweetcollector.listeners;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import me.prettyprint.hector.api.query.SliceQuery;

import org.json.JSONObject;
import org.qf.qcri.humane.tweetcollector.persist.cassandra.CassandraPersistentCollectionTask;
import org.qf.qcri.humane.tweetcollector.persist.cassandra.CassandraSchema;
import org.qf.qcri.humane.tweetcollector.persist.cassandra.LogCollectionActivity;
import org.qf.qcri.humane.tweetcollector.twitter.TweetCollector;


/*  initialized 
public class ContextListener implements ServletContextListener {


	private TweetCollector collector;
	

		ServletContext context;



	public void contextInitialized(ServletContextEvent ctx) {
		
		
			System.out.println("context is initialized");
			context = ctx.getServletContext();
			// set variable to servlet context
			context.setAttribute("TEST", "TEST_VALUE");
			
			//context listener initiation
			StringSerializer ss = StringSerializer.get();
			Keyspace keyspace;
			CassandraPersistentCollectionTask lca= new CassandraPersistentCollectionTask("twitterresearch",CassandraSchema.COLUMNFAMILY_NAME_COLLECTION_TASK);
			CassandraSchema cassandraSchema = new CassandraSchema("twitterresearch");
			keyspace = cassandraSchema.getKeyspace();
			RangeSlicesQuery<String, String, String> rangeSlicesQuery = HFactory.createRangeSlicesQuery(keyspace, ss, ss, ss);
	        rangeSlicesQuery.setColumnFamily(CassandraSchema.COLUMNFAMILY_NAME_COLLECTION_TASK);
	        rangeSlicesQuery.setRange("", "", false, 30);
	        QueryResult<OrderedRows<String, String, String>> result = rangeSlicesQuery.execute();

	        Mutator<String> mutator = HFactory.createMutator(keyspace, ss);

	        for (Row<String, String, String> row : result.get().getList()) {
	        	
	        	String etime= row.getColumnSlice().getColumnByName("EDate").getValue();
	        	if (etime.equals("Currently Active"))
	        	{
	
	   			 String crit=row.getColumnSlice().getColumnByName("Track").getValue();
	   			 String loc= row.getColumnSlice().getColumnByName("Geo").getValue();
	   			 String taskid = row.getColumnSlice().getColumnByName("CTaskID").getValue();
	   			 System.out.println(crit);
	   			 TweetCollector collector;
	   			
				try {
					
					collector = new TweetCollector(taskid);
					
					Thread t = new Thread(collector);
					t.start();
					System.out.println("Thread started");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 
				
	        	}
	            
	        }
	      
			
			
	}


	public void contextDestroyed(ServletContextEvent ctx) {
		context = ctx.getServletContext();
		System.out.println("Context Destroyed");
	}
}