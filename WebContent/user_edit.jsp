<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@page import="com.picsauditing.access.*"%>
<%@page import="java.util.*"%>
<%@page import="com.picsauditing.PICS.Utilities"%>
<%@page import="org.apache.commons.beanutils.BasicDynaBean"%>
<%@ include file="includes/main_ajax.jsp" %>
<%@page import="com.picsauditing.mail.EmailContractorBean"%>
<%@page import="com.picsauditing.mail.EmailTemplates"%>
<%@page import="com.picsauditing.mail.EmailUserBean"%>
<%@page import="com.picsauditing.util.SpringUtils"%>
<jsp:useBean id="uBean" class="com.picsauditing.access.User" scope ="page"/>
<jsp:useBean id="currentUser" class="com.picsauditing.access.User" scope ="page"/>
<jsp:useBean id="AUDITORS" class="com.picsauditing.PICS.Auditors" scope="application"/>
<%
permissions.tryPermission(OpPerms.EditUsers, OpType.View);

String accountID = permissions.getAccountIdString();
if (permissions.hasPermission(OpPerms.AllOperators) && request.getParameter("accountId") != null) {
	accountID = Utilities.intToDB(request.getParameter("accountId"));
}

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
	<a href="UsersManage.action">Refresh User List</a><%
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
		boolean isNew = uBean.userDO.id.equals("");
		boolean isGroup = uBean.userDO.isGroup.startsWith("Y");
		uBean.writeToDB();
		if (isNew) {
			%>Successfully Created New <%=isGroup?"Group":"User"%><br /><br />
			<a href="UsersManage.action?accountId=<%=uBean.userDO.accountID%>&isGroup=<%=uBean.userDO.isGroup%>&isActive=Yes">Click to Refresh List</a><%
			return;
		}
	} else {
		msg = uBean.getErrorMessages();
	}
}
uBean.setFromDB(userID);

if (action.equals("removeGroup")) {
	uBean.removeFromGroup(request.getParameter("groupID"));
	AUDITORS.resetAuditorsAL(request.getParameter("groupID"));
}
if (action.equals("addGroup")) {
	uBean.addToGroup(request.getParameter("groupID"), permissions);
	AUDITORS.resetAuditorsAL(request.getParameter("groupID"));
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
	AUDITORS.resetAuditorsAL(request.getParameter("groupID"));
}
if (action.equals("sendWelcomeEmail")) {
	// Send an email to the contractor
	EmailUserBean mailer = (EmailUserBean)SpringUtils.getBean("EmailUserBean");
	mailer.setPermissions(permissions);
	mailer.sendMessage(EmailTemplates.newuser, new Integer(userID));
}

///////////////////////////////////////////////////
// Display User and their groups and permissions //
boolean isGroup = false;

Set<User> myGroups = new TreeSet<User>();
Set<User> myMembers = new TreeSet<User>();
Set<Permission> myPerms = new TreeSet<Permission>();
List<BasicDynaBean> loginLog = new ArrayList<BasicDynaBean>();

if (uBean.isSet()) {
	myGroups = uBean.getGroups();
	myPerms = uBean.getOwnedPermissions();
	myMembers = uBean.getMembers();
	isGroup = uBean.userDO.isGroup.equals("Yes");
	loginLog = uBean.getLoginLog(10);
} else {
	// This is a new User or Group
	if (request.getParameter("isGroup") != null)
		isGroup = request.getParameter("isGroup").equals("true") ? true : false;
}
%>
<p style="font-style: italic">Note: Users must relogin for changes to take effect</p>
<div id="ajaxstatus" style="height: 20px;"></div>
<form id="user" method="POST" action="user_edit.jsp">
	<input type="hidden" name="action" value="saveUser" />
	<input type="hidden" name="id" value="<%=uBean.userDO.id%>" />
	<input type="hidden" name="accountID" value="<%=accountID%>" />
	<input type="hidden" name="isGroup" value="<%=(isGroup?"Yes":"No")%>" />
