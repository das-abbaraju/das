<%@ page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<%@ include file="utilities/adminGeneral_secure.jsp" %>
<jsp:useBean id="sBean" class="com.picsauditing.PICS.SearchBean" scope ="page"/>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope ="page"/>
<%	try{
	String action = request.getParameter("action");
	sBean.orderBy = request.getParameter("orderBy");
	if (null==sBean.orderBy)
		sBean.orderBy = "lastLogin DESC";
	if (null!=request.getParameter("startsWith"))
		sBean.orderBy = "name";
	if ("Send Emails".equals(action)) {
		java.util.Enumeration e = request.getParameterNames();
		while (e.hasMoreElements()) {
			String temp = (String)e.nextElement();
			if (temp.startsWith("sendEmail_")) {
				String cID = temp.substring(10);
				EmailBean.sendAnnualUpdateEmail(cID, permissions.getUsername());
			}//if
		}//while
	}//if

	sBean.setNoInsuranceOnly();
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
  <SCRIPT LANGUAGE="JavaScript" SRC="js/CalendarPopup.js"></SCRIPT>
  <SCRIPT LANGUAGE="JavaScript">document.write(getCalendarStyles());</SCRIPT>
  <SCRIPT LANGUAGE="JavaScript" ID="js1">var cal1 = new CalendarPopup();</SCRIPT>
  <script language="javascript" SRC="js/checkAllBoxes.js"></script>
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
          <td width="147"><img src="images/squares_rightUpperNav.gif" width="147" height="72" border="0"></td>
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
                <td height="70" colspan="2" align="center" class="buttons"> 
                  <%@ include file="includes/selectReport.jsp"%>
                  <span class="blueHeader">Annual Update Report</span></td>
              </tr>
			  <tr><td colspan="2">&nbsp;</td></tr>
              <tr> 
                <td height="30" align="left"><%=sBean.getStartsWithLinks()%></td>
                <td align="right"><%=sBean.getLinks()%></td>
              </tr>
            </table>
		  	<form name="form10" id="form10" method="post" action="report_annualUpdate.jsp">
            <table width="657" border="0" cellpadding="1" cellspacing="1" align="center">
              <tr bgcolor="#003366" class="whiteTitle">
			    <td colspan="2" align="center">Email<nobr><input name="checkAllBox" id="checkAllBox" type="checkbox" onclick="checkAll(document.form10)"></nobr></td>
                <td align="center"><a href="?orderBy=lastAnnualUpdateEmailDate DESC" class="whiteTitle">Sent</a></td>
                <td align="center"><a href="?orderBy=annualUpdateEmails" class="whiteTitle">Times</a></td>
                <td width="150"><a href="?orderBy=name" class="whiteTitle">Contractor</a></td>
                <td align="center"><a href="?orderBy=dateCreated DESC" class="whiteTitle">Created</a></td>
                <td align="center"><a href="?orderBy=lastLogin DESC" class="whiteTitle">Last Login</a></td>
                <td align="center"><a href="?orderBy=pqfSubmittedDate DESC" class="whiteTitle">PQF</a></td>
                <td align="center" bgcolor="#6699CC"><a href="?orderBy=auditDate DESC" class="whiteTitle">Audit</a></td>
              </tr>
<%	while (sBean.isNextRecord()) { %>
			  <tr <%=sBean.getBGColor()%> class="blueMain">
				<td><%=sBean.count-1%></td>
			    <td align="center">
                  <input name="sendEmail_<%=sBean.aBean.id%>" id="sendEmail_<%=sBean.aBean.id%>" type="checkbox">
			    </td>
                <td align="center"><%=sBean.cBean.lastAnnualUpdateEmailDate%></td>
                <td align="center"><%=sBean.cBean.annualUpdateEmails%></td>
                <td><%=sBean.getActiveStar()%>
                  <a href="contractor_detail.jsp?id=<%=sBean.aBean.id%>" title="view <%=sBean.aBean.name%> details" class="<%=sBean.getTextColor()%>"><%=sBean.aBean.name%></a></td>
                <td align="center"><%=sBean.aBean.dateCreated%></td>
                <td align="center"><%=sBean.aBean.lastLogin%></td>
                <td align="center"><%=sBean.cBean.pqfSubmittedDate%></td>
                <td align="center"><%=sBean.cBean.auditDate%></td>
		  	  </tr>
<%	}//while %>
		    </table><br>
            <center><input name="action" type="submit" class="buttons" value="Send Emails" onClick="return confirm('Are you sure you want to send these emails?');"> </center>
	        </form>
		    <br><center><%=sBean.getLinks()%></center>
<%	sBean.closeSearch(); %>
		  </td>
          <td>&nbsp;</td>
        </tr>
      </table>
      <br><center><%@ include file="utilities/contractor_key.jsp"%><br><br><br></center>
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