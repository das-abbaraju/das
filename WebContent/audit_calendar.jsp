<%//@ page language="java" errorPage="exception_handler.jsp"%>
<%@ page language="java"%>
<%@ include file="utilities/adminGeneral_secure.jsp" %>
<jsp:useBean id="dBean" class="com.picsauditing.PICS.DateBean" scope ="page"/>
<jsp:useBean id="calBean" class="com.picsauditing.PICS.CalendarBean" scope ="page"/>
<%
	String format = request.getParameter("format");
	String whichMonth = request.getParameter("whichMonth");
	String whichYear = request.getParameter("whichYear");
	if (!(pBean.isAdmin() || pBean.isAuditor() || pBean.isOperator() || pBean.isCorporate())){
		response.sendRedirect("/logout.jsp");
		return;
	}//if
	if (null==format)
		format = "";
	int thisMonth = dBean.getCurrentMonth();
	int thisYear = dBean.getCurrentYear();
	if (null==whichMonth)
		whichMonth = Integer.toString(thisMonth);
	if (null==whichYear)
		whichYear = Integer.toString(thisYear);
	int auditMonth = Integer.parseInt(whichMonth);
	int auditYear = Integer.parseInt(whichYear);
	if (!(null==request.getParameter("blockedDate"))) 
		calBean.writeBlockedDatetoDB(request.getParameter("blockedDate"), request.getParameter("description"), request.getParameter("startHour"),request.getParameter("startAmPm"),request.getParameter("endHour"),request.getParameter("endAmPm"));
	if (!(null==request.getParameter("unblock")))
		calBean.deleteBlockedDate(request.getParameter("unblock"));
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
<%	if (!format.equals("popup")){%>
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
<%	}//if%>
	  <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
        <tr> 
          <td>&nbsp;</td>
	      <td>
            <table width="657" border="0" cellpadding="0" cellspacing="0" align="center">
              <tr> 
                <td height="70" colspan="2" align="center" class="buttons"> 
<%	if (!format.equals("popup")){%>
				<%@ include file="includes/selectReport.jsp"%>
<%		if (pBean.isAdmin()){%>
				<%@ include file="includes/nav/editAuditNav.jsp"%>
<%		}//if
 	}//if
%>
                <span class="blueHeader">Office Audit Calendar</span> 
                </td>
              </tr>
			  <tr>
                <td colspan="2">&nbsp;</td>
			  </tr>
              <tr> 
                <td height="30" colspan="2" class="blueMain" align="center">
			   <span class="blueHeader"><%=dBean.getMonthName(auditMonth)%></span><br>
<%	String[] nextMonthsArray = dBean.getNextMonths(3);
	for (int x = 0; x < nextMonthsArray.length; x+=2) {
		int linkMonth = Integer.parseInt(nextMonthsArray[x]);		
		if (linkMonth==auditMonth)
			out.println(dBean.getMonthName(linkMonth));
		else
			out.println("<a href='?whichMonth="+nextMonthsArray[x]+"&whichYear="+nextMonthsArray[x+1]+"&format="+format+"'>"+dBean.getMonthName(linkMonth)+"</a>");
		if (x < 3)
			out.println(" | ");
	}//for
%>	
	  		    </td>
              </tr>
            </table>
            <table border="1" cellpadding="1" cellspacing="1" align="center">
              <tr bgcolor="#003366" class="header">
                <td width="90" align="center" >Sunday</td>
                <td width="90"  align="center">Monday</td>
                <td width="90"  align="center">Tuesday</td>
                <td width="90" align="center">Wednesday</td>
                <td width="90"  align="center">Thursday</td>
			    <td width="90" align="center" >Friday</td>
			    <td width="90"  align="center">Saturday</td>
              </tr>
              <%=calBean.writeCalendar(auditMonth,auditYear,pBean)%>
            </table>
		  </td>
          <td>&nbsp;</td>
        </tr>
	    <tr>
          <td colspan="3">&nbsp;</td>
        </tr>
<%	if (pBean.isAdmin()){%>
	    <tr>
	      <td>&nbsp;</td>
          <td align="center" class="blueMain">
            <form name="form1" method="post" action="?whichMonth=<%=auditMonth%>&whichYear=<%=auditYear%>">
              Block Out Date (mm/dd/yyyy): <input type="text" name="blockedDate" size="10"> 
              Start Time: <nobr><%=com.picsauditing.PICS.Inputs.getHourSelect("startHour","forms","")%>
              <%=com.picsauditing.PICS.Inputs.getHourSelect("startAmPm","forms","")%></nobr>
              End Time: <nobr><%=com.picsauditing.PICS.Inputs.getHourSelect("endHour","forms","")%>
              <%=com.picsauditing.PICS.Inputs.getHourSelect("endAmPm","forms","")%></nobr>
              Description: <input type="text" name="description" size="12">
              <input type="submit" value="Block" name="submit">
            </form>
		  </td>
          <td>&nbsp;</td>
	    </tr>
<%	}//if
	if (pBean.isAdmin() || pBean.isAuditor()){
%>
	    <tr>
	      <td>&nbsp;</td>
	      <td align="center" class="blueMain">'*' = Web Audit</td>
		</tr>
<%	}//if%>
      </table>
    </td>
  </tr>
<%	if (!format.equals("popup")){%>
  <tr>
    <td height="72" align="center" bgcolor="#003366" class="copyrightInfo">&copy;2007 
      Pacific Industrial Contractor Screening | site design: <a href="http://www.albumcreative.com" title="Album Creative Studios"><font color="#336699">ACS</font></a></td>
  </tr>
<%	}//if%>
</table>
</body>
</html>