<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>PQF Verification</title>
<script src="js/prototype.js" type="text/javascript"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
</head>
<body>
<h1>PQF Verification</h1>
<s:include value="filters.jsp" />
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

<table class="report">
	<thead>
	<tr>
		<td></td>
		<td><a href="javascript: changeOrderBy('form1','a.name');">Contractor</a></td>
		<td><a href="javascript: changeOrderBy('form1','ca.auditStatus DESC');">Status</a></td>
		<td ><a href="javascript: changeOrderBy('form1','ca.completedDate DESC');">Submitted</a></td>
		<td ><a href="javascript: changeOrderBy('form1','ca.scheduledDate DESC');">Followup</a></td>
		<td>%Completed</td>
		<td>%Verified</td>
		<td>Notes</td>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><a href="VerifyView.action?auditID=<s:property value="[0].get('auditID')"/>"><s:property value="[0].get('name')"/></a></td>
			<td><s:property value="[0].get('auditStatus')"/></td>
			<td><s:date name="[0].get('completedDate')" format="M/d/yy" /></td>
			<td><s:date name="[0].get('scheduledDate')" format="M/d/yy" /></td>
			<td align="right"><s:property value="[0].get('percentComplete')"/>%</td>
			<td align="right"><s:property value="[0].get('percentVerified')"/>%</td>
			<td>
				<s:if test="[0].get('notes') == null">
					<s:property value=""/>
				</s:if>
				<s:else>
					<s:property value="[0].get('notes').toString().substring(0, 50).concat('...')"/>		
				</s:else>
			</td>
		</tr>
	</s:iterator>	
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</body>
</html>