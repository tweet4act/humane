<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"
	import="java.util.*"
	%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Create New Collection</title>
<body>

<form method="post" action="CollectorServlet">
				<br />
				<p align="center"><p align="left" title="Collection Configuration">
		
	
<div class="container">
	  <div class="hero-unit">
         <center>
	   <h3>  Welcome Admin! </h3>
          <table class="table">
          <thead>
          		<tr>
          			<p>Configuration Settings for the Tweet Collection Set</p>
          		</tr>
          	</thead>
          	<tbody>
          		<tr>	
          			<td> Collection set name*:</td>
          			<td><INPUT TYPE="TEXT" NAME="name"
											value="${param['cname']}">
					</td>				
          		</tr>
          		<tr>
          			<td> Category for the collection*:</td>
          			<td> <select id="category" name ="category">
							<option value="Natural Disaster/Atmospheric/Hurricane">Natural Disaster/Atmospheric/Hurricane</option>
							<option value="Others">Others</option>
						</select>
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
          		<tr>
          		<td>
          		<% 
					Calendar cal = Calendar.getInstance();
					Date Start_time=cal.getTime(); 
				%>
          		<INPUT TYPE="HIDDEN" NAME="collectionid" value="col<%=Start_time%>
          		">
          		</td>
          		</tr>

          	</tbody>
          </table>
           <div class ="controls">
   
          		<button type="submit" class="btn btn-success">
  					OK
				</button>
			
			</div>
         </center>
		</div>
		</div>
	</form>
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