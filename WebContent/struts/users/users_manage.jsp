<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@page language="java" errorPage="../../exception_handler.jsp"%>
<html>
<head>
<title>Manage Users</title>
<script type="text/javascript" src="js/prototype.js"></script>
<script type="text/javascript" src="js/scriptaculous/scriptaculous.js?load=effects,controls"></script>
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
}

function addPermission() {
	$('addPermissionButton').innerHTML = 'Processing: <img src="images/ajax_process.gif" />';
	var opPerm = $('newPermissionSelect').value;
	pars = 'button=AddPerm&user.id='+currentUserID+'&opPerm='+opPerm+'&accountId='+accountID;
	var myAjax = new Ajax.Updater('permissionReport', 'UserAccessSaveAjax.action', {method: 'post', parameters: pars});
}

function removePermission(accessId) {
	$('permissionReport').innerHTML = 'Processing: <img src="images/ajax_process.gif" />';
	pars = 'button=RemovePerm&accessId='+accessId+'&user.id='+currentUserID+'&accountId='+accountID;
	var myAjax = new Ajax.Updater('permissionReport', 'UserAccessSaveAjax.action', {method: 'post', parameters: pars});
}

function updatePermission(accessId, typeName, theValue) {

	pars = 'accessId='+accessId+'&type='+typeName+'&permValue='+theValue.value;
	var myAjax = new Ajax.Updater('', 'UserAccessUpdateAjax.action', {method: 'post', parameters: pars,
			onSuccess: function(transport) {
				new Effect.Highlight($('permission_'+accessId),{duration: 0.75, startcolor:'#FFFF11'});
			}
	});
}

function addGroup(groupID) {
	$('groupReport').innerHTML = 'Processing: <img src="images/ajax_process.gif" />';
	pars = 'button=AddGroup&groupId='+groupID+'&user.id='+currentUserID+'&accountId='+accountID;
	var myAjax = new Ajax.Updater('groupReport', 'UserGroupSaveAjax.action', {method: 'post', parameters: pars});
}

function removeGroup(userGroupID) {
	$('groupReport').innerHTML = 'Processing: <img src="images/ajax_process.gif" />';
	pars = 'button=RemoveGroup&userGroupId='+userGroupID+'&user.id='+currentUserID+'&accountId='+accountID;
	var myAjax = new Ajax.Updater('groupReport', 'UserGroupSaveAjax.action', {method: 'post', parameters: pars});
}

function addMember(memberId) {
	$('memberReport').innerHTML = 'Processing: <img src="images/ajax_process.gif" />';
	pars = 'button=AddMember&memberId='+memberId+'&user.id='+currentUserID+'&accountId='+accountID;
	var myAjax = new Ajax.Updater('memberReport', 'UserGroupSaveAjax.action', {method: 'post', parameters: pars});
}

function removeMember(userGroupID) {
	$('memberReport').innerHTML = 'Processing: <img src="images/ajax_process.gif" />';
	pars = 'button=RemoveMember&userGroupId='+userGroupID+'&user.id='+currentUserID+'&accountId='+accountID;
	var myAjax = new Ajax.Updater('memberReport', 'UserGroupSaveAjax.action', {method: 'post', parameters: pars});
}

function checkUsername(username) {
	$('UserSave').writeAttribute('disabled','true');
	$('username_status').innerHTML = 'checking availability of username...';
	pars = 'userID='+currentUserID+'&username='+username;
	var myAjax = new Ajax.Updater('username_status', 'user_ajax.jsp', {method: 'get', parameters: pars,
		onComplete: function(transport) {
		if($('username_status').innerHTML.indexOf('is NOT available. Please choose a different username.') == -1)
		{
			$('UserSave').writeAttribute('disabled', null);						
		}
	}
	});
}

function loadAutocompleter(){}

<s:if test="user.group">
	loadAutocompleter = function() {
		var myAjax = new Ajax.Autocompleter('userSwitchAdd', 'userSwitchAutocomplete', 'UserSearchAjax.action', 
				{
					paramName:'filter.search',
					minChars: 3,
					updateElement: addUserSwitch
				} 
		);
	};

	function addUserSwitch(li) {
		$('userSwitchAdd').value = '';
		var pars = 'button=AddSwitchFrom&user.id='+currentUserID+'&memberId='+li.id+"&accountId="+accountID;
		var myAjax = new Ajax.Updater('userSwitch', 'UserGroupSaveAjax.action',
				{
					method: 'post',
					parameters: pars,
					onComplete: function() { loadAutocompleter(); $('userSwitchAdd').focus(); }
				}
		);
	}

	function removeUserSwitch(userId) {
		var pars = 'button=RemoveSwitchFrom&user.id='+currentUserID+'&memberId='+userId+"&accountId="+accountID;
		var myAjax = new Ajax.Updater('userSwitch', 'UserGroupSaveAjax.action',
				{
					method: 'post',
					parameters: pars,
					onComplete: function() { loadAutocompleter(); $('userSwitchAdd').focus(); }	
				} 
		);
	}
</s:if>

