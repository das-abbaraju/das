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

<div id="search">
<div id="showSearch"><a href="#" onclick="showSearch()">Show Filter Options</a></div>
<div id="hideSearch" style="display: none"><a href="#" onclick="hideSearch()">Hide Filter Options</a></div>
<s:form id="form1" method="get" cssStyle="display: none">
	<table border="0" align="center" cellpadding="2" cellspacing="0">
		<tr>
			<td align="left"><nobr>
			<s:textfield name="accountName" cssClass="forms" size="8" onfocus="clearText(this)" />
			 <s:select name="industry" list="industryList" cssClass="forms" />
			 <s:select list="tradeList" cssClass="forms" name="trade" /> 
			<s:submit name="imageField" type="image" src="images/button_search.gif" onclick="runSearch( 'form1')" />
			</nobr></td>
		</tr>
		<tr>
		<td><s:select list="operatorList" cssClass="forms" name="operator" listKey="id" listValue="name" /> 
			<s:textfield name="city" cssClass="forms" size="15" onfocus="clearText(this)"  />
			<s:select list="stateList" cssClass="forms" name="state" />
			<s:textfield name="zip" cssClass="forms" size="5" onfocus="clearText(this)" /></td>
		</tr>
		<tr>
			<td><s:select list="certsOptions" cssClass="forms" name="certsOnly" />
			<s:select list="visibleOptions" cssClass="forms" name="visible" />
			<s:select list="stateLicensesList" cssClass="forms" name="stateLicensedIn" />
			<s:textfield name="taxID" cssClass="forms" size="9" onfocus="clearText(this)"  />
			<span class="redMain">*must be 9 digits</span></td>
		</tr>
		<tr>
			<td><s:select list="tradePerformedByList" cssClass="forms" name="performedBy" />
			<s:select list="worksInList" cssClass="forms" name="worksIn" />
	</table>
	<s:hidden name="showPage" value="1" />
	<s:hidden name="startsWith" />
	<s:hidden name="orderBy" />
	<div class="alphapaging">
	<s:property value="report.startsWithLinksWithDynamicForm" escape="false" />
	</div>
</s:form>
</div>

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
		<td>Desktop</td>
		<td>Office</td>
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
			<td class="center"><s:if test="[0].get('ca2_auditID') > 0">
				<s:if test="[0].get('ca2_auditStatus').equals('Exempt')">N/A</s:if>
				<s:else>
					<a
						href="Audit.action?auditID=<s:property value="[0].get('ca2_auditID')"/>"><img
						src="images/icon_Desktop.gif" width="20" height="20" border="0"></a>
				</s:else>
			</s:if></td>
			<td class="center"><s:if test="[0].get('ca3_auditID') > 0">
				<s:if test="[0].get('ca3_auditStatus').equals('Exempt')">N/A</s:if>
				<s:else>
					<a
						href="Audit.action?auditID=<s:property value="[0].get('ca3_auditID')"/>"><img
						src="images/icon_Office.gif" width="20" height="20" border="0"></a>
				</s:else>
			</s:if></td>
			<td class="center"><s:if test="[0].get('certs') > 0">
				<a
					href="contractor_upload_certificates.jsp?id=<s:property value="[0].get('id')"/>"><img
					src="images/icon_insurance.gif" width="20" height="20" border="0"></a>
			</s:if></td>
		</tr>
	</s:iterator>

</table>
<center><s:property value="report.pageLinksWithDynamicForm" escape="false" />
</center>
</body>
</html>
