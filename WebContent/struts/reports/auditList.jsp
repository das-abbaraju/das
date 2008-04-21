<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Audit List</title>
</head>
<body>
<h1>Audit List</h1>

<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr>
	<td align="left"><s:property value="report.startsWithLinksWithDynamicForm" escape="false"/></td>
	<td align="right"><s:property value="report.pageLinksWithDynamicForm" escape="false"/></td>
  </tr>
</table>
<table border="0" cellpadding="1" cellspacing="1" align="center" width="100%">
	<tr bgcolor="#003366" class="whiteTitle">
		    <td colspan="2"><a href="?orderBy=a.name" class="whiteTitle">Contractor</a></td>
		    <td align="center"><a href="?orderBy=at.auditName DESC" class="whiteTitle">Type</a></td>
		    <td align="center"><a href="?orderBy=ca.createdDate DESC" class="whiteTitle">Created</a></td>
		    <td align="center"><a href="?orderBy=ca.auditStatus DESC" class="whiteTitle">Status</a></td>
		    <td align="center"><a href="?orderBy=au.name" class="whiteTitle">Auditor</a></td>
	</tr>
	<s:iterator value="data">
	<tr class="blueMain" <s:property value="color.nextBgColor" escape="false" />>
		<td align="right"><s:property value="color.counter" /></td>
		<td><a href="pqf_view.jsp?auditID=<s:property value="[0].get('auditID')"/>" 
			class="blueMain"><s:property value="[0].get('name')"/></a>
		</td>
		<td><s:property value="[0].get('auditName')"/></td>
		<td><s:date name="[0].get('createdDate')" format="M/d/yy" /></td>
		<td><s:property value="[0].get('auditStatus')"/></td>
		<td><s:property value="[0].get('auditorName')"/></td>
	</tr>
	</s:iterator>
</table>
<center>
<s:property value="report.pageLinksWithDynamicForm" escape="false"/>
</center>

</body>
</html>
