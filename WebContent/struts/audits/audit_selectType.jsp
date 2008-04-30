<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@ include file="../../includes/main.jsp"%>
<html>
<head>
<title>Audit Management</title>
</head>
<body>
<h1>Audit Management</h1>
<s:include value="pqf_nav.jsp"/>
<table border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td align="center" class="redMain">Please select an Audit Type:
		<table border="0">
		<s:iterator value="auditTypes">
			<tr>
				<td><a class="blueMain" href="pqf_editSubCategories.jsp?auditTypeID=<s:property value="auditTypeID"/>"><s:property value="auditName"/></a></td>
			</tr>
		</s:iterator>
		</table>
		</td>
	</tr>
</table>
</body>
</html>
