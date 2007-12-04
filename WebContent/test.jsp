<%@ page language="java" import="java.sql.*, java.io.File"%>
<%//@ page language="java" import="java.sql.*,java.io.File,javax.mail.*,javax.mail.internet.*, java.util.Properties, javax.activation.*"%>

<%@ include file="/utilities/admin_secure.jsp" %>

<%
	
 //<jsp:useBean id="ewBean" class="com.picsauditing.PICS.ExcelWriterBean" scope ="page"/>

//	response.setContentType("application/vnd.ms-excel");
//	ewBean.test(response.getOutputStream());
%>
<%!
Connection Conn = null;
Statement SQLStatement = null;
String Query = null;
ResultSet SQLResult = null;
ResultSetMetaData SQLResultMetaData = null;
%>

<%
try{

//---------------------------------
/*	String last = "";
	String last2 = "";
	String last3 = "";
	for (int i=0;i<10000;i++){
		String temp = com.picsauditing.PICS.DateBean.getTodaysDate();
		String temp2 = com.picsauditing.PICS.DateBean.toDBFormat(temp);
		String temp3 = com.picsauditing.PICS.DateBean.toShowFormat(temp2);
		if (!last.equals(temp)){
			last = temp;
			out.println("Today: "+temp+"<br>");
		}//if
		if (!last2.equals(temp2)){
			last2 = temp2;
			out.println("Today: "+temp2+"<br>");
		}//if
		if (!last3.equals(temp3)){
			last3 = temp3;
			out.println("Today: "+temp3+"<br>");
		}//if
//	}//for
*/	//---------------------------------
/*	String username = "info@picsauditing.com";
	String pass = "w3e4r5";

	String from = "PICS <"+"info@picsauditing.com>";
//	to = "jjensen@picsauditing.com";
	String to = "jeffjensen@byu.edu";
//	to = "elderjoemama@yahoo.com";
	String subject = "test9";
	String text1 = "test of authentication";
	String text2 = "test <b>of</b> authent<br>ication";
	Properties props = new Properties();
//	props.put("mail.smtp.host", "mail.picsauditing.com");
	props.put("mail.smtp.host", "localhost");
//	props.put("mail.smtp.auth", "true");
	Session s = Session.getInstance(props,null);
//	s.setDebug(true);

	MimeMessage message = new MimeMessage(s);
		
	Multipart multipart = new MimeMultipart();

	MimeBodyPart messageBodyPart = new MimeBodyPart();
	messageBodyPart.setContent(text1,"text/plain");
	multipart.addBodyPart(messageBodyPart);
	messageBodyPart = new MimeBodyPart();
	messageBodyPart.setContent(text2,"text/html");
	multipart.addBodyPart(messageBodyPart);
		
	message.setContent(multipart);

	InternetAddress f = new InternetAddress(from);
	message.setFrom(f);
	InternetAddress t = new InternetAddress(to);
	message.addRecipient(Message.RecipientType.TO, t);		
	message.setSubject(subject);
//	message.setText("test");

	Transport transport = s.getTransport("smtp");
//	transport.connect("localhost", username, pass);
//	transport.connect("mail.picsauditing.com", username, pass);
	transport.connect();
	transport.sendMessage(message, message.getAllRecipients());	
*/
//-------------------------
//	com.picsauditing.PICS.EmailBean eBean = new com.picsauditing.PICS.EmailBean();
//	eBean.sendAnnualUpdateEmail("249",adminName);
//-------------------------
//	com.picsauditing.PICS.ContractorBean cBean = new com.picsauditing.PICS.ContractorBean();
//	cBean.convertTrades();
//-----------------------
// Tests creating folders	
//	String path = config.getServletContext().getRealPath("/");
//	String folderPath = path + "files" + File.separator + "pqf"+ File.separator + "testDir";
//	File newFolderFile = new File(folderPath);
//	newFolderFile.mkdirs();
//-------------------

/*	com.picsauditing.PICS.VerifyPDFsBean vBean = new com.picsauditing.PICS.VerifyPDFsBean();
	for (int i=310;i<311;i++) {
		String temp = Integer.toString(i);
		vBean.convertPDFs(temp,config);
	} // for
*/
//------------------
/*  **To test the emails getting screened by AOL
	com.picsauditing.PICS.EmailBean eBean = new com.picsauditing.PICS.EmailBean();
	eBean.sendTestLoginEmail("","","","");
*/
/*
	// transfers all the requestedBy infor from accounts tablet to contractor table
	Conn = com.picsauditing.PICS.DBBean.getDBConnection();
	// query statement
	SQLStatement = Conn.createStatement();
	// generate query
	Query = "SELECT * FROM auditQuestions aq INNER JOIN auditCategories ac USING(category);";
	SQLResult = SQLStatement.executeQuery(Query);
	SQLResultMetaData = SQLResult.getMetaData();

	while(SQLResult.next())	{
		String qID = SQLResult.getString("questionID");
//		String rb = SQLResult.getString("a.requestedBy");
//		String rbID = SQLResult.getString("requestedByID");
		String catID = SQLResult.getString("catID");
		String Q2 = "UPDATE auditQuestions SET categoryID='"+catID+"' WHERE questionID="+qID+";";
	//	String Q2 = "UPDATE contractor_info SET requestedBy = '"+rb+"', requestedByID = "+rb2+
//				" WHERE id = "+id+";";
		SQLStatement.executeUpdate(Q2);
		} // while
--------------------------
*/
/*
	//SELECT * FROM `NCMS_Contractors` C JOIN NCMS_Desktop D WHERE C.ContractorsName=D.ContractorsName<br>
	// transfers all the taxIDs from NCMSDesktop to NCMS OSHA
	Conn = com.picsauditing.PICS.DBBean.getDBConnection();
	// query statement
	SQLStatement = Conn.createStatement();
	// generate query
	Query = "SELECT * FROM NCMS_Contractors;";
	SQLResult = SQLStatement.executeQuery(Query);
	SQLResultMetaData = SQLResult.getMetaData();

	while(SQLResult.next())	{
		String taxID = SQLResult.getString("Federal_ID");
		String lastReview = SQLResult.getString("Date_Of_Last_Review");
		String name = SQLResult.getString("ContractorsName");
		String Q2 = "UPDATE NCMS_Desktop SET taxID='"+taxID+"',lastReview='"+lastReview+
			"' WHERE ContractorsName='"+com.picsauditing.PICS.Utilities.escapeQuotes(name)+"';";
	//	String Q2 = "UPDATE contractor_info SET requestedBy = '"+rb+"', requestedByID = "+rb2+
//				" WHERE id = "+id+";";
		SQLStatement.executeUpdate(Q2);
		} // while
*/
/*/		cBean.setFromDB(id);
		vBean.verifyForms(id, config);
		if (vBean.isInfoOK) {
			String tempQuery = "UPDATE contractor_info SET isPrequalOK = 'Y' WHERE id = '" + id + "';";
			SQLStatement.executeUpdate(tempQuery);
		} else {
			String tempQuery = "UPDATE contractor_info SET isPrequalOK = 'N' WHERE id = '" + id + "';";
			SQLStatement.executeUpdate(tempQuery);
		} // if
	} // while
	SQLResult.close();
	SQLResult = null;
	SQLStatement.close();
	SQLStatement = null;
	Conn.close();
	Conn = null;
*/%>

<%//@ page language="java"%>
<%//@ page language="java" errorPage="exception_handler.jsp"%>

<html>
<head>
<title>PICS - Pacific Industrial Contractor Screening</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="PICS.css" rel="stylesheet" type="text/css">
</head>

<body>
<!--<img src="images/executive_member.gif" width="185" height="25" border="0">-->
<table>
		<tr>
			<td colspan="<%//=SQLResultMetaData.getColumnCount() %>">Table: <b><%//=SQLResultMetaData.getTableName(1) %></b></td>
		</tr>
		<tr>
<% 
//	for (int i=1; i<=SQLResultMetaData.getColumnCount(); i++) {
//		out.println("<td><b>" + SQLResultMetaData.getColumnName(i) + "&nbsp;</b></td>");
//	} // for
%>
	</tr>
<%	
/*	while(SQLResult.next())	{
		String id = SQLResult.getString("id");
		cBean.setFromDB(id);
		String temp = cBean.getTradeString();
		String temp2 = cBean.getSubTradeString();
		temp = temp.charAt(0) + ";" + temp.substring(1);
		if (temp2.length() == 0)
			temp2 = "0;";
		else
			temp2 = temp2.charAt(0) + ";" + temp2.substring(1);
		cBean.setTradeString(temp);
		cBean.setSubTradeString(temp2);
		cBean.writeToDB();


		out.println("<tr>");
		for (int i=1; i<=SQLResultMetaData.getColumnCount(); i++) {
			String temp = SQLResult.getString(i);
			if (temp == null) temp = "null";
			if (temp.length() > 80)
				temp = temp.substring(0,80);
			out.println("<td>" + temp + "&nbsp;</td>");
		} // for
		out.println("</tr>");
	} // while
	// close connection
	SQLResult.close();
	SQLResult = null;
	SQLStatement.close();
	SQLStatement = null;
	Conn.close();
	Conn = null;
*/%>	</table>
<%//=new com.picsauditing.PICS.OSHABean().renameOSHAFiles(config)%>
This page: <%=request.getServletPath()%>

</body>
</html>
<%	}finally{
		if (null != SQLResult){
			SQLResult.close();
			SQLResult = null;
		}//if
		if (null != SQLStatement){
			SQLStatement.close();
			SQLStatement = null;
		}//if
		if (null != Conn){
			Conn.close();
			Conn = null;
		}//if
	}//finally
%>