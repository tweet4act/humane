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
 * CassandraPersistentCollection class defines the structure of column family named COLLECTION 
 * inside the cassandra db 
 */
public class CassandraPersistentCollection {

	final public static int MAX_TWEETS = Integer.MAX_VALUE;
	final public static String COLLECTION_NAME = "CName";
	final public static String COLLECTION_UNIVERSAL_TYPE = "UType";
	final public static String COLLECTION_SUPER_TYPE = "SType";
	final public static String COLLECTION_TYPE = "Type";
	final public static String COLLECTION_CONSUMER_KEY = "ConsKey";
	final public static String COLLECTION_CONSUMER_SECRET = "ConsSec";
	final public static String COLLECTION_AUTHENTICATION_TYPE = "AuthType";
	final public static String COLLECTION_AUTHENTICATION_SECRET = "AuthSec";
	
	private Keyspace keyspace;
	String columnFamilyName = CassandraSchema.COLUMNFAMILY_NAME_COLLECTION;

	StringSerializer ss = StringSerializer.get();

	LongSerializer ls = LongSerializer.get();

	
	public CassandraPersistentCollection(String keyspaceName, String columnFamilyName) {
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
	
	public void set(String collectionid, String cname, String universaltype,String supertype, String type,String ck,String cs, String at, String as) {

		
		Mutator<String> mutator = HFactory.createMutator(keyspace, ss);
		//named them for the ordering
	
		mutator.insert(collectionid, columnFamilyName,
				HFactory.createStringColumn(COLLECTION_NAME, cname));
		
		mutator.insert(collectionid, columnFamilyName,
				HFactory.createStringColumn(COLLECTION_UNIVERSAL_TYPE, universaltype));
		
		mutator.insert(collectionid, columnFamilyName,
				HFactory.createStringColumn(COLLECTION_SUPER_TYPE, supertype));
		
		mutator.insert(collectionid, columnFamilyName,
				HFactory.createStringColumn(COLLECTION_TYPE, type));
		mutator.insert(collectionid, columnFamilyName,
				HFactory.createStringColumn(COLLECTION_CONSUMER_KEY, ck));
		mutator.insert(collectionid, columnFamilyName,
				HFactory.createStringColumn(COLLECTION_CONSUMER_SECRET, cs));
		mutator.insert(collectionid, columnFamilyName,
				HFactory.createStringColumn(COLLECTION_AUTHENTICATION_TYPE, at));
		mutator.insert(collectionid, columnFamilyName,
				HFactory.createStringColumn(COLLECTION_AUTHENTICATION_SECRET, as));

		mutator.execute();  
		
		System.out.println("CPC is set");
	}


}
