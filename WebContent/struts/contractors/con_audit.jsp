<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@page import="com.picsauditing.access.OpPerms"%>
<jsp:useBean id="permissions"
	class="com.picsauditing.access.Permissions" scope="session" />
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean"
	scope="page" />
<html>
<head>
<title><s:property value="conAudit.auditType.auditName" /> for
<s:property value="conAudit.contractor.name" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
</head>
<body>
<h1><s:property value="conAudit.contractorAccount.name" /> <span
	class="sub"><s:property value="conAudit.auditType.auditName" />
- <s:date name="conAudit.createdDate" format="MMM yyyy" /></span></h1>

<%
	String id = "1";
	cBean.setFromDB(id);
%>
<%@ include file="/utilities/adminOperatorContractorNav.jsp"%>

<span class="message"><s:property value="message" /></span>
<table class="report">
	<thead>
		<tr>
			<th>Num
			</td>
			<th>Category
			</td>
			<th>Complete
			</td>
		</tr>
	</thead>
	<s:iterator value="categories" status="rowStatus">
		<tr>
			<td><s:property value="auditType.auditName" /></td>
			<td><s:property value="auditStatus" /></td>
			<td><s:date name="createdDate" format="M/d/yy" /></td>
		</tr>
	</s:iterator>
</table>
</body>
</html>
