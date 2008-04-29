<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp"%>
<jsp:useBean id="dBean" class="com.picsauditing.PICS.DateBean" scope="page" />
<jsp:useBean id="calBean" class="com.picsauditing.PICS.CalendarBean" scope="page" />
<%
	permissions.tryPermission(OpPerms.OfficeAuditCalendar);

	String whichMonth = request.getParameter("whichMonth");
	String whichYear = request.getParameter("whichYear");
	int thisMonth = DateBean.getCurrentMonth();
	int thisYear = DateBean.getCurrentYear();
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
	calBean.setPermissions(permissions);
	calBean.setAccountID(permissions.getAccountId());
%>
<html>
<head>
<title>Audit Calendar</title>
<style>

table.month {
	border: 2px #000000 solid;
}
th.day {
	text-align: center;
	background-color: #003768;
	font-size: 16px;
	padding: 10px;
	font-weight: bold;
	color: #FFFFFF;
}
td.day {
	font-size: 11px;
	padding: 5px;
	border: 1px #777777 solid;
	width: 10%;
	height: 30px;
}
.daynum {
	color: #A84D10;
	font-size: 20px;
}

</style>
</head>
<body>
<h1><%=DateBean.getMonthName(auditMonth)%>
<span class="sub">Audit Calendar</span></h1>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<div id="internalnavcontainer">
<ul id="navlist">
<%
	String[] nextMonthsArray = dBean.getNextMonths(3);
	for (int x = 0; x < nextMonthsArray.length; x += 2) {
		int linkMonth = Integer.parseInt(nextMonthsArray[x]);
		%><li><a href="?whichMonth=<%= nextMonthsArray[x] + "&whichYear=" + nextMonthsArray[x + 1] %>"<%
		if (linkMonth == auditMonth) {
			%> class="current"<%
		}
		%>><%=DateBean.getMonthName(linkMonth) %></a></li><%
	}
%>
</ul>
</div>
<table class="month">
	<tr>
		<th class="day">Sunday</th>
		<th class="day">Monday</th>
		<th class="day">Tuesday</th>
		<th class="day">Wednesday</th>
		<th class="day">Thursday</th>
		<th class="day">Friday</th>
		<th class="day">Saturday</th>
	</tr>
	<%=calBean.writeCalendar(auditMonth, auditYear)%>
</table>
<%
	if (permissions.isPicsEmployee()) {
	%>
	<p align="center" class="blueMain">'*' = Web Audit</p>
	<%
	}
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
	type="submit" value="Block" name="submit" class="blueMain"></p>
</form>
<%
	}
%>
</body>
</html>