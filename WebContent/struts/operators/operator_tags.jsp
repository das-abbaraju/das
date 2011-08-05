<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title><s:text name="OperatorTags.title.WithOperator"><s:param value="%{operator.name}" /></s:text></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/rules.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>
<script type="text/javascript">
$(function() {
	$('a[rel*="facebox"]').facebox({
 		loading_image : 'loading.gif',
 		close_image : 'closelabel.gif'
 	});
	showAuditTypeRules();
	showCategoryRules();
	
	$('#operatorTagForm').delegate('.checkRemove', 'click', function(e) {
		var id = $(this).attr('rel');
		
		$.get('ContractorTagsAjax.action', {tagID: id, button: 'removeNum'} , function(data) {
			$.facebox(data);
		});
	});
 });

function showAuditTypeRules() {
	var data = {
			'comparisonRule.operatorAccount.id': <s:property value="id"/>,
			button: 'tags'
	};
	$('#auditrules').think({message: translate('JS.OperatorTags.message.LoadingRelatedRules', ['<s:text name="AuditType" />']) }).load('AuditTypeRuleTableAjax.action', data);
}

function showCategoryRules() {
	var data = {
			'comparisonRule.operatorAccount.id': <s:property value="id"/>,
			button: 'tags'
	};
	$('#categoryrules').think({message: translate('JS.OperatorTags.message.LoadingRelatedRules', ['<s:text name="AuditCategory" />']) }).load('CategoryRuleTableAjax.action', data);
}
</script>
</head>
<body>

<s:include value="../actionMessages.jsp" />

<s:if test="permissions.admin">
	<s:include value="opHeader.jsp"></s:include>
</s:if>
<s:else>
	<h1><s:text name="OperatorTags.title.DefineContractorTags" /></h1>
</s:else>

<a href="OperatorTags.action?id=<s:property value="id" />"><s:text name="button.Refresh" /></a>
<div id="warnConfirm"></div>
<s:form id="operatorTagForm">
	<s:hidden name="id" />
	<table class="report">
		<thead>
			<tr>
				<th><s:text name="OperatorTags.header.TagID" /></th>
				<th><s:text name="OperatorTags.header.TagName" /></th>
				<th><s:text name="OperatorTags.header.VisibleTo"><s:param value="%{operator.name}" /></s:text></th>
				<th><s:text name="OperatorTags.header.VisibleTo"><s:param value="%{getText('global.Contractors')}" /></s:text></th>
				<s:if test="operator.corporate">
					<th><s:text name="OperatorTags.header.UsableBySites" /></th>
				</s:if>
				<pics:permission perm="ContractorTags" type="Delete">
					<th><s:text name="button.Remove" /></th>
				</pics:permission>
			</tr>
		</thead>
		<s:set var="globalOperator" value="operator" />
		<s:iterator value="tags" status="rowstatus">
			<tr><s:hidden name="tags[%{#rowstatus.index}].id" value="%{id}" />
				<td class="right"><s:property value="id" /></td>
				<pics:permission perm="ContractorTags" type="Edit">
					<s:if test="operator.id == permissions.accountId">
						<td><s:textfield
							name="tags[%{#rowstatus.index}].tag" value="%{tag}" /></td>
						<td><s:checkbox name="tags[%{#rowstatus.index}].active" value="%{active}" /></td>
						<td><s:checkbox name="tags[%{#rowstatus.index}].visibleToContractor" value="%{visibleToContractor}" /></td>
						<s:if test="operator.corporate">
							<td><s:checkbox name="tags[%{#rowstatus.index}].inheritable" value="%{inheritable}" /></td>
						</s:if>
					</s:if>
				</pics:permission>
				<s:else>
					<td><s:property value="tag"/></td>
					<td class="center">
						<s:if test="active"><s:text name="YesNo.Yes" /></s:if>
						<s:else><s:text name="YesNo.No" /></s:else>
					</td>
					<td class="center">
						<s:if test="visibleToContractor"><s:text name="YesNo.Yes" /></s:if>
						<s:else><s:text name="YesNo.No" /></s:else>
					</td>
				</s:else>
				<pics:permission perm="ContractorTags" type="Delete">
					<s:if test="#globalOperator.corporate && operator.id != permissions.accountId">
						<s:if test="operator.id != permissions.accountId">
							<td><s:checkbox name="tags[%{#rowstatus.index}].inheritable" value="%{inheritable}" disabled="true"/></td>
						</s:if>
						<s:else>
							<td><s:checkbox name="tags[%{#rowstatus.index}].inheritable" value="%{inheritable}" /></td>
						</s:else>
					</s:if>
					<s:if test="operator.id != permissions.accountId"><td><s:text name="OperatorTags.message.CannotRemove" /></td></s:if>
					<s:else><td><a href="#" class="checkRemove" rel="<s:property value="id" />"><s:text name="button.Remove" /></a></td></s:else>
				</pics:permission>
			</tr>
		</s:iterator>
		<s:if test="permissions.operatorCorporate">
		<pics:permission perm="ContractorTags" type="Edit">
			<tr>
				<td><s:text name="OperatorTags.label.New" /></td>
				<td><s:textfield name="tags[%{tags.size}].tag" value="%{tag}" /></td>
					<td colspan="<s:property value="operator.corporate ? 4 : 3"/>"><s:text name="OperatorTags.label.AddNewTag" /></td>
			</tr>
		</pics:permission>
		</s:if>
		<s:else>
			<tr>
				<td><s:text name="OperatorTags.label.New" /></td>
				<td><s:textfield name="tags[%{tags.size}].tag" value="%{tag}" /></td>
				<td colspan="<s:property value="operator.corporate ? 4 : 3"/>">
					<s:text name="OperatorTags.label.AddNewTag" />
				</td>
			</tr>
		</s:else>
	</table>

	<div>
		<s:submit method="save" value="%{getText('button.Save')}" cssClass="picsbutton positive" />
	</div>
</s:form>

<pics:permission perm="ManageAuditTypeRules">
	<br/>
	<h3><s:text name="OperatorTags.label.RelatedRules"><s:param value="%{getText('AuditType')}" /></s:text></h3>
	<div id="auditrules"></div>
	<s:if test="permissions.isCanAddRuleForOperator(operator)">
		<a href="AuditTypeRuleEditor.action?button=New&ruleOperatorAccountId=<s:property value="operator.id" />" class="add"><s:text name="OperatorTags.link.AddRule"><s:param value="%{getText('AuditType')}" /></s:text></a>
	</s:if>
</pics:permission>

<pics:permission perm="ManageCategoryRules">
	<br/>
	<br/>
	<h3><s:text name="OperatorTags.label.RelatedRules"><s:param value="%{getText('AuditCategory')}" /></s:text></h3>
	<div id="categoryrules"></div>
	<s:if test="permissions.isCanAddRuleForOperator(operator)">
		<a href="CategoryRuleEditor.action?button=New&ruleOperatorAccountId=<s:property value="operator.id" />" class="add"><s:text name="OperatorTags.link.AddRule"><s:param value="%{getText('AuditCategory')}" /></s:text></a>
	</s:if>
</pics:permission>

</body>
</html>
