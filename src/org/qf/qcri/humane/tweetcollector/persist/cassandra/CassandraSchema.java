package org.qf.qcri.humane.tweetcollector.persist.cassandra;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.factory.HFactory;

/*
 * CassandraSchema defines the cluster, keyspace and columnfamilies that are used for the whole tweet collection
 * process.
 */
public class CassandraSchema {

	private static String CLUSTER_NAME = "Test Cluster";

	public static String DEFAULT_KEYSPACE_NAME = "twitterresearch";


	public static final int CASSANDRA_PORT =9160;
	
    public static final String CASSANDRA_HOST = "localhost";
	
	public static final String COLUMNFAMILY_NAME_TWEETS = "tweetcollector";
	
	public static final String COLUMNFAMILY_NAME_CONTENT = "collectionlog";
	
	public static final String COLUMNFAMILY_NAME_EVENT_COUNTERS = "eventcounter";
	
	public static final String COLUMNFAMILY_NAME_COLLECTION = "collection";
	
	public static final String COLUMNFAMILY_NAME_COLLECTION_TASK = "collectiontask";
	
	public static final String COLUMNFAMILY_NAME_FEATURE_EXTRACTOR = "featureextractor";

	final Cluster cluster;

	final Keyspace keyspace;

	public CassandraSchema() {
		this(DEFAULT_KEYSPACE_NAME);
	}

	public CassandraSchema(String keyspaceName) {
		cluster = getCluster();
		if (cluster != null) {
		
		} else {
			throw new IllegalStateException("Could not open cluster in '" + CASSANDRA_HOST + "' port " + CASSANDRA_PORT );
		}

		KeyspaceDefinition keyspaceDef = cluster.describeKeyspace(keyspaceName);

		// If the keyspace does not exist, create it
		if (keyspaceDef == null) {
			KeyspaceDefinition newKeyspace = HFactory.createKeyspaceDefinition(keyspaceName);
			cluster.addKeyspace(newKeyspace, true);
		} else {
			
		}

		// Load keyspace
		keyspace = HFactory.createKeyspace(keyspaceName, cluster);



		if (!hasColumnFamily(COLUMNFAMILY_NAME_TWEETS)) {
		 //Utils.logDebug(this, "Will try to create column family " + COLUMNFAMILY_NAME_TWEETS);
			createTweetsColumnFamily(COLUMNFAMILY_NAME_TWEETS);
			if (!hasColumnFamily(COLUMNFAMILY_NAME_TWEETS)) {
				//Util.logWarning(this, "Failed to create column family " + COLUMNFAMILY_NAME_TWEETS);
				throw new IllegalStateException("Could not create " + COLUMNFAMILY_NAME_TWEETS);
			}
		}
		
		if (!hasColumnFamily(COLUMNFAMILY_NAME_CONTENT)) {
			//Utils.logDebug(this, "Will try to create column family " + COLUMNFAMILY_NAME_CONTENT);
			createContentColumnFamily(COLUMNFAMILY_NAME_CONTENT);
			if (!hasColumnFamily(COLUMNFAMILY_NAME_CONTENT)) {
				//Utils.logWarning(this, "Failed to create column family " + COLUMNFAMILY_NAME_CONTENT);
				throw new IllegalStateException("Could not create " + COLUMNFAMILY_NAME_CONTENT);
			}
			

		}
		if (!hasColumnFamily(COLUMNFAMILY_NAME_EVENT_COUNTERS)) {
			Utils.logDebug(this, "Will try to create column family " + COLUMNFAMILY_NAME_EVENT_COUNTERS);
			createCounterColumnFamily(COLUMNFAMILY_NAME_EVENT_COUNTERS);
			if (!hasColumnFamily(COLUMNFAMILY_NAME_EVENT_COUNTERS)) {
				Utils.logWarning(this, "Failed to create column family " + COLUMNFAMILY_NAME_EVENT_COUNTERS);
				throw new IllegalStateException("Could not create " + COLUMNFAMILY_NAME_EVENT_COUNTERS);
			}
		}
		if (!hasColumnFamily(COLUMNFAMILY_NAME_COLLECTION)) {
			Utils.logDebug(this, "Will try to create column family " + COLUMNFAMILY_NAME_COLLECTION);
			createCollectionColumnFamily(COLUMNFAMILY_NAME_COLLECTION);
			if (!hasColumnFamily(COLUMNFAMILY_NAME_COLLECTION)) {
				Utils.logWarning(this, "Failed to create column family " + COLUMNFAMILY_NAME_COLLECTION);
				throw new IllegalStateException("Could not create " + COLUMNFAMILY_NAME_COLLECTION);
			}
		}
		if (!hasColumnFamily(COLUMNFAMILY_NAME_COLLECTION_TASK)) {
			Utils.logDebug(this, "Will try to create column family " + COLUMNFAMILY_NAME_COLLECTION_TASK);
			createCollectionTaskColumnFamily(COLUMNFAMILY_NAME_COLLECTION_TASK);
			if (!hasColumnFamily(COLUMNFAMILY_NAME_COLLECTION_TASK)) {
				Utils.logWarning(this, "Failed to create column family " + COLUMNFAMILY_NAME_COLLECTION_TASK);
				throw new IllegalStateException("Could not create " + COLUMNFAMILY_NAME_COLLECTION_TASK);
			}
		}
		if (!hasColumnFamily(COLUMNFAMILY_NAME_FEATURE_EXTRACTOR)) {
			Utils.logDebug(this, "Will try to create column family " + COLUMNFAMILY_NAME_FEATURE_EXTRACTOR);
			createFeatureExtractorColumnFamily(COLUMNFAMILY_NAME_FEATURE_EXTRACTOR);
			if (!hasColumnFamily(COLUMNFAMILY_NAME_FEATURE_EXTRACTOR)) {
				Utils.logWarning(this, "Failed to create column family " + COLUMNFAMILY_NAME_FEATURE_EXTRACTOR);
				throw new IllegalStateException("Could not create " + COLUMNFAMILY_NAME_FEATURE_EXTRACTOR);
			}
		}

	}

