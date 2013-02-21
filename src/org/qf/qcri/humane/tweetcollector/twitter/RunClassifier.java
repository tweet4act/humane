package org.qf.qcri.humane.tweetcollector.twitter;


import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;

import org.qf.qcri.humane.tweetcollector.persist.cassandra.CassandraSchema;
/*
 * RunClassifier contains the function runClassifier() which invokes the classifier code.
 * yet to be implemented
 */

public class RunClassifier {
	


public void runClassifier()
{
	CassandraSchema cassandraSchema = new CassandraSchema(CassandraSchema.DEFAULT_KEYSPACE_NAME);
	Keyspace keyspace = cassandraSchema.getKeyspace();
	StringSerializer ss = null;
	LongSerializer ls = null;
	RangeSlicesQuery<String, Long, String> rangeSlicesQuery = HFactory.createRangeSlicesQuery(keyspace, ss, ls, ss);
	rangeSlicesQuery.setColumnFamily(CassandraSchema.COLUMNFAMILY_NAME_FEATURE_EXTRACTOR);

	//rangeSlicesQuery.setRange("", "", false, 500);
	
	QueryResult<OrderedRows<String, Long, String>> result = rangeSlicesQuery.execute();

	
	
	System.out.println(result);
}

}
