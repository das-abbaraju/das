<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Contractor Search</title>
</head>
<body>
<h1>Contractor Search <span class="sub">Quick Version</span></h1>

<s:form id="form1" method="post">
	<s:hidden name="showPage" value="1" />
	<s:hidden name="startsWith" />
	<s:hidden name="orderBy" />
</s:form>

<s:property value="report.pageLinks" escape="false" />
<table border="0" cellpadding="1" cellspacing="1">
	<tr bgcolor="#993300" class="whiteTitle">
		<td>Contractor Name</td>
	</tr>
	<s:iterator value="data">
		<tr class="blueMain"
			<s:property value="color.nextBgColor" escape="false" />>
			<td><a
				href="ContractorView.action?id=<s:property value="[0].get('id')"/>"
				class="blueMain"><s:property value="[0].get('name')" /></a></td>
		</tr>
	</s:iterator>
</table>
<s:property value="report.pageLinks" escape="false" />
</body>
</html>
