package org.qf.qcri.humane.tweetcollector.servlet;

import java.util.TreeMap;

import javax.servlet.http.HttpServlet;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;

import org.qf.qcri.humane.tweetcollector.persist.cassandra.CassandraSchema;
import org.qf.qcri.humane.tweetfeatureextraction.CassandraPersistentFeatureExtractor;

/*
 * servlet that handles the tweet classifier code based upon the previously content feature extracted from  tweets
 */

public class ClassifierServlet extends HttpServlet{
	

private static final long serialVersionUID = 1L;

public void runClassifier()
{
	TreeMap<Long, String> tweetSet = null;
	CassandraPersistentFeatureExtractor cpf = new CassandraPersistentFeatureExtractor(CassandraSchema.DEFAULT_KEYSPACE_NAME,CassandraSchema.COLUMNFAMILY_NAME_FEATURE_EXTRACTOR);
	CassandraSchema cassandraSchema = new CassandraSchema(CassandraSchema.DEFAULT_KEYSPACE_NAME);
	Keyspace keyspace = cassandraSchema.getKeyspace();
	StringSerializer ss = null;
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
