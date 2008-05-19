<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp"%>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean"
	scope="page" />
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean"
	scope="page" />
<jsp:useBean id="note" class="com.picsauditing.PICS.redFlagReport.Note" scope="page" />	
<%@page import="org.apache.commons.beanutils.BasicDynaBean"%>
<%
	//ADDED FROM con_redFlags
	boolean canOperatorEditNotes = permissions.hasPermission(com.picsauditing.access.OpPerms.EditNotes);

	String id = request.getParameter("id");
	String action = request.getParameter("action");
	String todaysDate = com.picsauditing.PICS.DateBean
			.getTodaysDateTime();
	cBean.setFromDB(id);
	cBean.tryView(permissions);
	String pre = "(" + pBean.userName + ")";
	boolean canEditNotes = pBean.isAdmin()
			|| (pBean.isAuditor() && pBean.auditorCanSeeSet
					.contains(id));
	if ("Add Note".equals(action) && canEditNotes) {
		String notesDate = request.getParameter("notesDate");
		String newNote = request.getParameter("newNote");
		cBean.addNote(id, pre, newNote, notesDate);
		cBean.writeToDB();
		response.sendRedirect("ContractorView.action?id=" + id);
		return;
	}//if
	else if ("Change Notes".equals(action) && pBean.isAdmin()) {
		String changedNotes = request.getParameter("changedNotes");
		cBean.notes = changedNotes;
		cBean.isNotesChanged = true;
		cBean.writeToDB();
		response.sendRedirect("ContractorView.action?id=" + id);
		return;
	}//if
	if ("Add Internal Note".equals(action) && canEditNotes) {
		String notesDate = request.getParameter("adminNotesDate");
		String newAdminNote = request.getParameter("newAdminNote");
		cBean.addAdminNote(id, pre, newAdminNote, notesDate);
		cBean.writeToDB();
		response.sendRedirect("ContractorView.action?id=" + id);
		return;
	}//if
	else if ("Change Internal Notes".equals(action) && pBean.isAdmin()) {
		String changedAdminNotes = request
				.getParameter("changedAdminNotes");
		cBean.adminNotes = changedAdminNotes;
		cBean.isAdminNotesChanged = true;
		cBean.writeToDB();
		response.sendRedirect("ContractorView.action?id=" + id);
		return;
	}//if
	if ("Add Both Notes".equals(action) && canEditNotes) {
		String notesDate = request.getParameter("notesDate");
		String newNote = request.getParameter("newNote");
		String newAdminNote = request.getParameter("newAdminNote");
		cBean.addNote(id, pre, newNote, notesDate);
		cBean.addAdminNote(id, pre, newAdminNote, notesDate);
		cBean.writeToDB();
		response.sendRedirect("ContractorView.action?id=" + id);
		return;
	}//if
	// ADDED FROM con_redFlags
	if ("Add Operator Notes".equals(action) && canOperatorEditNotes){
		String newNote = request.getParameter("newOperatorNote");
		if (null != newNote && !"".equals(newNote)){
			note= new com.picsauditing.PICS.redFlagReport.Note(permissions.getAccountIdString(), id, permissions.getUserIdString(), permissions.getName(), newNote);
			note.writeToDB();
			response.sendRedirect("ContractorView.action?id="+id);
			return;
		}//if
	}//if
	if ("DeleteNote".equals(action) && canOperatorEditNotes){
		String deleteID = request.getParameter("dID");
		new com.picsauditing.PICS.redFlagReport.Note().deleteNote(deleteID,permissions);
		response.sendRedirect("ContractorView.action?id="+id);
		return;
	}//if
%>
<%@page import="java.util.List"%>
<html>
<head>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<title>Notes</title>
</head>
<body>
<% request.setAttribute("subHeading", "Contractor Notes");
	String conID = id;
%>
<%@ include file="includes/conHeaderLegacy.jsp"%>
<table border="2" align="center" cellpadding="5" cellspacing="5" width="100%">
<%if (!permissions.isContractor()){ %>
<span class="redMain">Operator Notes:</span><br>
<tr>
<td class=blueMain>
<div class="blueMain" style="width:350px;height:150px;overflow:auto;">
<%
List<BasicDynaBean> notesList = note.getContractorNotes(id,permissions);
int count = 0;
for (BasicDynaBean row : notesList){
count++;
String getNotes = row.get("formattedDate").toString()+"("+row.get("whoIs").toString()+","+FACILITIES.getNameFromID(row.get("opID").toString())+"):"+
row.get("note").toString();
%>
<%=com.picsauditing.PICS.Utilities.escapeNewLines(getNotes)%>
<%
if(canOperatorEditNotes && permissions.isCorporate() || permissions.isOperator())
{%>
&nbsp;&nbsp;&nbsp;<a href="?id=<%=id%>&action=DeleteNote&dID=<%=row.get("noteID")%>">Delete</a><br/>
<%	
}
}//for
%>
</div>
</td>
</tr>
<% } %>
<tr><td>
<span class="redMain">External Notes:</span><br>
<div class="blueMain" style="width:350px;height:150px;overflow:auto;">
<%=com.picsauditing.PICS.Utilities.escapeNewLines(cBean.notes)%>
</div>
</td>
<%
	if (pBean.isAdmin()
		|| (pBean.isAuditor() && pBean.auditorCanSeeSet
		.contains(id))) {
%>
<td>
<span class="redMain">Internal Notes:</span><br>
<div class="blueMain" style="width:350px;height:150px;overflow:auto;">
<%=com.picsauditing.PICS.Utilities.escapeNewLines(cBean.adminNotes)%>
</div>
</td>
<%
 }//if
