<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"
	import="java.util.*"
	import="org.qf.qcri.humane.tweetcollector.servlet.CollectorServlet"
	%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Create New Collection</title>
<body>

<form method="post" action="CollectorTaskServlet">
				<br />
				<p align="center"><p align="left" title="Collection Task Configuration">
	
	
<div class="container">
	  <div class="hero-unit">
         <center>
	   <h3>  Collection Task Configuration </h3>
          <table class="table">
          <thead>
          		<tr>
          			<p>Filtering conditions for the tweet collection task:</p>
          		</tr>
          	</thead>
          	<tbody>
          		<tr>	
          			<td> Task name*:</td>
          			<td><INPUT TYPE="TEXT" NAME="ctname"
											value="${param['ctname']}">
					</td>				
          		</tr>

          		<tr>	
          			<td>Filter based on track:</td>
          			<td><INPUT TYPE="TEXT" NAME="track"
											value="${param['track']}">
					</td>				
          		</tr>
          		<tr>	
          			<td>Filter based on geo:</td>
          			<td><INPUT TYPE="TEXT" NAME="geo"
											value="${param['geo']}">
					</td>				
				<tr>
          		<td>
          		
          		<INPUT TYPE="HIDDEN" NAME="collectionid" value="<%=CollectorServlet.COLLECTION_ID%>
          		">
          		</td>
          		<td>
          		
          		<INPUT TYPE="HIDDEN" NAME="collectionname" value="<%=CollectorServlet.COLLECTION_NAME%>
          		">
          		</td>
          		<td>
          		<% 
					Calendar cal = Calendar.getInstance();
					Date Start_time=cal.getTime(); 
				%>	
          		<INPUT TYPE="HIDDEN" NAME="collectiontaskid" value="<%="coltask"+Start_time%>
          		">
          		</td>
          		<td>
          		<INPUT TYPE="HIDDEN" NAME="startdate" value="<%=Start_time%>
          		">
          		</td>
          		<td>
          		<INPUT TYPE="HIDDEN" NAME="enddate" value="Currently Active">
          		</td>
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