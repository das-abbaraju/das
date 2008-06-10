<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp"%>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean"
	scope="page" />
<jsp:useBean id="note" class="com.picsauditing.PICS.redFlagReport.Note"
	scope="page" />
<%@page import="org.apache.commons.beanutils.BasicDynaBean"%>
<%@page import="java.util.List"%>
<%
	//ADDED FROM con_redFlags

	String id = request.getParameter("id");
	String action = request.getParameter("action");
	String todaysDate = com.picsauditing.PICS.DateBean.getTodaysDateTime();
	cBean.setFromDB(id);
	cBean.tryView(permissions);
	String pre = "(" + pBean.userName + ")";
	boolean canEditNotes = pBean.isAdmin() || (pBean.isAuditor() && pBean.auditorCanSeeSet.contains(id));
	boolean canOperatorEditNotes = permissions.hasPermission(com.picsauditing.access.OpPerms.EditNotes);
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
		response.sendRedirect("add_notes.jsp?id=" + id);
		return;
	}//if
	else if ("Change Internal Notes".equals(action) && pBean.isAdmin()) {
		String changedAdminNotes = request.getParameter("changedAdminNotes");
		cBean.adminNotes = changedAdminNotes;
		cBean.isAdminNotesChanged = true;
		cBean.writeToDB();
		response.sendRedirect("add_notes.jsp?id=" + id);
		return;
	}//if
	if ("Add Both Notes".equals(action) && canEditNotes) {
		String notesDate = request.getParameter("notesDate");
		String newNote = request.getParameter("newNote");
		String newAdminNote = request.getParameter("newAdminNote");
		cBean.addNote(id, pre, newNote, notesDate);
		cBean.addAdminNote(id, pre, newAdminNote, notesDate);
		cBean.writeToDB();
		response.sendRedirect("add_notes.jsp?id=" + id);
		return;
	}//if
	// ADDED FROM con_redFlags
	if ("Add Operator Notes".equals(action) && canOperatorEditNotes) {
		String newNote = request.getParameter("newOperatorNote");
		if (null != newNote && !"".equals(newNote)) {
			note = new com.picsauditing.PICS.redFlagReport.Note(permissions.getAccountIdString(), id,
					permissions.getUserIdString(), permissions.getName(), newNote);
			note.writeToDB();
			response.sendRedirect("add_notes.jsp?id=" + id);
			return;
		}//if
	}//if
	if ("DeleteNote".equals(action) && canOperatorEditNotes) {
		String deleteID = request.getParameter("dID");
		new com.picsauditing.PICS.redFlagReport.Note().deleteNote(deleteID, permissions);
		response.sendRedirect("add_notes.jsp?id=" + id);
		return;
	}//if
%>
<html>
<head>
<script src="js/prototype.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<title>Notes</title>
<style>
table.notes {
}
div.internal {
	float:right;
	width: 40%;
	border: 2px black solid;
	margin: 5px;
	background-color: #EEE;
}
div.external {
	border: 2px black solid;
	background-color: #DDD;
	margin: 5px;
}
</style>
</head>
<body>
<%
	request.setAttribute("subHeading", "Contractor Notes");
	String conID = id;
%>
<%@ include file="includes/conHeaderLegacy.jsp"%>

<%
	if (!permissions.isContractor()) {
		List<BasicDynaBean> notesList = note.getContractorNotes(id, permissions);
		int count = 0;
%>
<div style="text-align: center;"><a href="#new">Add new Note</a></div>
<h2>Operator Notes</h2>
<table class="report">
<tr>
	<th>Date</th>
	<th>Operator</th>
	<th>User</th>
	<th>Notes</th>
</tr>
<%
		for (BasicDynaBean row : notesList) {
			count++;
			%>
			<tr>
			<td><%=row.get("formattedDate") %></td>
			<td><%=FACILITIES.getNameFromID(row.get("opID").toString()) %></td>
			<td><%=row.get("whoIs") %></td>
			<td><%=row.get("note") %></td>
			<%
			if (canOperatorEditNotes) {
				%><td><a onclick="return confirm('Are you sure you want to delete this note?');"
				href="?id=<%=id%>&action=DeleteNote&dID=<%=row.get("noteID")%>">Delete</a></td>
				<%
			}
			%>
			</tr>
			<%
		}
%>
</table>
<br /><br />
<div style="text-align: center;"><a href="#new">Add new Note</a></div>
<%
	}
%>
<%
	if (permissions.seesAllContractors()) {
%>
		<div class="internal">
			<h2>Internal Notes</h2>
			<div id="internalView" class="notes" ondblclick="$('internalView').hide(); $('internalEdit').show();" title="Double click to edit">
				<%=com.picsauditing.PICS.Utilities.escapeNewLines(cBean.adminNotes)%>
			</div>
			<div id="internalEdit" style="display: none;">
				<form method="post">
					<input type="hidden" name="id" value="<%=id%>">
					<textarea name="changedNotesInternal" rows="20" style="width: 100%"><%=cBean.adminNotes%></textarea><br />
					<input name="action" type="submit" value="Save Notes">
				</form>
			</div>
		</div>
<%
	}
%>
<div class="external">
	<h2>PICS Notes</h2>
	<div id="externalView" class="notes" ondblclick="$('externalView').hide(); $('externalEdit').show();" title="Double click to edit">
		<%=com.picsauditing.PICS.Utilities.escapeNewLines(cBean.notes)%>
	</div>
	<div id="externalEdit" style="display: none;">
		<form method="post">
			<input type="hidden" name="id" value="<%=id%>">
			<textarea name="changedNotesExternal" style="width: 50%" rows="20"><%=cBean.notes%></textarea><br />
			<input name="action" type="submit" value="Save Notes">
		</form>
	</div>
</div>
<br clear="all">

<a name="new"></a>
<%
if (canEditNotes || canOperatorEditNotes) {
%>
<h2>Add New Note</h2>
<form action="add_notes.jsp" method="post" name="newNoteForm"
	id="newNoteForm">
	<input type="hidden" name="id" value="<%=id%>">
	Date: <input name="notesDate" value="<%=todaysDate%>">
	
	<% if (permissions.isPicsEmployee()) { %>
	<label><input type="radio" name="noteType" value="external" checked="checked" />External</label>
	<label><input type="radio" name="noteType" value="internal" />Internal</label>
	<% } %>
	<br />
	<textarea name="newNote" cols="70" rows="5"></textarea><br />
	<input name="action" type="submit" value="Add Note">
</form>
<%
}
%>
</body>
</html>