<%@page language="java" import="java.sql.*,com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope ="page"/>

<%	
Connection Conn = null;
Statement SQLStatement = null;
String Query = "";
ResultSet SQLResult = null;
ResultSetMetaData SQLResultMetaData = null;
	try{
	String[] dontShow = {"conID","ContractorsName","remove","fedTaxID","lastReview","approved"};

	String conID = request.getParameter("id");
	Conn = com.picsauditing.PICS.DBBean.getDBConnection();
	SQLStatement = Conn.createStatement();

	if ((null!=conID) && !"".equals(conID))
		Query = "SELECT * FROM NCMS_Desktop WHERE conID="+conID+";";

	SQLResult = SQLStatement.executeQuery(Query);
	SQLResultMetaData = SQLResult.getMetaData();

	String auditType = request.getParameter("auditType");
	String id = request.getParameter("id");
	aBean.setFromDB(conID);
	cBean.setFromDB(conID);
	cBean.tryView(permissions);
	
	boolean hasTableEntry = SQLResult.next();
	int count = 0;
%>
<html>
<head>
  <title>PICS - Pacific Industrial Contractor Screening</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <link href="PICS.css" rel="stylesheet" type="text/css">
</head>
<body bgcolor="#EEEEEE" background="images/watermark.gif" vlink="#003366" alink="#003366" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top">
	  <table width="100%" border="0" cellpadding="0" cellspacing="0">
        <tr> 
          <td width="50%" bgcolor="#993300">&nbsp;</td>
          <td width="146" valign="top" rowspan="2"><a href="index.jsp"><img src="images/logo.gif" alt="HOME" width="146" height="145" border="0"></a></td>
          <td width="364"><%@ include file="utilities/mainNavigation.jsp"%></td>
          <td width="147"><img src="images/squares_rightUpperNav.gif" width="147" height="72" border="0"></td>
          <td width="50%" bgcolor="#993300">&nbsp;</td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td valign="top" align="center">&nbsp;</td>
          <td valign="top"><%@ include file="utilities/rightLowerNav.jsp"%></td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td colspan="3" align="center">
			<table border="0" cellspacing="0" cellpadding="1" class="blueMain">
              <tr align="center" class="blueMain">
			    <td width="676"><%@ include file="includes/nav/secondNav.jsp"%></td>
			  </tr>
    		  <tr align="center" class="blueMain">
                <td class="blueHeader">NCMS Desktop Audit for <%=aBean.name%></td>
    		  </tr>
	  		  <tr align="center">
                <td class="blueMain">Date Audit Closed: <span class="redMain"><strong><%=cBean.desktopClosedDate%></strong></span>
                <%=cBean.getValidUntilDate(auditType)%>
                </td>
    		  </tr>
				   <tr>
				     <td>
					  <table width="657" border="0" cellpadding="1" cellspacing="1">
                        <tr class="whiteTitle"> 
                          <td bgcolor="#003366" width=1%>Num</td>
                          <td bgcolor="#003366">Category</td>
                          <td bgcolor="#993300">% Complete</td>
                        </tr>
<%	for (int i=1; i<=SQLResultMetaData.getColumnCount(); i++) {
		String cat = SQLResultMetaData.getColumnName(i);
		if (!Utilities.arrayContains(dontShow,cat)) {
			count++;
%>
                        <tr class="blueMain" <%=com.picsauditing.PICS.Utilities.getBGColor(count)%>> 
                          <td align=right><%=count%>.</td>
                          <td><%=cat%></td>
                          <td>
<%			if (hasTableEntry)
				out.print(SQLResult.getString(i));
%>
                          </td>
                        </tr>
<%		}//if
	}//for
%>
        		  </table>
					</td>
		          </tr> 
			</table>
		  </td>
          <td>&nbsp;</td>
        </tr>
      </table>
      <br>
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