%>
</tr>
</table>

<table border="0" align="center" cellpadding="5" cellspacing="0">
	<%
		if (pBean.isAdmin()
				|| ((pBean.isAuditor() && pBean.auditorCanSeeSet
						.contains(id)))) {
	%>
	<tr>
		<td valign="top" class="blueMain">
		<form action="add_notes.jsp" method="post" name="newNoteForm"
			id="newNoteForm"><input type="hidden" name="id"
			value="<%=id%>">
		<table border="0" cellspacing="0" cellpadding="0">
			<%
				if (pBean.isAdmin()
							|| ((pBean.isAuditor() && pBean.auditorCanSeeSet
									.contains(id)))) {
			%>
			<tr>
				<td class="redMain" align="left"><b>We now have 2 types of
				notes!!!<br>
				This side is viewable by Admins, Auditors, and Operators</b><br>
				</td>
			</tr>
			<%
				}//if
			%>
			<tr>
				<td class="redMain" align="left">Date: <input name="notesDate"
					class="forms" id="notesDate" value="<%=todaysDate%>"></td>
			</tr>
			<tr>
				<td class="redMain" align="left">New Note:</td>
			</tr>
			<tr>
				<td><textarea name="newNote" cols="42" rows="10" class="forms"
					id="notes"></textarea></td>
			</tr>
			<tr>
				<td align=left><input name="action" type="submit"
					class="buttons" value="Add Note"></td>
			</tr>
			<tr>
				<td align=right><input name="action" type="submit"
					class="buttons" value="Add Both Notes"></td>
			</tr>
		</table>
		</td>
		<td valign="top" class="blueMain">
		<%
			if (pBean.isAdmin()
						|| (pBean.isAuditor() && pBean.auditorCanSeeSet
								.contains(id))) {
		%>
		<table border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td class="redMain" align="left"><b>This side is called
				'Internal Notes'.<br>
				It is viewable by Auditors and Admins, but not by Operators</b><br>
				</td>
			</tr>
			<tr>
				<td class="redMain" align="left">Date: <input
					name="adminNotesDate" class="forms" id="adminNotesDate"
					value="<%=todaysDate%>"></td>
			</tr>
			<tr>
				<td class="redMain" align="left">New Internal Note:</td>
			</tr>
			<tr>
				<td><textarea name="newAdminNote" cols="42" rows="10"
					class="forms" id="newAdminNote"></textarea></td>
			</tr>
			<tr>
				<td align=right><input name="action" type="submit"
					class="buttons" value="Add Internal Note"></td>
			</tr>
		</table>
		</form>
		<%
			}//if
		%>
		</td>
	</tr>
	<%
		}//if
	%>
	
	<%
		if (pBean.isAdmin()) {
	%>
	<tr>
		<td valign="top" class="blueMain">
		<form action="add_notes.jsp" method="post" name="newVehicle"
			id="newVehicle"><input type="hidden" name="id" value="<%=id%>">
		<table border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td class="redMain">Edit Notes:</td>
			</tr>
			<tr>
				<td><textarea name="changedNotes" cols="42" rows="14"
					class="forms" id="notes"><%=cBean.notes%></textarea> <input
					name="action" type="submit" class="buttons" value="Change Notes">
				</td>
			</tr>
		</table>
		</form>
		</td>
		<td valign="top" class="blueMain">
		<form action="add_notes.jsp" method="post" name="editAdminNotesForm"
			id="editAdminNotesForm"><input type="hidden" name="id"
			value="<%=id%>">
		<table border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td class="redMain">Edit Internal Notes:</td>
			</tr>
			<tr>
				<td><textarea name="changedAdminNotes" cols="42" rows="14"
					class="forms" id="notes"><%=cBean.adminNotes%></textarea> <input
					name="action" type="submit" class="buttons"
					value="Change Internal Notes"></td>
			</tr>
		</table>
		</form>
		</td>
	</tr>
	<%
		}//if
		if(canOperatorEditNotes && permissions.isCorporate() || permissions.isOperator()) {
    %>
			<tr>
                <td valign="top" colspan=2>
                  <form action="add_notes.jsp" method="post" name="newNoteForm" id="newNoteForm">
                    <table border="0" cellspacing="1" cellpadding="1">
                      <tr>
                        <td class="redMain" align="left"><b>Enter a new note about <%=aBean.name%></b></td>
                      </tr>
                      <tr>
                     <td class="redMain" align="left">Date: <input name="notesDate"
					class="forms" id="notesDate" value="<%=todaysDate%>"></td>
                      </tr>
                      <tr>
					<td class="redMain" align="left">New Note:</td>
					</tr>
                    <tr>
                     <td>
                     <textarea name="newOperatorNote" cols="42" rows="10" class="forms" id="notes"></textarea>
                     </td>
                     </tr>
                      <tr>
                        <td align=right>
                          <input name="action" type="submit" class="buttons" value="Add Operator Notes">
                          <input name="id" type="hidden" value="<%=id%>">
                        </td>
                      </tr>
                    </table>
                  </form>
                </td>
              </tr>
<%	
	}//if
%>
</table>
</body>
</html>