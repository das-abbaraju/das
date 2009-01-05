<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Schedule &amp; Assign Audits</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>Insurance Approval</h1>
<s:include value="filters.jsp" />
<div><s:property value="report.pageLinksWithDynamicForm"
	escape="false" /></div>
<s:form id="approveInsuranceForm" method="post" cssClass="forms">
	<table class="report">
		<thead>
			<tr>
				<td></td>
				<td><a href="javascript: changeOrderBy('form1','a.name');">Contractor</a></td>
				<td>Type</td>
				<td>Auditor</td>
				<td>Operator</td>
				<td align="center"><a href="javascript: changeOrderBy('form1','expiresDate ASC');">Expires</a></td>
			</tr>
		</thead>
		<s:iterator value="data" status="stat">
			<tr>
				<td class="right"><s:property
					value="#stat.index + report.firstRowNumber" /></td>
				<td><a href="VerifyInsuranceApproval.action?id=<s:property value="get('id')"/>"><s:property value="[0].get('name')"/></a>
				</td>
				<td><a href="Audit.action?auditID=<s:property value="[0].get('auditID')"/>"><s:property value="[0].get('auditName')"/></a></td>
				<td><s:property value="get('auditor_name')"/></td>
				<td><s:property value="get('operatorName')"/></td>
				<td class="reportDate"><s:date name="get('expiresDate')"
					format="M/d/yy" /></td>
			</tr>
		</s:iterator>
	</table>
</s:form>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</body>
</html>
				