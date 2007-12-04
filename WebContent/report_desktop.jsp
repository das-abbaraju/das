<%@ page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%//@ page language="java"%>
<%@ include file="utilities/adminGeneral_secure.jsp" %>

<jsp:useBean id="sBean" class="com.picsauditing.PICS.SearchBean" scope ="session"/>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope ="page"/>
<jsp:useBean id="AUDITORS" class="com.picsauditing.PICS.Auditors" scope ="application"/>

<%	try{
	String assignAuditorID = request.getParameter("assignAuditorID");
	String action = request.getParameter("action");
	boolean doAssignments = "Assign Checked Audits".equals(action) && !assignAuditorID.equals(sBean.DEFAULT_AUDITOR_ID) && pBean.isAdmin();
	if(doAssignments){
		java.util.Enumeration e = request.getParameterNames();
		while(e.hasMoreElements()){
			String temp = (String)e.nextElement();
			if(temp.startsWith("assignDesktop_")){
				String cID = temp.substring(14);
				sBean.cBean.setFromDB(cID);
				sBean.cBean.desktopAuditor_id = assignAuditorID;
				sBean.cBean.desktopAssignedDate = DateBean.getTodaysDate();
				sBean.cBean.writeToDB();
			}//else if
		}//while
	}//if

	sBean.orderBy = request.getParameter("orderBy");
	if (null==sBean.orderBy)
		sBean.orderBy = "desktopSubmittedDate DESC";
	if ("auditDate".equals(sBean.orderBy))
		sBean.orderBy = "status<>'Inactive' DESC, DAYOFYEAR(auditDate)=0 DESC,DAYOFYEAR(auditDate),name";
	if ("paymentExpires".equals(sBean.orderBy))
		sBean.orderBy = "DAYOFYEAR(paymentExpires)=0 DESC,paymentExpires,name";
	
	sBean.setIsDesktopReport();
 	sBean.doSearch(request, sBean.ONLY_ACTIVE, 100, pBean, pBean.userID);
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
		  <td colspan="3" align="center" class="blueMain">
            <table width="657" border="0" cellpadding="0" cellspacing="0" align="center">
              <tr>
                <td height="70" colspan="2" align="center" class="buttons"> 
                  <%@ include file="includes/selectReport.jsp"%>
                  <span class="blueHeader">Schedule Desktop Audit Report</span>
                </td>
              </tr>
			</table>
            <%@ include file="includes/reportsSearch.jsp"%>
            <table width="657" border="0" cellpadding="0" cellspacing="0" align="center">
              <tr> 
                <td height="30" align="left"><%=sBean.getStartsWithLinks()%></td>
                <td align="right"><%=sBean.getLinks()%></td>
              </tr>
            </table>
            <form name="form2" method="post" action="report_desktop.jsp">
              Assign Checked Desktop Audits to: <%=AUDITORS.getAuditorsSelect("assignAuditorID","forms","")%>
              <input name="action" type="submit" class="buttons" value="Assign Checked Audits" onClick="return confirm('Are you sure you want to assign these desktop audits?');"><br><br>
			<table width="657" border="0" cellpadding="1" cellspacing="1" align="center">
              <tr bgcolor="#003366" class="whiteTitle"> 
			    <td colspan=2><a href="?orderBy=name" class="whiteTitle">Contractor</a></td>
 			    <td align="center"><a href="?orderBy=lastPayment" class="whiteTitle">Paid</a></td>
 			    <td align="center"><a href="?orderBy=pqfSubmittedDate DESC" class="whiteTitle">PQF</a></td>
 			    <td align="center"><a href="?orderBy=desktopSubmittedDate DESC" class="whiteTitle">Submitted</a></td>
 			    <td align="center"><a href="?orderBy=desktopClosedDate DESC" class="whiteTitle">Closed</a></td>
 			    <td align="center"><a href="?orderBy=manRevisionQ.answer DESC" class="whiteTitle">Revised</a></td>
 			    <td align="center"><a href="?orderBy=desktopAuditor_id DESC,name" class="whiteTitle">Desktop Auditor</a></td>
 			    <td align="center"><a href="?orderBy=desktopAssignedDate DESC" class="whiteTitle">Assigned</a></td>
 			    <td align="center">Assign</td>
  			  </tr>
<%	while (sBean.isNextRecord()) { %>
			  <tr <%=sBean.getBGColor()%> class="blueMain">
                <td align="right"><%=sBean.count-1%></td>
			    <td><a href="accounts_edit_contractor.jsp?id=<%=sBean.aBean.id%>" title="view <%=sBean.aBean.name%> details" class="<%=sBean.getTextColor()%>">
			    <%=sBean.getActiveStar()%><%=sBean.aBean.name%></a></td>
			    <td><%=sBean.cBean.lastPayment%></td>
			    <td><%=sBean.cBean.pqfSubmittedDate%></td>
			    <td><%=sBean.cBean.desktopSubmittedDate%></td>
			    <td><%=sBean.cBean.desktopClosedDate%></td>
			    <td><%=sBean.manualRevisionDate%></td>
			    <td align="center"><nobr><%=AUDITORS.getNameFromID(sBean.cBean.desktopAuditor_id)%></nobr></td>
			    <td><%=sBean.cBean.desktopAssignedDate%></td>
				<td><input type="checkbox" class="forms" name="assignDesktop_<%=sBean.aBean.id%>"></td>
		  	  </tr>
<%	} // while %>
		    </table>
	        </form>
		    <br><center><%=sBean.getLinks()%></center>
<%	sBean.closeSearch(); %>
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
		sBean.closeSearch();
	}//finally
%>