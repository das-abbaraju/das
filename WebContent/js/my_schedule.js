function loadAvail(){
	$calendar = $('#cal_avail').fullCalendar({
		
	});
}

function loadSched() {
	function saveEvent(calEvent, element, $cal) {
		var one_minute = 1000*60;
		$.post('MyScheduleAjax.action', 
			{ 
				button:'saveSchedule',
				'schedEvent.id': calEvent.id == null ? 0 : calEvent.id, 
				'schedEvent.weekDay': calEvent.start.getDay(),
				'schedEvent.startTime': calEvent.start.getHours() * 60 + calEvent.start.getMinutes(), 
				'schedEvent.duration': (calEvent.end.getTime() - calEvent.start.getTime()) / one_minute
			},
			function(response) {
				$.gritter.add({title: response.title, text:response.output});
				$calendar.weekCalendar("removeUnsavedEvents");
				if (response.schedEvent)
					$calendar.weekCalendar("updateEvent", fixEvent(response.schedEvent));
			},
			'json'
		);
	}

	function fixEvent(v) {
		var start = new Date();
		start.setDate(start.getDate() - (start.getDay() - v.weekDay));
		start.setHours(0);
		start.setMinutes(0);
		start.setSeconds(0);
		start.setMinutes(v.startTime);
		var end = new Date(start);
		end.setMinutes(start.getMinutes() + v.duration);
		return { id:v.id, start:start, end:end };
	}

	var $calendar = $('#cal_sched').weekCalendar({
		height: function(calendar){return 600;},
		businessHours: {start: 7, end: 17, limitDisplay: false},
		dateFormat: '',
		timeslotHeight: 30,
		timeslotsPerHour: 2,
		defaultEventLength: 4,
		buttons: false,
		newEventText: '',
		data: function(start, end, callback) {
			$.getJSON('MyScheduleJSON.action',
					{button:'jsonSchedule'},
					function(json) {
						events = new Array(json.events.length);
						$.each(json.events, function(k,v){
							events[k] = fixEvent(v);
						});
						callback(events);
					}
			);
		},
		resizable: function(calEvent, eventElement) {return false;},
		draggable: function(calEvent, eventElement) {return false;},
		eventNew: saveEvent,
		eventClick: function(calEvent, element) {
				if (confirm("Do you want to delete this timeslot?")){
					$.ajax({
						data: { button:'deleteSchedule',
								'schedEvent.id':calEvent.id},
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
	
	function clearForm() {
		$dialog.find(':input').val('');
	}

	$dialog = $('#vacation_form').dialog({
		title:'Vacation Entry', 
		autoOpen:false,
		modal: true,
		buttons: {
			Save: function() {
				var start = NaN;
				var end = NaN;

				if ($('#all-day').is(':checked')) {
					start = new Date($dialog.find('[name=startDate]').val());
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
								var event = $calendar.fullCalendar('clientEvents', json.calEvent.id)[0];
								event.start = json.calEvent.start;
								event.end = json.calEvent.end;
								$calendar.fullCalendar('updateEvent', event);
							}
							else
								$calendar.fullCalendar('renderEvent', json.calEvent);
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
		weekMode: 'liquid',
		header: { left:'title', center:'', right:'today,prev,next, ,month,basicWeek,basicDay' },
		eventClick: function(calEvent, jsEvent, view) { console.log(calEvent);
		console.log(view);
				var allDayCB = $dialog.find('#all-day:unchecked');
				if (calEvent.allDay && !allDayCB.is(':checked')) 
					allDayCB.click();
				else if (!calEvent.allDay && allDayCB.is(':checked'))
					allDayCB.click();

				clearForm();
				$dialog.find('[name=id]').val(calEvent.id);
				$dialog.find('[name=title]').val(calEvent.title);
				$dialog.find('[name=startDate]').val($.fullCalendar.formatDate(calEvent.start,'MM/dd/yyyy'));
				$dialog.find('[name=startTime]').val($.fullCalendar.formatDate(calEvent.start, 'hh:mm TT'));
				if (!calEvent.allDay) {
					$dialog.find('[name=endDate]').val($.fullCalendar.formatDate(calEvent.end,'MM/dd/yyyy'));
					$dialog.find('[name=endTime]').val($.fullCalendar.formatDate(calEvent.end, 'hh:mm TT'));
				}
				$dialog.dialog('open');
			},
		dayClick: function(dayDate) {
				clearForm();
				$dialog.find('[name=id]').val(0);
				$dialog.find('[name=startDate]').val($.fullCalendar.formatDate(dayDate,'MM/dd/yyyy'));
				$dialog.dialog('open');
			},
		events: 
			function (start, end, callback) {
				$.getJSON('MyScheduleJSON.action', {button: 'jsonVacation', start: start.getTime(), end: end.getTime()}, 
					function(json) { 
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
		businessHours: {start: 7, end: 17, limitDisplay: false},
		timeslotHeight: 40,
		timeslotsPerHour: 1,
		defaultEventLength: 4,
		readonly: true,
		data: 'MyScheduleJSON.action?button=jsonVacation'
	});

	$calendar.find('.day-column.day-1, .day-column.day-7').css({'background-color':'#dedede'});
}

$(function(){
	var tabMap = {
		preview:   {loaded: false, load: loadAvail},
		aschedule: {loaded: false, load: loadSched},
		vacation:  {loaded: false, load: loadVacat},
		holidays:  {loaded: false, load: loadHolid}
	};

	$('#schedule_tabs').bind('tabsshow', function(event, ui) {
	    if (!tabMap[ui.panel.id].loaded) {
	    	tabMap[ui.panel.id].load();
	    	tabMap[ui.panel.id].loaded = true;
	    }
	});

	$("#schedule_tabs").tabs();
	$("#schedule_tabs").tabs('select', 2);
});