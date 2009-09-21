<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title>My Schedule</title>
<s:include value="../jquery.jsp" />

<link rel="stylesheet" href="js/jquery/weekcalendar/jquery.weekcalendar.css">
<script type="text/javascript" src="js/jquery/weekcalendar/jquery.weekcalendar.js.min.js"></script>

<link rel="stylesheet" href="js/jquery/fullcalendar/fullcalendar.css">
<script type="text/javascript" src="js/jquery/fullcalendar/fullcalendar.min.js"></script>

<script type="text/javascript" src="js/my_schedule.js"></script>
<style>
#calendar_wrapper .monthly-company {
	color: #f00 !important;
}
table.modal_form {
	font-size: 14px;
}
table.modal_form td {
	padding: 4px;
	margin: 4px;
}
table.modal_form .title {
	font-weight: bold;
	text-align: right;
}
</style>
</head>
<body>
<h1>My Schedule</h1>
<s:include value="../actionMessages.jsp"></s:include>

<div id="message"></div>

<div id="schedule_tabs">
<ul>
	<li><a href="#preview"><span>Availability Preview</span></a></li>
	<li><a href="#aschedule"><span>Audit Schedule</span></a></li>
	<li><a href="#vacation"><span>Vacation</span></a></li>
	<li><a href="#holidays"><span>Holidays</span></a></li>
</ul>
<div id="preview">
<div id="calendar_wrapper">
<div id="cal_avail"></div>
</div>
</div>
<div id="aschedule">
<div id="calendar_wrapper">
<div id="cal_sched"></div>
</div>
</div>
<div id="vacation">
<div id="calendar_wrapper">
<div id="cal_vacat"></div>
</div>
</div>
<div id="holidays">
<div id="calendar_wrapper">
<div id="cal_holid"></div>
</div>
</div>
</div>
<div id="vacation_form" style="display:none">
<s:set name="vacationTimes" value="#{'12:00 AM':'', '12:00 PM':'12:00 PM', '02:00 PM':'02:00 PM', '04:00 PM':'04:00 PM', '06:00 PM':'06:00 PM'}"/>
<s:form>
	<fieldset>
		<s:hidden name="id"/>
		<table class="modal_form">
		<tr>
			<td class="title" rowspan="2">Title</td>
			<td><s:textfield name="title"/></td>
		</tr>
		<tr>
			<td><input type="checkbox" id="all-day" onclick="if(this.checked) $('.not-all-day').hide(); else $('.not-all-day').show();"/> All Day</td>
		</tr>
		<tr>
			<td class="title" rowspan="3">When</td>
			<td><s:textfield name="startDate" cssClass="datepicker" size="10"/> <s:select list="#vacationTimes" name="startTime" cssClass="not-all-day"/></td>
		</tr>
		<tr>
			<td class="title not-all-day" style="text-align:center">to</td>
		</tr>
		<tr>
			<td><s:textfield name="endDate" cssClass="datepicker not-all-day" size="10"/> <s:select list="#vacationTimes" name="endTime" cssClass="not-all-day"/></td>
		</tr>
		</table>
	</fieldset>
</s:form>
</div>
</body>
</html>
