function loadPreview() {
	function fixEvent(k, event) {
		event.start = new Date(event.start);
		if (event.end)
			event.end = new Date(event.end);
	}
	
	function clearForm() {
		$dialog.find(':input').val('');
		$dialog.find('[name=type]').val('Vacation');
	}
	
	function getType(calEvent) {
		return calEvent.id.split('_')[0];
	}

	function getId(calEvent) {
		return calEvent.id.split('_')[1];
	}

	var sources = [
			function(start, end, callback) {
				$.getJSON('MyScheduleJSON.action', {button:'json', type: 'Holiday', start: start.getTime(), end: end.getTime()}, 
					function(json) { 
						$.each(json.events, fixEvent);
						callback(json.events); 
					} 
				);
			},
			function(start, end, callback) {
				$.getJSON('MyScheduleJSON.action', {button:'json', type: 'Vacation', start: start.getTime(), end: end.getTime(), currentUserID: $('#currentUserID').val()}, 
					function(json) { 
						$.each(json.events, fixEvent);
						callback(json.events); 
					} 
				);
			},
			function(start, end, callback) {
				$.getJSON('MyScheduleJSON.action', {button:'json', type: 'Audit', start: start.getTime(), end: end.getTime(), currentUserID: $('#currentUserID').val()}, 
						function(json) { 
							$.each(json.events, fixEvent);
							callback(json.events); 
						} 
					);				
			},
			function(start, end, callback) {
				$.getJSON('MyScheduleJSON.action', {button:'json', type: 'Availability', start: start.getTime(), end: end.getTime(), currentUserID: $('#currentUserID').val()}, 
						function(json) { 
							$.each(json.events, fixEvent);
							callback(json.events); 
						} 
					);				
			}	
	];


	$dialog = $('#vacation_form').dialog({
		title:'Calendar Entry', 
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
					'type': $dialog.find('[name=type]').val(),
					'button': 'save',
					'currentUserID': $('#currentUserID').val()
				};

				if (!isNaN(start))
					data['calEvent.start'] = start.getTime();
				if (!isNaN(end))
					data['calEvent.end'] = end.getTime();

				$.getJSON('MyScheduleJSON.action', data,
					function(json) {
						$.gritter.add({title:json.title, text:json.output});
						if (json.calEvent) {
							fixEvent(null, json.calEvent);
							if (json.update){
								var event = $calendar.fullCalendar('clientEvents', json.calEvent.id)[0];
								event.start = json.calEvent.start;
								event.end = json.calEvent.end;
								event.title = json.calEvent.title;
								event.className = json.calEvent.className;
								event.owner = json.calEvent.owner;
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
				var type = $dialog.find('[name=type]').val();
				$.getJSON('MyScheduleJSON.action',
					{button: 'delete',type: type, 'calEvent.id': calID, 'currentUserID': $('#currentUserID').val()},
					function(json) {
						$.gritter.add({title:json.title, text:json.output});
						if (json.calEvent) {
							if (json.deleted)
								$calendar.fullCalendar('removeEvents', json.calEvent.id);
						}
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
		header: {
			left: 'prev,next today',
			center: 'title',
			right: 'month,basicWeek,basicDay'
		},
		loading: function(isLoading, view) {
				if(isLoading)
					$.gritter.add({title:'Loading...', text: 'Fetching calendar events', time: 2000});
				else
					$.gritter.add({title:'Loading Finished', text: 'Finished fetching calendar events', time: 2000});
			},
		eventClick: function(calEvent, jsEvent, view) {
				if (getType(calEvent) == 'Availability' || getType(calEvent) == 'Audit')
					return;
				if (!hasHoliday && getType(calEvent) == 'Holiday')
					return;
				var allDayCB = $dialog.find('#all-day:unchecked');
				if (calEvent.allDay && !allDayCB.is(':checked')) 
					allDayCB.click();
				else if (!calEvent.allDay && allDayCB.is(':checked'))
					allDayCB.click();

				clearForm();
				$dialog.find('[name=id]').val(getId(calEvent));
				$dialog.find('[name=type]').val(getType(calEvent));
				$dialog.find('[name=title]').val(calEvent.title);
				$dialog.find('[name=startDate]').val($.fullCalendar.formatDate(calEvent.start,'MM/dd/yyyy'));
				$dialog.find('[name=startTime]').val($.fullCalendar.formatDate(calEvent.start, 'hh:mm TT'));
				if (!calEvent.allDay) {
					$dialog.find('[name=endDate]').val($.fullCalendar.formatDate(calEvent.end,'MM/dd/yyyy'));
					$dialog.find('[name=endTime]').val($.fullCalendar.formatDate(calEvent.end, 'hh:mm TT'));
				}
				$dialog.dialog('open');
			},
		eventRender: function (calEvent, element, view) {
				$(element)
					.attr({title: getType(calEvent)+' '+$.fullCalendar.formatDates(calEvent.start,calEvent.end,"'['[MM/dd ]h:mmt{'-'[MM/dd ]h:mmt}']'")})
					.tooltip({track: true, delay:0});
			},
		dayClick: function(dayDate, view) {
				clearForm();
				$dialog.find('[name=id]').val(0);
				$dialog.find('[name=startDate]').val($.fullCalendar.formatDate(dayDate,'MM/dd/yyyy'));
				$dialog.dialog('open');
			},
		eventSources: sources
	});
	
	$('#currentUserID').change(function(){
		$calendar.fullCalendar('refetchEvents');
	});
}

function loadSched() {
	function saveEvent(calEvent, element, $cal) {
		var one_minute = 1000*60;
		$.post('MyScheduleJSON.action', 
			{ 
				button:'saveSchedule',
				currentUserID: $('#currentUserID').val(),
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
	
	var date = new Date();
	date.setHours(7);

	var $calendar = $('#cal_sched').weekCalendar({
		date: date,
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
					{button:'json', type:'weekly', currentUserID: $('#currentUserID').val()},
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
					$.getJSON('MyScheduleJSON.action',
						{ button:'deleteSchedule',
							'schedEvent.id':calEvent.id,
							currentUserID: $('#currentUserID').val()},
						function(json) { console.log(json);
								$.gritter.add({title: json.title, text:json.output});
								if (json.deleted)
									$('#cal_sched').weekCalendar('removeEvent',calEvent.id);
							}
					);
				}
			}
	});

	$calendar.find('.today').removeClass('today');
	$calendar.find('.day-column.day-1, .day-column.day-7').css({'background-color':'#dedede'});
	
	$('#currentUserID').change(function(){
		$calendar.weekCalendar('refresh');
	});
}

$(function(){
	var tabMap = {
		preview:   {loaded: false, load: loadPreview, refresh: function(){$('#cal_vacat').fullCalendar('refetchEvents')}},
		aschedule: {loaded: false, load: loadSched, refresh: function(){$('#cal_sched').weekCalendar('refresh');}}
	};

	$('#schedule_tabs').bind('tabsshow', function(event, ui) {
	    if (!tabMap[ui.panel.id].loaded) {
	    	tabMap[ui.panel.id].load();
	    	tabMap[ui.panel.id].loaded = true;
	    } else
	    	tabMap[ui.panel.id].refresh();
	});

	$("#schedule_tabs").tabs();
});