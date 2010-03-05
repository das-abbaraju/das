<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Manage Flag Criteria</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=<s:property value="version"/>" />
<style type="text/css">
table.report a {
	text-decoration: underline;
}
#impactDiv {
	float: right;
	clear: none;
	width: 30%;
}
#impactDiv table.report {
	float: right;
}

#criteriaDiv, #addCriteria {
	float: left;
	clear: none;
	width: 70%
}

.flagImage {
	width: 10px;
	height: 12px;
}

.hide {
	display: none;
}

.hover {
	margin-left: 10px;
}

.newImpact {
	color: gray;
}
</style>
<s:include value="../jquery.jsp"/>
<script type="text/javascript" src="js/op_manage_flag_criteria.js?v=20100219"></script>
<script type="text/javascript">
function getFlag(selectObject) {
	var flagColor = $(selectObject).find("option:selected").val();

	if (flagColor == "Red")
		flagColor = '<s:property value="@com.picsauditing.jpa.entities.FlagColor@Red.smallIcon" escape="false" />';
	else if (flagColor == "Amber")
		flagColor = '<s:property value="@com.picsauditing.jpa.entities.FlagColor@Amber.smallIcon" escape="false" />';
	else
		flagColor = '<s:property value="@com.picsauditing.jpa.entities.FlagColor@Green.smallIcon" escape="false" />';
	
	var flagImage = $(selectObject.parentNode).find("span.flagImage img").replaceWith(flagColor);
}
</script>
</head>
<body>
<s:include value="opHeader.jsp"></s:include>
<div id="dialog" style="display:none"></div>

<div style="vertical-align: top">
<s:form id="form1" method="get">
	<s:hidden name="id" />
	<div id="mainThinkingDiv" style="position: absolute; top: -15px; left: 20px;"></div>
	<div id="growlBox"></div>
	<div id="impactDiv"></div>
	<div id="criteriaDiv"><s:include value="op_manage_flag_criteria_list.jsp"></s:include></div>
	<div style="clear: left; margin: 10px 0px;">
		<s:if test="canEditFlags()">
			<a href="#" onclick="getAddQuestions(<s:if test="insurance">true</s:if>); return false;" class="picsbutton">Add New Criteria</a>
		</s:if>
		<span id="thinking"></span>
	</div>
	<div id="addCriteria" style="display:none"></div>
</s:form>
</div>

</body>
</html>
