<%@ page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@page import="org.apache.commons.beanutils.*"%>
<%@page import="java.util.*"%>
<%@page import="com.picsauditing.access.*"%>
<jsp:useBean id="pBean" class="com.picsauditing.PICS.PermissionsBean" scope="session" />
<jsp:useBean id="permissions" class="com.picsauditing.access.Permissions" scope="session" />
<jsp:useBean id="pageBean" class="com.picsauditing.PICS.WebPage" scope ="page"/>
<%
if (!permissions.loginRequired(response, request)) return;
permissions.tryPermission(OpPerms.EditUsers, OpType.View);

SearchUsers search = new SearchUsers();
//search.sql.addField("u.username");
search.sql.addField("u.isGroup");
String isGroup = request.getParameter("isGroup");
if (isGroup != null && isGroup.equals("Yes")) {
	search.sql.addWhere("isGroup = 'Yes' ");
	search.sql.addWhere("accountID = "+permissions.getAccountId());
} else {
	// Default: users only
	search.sql.addWhere("isGroup = 'No' ");
	// Only search for Admin and Auditor users
	search.sql.addWhere("accountID = "+permissions.getAccountId());
	//search.inGroups("10,11");
}
// Only search for Auditors and Admins
search.sql.addOrderBy("u.name");
search.setPageByResult(request);
search.setLimit(20);

List<BasicDynaBean> searchData = search.doSearch();

pageBean.setTitle("Manage Users");
pageBean.includeScriptaculous(true);
%>
<%@ include file="includes/header.jsp" %>
<script type="text/javascript">
var currentUser = 0;

function getPage(pars) {
	pars = 'userID='+currentUser+pars;
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

function saveGroup(action, groupID, childID) {
	pars = '&action='+action+'&groupID='+groupID;
	if (action == "removeUserFromGroup") pars = pars + '&childID=' + childID;
	getPage(pars);
}
</script>
<table border="0">
<tr valign="top"><td width="200">
	<table border="0" cellpadding="1" cellspacing="1">
		<tr>
			<td colspan="2" align="center" class="blueSmall" height="30">
				<a href="?isGroup=Yes">Show Groups</a>
				<a href="?isGroup=No">Show Users</a>
			</td>
		</tr>
		<tr>
			<td colspan="2" align="center" class="blueSmall" height="30">
				<a href="#" onclick="addUser(true); return false;">Add Group</a>
				<a href="#" onclick="addUser(false); return false;">Add User</a>
			</td>
		</tr>
		<tr>
			<td colspan="2"><%=search.getPageLinks()%></td>
		</tr>
		<tr bgcolor="#003366" class="whiteTitle">
			<td>&nbsp;</td>
			<td>User/Group</td>
		</tr>
	<%
	int counter = search.getStartRow();
	for(BasicDynaBean row: searchData) {
	%>
		<tr bgcolor="#FFFFFF" 
			class="active" 
			style="cursor: pointer" 
			onclick="showUser(<%=row.get("id")%>); return false;">
			<td align="right"><%=counter++%>.</td>
			<td<% if (row.get("isActive").toString().startsWith("N")) { %> style="font-style: italic; color: #999999"<% } %>><%=row.get("name")%></td>
		</tr>
	<%
	}
	%>
	</table>
</td>
<td id="editUser" width="400" class="blueMain">
	<div id="ajaxstatus" style="height: 30px;"></div>
</td>
</tr>
</table>
<%@ include file="includes/footer.jsp" %>
