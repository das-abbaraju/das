<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Report Sales</title>
<script src="js/prototype.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />

</head>
<body>
<h1>Report of Sales Representatives</h1>

<table class="report">
	<thead>
		<tr>
			<td>User</td>
			<td>Account</td>
			<td>Sales</td>
			<td># of Contractors Registered</td>
		</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td><s:property value="get('userName')" /></td>
			<td><a href="FacilitiesEdit.action?id=<s:property value="[0].get('id')"/>&type=<s:property value="[0].get('type')"/>"><s:property value="get('accountName')" /></a></td>
			<td><s:property value="get('ownerPercent')" /> %</td>
			<td><s:property value="get('countCons')" /></td>
		</tr>
	</s:iterator>
</table>
</body>
</html>
