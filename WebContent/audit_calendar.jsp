<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp"%>
<jsp:useBean id="dBean" class="com.picsauditing.PICS.DateBean" scope="page" />
<jsp:useBean id="calBean" class="com.picsauditing.PICS.CalendarBean" scope="page" />
<%
	permissions.tryPermission(OpPerms.OfficeAuditCalendar);

	//String format = request.getParameter("format");
	//if (!format.equals("popup")) {}
	String whichMonth = request.getParameter("whichMonth");
	String whichYear = request.getParameter("whichYear");
	int thisMonth = dBean.getCurrentMonth();
	int thisYear = dBean.getCurrentYear();
	if (null == whichMonth)
		whichMonth = Integer.toString(thisMonth);
	if (null == whichYear)
		whichYear = Integer.toString(thisYear);
	int auditMonth = Integer.parseInt(whichMonth);
	int auditYear = Integer.parseInt(whichYear);
	if (!(null == request.getParameter("blockedDate")))
		calBean.writeBlockedDatetoDB(request.getParameter("blockedDate"), request.getParameter("description"),
				request.getParameter("startHour"), request.getParameter("startAmPm"), request
						.getParameter("endHour"), request.getParameter("endAmPm"));
	if (!(null == request.getParameter("unblock")))
		calBean.deleteBlockedDate(request.getParameter("unblock"));
%>
<html>
<head>
<title>Audit Calendar</title>
</head>
<body>
<table width="657" border="0" cellpadding="0" cellspacing="0"
	align="center">
	<tr>
		<td class="blueHeader" align="center">Office Audit Calendar</td>
	</tr>
	<tr>
		<td colspan="2">&nbsp;</td>
	</tr>
	<tr>
		<td height="30" colspan="2" class="blueMain" align="center"><span
			class="blueHeader"><%=dBean.getMonthName(auditMonth)%></span><br>
		<%
			String[] nextMonthsArray = dBean.getNextMonths(3);
			for (int x = 0; x < nextMonthsArray.length; x += 2) {
				int linkMonth = Integer.parseInt(nextMonthsArray[x]);
				if (linkMonth == auditMonth)
					out.println(dBean.getMonthName(linkMonth));
				else
					out.println("<a href='?whichMonth=" + nextMonthsArray[x] + "&whichYear=" + nextMonthsArray[x + 1]
							+ "'>" + dBean.getMonthName(linkMonth) + "</a>");
				if (x < 3)
					out.println(" | ");
			}//for
		%>
		</td>
	</tr>
</table>
<table border="1" cellpadding="1" cellspacing="1" align="center">
	<tr bgcolor="#003366" class="header">
		<td width="90" align="center">Sunday</td>
		<td width="90" align="center">Monday</td>
		<td width="90" align="center">Tuesday</td>
		<td width="90" align="center">Wednesday</td>
		<td width="90" align="center">Thursday</td>
		<td width="90" align="center">Friday</td>
		<td width="90" align="center">Saturday</td>
	</tr>
	<%=calBean.writeCalendar(auditMonth, auditYear, pBean)%>
</table>
<%
	if (permissions.isAdmin()) {
%>
<form name="form1" method="post"
	action="?whichMonth=<%=auditMonth%>&whichYear=<%=auditYear%>">
<p align="center" class="blueMain">Block Out Date (mm/dd/yyyy): <input
	type="text" name="blockedDate" size="10"> Start Time: <nobr><%=com.picsauditing.PICS.Inputs.getHourSelect("startHour", "forms", "")%>
<%=com.picsauditing.PICS.Inputs.getHourSelect("startAmPm", "forms", "")%></nobr>
End Time: <nobr><%=com.picsauditing.PICS.Inputs.getHourSelect("endHour", "forms", "")%>
<%=com.picsauditing.PICS.Inputs.getHourSelect("endAmPm", "forms", "")%></nobr>
Description: <input type="text" name="description" size="12"> <input
	type="submit" value="Block" name="submit"></p>
</form>
<%
	}
	if (permissions.isPicsEmployee()) {
%>
<p align="center" class="blueMain">'*' = Web Audit</p>
<%
	}
%>
</body>
</html>