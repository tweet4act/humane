package org.qf.qcri.humane.tweetcollector.persist.cassandra;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.CounterSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.HCounterColumn;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.ColumnQuery;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import me.prettyprint.hector.api.query.SliceQuery;
import me.prettyprint.hector.api.query.SubSliceCounterQuery;




public class CassandraPersistentTweet {

	
	final static String LOGS = "Logs";
	String columnFamilyName;

	final public static int MAX_TWEETS = Integer.MAX_VALUE;

	StringSerializer ss = StringSerializer.get();

	LongSerializer ls = LongSerializer.get();
	private Keyspace keyspace;
	public static ColumnFamilyTemplate<String, String> template;

	public CassandraPersistentTweet() {
		CassandraSchema cassandraSchema = new CassandraSchema();
		this.keyspace = cassandraSchema.getKeyspace();
	}

	public CassandraPersistentTweet(String columnFamilyName) {
		CassandraSchema cassandraSchema = new CassandraSchema();
		this.keyspace = cassandraSchema.getKeyspace();
		this.setColumnFamilyName(columnFamilyName);
	}

	public CassandraPersistentTweet(String keyspaceName, String columnFamilyName) {
		CassandraSchema cassandraSchema = new CassandraSchema(keyspaceName);
		this.keyspace = cassandraSchema.getKeyspace();
		this.setColumnFamilyName(columnFamilyName);
	}

 	public void set(String key, Long tweetID, String json) {

		if (columnFamilyName == null) {
			throw new IllegalStateException("Must setColumnFamilyName() first");
		}

		// This mutator will act over UTF8 keys (urls)
		Mutator<String> mutator = HFactory.createMutator(keyspace, ss);

		// The column sub-key is a long
		HColumn<Long, String> column = HFactory.createColumn(tweetID, json);

		mutator.insert(key, columnFamilyName, column);
		mutator.execute();
	}

	public TreeMap<Long, String> get(String key) {
		return get(key, new Long(Long.MIN_VALUE), new Long(Long.MAX_VALUE), false, MAX_TWEETS );
	}
	
	public TreeMap<Long, String> getFirst(String key, int number) {
		return get(key, new Long(Long.MIN_VALUE), new Long(Long.MAX_VALUE), false, number );
	}
	
	public TreeMap<Long, String> getLast(String key, int number) {
		return get(key, new Long(Long.MAX_VALUE), new Long(Long.MIN_VALUE), true, number );
	}
	
	private TreeMap<Long, String> get(String key, Long min, Long max, boolean reversed, int maxTweets) {
		if (columnFamilyName == null) {
			throw new IllegalStateException("Must setColumnFamilyName() first");
		}

		SliceQuery<String, Long, String> query = HFactory.createSliceQuery(keyspace, ss, ls, ss);
		query.setColumnFamily(columnFamilyName);
		query.setKey(key);
		query.setRange(min, max, reversed, maxTweets);
		QueryResult<ColumnSlice<Long, String>> results = query.execute();

		TreeMap<Long, String> response = new TreeMap<Long, String>();

		if (results != null) {
			ColumnSlice<Long, String> result = results.get();
			for (HColumn<Long, String> column : result.getColumns()) {
				response.put(column.getName(), column.getValue());
			}
		}
		return response;
	}

	public String getColumnFamilyName() {
		return columnFamilyName;
	}

	public void setColumnFamilyName(String columnFamilyName) {
		if (CassandraSchema.isValidColumnFamilyNameTweets(columnFamilyName)) {
			this.columnFamilyName = columnFamilyName;
		} else {
			throw new IllegalArgumentException("Not a valid column family name for tweets: " + columnFamilyName);
		}
	}
	
	    /*
		public String getRecentTweets(String key){
		    //Following is an example of an Slice query to get all columns give a specific row
			SliceQuery<String, String, String> q = HFactory.createSliceQuery(keyspace, ss, ss, ss);
			//return 5
			q.setColumnFamily(columnFamilyName).setKey(key).setRange("", "", true, 5);
			QueryResult<ColumnSlice<String, String>> r = q.execute();
			ColumnSlice<String, String> cs = r.get(); 
			List<String> values = new ArrayList<String>();
			int i=0;
			for(HColumn<String , String> hc :cs.getColumns()){
				try {
					
					JSONObject jsonobj = new JSONObject(hc.getValue());
					String text=jsonobj.get("text").toString();
					String id=jsonobj.get("tweetID").toString();
					String date= jsonobj.get("createdAt").toString();
					i=i+1;
					//preety printing
					text="("+i+")\t"+text+"\t id:"+id+"\t Date:"+date+"</br>";
					values.add(text);
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		
			//Arrays.toString(values.toArray());
			return Arrays.toString(values.toArray());
		}
		*/
}

