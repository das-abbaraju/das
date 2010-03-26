<%@page language="java" errorPage="exception_handler.jsp"%>
<%@page import="com.picsauditing.mail.EmailBuilder"%>
<%@page import="com.picsauditing.jpa.entities.ContractorAccount"%>
<%@page import="com.picsauditing.mail.EmailSender"%>
<%@page import="com.picsauditing.dao.ContractorAccountDAO"%>
<%@page import="com.picsauditing.util.SpringUtils"%>
<%@page import="com.picsauditing.dao.NoteDAO"%>
<%@page import="com.picsauditing.jpa.entities.Note"%>
<%@page import="com.picsauditing.jpa.entities.User"%>
<%@page import="com.picsauditing.jpa.entities.NoteCategory"%>
<%@page import="com.picsauditing.jpa.entities.Account"%>
<%@page import="com.picsauditing.access.OpPerms"%>
<jsp:useBean id="permissions" class="com.picsauditing.access.Permissions" scope="session" />
<%
String id = request.getParameter("id");
if ((null == id) || ("".equals(id)))
	throw new Exception("No contractor_id specified.");
ContractorAccountDAO dao = (ContractorAccountDAO) SpringUtils.getBean("ContractorAccountDAO");
ContractorAccount contractor = dao.find(Integer.parseInt(id));
EmailBuilder emailBuilder = new EmailBuilder();
emailBuilder.setTemplate(2); // Welcome Email
emailBuilder.setPermissions(permissions);
emailBuilder.setContractor(contractor, OpPerms.ContractorAdmin);
EmailQueue emailQueue = emailBuilder.build();
emailQueue.setPriority(90);
emailQueue.setFromAddress("\"PICS Customer Service\"<info@picsauditing.com>");
EmailSender.send(emailQueue);

NoteDAO noteDAO = (NoteDAO) SpringUtils.getBean("NoteDAO");
Note note = new Note();
note.setAccount(contractor);
note.setAuditColumns(new User(permissions.getUserId()));
note.setSummary("Welcome Email Sent "+ emailBuilder.getSentTo());
note.setNoteCategory(NoteCategory.General);
note.setViewableById(Account.EVERYONE);
noteDAO.save(note);

String message = "A welcome email was sent to "+emailBuilder.getSentTo();

%>
<%@page import="com.picsauditing.jpa.entities.EmailQueue"%>
<html>
<head>
<title>PICS - Welcome Email</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>
<body bgcolor="#EEEEEE" vlink="#003366" alink="#003366" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
	<center><%=message%><br>
	<br>
	<a href="javascript:window.close();" class="blueMain">Close This Window</a>
	</center>
</body>
</html>
