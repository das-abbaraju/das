<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:property value="reportName" /></title>
<s:include value="reportHeader.jsp" />
<script type="text/javascript">
function approve(id) {
	$.post('flag_changes.jsp', {approveID: id});
	$("#row" + id).hide();
}
</script>
</head>
<body>
<h1><s:property value="reportName" /></h1>

<s:include value="filters.jsp" />

<s:if test="report.allRows == 0">
	<div class="info">No flag changes to report</div>
</s:if>
<s:else>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
		<tr>
			<th></th>
			<th>Approve</th>
			<th><a href="?orderBy=a.name,operator.name">Contractor</a></th>
			<pics:permission perm="AllContractors">
				<th><a href="?orderBy=operator.name,a.name">Operator</a></th>
			</pics:permission>
			<th>Baseline</th>
			<th>Change</th>
			<th>Last Calc</th>
			<th>Useful Links</th>
		</tr>
	</thead>
	<tbody>
	<s:iterator value="data" status="stat">
		<s:set name="gcID" value="get('gcID')"></s:set>
		<tr id="row<s:property value="#gcID"/>">
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><a href="#<s:property value="#gcID"/>" onclick="approve(<s:property value="#gcID"/>); return false;" class=".button.green">Approve</a></td>
			<td><a href="ContractorView.action?id=<s:property value="get('id')"/>" 
					rel="ContractorQuickAjax.action?id=<s:property value="get('id')"/>" 
					class="contractorQuick account<s:property value="get('status')"/>" title="<s:property value="get('name')"/>"
				><s:property value="get('name')"/></a></td>
			<td><a href="OperatorConfiguration.action?id=<s:property value="get('opId')"/>"><s:property value="get('opName')"/></a></td>
			<td><s:property value="get('baselineFlag')"/></td>
			<td><s:property value="@com FlagColor.getSmallIcon(get('flag').toString()) "/>
				<a href="ContractorFlag.action?id=<s:property value="get('id')"/>&opID=<s:property value="get('opId')"/>"
				><s:property value="get('flag')"/></a></td>
			<td><s:property value="get('lastRecalculation')"/> mins ago</td>
			<td></td>
		</tr>
	</s:iterator>
	</tbody>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</s:else>

</body>
</html>
