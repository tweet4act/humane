package org.qf.qcri.humane.tweetcollector.persist.cassandra;

import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.HCounterColumn;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.CounterQuery;
import me.prettyprint.hector.api.query.QueryResult;

/*
 * CassandraPersistentEventCounters is responsible for holding the count information about the
 * number of tweets collected for each collection task  
 */
public class CassandraPersistentEventCounters {
	final Keyspace keyspace;
	
	String columnFamilyName;
	
	StringSerializer ss = StringSerializer.get();
	LongSerializer ls = LongSerializer.get();

	public CassandraPersistentEventCounters() {
		CassandraSchema cassandraSchema = new CassandraSchema();
		this.keyspace = cassandraSchema.getKeyspace();
	}

	public CassandraPersistentEventCounters(String columnFamilyName) {
		CassandraSchema cassandraSchema = new CassandraSchema();
		this.keyspace = cassandraSchema.getKeyspace();
		this.setColumnFamilyName(columnFamilyName);
	}

	public CassandraPersistentEventCounters(String keyspaceName, String columnFamilyName) {
		CassandraSchema cassandraSchema = new CassandraSchema(keyspaceName);
		this.keyspace = cassandraSchema.getKeyspace();
		this.setColumnFamilyName(columnFamilyName);
	}
	
	public String getColumnFamilyName() {
		return columnFamilyName;
	}

	public void setColumnFamilyName(String columnFamilyName) {
		if (CassandraSchema.isValidColumnFamilyNameEventCounters(columnFamilyName)) {
			this.columnFamilyName = columnFamilyName;
		} else {
			throw new IllegalArgumentException("Not a valid column family name for content: " + columnFamilyName);
		}
	}
	
	public void set(String part, String key, long counter) {
		if (columnFamilyName == null) {
			throw new IllegalStateException("Must set columnFamilyName first");
		}

		// This mutator will act over UTF8 keys (urls)
		Mutator<String> mutator = HFactory.createMutator(keyspace, ss);
		
		mutator.insertCounter(key, columnFamilyName, HFactory.createCounterColumn(part, counter));
		mutator.execute(); 
	}
	
	public void increment(String part, String key, long increment) {
		if (columnFamilyName == null) {
			throw new IllegalStateException("Must set columnFamilyName first");
		}

		// This mutator will act over UTF8 keys (urls)
		Mutator<String> mutator = HFactory.createMutator(keyspace, ss);
		
		mutator.incrementCounter(key, columnFamilyName, part, increment);
	
		mutator.execute();
	}
	
	public Long get(String part, String key) {
		CounterQuery<String, String> counterQuery = HFactory.createCounterColumnQuery(keyspace,  ss, ss);
		counterQuery.setColumnFamily(columnFamilyName);
		counterQuery.setKey(key);
		counterQuery.setName(part);
		QueryResult<HCounterColumn<String>> results = counterQuery.execute();
		
		HCounterColumn<String> column = results.get();
		
		return column.getValue();
	
		
	}

}
