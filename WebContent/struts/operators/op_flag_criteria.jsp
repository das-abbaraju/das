<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Manage Flag Criteria</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<script type="text/javascript" src="js/prototype.js"></script>
<script type="text/javascript" src="js/scriptaculous/scriptaculous.js"></script>
<script type="text/javascript" src="js/op_flag_criteria.js"></script>
<script type="text/javascript">
var opID = 0;
</script>

</head>
<body>
<h1>Manage Flag Criteria <span class="sub"><s:property value="operator.name" /></span></h1>

<s:if test="operator != operator.inheritFlagCriteria">
	<div id="info">The PQF/Audit Criteria for this account inherits the configuration from <s:property
		value="operator.inheritFlagCriteria.name" />. Please login to that account to modify the criteria. <s:if
		test="permissions.admin">
		<a href="OperatorFlagCriteria.action?id=<s:property
		value="operator.inheritFlagCriteria.id" />">Open <s:property
			value="operator.inheritFlagCriteria.name" /></a>
	</s:if></div>
</s:if>
<s:if test="operator != operator.inheritInsuranceCriteria">
	<div id="info">The InsureGuard&trade; Criteria for this account inherits the configuration from <s:property
		value="operator.inheritInsuranceCriteria.name" />. Please login to that account to modify the criteria. <s:if
		test="permissions.admin">
		<a href="OperatorFlagCriteria.action?id=<s:property
		value="operator.inheritFlagCriteria.id" />">Open <s:property
			value="operator.inheritFlagCriteria.name" /></a>
	</s:if></div>
</s:if>

<div style="position: relative;">
<div id="mainThinkingDiv" style="position: absolute; top: -15px; left: 20px;"></div>
<div id="criteriaList"><s:include value="op_flag_criteria_list.jsp"></s:include></div>

<div id="criteriaEdit"
	style="display: none; position: absolute; top: 100px; left: 100px; z-index: 10; background-color: white; padding: 10px; border: 2px solid black;"></div>

<button id="addButton" class="picsbutton positive" onclick="showNewCriteria();">+ Add Criteria</button>
<table id="criteriaAdd" style="display: none;">
<tr>
<td style="padding: 5px; vertical-align: top;"><input type="text" id="questionTextBox" value="" /></td>
<td>
<button id="questionSearch" class="picsbutton positive" onclick="questionSearch();">Search</button>
<button id="newCriteriaClose" class="picsbutton negative" onclick="closeNewCriteria();">Close</button>
</td>
</tr>
</table>
<div class="clear"></div>

<div id="questionList"></div>

<div>
<table style="width: 100%;">
	<tr>
		<s:if test="inheritsFlagCriteria.size > 0">
			<td style="padding: 10px;">
			<h3>Companies that inherit the PQF/Audit Criteria</h3>
			<ul>
				<s:iterator value="inheritsFlagCriteria">
					<li><a href="FacilitiesEdit.action?opID=<s:property value="id"/>"><s:property value="name" /></a></li>
				</s:iterator>
			</ul>
			</td>
		</s:if>
		<s:if test="inheritsInsuranceCriteria.size > 0">
			<td style="padding: 10px;">
			<h3>Companies that inherit the InsureGuard&trade; Criteria</h3>
			<ul>
				<s:iterator value="inheritsInsuranceCriteria">
					<li><a href="FacilitiesEdit.action?opID=<s:property value="id"/>"><s:property value="name" /></a></li>
				</s:iterator>
			</ul>
			</td>
		</s:if>
	</tr>
</table>
</div>
</div>

</body>
</html>