	public static Cluster getCluster() {
		CassandraHostConfigurator config = new CassandraHostConfigurator();
        config.setHosts(CASSANDRA_HOST);
        config.setPort(CASSANDRA_PORT);
		return HFactory.getOrCreateCluster(CLUSTER_NAME, config, new ConcurrentHashMap<String, String>());
	}

	public Keyspace getKeyspace() {
		return keyspace;
	}

	private boolean hasColumnFamily(String columnFamilyName) {
		KeyspaceDefinition keyspaceDef = cluster.describeKeyspace(keyspace.getKeyspaceName());

		List<ColumnFamilyDefinition> columns = keyspaceDef.getCfDefs();
		for (ColumnFamilyDefinition cfDef : columns) {
			if (cfDef.getName().equals(columnFamilyName)) {
				return true;
			}
		}
		return false;
	}
	private void createCounterColumnFamily(String columnFamilyName) {
		
		ColumnFamilyDefinition cfDef = HFactory.createColumnFamilyDefinition(keyspace.getKeyspaceName(), columnFamilyName, ComparatorType.UTF8TYPE);

		// The values are counters
		cfDef.setDefaultValidationClass(ComparatorType.COUNTERTYPE.getClassName());

		String rc = cluster.addColumnFamily(cfDef, true);

		Utils.logDebug(this, "Result of adding '" + cfDef.getName() + "' to '" + keyspace.getKeyspaceName() + "': " + rc);
	}


	private void createTweetsColumnFamily(String columnFamilyName) {
		// Create a column
		ColumnFamilyDefinition cfDef = HFactory.createColumnFamilyDefinition(keyspace.getKeyspaceName(), columnFamilyName, ComparatorType.LONGTYPE);
		
		cluster.addColumnFamily(cfDef, true);

		//Utils.logDebug(this, "Result of adding '" + cfDef.getName() + "' to '" + keyspace.getKeyspaceName() + "': " + rc);
	}
	
	private void createContentColumnFamily(String columnFamilyName) {
		// Create a column
		ColumnFamilyDefinition cfDef = HFactory.createColumnFamilyDefinition(keyspace.getKeyspaceName(), columnFamilyName, ComparatorType.UTF8TYPE);

		cluster.addColumnFamily(cfDef, true);

	   //Utils.logDebug(this, "Result of adding '" + cfDef.getName() + "' to '" + keyspace.getKeyspaceName() + "': " + rc);
	}
	
	private void createCollectionColumnFamily(String columnFamilyName) {
		// Create a column
		ColumnFamilyDefinition cfDef = HFactory.createColumnFamilyDefinition(keyspace.getKeyspaceName(), columnFamilyName, ComparatorType.UTF8TYPE);
		
		cluster.addColumnFamily(cfDef, true);

	}
	private void createCollectionTaskColumnFamily(String columnFamilyName) {
		// Create a column
		ColumnFamilyDefinition cfDef = HFactory.createColumnFamilyDefinition(keyspace.getKeyspaceName(), columnFamilyName, ComparatorType.UTF8TYPE);
		
		cluster.addColumnFamily(cfDef, true);

	}
	
	private void createFeatureExtractorColumnFamily(String columnFamilyName) {
		// Create a column
		ColumnFamilyDefinition cfDef = HFactory.createColumnFamilyDefinition(keyspace.getKeyspaceName(), columnFamilyName, ComparatorType.LONGTYPE);
		
		cluster.addColumnFamily(cfDef, true);

	}
	public static boolean isValidColumnFamilyNameContent(String columnFamilyName) {
		return columnFamilyName.equals(COLUMNFAMILY_NAME_CONTENT);
	}

	public static boolean isValidColumnFamilyNameTweets(String columnFamilyName) {
		return columnFamilyName.equals(COLUMNFAMILY_NAME_TWEETS);
	}
	public static boolean isValidColumnFamilyNameEventCounters(String columnFamilyName) {
		return (columnFamilyName.equals(COLUMNFAMILY_NAME_EVENT_COUNTERS));
	}
	public static boolean isValidColumnFamilyNameCollection(String columnFamilyName) {
		return (columnFamilyName.equals(COLUMNFAMILY_NAME_COLLECTION));
	}
	public static boolean isValidColumnFamilyNameCollectionTask(String columnFamilyName) {
		return (columnFamilyName.equals(COLUMNFAMILY_NAME_COLLECTION_TASK));
	}
	public static boolean isValidColumnFamilyNameFeatureExtractor(String columnFamilyName) {
		return (columnFamilyName.equals(COLUMNFAMILY_NAME_FEATURE_EXTRACTOR));
	}

}