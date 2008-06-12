<%@page language="java" import="com.picsauditing.PICS.*,java.sql.*" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<%@include file="utilities/adminGeneral_secure.jsp" %>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope ="page"/>
<%

Connection Conn = null;
Statement SQLStatement = null;
String Query = "";
ResultSet SQLResult = null;
ResultSetMetaData SQLResultMetaData = null;
try{

	String name = request.getParameter("name");
	String conID = request.getParameter("conID");
	String action = request.getParameter("action");
	String closedDate = request.getParameter("closedDate");
	Conn = com.picsauditing.PICS.DBBean.getDBConnection();
	SQLStatement = Conn.createStatement();
	if ("Approve".equals(action)) {
		//link in db
		cBean.setFromDB(conID);
		cBean.desktopVerifiedPercent = "100";
		cBean.desktopSubmittedDate = closedDate;
		cBean.desktopClosedDate = closedDate;
		cBean.hasNCMSDesktop = "Yes";

		cBean.addNote(conID,"("+permissions.getUsername()+")","NCMS Desktop imported, approved",DateBean.getTodaysDateTime());
		cBean.writeToDB();

		Query = "UPDATE NCMS_Desktop SET remove='Yes',conID="+conID+",approved='Yes' WHERE ContractorsName='"+com.picsauditing.PICS.Utilities.escapeQuotes(name)+"';";
		SQLStatement.executeUpdate(Query);

		SQLStatement.close();
		SQLStatement = null;
		Conn.close();
		Conn = null;

		response.sendRedirect("report_ncms.jsp");
		return;
	}//if
	//SELECT * FROM `NCMS_Contractors` C JOIN NCMS_Desktop D WHERE C.ContractorsName=D.ContractorsName<br>
	// generate query
	if ("Remove".equals(action)) {
		Query = "UPDATE NCMS_Desktop SET remove='Yes' AND conID="+conID+" WHERE ContractorsName='"+com.picsauditing.PICS.Utilities.escapeQuotes(name)+"';";
		SQLStatement.executeUpdate(Query);

		SQLStatement.close();
		SQLStatement = null;
		Conn.close();
		Conn = null;

		cBean.setFromDB(conID);
		cBean.addNote(conID,"("+permissions.getUsername()+")","NCMS Desktop imported, approved",DateBean.getTodaysDateTime());
		cBean.writeToDB();

		response.sendRedirect("report_ncms.jsp");
		return;
	}//if
	if ((null!=name) && !"".equals(name))
		Query = "SELECT * FROM NCMS_Desktop WHERE ContractorsName='"+com.picsauditing.PICS.Utilities.escapeQuotes(name)+"';";

	SQLResult = SQLStatement.executeQuery(Query);
	SQLResultMetaData = SQLResult.getMetaData();
	SQLResult.next();
	String lastReview= com.picsauditing.PICS.DateBean.toShowFormat(SQLResult.getString("lastReview"));
%>
<html>
<head>
  <title>NCMS</title>
  <link rel="stylesheet" type="text/css" media="screen"
	href="css/forms.css" />
</head>
<body>
            <h1><%=name%><span class="sub">NCMS Data</span></h1>
            <table width="657" border="0" cellpadding="0" cellspacing="0" align="center">
              <tr>
                <td height="70" align="center" class="forms"><br/><br/> 
                <form name="form1" method="post" action="report_ncmsIndividual.jsp">
				    <input type=hidden name=conID value=<%=conID%>>
				    <input type=hidden name=name value="<%=name%>">
				    <input type=hidden name=closedDate value=<%=lastReview%>>
					<input name="action" type="submit" class="forms" value="Approve">
					<input name="action" type="submit" class="forms" value="Remove">
                  </form>
			  </td>
              </tr>
			  <tr><td>&nbsp;</td></tr>
			  <tr>
			    <td>&nbsp;</td>
		      </tr>
            </table>
            <table class="forms">
<% 	for (int i=1; i<=SQLResultMetaData.getColumnCount(); i++) {%>
              <tr>
              <th><%=SQLResultMetaData.getColumnName(i)%>:</th>
               <td>&nbsp;<%=SQLResult.getString(i)%></td>
              </tr>
<%	}//for%>
		    </table>
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