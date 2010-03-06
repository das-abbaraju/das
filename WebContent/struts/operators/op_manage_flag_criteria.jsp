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
<s:if test="permissions.operatorCorporate">
	<s:if test="!insurance && !operator.equals(operator.inheritFlagCriteria)">
		<div id="info">Flag Criteria inherited from <s:property value="operator.inheritFlagCriteria.name" /></div>
	</s:if>
	<s:if test="insurance && !operator.equals(operator.inheritInsuranceCriteria)">
		<div id="info">Insurance Criteria inherited from <s:property value="operator.inheritInsuranceCriteria.name" /></div>
	</s:if>
</s:if>

<div style="vertical-align: top">
<s:form id="form1" method="get">
	<s:hidden name="id" />
	<s:hidden name="insurance" />
	<div id="mainThinkingDiv" style="position: absolute; top: -15px; left: 20px;"></div>
	<div id="growlBox"></div>
	<div id="criteriaDiv"><s:include value="op_manage_flag_criteria_list.jsp"></s:include></div>
	<s:if test="canEditFlags()">
		<div id="impactDiv"></div>
		<div style="clear: left; margin: 10px 0px;">
			<a href="#" onclick="getAddQuestions(); return false;" class="add">Add New Criteria</a>
			<span id="thinking"></span>
		</div>
		<div id="addCriteria" style="display:none"></div>
	</s:if>
</s:form>
</div>

<s:if test="permissions.admin">
	<s:if test="!insurance && !operator.equals(operator.inheritFlagCriteria)">
		Flag Criteria inherited from <a href="ManageFlagCriteriaOperator.action?id=<s:property value="operator.inheritFlagCriteria.id" />">
			<s:property value="operator.inheritFlagCriteria.name" /></a>
	</s:if>
	<s:if test="insurance && !operator.equals(operator.inheritInsuranceCriteria)">
		Insurance Criteria inherited from <a href="ManageFlagCriteriaOperator.action?id=<s:property value="operator.inheritInsuranceCriteria.id" />">
			<s:property value="operator.inheritInsuranceCriteria.name" /></a>
	</s:if>
</s:if>

</body>
</html>
