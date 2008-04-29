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
<h1>Contractor Search <span class="sub">Operator Version</span></h1>
<table border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td colspan="2" align="center" class="blueMain"><span
			class="redMain">You have <strong><s:property
			value="contractorCount" /></strong> contractors in your database.</span></td>
	</tr>
</table>
<div id="search">
<div id="showSearch"><a href="#" onclick="$('showSearch').hide(); $('hideSearch').show(); Effect.SlideDown('form1',{duration:.3}); return false;">Show Filter Options</a></div>
<div id="hideSearch" style="display: none"><a href="#" onclick="$('hideSearch').hide(); $('showSearch').show(); Effect.SlideUp('form1',{duration:.5}); return false;">Hide Filter Options</a></div>
<s:form id="form1" method="post" cssStyle="display: none">
	<table border="0" align="center" cellpadding="2" cellspacing="0">
		<tr>
			<td align="left"><s:textfield name="accountName"
				cssClass="forms" size="8" onfocus="clearText(this)" /> <s:select
				list="tradeList" cssClass="forms" name="trade" /> <s:select
				list="tradePerformedByList" cssClass="forms" name="performedBy" />
			<s:submit name="imageField" type="image"
				src="images/button_search.gif" onclick="runSearch( 'form1')" /></td>
		</tr>
		<tr>
			<td><s:if test="operator">
				<s:select list="flagStatusList" cssClass="forms" name="flagStatus" />
			</s:if> <s:if test="corporate">
				<s:select list="operatorList" cssClass="forms" name="operator"
					listKey="id" listValue="name" />
			</s:if> <s:if test="operator">
				<s:select list="stateLicensesList" cssClass="forms"
					name="stateLicensedIn" />
			</s:if><s:textfield name="taxID" cssClass="forms" size="9"
				onfocus="clearText(this)" /> <span class="redMain">*must be
			9 digits</span></td>
		</tr>
		<tr>
			<s:if test="operator">
				<td><s:select list="worksInList" cssClass="forms"
					name="worksIn" /></td>
			</s:if>
		</tr>
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
		<s:if test="pqfVisible">
			<td>PQF</td>
		</s:if>
		<s:if test="desktopVisible">
			<td>Desktop</td>
		</s:if>
		<s:if test="daVisible">
			<td>DA</td>
		</s:if>
		<s:if test="officeVisible">
			<td>Office</td>
		</s:if>
		<s:if test="operatorAccount.canSeeInsurance">
			<td>Ins. Certs</td>
		</s:if>
		<s:if test="operator">
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
			<td align="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><a
				href="ContractorView.action?id=<s:property value="[0].get('id')"/>"
				><s:property value="[0].get('name')" /></a></td>

			<s:if test="pqfVisible">
				<td align="center"><s:if test="[0].get('ca1_auditID') > 0">
					<s:if test="[0].get('ca1_auditStatus').equals('Exempt')">N/A</s:if>
					<s:else>
						<a
							href="pqf_view.jsp?auditID=<s:property value="[0].get('ca1_auditID')"/>"><img
							src="images/icon_PQF.gif" width="20" height="20" border="0"></a>
					</s:else>
				</s:if></td>
			</s:if>
			<s:if test="desktopVisible">
				<td>&nbsp; <s:if
					test="[0].get('ca2_auditID') > 0">
					<s:if test="[0].get('ca2_auditStatus').equals('Exempt')">N/A</s:if>
					<s:else>
						<a
							href="pqf_view.jsp?auditID=<s:property value="[0].get('ca2_auditID')"/>"><img
							src="images/icon_Desktop.gif" width="20" height="20" border="0"></a>
					</s:else>
				</s:if></td>
			</s:if>
			<s:if test="daVisible">
				<td align="center"><s:if test="[0].get('ca6_auditID') > 0">
					<s:if test="[0].get('ca6_auditStatus').equals('Exempt')">N/A</s:if>
					<s:else>
						<a
							href="pqf_view.jsp?auditID=<s:property value="[0].get('ca6_auditID')"/>"><img
							src="images/icon_DA.gif" width="20" height="20" border="0"></a>
					</s:else>
				</s:if></td>
			</s:if>
			<s:if test="officeVisible">
				<td align="center"><s:if test="[0].get('ca3_auditID') > 0">
					<s:if test="[0].get('ca3_auditStatus').equals('Exempt')">N/A</s:if>
					<s:else>
						<a
							href="pqf_view.jsp?auditID=<s:property value="[0].get('ca3_auditID')"/>"><img
							src="images/icon_Office.gif" width="20" height="20" border="0"></a>
					</s:else>
				</s:if></td>
			</s:if>
			<td align="center"><s:if test="operatorAccount.canSeeInsurance">
				&nbsp;&nbsp;<s:if test="[0].get('certs') > 0">
					<a
						href="contractor_upload_certificates.jsp?id=<s:property 
 					value="[0].get('id')"/>"><img
						src="images/icon_insurance.gif" width="20" height="20" border="0"></a>
				</s:if>
			</s:if></td>

			<td align="center"><s:if test="operator">
				<a href="con_redFlags.jsp?id=<s:property value="[0].get('id')"/>"
					title="Click to view Flag Color details"> <img
					src="images/icon_<s:property value="[0].get('lflag')"/>Flag.gif"
					width="12" height="15" border="0"></a>
			</s:if></td>
			<s:if test="operatorAccount.approvesRelationships">
				<pics:permission perm="ViewUnApproved">
					<td align="center">&nbsp;&nbsp;&nbsp;&nbsp;<s:property
						value="[0].get('workStatus')" />
				</pics:permission>
				</td>
			</s:if>
		</tr>
	</s:iterator>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

</body>
</html>
