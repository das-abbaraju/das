<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Manage User Permissions</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp" />
<script type="text/javascript">
function savePerm(userID, permType, checkbox) {
	var data = {
		button: checkbox.checked ? 'AddPerm' : 'RemovePerm',
		'user.id': userID,
		opPerm: permType,
		accountId: <s:property value="account.id" />
	};

	$.getJSON('ManageUserPermissionsAjax.action', data, function(json) {
			if (json.reset == true)
				checkbox.checked = !checkbox.checked;
			$.gritter.add({
				title: json.title,
				text: json.msg
			})
		}
	);
}
</script>
</head>
<body>
<s:include value="../contractors/conHeader.jsp" />
<s:include value="../actionMessages.jsp" />

<s:form action="ManageUserPermissions" id="ManageUserPermissions">
	<s:hidden value="accountId" />
</s:form>

<table class="report">
	<thead>
		<tr>
			<td>&nbsp;</td>
			<td>User</td>
			<td><s:text name="OpPerms.ContractorAdmin.description" />
				<span class="block" style="position: relative;"><img src="images/help.gif" height="15" width="15"><span class="hoverhelp" style="bottom: 13px; left: -99px;">
					<s:text name="OpPerms.ContractorAdmin.helpText" />
				</span></span>
			</td>
			<td><s:property value="OpPerms.ContractorBilling.description" />
				<span class="block" style="position: relative;"><img src="images/help.gif" height="15" width="15"><span class="hoverhelp" style="bottom: 13px; left: -99px;">
					<s:property value="OpPerms.ContractorBilling.helpText" />
				</span></span>
			</td>
			<td><s:property value="OpPerms.ContractorSafety.description" />
				<span class="block" style="position: relative;"><img src="images/help.gif" height="15" width="15"><span class="hoverhelp" style="bottom: 13px; left: -99px;">
					<s:property value="OpPerms.ContractorSafety.helpText" />
				</span></span>
			</td>
			<td><s:property value="OpPerms.ContractorInsurance.description" />
				<span class="block" style="position: relative;"><img src="images/help.gif" height="15" width="15"><span class="hoverhelp" style="bottom: 13px; left: -99px;">
					<s:property value="OpPerms.ContractorInsurance.helpText" />
				</span></span>
			</td>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="userList" status="stat" id="user">
			<s:if test="activeB">
				<tr>
					<td><s:property value="#stat.index + 1" />.</td>
					<td><a
						href="UsersManage.action?account=<s:property value="account.id"/>&user=<s:property value="id"/>"
						title="<s:property value="#user.account.name" />"><s:property
						value="name" /></a></td>
					<s:iterator
						value="@com.picsauditing.actions.users.ManageUserPermissions@permissionTypes"
						id="perm">
						<td class="center"><input type="checkbox"
							onclick="savePerm(<s:property value="#user.id" />, '<s:property value="#perm" />', this);"
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