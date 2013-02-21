package org.qf.qcri.humane.tweetcollector.persist.cassandra;


import java.util.TreeMap;

import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.SliceQuery;
	
/*
 * CassandraPersistentCollectionTask defines the Collection Task column family inside the cassandra db
 */
	public class CassandraPersistentCollectionTask {
		
		
		
		public static final String COLLECTION_TASK_ID = "CTaskID";
		
		public static final String COLLECTION_TASK_START_DATE = "SDate";
		
		public static final String COLLECTION_TASK_END_DATE = "EDate";
		
		public static final String COLLECTION_TASK_GEO = "Geo";
		
		public static final String COLLECTION_TASK_TRACK= "Track";
		
		public static final String COLLECTION_TASK_NAME= "CtaskName";
		final public static int MAX_TWEETS = Integer.MAX_VALUE;

		private static final String COLLECTION_NAME = "ColName";
		
		private Keyspace keyspace;
		String columnFamilyName = CassandraSchema.COLUMNFAMILY_NAME_COLLECTION_TASK;

		StringSerializer ss = StringSerializer.get();

		LongSerializer ls = LongSerializer.get();

		
		public CassandraPersistentCollectionTask(String keyspaceName, String columnFamilyName) {
			// TODO Auto-generated constructor stub
			CassandraSchema cassandraSchema = new CassandraSchema(keyspaceName);
			this.keyspace = cassandraSchema.getKeyspace();
			this.setColumnFamilyName(columnFamilyName);
		
		}

		private String setColumnFamilyName(String columnFamilyName) {
			// TODO Auto-generated method stub
			return columnFamilyName;
			
		}
		
	
		public TreeMap<Long, String> get(String key) {
			return get(key, new Long(Long.MIN_VALUE), new Long(Long.MAX_VALUE), false, MAX_TWEETS );
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
		
		public void set(String collectionid, String collectiontaskid,String collectionname, String collectiontaskname, String startdate,String enddate, String track,String geo) {

			
			Mutator<String> mutator = HFactory.createMutator(keyspace, ss);
			//named them for the ordering
		
			mutator.insert(collectionid, columnFamilyName,
					HFactory.createStringColumn(COLLECTION_TASK_ID, collectiontaskid));
			
			mutator.insert(collectionid, columnFamilyName,
					HFactory.createStringColumn(COLLECTION_NAME, collectionname));
			mutator.insert(collectionid, columnFamilyName,
					HFactory.createStringColumn(COLLECTION_TASK_NAME, collectiontaskname));
			
			mutator.insert(collectionid, columnFamilyName,
					HFactory.createStringColumn(COLLECTION_TASK_START_DATE, startdate));
			
			mutator.insert(collectionid, columnFamilyName,
					HFactory.createStringColumn(COLLECTION_TASK_END_DATE, enddate));
			
			mutator.insert(collectionid, columnFamilyName,
					HFactory.createStringColumn(COLLECTION_TASK_TRACK, track));
			mutator.insert(collectionid, columnFamilyName,
					HFactory.createStringColumn(COLLECTION_TASK_GEO, geo));


			mutator.execute();  
			
		}


}
