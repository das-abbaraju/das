<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>User Permissions Matrix</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=20091105" />
</head>
<body>
<h1>User Permissions Matrix</h1>

<table class="report">
	<thead>
	<tr>
		<td colspan="2">User/Group</td>
		<s:iterator value="perms">
			<td><s:property value="description" /></td>
		</s:iterator>
	</tr>
	</thead>
	<s:iterator value="users" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><a href="UsersManage.action?accountId=<s:property value="accountID"/>&user.id=<s:property value="id"/>">
					<s:property value="name" /></a>
			</td>
			<s:iterator value="perms">
				<td>
				<s:iterator value="permissions">
					<s:if test="[1].equals(opPerm)">
						<s:if test="viewFlag==true">V</s:if>
						<s:if test="editFlag==true">E</s:if>
						<s:if test="deleteFlag==true">D</s:if>
						<s:if test="grantFlag==true">G</s:if>
					</s:if>
				</s:iterator>
				</td>
			</s:iterator>
		</tr>
	</s:iterator>
</table>

<div>
<ul>
<li>V = View</li>
<li>E = Edit</li>
<li>D = Delete</li>
<li>G = Grant</li>
</div>

</body>
</html>
