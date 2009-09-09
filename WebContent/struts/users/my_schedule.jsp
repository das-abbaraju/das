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
function saveEvent(calEvent, element, $cal) {
	$.ajax({
		data: { button:'save',
				'calEvent.id':calEvent.id, 
				'calEvent.start':calEvent.start.getTime(), 
				'calEvent.end': calEvent.end.getTime()
			},
		url: 'MyScheduleAjax.action',
		success: function(text) {
				$.gritter.add({title: 'Calendar Event Saved', text:text})
				if ($cal) {
					$cal.weekCalendar('refresh');
					styleCal($cal);
				}
			}
	});
}

function styleCal(cal) {
	$(cal).find('.today').removeClass('today');
	$(cal).find('.day-column.day-1, .day-column.day-7').css({'background-color':'#dedede'});
}

$(function(){
	$calendar = $('#cal_sched').weekCalendar({
		height: function(calendar){return 600;},
		businessHours: {start: 7, end: 17, limitDisplay: true},
		dateFormat: '',
		timeslotHeight: 30,
		timeslotsPerHour: 2,
		defaultEventLength: 4,
		buttons: false,
		newEventText: '',
		data: 'MyScheduleJSON.action?button=jsonSchedule',
		eventResize: saveEvent,
		eventDrop: saveEvent,
		eventNew: function(calEvent, element) {
				calEvent.id = 0;
				saveEvent(calEvent, element, $('#cal_sched'));
			},
		eventClick: function(calEvent, element) {
				if (confirm("Do you want to delete this timeslot?")){
					$.ajax({
						data: { button:'deleteSchedule',
								'calEvent.id':calEvent.id
							},
						url: 'MyScheduleAjax.action',
						success: function(text) {
								$.gritter.add({title: 'Calendar Event Removed', text:text});
								$('#cal_sched').weekCalendar('removeEvent',calEvent.id);
							}
					});
				}
			}
	});

	styleCal($calendar);
});

$(function(){
	$calendar = $('#cal_vacat').weekCalendar({
		height: function(calendar){return 600;},
		businessHours: {start: 7, end: 17, limitDisplay: true},
		timeslotHeight: 40,
		timeslotsPerHour: 1,
		defaultEventLength: 4,
		readonly: true,
		data: 'MyScheduleJSON.action?button=jsonVacation'
	});

	$calendar.find('.day-column.day-1, .day-column.day-7').css({'background-color':'#dedede'});
});

$(function(){
	$calendar = $('#cal_holid').weekCalendar({
		height: function(calendar){return 600;},
		businessHours: {start: 7, end: 17, limitDisplay: true},
		timeslotHeight: 40,
		timeslotsPerHour: 1,
		defaultEventLength: 4,
		readonly: true,
		data: 'MyScheduleJSON.action?button=jsonAvailability'
	});

	$calendar.find('.day-column.day-1, .day-column.day-7').css({'background-color':'#dedede'});
});

$(function(){
	$calendar = $('#cal_avail').weekCalendar({
		height: function(calendar){return 600;},
		businessHours: {start: 7, end: 17, limitDisplay: true},
		timeslotHeight: 30,
		timeslotsPerHour: 2,
		defaultEventLength: 4,
		readonly: true,
		data: 'MyScheduleJSON.action?button=jsonAvailability'
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
<div id="preview">
<div id="calendar_wrapper">
<div id="cal_avail"></div>
</div>
</div>
</div>

</body>
</html>
