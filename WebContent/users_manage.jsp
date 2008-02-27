<%@page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@page import="org.apache.commons.beanutils.*"%>
<%@page import="java.util.*"%>
<%@include file="includes/main.jsp" %>
<%@page import="com.picsauditing.search.SelectUser"%>
<%@page import="com.picsauditing.search.Report"%>
<%
permissions.tryPermission(OpPerms.EditUsers, OpType.View);

String getParams = ""; // Used when we go to Page 2,3,etc

String accountID = permissions.getAccountIdString();
if (permissions.hasPermission(OpPerms.EditAllUsers) && request.getParameter("accountID") != null) {
	accountID = Utilities.intToDB(request.getParameter("accountID"));
	getParams += "&accountID="+accountID;
}

List<BasicDynaBean> facilityMap;
if ("filterOperators".equals(request.getParameter("action"))) {
	String filter = request.getParameter("filter");
	String where = "";
	if (filter != null && filter.length() > 3) {
		where = "a.id IN (SELECT accountID FROM users WHERE name LIKE '%"+Utilities.escapeQuotes(filter)+"%' OR username LIKE '%"+Utilities.escapeQuotes(filter)+"%' OR email LIKE '%"+Utilities.escapeQuotes(filter)+"%')";
	}
	facilityMap = FACILITIES.listAll(where);
	%>
<option value="1100">PICS Employees</option>
		<% for(BasicDynaBean row : facilityMap) { %>
		<option value="<%=row.get("id")%>"<%=(row.get("id").equals(accountID))?" SELECTED":""%>><%=row.get("name")%></option>
		<% } %>
	<%
	return;
}
facilityMap = FACILITIES.listAll();

SelectUser sql = new SelectUser();
sql.addField("u.lastLogin");
sql.addField("u.isGroup");

Report search = new Report();
search.setSql(sql);

String isGroup = request.getParameter("isGroup");
String isActive = request.getParameter("isActive");
if (isActive == null) isActive = "Yes";
if ("Yes".equals(isGroup) || "No".equals(isGroup)) {
	sql.addWhere("isGroup = '"+isGroup+"' ");
	getParams += "&isGroup="+isGroup;
}
if ("Yes".equals(isActive) || "No".equals(isActive)) {
	sql.addWhere("isActive = '"+isActive+"' ");
	getParams += "&isActive="+isActive;
}

sql.addWhere("accountID = "+accountID);
// Only search for Auditors and Admins
sql.addOrderBy("u.name");
search.setPageByResult(request);
search.setLimit(20);

List<BasicDynaBean> searchData = search.getPage();

pageBean.setTitle("Manage Users");
pageBean.includeScriptaculous(true);
%>
<%@ include file="includes/header.jsp" %>
<script type="text/javascript">
var currentUser = 0;
var accountID = <%=accountID%>;

var permTypes = new Array();
<%
for(Permission perm: permissions.getPermissions()) {
	if (perm.isGrantFlag()) {
		OpPerms p = perm.getAccessType();
		%>permTypes['<%=p.toString()%>'] = new Array("<%=p.getHelpText() %>",
			<%=p.usesView() %>, <%=p.usesEdit() %>, <%=p.usesDelete() %>);
		<%
	}
}
%>
function showPermDesc(item) {
	var x = $F(item);
	$('permDescription').innerHTML = permTypes[x][0];
	$('new_viewFlag').disabled = !permTypes[x][1];
	$('new_editFlag').disabled = !permTypes[x][2];
	$('new_deleteFlag').disabled = !permTypes[x][3];
}

function filterOperators() {
	pars = '&action=filterOperators&filter='+$('filter').getValue();
	var myAjax = new Ajax.Updater('operators', 'users_manage.jsp', {method: 'get', parameters: pars});
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
</script>
<table border="0">
<tr>
	<td colspan="2" align="center" class="blueSmall" height="30">
		<form action="users_manage.jsp" method="get">
			<% if (permissions.hasPermission(OpPerms.EditAllUsers)) { %>
			Filter by User: <input type="text" name="filter" id="filter" class="blueSmall" onchange="filterOperators();" /><br />
			Operator:	
			<select id="operators" name="accountID" class="blueSmall">
				<option value="1100">PICS Employees</option>
				<% for(BasicDynaBean row : facilityMap) { %>
				<option value="<%=row.get("id")%>"<%=(row.get("id").equals(accountID))?" SELECTED":""%>><%=row.get("name")%></option>
				<% } %>
			</select> <br />
			<% } %>
			Type:
			<select name="isGroup" class="blueSmall">
				<option value="">All</option>
				<option value="Yes"<%=("Yes".equals(isGroup))?" SELECTED":""%>>Groups</option>
				<option value="No"<%=("No".equals(isGroup))?" SELECTED":""%>>Users</option>
			</select>
			Status:
			<select name="isActive" class="blueSmall">
				<option value="">All</option>
				<option value="Yes"<%=("Yes".equals(isActive))?" SELECTED":""%>>Active</option>
				<option value="No"<%=("No".equals(isActive))?" SELECTED":""%>>Inactive</option>
			</select>
			<input type="submit" value="Show" class="blueSmall"/>
		</form>
	</td>
</tr>
<tr>
	<td colspan="2" align="center" class="blueSmall" height="30">
		<a href="#" onclick="addUser(true); return false;">Add Group</a>
		<a href="#" onclick="addUser(false); return false;">Add User</a>
	</td>
</tr>
<tr valign="top"><td>
	<table border="0" cellpadding="1" cellspacing="1">
		<tr>
			<td colspan="3"><%=search.getPageLinks(getParams)%></td>
		</tr>
		<tr bgcolor="#003366" class="whiteTitle">
			<td>&nbsp;</td>
			<td colspan="2">User/Group</td>
			<td>Last Login</td>
		</tr>
	<%
	int counter = search.getSql().getStartRow()+1;
	for(BasicDynaBean row: searchData) {
		String lastLogin = DateBean.toShowFormat(row.get("lastLogin"));
		boolean rowGroup = "Yes".equals(row.get("isGroup"));
		if (rowGroup) lastLogin = "N/A";
		else if(lastLogin.equals("")) lastLogin = "never";
	%>
		<tr bgcolor="#FFFFFF" 
			class="active" 
			style="cursor: pointer; <%="No".equals(row.get("isActive"))?" font-style: italic; color: #999999":""%>"
			onclick="showUser(<%=row.get("id")%>)">
			<td align="right"><%=counter++%>.</td>
			<td><%=rowGroup?"G":"U"%></td>
			<td<%=rowGroup?" style=\"font-weight: bold \"":""%>><%=row.get("name")%></td>
			<td><%=lastLogin%></td>
		</tr>
	<%
	}
	%>
	</table>
</td>
<td id="editUser" width="500" class="blueMain">
	<div id="ajaxstatus" style="height: 30px;"></div>
</td>
</tr>
</table>
<%@ include file="includes/footer.jsp" %>
