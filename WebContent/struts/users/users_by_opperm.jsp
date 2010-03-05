<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Users By OpPerm</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />

</head>
<body>
<h1>Users By OpPerm
<span class="sub"><s:property value="opPerm.description" /></span></h1>

<table class="report">
	<thead>
		<tr>
			<td>Account</td>
			<td>User</td>
		</tr>
	</thead>
	<s:iterator value="users">
		<tr>
			<td><s:property value="account.name" /></td>
			<td><s:property value="name" /></td>
		</tr>
	</s:iterator>
</table>
</body>
</html>
