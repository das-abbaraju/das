<%@ page language="java"%>
<%@page import="com.picsauditing.access.*"%>
<%@page import="java.util.*"%>
<%@page import="com.picsauditing.PICS.Utilities"%>
<jsp:useBean id="uBean" class="com.picsauditing.access.User" scope ="page"/>
<jsp:useBean id="pBean" class="com.picsauditing.PICS.PermissionsBean" scope="session" />
<%
//pBean.getPermissions().tryPermission(OpPerms.EditUsers);
List<User> myGroups = new ArrayList<User>();
List<User> myMembers = new ArrayList<User>();
Set<Permission> myPerms = new HashSet<Permission>();
boolean isGroup = false;

if (request.getParameter("submit") == null) {
	String editID = request.getParameter("id");
	uBean.setFromDB(editID);
	if (uBean.userDO.id.length() > 0) {
		myGroups = uBean.getGroups();
		myPerms = uBean.getOwnedPermissions();
		myMembers = uBean.getMembers();
		isGroup = uBean.userDO.isGroup.equals("Yes");
	}
} else {
	uBean.setFromRequest(request);
	if (uBean.isOK()){
		//uBean.writeToDB();
		//AUDITORS.resetAuditorsAL();
		//response.sendRedirect("users_manage.jsp");
		return;
	}//if
}//else
%>
<br /><br />
<table border="0" cellspacing="0" cellpadding="1">
<tr> 
	<td colspan="2" class="redMain"><%
		if (request.getParameter("submit") != null) out.println(uBean.getErrorMessages());
	%></td>
</tr>
<tr>
  <td>&nbsp;</td>
  <td><input name="submit" type="submit" class="forms" value="Save" style="font-size: 14px; font-weight: bold;"></td>
</tr>
<tr>
	<td class="blueMain" align="right">Is Group?</td>
	<td class="blueMain" align="left"><%=com.picsauditing.PICS.Inputs.getYesNoRadio("isGroup","forms", uBean.userDO.isGroup)%></td>
</tr>
<tr> 
	<td class="blueMain" align="right">Name</td>
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
	<td><input name="password" type="password" class="forms" size="15" value="<%=uBean.userDO.password%>"></td>
</tr>
<% } %>
<tr>
	<td class="blueMain" align="right">Active?</td>
	<td class="blueMain" align="left"><%=com.picsauditing.PICS.Inputs.getYesNoRadio("isActive","forms", uBean.userDO.isActive)%></td>
</tr>
</table>

<p>Member of Group(s): <%
for (User group: myGroups) {
%>
<a href="#" onclick="showUser(<%=group.userDO.id%>); return false;"><%=group.userDO.name%></a>
	(<a href="removeGroup(<%=uBean.userDO.id%>, <%=group.userDO.id%>); return false;">remove</a>) | 
<%
}
%> <a href="#" onclick="showUser(10); return false;" style="font-style: italic;">Add to Group</a>
</p>

<table>
	<tr bgcolor="#003366" class="whiteTitle">
		<td>Permission</td>
		<td>Read</td>
		<td>Edit</td>
		<td>Delete</td>
		<td>Grant</td>
		<td>&nbsp;</td>
	</tr>
<%
for (Permission perm: myPerms) {
%>
	<tr class="active">
		<td><%=perm.getAccessType().getDescription()%></td>
		<td><%=perm.isViewFlag()%></td>
		<td><%=perm.isEditFlag()%></td>
		<td><%=perm.isDeleteFlag()%></td>
		<td><%=perm.isGrantFlag()%></td>
		<td><a href="#">Remove</a></td>
	</tr>
<%
}

ArrayList<String> grantPerms = new ArrayList<String>();
for(Permission perm: uBean.getPermissions()) {
	if (perm.isGrantFlag()) {
		grantPerms.add(perm.getAccessType().name());
		grantPerms.add(perm.getAccessType().getDescription());
	}
}
%>
	<tr class="active">
		<td>
		<% //Utilities.inputSelect("accessType", "", "", grantPerms);
		%>
		</td>
		<td></td>
		<td></td>
		<td></td>
		<td></td>
		<td><a href="#">Add</a></td>
	</tr>
</table>

<% if (isGroup) { %>
<p>Contains Member(s): 
<ul>
<%
for (User group: myMembers) {
%>
<li>
	<a href="#" 
		onclick="showUser(<%=group.userDO.id%>); return false;"
		<% if (group.userDO.isActive.startsWith("N")) { %> style="font-style: italic; color: #999999"<% } %>
		><%=group.userDO.name%></a>
	(<a href="#" onclick="removeGroup(<%=group.userDO.id%>, <%=uBean.userDO.id%>); return false;">remove</a>)
</li>
<%
}
%>
</ul>
</p>
<% } %>
