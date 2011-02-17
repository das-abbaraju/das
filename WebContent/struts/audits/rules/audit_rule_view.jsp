<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<script>
function deleteRule(opID,ruleID,ruleType) {
	var deleteMe = confirm('You are deleting a rule with potentially broad reaching affects. Are you sure you want to do this?');
	if (!deleteMe)
		return;
	
	window.location.href = 'OperatorTags.action?button=DeleteRule&id='+opID+'&ruleID='+ruleID+'&ruleType='+ruleType;
}
</script>
<tr class="<s:property value="#ruleclass"/> <s:if test="include">on</s:if><s:else>off</s:else>">
	<td class="center"><a href="<s:property value="#ruleURL"/>?id=<s:property value="#r.id"/>" class="preview"></a></td>
	<td class="center"><s:property value="include ? 'Yes' : 'No'"/></td>
	<td><s:property value="level"/><span style="font-size-adjust: 0.4">.<s:property value="priority"/></span></td>
	<td><s:property value="auditTypeLabel"/>
		<s:if test="auditType"><a href="ManageAuditType.action?id=<s:property value="auditType.id"/>" target="_BLANK">^</a></s:if>
	</td>
	<s:if test="categoryRule">
		<td>
			<s:if test="auditCategory == null">
				<s:if test="rootCategory == null">*</s:if>
				<s:else><s:property value="rootCategory ? '* All Top Categories' : '* All Child Categories' "/></s:else>
			</s:if>
			<s:else>
				<s:property value="auditCategory.name"/>
				<a href="ManageCategory.action?id=<s:property value="auditCategory.id"/>" target="_BLANK">^</a>
			</s:else>
		</td>
	</s:if>
	<s:if test="operatorAccount.corporate" >
		<td class="account<s:property value="operatorAccount.status"/>"><s:property value="operatorAccountLabel"/>
			<s:if test="operatorAccount"><a href="FacilitiesEdit.action?id=<s:property value="operatorAccount.id"/>" target="_BLANK">^</a></s:if>
		</td>
	</s:if>
	<td><s:property value="riskLabel"/></td>
	<td><s:property value="tagLabel"/></td>
	<td><s:property value="acceptsBidsLabel"/></td>
	<s:if test="#showAction">
		<s:if test="class.toString() == 'class com.picsauditing.jpa.entities.AuditTypeRule'">
			<s:set var="rRuleType" value="'ManageAuditTypeRules'" scope="action" />		
		</s:if>
		<s:else>
			<s:set var="rRuleType" value="'ManageCategoryRules'" scope="action"  />
		</s:else>
		<td>
			<s:if test="permissions.hasPermission(#rRuleType, 'Delete')">
				<s:if test="'Similar Rules' == key">
					<a href="?button=merge">Merge</a>
				</s:if>
			</s:if>
			<s:if test="'More Granular' == key || 'Current Rule' == key">
				<s:if test="permissions.hasPermission(#rRuleType, 'Delete')">
					<a class="remove" href="?id=<s:property value="id"/>&button=delete">Delete</a>
				</s:if>
			</s:if>
			<s:if test="'Current Rule' == key">
				<s:if test="permissions.hasPermission(#rRuleType, 'Delete')">
					<br /><a class="remove" href="?id=<s:property value="id"/>&button=deleteChildren">Delete All</a>
					<br /><a class="add" href="?id=<s:property value="id"/>&button=copy">Copy</a>
				</s:if>
				<s:if test="permissionToEdit">
					<br /><a class="edit" href="?id=<s:property value="id"/>&button=edit">Edit</a>
				</s:if>
			</s:if>	
		</td>
	</s:if>
	<s:if test="(!categoryRule && permissions.canEditAuditRules) || (categoryRule && permissions.canEditCategoryRules)">
		<s:if test="isCanEditRule(#r)">
			<td class="center"><a class="remove" href="javascript:deleteRule(<s:property value="#globalOperator.id"/>,<s:property value="id"/>,'<s:if test="categoryRule">category</s:if><s:else>audittype</s:else>');"></a></td>
		</s:if>
		<s:else>
			<td>&nbsp;</td>
		</s:else>
	</s:if>
</tr>