<table border="0" cellspacing="0" cellpadding="1">
	<tr>
		<td colspan="2" class="redMain"><%=msg%></td>
	</tr>
	<% if (permissions.hasPermission(OpPerms.EditUsers, OpType.Edit)) { %>
	<tr>
	  <td>&nbsp;</td>
	  <td><input id="UserSave" type="button" class="forms" value="Save"
	  	onclick="saveUser();" 
	  	style="font-size: 14px; font-weight: bold;"></td>
	</tr>
	<% } %>
	<% if (uBean.userDO.dateCreated.length() > 0) { %>
	<tr  class="blueMain">
		<td align="right"><%= (isGroup?"Group":"User") %> #</td>
		<td><%=uBean.userDO.id%></td>
	</tr>
	<tr>
		<td class="blueMain" align="right">Date created</td>
		<td class="blueMain"><%=uBean.userDO.dateCreated%>
			<% if (!isGroup) { %><a href="#" onclick="sendWelcomeEmail(); return false;">Send Welcome Email</a><% } %>
		</td>
	</tr>
	<% } %>
	<tr>
		<td class="blueMain" align="right">Display name</td>
		<td> <input name="name" type="text" class="forms" size="30" value="<%=uBean.userDO.name%>"></td>
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
		<td class="redMain"><input name="username" id="username" type="text" class="forms" size="30" 
			value="<%=uBean.userDO.username%>" onblur="checkUsername(this.value, <%=(uBean.userDO.id.length()>0 ? uBean.userDO.id : 0) %>);">
			<span id="username_status"></span></td>
	</tr>
	<tr> 
		<td class="blueMain" align="right">Password</td>
		<td><input name="newPassword" type="password" class="forms" size="15" value="<%=uBean.userDO.password%>"></td>
	</tr>
		<% if (uBean.userDO.dateCreated.length() > 0) { %>
		<tr> 
			<td class="blueMain" align="right">Last login</td>
			<td class="blueMain"><%=uBean.userDO.lastLogin%></td>
		</tr>
		<% } %>
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

if (!isGroup && permissions.hasPermission(OpPerms.SwitchUser)) {
	%><p><a href="login.jsp?switchUser=<%= uBean.userDO.username %>">Switch to this User</a></p><%
}

currentUser.setFromDB(permissions.getUserIdString());

