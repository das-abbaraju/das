<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Manage Flag Criteria</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=20091105" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=20091105" />
<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=20091105" />
<s:include value="../jquery.jsp"/>
<script type="text/javascript" src="js/jquery/address/jquery.address-1.0.min.js"></script>
<script type="text/javascript" src="js/op_flag_criteria.js?v=20091105"></script>
<script type="text/javascript">
var opID = '<s:property value="operator.id" />';
var shaType = '<s:property value="operator.oshaType" />';
var $tabs, $dialog;
$(function(){
	$dialog = $('#dialog');

	$.address.init(function() {
		$tabs = $('#questions_tab').tabs({
			cache: true
		});
	}).change(function(event) {
		var selection = $('a[rel=address:' + event.value + ']');
		$tabs.tabs('select', selection.attr('href'));
		$.address.title($.address.title().split(' | ')[0] + ' | ' + selection.text());
    });
});
</script>

</head>
<body>
<s:include value="opHeader.jsp"></s:include>
<div id="dialog" style="display:none"></div>

<s:if test="contractorsNeedingRecalculation > 10">
	<div class="alert">
		<s:property value="contractorsNeedingRecalculation"/> contractor flags are now waiting to be automatically recalculated.
	</div>
</s:if>
<br clear="all"/>

<s:if test="operator != operator.inheritFlagCriteria">
	<div class="info">The PQF/Audit Criteria for this account inherits the configuration from <s:property
		value="operator.inheritFlagCriteria.name" />. Please login to that account to modify the criteria. <s:if
		test="permissions.admin">
		<a href="OperatorFlagCriteria.action?id=<s:property
		value="operator.inheritFlagCriteria.id" />">Open <s:property
			value="operator.inheritFlagCriteria.name" /></a>
	</s:if></div>
</s:if>
<s:if test="operator != operator.inheritInsuranceCriteria">
	<div class="info">The InsureGUARD&trade; Criteria for this account inherits the configuration from <s:property
		value="operator.inheritInsuranceCriteria.name" />. Please login to that account to modify the criteria. <s:if
		test="permissions.admin">
		<a href="OperatorFlagCriteria.action?id=<s:property
		value="operator.inheritInsuranceCriteria.id" />">Open <s:property
			value="operator.inheritInsuranceCriteria.name" /></a>
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
</div>
<div id="questions_tab">
	<ul>
		<li><a href="OperatorFlagCriteriaAjax.action?id=<s:property value="id"/>&classType=Audit&button=criteria" title="audit" rel="address:/audit">PQF/Audits</a></li>
		<s:if test="operator.canSeeInsurance.isTrue()">
			<li><a href="OperatorFlagCriteriaAjax.action?id=<s:property value="id"/>&classType=Policy&button=criteria" title="policy" rel="address:/policy">InsureGUARD&trade;</a></li>
		</s:if>
	</ul>
</div>

<div id="notesList"><s:include value="../notes/account_notes_embed.jsp"></s:include></div>

</body>
</html>
