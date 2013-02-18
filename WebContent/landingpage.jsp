<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"
	import="org.qf.qcri.humane.tweetcollector.persist.cassandra.CassandraPersistentCollectionTask"
	import="org.qf.qcri.humane.tweetcollector.persist.cassandra.CassandraSchema"
	import="org.qf.qcri.humane.tweetcollector.servlet.CollectorServlet"
	import="java.util.*"
	import= "java.util.ArrayList"
	import="java.util.Arrays"
	import="java.util.List"
	import="java.util.Random"
	import="org.json.JSONObject"
	import="org.qf.qcri.humane.tweetcollector.persist.cassandra.CassandraPersistentEventCounters"
	import="org.qf.qcri.humane.tweetcollector.persist.cassandra.CassandraPersistentTweet"
	import="me.prettyprint.cassandra.serializers.LongSerializer"
	import="me.prettyprint.cassandra.serializers.StringSerializer"
	import="me.prettyprint.hector.api.Keyspace"
	import="me.prettyprint.hector.api.beans.HColumn"
	import="me.prettyprint.hector.api.beans.OrderedRows"
	import="me.prettyprint.hector.api.beans.Row"
	import="me.prettyprint.hector.api.factory.HFactory"
	import="me.prettyprint.hector.api.mutation.Mutator"
	import="me.prettyprint.hector.api.query.QueryResult"
	import="me.prettyprint.hector.api.query.RangeSlicesQuery"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Humane Tweet Collector Admin</title>
<link href="bootstrap/css/bootstrap.min.css" rel="stylesheet">
<script src="bootstrap/js/bootstrap.min.js"></script>
<script src="bootstrap/js/bootstrap.js"></script>
<style type="text/css" title="currentStyle">
			@import "bootstrap/css/demo_page.css";
			@import "bootstrap/css/demo_table.css";
		</style>  
<script type="text/javascript" language="javascript" src="bootstrap/js/jquery.js"></script>
<script type="text/javascript" language="javascript" src="bootstrap/js/jquery.dataTables.js"></script>
</head>
<body>
	<div class="navbar">
		<div class="navbar-inner">
			<div class="container">
				<!-- brand class is from bootstrap.css  -->
				<a class="brand" href="#">Humane Tweet Collector</a>
				
				<!-- /.nav-collapse -->
			</div>
		</div>
		<!-- /navbar-inner -->
	</div>
	<!-- /navbar -->
		
	
<div class="container">
	<div class="hero-unit">
        
	   <h3>  Welcome Admin! </h3>
	<form method="post" action="collection.jsp">
	      <div class ="controls">
          <button type="submit" class="btn btn-success">
  					Create New Collection
		 </button>
		 </div>
	  </form>
	</div>
	<div>
	<table  cellpadding="0" cellspacing="0" border="0" class="display dataTable" id="historyData">
                    <thead>
                        <tr>
                            
                            <th>Collection Name</th>
                            <th>Filtering criteria on keywords</th>
                            <th>Filtering criteria on Location</th>
                            <th>Start Date</th>
                            <th>Stop Date</th>
                            <th>Counter</th>
                            <th>Last 5 tweets</th>
                        </tr>
                    </thead>
                    <tbody>
                    <%
                	StringSerializer ss = StringSerializer.get();
                    CassandraPersistentTweet cpt= new CassandraPersistentTweet("twitterresearch","tweetcollector");
                	LongSerializer ls = LongSerializer.get();
                	final Keyspace keyspace;
        			CassandraPersistentCollectionTask cpct= new CassandraPersistentCollectionTask("twitterresearch","collectiontask");
        			CassandraSchema cassandraSchema = new CassandraSchema("twitterresearch");
        			keyspace = cassandraSchema.getKeyspace();
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
				
					 TreeMap<Long, String> tweetSet = cpt.getLast(row.getColumnSlice().getColumnByName("CTaskID").getValue(), 5);
					 for(Map.Entry<Long,String> entry: tweetSet.entrySet())
					 {
						 Long key = entry.getKey();
						 //String jsonObj=entry.getValue();
						 JSONObject jsonobj = new JSONObject(entry.getValue());
						 String text=jsonobj.get("text").toString();
						 String id=jsonobj.get("tweetID").toString();
						 String date= jsonobj.get("createdAt").toString();
							i=i+1;
							//preety printing
						text="("+i+")\t"+text+"\t id:"+id+"\t Date:"+date+"</br>";
						values.add(text);
						
						
					 }
					 String a=Arrays.toString(values.toArray());
					  %>
						<tr class="gradeA">
						
						<td>
						<%=row.getColumnSlice().getColumnByName("ColName").getValue()%>
						</td>
						<td>
						<%=row.getColumnSlice().getColumnByName("Track").getValue()%>
						</td>
						<td>
						<%=row.getColumnSlice().getColumnByName("Geo").getValue()%>
						</td>
						<td>
						<%=row.getColumnSlice().getColumnByName("SDate").getValue()%>
						</td>
						<td>
						<%=row.getColumnSlice().getColumnByName("EDate").getValue()%>
						</td>
						<td>
						<%=range%>
						</td>
						<td id="123">
						<%=a%>
						</td>
						<td>
						<form method="post" action="TweetCollectorStopServlet">
          				<INPUT TYPE="HIDDEN" NAME="interupt" value="interrupted">
          				<INPUT TYPE="HIDDEN" NAME="latest" value ="<%=CollectorServlet.COLLECTION_ID%>">			
						<div class ="controls"> 
						<button type="submit" class="btn btn-warning">
  						 Stop Collection
						</button>
						</div>
						</form>
						
						</td>
						</tr>
						

						

						
			<%  } 
			
			%>
			
	
</tbody>
</table>
</div>

</div>
 <script src="bootstrap/js/jquery.js"></script>
 <script src="bootstrap/js/bootstrap-transition.js"></script>
<script src="bootstrap/js/bootstrap-tab.js"></script>
</body>

</html>