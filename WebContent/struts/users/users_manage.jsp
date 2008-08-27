<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@page language="java" errorPage="../../exception_handler.jsp"%>
<html>
<head>
<title>Manage Users</title>
<script type="text/javascript" src="js/prototype.js"></script>
<script type="text/javascript" src="js/scriptaculous/scriptaculous.js?load=effects"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css"/>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<script type="text/javascript">
var accountID = <s:property value="accountId"/>;
var currentUserID = 0;
<s:if test="user.id > 0">currentUserID = <s:property value="user.id"/>;</s:if>

var permTypes = new Array();
<s:iterator value="permissions.permissions">
	<s:if test="grantFlag == true">permTypes['<s:property value="opPerm"/>'] = new Array("<s:property value="opPerm.helpText"/>",<s:property value="opPerm.usesView()"/>,<s:property value="opPerm.usesEdit()"/>,<s:property value="opPerm.usesDelete()"/>);</s:if>
</s:iterator>

function showPermDesc(item) {
	var x = $F(item);
	$('permDescription').innerHTML = permTypes[x][0];
	$('new_viewFlag').disabled = !permTypes[x][1];
	$('new_editFlag').disabled = !permTypes[x][2];
	$('new_deleteFlag').disabled = !permTypes[x][3];
}

function addPermission() {
	$('addPermissionButton').innerHTML = 'Processing: <img src="images/ajax_process.gif" />';
	var opPerm = $('newPermissionSelect').value;
	pars = 'button=AddPerm&accountId='+accountID+'&user.id='+currentUserID+'&opPerm='+opPerm;
	var myAjax = new Ajax.Updater('permissionReport', 'UserAccessSaveAjax.action', {method: 'post', parameters: pars});
}

function removePermission(accessId) {
	$('permissionReport').innerHTML = 'Processing: <img src="images/ajax_process.gif" />';
	pars = 'button=RemovePerm&accessId='+accessId+'&user.id='+currentUserID;
	var myAjax = new Ajax.Updater('permissionReport', 'UserAccessSaveAjax.action', {method: 'post', parameters: pars});
}

function updatePermission(accessId, typeName, theCheckbox) {
	pars = 'accessId='+accessId+'&type='+typeName+'&permValue='+theCheckbox.checked;
	var myAjax = new Ajax.Updater('', 'UserAccessUpdateAjax.action', {method: 'post', parameters: pars,
			onSuccess: function(transport) {
				new Effect.Highlight($('permission_'+accessId),{duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});
			}
	});
}

function addGroup(groupID) {
	$('groupReport').innerHTML = 'Processing: <img src="images/ajax_process.gif" />';
	pars = 'button=AddGroup&groupId='+groupID+'&user.id='+currentUserID;
	var myAjax = new Ajax.Updater('groupReport', 'UserGroupSaveAjax.action', {method: 'post', parameters: pars});
}

function removeGroup(userGroupID) {
	$('groupReport').innerHTML = 'Processing: <img src="images/ajax_process.gif" />';
	pars = 'button=RemoveGroup&userGroupId='+userGroupID+'&user.id='+currentUserID;
	var myAjax = new Ajax.Updater('groupReport', 'UserGroupSaveAjax.action', {method: 'post', parameters: pars});
}

function addMember(memberId) {
	$('memberReport').innerHTML = 'Processing: <img src="images/ajax_process.gif" />';
	pars = 'button=AddMember&memberId='+memberId+'&user.id='+currentUserID;
	var myAjax = new Ajax.Updater('memberReport', 'UserGroupSaveAjax.action', {method: 'post', parameters: pars});
}

function removeMember(userGroupID) {
	$('memberReport').innerHTML = 'Processing: <img src="images/ajax_process.gif" />';
	pars = 'button=RemoveMember&userGroupId='+userGroupID+'&user.id='+currentUserID;
	var myAjax = new Ajax.Updater('memberReport', 'UserGroupSaveAjax.action', {method: 'post', parameters: pars});
}

function checkUsername(username, userID) {
	$('UserSave').writeAttribute('disabled','true');
	$('username_status').innerHTML = 'checking availability of username...';
	pars = 'userID='+userID+'&username='+username;
	var myAjax = new Ajax.Updater('username_status', 'user_ajax.jsp', {method: 'get', parameters: pars,
		onComplete: function(transport) {
		if($('username_status').innerHTML.indexOf('is NOT available. Please choose a different username.') == -1)
		{
			$('UserSave').writeAttribute('disabled', null);						
		}
	}
	});
}

</script>
</head>
<body>
<h1>Manage User Accounts</h1>

<table border="0">
<tr>
	<td colspan="2" align="center">
		<div id="search">
		<div id="showSearch" onclick="showSearch()" <s:if test="filtered">style="display: none"</s:if> ><a href="#">Show Filter Options</a></div>
		<div id="hideSearch" <s:if test="!filtered">style="display: none"</s:if> ><a href="#" onclick="hideSearch()">Hide Filter Options</a></div>
		<s:form id="form1" method="post" cssStyle="%{filtered ? '' : 'display: none'}">
		
		<pics:permission perm="AllOperators">
			Operator:<span id="operators"><s:include value="../operators/facilitySelect.jsp" />
			</span><br />
		</pics:permission>
			Type:
				<s:select name="isGroup" cssClass="blueSmall"
				       headerKey="" headerValue="All"
				       list="#{'Yes':'Groups', 'No':'Users'}"
				       value="isGroup"
				/>
			Status:
				<s:select name="isActive" cssClass="blueSmall"
				       headerKey="" headerValue="All"
				       list="#{'Yes':'Active', 'No':'Inactive'}"
				       value="isActive"
				/>
			<s:submit name="imageField" type="image" src="images/button_search.gif" onclick="runSearch('form1')" />
			<s:hidden name="showPage" value="1"/>
			<s:hidden name="startsWith" value=""/>
			<s:hidden name="orderBy"  value="name"/>
		</s:form>
		</div>
	</td>
