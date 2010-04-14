<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Job Competency Matrix</title>
<link rel="stylesheet" type="text/css" media="screen"
	href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen"
	href="css/reports.css?v=<s:property value="version"/>" />

<s:include value="../jquery.jsp" />
</head>
<body>
<h1>Job Competency Matrix</h1>

<table class="report">
	<thead>
		<tr>
			<s:iterator value="competencies" id="competency">
				<th><s:property value="#competency.label" /></th>
			</s:iterator>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="roles" id="role">
			<tr>
				<s:iterator value="competencies" id="competency">
					<td><s:checkbox></s:checkbox></td>
				</s:iterator>
			</tr>
		</s:iterator>
	</tbody>
</table>
</body>
</html>