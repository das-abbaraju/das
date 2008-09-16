<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Operator Flag Criteria</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<script type="text/javascript" src="js/prototype.js"></script>
<script type="text/javascript"
	src="js/scriptaculous/scriptaculous.js?load=effects"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/calendar.css" />
</head>
<body>
<h1>Operator Flag Criteria</h1>

<s:include value="filters.jsp" />

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
	<tr>
		<td colspan="2">Contractor Name</td>
		<td>Flag</td>
		<td>Risk Level</td>
		<s:iterator value="operatorAccount.audits">
			<s:if test="canSee && minRiskLevel > 0">
				<td><s:property value="auditType.auditName"/> Status</td>
				<td>% Completed</td>
		</s:if>
		</s:iterator>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right">
				<s:property value="#stat.index + report.firstRowNumber" />
			</td>
			<td>
				<s:property value="[0].get('name')"/></a>
			</td>
			<td class="center">
				<a href="ContractorFlag.action?id=<s:property value="get('id')"/>" title="Click to view Flag Color details">
				<img src="images/icon_<s:property value="[0].get('lflag')"/>Flag.gif" width="12" height="15" border="0"></a>
			</td>
			<td class="center">
				<s:property value="[0].get('riskLevel')" />
			</td>
			<s:iterator value="operatorAccount.audits">		
				<s:if test="canSee && minRiskLevel > 0">
					<td><s:property value="%{get(auditType.auditName + ' Status')}"/></td>
					<td class="right"><s:property value="%{get(auditType.auditName + ' Completed')}"/></td>
				</s:if>
			</s:iterator>
		</tr>
	</s:iterator>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</body>
</html>
