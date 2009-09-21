$(function (){
	function fixEvent(k, event) {
		event.start = new Date(event.start);
		if (event.end)
			event.end = new Date(event.end);
	}
	
	function clearForm() {
		$dialog.find(':input').val('');
	}
	
	function getType(calEvent) {
		return calEvent.id.split('_')[0];
	}

	function getId(calEvent) {
		return calEvent.id.split('_')[1];
	}

	var sources = [
			function(start, end, callback) {
				$.getJSON('MyScheduleJSON.action', {button:'json', type: 'Vacation', start: start.getTime(), end: end.getTime()}, 
					function(json) { 
						$.each(json.events, fixEvent);
						callback(json.events); 
					} 
				);
			},
			function(start, end, callback) {
				$.getJSON('MyScheduleJSON.action', {button:'json', type: 'Holiday', start: start.getTime(), end: end.getTime()}, 
						function(json) { 
							$.each(json.events, fixEvent);
							callback(json.events); 
						} 
					);
			},
			function(start, end, callback) {
				$.getJSON('MyScheduleJSON.action', {button:'json', type: 'Schedule', start: start.getTime(), end: end.getTime()}, 
						function(json) { 
							$.each(json.events, fixEvent);
							callback(json.events); 
						} 
					);				
			},
			function(start, end, callback) {
				$.getJSON('MyScheduleJSON.action', {button:'json', type: 'Availability', start: start.getTime(), end: end.getTime()}, 
						function(json) { 
							$.each(json.events, fixEvent);
							callback(json.events); 
						} 
					);				
			}
	
	];


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
					'type': $dialog.find('[name=type]').val(),
					'button': 'save'
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
							$calendar.fullCalendar('removeEvents', json.calEvent.id);
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
		header: { left:'title', center:'', right:'today, ,prev,next, ,month,basicWeek,basicDay' },
		eventClick: function(calEvent, jsEvent, view) {
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
		dayClick: function(dayDate) {
				clearForm();
				$dialog.find('[name=id]').val(0);
				$dialog.find('[name=startDate]').val($.fullCalendar.formatDate(dayDate,'MM/dd/yyyy'));
				$dialog.dialog('open');
			},
		eventSources: sources
	});
}
);