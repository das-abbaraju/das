<%@page language="java" import="com.picsauditing.PICS.*,java.sql.*"
	errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp"%>
<%@page import="com.picsauditing.jpa.entities.ContractorAudit"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.picsauditing.jpa.entities.AuditType"%>
<%@page import="com.picsauditing.jpa.entities.ContractorAccount"%>
<%@page import="com.picsauditing.jpa.entities.AuditStatus"%>
<%@page import="java.text.DateFormat"%>
<%@page import="com.picsauditing.dao.ContractorAuditDAO"%>
<%@page import="com.picsauditing.util.SpringUtils"%>
<%@page import="com.picsauditing.dao.NoteDAO"%>
<%@page import="com.picsauditing.jpa.entities.Note"%>
<%@page import="com.picsauditing.jpa.entities.User"%>
<%@page import="com.picsauditing.jpa.entities.NoteCategory"%>
<%@page import="com.picsauditing.jpa.entities.Account"%>

<%
	Connection Conn = null;
	Statement SQLStatement = null;
	String Query = "";
	ResultSet SQLResult = null;
	ResultSetMetaData SQLResultMetaData = null;
	if (permissions.isContractor()) throw new com.picsauditing.access.NoRightsException("Not Contractor");
	try {

		String name = request.getParameter("name");
		String conID = request.getParameter("conID");
		String action = request.getParameter("action");
		String closedDate = request.getParameter("closedDate");
		Conn = com.picsauditing.PICS.DBBean.getDBConnection();
		NoteDAO noteDAO = (NoteDAO) SpringUtils.getBean("NoteDAO");
		SQLStatement = Conn.createStatement();
		if ("Approve".equals(action)) {
			//link in db
			ContractorAuditDAO  conAuditdao = (ContractorAuditDAO)SpringUtils.getBean("ContractorAuditDAO");
			ContractorAudit conAudit = new ContractorAudit();
			conAudit.setPercentComplete(100);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			try {
				conAudit.setCompletedDate(df.parse(closedDate));
			} catch(Exception e) {
				System.out.println(e);
			}
			conAudit.setClosedDate(conAudit.getCompletedDate());
			conAudit.setAuditType(new AuditType(4));
			conAudit.setContractorAccount(new ContractorAccount());
			conAudit.getContractorAccount().setId(Integer.parseInt(conID));
			conAudit.setPercentVerified(0);
			conAudit.setAuditColumns(new User(permissions.getUserId()));
			conAudit.changeStatus(AuditStatus.Active, new User(permissions.getUserId()));
			conAuditdao.save(conAudit);
			
			Note note = new Note();
			note.setAccount(new Account());
			note.getAccount().setId(Integer.parseInt(conID));
			note.setAuditColumns(new User(permissions.getUserId()));
			note.setSummary("NCMS Desktop imported, approved");
			note.setNoteCategory(NoteCategory.Audits);
			note.setViewableById(Account.EVERYONE);
			noteDAO.save(note);

			Query = "UPDATE NCMS_Desktop SET remove='Yes',conID="
					+ conID
					+ ",approved='Yes' WHERE ContractorsName='"
					+ com.picsauditing.PICS.Utilities
							.escapeQuotes(name) + "';";
			SQLStatement.executeUpdate(Query);

			SQLStatement.close();
			SQLStatement = null;
			Conn.close();
			Conn = null;

			response.sendRedirect("ReportNCMS.action");
			return;
		}//if
		//SELECT * FROM `NCMS_Contractors` C JOIN NCMS_Desktop D WHERE C.ContractorsName=D.ContractorsName<br>
		// generate query
		if ("Remove".equals(action)) {
			Query = "UPDATE NCMS_Desktop SET remove='Yes' AND conID="
					+ conID
					+ " WHERE ContractorsName='"
					+ com.picsauditing.PICS.Utilities
							.escapeQuotes(name) + "';";
			SQLStatement.executeUpdate(Query);

			SQLStatement.close();
			SQLStatement = null;
			Conn.close();
			Conn = null;

			Note note = new Note();
			note.setAccount(new Account());
			note.getAccount().setId(Integer.parseInt(conID));
			note.setAuditColumns(new User(permissions.getUserId()));
			note.setSummary("NCMS Desktop imported, approved");
			note.setNoteCategory(NoteCategory.Audits);
			noteDAO.save(note);

			response.sendRedirect("ReportNCMS.action");
			return;
		}//if
		if ((null != name) && !"".equals(name))
			Query = "SELECT * FROM NCMS_Desktop WHERE ContractorsName='"
					+ com.picsauditing.PICS.Utilities
							.escapeQuotes(name) + "';";

		SQLResult = SQLStatement.executeQuery(Query);
		SQLResultMetaData = SQLResult.getMetaData();
		SQLResult.next();
		String lastReview = SQLResult.getString("lastReview");
%>
<html>
<head>
<title>NCMS</title>
<link rel="stylesheet" type="text/css" media="screen"
	href="css/forms.css" />
</head>
<body>
<h1><%=name%><span class="sub">NCMS Data</span></h1>
<table width="657" border="0" cellpadding="0" cellspacing="0"
	align="center">
	<tr>
		<td height="70" align="center" class="forms"><br />
		<br />
		<form name="form1" method="post" action="report_ncmsIndividual.jsp?">
		<input type=hidden name=conID value=<%=conID%>> <input
			type=hidden name=name value="<%=name%>"> <input type=hidden
			name=closedDate value=<%=lastReview%>> <input name="action"
			type="submit" class="forms" value="Approve"> <input
			name="action" type="submit" class="forms" value="Remove"></form>
		</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
	</tr>
</table>
<table class="forms">
	<%
		for (int i = 1; i <= SQLResultMetaData.getColumnCount(); i++) {
	%>
	<tr>
		<th><%=SQLResultMetaData.getColumnName(i)%>:</th>
		<td>&nbsp;<%=SQLResult.getString(i)%></td>
	</tr>
	<%
		}//for
	%>
</table>
</body>
</html>
<%
	} finally {
		if (null != SQLResult) {
			SQLResult.close();
			SQLResult = null;
		}//if
		if (null != SQLStatement) {
			SQLStatement.close();
			SQLStatement = null;
		}//if
		if (null != Conn) {
			Conn.close();
			Conn = null;
		}//if
	}//finally
%>