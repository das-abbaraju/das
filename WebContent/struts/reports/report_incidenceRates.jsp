<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Incidence Rates</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>Incidence Rates Report</h1>
<s:include value="filters.jsp" />

<div><s:property value="report.pageLinksWithDynamicForm"
	escape="false" /></div>
	
<table class="report">
	<thead>
		<tr>
			<td></td>
			<th><a href="javascript: changeOrderBy('form1','a.name');">Contractor</a></th>
			<td>Location</td>
			<td>Type</td>
			<td>Rate</td>
			<td>Year</td>
		</tr>
	</thead>
	<!--TODO Add in the Contractor FlagColor-->
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property
				value="#stat.index + report.firstRowNumber" /></td>
			<td><a
				href="Audit.action?auditID=<s:property value="[0].get('auditID')"/>"><s:property
				value="[0].get('name')" /></a></td>
			<td><s:if test="%{[0].get('location') == 'Corporate'}">
				<s:property value="[0].get('location')" />
			</s:if><s:else>
				<s:property
					value="%{[0].get('location')+'-'+[0].get('description')}" />
			</s:else></td>
			<td><s:property value="[0].get('SHAType')" /></td>
			<!--Need to fix this before the year end-->
			<td class="right"><s:property
				value="%{new java.text.DecimalFormat('#,##0.00').format([0].get('recordableTotal')*200000f/[0].get('manHours'))}" />
			</td>
			<td><s:property value="get('auditFor')" />
			</td>
		</tr>
	</s:iterator>
</table>


<div><s:property value="report.pageLinksWithDynamicForm"
	escape="false" /></div>
</body>
</html>