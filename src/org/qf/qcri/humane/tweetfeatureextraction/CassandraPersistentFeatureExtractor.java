package org.qf.qcri.humane.tweetfeatureextraction;

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

import org.qf.qcri.humane.tweetcollector.persist.cassandra.CassandraSchema;
/*
 * CassandraPersistentFeatureExtractor defines the schema of the column family that holds the information about 
 * the extracted feature from each tweet in the cassandra db	
 */
	public class CassandraPersistentFeatureExtractor {
		
		
		private static final long serialVersionUID=1;
		public static final String COLLECTION_TASK_ID = "CTaskID";
		
		
		final public static int MAX_TWEETS = Integer.MAX_VALUE;


		@SuppressWarnings("unused")
		private static final String COLLECTION_EXTRACTOR_VERSION = null;

		@SuppressWarnings("unused")
		private static  String COLLECTION_TWEETID = null;

		@SuppressWarnings("unused")
		private static  String COLLECTION_EXTRACTED_FEATURE = null;
		
		private Keyspace keyspace;
		String columnFamilyName = CassandraSchema.COLUMNFAMILY_NAME_FEATURE_EXTRACTOR;

		StringSerializer ss = StringSerializer.get();

		LongSerializer ls = LongSerializer.get();

		
		public CassandraPersistentFeatureExtractor(String keyspaceName, String columnFamilyName) {
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
		
		public void set(String collectionid,Long tweetid, String extractedfeature) {
			if (columnFamilyName == null) {
				throw new IllegalStateException("Must setColumnFamilyName() first");
			}
			
			// This mutator will act over UTF8 keys (urls)
			Mutator<String> mutator = HFactory.createMutator(keyspace, ss);

			// The column sub-key is a long
			HColumn<Long, String> column = HFactory.createColumn(tweetid, extractedfeature);

			mutator.insert(collectionid, columnFamilyName, column);
			mutator.execute();
			

			
		}
		
		public String getColumnFamilyName() {
			return columnFamilyName;
		}

		public static long getSerialversionuid() {
			return serialVersionUID;
		}


}

