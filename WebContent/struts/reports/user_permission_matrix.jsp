<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>User Permissions Matrix</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
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
					<s:if test="viewFlag">V</s:if>
					<s:if test="editFlag">E</s:if>
					<s:if test="deleteFlag">D</s:if>
					<s:if test="grantFlag">G</s:if>
					</s:if>
				</s:iterator>
				</td>
			</s:iterator>
		</tr>
	</s:iterator>

</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</body>
</html>