if (!uBean.isSuGroup()) {
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
			<td><div style="cursor: help" title="<%=perm.getAccessType().getHelpText()%>"><%=perm.getAccessType().getDescription()%></div><input type="hidden" name="accessType" value="<%=accessType %>" /></td>
			<td><%=(perm.getAccessType().usesView()) ? Utilities.getCheckBoxInput(accessType+"_viewFlag", "blueSmall", perm.isViewFlag()) : "N/A"%></td>
			<td><%=(perm.getAccessType().usesEdit()) ? Utilities.getCheckBoxInput(accessType+"_editFlag", "blueSmall", perm.isEditFlag()) : "N/A"%></td>
			<td><%=(perm.getAccessType().usesDelete()) ? Utilities.getCheckBoxInput(accessType+"_deleteFlag", "blueSmall", perm.isDeleteFlag()) : "N/A"%></td>
			<td><%=Utilities.getCheckBoxInput(accessType+"_grantFlag", "blueSmall", perm.isGrantFlag())%></td>
			<td class=""><input type="button" value="Remove" class="blueSmall" onclick="deletePermission('<%=accessType%>')" /></td>
		</tr>
		<%
	} else {
		// This user cannot grant(edit) this permission
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
		String selectOptions = com.picsauditing.PICS.Utilities.inputSelectAll("new", "forms", "", grantPerms,
				"- Grant/Revoke Permission -", "", "showPermDesc(this);", true, false, null, new String[0]);
		%>
		<tr class="active">
			<td><%=selectOptions%></td>
			<td><input type=checkbox class="blueSmall" id="new_viewFlag" name="new_viewFlag" checked></td>
			<td><input type=checkbox class="blueSmall" id="new_editFlag" name="new_editFlag"></td>
			<td><input type=checkbox class="blueSmall" id="new_deleteFlag" name="new_deleteFlag"></td>
			<td><input type=checkbox class="blueSmall" id="new_grantFlag" name="new_grantFlag"></td>
			<td>&nbsp;</td>
		</tr>
		<tr class="active">
			<td id="permDescription" colspan="6" style="width: 450px;">
			</td>
		</tr>
		<%
	}
	%>
	</table>
	</form>
	
	<%
	} // End Permissions
	////////////////////////////

	
	
	Set<User> allGroups = currentUser.getAccountGroups();
	allGroups.remove(uBean); // You can't add yourself to your own group
	if (allGroups.size() > 0) {
		%>
	<form id="addGroup">
		<table>
		<tr bgcolor="#003366" class="whiteTitle">
			<td colspan="2">Member of Group(s):</td>
		</tr>
		<%
		for (User group: myGroups) {
			allGroups.remove(group);
			%>
			<tr class="blueMain">
			<% if(permissions.hasPermission(OpPerms.EditUsers,OpType.Delete) || permissions.hasPermission(OpPerms.EditUsers,OpType.Edit)) { %>
				<td><a href="#" onclick="showUser(<%=group.userDO.id%>); return false;"><%=group.userDO.name%></a></td>
				<td>&nbsp;<a href="#" onclick="saveGroup('removeGroup', <%=group.userDO.id%>); return false;">remove</a></td>
			<% } else { %>
			<td><%=group.userDO.name%></td>
			<% } %>
			</tr>
			<%
		}
		
		for (User group: allGroups) {
			%>
			<tr class="blueMain">
				<% if(permissions.hasPermission(OpPerms.EditUsers,OpType.Edit)) { %>
				<td><a href="#" style="font-style: italic; color: red;" onclick="showUser(<%=group.userDO.id%>); return false;"><%=group.userDO.name%></a></td>
				<td>&nbsp;<a href="#" style="font-style: italic; color: red;" onclick="saveGroup('addGroup',<%=group.userDO.id%>); return false;">add</a></td>
			<% } %>
			</tr>
			<%
		}
		%>
	</table>
	</form>
		<%
	} //if

}

if (isGroup && myMembers.size() > 0) {
	%>
	<table>
	<tr bgcolor="#003366" class="whiteTitle">
		<td colspan="2">Contains Member(s):</td>
	</tr>
	<%
	for (User child: myMembers) {
		%>
		<tr class="blueMain">
			<td><a href="#" 
				onclick="showUser(<%=child.userDO.id%>); return false;"
				<% if (child.userDO.isActive.startsWith("N")) { %> style="font-style: italic; color: #999999"<% } %>
				><%=child.userDO.name%></a></td>
			<td><a href="#" class="blueSmall" onclick="saveGroup('removeUserFromGroup',<%=userID %>,<%=child.userDO.id%>); return false;">remove</a></td>
		</tr>
		<%
	}
	%>
	</table>
	<%
}

if (loginLog != null && loginLog.size() > 0) {
	%>
	<table class="blueMain">
	<tr bgcolor="#003366" class="whiteTitle">
		<td>Login Date/Time</td>
		<td>IP Address</td>
		<td>Notes</td>
	</tr>
	<%
	for(BasicDynaBean row: loginLog) {
		String notes = "";
		if ("N".equals(row.get("successful"))) {
			notes = "wrong password";
		} else if (row.get("name") != null) {
			notes = "admin login by "+row.get("name");
		}
		%>
		<tr class="blueMain">
			<td><%=row.get("date")%></td>
			<td><%=row.get("remoteAddress")%></td>
			<td><%=notes%></td>
		</tr>
		<%
	}
	%>
	</table>
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