<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Accounts Manage</title>
<meta name="header_gif" content="header_manageAccounts.gif" />
<script language="JavaScript" SRC="js/Search.js"></script>
</head>
<body>
<s:form id="form1" method="post">
<table border="0" align="center" cellpadding="2" cellspacing="0">
<tr>
<td align="left">
<s:textfield name="name" cssClass="forms" size="8" onfocus="clearText(this)" />
<s:select list="industryList" cssClass="forms" value="industry"/>
</td></tr>

</table>
	<s:hidden name="showPage" value="1"/>
	<s:hidden name="startsWith" value = "sql.startsWith" />
	<s:hidden name="orderBy" />
</s:form>

<table border="0" cellpadding="1" cellspacing="1">
<tr bgcolor="#993300" class="whiteTitle">
	<td colspan="2">Contractor</td>
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
	</tr>
</s:iterator>

</table>

</body>
</html>
