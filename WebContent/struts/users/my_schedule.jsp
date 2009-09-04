<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>My Schedule</title>

<link rel="stylesheet" href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.7/themes/smoothness/jquery-ui.css">
<link rel="stylesheet" href="js/jquery/weekcalendar/jquery.weekcalendar.css">
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js"></script>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.7.2/jquery-ui.min.js"></script>
<script type="text/javascript" src="js/jquery/weekcalendar/jquery.weekcalendar.js.min.js"></script>

<script type="text/javascript">
function saveEvent(calEvent, element) {
	$('#message').load('MyScheduleAjax.action', {
			button:'save',
			'calEvent.id':calEvent.id, 
			'calEvent.start':calEvent.start.getTime(), 
			'calEvent.end': calEvent.end.getTime()
		});
}

$(function(){
	$calendar = $('#calendar').weekCalendar({
			firstDayOfWeek: 1,
			timeslotHeight: 40,
			timeslotsPerHour: 2,
			defaultEventLength: 4,
			buttons: false,
			newEventText: 'Timeslot',
			data: 'MyScheduleJSON.action',
			eventResize: saveEvent,
			eventDrop: saveEvent,
			eventNew: function(calEvent, element) {
					calEvent.id = 0;
					saveEvent(calEvent, element);
				}
		});

});
$(document).ready(function(){
	$("#schedule_tabs").tabs();
});

</script>
</head>
<body>
<h1>My Schedule</h1>
<s:include value="../actionMessages.jsp"></s:include>

<div id="message"></div>

<div id="schedule_tabs">
<ul>
	<li><a href="#aschedule"><span>Audit Schedule</span></a></li>
	<li><a href="#vacation"><span>Vacation</span></a></li>
	<li><a href="#holidays"><span>Holidays</span></a></li>
	<li><a href="#preview"><span>Availability Preview</span></a></li>
</ul>
<div id="aschedule">
<div id="calendar_wrapper">
<div id="calendar"></div>
</div>
</div>
<div id="vacation">Lorem ipsum dolor sit amet, consectetuer
adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet
dolore magna aliquam erat volutpat. Lorem ipsum dolor sit amet,
consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut
laoreet dolore magna aliquam erat volutpat.</div>
<div id="holidays">Lorem ipsum dolor sit amet, consectetuer
adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet
dolore magna aliquam erat volutpat. Lorem ipsum dolor sit amet,
consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut
laoreet dolore magna aliquam erat volutpat. Lorem ipsum dolor sit amet,
consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut
laoreet dolore magna aliquam erat volutpat.</div>
</div>
<div id="preview">Lorem ipsum dolor sit amet, consectetuer
adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet
dolore magna aliquam erat volutpat. Lorem ipsum dolor sit amet,
consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut
laoreet dolore magna aliquam erat volutpat.</div>

</body>
</html>
