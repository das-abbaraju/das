<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title>Schedule Audit</title>
<meta name="help" content="Scheduling_Audits">
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"></s:include>
<script type="text/javascript" src="js/schedule_audit.js?v=<s:property value="version"/>"></script>
<script type="text/javascript">
var auditID = <s:property value="conAudit.id"/>;
var startDate = '<s:date name="availableSet.latest" format="MM/dd/yyyy"/>';
</script>
<style type="text/css">
#auditHeader,#auditHeaderNav {
	display: none;
}

li#li_availability {
	width: 100%;
	margin: 5px;
	padding: 5px;
}

li#li_availability div.cal_day {
	float: left;
	padding: 10px 20px;
	margin: 10px;
	border: 1px dashed #999;
	width: 14em;
	height: 10em;
}

li#li_availability div.cal_day div.cal_times {
	height:80%;
	overflow:auto;
	padding: 0;
	margin: 0;
}

li#li_availability div.cal_day:hover {
	border: 1px solid #002240;
	background-color: white;
}

li#li_availability a {
	padding: 5px;
	border: 1px solid #4686BF;
	white-space: nowrap;
	text-decoration: none;
	line-height: 35px;
}

li#li_availability a:hover {
	background-color: #ffffbb;
}
</style>
</head>
<body>
<s:include value="../contractors/conHeader.jsp" />

<s:form>
	<fieldset class="form bottom">
	<h2 class="formLegend">Choose an Audit Time</h2>
	<ol>
		<s:if test="permissions.admin">
			<li><a class="picsbutton" href="?button=edit&auditID=<s:property value="auditID"/>">Edit Schedule Manually</a></li>
		</s:if>
		<li>Please choose one of the available time blocks below for your audit.</li>
		<li id="li_availability">
			<s:include value="schedule_audit_select_content.jsp"/>
		</li>
		<li>
			<input type="button" id="show_next" class="picsbutton" value="Show More Timeslots" onclick="showNextAvailable()"/>
		</li>
	</ol>
	</fieldset>
</s:form>

</body>
</html>