<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Insurance Policy Approval</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>Insurance Policy Approval</h1>
<s:include value="filters.jsp" />
<div><s:property value="report.pageLinksWithDynamicForm"
	escape="false" /></div>
<s:form id="approveInsuranceForm" method="post" cssClass="forms">
	<table class="report">
		<thead>
			<tr>
				<td></td>
				<td><a href="javascript: changeOrderBy('form1','a.name');">Contractor</a></td>
				<td>Policy Type</td>
				<td>Status</td>
				<td align="center"><a href="javascript: changeOrderBy('form1','createdDate ASC');">Effective</a></td>
				<td align="center"><a href="javascript: changeOrderBy('form1','expiresDate ASC');">Expires</a></td>
				<td>Limits</td>
				<td>Additional Insured</td>
				<td>Waiver of Subrogation</td>
				<td>File</td>
				<td>Notes</td>
			</tr>
		</thead>
		<s:iterator value="data" status="stat">
			<tr>
				<td class="right"><s:property
					value="#stat.index + report.firstRowNumber" /></td>
				<td><a href="ContractorView.action?id=<s:property value="get('id')"/>"><s:property value="[0].get('name')"/></a>
				</td>
				<td><a href="Audit.action?auditID=<s:property value="[0].get('auditID')"/>"><s:property value="[0].get('auditName')"/></a></td>
				<td><s:property value="get('caoStatus')"/></td>
				<td class="reportDate"><s:date name="get('createdDate')" format="M/d/yy" /></td>
				<td class="reportDate"><s:date name="get('expiresDate')" format="M/d/yy" /></td>
				<td class="limits" style="font-size: smaller;">
					<s:iterator value="getDataForAudit(get('auditID'),'limits')">
						<strong><s:property value="new java.text.DecimalFormat('$#,##0').format(new java.lang.Long(answer))"/></strong> - <s:property value="question.question"/><br/>
					</s:iterator>
				</td>
				<td style="font-size: smaller;">
					<s:iterator value="getDataForAudit(get('auditID'),'aiName')">
						<strong><s:property value="answer"/></strong><br/>
					</s:iterator>
				</td>
				<td style="font-size: smaller;">
					<s:iterator value="getDataForAudit(get('auditID'),'aiWaiverSub')">
						<strong><s:property value="answer"/></strong><br/>
					</s:iterator>
				</td>
				<td style="width: 45px; font-size: smaller;">
					<s:iterator value="getDataForAudit(get('auditID'),'aiFile')">
						<a href="DownloadAuditData.action?auditID=<s:property value="audit.id"/>&answer.id=<s:property value="id"/>" target="_BLANK">View File</a>
					</s:iterator>
				</td>
				<td style="width: 120px; font-size: smaller;" class="notes"><s:property value="get('caoNotes')"/></td>
			</tr>
		</s:iterator>
	</table>
</s:form>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</body>
</html>
				