<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"  errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:text name="AuditCalendar.subheading" /></title>
<s:include value="../jquery.jsp" />

<link rel="stylesheet" href="js/jquery/fullcalendar/fullcalendar.css?v=${version}">
<script type="text/javascript" src="js/jquery/fullcalendar/fullcalendar.min.js?v=${version}"></script>

<link rel="stylesheet" href="css/reports.css?v=<s:property value="version"/>"/>

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
					startThinking( {div: 'thinking', message: translate('JS.AuditCalendar.FetchingEvents') } );
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
						activation: 'click',
						ajaxProcess: function(data) {
							data = $(data).not('meta, link, title');
							return data;
						}
					}
				);
			},
			events: function(start, end, callback) {
				$.getJSON("AuditCalendarJSON!audits.action",
					{
						start: $.fullCalendar.formatDate(start, 'MM/dd/yyyy HH:mm'),
						end: $.fullCalendar.formatDate(end,'MM/dd/yyyy HH:mm')
					},
					function(json) {
						callback(json.events);
						$('#auditorReportTable tbody').empty();
						
						$.each(json.auditorCount, function (k, v) {
							if (k != translate('JS.AuditCalendar.Total'))
								$('#auditorReportTable tbody').append('<tr><td>'+k+'</td><td>'+v+'</td></tr>');
						});
						
						$('#auditorReportTable tbody').append('<tr><td>' + translate('JS.AuditCalendar.Total') + 
								'</td><td>'+json.auditorCount[translate('JS.AuditCalendar.Total')]+'</td></tr>');
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
.cal-onsite, .cal-onsite a, .cal-onsite .fc-event-time {
	background-color: #A74D0F;
	border-color: #A74D0F;
}
ul {
	list-style: none;
}
</style>
</head>
<body>
<h1><s:text name="AuditCalendar.title" /></h1>
<s:include value="../actionMessages.jsp"></s:include>

<div id="thinking"></div>
<div id="calendar"></div>
<div id="auditorReport">
	<table class="report" id="auditorReportTable">
		<thead>
			<tr>
				<th><s:text name="global.SafetyProfessional" /></th>
				<th><s:text name="AuditCalendar.NumberOfAudits" /></th>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>
</div>
</body>
</html>
