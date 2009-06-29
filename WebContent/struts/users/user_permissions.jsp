<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Permissions</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
</head>
<body>
<h1>Permissions & Login Info</h1>

<s:include value="userHeader.jsp" />

<table style="width: 100%">
	<tr>
		<td>
		<h3>Recent Logins</h3>
		<table class="report">
		<thead>
			<tr>
				<th>Login Date/Time</th>
				<th>IP Address</th>
				<th>Notes</th>
			</tr>
		</thead>
		<tbody>
		<s:iterator value="recentLogins">
			<tr>
				<td><s:date name="loginDate"/></td>
				<td><s:property value="remoteAddress"/></td>
				<td>
					<s:if test="admin.id > 0">Login by <s:property value="admin.name"/> from <s:property value="admin.account.name"/></s:if>
					<s:if test="successful == 'N'">Incorrect password attempt</s:if>
				</td>
			</tr>
		</s:iterator>
		</tbody>
		</table>
		<s:if test="permissions.operatorCorporate">
			<h3>Visible Audit/Policy Types</h3>
			<div>
			<ul>
				<s:iterator value="viewableAuditsList">
					<li><s:property value="auditName" /></li>
				</s:iterator>
			</ul>
			</div>
		</s:if>
		</td><td>
		<h3>Permissions</h3>
		<table class="report">
			<thead>
				<tr>
					<th>Permission Name</th>
					<th>View</th>
					<th>Edit</th>
					<th>Delete</th>
				</tr>
			</thead>
			<tbody>
			<s:iterator value="permissions.permissions">
				<tr>
					<td title="<s:property value="opPerm.helpText" />"><s:property value="opPerm.description" /></td>
					<td><s:if test="viewFlag">View</s:if></td>
					<td><s:if test="editFlag">Edit</s:if></td>
					<td><s:if test="deleteFlag">Delete</s:if></td>
				</tr>
			</s:iterator>
			</tbody>
		</table>
	</td>
	</tr>
</table>

<div class="clear"></div>
</body>
</html>
