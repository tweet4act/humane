package org.qf.qcri.humane.tweetfeatureextraction;

import static org.junit.Assert.*;

import java.util.TreeMap;

import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;

import org.junit.Test;
import org.qf.qcri.humane.tweetcollector.persist.cassandra.CassandraPersistentTweet;
import org.qf.qcri.humane.tweetcollector.persist.cassandra.CassandraSchema;

public class CassandraPersistentFeatureExtractorTest {


	@Test
	public void testSetGet() {
		CassandraSchemaTest.clearKeyspace(CassandraSchemaTest.TEST_KEYSPACENAME);
		
		//CassandraPersistentTweet cpfe = new CassandraPersistentTweet(CassandraSchemaTest.TEST_KEYSPACENAME, CassandraSchema.COLUMNFAMILY_NAME_FEATURE_EXTRACTOR);
		CassandraPersistentFeatureExtractor cpfe= new CassandraPersistentFeatureExtractor(CassandraSchemaTest.TEST_KEYSPACENAME,CassandraSchema.COLUMNFAMILY_NAME_FEATURE_EXTRACTOR);
		assertTrue(cpfe != null);
		
		cpfe.set("testCollection", 2342l, "testExtracted");
		
		
		TreeMap<Long, String> result = cpfe.get("testCollection");
		assertEquals(1, result.size());
		assertEquals("testExtracted", result.get(new Long(2342)) );
		
	}
	
	@Test
	public void getTest()
	{
		TreeMap<Long, String> tweetSet = null;
		CassandraPersistentFeatureExtractor cpf = new CassandraPersistentFeatureExtractor(CassandraSchema.DEFAULT_KEYSPACE_NAME,CassandraSchema.COLUMNFAMILY_NAME_FEATURE_EXTRACTOR);
		CassandraSchema cassandraSchema = new CassandraSchema(CassandraSchema.DEFAULT_KEYSPACE_NAME);
		Keyspace keyspace = cassandraSchema.getKeyspace();
		StringSerializer ss = null;
		LongSerializer ls = null;
		RangeSlicesQuery<String, String, String> rangeSlicesQuery = HFactory.createRangeSlicesQuery(keyspace, ss, ss, ss);
		rangeSlicesQuery.setColumnFamily(CassandraSchema.COLUMNFAMILY_NAME_FEATURE_EXTRACTOR);
		
		rangeSlicesQuery.setRange("", "", false, 3000);
		
		QueryResult<OrderedRows<String, String, String>> result = rangeSlicesQuery.execute();
		//CassandraPersistentFeatureExtractor cpfe = new CassandraPersistentFeatureExtractor(CassandraSchema.DEFAULT_KEYSPACE_NAME,CassandraSchema.COLUMNFAMILY_NAME_FEATURE_EXTRACTOR);
		
		for (Row<String, String, String> row : result.get().getList()) { 

			tweetSet = cpf.getLast(row.getKey().toString(), 50);
		}
		
		
		System.out.println(tweetSet);
	}

	
}
