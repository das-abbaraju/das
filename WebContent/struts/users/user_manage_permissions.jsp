<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Manage User Permissions</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=20091231" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=20091231" />
<s:include value="../jquery.jsp" />
<script type="text/javascript">
function savePerm(userID, permType, isChecked) {
	var data = {
		button: isChecked ? 'AddPerm' : 'RemovePerm',
		'user.id': userID,
		opPerm: permType,
		accountId: <s:property value="account.id" />
	};

	$.ajax({
		url: 'ManageUserPermissionsAjax.action',
		data: data,
		success: function(text, status){
			$.gritter.add({
				title: isChecked ? 'Added New Permission' : 'Removed Permission',
				text: (text).replace("Contractor", "") // Remove "Contractor" from the permission types
			})
		}
	});
}
</script>
</head>
<body>
<h1>Manage User Permissions</h1>
<s:include value="../actionMessages.jsp" />

<s:form action="ManageUserPermissions" id="ManageUserPermissions">
	<s:hidden value="accountId" />
</s:form>
<table class="report">
	<thead>
		<tr>
			<td>&nbsp;</td>
			<td>User</td>
			<td>Admin</td>
			<td>Billing</td>
			<td>Safety</td>
			<td>Insurance</td>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="userList" status="stat" id="user">
			<s:if test="activeB">
				<tr>
					<td><s:property value="#stat.index + 1" />.</td>
					<td><a
						href="UsersManage.action?accountId=<s:property value="account.id"/>&user.id=<s:property value="id"/>"
						title="<s:property value="#user.account.name" />"><s:property
						value="name" /></a></td>
					<s:iterator
						value="@com.picsauditing.actions.users.ManageUserPermissions@permissionTypes"
						id="perm">
						<td class="center"><input type="checkbox"
							onchange="savePerm(<s:property value="#user.id" />, '<s:property value="#perm" />', this.checked);"
							<s:iterator value="#user.ownedPermissions" id="userPerm">
								<s:if test="#perm == #userPerm.opPerm">checked="checked"</s:if>
							</s:iterator> /></td>
					</s:iterator>
				</tr>
			</s:if>
		</s:iterator>
	</tbody>
</table>
</body>
</html>