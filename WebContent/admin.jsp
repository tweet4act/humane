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
				
					<ul class="nav nav-tabs" id="myTab">
						<li class="active"><a href="#1" data-toggle="tab">Home</a></li>
						<li><a href="#2"  data-toggle="tab">Dashboard</a></li>

					</ul>

				<!-- /.nav-collapse -->
			</div>
		</div>
		<!-- /navbar-inner -->
	</div>
	<!-- /navbar -->
<div class="tab-content">
		<div class="tab-pane active" id="1">
			<form method="post" action="TweetCollectorServlet">
				<br />
				<p align="center"><p align="left" title="Admin Page">
		
	
				<div class="container">
	  <div class="hero-unit">
         <center>
	   <h3>  Welcome Admin! </h3>
          <table class="table">
          <thead>
          		<tr>
          			<p>Configuration Settings for the Tweet Collector</p>
          		</tr>
          	</thead>
          	<tbody>
          		<tr>
          			<td> Universal type of incident*:</td>
          			<td> <select id="uni" name ="universal">
							<option value="Natural Disaster">Natural Disaster</option>
							<option value="Man-made Disaster">Man-made Disaster</option>
						</select>
					</td>
				</tr>
				<tr>
          			<td> Super type for the incident*:</td>
          			<td> <select id="sup" name ="super">
							<option value="atmospheric">atmospheric</option>
							<option value="water">water</option>
							<option value="fire">fire</option>
							<option value="mixed">mixed</option>
							<option value="other">other</option>
						</select>
					</td>
				</tr>
				<tr>	
          			<td> Event type*:</td>
          			<td> <select id="et" name ="etype">
							<option value="atmospheric">Hurricane</option>
							<option value="Typhoon">Typhoon</option>
							<option value="Earthquake">Earthquake</option>
							<option value="Flood">Flood</option>
							<option value="Tornado">Tornado</option>
							<option value="other">other</option>
						</select>
					</td>				
          		</tr>
				<tr>	
          			<td> Event instance name*:</td>
          			<td><INPUT TYPE="TEXT" NAME="name"
											value="${param['name']}">
					</td>				
          		</tr>
          		<tr>	
          			<td>Filter tweets on tracks:</td>
          			<td><input TYPE="TEXT" NAME="track"
											value="${param['track']}">
					</td>				
          		</tr>
          		<tr>	
          			<td>geo location:</td>
          			<td><INPUT TYPE="TEXT" NAME="geo"
											value="${param['geo']}">
					</td>				
          		</tr>
          		<tr>
          		<td><p> Authentication information required for tweet collection:</p></td>
          		</tr>
          		
          		
          		<tr>	
          			<td>consumer key*:</td>
          			<td><INPUT TYPE="TEXT" NAME="ck"
											value="${param['ck']}">
					</td>				
          		</tr>
          		<tr>	
          			<td>consumer secret*:</td>
          			<td><INPUT TYPE="TEXT" NAME="cs"
											value="${param['cs']}">
					</td>				
          		</tr>
          		<tr>	
          			<td>access token*:</td>
          			<td><INPUT TYPE="TEXT" NAME="at"
											value="${param['at']}">
					</td>				
          		</tr>
          		<tr>	
          			<td>access token secret*:</td>
          			<td><INPUT TYPE="TEXT" NAME="as"
											value="${param['as']}">
					</td>				
          		</tr>

          	</tbody>
          </table>
           <div class ="controls">
          		<button type="submit" class="btn btn-success">
  					Start Collection
				</button>
		</div>
         </center>
		</div>
		</div>
	</form>
	<% String message=null;
	if(request.getAttribute("message")=="success"){%>
	<div class="alert alert-success">
	<button type="button" class="close" data-dismiss="alert">&times;</button>
	The job is successfully submitted
	</div>
	<%
	}
	%>
	
	</div>
    
<div class="tab-pane" id="2">

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