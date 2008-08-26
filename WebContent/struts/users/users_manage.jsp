<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Manage Users</title>
<script type="text/javascript" src="js/prototype.js"></script>
<script type="text/javascript" src="js/scriptaculous/scriptaculous.js?load=effects"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css"/>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<script type="text/javascript">
var currentUser = 0;
var accountID = <s:property value="accountId"/>;

var permTypes = new Array();
<s:iterator value="%{permissions.getPermissions()}">
	<s:if test="grantFlag == true">permTypes['<s:property value="accessType.toString()"/>'] = new Array("<s:property value="accessType.helpText"/>",<s:property value="accessType.usesView()"/>,<s:property value="accessType.usesEdit()"/>,<s:property value="accessType.usesDelete()"/>);</s:if>
</s:iterator>

function showPermDesc(item) {
	var x = $F(item);
	$('permDescription').innerHTML = permTypes[x][0];
	$('new_viewFlag').disabled = !permTypes[x][1];
	$('new_editFlag').disabled = !permTypes[x][2];
	$('new_deleteFlag').disabled = !permTypes[x][3];
}

function filterOperators() {
	pars = '&action=filterOperators&filter='+$('filter').getValue()+'&shouldIncludePICS=true';
	var myAjax = new Ajax.Updater('operators', 'FacilitiesGetAjax.action', {method: 'get', parameters: pars});
}
function getPage(pars) {
	pars = 'userID='+currentUser+'&accountID='+accountID+pars;
	$('ajaxstatus').innerHTML = 'Processing: <img src="images/ajax_process.gif" />';
	var myAjax = new Ajax.Updater('editUser', 'user_edit.jsp', {method: 'post', parameters: pars});
}

function showUser(userID) {
	currentUser = userID;
	getPage('');
}

function addUser(isGroup) {
	currentUser = 0;
	pars = '&isGroup='+isGroup;
	getPage(pars);
}

function saveUser() {
	var pars = '&' + $('user').serialize();
	getPage(pars);
}
function deleteUser() {
	var pars = '&action=deleteUser';
	getPage(pars);
}

function savePermissions() {
	var pars = '&' + $('permissions').serialize();
	getPage(pars);
}
function deletePermission(accessType) {
	var pars = '&action=deletePermission&accessType=' + accessType;
	getPage(pars);
}
function sendWelcomeEmail() {
	var pars = '&action=sendWelcomeEmail';
	getPage(pars);
}

function saveGroup(action, groupID, childID) {
	pars = '&action='+action+'&groupID='+groupID;
	if (action == "removeUserFromGroup") pars = pars + '&childID=' + childID;
	getPage(pars);
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
			Filter by User: <input type="text" name="filter" id="filter" class="blueSmall" onchange="filterOperators();" /><br />
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
			<s:submit name="imageField" type="image" src="images/button_search.gif" onclick="runSearch( 'form1')" />
			<s:hidden name="showPage" value="1"/>
			<s:hidden name="startsWith" value=""/>
			<s:hidden name="orderBy"  value="name"/>
		</s:form>
		</div>
	</td>
</tr>
<tr><td>
<div>
<s:property value="search.pageLinksWithDynamicForm" escape="false"/>
</div>
</td></tr>
<tr>
	<td colspan="3" align="center" class="blueMain">
		<a href="#" onclick="addUser(true); return false;">Add Group</a>
		&nbsp;&nbsp;<a href="#" onclick="addUser(false); return false;">Add User</a>
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
	<s:iterator value="searchData" status="stat">
		<tr style="cursor: pointer; <s:if test="'No'.equals(isActive)">font-style: italic; color: #999999</s:if>"
			onclick="showUser('<s:property value="[0].get('id')"/>')">
			<td align="right"><s:property value="#stat.index + search.sql.startRow + 1" />.</td>
			
			<s:if test="'Yes'.equals([0].get('isGroup'))">
				<td>G</td>
				<td style="font-weight: bold "><s:property value="[0].get('name')"/></td>
				<td>N/A</td>
			</s:if>
			<s:else>
				<td>U</td>
				<s:if test="%{[0].get('isActive') == 'Yes'}">
				<td><s:property value="[0].get('name')"/>
				</td>
				</s:if>
				<s:else>
				<td class="inactive"><s:property value="[0].get('name')"/>*
				</td>
				</s:else>
				<td><s:if test="[0].get('lastLogin') != null"><s:date name="[0].get('lastLogin')" format="MM/dd/yy"/></s:if>
						<s:else>never</s:else></td>
			</s:else>
		</tr>
	</s:iterator>
	</table>
</td>
<td id="editUser" class="blueMain" style="margin: 30px; padding: 30px; vertical-align: top;">

<s:include value="../actionMessages.jsp" />

<div id="ajaxstatus" style="height: 20px;"></div>
<s:form>
	<s:hidden name="user.id" />
	<s:hidden name="user.accountID" />
	<s:hidden name="user.isGroup" />
<pics:permission perm="EditUsers" type="Edit">
	<div class="buttons">
		<button class="positive" type="submit" name="button" value="Save">Save</button>
	</div>
</pics:permission>
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
</s:if>
	<tr>
		<th>Display name</th>
		<td><s:textfield name="user.name" size="30"/></td>
	</tr>
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
</s:if>
	<tr>
		<th>Active</th>
		<td><s:radio theme="pics" list="#{'Yes':'Yes','No':'No'}" name="user.isActive"></s:radio> </td>
	</tr>

</table>

<s:if test="user.id > 0">
	<pics:permission perm="SwitchUser">
		<div><a href="login.jsp?switchUser=<s:property value="user.username"/>">Switch to this User</a></div>
	</pics:permission>
	<s:if test="!user.superUser">
	</s:if>

	<table class="report">
	<thead>
		<tr>
			<th>Permission</th>
			<th>Read</th>
			<th>Edit</th>
			<th>Delete</th>
			<th>Grant</th>
			<th></th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="user.ownedPermissions">
		<pics:permission perm="%{opPerm}"></pics:permission>
			<tr>
				<td><s:property value="opPerm.description"/></td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
			</tr>
		</s:iterator>
	</tbody>
	<tfoot>
			<tr>
				<td>f</td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
			</tr>
		</tfoot>
	</table>

	<table class="report">
	<thead>
		<tr>
			<th colspan="2">Member of Group(s):</th>
		</tr>
	</thead>
	<tbody>
	<s:iterator value="user.groups">
		<tr>
			<td><a href="#" onclick="showUser(<s:property value="group.id"/>); return false;"><s:property value="group.name"/></a></td>
			<td>&nbsp; <a href="#" onclick="saveGroup('removeGroup', <s:property value="group.id"/>); return false;">remove</a></td>
		</tr>
	</s:iterator>
	<s:iterator value="allGroups">
		<tr>
			<td><a href="#" style="font-style: italic; color: red;" 
				onclick="showUser(<s:property value="id"/>); return false;"><s:property value="name"/></a></td>
			<td>&nbsp; <a href="#" style="font-style: italic; color: red;" 
				onclick="saveGroup('addGroup', <s:property value="id"/>); return false;">add</a></td>
		</tr>
	</s:iterator>
	</tbody>
	</table>


</s:if>




</s:form>

</td>
</tr>
</table>
</body>
</html>
