package org.qf.qcri.humane.tweetcollector.persist.cassandra;

import java.util.Arrays;

import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;

public class LogCollectionActivity {

	StringSerializer ss = StringSerializer.get();

	LongSerializer ls = LongSerializer.get();
	private Keyspace keyspace;
	String columnFamilyName = "collectionlog";
	public LogCollectionActivity(String keyspaceName, String columnFamilyName) {
		// TODO Auto-generated constructor stub
		CassandraSchema cassandraSchema = new CassandraSchema(keyspaceName);
		this.keyspace = cassandraSchema.getKeyspace();
		this.setColumnFamilyName(columnFamilyName);
	
	}

	private String setColumnFamilyName(String columnFamilyName) {
		// TODO Auto-generated method stub
		return columnFamilyName;
		
	}
	
/*
 * Function to get the Log collection information to be shown in the UI
 */
	
	public StringBuffer getLogCollectionData(String columnFamilyName, String key){
	    //Following is an example of an Slice query to get all rows
		StringBuffer buf = new StringBuffer(); 
		RangeSlicesQuery<String, String, String> rangeSlicesQuery = HFactory
				.createRangeSlicesQuery(keyspace, ss, ss, ss);
		rangeSlicesQuery.setColumnFamily(columnFamilyName);

		rangeSlicesQuery.setRange("", "", false,20);
		QueryResult<OrderedRows<String, String, String>> result = rangeSlicesQuery
				.execute();
		buf.append("[");
		for (Row<String, String, String> row : result.get().getList()) {
	        
			CassandraPersistentEventCounters cpc= new CassandraPersistentEventCounters("twitterresearch","eventcounter");
			Long range= cpc.get("counter", row.getKey());
			buf.append("[");
			for (HColumn<String, String> hc : row.getColumnSlice().getColumns()) {
				
				buf.append("\'"+hc.getValue()+"\',"); 
			}
			
			buf.append(range);
			buf.append("],");
			//System.out.println(buf.toString());
		}
		buf.deleteCharAt(buf.lastIndexOf(","));
		buf.append("]");
		System.out.println(buf.toString());
		return buf;
	}
	
	public void set(String eVENT_ID, String universal, String supr,String etype, String name, String track, String geo, String ck,String cs, String at, String as,String starttime, String stoptime) {

	
		Mutator<String> mutator = HFactory.createMutator(keyspace, ss);
		//named them for the ordering
		//universal type 
		mutator.insert(eVENT_ID, columnFamilyName,
				HFactory.createStringColumn("ACategory", universal));
		//super cluster
		mutator.insert(eVENT_ID, columnFamilyName,
				HFactory.createStringColumn("BType", supr));
		//disaster type
		mutator.insert(eVENT_ID, columnFamilyName,
				HFactory.createStringColumn("EType", etype));
		//disaster instance name
		mutator.insert(eVENT_ID, columnFamilyName,
				HFactory.createStringColumn("EName", name));
		//filtering track
		mutator.insert(eVENT_ID, columnFamilyName,
				HFactory.createStringColumn("Filtering criteria on keywords", track));
		mutator.insert(eVENT_ID, columnFamilyName,
				HFactory.createStringColumn("Filtering criteria on Location", geo));
		mutator.insert(eVENT_ID, columnFamilyName,
				HFactory.createStringColumn("customer key", ck));
		mutator.insert(eVENT_ID, columnFamilyName,
				HFactory.createStringColumn("customer secret", cs));
		mutator.insert(eVENT_ID, columnFamilyName,
				HFactory.createStringColumn("authentication type", at));
		mutator.insert(eVENT_ID, columnFamilyName,
				HFactory.createStringColumn("authentication secret", as));
		mutator.insert(eVENT_ID, columnFamilyName,
				HFactory.createStringColumn("Starttime", starttime));
		mutator.insert(eVENT_ID, columnFamilyName,
				HFactory.createStringColumn("StopTime", stoptime));


		mutator.execute();  
		
	}


}
