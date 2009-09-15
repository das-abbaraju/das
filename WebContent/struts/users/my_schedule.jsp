<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>My Schedule</title>
<s:include value="../jquery.jsp" />

<link rel="stylesheet" href="js/jquery/weekcalendar/jquery.weekcalendar.css">
<script type="text/javascript" src="js/jquery/weekcalendar/jquery.weekcalendar.js.min.js"></script>

<link rel="stylesheet" href="js/jquery/fullcalendar/fullcalendar.css">
<script type="text/javascript" src="js/jquery/fullcalendar/fullcalendar.min.js"></script>

<script type="text/javascript">
function loadSched() {
	function saveEvent(calEvent, element, $cal) {
		console.log(calEvent.id);
		$.post('MyScheduleAjax.action', 
			{ 
				button:'save',
				'calEvent.id': calEvent.id == null ? 0 : calEvent.id, 
				'calEvent.start':calEvent.start.getTime(), 
				'calEvent.end': calEvent.end.getTime()
			},
			function(response) {
				$.gritter.add({title: 'Calendar Event', text:response.output});
				$calendar.weekCalendar("removeUnsavedEvents");
				$calendar.weekCalendar("updateEvent", response.calEvent);
			},
			'json'
		);
	}

	var $calendar = $('#cal_sched').weekCalendar({
		businessHours: {start: 7, end: 17, limitDisplay: true},
		dateFormat: '',
		timeslotHeight: 30,
		timeslotsPerHour: 2,
		defaultEventLength: 4,
		buttons: false,
		newEventText: '',
		data: 'MyScheduleJSON.action?button=jsonSchedule',
		resizable: function(calEvent, eventElement) {return false;},
		draggable: function(calEvent, eventElement) {return false;},
		eventResize: saveEvent,
		eventDrop: saveEvent,
		eventNew: saveEvent,
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

	$calendar.find('.today').removeClass('today');
	$calendar.find('.day-column.day-1, .day-column.day-7').css({'background-color':'#dedede'});
}

function loadVacat() {
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
}

function loadHolid(){
	function fixEvent(k, event) {
		event.start = new Date(event.start);
		var message = "start: " + event.start;
		if (event.end) {
			event.end = new Date(event.end);
			message += "\nend: " + event.end;
		}
		event.url = 'javascript:alert("'+message+'")';
	}

	$calendar = $('#cal_holid').fullCalendar({
		fixedWeeks: false,
		events: 
			function (start, end, callback) {
				$.getJSON('MyScheduleJSON.action', {button: 'jsonVacation', start: start.getTime(), end: end.getTime()}, 
					function(json) { 
						var events = new Array(json.events.length); 
						$.each(json.events, fixEvent);
						callback(json.events); 
					} 
				);
			}
	});
}

function loadAvail(){
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
}

$(function(){
	var tabMap = {
		aschedule: {loaded: false, load: loadSched},
		vacation: {loaded: false, load: loadVacat},
		holidays: {loaded: false, load: loadHolid},
		preview: {loaded: false, load: loadAvail}
	};

	$('#schedule_tabs').bind('tabsshow', function(event, ui) {
	    if (!tabMap[ui.panel.id].loaded) {
	    	tabMap[ui.panel.id].load();
	    	tabMap[ui.panel.id].loaded = true;
	    }
	});

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
