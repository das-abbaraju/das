<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Contractor Search - Admin</title>
<script src="js/prototype.js" type="text/javascript"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
</head>
<body>
<h1>Contractor Search <span class="sub">Admin Version</span></h1>

<s:include value="filters.jsp" />


<s:if test="report.allRows > 1">
	<div class="right"><a class="excel" href="javascript: download('ContractorListAdmin');" title="Download all <s:property value="report.allRows"/> results to a CSV file">Download</a></div>
</s:if>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
	<tr>
		<td colspan="2">Contractor Name</td>
		<td></td>
		<td>Industry</td>
		<td>Trade</td>
		<td></td>
		<td>PQF</td>
		<td>Insur</td>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><a
				href="ContractorView.action?id=<s:property value="[0].get('id')"/>"
				><s:property value="[0].get('name')" /></a></td>
			<td><a
				href="accounts_edit_contractor.jsp?id=<s:property value="[0].get('id')"/>"
				>Edit</a></td>
			<td><s:property value="[0].get('industry')" /></td>
			<td><s:property value="[0].get('main_trade')" /></td>
			<td><a
				href="ConAuditList.action?id=<s:property value="[0].get('id')"/>">Audits</a></td>
			<td class="center"><s:if test="[0].get('ca1_auditID') > 0">
				<s:if test="[0].get('ca1_auditStatus').equals('Exempt')">N/A</s:if>
				<s:else>
					<a
						href="Audit.action?auditID=<s:property value="[0].get('ca1_auditID')"/>"><img
						src="images/icon_PQF.gif" width="20" height="20" border="0"></a>
				</s:else>
			</s:if></td>
			<td class="center">
				<a
					href="contractor_upload_certificates.jsp?id=<s:property value="[0].get('id')"/>"><img
					src="images/icon_insurance.gif" width="20" height="20" border="0"></a>
			</td>
		</tr>
	</s:iterator>

</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</body>
</html>
