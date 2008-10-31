<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Multiple User Login</title>
<script src="js/prototype.js" type="text/javascript"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
</head>
<body>
<h1>Multi-User Login</h1>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<div class="helpOnRight">
These Users have used more than 3 unique IP addresses to login in the past 3 months.</div>
<table class="report" style="clear : none;">
	<thead>
	<tr>
		<td></td>
	    <th><a href="?orderBy=a.name" >Name</a></th>
	    <td><a href="?orderBy=contact" >Contact</a></td>
	    <td><a href="?orderBy=count DESC" >Used</a></td>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
	<tr>
		<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
		<td><a href="UsersManage.action?accountId=<s:property value="get('id')"/>">
		<s:property value="get('name')"/></td>
		<td><a href="UsersManage.action?accountId=<s:property value="get('id')"/>&user.id=<s:property value="get('userId')"/>">
		<s:property value="get('contact')"/></a></td>
		<td class="center"><s:property value="get('count')"/></td>
	</tr>
	</s:iterator>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</body>
</html>