</tr>
<tr><td>
</td></tr>
<tr>
	<td colspan="3" align="center" class="blueMain">
	<s:hidden name="user.id" />
	<s:hidden name="" />
		<a href="?button=newUser&accountId=<s:property value="accountId"/>&user.accountID=<s:property value="accountId"/>&isActive=<s:property value="[1].isActive"/>&isGroup=<s:property value="[1].isGroup"/>&user.isGroup=Yes">Add Group</a>
		&nbsp;&nbsp;
		<a href="?button=newUser&accountId=<s:property value="accountId"/>&user.accountID=<s:property value="accountId"/>&isActive=<s:property value="[1].isActive"/>&isGroup=<s:property value="[1].isGroup"/>&user.isGroup=No">Add User</a>
	</td>
</tr>
<tr valign="top"><td>
	<table class="report">
		<thead>
		<tr>
			<td>&nbsp;</td>
			<td colspan="2">User/Group</td>
			<td>Last Login</td>
		</tr>
		</thead>
	<s:iterator value="userList" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + 1" />.</td>
			<s:if test="group">
				<td>G</td>
				<td style="font-weight: bold"><a href="?accountId=<s:property value="accountId"/>&user.id=<s:property value="id"/>&isActive=<s:property value="[1].isActive"/>&isGroup=<s:property value="[1].isGroup"/>"><s:property value="name"/></a></td>
				<td>N/A</td>
			</s:if>
			<s:else>
				<td>U</td>
				<s:if test="isActive.toString().equals('Yes')">
					<td>
						<a href="?accountId=<s:property value="accountId"/>&user.id=<s:property value="id"/>&isActive=<s:property value="[1].isActive"/>&isGroup=<s:property value="[1].isGroup"/>"><s:property value="name"/>*</a>
					</td>
				</s:if>
				<s:else>
					<td class="inactive">
						<a href="?accountId=<s:property value="accountId"/>&user.id=<s:property value="id"/>&isActive=<s:property value="[1].isActive"/>&isGroup=<s:property value="[1].isGroup"/>"><s:property value="name"/></a>
					</td>
				</s:else>
				<td>
					<s:if test="lastLogin != null">
						<s:date name="lastLogin" format="MM/dd/yy"/>
					</s:if>
					<s:else>never</s:else>
				</td>
			</s:else>
		</tr>
	</s:iterator>
	</table>
</td>
<td id="editUser" class="blueMain" style="margin: 30px; padding: 30px; vertical-align: top;">

<s:include value="../actionMessages.jsp" />

<div id="ajaxstatus" style="height: 20px;"></div>
<s:form action="UserSave">
	<s:hidden name="user.id" />
	<s:hidden name="accountId" />
	<s:hidden name="isGroup" />
	<s:hidden name="isActive" />
	<s:hidden name="user.accountID" />
	<s:hidden name="user.isGroup" />
<s:if test="user.id > 0">	
<pics:permission perm="EditUsers" type="Edit">
	<div class="buttons">
		<button class="positive" type="submit" name="button" value="Save">Save</button>
	</div>
</pics:permission>
</s:if>
<table class="forms">
<s:if test="user.id > 0">
	<tr>
		<th><s:if test="user.group">Group</s:if><s:else>User</s:else> #</th>
		<td><s:property value="user.id"/></td>
	</tr>
	<tr>
		<th>Date created</th>
		<td><s:date name="user.dateCreated" format="MM/d/yyyy" />
			<s:if test="!user.group"><a href="#" onclick="sendWelcomeEmail(); return false;">Send Welcome Email</a></s:if>
		</td>
	</tr>

	<tr>
		<th>Display name</th>
		<td><s:textfield name="user.name" size="30"/></td>
	</tr>
	</s:if>
<s:if test="!user.group">
	<tr>
		<th>Email</th>
		<td><s:textfield name="user.email" size="40"/></td>
	</tr>
	<tr> 
		<td colspan="2">&nbsp;</td>
	</tr>
	<tr>
		<th>Username</th>
		<td><s:textfield name="user.username" size="30" onblur="checkUsername(this.value, user.id);"/>
			<span id="username_status"></span>
		</td>
	</tr>
	<tr>
		<th>Password</th>
		<td><s:textfield name="user.password" /></td>
	</tr>
	<s:if test="user.id > 0">
		<tr>
			<th>Last login</th>
			<td><s:date name="user.lastLogin" /></td>
		</tr>
	</s:if>

	<tr>
		<th>Active</th>
		<td><s:radio theme="pics" list="#{'Yes':'Yes','No':'No'}" name="user.isActive"></s:radio> </td>
	</tr>
</s:if>
</table>

</s:form>

<s:if test="user.id > 0">
	<s:if test="!user.group">
		<pics:permission perm="SwitchUser">
			<div><a href="login.jsp?switchUser=<s:property value="user.username"/>">Switch to this User</a></div>
		</pics:permission>
	</s:if>
	
	<s:if test="!user.superUser">
		<div id="permissionReport">
			<s:include value="user_save_permissions.jsp" />
		</div>
		
		<div id="groupReport">
			<s:include value="user_save_groups.jsp" />
		</div>
	</s:if>
	
	<s:if test="user.group">
		<div id="memberReport">
			<s:include value="user_save_members.jsp" />
		</div>
	</s:if>
</s:if>

</td>
</tr>
</table>
</body>
</html>
