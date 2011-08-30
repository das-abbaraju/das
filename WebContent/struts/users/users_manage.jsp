<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:text name="%{scope}.title" /><s:if test="user.id > 0">: <s:property value="user.name"/></s:if></title>
<link rel="stylesheet" type="text/css" media="screen"
	href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen"
	href="css/reports.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp" />
<script type="text/javascript" src="js/user_manage.js?v=<s:property value="version"/>"></script>

<script type="text/javascript">
var accountID = '<s:property value="account.id" />';
var currentUserID = 0;
<s:if test="user.id > 0">currentUserID = <s:property value="user.id"/>;</s:if>

var permTypes = new Array();
<s:iterator value="permissions.permissions">
	<s:if test="grantFlag == true">permTypes['<s:property value="opPerm"/>'] = new Array("<s:property value="opPerm.helpText"/>",<s:property value="opPerm.usesView()"/>,<s:property value="opPerm.usesEdit()"/>,<s:property value="opPerm.usesDelete()"/>);</s:if>
</s:iterator>
$(function(){
	$('#accountMoveSuggest').autocomplete('UsersManageAjax.action?user=<s:property value="user.id"/>&button=Suggest').result(function(event, data){
		$('#moveToAccount').val(data[1])
	});
	$('#departmentSuggest').autocomplete('UsersManageAjax.action?user=<s:property value="user.id"/>&button=Department').result(function(event, data){
		$('#departmentRole').val(data[3])
	});
});
</script>
<style type="text/css">
.user-password, .addableGroup, .addableMember {
	display: none;
}
div.autocomplete {
	position: absolute;
	width: 250px;
	background-color: white;
	border: 1px solid #888;
	margin: 0;
	padding: 0;
}

div.autocomplete ul {
	list-style-type: none;
	margin: 0;
	padding: 0;
}

div.autocomplete ul li.selected {
	background-color: #ffb;
}

div.autocomplete ul li {
	list-style-type: none;
	display: block;
	margin: 0;
	padding: 2px;
	cursor: pointer;
}
</style>
</head>
<body>
<h1><s:text name="%{scope}.title" /></h1>

<s:if test="account.contractor">
	<a href="ContractorView.action?id=<s:property value="account.id"/>"><s:property value="account.name" /></a>
</s:if>
<s:if test="account.operatorCorporate">
	<a href="FacilitiesEdit.action?id=<s:property value="account.id"/>"><s:property value="account.name" /></a>
</s:if>
<s:if test="account.assessment">
	<a href="AssessmentCenterEdit.action?id=<s:property value="account.id"/>"><s:property value="account.name" /></a>
</s:if>
<s:if test="account.admin">PICS</s:if>
&gt; <a href="UsersManage.action?account=<s:property value="account.id"/>"><s:text name="%{scope}.title" /></a>
<s:if test="user.id > 0">&gt; <a href="?user=<s:property value="user.id"/>"><s:property value="user.name" /></a>
</s:if>
<s:if test="user.id == 0">&gt; NEW USER</s:if>

<div id="manage_controls" <s:if test="user != null">style="display:none"</s:if>>
<s:if test="!account.contractor">
	<div id="search"><s:form id="form1" method="get">
		<input type="hidden" name="button" value="Search">
		<button class="picsbutton positive" type="submit"><s:text name="button.Search" /></button>
		<br />
		<div class="filterOption">
		<h4><s:text name="%{scope}.Type" />:</h4>
		<s:hidden name="account.id" value="%{account.id}" />
		<s:radio name="isGroup"
			list="#{'Yes':'Groups', 'No':'Users', '':'Both'}" value="isGroup" />
		</div>
		<div class="filterOption">
		<h4><s:text name="global.Status" />:</h4>
		<s:radio name="isActive"
			list="#{'Yes':'Active', 'No':'Inactive', '':'All'}" value="isActive" />
		</div>
	</s:form>
	<div class="clear"></div>
	</div>
