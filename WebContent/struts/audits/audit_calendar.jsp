<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title>Audit Calendar</title>
<s:include value="../jquery.jsp" />

<link rel="stylesheet" href="js/jquery/fullcalendar/fullcalendar.css">
<script type="text/javascript" src="js/jquery/fullcalendar/fullcalendar.min.js"></script>
<script src="js/jquery/cluetip/jquery.cluetip.js" type="text/javascript"></script>
<link href="js/jquery/cluetip/jquery.cluetip.css" media="screen" type="text/css" rel="stylesheet">

<link rel="stylesheet" href="css/reports.css?v=20091105"/>

<script type="text/javascript">
$(function() {
	$('#calendar').fullCalendar({
			weekMode: 'liquid',
			firstHour: 5,
			header: {
				left: 'prev,next today',
				center: 'title',
				right: 'month,agendaWeek,agendaDay'
			},
			loading: function(isLoading, view) {
				if(isLoading)
					startThinking( {div: 'thinking', message: 'Fetching Calendar Events' } );
				else
					stopThinking ( {div: 'thinking' } );
			},
			eventRender: function (calEvent, element, view) {
				$(element).attr({rel: 'AuditQuickAjax.action?auditID='+calEvent.id}).cluetip({
						sticky: true, 
						clickThrough: true,
						positionBy: 'mouse',
						ajaxCache: true,
						closeText: "<img src='images/cross.png' width='16' height='16'>",
						arrows: true,
						dropShadow: false,
						cluetipClass: 'jtip',
						ajaxProcess: function(data) {
							data = $(data).not('meta, link, title');
							return data;
						}
					}
				);
			},
			events: function(start, end, callback) {
				$.getJSON("AuditCalendarJSON.action",
					{
						button: 'audits',
						start: $.fullCalendar.formatDate(start,'MM/dd/yyyy HH:mm'),
						end: $.fullCalendar.formatDate(end,'MM/dd/yyyy HH:mm')
					},
					function(json) {
						callback(json.events);
						$table = $('<table class="report" />');
						$table.append('<thead><tr><td>Auditor</td><td>Number of Audits</td></tr></thead>');
						$.each(json.auditorCount, function (k, v) {
								if (k != 'Total')
									$table.append('<tr><td>'+k+'</td><td>'+v+'</td></tr>');
							}
						);
						$table.append('<tr><td> Total </td><td>'+json.auditorCount['Total']+'</td></tr>');
						$('#auditorReport').html($table);
					}
				);
			}
		}
	);
});
</script>
<style>
#thinking {
	float: right;
}
#calendar {
	margin-top: 40px;
}
span.fc-event-time {
	display: block;
}
.cal-webcam a span.fc-event-time {
	background-image: url('images/icon_webcam.png');
	background-repeat: no-repeat;
	text-indent: 24px;
}
ul {
	list-style: none;
}
</style>
</head>
<body>
<h1>Audit Calendar</h1>
<s:include value="../actionMessages.jsp"></s:include>

<div id="auditorReport"></div>
<div id="thinking"></div>
<div id="calendar"></div>
</body>
</html>
