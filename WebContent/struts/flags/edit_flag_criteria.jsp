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
	$('.showCritOp').click(function(){
		var data = {
				'criteriaID': <s:property value="id"/>
		};
		startThinking({ div: "criteriaoperators", message: "Loading Affected Operators" });
		$('#criteriaoperators').load('ReportCriteriaOperatorsAjax.action', data);		
	});
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

</script>
</head>
<body>
<h1>Edit Flag Criteria</h1>

<s:if test="criteria != null">
<s:include value="../actionMessages.jsp"/>
</s:if>

<a href="ManageFlagCriteria.action">&lt;&lt; Back to List</a>
<s:form id="itemform" method="post" cssClass="form">
	<s:hidden name="criteria"/>
	<fieldset>
		<h2>General</h2>
		<ol>
			<li>
				<label><s:text name="global.id"/>:</label>
				<s:if test="criteria.id == 0">NEW</s:if>
				<s:else><s:property value="criteria.id"/></s:else>
				<s:if test="criteria.id > 0">
				<s:set name="o" value="criteria"/><s:include value="../who.jsp"/>
				</s:if>
			</li>
			<li>
				<s:select list="criteriaCategory" name="criteria.category" theme="form" />
			</li>
			<li>
				<s:textfield name="criteria.displayOrder" theme="form"/>
			</li>
			<li>
				<s:textfield name="criteria.label" theme="formhelp" maxlength="30"/>
			</li>
			<li>
				<s:textarea name="criteria.description" theme="formhelp" cols="30" rows="4" />
			</li>
		</ol>
	</fieldset>
	<fieldset>
		<h2>Value</h2>
		<ol>
			<li>
				<s:select name="criteria.dataType" list="datatypeList" theme="form"/>
			</li>
			<li>
				<s:select name="criteria.comparison" list="comparisonList" theme="form"/>
			</li>
			<li>
				<s:textfield name="criteria.defaultValue" theme="form"/>
			</li>
			<li>
				<s:checkbox name="criteria.allowCustomValue" theme="form"/>
			</li>
		</ol>
	</fieldset>
	
	<!-- problem area -->
	<fieldset>
		<h2>Audit | Question</h2>
		<ol>
			<li>
				<s:select name="criteria.auditType" list="{}" value="%{criteria.auditType.id}" theme="form">
					<s:iterator value="auditTypeMap" var="aType">
						<s:optgroup label="%{#aType.key}" list="#aType.value" listKey="id" listValue="auditName"/>
					</s:iterator>
				</s:select>
			</li>
			<li>
				<s:select name="criteria.question" list="{}" value="%{criteria.question.id}" theme="form">
					<s:iterator value="questionMap" var="flagQuestion">
						<s:optgroup label="%{#flagQuestion.key.auditName}" list="#flagQuestion.value" listKey="id" listValue="shortQuestion" />
					</s:iterator>
				</s:select>
			</li>
		</ol>
	</fieldset>
	<!-- end problem area -->
	<fieldset>
		<h2>OSHA</h2>
		<ol>
			<li>
				<s:select name="criteria.oshaType" list="@com.picsauditing.jpa.entities.OshaType@values()" theme="form"/>
			</li>
			<li>
				<s:select name="criteria.oshaRateType" list="@com.picsauditing.jpa.entities.OshaRateType@values()" listValue="description" theme="form"/>
			</li>
			<li>
				<s:select name="criteria.multiYearScope" list="@com.picsauditing.jpa.entities.MultiYearScope@values()" listValue="description" theme="form"/>
			</li>
		</ol>
	</fieldset>
	<fieldset>
		<ol>
			<li>
				<s:select list="@com.picsauditing.jpa.entities.AuditStatus@values()" name="criteria.requiredStatus" theme="form"/>
			</li>
			<li>
				<s:checkbox name="criteria.insurance" theme="form"/>
			</li>
			<li>
				<s:checkbox name="criteria.flaggableWhenMissing" theme="form"/>
			</li>
		</ol>
	</fieldset>
	
	<fieldset class="form submit">
		<s:submit action="ManageFlagCriteria!save" value="Save" cssClass="picsbutton positive" />
		<s:submit action="ManageFlagCriteria" value="Cancel" cssClass="picsbutton" />
	</fieldset>
</s:form>

<h3>Used By The Following Operators</h3>
<a class="refresh showCritOp showPointer">Refresh</a>	
<div id="criteriaoperators"></div>

</body>
</html>