</s:if>
<div style="margin:5px 0 5px 0; list-style: none;">
	<s:if test="!account.contractor">
		<a class="add"
			href="<s:property value="scope" />!add.action?account=<s:property value="account.id"/>&isActive=<s:property value="isActive"/>&isGroup=<s:property value="isGroup"/>&userIsGroup=Yes"
			><s:text name="%{scope}.addGroup" /></a>
	</s:if>
	<a class="add" 
		href="<s:property value="scope" />!add.action?account=<s:property value="account.id"/>&isActive=<s:property value="isActive"/>&isGroup=<s:property value="isGroup"/>&userIsGroup=No"
		><s:text name="%{scope}.addUser" /></a>
	<s:if test="!account.contractor">
		<a class="preview"
			href="ReportUserPermissionMatrix.action?accountID=<s:property value="account.id"/>"><s:text name="ReportUserPermissionMatrix.title" /></a>
	</s:if>
	<s:if test="account.contractor && account.users.size() > 1">
		<a class="edit" href="ManageUserPermissions.action?id=<s:property value="account.id"/>"><s:text name="ManageUserPermissions.title" /></a>
	</s:if>
</div>

<table>
	<tr>
		<td>
			<table class="report">
				<thead>
					<tr>
						<td>&nbsp;</td>
						<td colspan="2"><s:text name="%{scope}.UserGroup" /></td>
						<td><s:text name="User.lastLogin" /></td>
					</tr>
				</thead>
				<s:iterator value="userList" status="stat">
					<tr>
						<td class="right"><s:property value="#stat.count" />.</td>
						<s:if test="get('isGroup') == 'Yes'">
							<td><s:text name="%{scope}.Group" /></td>
							<td style="font-weight: bold"><a
								href="?account=<s:property value="get('accountID')"/>&user=<s:property value="get('id')"/>&isActive=<s:property value="[1].isActive"/>&isGroup=<s:property value="[1].isGroup"/>"><s:property
								value="get('name')" /></a></td>
							<td><s:text name="global.NA" /></td>
						</s:if>
						<s:else>
							<td><s:text name="%{scope}.User" /></td>
							<td><a
								href="?account=<s:property value="get('accountID')"/>&user=<s:property value="get('id')"/>&isActive=<s:property value="[1].isActive"/>&isGroup=<s:property value="[1].isGroup"/>"
								class="userActive<s:property value="get('isActive')" />"><s:property value="get('name')" /></a></td>
							<td>
								<s:if test="get('lastLogin') != null">
									<s:date name="get('lastLogin')" />
								</s:if>
								<s:else><s:text name="%{scope}.never" /></s:else>
							</td>
						</s:else>
					</tr>
				</s:iterator>
			</table>
		</td>
		<td>
			<table>
				<tr><td>&nbsp;</td></tr>
				<s:iterator value="userList" status="stat">
					<tr>
						<td>
							<s:if test="locked">
								<a href="?account=<s:property value="account.id"/>&user=<s:property value="id"/>&isActive=<s:property value="[1].isActive"/>&isGroup=<s:property value="[1].isGroup"/>"><span title="This user account is locked.<pics:permission perm="EditAccountDetails"> Click this icon to Manage this user's details.</pics:permission>"><img src="images/lock.gif" style="padding-left:5px;" width="15" height="15" alt="This user account is locked" /></span></a>
							</s:if>
							<s:else>
								&nbsp;
							</s:else>
						</td>
					</tr>
				</s:iterator>
			</table>
		</td>
	</tr>
</table>
</div>


