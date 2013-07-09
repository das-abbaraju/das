<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html>
<head>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/rules.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>
</head>

<body>
<script type="text/javascript">

$(function(){
	showRules();
});
function showRules() {
	var data = {
			'lastRelease': '<s:property value="lastRelease"/>'
	};
	startThinking({ div: "typeRules", message: "Loading Related Rules" });
	$('#typeRules').load('AuditTypeRulesChangesAjax.action', data);
	startThinking({ div: "catRules", message: "Loading Related Rules" });
	$('#catRules').load('AuditCatRulesChangesAjax.action', data);
}

</script>

<s:if test="permissions.admin">

<h4>Audit Type Rules</h4>
<div id="typeRules"></div>

<h4>Audit Category Rules</h4>
<div id="catRules"></div>

<s:if test="criteriaList.size() > 0">
<h4>Flag Criteria</h4>
<table id="criterialist" class="report">
	<thead>
		<tr>
			<th>Category</th>
			<th>Display Order</th>
			<th>Label</th>
			<th>Description</th>
			<th>Updated</th>
			<th>On</th>
		</tr>
	</thead>
	<s:iterator value="criteriaList">
		<tr>
			<td><s:property value="category"/></td>
			<td><s:property value="displayOrder"/></td>
			<td><s:property value="label"/></td>
			<td>
				<a href="ManageFlagCriteria!edit.action?criteria=<s:property value="id"/>">
					<s:if test="!isStringEmpty(description)">
						<s:property value="description" />
					</s:if>
					<s:else>Description is missing...</s:else>
				</a>
			</td>
			<td><s:property value="updatedBy.name" /></td>
			<td><s:date name="updateDate" format="%{@com.picsauditing.util.PicsDateFormat@Iso}"/></td>
		</tr>
	</s:iterator>
</table>
</s:if>

<s:if test="flagCriteriaOperatorList.size() > 0">
<h4>Flag Criteria Operator</h4>
<table id="flagCriteriaOperatorList" class="report">
	<thead>
		<tr>
			<th>Criteria</th>
			<th>Operator</th>
			<th>Tag</th>
			<th>Flag Color</th>
			<th>Updated</th>
			<th>On</th>
		</tr>
	</thead>
	<s:iterator value="flagCriteriaOperatorList">
		<tr>
			<td>
				<a href="ManageFlagCriteria!edit.action?criteria=<s:property value="criteria.id"/>">
					<s:if test="!isStringEmpty(criteria.description)">
						<s:property value="criteria.description" />
					</s:if>
					<s:else>Description is missing...</s:else>
				</a>
			</td>
			<td>
				<a href="/ManageFlagCriteriaOperator.action?id=<s:property value="operator.id" />">
					<s:property value="operator.name" />
				</a>
			</td>
			<td>
				<s:if test="tag != null"><s:property value="tag.tag" /></s:if>
				<s:else>
					*
				</s:else>
			</td>
			<td><s:property value="flag" /></td>
			<td><s:property value="updatedBy.name" /></td>
			<td><s:date name="updateDate" format="%{@com.picsauditing.util.PicsDateFormat@Iso}"/></td>
		</tr>
	</s:iterator>
</table>
</s:if>



<s:if test="auditTypes.size() > 0">
<h4>Audit Types</h4>
<table class="report">
<thead>
<tr>
	<th>Order</th>
	<th>Class</th>
	<th>Audit Name</th>
	<th>Updated</th>
	<th>On</th>
</tr>
</thead>
<s:iterator value="auditTypes">
<tr>
	<td class="center"><s:property value="displayOrder"/></td>
	<td><s:property value="classType"/></td>
	<td><a title="<s:property value="name"/>" href="ManageAuditType.action?id=<s:property value="id"/>"><s:property value="name"/></a></td>
	<td><s:property value="updatedBy.name" /></td>
	<td><s:date name="updateDate" format="%{@com.picsauditing.util.PicsDateFormat@Iso}"/></td>
</tr>
</s:iterator>
</table>
</s:if>

<s:if test="auditCategories.size() > 0">
<h4>Audit Categories</h4>
<table class="report">
<thead>
<tr>
	<th>Name</th>
	<th>Audit Type</th>
	<th>#Question</th>
	<th>Updated</th>
	<th>On</th>
