<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Contractor Search</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
</head>
<body>
<h1>Contractor Search <span class="sub">Quick Version</span></h1>

<s:form id="form1" method="post">
	<s:hidden name="showPage" value="1" />
	<s:hidden name="startsWith" />
	<s:hidden name="orderBy" />
</s:form>

<s:property value="report.pageLinks" escape="false" />
<table class="box">
	<thead>
	<tr>
		<td>Contractor Name</td>
	</tr>
	</thead>
	<s:iterator value="data">
		<tr>
			<td><a
				href="ContractorView.action?id=<s:property value="[0].get('id')"/>"
				><s:property value="[0].get('name')" /></a></td>
		</tr>
	</s:iterator>
</table>
<s:property value="report.pageLinks" escape="false" />
</body>
</html>
