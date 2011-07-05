<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Contractors Assigned To CSRs</title>
<s:include value="reportHeader.jsp" />
<link rel="stylesheet" type="text/css" media="screen"
	href="css/forms.css?v=<s:property value="version"/>" /></head>
<body>
<h1>Contractors Assigned To CSRs</h1>


<div id="search">
	<s:form id="form1" method="post"
		cssStyle="background-color: #F4F4F4;">
	<s:hidden name="showPage" value="1" />
	<s:hidden name="startsWith" />
	<s:hidden name="orderBy" />
	<div>
		<button id="searchfilter" type="submit" name="button" value="Search"  class="picsbutton positive">Search</button>
	</div>
	<s:if test="permissions.hasGroup(981)">
	<div class="filterOption"><a href="#"
		onclick="toggleBox('form1_conAuditorId'); return false;">CSR</a> = <span
		id="form1_conAuditorId_query">ALL</span><br />
	<span id="form1_conAuditorId_select" style="display: none"
		class="clearLink"> <s:select name="csrIds"
		cssClass="forms" list="csrs" listKey="id" listValue="name"
		multiple="true" size="5" id="form1_conAuditorId" /> <script
		type="text/javascript">updateQuery('form1_conAuditorId');</script> <br />
	<a class="clearLink" href="#"
		onclick="clearSelected('form1_conAuditorId'); return false;">Clear</a>
	</span></div>
	</s:if>

	<br clear="all"/>
	</s:form>
</div>
<br/>
<table class="report">
	<thead>
		<tr>
			<td><a href="?orderBy=u.name ASC">CSR Name</a></td>
			<td><a href="?orderBy=a.state ASC">State</a></td>
			<td><a href="?orderBy=cnt ASC">Contractor Count</a></td>
		</tr>
	</thead>
	<s:iterator value="data">
		<tr>
			<td><s:property value="get('name')"/></td>
			<td><s:property value="get('state')"/></td>
			<td><s:property value="get('cnt')"/></td>
		</tr>
	</s:iterator>
</table>
</body>
</html>
