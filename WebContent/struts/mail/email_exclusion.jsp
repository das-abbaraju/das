<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>

<title>Email Exclusion List</title>

<link rel="stylesheet" type="text/css" media="screen"
	href="css/reports.css?v=<s:property value="version"/>" />
</head>
<body>
<h1>Email Exclusion List</h1>

<s:include value="../actionMessages.jsp" />

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
		<td>Email</td>
		<td><s:text name="button.Remove" /></td>
	</thead>
	<s:iterator value="data">
		<tr>
			<td><s:property value="get('email')"/></td>
			<td class="center">
				<a href="?email=<s:property value="get('email')"/>&button=remove" class="remove">Remove</a>
			</td>
		</tr>
	</s:iterator>
</table>

<s:form>
Exclude New Email Address: <s:textfield name="email" /><br />
<button name="button" class="save" value="save">Save</button>
</s:form>

</body>
</html>