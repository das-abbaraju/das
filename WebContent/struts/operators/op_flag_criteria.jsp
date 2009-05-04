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
<script type="text/javascript" src="js/op_flag_criteria.js"></script>
<script type="text/javascript">
	var opID = '<s:property value="operator.id" />';
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
<div style="position: absolute; right: 0; top: 0;"><a href=""
	style="background-color: #EEE; padding: 3px 8px;" onclick="showOtherAccounts(); return false;"
	>There are <s:property value="inheritingOperators.size"/> other account(s) that use this criteria</a></div>
<div id="otherAccounts" style="position: absolute; top: 28px; right: 10px; background-color: #F0F0F0; display: none;">
<ol>
	<s:iterator value="inheritingOperators">
		<li><a href="FacilitiesEdit.action?id=<s:property value="id"/>"><s:property value="name" /></a></li>
	</s:iterator>
</ol>
<a href="#" onclick="$('otherAccounts').hide(); return false;">...hide</a>
</div>
</s:if>

<ul id="navListTop">
	<li><a href="?id=<s:property value="id"/>&classType=PQF" class="<s:if test="classType.PQF">current</s:if>">PQF</a></li>
	<li><a href="?id=<s:property value="id"/>&classType=Policy" class="<s:if test="classType.policy">current</s:if>">InsureGuard</a></li>
	<li><a href="?id=<s:property value="id"/>&classType=Audit" class="<s:if test="classType.audit">current</s:if>">Audits</a></li>
</ul>

<div id="criteriaList"><s:include value="op_flag_criteria_list.jsp"></s:include></div>
</div>

</body>
</html>
