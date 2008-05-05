<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp"%>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean"
	scope="page" />
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean"
	scope="page" />
<%
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
%>
<html>
<head>
<title>Notes</title>
</head>
<body>
<h1><%=aBean.getName(id)%><span class="sub">Contractor Notes</span></h1>
<%@ include file="utilities/adminOperatorContractorNav.jsp"%>

<div>
<%=com.picsauditing.PICS.Utilities.escapeNewLines(cBean.notes)%>
</div>


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
	<tr>
		<td valign="top" class="redMain" align="left">
		</td>
		<td valign="top" class="redMain" align="left">
		<%
			if (pBean.isAdmin()
					|| (pBean.isAuditor() && pBean.auditorCanSeeSet
							.contains(id))) {
		%>
		Internal Notes:<br>
		<span class="blueMain"><%=com.picsauditing.PICS.Utilities
								.escapeNewLines(cBean.adminNotes)%></span>
		<%
			}//if
		%>
		</td>
	</tr>
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
	%>

</table>
</body>
</html>