</tr>
</thead>
<s:iterator value="auditCategories">
<tr>
	<td><a title="<s:property value="name"/>" href="ManageCategory.action?id=<s:property value="id"/>"><s:property value="name"/></a></td>
	<td><a title="<s:property value="auditType.name"/>" href="ManageAuditType.action?id=<s:property value="auditType.id"/>"><s:property value="auditType.name"/></a></td>
	<td><s:property value="numRequired"/> / <s:property value="numQuestions"/></td>
	<td><s:property value="updatedBy.name" /></td>
	<td><s:date name="updateDate" format="%{@com.picsauditing.util.PicsDateFormat@Iso}"/></td>
</tr>
</s:iterator>
</table>
</s:if>

<s:if test="questions.size() > 0">
<h4>Questions</h4>
<table class="report">
<thead>
<tr>
	<th>Name</th>
	<th>Category</th>
	<th>Question Type</th>
	<th>Required</th>
	<th>Required Question</th>
	<th>Visible Question</th>
	<th>Updated</th>
	<th>On</th>
</tr>
</thead>
<s:iterator value="questions">
<tr>
	<td><a title="<s:property value="name"/>" href="ManageQuestion.action?id=<s:property value="id"/>"><s:property value="name"/></a></td>
	<td><a title="<s:property value="category.name"/>" href="ManageCategory.action?id=<s:property value="category.id"/>"><s:property value="category.name"/></a></td>
	<td><s:property value="questionType" /></td>
	<td><s:property value="required" /></td>
	<td><a title="<s:property value="requiredQuestion.name"/>" href="ManageQuestion.action?id=<s:property value="requiredQuestion.id"/>"><s:property value="requiredQuestion.name"/></a></td>
	<td><a title="<s:property value="visibleQuestion.name"/>" href="ManageQuestion.action?id=<s:property value="visibleQuestion.id"/>"><s:property value="visibleQuestion.name"/></a></td>
	<td><s:property value="updatedBy.name" /></td>
	<td><s:date name="updateDate" format="%{@com.picsauditing.util.PicsDateFormat@Iso}"/></td>
</tr>
</s:iterator>
</table>
</s:if>

<s:if test="workFlows.size() > 0">
<h4>Workflows</h4>
<table class="report">
<thead>
<tr>
	<th>Name</th>
	<th>Updated</th>
	<th>On</th>
</tr>
</thead>
<s:iterator value="workFlows">
<tr>
	<td><a title="<s:property value="name"/>" href="ManageAuditWorkFlow.action?id=<s:property value="id"/>"><s:property value="name"/></a></td>
	<td><s:property value="updatedBy.name" /></td>
	<td><s:date name="updateDate" format="%{@com.picsauditing.util.PicsDateFormat@Iso}"/></td>
</tr>
</s:iterator>
</table>
</s:if>

<s:if test="workFlowSteps.size() > 0">
<h4>Workflow Steps</h4>
<table class="report">
<thead>
<tr>
	<th>Workflow</th>
	<th>Old Status</th>
	<th>New Status</th>
	<th>Note Required</th>
	<th>Email Template</th>
	<th>Updated</th>
	<th>On</th>
</tr>
</thead>
<s:iterator value="workFlowSteps">
<tr>
	<td><a title="<s:property value="workflow.name"/>" href="ManageAuditWorkFlow.action?id=<s:property value="workflow.id"/>"><s:property value="workflow.name"/></a></td>
	<td><s:property value="oldStatus" /></td>
	<td><s:property value="newStatus" /></td>
	<td><s:property value="noteRequired" /></td>
	<td><s:property value="emailTemplate.templateName" /></td>
	<td><s:property value="updatedBy.name" /></td>
	<td><s:date name="updateDate" format="%{@com.picsauditing.util.PicsDateFormat@Iso}"/></td>
</tr>
</s:iterator>
</table>
</s:if>

<s:if test="translations.size() > 0">
<h4>Translations</h4>
<table class="report">
<thead>
<tr>
	<th>Key</th>
	<th>Locale</th>
	<th>Value</th>
	<th>Updated</th>
	<th>On</th>
</tr>
</thead>
<s:iterator value="translations">
<tr>
	<td><a title="<s:property value="key"/>" href="ManageTranslations.action?button=Search&key=<s:property value="key"/>"><s:property value="key"/></a></td>
	<td><s:property value="locale" /></td>
	<td><s:property value="value" /></td>
	<td><s:property value="updatedBy.name" /></td>
	<td><s:date name="updateDate" format="%{@com.picsauditing.util.PicsDateFormat@Iso}"/></td>
</tr>
</s:iterator>
</table>
</s:if>

</s:if>
</body>
</html>