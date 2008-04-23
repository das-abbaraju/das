<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Contractor Search - Operator</title>
<script type="text/javascript" src="js/Search.js" />

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
<s:form id="form1" method="post">
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
				<s:select list="operatorList" cssClass="forms" name="operator" />
			</s:if> <s:select list="stateLicensesList" cssClass="forms"
				name="stateLicensedIn" /><s:textfield name="taxID" cssClass="forms"
				size="9" onfocus="clearText(this)" /> <span class="redMain">*must
			be 9 digits</span></td>
		</tr>
		<tr>
			<td><s:select list="worksInList" cssClass="forms" name="worksIn" />
			</td>
		</tr>
	</table>
	<s:hidden name="showPage" value="1" />
	<s:hidden name="startsWith" />
	<s:hidden name="orderBy" />
</s:form>

<table border="0" cellpadding="0" cellspacing="0" width="900">
	<tr>
		<td align="left"><s:property
			value="report.startsWithLinksWithDynamicForm" escape="false" /></td>
		<td align="right"><s:property
			value="report.pageLinksWithDynamicForm" escape="false" /></td>
	</tr>
</table>
<table border="0" cellpadding="1" cellspacing="1">
	<tr bgcolor="#003366" class="whiteTitle">
		<td colspan="2">Contractor Name</td>
		<s:if test="pqfVisible">
			<td align="center" bgcolor="#336699">PQF</td>
		</s:if>
		<s:if test="desktopVisible">
			<td align="center" bgcolor="#336699">Desktop</td>
		</s:if>
		<s:if test="daVisible">
			<td align="center" bgcolor="#336699">DA</td>
		</s:if>
		<s:if test="officeVisible">
			<td align="center" bgcolor="#336699">Office</td>
		</s:if>
		<s:if test="operatorAccount.canSeeInsurance">
			<td align="center" bgcolor="#6699CC">Ins. Certs</td>
		</s:if>
		<s:if test="operator">
			<td align="center" bgcolor="#6699CC"><a
				href="?orderBy=flag DESC" class="whiteTitle">Flag</a></td>
		</s:if>

		<s:if test="operatorAccount.approvesRelationships">
			<pics:permission perm="ViewUnApproved">
				<td align="center" bgcolor="#6699CC"><nobr>Approved</nobr></td>
			</pics:permission>
		</s:if>

	</tr>
	<s:iterator value="data">
		<tr class="blueMain"
			<s:property value="color.nextBgColor" escape="false" />>
			<td align="right"><s:property value="color.counter" /></td>
			<td align="center"><a
				href="ContractorView.action?id=<s:property value="[0].get('id')"/>"
				class="blueMain"><s:property value="[0].get('name')" /></a></td>
			<s:if test="pqfVisible">
				<td align="center"><a
					href="pqf_view.jsp?auditID=<s:property 
			value="[0].get('ca1_auditID')"/>"><img
					src="images/icon_PQF.gif" width="20" height="20" border="0"></a>
				</td>
			</s:if>
			<s:if test="desktopVisible">
				<td align="center">&nbsp;<a
					href="pqf_view.jsp?auditID=<s:property 
			value="[0].get('ca2_auditID')"/>"><img
					src="images/icon_Desktop.gif" width="20" height="20" border="0"></a>
				</td>
			</s:if>
			<s:if test="daVisible">
				<td align="center"><a
					href="pqf_view.jsp?auditID=<s:property 
			value="[0].get('ca6_auditID')"/>"><img
					src="images/icon_DA.gif" width="20" height="20" border="0"></a></td>
			</s:if>
			<s:if test="officeVisible">
				<td align="center"><a
					href="pqf_view.jsp?auditID=<s:property 
			value="[0].get('ca3_auditID')"/>"><img
					src="images/icon_Office.gif" width="20" height="20" border="0"></a>
				</td>
			</s:if>
			<td align="center"><s:if test="operatorAccount.canSeeInsurance">
				&nbsp;&nbsp;<s:if test="[0].get('certs') > 0">
					<a
						href="contractor_upload_certificates.jsp?id=<s:property 
 					value="[0].get('id')"/>"><img
						src="images/icon_insurance.gif" width="20" height="20" border="0"></a>
				</s:if>
				<s:else>
					<img src="images/notOkCheck.gif" width="19" height="15"
						alt='Non Uploaded'>
				</s:else>
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
<center><s:property value="report.pageLinksWithDynamicForm"
	escape="false" /></center>
</body>
</html>
