<%@ page language="java"%>
<%@page import="com.picsauditing.access.*"%>
<%@page import="java.util.*"%>
<%@page import="com.picsauditing.PICS.Utilities"%>
<jsp:useBean id="uBean" class="com.picsauditing.access.User" scope ="page"/>
<jsp:useBean id="currentUser" class="com.picsauditing.access.User" scope ="page"/>
<jsp:useBean id="permissions" class="com.picsauditing.access.Permissions" scope="session" />
<%
if (!permissions.isLoggedIn()) {
	%><a href="login.jsp" style="color: red; font-weight: bold;">You must log in again</a><%
	return;
}
permissions.tryPermission(OpPerms.EditUsers, OpType.View);

String userID;
String action = request.getParameter("action");
if (action == null) action = "";

if (action.equals("deleteUser")) {
	// Delete the user
	permissions.tryPermission(OpPerms.EditUsers, OpType.Delete);
	uBean.setFromDB(request.getParameter("userID"));
	String name = uBean.userDO.name;
	uBean.deleteUser();
	%>Successfully deleted <%=name %><br />
	<a href="users_manage.jsp">Refresh User List</a><%
	return;
}

if (!action.equals("")) {
	permissions.tryPermission(OpPerms.EditUsers, OpType.Edit);
}

userID = request.getParameter("userID");
String msg = "";
if (action.equals("saveUser")) {
	// Insert or Update the User
	// Delete user occurs on users_manage.jsp
	uBean.setFromRequest(request);
	if (uBean.isOK()){
		uBean.writeToDB();
		userID = uBean.userDO.id;
		//AUDITORS.resetAuditorsAL();
	} else {
		msg = uBean.getErrorMessages();
	}
}
uBean.setFromDB(userID);

if (action.equals("removeGroup")) {
	uBean.removeFromGroup(request.getParameter("groupID"));
}
if (action.equals("addGroup")) {
	uBean.addToGroup(request.getParameter("groupID"), permissions);
}
if (action.equals("savePermissions")) {
	PermissionDB permDB = new PermissionDB();
	permDB.setUserID(request.getParameter("userID"));
	permDB.save(request, permissions);
}
if (action.equals("deletePermission")) {
	PermissionDB permDB = new PermissionDB();
	permDB.setUserID(request.getParameter("userID"));
	permDB.delete(request.getParameter("accessType"));
}
if (action.equals("removeUserFromGroup")) {
	User tempUser = new User();
	tempUser.setFromDB(request.getParameter("childID"));
	tempUser.removeFromGroup(request.getParameter("userID"));
}

///////////////////////////////////////////////////
// Display User and their groups and permissions //
boolean isGroup = false;

Set<User> myGroups = new HashSet<User>();
Set<User> myMembers = new HashSet<User>();
Set<Permission> myPerms = new HashSet<Permission>();
if (uBean.isSet()) {
	myGroups = uBean.getGroups();
	myPerms = uBean.getOwnedPermissions();
	myMembers = uBean.getMembers();
	isGroup = uBean.userDO.isGroup.equals("Yes");
} else {
	// This is a new User or Group
	if (request.getParameter("isGroup") != null)
		isGroup = request.getParameter("isGroup").equals("true") ? true : false;
}
%>
<p style="font-style: italic">Note: Users must relogin for changes to take effect</p>
<br /><br />
<form id="user" method="POST" action="user_edit.jsp">
	<input type="hidden" name="action" value="saveUser" />
	<input type="hidden" name="id" value="<%=uBean.userDO.id%>" />
	<input type="hidden" name="accountID" value="<%=permissions.getAccountIdString() %>" />
	<input type="hidden" name="isGroup" value="<%=(isGroup?"Yes":"No")%>" />
<table border="0" cellspacing="0" cellpadding="1">
	<tr>
		<td colspan="2" class="redMain"><%=msg%></td>
	</tr>
	<tr>
	  <td>&nbsp;</td>
	  <td><input type="button" class="forms" value="Save"
	  	onclick="saveUser();" 
	  	style="font-size: 14px; font-weight: bold;"></td>
	</tr>
	<tr>
		<td class="blueMain" align="right"><%= (isGroup?"Group":"User") %> name</td>
		<td> <input name="name" type="text" class="forms" size="20" value="<%=uBean.userDO.name%>"></td>
	</tr>
	<% if (!isGroup) { %>
	<tr> 
		<td class="blueMain" align="right">Email</td>
		<td><input name="email" type="text" class="forms" size="30" value="<%=uBean.userDO.email%>"></td>
	</tr>
	<tr> 
		<td>&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
	<tr> 
		<td class="blueMain" align="right">Username</td>
		<td><input name="username" type="text" class="forms" size="15" value="<%=uBean.userDO.username%>"></td>
	</tr>
	<tr> 
		<td class="blueMain" align="right">Password</td>
		<td><input name="newPassword" type="password" class="forms" size="15" value="<%=uBean.userDO.password%>"></td>
	</tr>
	<% } %>
	<tr>
		<td class="blueMain" align="right">Active?</td>
		<td class="blueMain" align="left"><%=Utilities.getYesNoRadio("isActive",uBean.userDO.isActive)%></td>
	</tr>
