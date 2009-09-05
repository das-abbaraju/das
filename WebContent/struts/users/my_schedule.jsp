<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>My Schedule</title>
<s:include value="../jquery.jsp" />

<link rel="stylesheet" href="js/jquery/weekcalendar/jquery.weekcalendar.css">
<script type="text/javascript" src="js/jquery/weekcalendar/jquery.weekcalendar.js.min.js"></script>

<script type="text/javascript">
function saveEvent(calEvent, element) {
	$.ajax({
		data: { button:'save',
				'calEvent.id':calEvent.id, 
				'calEvent.start':calEvent.start.getTime(), 
				'calEvent.end': calEvent.end.getTime()
			},
		url: 'MyScheduleAjax.action',
		success: function(text) {
				$.gritter.add({title: 'Calendar Event', text:text})
			}
	});
}

$(function(){
	$calendar = $('#cal_sched').weekCalendar({
		height: function(calendar){return 600;},
		businessHours: {start: 6, end: 20, limitDisplay: true},
		dateFormat: '',
		timeslotHeight: 40,
		timeslotsPerHour: 2,
		defaultEventLength: 4,
		buttons: false,
		newEventText: '',
		data: 'MyScheduleJSON.action',
		eventResize: saveEvent,
		eventDrop: saveEvent,
		eventNew: function(calEvent, element) {
				calEvent.id = 0;
				saveEvent(calEvent, element);
			}
	});

//	$calendar.find('.day-6, .day-7').remove();
//	for (var i=1; i<=5; i++)
//		$calendar.find('.week-calendar-time-slots .day-'+i).css({width: 'auto'});
//	$calendar.find('.week-calendar-time-slots td[colspan=7]').attr('colspan', 5);
//	$('.day-column-header br').remove();

	$calendar.find('.today').removeClass('today');
	$calendar.find('.day-column.day-1, .day-column.day-7').css({'background-color':'#dedede'});
});

$(function(){
	$calendar = $('#cal_avail').weekCalendar({
		height: function(calendar){return 600;},
		businessHours: {start: 6, end: 20, limitDisplay: true},
		timeslotHeight: 40,
		timeslotsPerHour: 2,
		defaultEventLength: 4,
		buttons: false,
		newEventText: '',
		data: 'MyScheduleJSON.action',
		eventResize: saveEvent,
		eventDrop: saveEvent,
		eventNew: function(calEvent, element) {
				calEvent.id = 0;
				saveEvent(calEvent, element);
			}
	});

	$calendar.find('.day-column.day-1, .day-column.day-7').css({'background-color':'#dedede'});
});

$(document).ready(function(){
	$("#schedule_tabs").tabs();

	$.gritter.add({
		title: 'Welcome to PICS',
		text: 'This is a new notification tool that Trevor, Kyle, and Arwen thought up while Keerthi was on vacation.'
	});
	
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
<div id="cal_sched"></div>
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
<div id="preview">
<div id="calendar_wrapper">
<div id="cal_avail"></div>
</div>
</div>
</div>

</body>
</html>
