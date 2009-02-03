<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="../exception_handler.jsp"%>
<html>
<head>
<title><s:property value="contractor.name" /></title>

<%
// List of Javascript and css files needed to create 
// queries/reports and display the results in table format
// <script src="js/CalendarPopup.js" type="text/javascript"></script>
// <script src="js/Search.js" type="text/javascript"></script>
// <link rel="stylesheet" type="text/css" media="screen" href="css/calendar.css" />
%>
<script src="js/prototype.js" type="text/javascript"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css" />
</head>
<body>
<s:include value="conHeader.jsp" />

<div id="alert">This page is still under development</div>
<img src="images/beta.jpg" width="98" height="100" style="position: absolute; right: 40px;" title="This is a new feature. Please send us your feedback or suggestions." />

<s:if test="openTasks.size() > 0">
	<table class="notes">
		<s:iterator value="openTasks">
			<tr>
				<td>
					<a href=""><s:property value="noteCategory" /></a>: <s:property value="summary" />
					<br /><span style="float: right; padding: 5px;"><a href="" class="edit">Edit</a> <a href="" class="remove">Delete</a></span>
					<br />
					<s:if test="updateDate != null && updateDate.after(creationDate)">
						<b>Edited:</b> by <s:property value="updatedBy.name" /> on <s:date name="updateDate" format="M/d/yy" /> time
					</s:if>
				</td>
			</tr>
		</s:iterator>
	</table>
</s:if>


<s:if test="notes.size() > 0">
	<table class="notes">
		<thead>
			<tr>
				<th colspan="3">
					<ul class="filters">
						<li><a href="" class="remove">PQF</a></li>
						<li><a href="" class="remove">PICS</a></li>
						<li><a href="" class="remove">Billing</a></li>
					</ul>
				</th>
	
			</tr>
		</thead>
		<tbody>
			<s:iterator value="notes">
			<tr>
				<td class="center">
					<a href=""><s:property value="createdBy.name" /></a><br />
					<a href=""><s:property value="createdBy.account.name" /></a><br />
	
					<nobr><s:date name="creationDate" format="M/d/yy h:mm a" /></nobr>
				</td>
				<td class="priority"><a href=""><img src="images/star<s:property value="priority" />.gif" 
					height="25" width="25" title="<s:property value="priority" /> Priority" /></a></td>
				<td>
					<a href=""><s:property value="noteCategory" /></a>: <s:property value="summary" />
					<br /><span style="float: right; padding: 5px;"><a href="" class="edit">Edit</a> <a href="" class="remove">Delete</a></span>
					<br />
					<s:if test="updateDate != null && updateDate.after(creationDate)">
						<b>Edited:</b> by <s:property value="updatedBy.name" /> on <s:date name="updateDate" format="M/d/yy" /> time
					</s:if>
				</td>
			</tr>
			</s:iterator>
		</tbody>
	</table>
</s:if>

<s:form id="editNotes">
<s:hidden name="id"/>
<div>
	Priority:
	<s:select list="priorityList" value="note.priority"></s:select>
	Note Category:
	<s:select list="noteCategoryList" value="note.noteCategory"></s:select>
	Can Contractor View:
	<s:checkbox name="note.canContractorView"></s:checkbox><br/>
	Note Status: <s:select list="noteStatus" value="note.status"></s:select>
	Followup Date: <input name="followupDate" id="followupDate" 
	 type="text" size="10" 
	 value="<s:date name="note.followupDate" format="MM/dd/yyyy" />" />
	 <a href="#" 
	 id="anchorfollowupDate" name="anchorfollowupDate" 
	 onclick="cal1.select($('followupDate'), 'anchorfollowupDate','MM/dd/yyyy'); return false;">
	 <img src="images/icon_calendar.gif" width="18" height="15" border="0" /></a>
	<br/>
	<%
	//ViewableBy : <span id="operators"><s:select name="note.account.id" 
	//  list="facilities" listKey="id" listValue="name" /></span>
	 %>
	  
	<br/>
	Note: <s:textarea name="note.summary" cols="45" rows="5"></s:textarea><br/>
	Body: <s:textarea name="note.body" cols="45" rows="15"></s:textarea>
	
	<pics:permission perm="EditNotes" type="Edit">
		<td><div class="buttons"><button class="positive" name="button" type="submit" value="add">Save</button></div></td>
	</pics:permission>
	
</div>
</s:form>

<div id="caldiv1" style="position:absolute; visibility:hidden; background-color:white; layer-background-color:white;"></div>

</body>
</html>