</table>
</form>

<%
if (!uBean.isSet()) return;
// Don't show below data until, we've saved our user

currentUser.setFromDB(permissions.getUserIdString());

////////////////////////////
// Begin Permissions
boolean canGrant = false;
ArrayList<String> temp = new ArrayList<String>();
for(Permission perm: permissions.getPermissions()) {
	if (perm.isGrantFlag() && !myPerms.contains(perm)) {
		canGrant = true;
		temp.add(perm.getAccessType().name());
		temp.add(perm.getAccessType().getDescription());
	}
}

if (canGrant || myPerms.size() > 0) {
%>
<form id="permissions" method="POST" action="user_edit.jsp">
	<input type="hidden" name="action" value="savePermissions" />
<table>
	<tr bgcolor="#003366" class="whiteTitle">
		<td>Permission</td>
		<td>Read</td>
		<td>Edit</td>
		<td>Delete</td>
		<td>Grant</td>
		<td class="blueSmall"><input type="button" value="Save" class="blueSmall" onclick="savePermissions()" /></td>
	</tr>
<%
for (Permission perm: myPerms) {
	if (permissions.hasPermission(perm.getAccessType(), OpType.Grant)) {
		String accessType = perm.getAccessType().toString();
		%>
		<tr class="active">
			<td><%=perm.getAccessType().getDescription()%><input type="hidden" name="accessType" value="<%=accessType %>" /></td>
			<td><%=Utilities.getCheckBoxInput(accessType+"_viewFlag", "blueSmall", perm.isViewFlag())%></td>
			<td><%=Utilities.getCheckBoxInput(accessType+"_editFlag", "blueSmall", perm.isEditFlag())%></td>
			<td><%=Utilities.getCheckBoxInput(accessType+"_deleteFlag", "blueSmall", perm.isDeleteFlag())%></td>
			<td><%=Utilities.getCheckBoxInput(accessType+"_grantFlag", "blueSmall", perm.isGrantFlag())%></td>
			<td class=""><input type="button" value="Remove" class="blueSmall" onclick="deletePermission('<%=accessType%>')" /></td>
		</tr>
		<%
	} else {
		%>
		<tr class="active">
			<td><%=perm.getAccessType().getDescription()%></td>
			<td><%=(perm.isViewFlag()?"Y":"N")%></td>
			<td><%=(perm.isEditFlag()?"Y":"N")%></td>
			<td><%=(perm.isDeleteFlag()?"Y":"N")%></td>
			<td>N</td>
			<td>&nbsp;</td>
		</tr>
		<%
	}
}

if (canGrant) {
	String[] grantPerms = temp.toArray(new String[0]);
	%>
	<tr class="active">
		<td>
			<%=Utilities.inputSelect2First("new", "forms", "", grantPerms, "", "- Grant/Revoke Permission -")%>
		</td>
		<td><%=Utilities.getCheckBoxInput("new_viewFlag", "blueSmall", true)%></td>
		<td><%=Utilities.getCheckBoxInput("new_editFlag", "blueSmall", false)%></td>
		<td><%=Utilities.getCheckBoxInput("new_deleteFlag", "blueSmall", false)%></td>
		<td><%=Utilities.getCheckBoxInput("new_grantFlag", "blueSmall", false)%></td>
		<td></td>
	</tr>
	<%
}
%>
</table>
</form>

<%
} // End Permissions
////////////////////////////

%>
<p class="blueMain">Member of Group(s):
<form id="addGroup">
<ul>
<%
// = permissions.
Set<User> allGroups = currentUser.getAccountGroups();
//allGroups.addAll(currentUser.getGroups());

for (User group: myGroups) {
	allGroups.remove(group);
%>
<li class="blueMain">
	<a href="#" onclick="showUser(<%=group.userDO.id%>); return false;"><%=group.userDO.name%></a>
	(<a href="#" onclick="saveGroup('removeGroup', <%=group.userDO.id%>); return false;">remove</a>)
</li>
<%
}
for (User group: allGroups) {
%>
<li class="blueMain">
	<a href="#" onclick="saveGroup('addGroup',<%=group.userDO.id%>); return false;" style="font-style: italic; color: red;">Add to <%=group.userDO.name%></a>
</li>
<%
}
%>
</ul>
</form>
</p>

<%

if (isGroup) {
%>
<p class="blueMain">Contains Member(s):
<ul>
<%
for (User child: myMembers) {
%>
<li class="blueMain">
	<a href="#" 
		onclick="showUser(<%=child.userDO.id%>); return false;"
		<% if (child.userDO.isActive.startsWith("N")) { %> style="font-style: italic; color: #999999"<% } %>
		><%=child.userDO.name%></a>
	(<a href="#" class="blueSmall" onclick="saveGroup('removeUserFromGroup',null,<%=child.userDO.id%>); return false;">remove</a>)
</li>
<%
}
%>
</ul>
</p>
<%
}
if (permissions.hasPermission(OpPerms.EditUsers, OpType.Delete)) {
	%>
	<br /><br /><br /><br />
	<form>
		<input class="blueSmall" type="button" value="Delete User" onclick="deleteUser()" />
	</form>
	<%
}
%>