<%@ page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@page import="org.apache.commons.beanutils.*"%>
<%@page import="java.util.*"%>
<%@page import="com.picsauditing.access.*"%>
<jsp:useBean id="pBean" class="com.picsauditing.PICS.PermissionsBean" scope="session" />
<jsp:useBean id="pageBean" class="com.picsauditing.PICS.WebPage" scope ="page"/>

<%
pBean.getPermissions().tryPermission(OpPerms.EditUsers);

String action = request.getParameter("action");
String action_id = request.getParameter("action_id");
String action_type = request.getParameter("action_type");

if (("D".equals(action)) || ("Delete".equals(action)))
	//sBean.deleteAccount(action_id, config.getServletContext().getRealPath("/"));
if ("Edit".equals(action)){
	if ("Auditor".equals(action_type)) {
		response.sendRedirect("accounts_edit_auditor.jsp?id="+action_id);		
		return;
	}//if
}//if

SearchUsers search = new SearchUsers();
//search.sql.addField("u.username");
search.sql.addField("u.isGroup");
String isGroup = request.getParameter("isGroup");
if (isGroup != null && isGroup.equals("Yes")) {
	search.sql.addWhere("isGroup = 'Yes' ");
} else {
	// Default: users only
	search.sql.addWhere("isGroup = 'No' ");
	// Only search for Admin and Auditor users
	search.inGroups("10,11");
}
// Only search for Auditors and Admins
search.sql.addOrderBy("u.name");
search.setPageByResult(request);
search.setLimit(25);

List<BasicDynaBean> searchData = search.doSearch();

pageBean.setTitle("Manage Users");
pageBean.includeScriptaculous(true);
%>
<%@ include file="includes/header.jsp" %>
<script type="text/javascript">
function showUser(userID) {
	$('editUser').innerHTML = '<img src="images/ajax_process.gif" />';
	pars = 'id='+userID;
	var myAjax = new Ajax.Updater('editUser', 'user_edit.jsp', {method: 'post', parameters: pars});
}
</script>
<table border="0">
<tr valign="top"><td>
<table border="0" cellpadding="1" cellspacing="1">
	<tr>
		<td colspan="2" align="right"><a href="user_edit.jsp">Add New</a></td>
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
	<tr bgcolor="#FFFFFF" class="active" style="cursor: pointer" onclick="showUser(<%=row.get("id")%>); return false;">
		<td><%=counter++%></td>
		<td<% if (row.get("isActive").toString().startsWith("N")) { %> style="font-style: italic; color: #999999"<% } %>><%=row.get("name")%></td>
	</tr>
<%
}
%>
	<tr>
		<td colspan="2"><%=search.getPageLinks()%></td>
	</tr>
</table>
</td>
<td id="editUser" width="400" class="blueMain">
</td>
</tr>
</table>
<%@ include file="includes/footer.jsp" %>
