<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Accounts Manage</title>
<meta name="header_gif" content="header_manageAccounts.gif" />
<script language="JavaScript" SRC="js/Search.js"></script>
</head>
<body>
<s:form id="form1" method="get">
<table border="0" align="center" cellpadding="2" cellspacing="0">
<tr>
<td align="left">
<s:textfield name="name" cssClass="forms" size="8" onfocus="clearText(this)" />
<s:select name="industry" list="industryList" cssClass="forms" />
<s:select list="tradeList" cssClass="forms" name="trade" />
<s:select list="tradePerformedByList" cssClass="forms" name="performedBy" />
<s:submit name="imageField" type="image" src="images/button_search.gif" onclick="runSearch( 'form1')" onmouseover="MM_swapImage('imageField','','images/button_search_o.gif',1)" onmouseout="MM_swapImgRestore()" />
</td></tr>
<tr><td>
<s:select list="operatorList" cssClass="forms" name="operator" />
<s:textfield name="city" cssClass="forms" size="15" onfocus="clearText(this)" />
<s:select list="stateList" cssClass="forms" name="state" />
<s:textfield name="zip" cssClass="forms" size="5" onfocus="clearText(this)" />
</td></tr>
<tr><td>
<s:select list="certsOptions" cssClass="forms" name="certsOnly" />
<s:select list="visibleOptions" cssClass="forms" name="visible" />
<s:select list="stateLicensesList" cssClass="forms" name="stateLicensedIn" />
<s:textfield name="taxid" cssClass="forms" size="9" onfocus="clearText(this)" />
<span class="redMain">*must be 9 digits</span>
</td></tr>
<tr><td>
<s:select list="worksInList" cssClass="forms" name="worksIn" />
</table>
	<s:hidden name="showPage" value="1"/>
	<s:hidden name="startsWith" value = "sql.startsWith" />
	<s:hidden name="orderBy" />
</s:form>
<center><s:property value="report.startsWithLinksWithDynamicForm" escape="false"/></center>
<table width="657" height="40" border="0" cellpadding="0" cellspacing="0">
  <tr>
	<td align="right"><s:property value="report.pageLinksWithDynamicForm" escape="false"/></td>
  </tr>
</table>
<table border="0" cellpadding="1" cellspacing="1">
<tr bgcolor="#993300" class="whiteTitle">
	<td colspan="2">Contractor Name</td>
	<td></td>
	<td>Industry</td>
	<td>Trade</td>
	<td align="center" bgcolor="#6699CC"></td>
	<td align="center" bgcolor="#6699CC">PQF</td>
	<td align="center" bgcolor="#6699CC">Desktop</td>
	<td align="center" bgcolor="#6699CC">Office</td>
	<td align="center" bgcolor="#6699CC">Insur</td>
</tr>
<s:iterator value="data">
	<tr class="blueMain" <s:property value="color.nextBgColor" escape="false" />>
		<td align="right"><s:property value="color.counter" /></td>
		<td><a href="contractor_detail.jsp?id=<s:property value="[0].get('id')"/>" 
			class="blueMain"><s:property value="[0].get('name')"/></a>
		</td>
		<td>
			<a href="accounts_edit_contractor.jsp?id=<s:property value="[0].get('id')"/>" class="blueMain">Edit</a>
		</td>
		<td><s:property value="[0].get('industry')"/></td>
		<td><s:property value="[0].get('main_trade')"/></td>
		<td><a href="ConAuditList.action?id=<s:property value="[0].get('id')"/>">Audits</a></td>
		<td align="center"><s:if test="[0].get('ca1_auditID') > 0">
			<s:if test="[0].get('ca1_auditStatus').equals('Exempt')">N/A</s:if>
			<s:else><a 
				href="pqf_view.jsp?auditID=<s:property value="[0].get('ca1_auditID')"/>"><img 
				src="images/icon_PQF.gif" width="20" height="20" border="0"></a></s:else>
		</s:if></td>
		<td align="center"><s:if test="[0].get('ca2_auditID') > 0">
			<s:if test="[0].get('ca2_auditStatus').equals('Exempt')">N/A</s:if>
			<s:else><a 
				href="pqf_view.jsp?auditID=<s:property value="[0].get('ca2_auditID')"/>"><img 
				src="images/icon_Desktop.gif" width="20" height="20" border="0"></a></s:else>
		</s:if></td>
		<td align="center"><s:if test="[0].get('ca3_auditID') > 0">
			<s:if test="[0].get('ca3_auditStatus').equals('Exempt')">N/A</s:if>
			<s:else><a 
				href="pqf_view.jsp?auditID=<s:property value="[0].get('ca3_auditID')"/>"><img 
				src="images/icon_Office.gif" width="20" height="20" border="0"></a></s:else>
		</s:if></td>
		<td align="center"><s:if test="[0].get('certs') > 0"><a 
			href="contractor_upload_certificates.jsp?id=<s:property value="[0].get('id')"/>"><img 
			src="images/icon_insurance.gif" width="20" height="20" border="0"></a>
		</s:if></td>
	</tr>
</s:iterator>

</table>
<center>
<s:property value="report.pageLinksWithDynamicForm" escape="false"/>
</center>
</body>
</html>
