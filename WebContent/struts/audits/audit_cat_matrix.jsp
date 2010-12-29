<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Manual Audit Matrix</title>
<s:include value="../jquery.jsp"/>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
</head>
<body>
<h1>Audit Category Matrix</h1>


<s:include value="../actionMessages.jsp"></s:include>

<s:form id="form1" method="post">
<table class="report">
	<thead>
		<tr>
			<td>&nbsp;</td>
			<td>Competency</td>
			<s:iterator value="auditType.topCategories">
				<td><s:property value="name"/></td>
			</s:iterator>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="operatorCompetencies" status="stat" id="user">
				<tr>
					<td><s:property value="#stat.index + 1" />.</td>
					<td><s:property value="category" />/<s:property value="label"/></td>
					<s:iterator value="auditType.topCategories">
					  <td class="center"><input type="checkbox"></input>	</td>
					</s:iterator>
				</tr>
		</s:iterator>
	</tbody>
</table>
</s:form>

</body>
</html>