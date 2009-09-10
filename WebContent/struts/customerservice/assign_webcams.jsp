<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Assign Webcams</title>
<link rel="stylesheet" href="css/reports.css" />
<link rel="stylesheet" href="css/forms.css" />
<s:include value="../jquery.jsp" />
</head>
<body>
<h1>Assign Webcams</h1>

<s:include value="../actionMessages.jsp" />

<div class="left" id="audit_list">
<table class="report">
	<thead>
		<tr>
			<td>id</td>
			<td>Contractor</td>
			<td>Type</td>
		</tr>
	</thead>
	<s:iterator value="audits">
		<tr>
			<td><s:property value="id" /></td>
			<td><s:property value="contractorAccount.name" /></td>
			<td><s:property value="auditType.auditName" /></td>
		</tr>
	</s:iterator>
</table>
</div>

<br clear="all" />
</body>
</html>