<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>

<title>Edit Flag Criteria</title>

<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />

<s:include value="../jquery.jsp"/>
<script type="text/javascript">
$(function(){
	showCriteriaOperators();
	$('.cluetip').cluetip({
		closeText: "<img src='images/cross.png' width='16' height='16'>",
		arrows: true,
		cluetipClass: 'jtip',
		local: true,
		clickThrough: false
	});
	<s:if test="">
		var jsonObj = <s:property value="" />
	</s:if>	
});

function showCriteriaOperators() {
	var data = {
			'criteriaID': <s:property value="id"/>
	};
	startThinking({ div: "criteriaoperators", message: "Loading Affected Operators" });
	$('#criteriaoperators').load('ReportCriteriaOperatorsAjax.action', data);
}
</script>
</head>
<body>
<h1>Edit Flag Criteria</h1>

<s:if test="criteria != null">
<s:include value="../actionMessages.jsp"/>
</s:if>
<s:form id="itemform" method="post" action="EditFlagCriteria">
	<s:hidden name="id"/>
	<fieldset>
		<h2>General</h2>
		<div>
			<label><s:text name="form.id"/></label>
			<s:if test="criteria.id == 0">NEW</s:if>
			<s:else><s:property value="criteria.id"/></s:else>
			<s:if test="criteria.id > 0">
			<s:set name="o" value="criteria"/><s:include value="../who.jsp"/>
			</s:if>
		</div>
		<s:select list="criteriaCategory" name="criteria.category" theme="form" />
		<s:textfield name="criteria.displayOrder" theme="form"/>
		<s:textfield name="criteria.label" theme="formhelp" maxlength="30"/>
		<s:textarea name="criteria.description" theme="formhelp" cols="30" rows="4" />
		
	</fieldset>
	<fieldset>
		<h2>Value</h2>
		<s:select name="criteria.dataType" list="datatypeList" theme="form"/>
		<s:select name="criteria.comparison" list="comparisonList" theme="form"/>
		<s:textfield name="criteria.defaultValue" theme="form"/>
		<s:checkbox name="criteria.allowCustomValue" theme="form"/>
	</fieldset>
	
	<!-- problem area -->
	<fieldset>
		<h2>Audit | Question</h2>
		<s:select name="auditTypeID" list="{}" headerKey="0" headerValue=" - Audit Type - " value="%{criteria.auditType.id}" theme="form">
			<s:iterator value="auditTypeMap" var="aType">
				<s:optgroup label="%{#aType.key}" list="#aType.value" listKey="id" listValue="auditName"/>
			</s:iterator>
		</s:select>
		<s:select name="questionID" list="{}" headerKey="-1" headerValue=" - Question - " value="%{criteria.question.id}" theme="form">
			<s:iterator value="questionMap" var="flagQuestion">
				<s:optgroup label="%{#flagQuestion.key.auditName}" list="#flagQuestion.value" listKey="id" listValue="shortQuestion" />
			</s:iterator>
		</s:select>
	</fieldset>
	<!-- end problem area -->
	<fieldset>
		<h2>OSHA</h2>
		<s:select name="criteria.oshaType" list="@com.picsauditing.jpa.entities.OshaType@values()" headerKey="" headerValue=" - Osha Type - " theme="form"/>
		<s:select name="criteria.oshaRateType" list="@com.picsauditing.jpa.entities.OshaRateType@values()" listValue="description" headerKey="" headerValue=" - Osha Rate Type - " theme="form"/>
		<s:select name="criteria.multiYearScope" list="@com.picsauditing.jpa.entities.MultiYearScope@values()" listValue="description" headerKey="" headerValue=" - Multi Year Scope - " theme="form"/>
	</fieldset>
	<fieldset>
		<s:select list="@com.picsauditing.jpa.entities.AuditStatus@values()" name="criteria.requiredStatus" headerKey="" headerValue=" - Required Status - " theme="form"/>
		<s:checkbox name="criteria.insurance" theme="form"/>
		<s:checkbox name="criteria.flaggableWhenMissing" theme="form"/>
	</fieldset>
	
	<fieldset class="form submit">
		<input type="submit" name="button" value="Save" class="picsbutton positive"/>
		<input type="button" value="Cancel" class="picsbutton goback"/>
	</fieldset>
</s:form>

<h3>Used By The Following Operators</h3>
<a href="#" onclick="showCriteriaOperators(); return false;" class="refresh">Refresh</a>	
<div id="criteriaoperators"></div>

</body>
</html>