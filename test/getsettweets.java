import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.beans.Rows;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;

import org.json.JSONObject;
import org.qf.qcri.humane.tweetcollector.persist.cassandra.CassandraPersistentCollectionTask;
import org.qf.qcri.humane.tweetcollector.persist.cassandra.CassandraPersistentEventCounters;
import org.qf.qcri.humane.tweetcollector.persist.cassandra.CassandraPersistentTweet;
import org.qf.qcri.humane.tweetcollector.persist.cassandra.CassandraSchema;


public class getsettweets {
	
	public static void main (String[] args)
	{
		FileWriter fstream =null;
		BufferedWriter out =null;
     	try {
			 fstream = new FileWriter("out.txt");
			  out = new BufferedWriter(fstream);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        CassandraPersistentTweet cpt= new CassandraPersistentTweet("twitterresearch","tweetcollector");
    	LongSerializer ls = LongSerializer.get();
    	final Keyspace keyspace;
		CassandraPersistentCollectionTask cpct= new CassandraPersistentCollectionTask("twitterresearch","collectiontask");
		CassandraSchema cassandraSchema = new CassandraSchema("twitterresearch");
		keyspace = cassandraSchema.getKeyspace();
		StringSerializer ss = StringSerializer.get();
		RangeSlicesQuery<String, String, String> rangeSlicesQuery = HFactory.createRangeSlicesQuery(keyspace, ss, ss, ss);
		rangeSlicesQuery.setColumnFamily("collectiontask");
		
		rangeSlicesQuery.setRange("", "", false, 3000);
		QueryResult<OrderedRows<String, String, String>> result = rangeSlicesQuery.execute();
		String previousColName="";
		for (Row<String, String, String> row : result.get().getList()) { 
		int i=0;
		CassandraPersistentEventCounters cpc= new CassandraPersistentEventCounters("twitterresearch","eventcounter");
		Long range= cpc.get("counter", row.getColumnSlice().getColumnByName("CTaskID").getValue());
		List<String> values = new ArrayList<String>();
	
		 TreeMap<Long, String> tweetSet = cpt.getLast(row.getColumnSlice().getColumnByName("CTaskID").getValue(), 100);
		 for(Map.Entry<Long,String> entry: tweetSet.entrySet())
		 {
			 String text = "";
			 String id="";
			 String date="";
			 try{
				 Long key = entry.getKey();
			 
			 //String jsonObj=entry.getValue();
			 JSONObject jsonobj = new JSONObject(entry.getValue());
			 text=jsonobj.get("text").toString();
			 id=jsonobj.get("tweetID").toString();
			 date= jsonobj.get("createdAt").toString();
			 }catch (Exception e)
			 {
				 e.printStackTrace();
			 }
				i=i+1;
				
			//preety printing
			text = text+"\t id:"+id+"\t Date:"+date+"\n";
			try {
				out.write(text);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			values.add(text);
			System.out.println(text);
			
			
			
		 }
		 try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}
	
	

}
