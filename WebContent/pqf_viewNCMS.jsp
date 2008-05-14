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
<title>NCMS Desktop Audit for <%=aBean.name %></title>
</head>
<body>
			<table border="0" cellspacing="0" cellpadding="1" class="blueMain">
              <tr align="center" class="blueMain">
			    <td width="676"><%@ include file="utilities/adminOperatorContractorNav.jsp"%></td>
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
</body>
</html>
