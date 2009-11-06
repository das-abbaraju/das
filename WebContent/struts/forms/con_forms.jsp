<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>View Forms &amp; Documents</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=20091105" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=20091105" />
</head>
<body>
<s:include value="../contractors/conHeader.jsp"></s:include>

<table class="report">
	<thead>
		<tr>
			<th>Form</th>
			<th>Facility</th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="forms">
		<tr>
			<td><a href="forms/<s:property value="file"/>" target="_blank"><s:property value="formName"/></a></td>
			<td><s:property value="account.name"/></td>
		</tr>
		</s:iterator>
	</tbody>
</table>
</body>
</html>
