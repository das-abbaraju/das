<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>
<%@ page language="java" errorPage="/exception_handler.jsp" %>

<html>
	<head>
		<title>My Schedule</title>
		
		<link rel="stylesheet" href="js/jquery/weekcalendar/jquery.weekcalendar.css?v=${version}">
		<link rel="stylesheet" href="js/jquery/fullcalendar/fullcalendar.css?v=${version}">
		<link href="js/jquery/timeentry/jquery.timeentry.css?v=${version}" media="screen" type="text/css" rel="stylesheet">
		
		<style>
		.cal-availability, .cal-availability a, .cal-availability .fc-event-time {
			border-color: #f22;
			background-color: #f22;
		}
		.cal-vacation, .cal-vacation a, .cal-vacation .fc-event-time {
			border-color: #606;
			background-color: #606;
		}
		.cal-holiday, .cal-holiday a, .cal-holiday .fc-event-time {
			border-color: #063;
			background-color: #063;
		}
		.cal-schedule, .cal-schedule a, .cal-schedule .fc-event-time {
			border-color: #00f;
			background-color: #00f;
		}
		.cal-onsite, .cal-onsite a, .cal-onsite .fc-event-time {
			background-color: #A74D0F;
			border-color: #A74D0F;
		}
		span.fc-event-time {
			display: block;
		}
		.cal-webcam a span.fc-event-time {
			background-image: url('images/icon_webcam.png');
			background-repeat: no-repeat;
			text-indent: 24px;
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
		ul {
			list-style: none;
		}
		</style>
		
		<s:include value="../jquery.jsp" />
		
		<script type="text/javascript" src="js/jquery/weekcalendar/jquery.weekcalendar.js.min.js?v=${version}"></script>
		<script type="text/javascript" src="js/jquery/fullcalendar/fullcalendar.min.js?v=${version}"></script>
		<script src="js/jquery/timeentry/jquery.timeentry.min.js?v=${version}" type="text/javascript"></script>
		
		
		<script type="text/javascript">
			var hasHoliday = false;
			
			<pics:permission perm="Holidays">
				hasHoliday = true;
			</pics:permission>
			
			$.ajaxSetup({
				error: function (XMLHttpRequest, textStatus, errorThrown) {
					$.gritter.add({
						title:'Unexpected Error Occurred', 
						text: 'Connection with PICS failed. If this continues, try refreshing or logging out.'
					});
				}
			});
		</script>
		
		<script type="text/javascript" src="js/my_schedule.js?v=<s:property value="version"/>"></script>
	</head>
	<body>
		<h1>My Schedule</h1>
		
		<s:include value="../actionMessages.jsp" />
		
		<div id="message"></div>
		
		<s:select list="safetyList" name="currentUserID" id="currentUserID" listKey="id" listValue="name"/>
		
		<div id="thinkingDiv" style="float:right"></div>
		<div id="schedule_tabs" class="ui-tabs">
			<ul>
				<li>
					<a href="#preview"><span>Availability Preview</span></a>
				</li>
				<li>
					<a href="#aschedule"><span>Audit Schedule</span></a>
				</li>
			</ul>
			
			<div id="preview" class="ui-tabs-hide">
				<table>
					<tr>
						<s:iterator value="#{'Vacation':'cal-vacation', 'Holiday':'cal-holiday', 'Schedule':'cal-schedule', 'Onsite':'cal-onsite', 'Availability':'cal-availability'}">
							<td class="<s:property value="value"/>" style="color:white;padding:2px;">
								<s:property value="key"/>
							</td>
						</s:iterator>
					</tr>
				</table>
				
				<div id="calendar_wrapper">
					<div id="cal_vacat"></div>
				</div>
			</div>
			<div id="aschedule" class="ui-tabs-hide">
				<div id="calendar_wrapper">
					<div id="cal_sched"></div>
				</div>
			</div>
		</div>
		<div id="vacation_form" style="display:none">
			<s:set name="vacationTimes" value="#{'12:00 AM':'', '12:00 PM':'12:00 PM', '02:00 PM':'02:00 PM', '04:00 PM':'04:00 PM', '06:00 PM':'06:00 PM'}"/>
			
			<s:form>
				<fieldset>
					<s:hidden name="id"/>
					
					<table class="modal_form">
						<pics:permission perm="Holidays">
							<tr>
								<td class="title">
									Type
								</td>
								<td>
									<s:select list="#{'Vacation':'Current User','Holiday':'Everyone'}" name="type"/>
								</td>
							</tr>
						</pics:permission>
						
						<pics:permission perm="Holidays" negativeCheck="true">
							<s:hidden name="type" value="'Vacation'"/>
						</pics:permission>
						
						<tr>
							<td class="title" rowspan="2">
								Title
							</td>
							<td>
								<s:textfield name="title"/>
							</td>
						</tr>
						<tr>
							<td>
								<input type="checkbox" id="all-day" onclick="if(this.checked) $('.not-all-day').hide(); else $('.not-all-day').show();"/>
								All Day
							</td>
						</tr>
						<tr>
							<td class="title" rowspan="3">
								When
							</td>
							<td>
								<s:textfield name="startDate" cssClass="datepicker" size="10"/>
								<span class="not-all-day">
									<s:textfield name="startTime" cssClass="time" size="8"/>
								</span>
							</td>
						</tr>
						<tr>
							<td class="title not-all-day" style="text-align:center">
								to
							</td>
						</tr>
						<tr>
							<td>
								<s:textfield name="endDate" cssClass="datepicker not-all-day" size="10"/>
								<span class="not-all-day">
									<s:textfield name="endTime" cssClass="time" size="8"/>
								</span>
							</td>
						</tr>
					</table>
				</fieldset>
			</s:form>
		</div>
	</body>
</html>