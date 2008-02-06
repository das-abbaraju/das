<%@ page language="java" import="com.picsauditing.PICS.*,java.sql.*" errorPage="exception_handler.jsp"%>
<%@ include file="utilities/adminGeneral_secure.jsp" %>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope ="page"/>
<jsp:useBean id="permissions" class="com.picsauditing.access.Permissions" scope="session" />

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
  <title>PICS - Pacific Industrial Contractor Screening</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <META Http-Equiv="Cache-Control" Content="no-cache">
  <META Http-Equiv="Pragma" Content="no-cache">
  <META Http-Equiv="Expires" Content="0">
  <link href="PICS.css" rel="stylesheet" type="text/css">
</head>
<body bgcolor="#EEEEEE" vlink="#003366" alink="#003366" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<table width="100%" height="100%" border="1" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top">
	  <table width="100%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td width="50%" bgcolor="#993300">&nbsp;</td>
          <td width="146" valign="top" rowspan="2"><a href="index.jsp"><img src="images/logo.gif" alt="HOME" width="146" height="145" border="0"></a></td>
          <td width="364"><%@ include file="utilities/mainNavigation.jsp"%></td>
          <td width="147"><%@ include file="utilities/rightUpperNav.jsp"%></td>
          <td width="50%" bgcolor="#993300">&nbsp;</td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td valign="top" align="center"><img src="images/header_reports.gif" width="321" height="72"></td>
          <td valign="top"><%@ include file="utilities/rightLowerNav.jsp"%></td>
          <td>&nbsp;</td>
        </tr>
      </table>
      <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
        <tr> 
          <td>&nbsp;</td>
		  <td colspan="3">
            <table width="657" border="0" cellpadding="0" cellspacing="0" align="center">
              <tr>
                <td height="70" align="center" class="forms"> 
                  <%@ include file="includes/selectReport.jsp"%>
			      <a class=forms href="report_ncms.jsp">&lt;&lt; Back to NCMS Reports</a><br>
			    <form name="form1" method="post" action="report_ncmsIndividual.jsp">
				    <input type=hidden name=conID value=<%=conID%>>
				    <input type=hidden name=name value="<%=name%>">
				    <input type=hidden name=closedDate value=<%=lastReview%>>
					<input name="action" type="submit" class="forms" value="Approve">
					<input name="action" type="submit" class="forms" value="Remove">
                  </form>
				  <span class="blueHeader">NCMS Data For <%=name%></span>                </td>
              </tr>
			  <tr><td>&nbsp;</td></tr>
			  <tr>
			    <td>&nbsp;</td>
		      </tr>
            </table>
            <table border=1 cellspacing=0 cellpadding=0 align=center>
<% 	for (int i=1; i<=SQLResultMetaData.getColumnCount(); i++) {%>
              <tr class=blueMain <%=com.picsauditing.PICS.Utilities.getBGColor(i)%>>
                <td align=right><%=SQLResultMetaData.getColumnName(i)%>:</td>
                <td>&nbsp;<%=SQLResult.getString(i)%></td>
              </tr>
<%	}//for%>
		    </table>
		  </td>
          <td>&nbsp;</td>
        </tr>
      </table>
      <br><center><%@ include file="utilities/contractor_key.jsp"%></center><br><br>
    </td>
  </tr>
  <tr>
    <td height="72" align="center" bgcolor="#003366" class="copyrightInfo">&copy;2007 
      Pacific Industrial Contractor Screening | site design: <a href="http://www.albumcreative.com" title="Album Creative Studios"><font color="#336699">ACS</font></a></td>
  </tr>
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