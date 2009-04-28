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
</head>
<body>
<h1>Manage Flag Criteria <span class="sub"><s:property value="operator.name" /></span></h1>

<s:if test="operator != operator.inheritFlagCriteria">
	<div id="info">The PQF/Audit Criteria for this account inherits the configuration from <s:property
		value="operator.inheritFlagCriteria.name" />. Please login to that account to modify the criteria.
		<s:if test="permissions.admin"><a href="OperatorFlagCriteria.action?id=<s:property
		value="operator.inheritFlagCriteria.id" />">Open <s:property
		value="operator.inheritFlagCriteria.name" /></a></s:if>
		</div>
</s:if>
<s:if test="operator != operator.inheritInsuranceCriteria">
	<div id="info">The InsureGuard&trade; Criteria for this account inherits the configuration from <s:property
		value="operator.inheritInsuranceCriteria.name" />. Please login to that account to modify the criteria.
		<s:if test="permissions.admin"><a href="OperatorFlagCriteria.action?id=<s:property
		value="operator.inheritFlagCriteria.id" />">Open <s:property
		value="operator.inheritFlagCriteria.name" /></a></s:if>
		</div>
</s:if>
<div id="criteriaList"><s:include value="op_flag_criteria_list.jsp"></s:include></div>

<div id="criteriaEdit" style="display: none"></div>

<div id="criteriaAdd" style="display: none"></div>

</body>
</html>
