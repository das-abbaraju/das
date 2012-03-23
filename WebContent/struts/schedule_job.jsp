<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<html>
	<head>
		<title>
			<s:property value="Schedule Job" default="Schedule Job Stuff" />
		</title>
		
		<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/calendar.css" />
		
		<s:include value="jquery.jsp" />
	    <s:include value="actionMessages.jsp" />
	</head>
	<body>
		<s:form id="save" method="POST" enctype="multipart/form-data">
			<fieldset class="form submit">
			<ol>
			<li>
				<s:submit cssClass="picsbutton positive" method="initSchedule" value="Init Scheduler" />
			</li>
			<li>
				<label>Schedule:</label>
				<s:radio 
					list="#{'daily':'Daily', 'weekly':'Days Per Week', 'monthly':'Days Per Month'}" 
					name="scheduleType"
					theme="pics"
					cssClass="inline" 
				/>
				<%-- TODO: MAKE THIS A DROPDOWN --%>
				<label>Recurrence:</label>
				<s:radio 
					list="#{'0':'once', '900':'15 Minutes'}" 
					name="recurrenceInterval"
					theme="pics"
					cssClass="inline" 
				/>
				<%-- TODO: Clean it up --%>
				<label>Days of the Week:</label>
				<s:checkbox
					list="#{'1':'Monday', '2':'Tuesday'}" 
					name="daysOfTheWeek"
					theme="pics"
					cssClass="inline" 
				/>
			</li>
			<li>
				<s:submit cssClass="picsbutton positive" method="startJob" value="Start a Job" />
			</li>
			</ol>
			</fieldset>
		</s:form>

<%-- Links:
<a href="ScheduleTester!startSchedule.action">Start the Scheduler</a>
<br />
<a href="ScheduleTester!initSchedule.action">Schedule 5 seconds from now...</a>
 --%>
	
	</body>
</html>

