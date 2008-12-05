<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Operator Flag Criteria</title>
<s:include value="reportHeader.jsp" />
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
		<td></td>
		<td colspan="2">Contractor Name</td>
		<s:if test="permissions.operator">
			<td>Flag</td>
		</s:if>
		<td>Risk Level</td>
		<s:iterator value="operatorAccount.audits">
			<s:if test="canSee && minRiskLevel > 0">
				<td><s:property value="auditType.auditName"/> Status</td>
			</s:if>
		</s:iterator>
		<s:iterator value="operatorAccount.flagQuestionCriteria">
			<s:if test="checked.toString().equals('Yes') && auditQuestion.id != 0">
				<td><s:property value="auditQuestion.columnHeader"/></td>
			</s:if>
		</s:iterator>
		<s:if test="hasFatalities">
			<td>Fatalities '07</td>
			<td>Fatalities '06</td>
			<td>Fatalities '05</td>
		</s:if>
		<s:if test="hasTrir">
			<td>TRIR '07</td>
			<td>TRIR '06</td>
			<td>TRIR '05</td>
		</s:if>
		<s:if test="hasLwcr">
			<td>LWCR '07</td>
			<td>LWCR '06</td>
			<td>LWCR '05</td>
		</s:if>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right">
				<s:property value="#stat.index + report.firstRowNumber" />
			</td>
			<td colspan="2"><nobr><a href="ContractorView.action?id=<s:property value="[0].get('id')"/>">
				<s:property value="[0].get('name')"/></a></nobr>
			</td>
			<s:if test="permissions.operator">
				<td class="center">
					<a href="ContractorFlag.action?id=<s:property value="get('id')"/>" title="Click to view Flag Color details">
					<img src="images/icon_<s:property value="[0].get('lflag')"/>Flag.gif" width="12" height="15" border="0"></a>
				</td>
			</s:if>
			<td class="center">
				<s:property value="[0].get('riskLevel')" />
			</td>
			<s:iterator value="operatorAccount.audits">		
				<s:if test="canSee && minRiskLevel > 0">
					<td><span title="Completed - <s:property value="%{get(auditType.auditName + ' Completed')}"/>%"><s:property value="%{get(auditType.auditName + ' Status')}"/></span></td>
				</s:if>
			</s:iterator>
			<s:iterator value="operatorAccount.flagQuestionCriteria">
				<s:if test="checked.toString().equals('Yes') && auditQuestion.id != 0">
					<td><s:if test="%{get('verified' + auditQuestion.id)} > 0">
						<s:property value="%{get('verified' + auditQuestion.id)}"/>
					</s:if>
					<s:else>
						<s:property value="%{get('answer' + auditQuestion.id)}"/>
					</s:else>
					</td>
				</s:if>
			</s:iterator>
			<s:if test="hasFatalities">
				<td><s:property value="get('fatalities07')"/></td>
				<td><s:property value="get('fatalities06')"/></td>
				<td><s:property value="get('fatalities05')"/></td>
			</s:if>
			<s:if test="hasTrir">
				<td><s:property value="get('trir07')"/></td>
				<td><s:property value="get('trir06')"/></td>
				<td><s:property value="get('trir05')"/></td>
			</s:if>
			<s:if test="hasLwcr">
				<td><s:property value="get('lwcr07')"/></td>
				<td><s:property value="get('lwcr06')"/></td>
				<td><s:property value="get('lwcr05')"/></td>
			</s:if>
		</tr>
	</s:iterator>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</body>
</html>