</script>
<style type="text/css">
	div.autocomplete {
  position:absolute;
  width:250px;
  background-color:white;
  border:1px solid #888;
  margin:0;
  padding:0;
}
div.autocomplete ul {
  list-style-type:none;
  margin:0;
  padding:0;
}
div.autocomplete ul li.selected { background-color: #ffb;}
div.autocomplete ul li {
  list-style-type:none;
  display:block;
  margin:0;
  padding:2px;
  cursor:pointer;
}
</style>
</head>
<body onload="loadAutocompleter()">
<h1>Manage User Accounts</h1>

<table border="0">
<tr>
	<td colspan="2" align="center">
		<div id="search">
		<div id="showSearch" onclick="showSearch()" <s:if test="filtered">style="display: none"</s:if> ><a href="#">Show Filter Options</a></div>
		<div id="hideSearch" <s:if test="!filtered">style="display: none"</s:if> ><a href="#" onclick="hideSearch()">Hide Filter Options</a></div>
		<s:form id="form1" method="post" cssStyle="%{filtered ? '' : 'display: none'}">
		<div style="text-align: center; width: 100%;">
			<div>
				<button class="picsbutton positive" type="submit" name="button" value="Search">Search</button>
			</div>
		</div><br/>		
		<pics:permission perm="AllOperators">
			<label>Operator:</label> <span id="operators">
			<s:select 
		       name="accountId"
		       cssClass="blueSmall"
			   headerKey="1100"
			   headerValue="PICS Employees"
		       list="facilities"
		       listKey="id"
		       listValue="name"
		       />
			</span><br />
		</pics:permission>
			<label>Type:</label>
				<s:select name="isGroup" cssClass="blueSmall"
				       headerKey="" headerValue="All"
				       list="#{'Yes':'Groups', 'No':'Users'}"
				       value="isGroup"
				/>
			<label>Status:</label>
				<s:select name="isActive" cssClass="blueSmall"
				       headerKey="" headerValue="All"
				       list="#{'Yes':'Active', 'No':'Inactive'}"
				       value="isActive"
				/>
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
		<a href="?button=newUser&accountId=<s:property value="accountId"/>&isActive=<s:property value="isActive"/>&isGroup=<s:property value="isGroup"/>&user.isGroup=Yes&user.isActive=Yes">Add Group</a>
		&nbsp;&nbsp;
		<a href="?button=newUser&accountId=<s:property value="accountId"/>&isActive=<s:property value="isActive"/>&isGroup=<s:property value="isGroup"/>&user.isGroup=No&user.isActive=Yes">Add User</a>
		&nbsp;&nbsp;
		<a href="ReportUserPermissionMatrix.action?accountID=<s:property value="accountId"/>">Permissions Matrix</a>
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
<td id="editUser" class="blueMain" style="padding-left: 20px; vertical-align: top;">

<s:include value="../actionMessages.jsp" />
<s:if test="user != null">
	
	<s:form action="UserSave" id="UserSave">
		<s:hidden name="user.id" />
		<s:hidden name="accountId" />
		<s:hidden name="isGroup" />
		<s:hidden name="isActive" />
		<s:hidden name="user.isGroup" />
		<s:if test="user.group">
			<s:hidden name="user.isActive" />
		</s:if>

			<div>
				<pics:permission perm="EditUsers" type="Edit">
					<button id="SaveButton" class="picsbutton positive" type="submit" name="button" value="Save">Save</button>
				</pics:permission>
				<pics:permission perm="EditUsers" type="Delete">
					<s:if test="user.id > 0">
						<button type="submit" name="button" class="picsbutton negative" value="Delete" onclick="return confirm('Are you sure you want to delete this user/group?');">Delete</button>
					</s:if>
				</pics:permission>
			</div>
			<fieldset class="form">
			<legend><span>User Details</span></legend>		
			<ol>
				<li><label>
					<s:if test="user.group">Group</s:if>
					<s:else>User</s:else> #:</label>
					<s:property value="user.id"/>
				</li>
				<li><label>Date Created:</label>
					<s:date name="user.creationDate" format="MM/d/yyyy" />
				</li>
				<li><label>Display Name:</label>
					<s:textfield name="user.name" size="30"/>
				</li>
			</ol>
			<ol>
			<s:if test="!user.group">	
				<li><label>Email:</label>
					<s:textfield name="user.email" size="40"/>
				</li>
				<li>
				</li>
				<li><label>Username:</label>
					<s:textfield name="user.username" size="30" onblur="checkUsername(this.value);"/>
					<span id="username_status"></span>
				</li>
				<li><label>Password:</label>
					<s:password name="password1" value=""/>
				</li>
				<li><label>Confirm Password:</label>
					<s:password name="password2" value=""/>
				</li>
				<li><label for="user.phone">Phone:</label>
					<s:textfield name="user.phone" size="15"/>(optional)</li>
				<li><label for="user.fax">Fax:</label>
					<s:textfield name="user.fax" size="15"/>(optional)</li>
				<s:if test="user != null">
					<li><label>Last Login:</label>
						<s:date name="user.lastLogin" />
					</li>
				</s:if>
			</s:if>
				<li><label>Active</label>
					<s:radio theme="pics" list="#{'Yes':'Yes','No':'No'}" name="user.isActive"></s:radio>
				</li>
			</ol>
			</fieldset>
			<s:if test="user.id > 0 && !user.group && user.account.id != 1100">
			<fieldset class="form bottom">
			<div>
				<pics:permission perm="SwitchUser">
					<a href="Login.action?button=login&switchToUser=<s:property value="user.id"/>">Switch to this User</a> | 
				</pics:permission>
				<a href="UserSave.action?button=sendWelcomeEmail&accountId=<s:property value="accountId"/>&user.accountID=<s:property value="accountId"/>&user.id=<s:property value="user.id"/>&isActive=<s:property value="isActive"/>&isGroup=<s:property value="isGroup"/>">Send Welcome Email</a>
			</div>
			</fieldset>
			</s:if>
	</s:form>
<br clear="all">	
	<s:if test="user.id > 0">
		<s:if test="!user.superUser">
			<div id="permissionReport" style="width: 100%">
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
		</s:if>
		<s:if test="permissions.admin">
			<s:if test="user.group">
				<div id="userSwitch">
					<s:include value="user_save_userswitch.jsp" />
				</div>
			</s:if>
		</s:if>
	</s:if>
</s:if>

</td>
</tr>
</table>
</body>
</html>
