<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Audit/Evaluations for <s:property value="contractor.name" /></title>
<meta name="header_gif" content="header_contractorDetails.gif" />
</head>
<body>
<h1>Audits &amp; Evaluations for <s:property
	value="contractor.name" /></h1>

<table cellspacing="1" cellpadding="3" border="0">
	<tr class="whiteTitle" bgcolor="#003366" align="center">
		<td>Date</td>
		<td>Type</td>
		<td>Auditor</td>
		<td>Status</td>
		<td>Expires</td>
	</tr>
<s:iterator value="audits">
	<tr class="blueMain">
		<td><s:property value="createdDate" /></td>
		<td><s:property value="auditType.auditName" /></td>
		<td><s:property value="auditor.name" /></td>
		<td><s:property value="auditStatus" /></td>
		<td><s:property value="expiresDate" /></td>
	</tr>
</s:iterator>
</table>

</body>
</html>
