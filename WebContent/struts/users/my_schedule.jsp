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

function loadVacat(){
	function fixEvent(k, event) {
		event.start = new Date(event.start);
		if (event.end)
			event.end = new Date(event.end);
	}

	function isAllDay(event) {
	}

	$dialog = $('#vacation_form').dialog({
		title:'Vacation Entry', 
		autoOpen:false,
		modal: true,
		buttons: {
			Save: function() {
				var start;
				var end;

				if ($('#all-day').is(':checked')) {
					start = new Date($dialog.find('[name=startDate]').val());
					end = NaN;
				} else {
					start = new Date($dialog.find('[name=startDate]').val() + " " + $dialog.find('[name=startTime]').val());
					end = new Date($dialog.find('[name=endDate]').val() + " " + $dialog.find('[name=endTime]').val());
				}
				var data = {
					'calEvent.id': $dialog.find('[name=id]').val(),
					'calEvent.title':$dialog.find('[name=title]').val(),
					'button': 'saveVacation'
				};

				if (!isNaN(start))
					data['calEvent.start'] = start.getTime();
				if (!isNaN(end))
					data['calEvent.end'] = end.getTime();

				$.getJSON('MyScheduleJSON.action', data,
					function(json) {
						$.gritter.add({title: json.title, text: json.output});
						if (json.calEvent) {
							fixEvent(null, json.calEvent);
							if (json.update){
								var event = $calendar.fullCalendar('getEventsById', json.calEvent.id)[0];
								event.start = json.calEvent.start;
								event.end = json.calEvent.end;
								$calendar.fullCalendar('updateEvent', event);
							}
							else
								$calendar.fullCalendar('addEvent', json.calEvent);
						}
							
						$dialog.dialog('close');
					}
				);
			},
			Delete: function() {
				var calID = $dialog.find('[name=id]').val();
				$.getJSON('MyScheduleJSON.action',
					{button: 'deleteVacation', 'calEvent.id': calID},
					function(json) {
						$.gritter.add({title: json.title, text: json.output});
						if (json.deleted)
							$calendar.fullCalendar('removeEvent', json.calEvent);
						$dialog.dialog('close');
					}
				);
			},
			Cancel: function() { $dialog.dialog('close'); }
		},
		open: function() {
			$('.datepicker').datepicker();
		}
	});

	$calendar = $('#cal_vacat').fullCalendar({
		fixedWeeks: false,
		eventClick: function(calEvent, jsEvent) {
				$dialog.dialog('open');
				$dialog.find('[name=id]').val(calEvent.id);
				$dialog.find('[name=title]').val(calEvent.title);
				$dialog.find('[name=startDate]').val($.datepicker.formatDate('mm/dd/yy',calEvent.start));
				$dialog.find('[name=startTime]').val($.fullCalendar.formatDate(calEvent.start, 'h:i A'));
				$dialog.find('[name=endDate]').val($.datepicker.formatDate('mm/dd/yy',calEvent.end));
				$dialog.find('[name=endTime]').val($.fullCalendar.formatDate(calEvent.end, 'h:i A'));
			},
		dayClick: function(dayDate) {
				$dialog.dialog('open');
				$dialog.find('[name=id]').val(0);
				$dialog.find('[name=title]').val('');
				$dialog.find('[name=startDate]').val($.datepicker.formatDate('mm/dd/yy',dayDate));
				$dialog.find('[name=startTime]').val('');
				$dialog.find('[name=endDate]').val('');
				$dialog.find('[name=endTime]').val('');
			},
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

function loadHolid() {
	$calendar = $('#cal_holid').weekCalendar({
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
		vacation:  {loaded: false, load: loadVacat},
		holidays:  {loaded: false, load: loadHolid},
		preview:   {loaded: false, load: loadAvail}
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
<style>
.monthly-company {
	color: #f00;
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
<div id="vacation_form" style="display:none">
<s:set name="vacationTimes" value="#{'12:00 AM':'', '12:00 PM':'12:00 PM', '02:00 PM':'02:00 PM', '04:00 PM':'04:00 PM'}"/>
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
