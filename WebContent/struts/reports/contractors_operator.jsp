<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Contractor Search - Operator</title>
<script src="js/prototype.js" type="text/javascript"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects"
	type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
</head>
<body>
<h1>Contractor List <span class="sub">
<s:if test="permissions.operator">
Operator Version
</s:if>
<s:else>
Corporate Version
</s:else>
</span></h1>
<table border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td colspan="2" align="center" class="blueMain"><span
			class="redMain">You have <strong><s:property
			value="contractorCount" /></strong> contractors in your database.</span></td>
	</tr>
</table>
<s:include value="filters.jsp" />
<s:if test="report.allRows > 1">
	<div class="right"><a class="excel" href="javascript: download('ContractorListOperator');" title="Download all <s:property value="report.allRows"/> results to a CSV file">Download</a></div>
</s:if>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
	<tr>
		<td colspan="2">Contractor Name</td>
		<pics:permission perm="ContractorDetails">
			<td></td>
			<s:if test="pqfVisible">
				<td>PQF</td>
			</s:if>
		</pics:permission>
		<!-- 
		<pics:permission perm="InsuranceCerts">
			<td>Ins. Certs</td>
		</pics:permission>
		 -->
		<s:if test="permissions.operator">
			<td><a
				href="?orderBy=flag DESC">Flag</a></td>
		</s:if>

		<s:if test="operatorAccount.approvesRelationships">
			<pics:permission perm="ViewUnApproved">
				<td><nobr>Approved</nobr></td>
			</pics:permission>
		</s:if>

	</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><a
				href="ContractorView.action?id=<s:property value="[0].get('id')"/>"
				><s:property value="[0].get('name')" /></a>
			</td>
			<pics:permission perm="ContractorDetails">
			<td><a
				href="ConAuditList.action?id=<s:property value="[0].get('id')"/>">Audits</a></td>
			<s:if test="pqfVisible">
				<td align="center"><s:if test="[0].get('ca1_auditID') > 0">
					<s:if test="[0].get('ca1_auditStatus').equals('Exempt')">N/A</s:if>
					<s:else>
						<a
							href="Audit.action?auditID=<s:property value="[0].get('ca1_auditID')"/>"><img
							src="images/icon_PQF.gif" width="20" height="20" border="0"></a>
					</s:else>
				</s:if></td>
			</s:if>
			</pics:permission>
			<!-- 
			<pics:permission perm="InsuranceCerts">
			<td align="center">
					&nbsp;&nbsp;<s:if test="[0].get('certs') > 0">
					<a
						href="contractor_upload_certificates.jsp?id=<s:property 
 					value="[0].get('id')"/>"><img
						src="images/icon_insurance.gif" width="20" height="20" border="0"></a>
				</s:if>
			</td>
			</pics:permission>
			 -->
			<s:if test="permissions.operator">
				<td class="center">
					<a href="ContractorFlag.action?id=<s:property value="[0].get('id')"/>" 
						title="<s:property value="[0].get('flag')"/> - Click to view details"><img 
						src="images/icon_<s:property value="[0].get('lflag')"/>Flag.gif" width="12" height="15" border="0"></a>
				</td>
			</s:if>
			<s:if test="operatorAccount.approvesRelationships">
				<pics:permission perm="ViewUnApproved">
					<td align="center">&nbsp;&nbsp;&nbsp;&nbsp;<s:property
						value="[0].get('workStatus')" />
					</td>
				</pics:permission>
			</s:if>
		</tr>
	</s:iterator>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

</body>
</html>
