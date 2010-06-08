<%@page language="java" errorPage="exception_handler.jsp"%>
<%@page import="com.picsauditing.mail.EmailBuilder"%>
<%@page import="com.picsauditing.util.SpringUtils"%>
<%@page import="com.picsauditing.dao.AuditDataDAO"%>
<%@page import="java.util.List"%>
<%@page import="com.picsauditing.jpa.entities.AuditData"%>
<%@page import="java.io.File"%>
<%@page import="com.picsauditing.util.FileUtils"%>
<%@page import="com.picsauditing.PICS.PICSFileType"%>
<%@page import="com.picsauditing.util.Strings"%>
<%
String number = request.getParameter("count");
String path = System.getProperty("pics.ftpDir");
if(Strings.isEmpty(path))
	path = application.getInitParameter("FTP_DIR");
AuditDataDAO dao = (AuditDataDAO) SpringUtils.getBean("AuditDataDAO");
String where = "t.answer = 'undefined' AND t.question.questionType = 'File'";
List<AuditData> auditDatas = (List<AuditData>) dao.findWhere(AuditData.class, where, Integer.parseInt(number));
int count = 0;
for(AuditData auditData : auditDatas) {
	File dir = new File(path + "/files/" + FileUtils.thousandize(auditData.getId()));
	File[] files = FileUtils.getSimilarFiles(dir, PICSFileType.data+ "_" + auditData.getId());
	if(files.length > 0) {
		String extension = FileUtils.getExtension(files[0].getName());
		auditData.setAnswer(extension);
		dao.save(auditData);
		count++;
	}
}
String message = "Found a match for " + count;
%>

<html>
<head>
<title>PICS</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>
<body bgcolor="#EEEEEE" vlink="#003366" alink="#003366" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
	<center><%=message%><br>
	<br>
	</center>
</body>
</html>
