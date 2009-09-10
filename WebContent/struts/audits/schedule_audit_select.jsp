<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" errorPage="../../exception_handler.jsp"%>
<html>
<head>
<title>Schedule Audit</title>
<meta name="help" content="Scheduling_Audits">
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js"></script>
<style type="text/css">
#auditHeader,#auditHeaderNav {
	display: none;
}
table.cal_availability {
	width: 100%;
	border-collapse: separate;
	border-spacing: 10px;
}
table.cal_availability td {
	padding: 10px;
	border: 1px dashed #999;
}
table.cal_availability td:hover {
	border: 1px solid #002240;
	background-color: white;
}
table.cal_availability a {
	margin-left: 20px;
	padding: 5px;
	border: 1px solid #4686BF;
	white-space: nowrap;
	text-decoration: none;
	line-height: 35px;
}
table.cal_availability a:hover {
	background-color: #ffffbb;
}
</style>
</head>
<body>
<s:include value="../contractors/conHeader.jsp" />

<s:form>
	<fieldset class="form"><legend><span>Choose an Audit Time</span></legend>
	<ol>
		<li>Please choose one of the available time blocks below for your audit.</li>
		<li>
		<table class="cal_availability">
			<s:iterator value="nextAvailable.rows">
				<tr>
					<s:iterator value="days">
						<td><h4><s:date name="key" format="EEEE, MMM d"/></h4>
						<s:iterator value="value">
							<br /><a href="ScheduleAudit.action?auditID=99949&button=select&timeSelected=<s:date name="startDate" format="MM/dd/yyyy hh:mm a z" />"><s:date name="startDate" format="h:mm a" />
							to <s:date name="endDate" format="h:mm a z" /></a>
						</s:iterator>
						</td>
					</s:iterator>
				</tr>
			</s:iterator>
			<tr>
			<td colspan="4"><a href="">Show me more available time slots</a></td>
			</tr>
		</table>
		</li>
	</ol>
	</fieldset>
	<fieldset class="form submit">
	<div>
	<button id="saveButton" class="picsbutton positive" value="Save Profile" name="button" type="submit">Verify
	Address</button>
	</div>
	</fieldset>
</s:form>

</body>
</html>