<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Requested Contractors List</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>Contractor Registration Request</h1>

<div>
<a href="RequestNewContractor.action">Request New Contractor</a>
</div>

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

<table class="report">
	<thead>
	<tr>
		<td colspan="2">Account Name</td>
		<td>Requested By</td>
		<td>DeadLine</td>
		<s:if test="permissions.operatorCorporate">
			<td>Follow Up</td>
		</s:if>
		<td>Contacted By</td>
		<td>On</td>
		<td>Attempts</td>
		<td title="Potential Matches in PICS">Matches</td>
		<td>In PICS</td>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><a href="RequestNewContractor.action?requestID=<s:property value="get('id')"/>">
				<s:property value="get('name')" /></a>
			</td>
			<td title="<s:property value="get('RequestedUser')"/>">
				<s:property value="get('RequestedBy')"/>
			</td>
			<td><s:date name="get('deadline')" format="MM/dd/yyyy"/></td>
			<s:if test="permissions.operatorCorporate">
				<td><s:property value="get('handledBy')"/></td>
			</s:if>
			<td><s:property value="get('ContactedBy')" /></td>
			<td><s:date name="get('lastContactDate')" format="MM/dd/yyyy"/></td>
			<td><s:property value="get('contactCount')" /></td>
			<td><s:property value="get('matchCount')" /></td>
			<td><s:if test="get('conID') != null">
					<a href="ContractorView.action?id=<s:property value="get('conID')"/>">
					<s:property value="get('contractorName')" /></a>			
				</s:if>
			</td>
		</tr>
	</s:iterator>

</table>

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

</body>
</html>