<div id="user_edit">
<s:include value="../actionMessages.jsp" />
<s:if test="user != null">
	<div style="margin-bottom: 10px;">
		<button class="picsbutton" onclick="showUserList();">&lt;&lt; Back to User List</button>
	</div>
	<s:form id="UserSave">
		<s:if test="user.locked">
			<div class="alert">This user account is locked.
				<pics:permission perm="EditAccountDetails">
					<span title="<s:text name="%{scope}.help.Unlock" />Press this button to unlock this user's account">
						<s:submit method="unlock" cssClass="picsbutton negative" value="%{getText(scope + '.button.UnlockThisAccount')}" />
					</span>
				</pics:permission>
			</div>
		</s:if>
		<s:hidden name="user" />
		<s:hidden name="account" />
		<s:hidden name="isGroup" />
		<s:hidden name="isActive" />
		<s:hidden name="userIsGroup" />
		<fieldset class="form">
		<h2 class="formLegend"><s:if test="user.group">Group</s:if><s:else>User</s:else> Details</h2>
		<ol>
			<s:if test="account.users.size() > 1">
				<s:if test="user.id > 0">
				<li><label> <s:if test="user.group">Group</s:if> <s:else>User</s:else>
				#:</label> <s:property value="user.id" /></li>				
				<li><label>Date Created:</label> <s:date
					name="user.creationDate" format="MM/d/yyyy" /></li>
				</s:if>
			</s:if>
			<li><label>Display Name:</label> <s:textfield name="user.name"
				size="30" />
			</li>
			<s:if test="user.isGroup.toString() == 'No'">
				<li><label>Email:</label>
					<s:textfield name="user.email" size="40" />
				</li>
				<li><label>Username:</label>
					<s:textfield name="user.username" size="30" onchange="checkUsername(this.value);" />
					<span id="username_status"></span>
				</li>
				<s:if test="user.id == 0">
					<li><label>Send Activation Email</label>
						<s:checkbox id="sendActivationEmail" name="sendActivationEmail" />
					</li>
				</s:if>
				<li><label>Manually Set Password</label> 
					<input id="manual_password" type="checkbox" onclick="$('.user-password').toggle()">
				</li>
				<li class="user-password"><label>Password:</label>
					<s:password name="password1" value="" />
				</li>
				<li class="user-password">
					<label>Confirm Password:</label>
					<s:password name="password2" value="" />
				</li>
				<li><label for="user.phone">Phone:</label>
					<s:textfield name="user.phone" size="15" />
				</li>
				<li><label for="user.fax">Fax:</label>
					<s:textfield name="user.fax" size="15" />
				</li>
				<s:if test="user.account.demo || user.account.admin">
				<li><label for="user.locale">Language:</label> <s:select
					list="@com.picsauditing.jpa.entities.AppTranslation@getLocales()"
					name="user.locale" listValue="displayName"></s:select>
				</li>
				</s:if>
				<li><s:select name="user.timezone" value="user.timezone.iD" theme="form" 
					list="@com.picsauditing.util.TimeZoneUtil@TIME_ZONES"></s:select>
				</li>
				<s:if test="account.operatorCorporate">
					<s:hidden id="departmentRole" />
					<li><s:textfield id="departmentSuggest" name="user.department" size="15" theme="formhelp" />
					</li>
				</s:if>
				<s:if test="user.account.id != 1100">
				<li><label>&nbsp;Primary Contact:</label>
					<s:checkbox id="setPrimaryAccount" name="setPrimaryAccount" />
					<pics:fieldhelp title="Primary Contact">
						<p>Set User as Primary Contact</p>
					</pics:fieldhelp>
				</li>
				</s:if>		
				<s:if test="account.contractor">
					<li><label>User Role:</label>
						<s:checkbox id="conAdmin" name="conAdmin"/>
						<label for="conAdmin" class="checkbox">
						<b><s:property value="@com.picsauditing.access.OpPerms@ContractorAdmin.description"/></b>
						<i>(<s:property value="@com.picsauditing.access.OpPerms@ContractorAdmin.helpText"/>)</i></label></li>
					<li><label>&nbsp;</label> <s:checkbox
						id="conBilling" name="conBilling" /><label
						for="conBilling" class="checkbox"><b><s:property value="@com.picsauditing.access.OpPerms@ContractorBilling.description"/></b><i> (<s:property value="@com.picsauditing.access.OpPerms@ContractorBilling.helpText"/>)</i></label></li>
					<li><label>&nbsp;</label> <s:checkbox
						id="conSafety" name="conSafety" /><label
						for="conSafety" class="checkbox"><b><s:property value="@com.picsauditing.access.OpPerms@ContractorSafety.description"/></b><i> (<s:property value="@com.picsauditing.access.OpPerms@ContractorSafety.helpText"/>)</i></label></li>
					<li><label>&nbsp;</label> <s:checkbox
						id="conInsurance" name="conInsurance" /><label
						for="conInsurance" class="checkbox"><b><s:property value="@com.picsauditing.access.OpPerms@ContractorInsurance.description"/></b><i> (<s:property value="@com.picsauditing.access.OpPerms@ContractorInsurance.helpText"/>)</i></label></li>
					</s:if>
				<s:if test="user.id > 0">
					<li><label>Last Login:</label><s:if test="user.lastLogin != null"> <s:date name="user.lastLogin" /></s:if><s:else> never</s:else>
					</li>
				</s:if>
				<!-- CSR Shadowing -->
				<s:if test="csr && permissions.admin">
					<li><label>Shadow CSR:</label>
						<s:select list="csrs" listKey="user.id" listValue="user.name" headerKey="0" 
							headerValue="- Select CSR -" name="shadowID" value="%{user.shadowedUser != null ? user.shadowedUser.id : 0}" />
					</li>
				</s:if>
			</s:if>
			<s:if test="user.id > 0">
				<li><label>Active:</label>
					<s:radio theme="pics" list="#{'Yes':'Yes','No':'No'}" name="user.isActive"></s:radio>
				</li>
				<s:if test="permissions.isAdmin()">
					<!-- Move User to Account -->
					<s:hidden name="moveToAccount" id="moveToAccount" />
					<li><label>Move User to Account:</label>
						<s:textfield id="accountMoveSuggest" /><br/>
						<pics:fieldhelp title="Move User to Account">
							<p>The name of the account you wish to move the user to.  
							This field will autocomplete as you type.</p>
						</pics:fieldhelp>
						<s:submit method="move" cssClass="picsbutton" value="%{getText(scope + '.button.MoveUser')}" onclick="return confirm('%{getText(scope + '.confirm.Move')}');" />
					</li>
				</s:if>
				<s:if test="hasProfileEdit">
					<li><label>&nbsp;</label>
						<a class="picsbutton" href="?button=resetPassword&user=<s:property value="user.id"/>">Send Reset Password Email</a>
						<s:if test="!user.group">
							<pics:permission perm="SwitchUser">
								<a class="picsbutton" href="Login.action?button=login&switchToUser=<s:property value="user.id"/>">Switch to this User</a>
							</pics:permission>
						</s:if>
					</li>
				</s:if>
			</s:if>
			<s:else>
				<s:hidden name="user.isActive" value="Yes" />
			</s:else>
		</ol>
		</fieldset>
		<fieldset class="form submit">
			<s:submit method="save" cssClass="picsbutton positive" value="%{getText('button.Save')}" />
			<pics:permission perm="EditUsers" type="Delete">
				<s:if test="user.id > 0 && !account.contractor">
					<s:submit method="delete" cssClass="picsbutton negative" value="%{getText('button.Delete')}" onclick="return confirm('%{getText(scope + '.confirm.Delete')}');" />
				</s:if>
			</pics:permission>
		</fieldset>
	</s:form>
			
	<br clear="all">
	<s:if test="user.id > 0">
		<s:if test="!account.contractor">
			<s:if test="!user.superUser">
				<td class="column">
				</td>
				<div id="permissionReport" style="width: 600px"><s:include
					value="user_save_permissions.jsp" /></div>

				<div id="groupReport"><s:include
					value="user_save_groups.jsp" /></div>
			</s:if>

			<s:if test="user.group">
				<div id="memberReport"><s:include
					value="user_save_members.jsp" /></div>
			</s:if>
		</s:if>

		<s:if test="permissions.admin">
			<s:if test="user.group">
				<div id="userSwitch"><s:include
					value="user_save_userswitch.jsp" /></div>
			</s:if>
		</s:if>

		<s:if test="!user.group">
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
							<td><s:date name="loginDate" /></td>
							<td><a href="http://www.hostip.info/?spip=<s:property value="remoteAddress" />"><s:property value="remoteAddress" /></a></td>
							<td><s:if test="admin.id > 0">Login by <s:property
									value="admin.name" /> from <s:property
									value="admin.account.name" />
							</s:if> <s:if test="successful == 'N'">Incorrect password attempt</s:if>
							</td>
						</tr>
					</s:iterator>
				</tbody>
			</table>
		</s:if>
	</s:if>
</s:if>

</div>


</body>
</html>
