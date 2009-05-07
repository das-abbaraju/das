<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Manage Flag Criteria</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/modalbox.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/calendar.css" />
<script type="text/javascript" src="js/prototype.js"></script>
<script type="text/javascript" src="js/scriptaculous/scriptaculous.js"></script>
<script type="text/javascript" src="js/modalbox.js"></script>
<script type="text/javascript" src="js/CalendarPopup.js"></script>

<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css" />
<script src="js/notes.js" type="text/javascript"></script>

<script type="text/javascript" src="js/op_flag_criteria.js"></script>
<script type="text/javascript">
	var opID = '<s:property value="operator.id" />';
	var classType = '<s:property value="classType"/>';
</script>

</head>
<body>
<s:include value="opHeader.jsp"></s:include>

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
<div id="growlBox"></div>

<s:if test="inheritingOperators.size > 0">
<div style="position: absolute; right: 0; top: 0;">[<a href=""
	style="padding: 3px;" onclick="showOtherAccounts(); return false;"
	>There are <s:property value="inheritingOperators.size"/> other account(s) that use this criteria</a>]</div>
<div id="otherAccounts" style="position: absolute; top: 28px; right: 10px; background-color: #EEEEEE; border: 1px solid #C3C3C3; display: none;">
<ol>
	<s:iterator value="inheritingOperators">
		<pics:permission perm="ManageOperators">
			<li><a href="FacilitiesEdit.action?id=<s:property value="id"/>"><s:property value="name" /></a></li>
		</pics:permission>
		<pics:permission perm="ManageOperators" negativeCheck="true">
			<li><s:property value="name" /></li>
		</pics:permission>
	</s:iterator>
</ol>
... <a href="#" onclick="showOtherAccounts(); return false;">hide</a>
</div>
</s:if>

<ul id="navListTop">
	<li><a href="?id=<s:property value="id"/>&classType=Audit" class="<s:if test="classType.Audit">current</s:if>">PQF/Audits</a></li>
	<li><a href="?id=<s:property value="id"/>&classType=Policy" class="<s:if test="classType.Policy">current</s:if>">InsureGuard</a></li>
</ul>

<div id="criteriaList"><s:include value="op_flag_criteria_list.jsp"></s:include></div>

<input id="addQuestionButton" type="button" class="picsbutton positive" value="Add Question" onclick="toggleQuestionList()"/>
<input id="hideQuestionButton" type="button" class="picsbutton positive" value="Hide Questions" onclick="toggleQuestionList()" style="display:none"/>
<div id="questionList" style="display:none">
	<table class="report" id="questionTable">
		<thead>
			<tr>
				<th> Type </th>
				<th> # </th>
				<th> Question </th>
			</tr>
		</thead>
		<s:iterator value="questions">
			<tr id="addRow<s:property value="id"/>" class="clickable" onclick="showCriteria(<s:property value="id"/>,'<s:property value="subCategory.category.auditType.auditName"/>')">
				<td>
					<s:property value="subCategory.category.auditType.auditName"/>
				</td>
				<td>
					<s:property value="subCategory.category.number"/>.<s:property value="subCategory.number"/>.<s:property value="number"/>
				</td>
				<td>
					<s:property value="question" escape="false"/>
				</td>
			</tr>
		</s:iterator>
	</table>
</div>
</div>
<br clear="all"/>
<s:if test="contractorsNeedingRecalculation > 10">
	<div id="alert">
		<s:property value="contractorsNeedingRecalculation"/> of your contractor's flags will be recalculated.
	</div>
</s:if>
</body>
</html>
