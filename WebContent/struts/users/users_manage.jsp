<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Manage Users</title>
<script type="text/javascript" src="js/prototype.js"></script>
<script type="text/javascript" src="js/scriptaculous/scriptaculous.js?load=effects"></script>
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
	$('username_status').innerHTML = 'checking availability of username...';
	pars = 'userID='+userID+'&username='+username;
	var myAjax = new Ajax.Updater('username_status', 'user_ajax.jsp', {method: 'get', parameters: pars});
}

</script>
</head>
<body>
<h1>Manage User Accounts</h1>

<table border="0">
<tr>
	<td colspan="2" align="center">
		<div id="search">
		<div id="showSearch"><a href="#" onclick="showSearch()">Show Filter Options</a></div>
		<div id="hideSearch" style="display: none"><a href="#" onclick="hideSearch()">Hide Filter Options</a></div>
		<s:form id="form1" method="post" cssStyle="display: none">
		
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
	<td colspan="2" align="center" class="blueMain">
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
	<div id="ajaxstatus"></div>
	Select a user or group from the left to view
</td>
</tr>
</table>
</body>
</html>
