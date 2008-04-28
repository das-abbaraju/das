<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ taglib uri="WEB_INF/tiles.tld" prefix="tiles"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Audit/Evaluations for <s:property value="contractor.name" /></title>
<meta name="header_gif" content="header_contractorDetails.gif" />
</head>
<body>
<h1><s:property value="contractor.name" />
<span class="sub">Contractor Audit &amp; Evaluations</span></h1>

<s:include value="con_nav.jsp"></s:include>
<s:property value="action"/>

<table cellspacing="1" cellpadding="3" border="0">
	<tr class="whiteTitle" bgcolor="#003366" align="center">
		<td></td>
		<td>Type</td>
		<td>Status</td>
		<td>Created</td>
		<td>For</td>
		<td>Auditor</td>
		<td>Location</td>
		<td>Scheduled</td>
		<td>Submitted</td>
		<td>Closed</td>
		<td>Expires</td>
	</tr>
	<s:iterator value="audits" status="auditStatus">
		<tr class="blueMain"
			<s:if test="#auditStatus.even">bgcolor="#FFFFFF"</s:if>>
			<td><a href="pqf_view.jsp?auditID=<s:property value="id" />">View</a></td>
			<td><s:property value="auditType.auditName" /></td>
			<td><s:property value="auditStatus" /></td>
			<td><s:date name="createdDate" format="M/d/yy" /></td>
			<td><s:property value="requestingOpAccount.name" /></td>
			<td><s:property value="auditor.name" /></td>
			<td><s:property value="auditLocation" /></td>
			<td><s:date name="scheduledDate" format="M/d/yy" /></td>
			<td align="right"><s:if test="percentComplete < 100">
				<s:property value="percentComplete" />%</s:if> <s:else>
				<s:date name="completedDate" format="M/d/yy" />
			</s:else></td>
			<td align="right"><s:if test="percentVerified < 100">
				<s:property value="percentVerified" />%</s:if> <s:else>
				<s:date name="closedDate" format="M/d/yy" />
			</s:else></td>
			<td><s:date name="expiresDate" format="M/d/yy" /></td>
		</tr>
	</s:iterator>
</table>
</body>
</html>
