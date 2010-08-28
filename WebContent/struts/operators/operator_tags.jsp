<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title><s:property value="operator.name" /> Tags</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/rules.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>
<script type="text/javascript">
$(function() {
	$('a[rel*=facebox]').facebox({
 		loading_image : 'loading.gif',
 		close_image : 'closelabel.gif'
 	});
 });
function checkRemove(id){
	jQuery.get('ContractorTagsAjax.action',{tagID: id, button: 'removeNum'}, function(data){
		jQuery.facebox(data);
});
}

</script>
</head>
<body>
<s:if test="permissions.admin">
	<s:include value="opHeader.jsp"></s:include>
</s:if>
<s:else>
	<h1>Define Contractor Tags</h1>
</s:else>

<a href="OperatorTags.action?id=<s:property value="id" />">Refresh</a>
<div id="warnConfirm"></div>
<s:form>
	<s:hidden name="id" />
	<table class="report">
		<thead>
			<tr>
				<th>Tag ID</th>
				<th>Tag Name</th>
				<th>Visible to <br /><s:property value="operator.name" /></th>
				<th>Visible to <br />Contractors</th>
				<s:if test="operator.corporate">
					<th>Usable by<br />Sites</th>
				</s:if>
				<pics:permission perm="ContractorTags" type="Delete"><th>Remove</th></pics:permission>
			</tr>
		</thead>
		<s:iterator value="tags" status="rowstatus">
			<tr><s:hidden name="tags[%{#rowstatus.index}].id" value="%{id}" />
				<td class="right"><s:property value="id" /></td>
				<s:if test="operator.id == permissions.accountId">
					<td><s:textfield
						name="tags[%{#rowstatus.index}].tag" value="%{tag}" /></td>
					<td><s:checkbox name="tags[%{#rowstatus.index}].active" value="%{active}" /></td>
					<td><s:checkbox name="tags[%{#rowstatus.index}].visibleToContractor" value="%{visibleToContractor}" /></td>
					<s:if test="operator.corporate">
						<td><s:checkbox name="tags[%{#rowstatus.index}].inheritable" value="%{inheritable}" /></td>
					</s:if>
				</s:if>
				<s:else>
					<td><s:property value="tag"/></td>
					<td class="center"><s:if test="active">Yes</s:if><s:else>No</s:else></td>
					<td class="center"><s:if test="visibleToContractor">Yes</s:if><s:else>No</s:else></td>
				</s:else>
				<pics:permission perm="ContractorTags" type="Delete">
					<s:if test="operator.id != permissions.accountId"><td>Can Not Remove</td></s:if>
					<s:else><td><a href="#" onclick="checkRemove(<s:property value="id" />);">Remove</a></td></s:else>
				</pics:permission>
			</tr>
		</s:iterator>
		<tr>
			<td>NEW</td>
			<td><s:textfield name="tags[%{tags.size}].tag" value="%{tag}" /></td>
				<td colspan="<s:property value="operator.corporate ? 4 : 3"/>">Add New Tag</td>
		</tr>
	</table>

	<div>
	<button type="submit" name="button" value="Save" class="picsbutton positive">Save</button>
	</div>
</s:form>

<br/>
<br/>

<pics:permission perm="ManageAuditTypeRules">
	<div>
		<h3>Related Audit Type Rules</h3>
		<s:if test="relatedAuditTypeRules.size() == 0">
			<div class="alert">
				There are no Audit Type rules for any of the above tags. <a href="AuditTypeRuleEditor.action?button=edit&rule.include=true">Click here to create an Audit Type Rule.</a>
			</div>
		</s:if>
		<s:else>
			<s:if test="relatedRules.size() >= 50">
				<div class="alert">
					There are too many rules to display here. <a href="AuditTypeRuleSearch.action">Click here to Search for Audit Type Rules.</a>
				</div>
			</s:if>
			<table class="report">
				<s:set name="ruleURL" value="'AuditTypeRuleEditor.action'"/>
				<s:set name="auditTypeRule" value="true"/>
				<s:include value="/struts/audits/rules/audit_rule_header.jsp"/>
				<s:iterator value="relatedAuditTypeRules" id="r">
					<s:include value="/struts/audits/rules/audit_rule_view.jsp"/>
				</s:iterator>
			</table>
		</s:else>
	</div>
</pics:permission>

<pics:permission perm="ManageCategoryRules">
	<div>
		<h3>Related Category Rules</h3>
		<s:if test="relatedCategoryRules.size() == 0">
			<div class="alert">
				There are no Category rules for any of the above tags. <a href="CategoryRuleEditor.action?button=edit&rule.include=true">Click here to create a Category Rule.</a>
			</div>
		</s:if>
		<s:else>
			<s:if test="relatedCategoryRules.size() >= 50">
				<div class="alert">
					There are too many rules to display here. <a href="CategoryRuleSearch.action">Click here to Search for Category Rules.</a>
				</div>
			</s:if>
			<table class="report">
				<s:set name="ruleURL" value="'CategoryRuleEditor.action'"/>
				<s:set name="categoryRule" value="true"/>
				<s:include value="/struts/audits/rules/audit_rule_header.jsp"/>
				<s:iterator value="relatedCategoryRules" id="r">
					<s:include value="/struts/audits/rules/audit_rule_view.jsp"/>
				</s:iterator>
			</table>
		</s:else>
	</div>
</pics:permission>

</body>
</html>
