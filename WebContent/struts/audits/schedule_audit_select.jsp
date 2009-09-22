<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" errorPage="../../exception_handler.jsp"%>
<html>
<head>
<title>Schedule Audit</title>
<meta name="help" content="Scheduling_Audits">
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<s:include value="../jquery.jsp"></s:include>
<script type="text/javascript" src="js/schedule_audit.js"></script>
<script type="text/javascript">
var auditID = <s:property value="conAudit.id"/>;
$(function() {
	var ss = $("#ScheduleAudit_availabilityStartDate").datepicker({ minDate: new Date(), numberOfMonths: [1, 2] });
});
</script>
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
	<fieldset class="form bottom"><legend><span>Choose an Audit Time</span></legend>
	<ol>
		<li>Please choose one of the available time blocks below for your audit.<br/>
			<s:textfield name="availabilityStartDate" onchange="showNextAvailable()"/> 
		</li>
		<li id="li_availability">
			<s:include value="schedule_audit_select_content.jsp"/>
		</li>
	</ol>
	</fieldset>
</s:form>

</body>
</html>