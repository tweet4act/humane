package org.qf.qcri.humane.tweetfeatureextraction;

import org.qf.qcri.humane.tweetcollector.persist.cassandra.CassandraSchema;

import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.exceptions.HInvalidRequestException;

public class CassandraSchemaTest {
	public static final String TEST_KEYSPACENAME = "TestKeyspace";
	
	public static void clearKeyspace(String keyspaceName) {
		// Start with empty keyspace
		Cluster cluster = CassandraSchema.getCluster();
		try {
			cluster.dropKeyspace(keyspaceName, true);
			
			// If this is not null, the keyspace was not deleted
			if( cluster.describeKeyspace(keyspaceName) != null ) {
				throw new IllegalStateException("The key space was not deleted");
			}

		} catch (HInvalidRequestException e) {
			e.printStackTrace();
		}
	}
}
