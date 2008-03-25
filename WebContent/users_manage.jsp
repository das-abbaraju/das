<%@ taglib prefix="s" uri="/struts-tags" %>
<%@page language="java" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<html>
<head>
<title>Manage Users</title>
<script src="js/prototype.js" type="text/javascript"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects" type="text/javascript"></script>
<script src="js/Search.js" type="text/javascript"></script>
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
<table border="0">
<tr>
	<td colspan="2" align="center" class="blueSmall">
		<s:form name="form1" id="form1" action="UsersManage" method="post">
			<s:if test="hasAllOperators">
			Filter by User: <input type="text" name="filter" id="filter" class="blueSmall" onchange="filterOperators();" /><br />
			Operator:<span id="operators"><s:action name="FacilitiesGetAjax" executeResult="true" >
				<s:param name="shouldIncludePICS" value="%{true}"/>
			</s:action></span><br />
			</s:if>
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
			<input type="submit" value="Show" class="blueSmall"/>
			
			<input type="hidden" name="showPage" value="1"/>
			<input type="hidden" name="startsWith" value=""/>
			<input type="hidden" name="orderBy"  value="name"/>
		</s:form>
	</td>
</tr>
<tr>
	<td colspan="2" align="center" class="blueSmall">
		<a href="#" onclick="addUser(true); return false;">Add Group</a>
		<a href="#" onclick="addUser(false); return false;">Add User</a>
	</td>
</tr>
<tr valign="top"><td>
	<table border="0" cellpadding="1" cellspacing="1">
		<tr>
			<td colspan="3"><s:property value="search.pageLinksWithDynamicForm" escape="false"/></td>
		</tr>
		<tr bgcolor="#003366" class="whiteTitle">
			<td>&nbsp;</td>
			<td colspan="2">User/Group</td>
			<td>Last Login</td>
		</tr>
	
	<s:iterator value="searchData" status="stat">
		<tr bgcolor="#FFFFFF" 
			class="active" 
			style="cursor: pointer; <s:if test="'No'.equals(isActive)">font-style: italic; color: #999999</s:if>"
			onclick="showUser('<s:property value="[0].get('id')"/>')">
			<td align="right"><s:property value="#stat.index + search.sql.startRow + 1" />.</td>
			
			<s:if test="'Yes'.equals([0].get('isGroup'))">
				<td>G</td>
				<td style="font-weight: bold "><s:property value="[0].get('name')"/></td>
				<td>N/A</td>
			</s:if>
			<s:else>
				<td>U</td>
				<td><s:property value="[0].get('name')"/></td>
				<td><s:if test="[0].get('lastLogin') != null"><s:date name="[0].get('lastLogin')" format="MM/dd/yy"/></s:if>
						<s:else>never</s:else></td>
			</s:else>
		</tr>
	</s:iterator>
	
	</table>
</td>
<td id="editUser" width="500" class="blueMain">
	<div id="ajaxstatus" style="height: 30px;"></div>
</td>
</tr>
</table>
</body>
</html>
