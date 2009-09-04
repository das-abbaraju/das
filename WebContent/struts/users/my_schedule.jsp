<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>My Schedule</title>
<link rel="stylesheet" href="js/jquery/weekcalendar/jquery.weekcalendar.css">
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js"></script>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.7.2/jquery-ui.min.js"></script>
<script type="text/javascript" src="js/jquery/weekcalendar/jquery.weekcalendar.js.min.js"></script>
<script type="text/javascript">
$(function(){
	$cal = $('#calendar').weekCalendar({
			firstDayOfWeek: 1,
			timeslotHeight: 40,
			timeslotsPerHour: 2,
			defaultEventLength: 4,
			buttons: false,
			newEventText: 'Timeslot',
			data: 'MyScheduleJSON.action'
		});
});
</script>
</head>
<body>
<div id="calendar_wrapper">
<div id="calendar"></div>
</div>
<s:include value="../actionMessages.jsp"></s:include>
</body>
</html>
