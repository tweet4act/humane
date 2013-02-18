<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"
	import="org.qf.qcri.humane.tweetcollector.persist.cassandra.LogCollectionActivity"
	import="org.qf.qcri.humane.tweetcollector.persist.cassandra.CassandraSchema"
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

	      <div class ="controls">
          <button type="submit" class="btn btn-success" onclick="collection.jsp">
  					Create New Collection
		 </button>
		 </div>
	  
	 
	 <div class="accordion" id="accordion2">
  <div class="accordion-group">
    <div class="accordion-heading">
      <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#collapseOne">
        Collapsible Group Item #1
      </a>
    </div>
    <div id="collapseOne" class="accordion-body collapse in">
      <div class="accordion-inner">
        Anim pariatur cliche...
      </div>
    </div>
  </div>
  <div class="accordion-group">
    <div class="accordion-heading">
      <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#collapseTwo">
        Collapsible Group Item #2
      </a>
    </div>
    <div id="collapseTwo" class="accordion-body collapse">
      <div class="accordion-inner">
        Anim pariatur cliche...
      </div>
    </div>
  </div>
</div> 
	  

<div id="history">



<table  cellpadding="0" cellspacing="0" border="0" class="display dataTable" id="historyData">
                    <thead>
                        <tr>
                            <th>Universal Category</th>
                            <th>Super Type</th>
                            <th>Event Type</th>
                            <th>Event Name</th>
                            <th>Filtering criteria on keywords</th>
                            <th>Filtering criteria on Location</th>
                            <th>Start time</th>
                            <th>Stop Time</th>
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
        			LogCollectionActivity lca= new LogCollectionActivity("twitterresearch","collectionlog");
        			CassandraSchema cassandraSchema = new CassandraSchema("twitterresearch");
        			keyspace = cassandraSchema.getKeyspace();
					RangeSlicesQuery<String, String, String> rangeSlicesQuery = HFactory.createRangeSlicesQuery(keyspace, ss, ss, ss);
					rangeSlicesQuery.setColumnFamily("collectionlog");
					int i=0;
					rangeSlicesQuery.setRange("", "", false, 3000);
					QueryResult<OrderedRows<String, String, String>> result = rangeSlicesQuery.execute();
					for (Row<String, String, String> row : result.get().getList()) {
					CassandraPersistentEventCounters cpc= new CassandraPersistentEventCounters("twitterresearch","eventcounter");
					Long range= cpc.get("counter", row.getKey());
					List<String> values = new ArrayList<String>();
					//String a= cpt.getRecentTweets(row.getKey());
					 TreeMap<Long, String> tweetSet = cpt.getLast(row.getKey(), 5);
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
						<td >
						<%=row.getColumnSlice().getColumnByName("ACategory").getValue()%>
						</td>
						<td>
						<%=row.getColumnSlice().getColumnByName("BType").getValue()%>
						</td>
						<td>
						<%=row.getColumnSlice().getColumnByName("EType").getValue()%>
						</td>
						<td>
						<%=row.getColumnSlice().getColumnByName("EName").getValue()%>
						</td>
						<td>
						<%=row.getColumnSlice().getColumnByName("Filtering criteria on keywords").getValue()%>
						</td>
						<td>
						<%=row.getColumnSlice().getColumnByName("Filtering criteria on Location").getValue()%>
						</td>
						<td>
						<%=row.getColumnSlice().getColumnByName("Starttime").getValue()%>
						</td>
						<td>
						<%=row.getColumnSlice().getColumnByName("StopTime").getValue()%>
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
          				<INPUT TYPE="HIDDEN" NAME="latest" value="<%=row.getKey()%>">			
						<div class ="controls"> 
						<button type="submit" class="btn btn-warning" data-toggle="button">
  						 Stop Collection
						</button>
						</div>
						</form>
						
						</td>

						</tr>
			<%  } %>
			
	
</tbody>
</table>
                     
</div>
</div>
 </div>
 <script src="bootstrap/js/jquery.js"></script>
 <script src="bootstrap/js/bootstrap-transition.js"></script>
<script src="bootstrap/js/bootstrap-tab.js"></script>
<script type="text/javascript">

$(document).ready(function() {
    /*
     * Insert a 'details' column to the table
     */
    var nCloneTh = document.createElement( 'th' );
    var nCloneTd = document.createElement( 'td' );
    nCloneTd.innerHTML = '<img src="http://www.datatables.net/release-datatables/examples/examples_support/details_open.png">';
    nCloneTd.className = "center";
     
    $('#historyData thead tr').each( function () {
        this.insertBefore( nCloneTh, this.childNodes[0] );
    } );
     
    $('#historyData tbody tr').each( function () {
        this.insertBefore(  nCloneTd.cloneNode( true ), this.childNodes[0] );
    } );
     
    /*
     * Initialse DataTables, with no sorting on the 'details' column
     */

     
    /* Add event listener for opening and closing details
     * Note that the indicator for showing which row is open is not controlled by DataTables,
     * rather it is done here
     */
    $('#historyData tbody td img').live('click', function () {
        var nTr = $(this).parents('tr')[0];
        if ( this.fnIsOpen(nTr) )
        {
            /* This row is already open - close it */
            this.src = "http://www.datatables.net/release-datatables/examples/examples_support/details_open.png";
            this.fnClose( nTr );
        }
        else
        {
            /* Open this row */
            this.src = "http://www.datatables.net/release-datatables/examples/examples_support/details_close.png";
            this.fnOpen( nTr, fnFormatDetails(this, nTr), 'details' );
        }
    } );
} );

/* Formating function for row details */
function fnFormatDetails ( oTable, nTr )
{
    var aData = oTable.fnGetData( nTr );
    var sOut = '<table cellpadding="5" cellspacing="0" border="0" style="padding-left:50px;">';
    sOut += '<tr><td>Rendering engine:</td><td>'+aData[1]+' '+aData[4]+'</td></tr>';
    sOut += '<tr><td>Link to source:</td><td>Could provide a link here</td></tr>';
    sOut += '<tr><td>Extra info:</td><td>And any further details here (images etc)</td></tr>';
    sOut += '</table>';
     
    return sOut;
}

</script>
</body>

</html>