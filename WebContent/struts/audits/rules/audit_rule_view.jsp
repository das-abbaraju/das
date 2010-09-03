<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<tr class="<s:property value="#ruleclass"/> clickable<s:if test="include"> on</s:if><s:else> off</s:else>" onclick="location.href='<s:property value="#ruleURL"/>?id=<s:property value="#r.id"/>'">
	<td><s:property value="include ? 'Yes' : 'No'"/></td>
	<td><s:property value="auditTypeLabel"/>
		<s:if test="auditType"><a href="ManageAuditType.action?id=<s:property value="auditType.id"/>" target="_BLANK">^</a></s:if>
	</td>
	<s:if test="categoryRule">
		<td><s:property value="auditCategoryLabel"/>
			<s:if test="auditCategory"><a href="ManageCategory.action?id=<s:property value="auditCategory.id"/>" target="_BLANK">^</a></s:if>
		</td>
	</s:if>
	<td><s:property value="contractorTypeLabel"/></td>
	<td class="account<s:property value="operatorAccount.status"/>"><s:property value="operatorAccountLabel"/>
		<s:if test="operatorAccount"><a href="FacilitiesEdit.action?id=<s:property value="operatorAccount.id"/>" target="_BLANK">^</a></s:if>
	</td>
	<td><s:property value="riskLabel"/></td>
	<td><s:property value="tagLabel"/></td>
	<td><s:property value="acceptsBidsLabel"/></td>
	<s:if test="categoryRule == false">
		<td><s:property value="dependentAuditTypeLabel"/>
			<s:if test="dependentAuditType"><a href="ManageAuditType.action?id=<s:property value="dependentAuditType.id"/>" target="_BLANK">^</a></s:if>
		</td>
		<td><s:property value="dependentAuditStatusLabel"/></td>
	</s:if>
	<td><s:property value="questionLabel"/>
		<s:if test="question"><a href="ManageQuestion.action?id=<s:property value="question.id"/>" target="_BLANK">^</a></s:if>
	</td>
	<td><s:property value="questionComparatorLabel"/></td>
	<td><s:property value="questionAnswerLabel"/></td>
	<s:if test="#showAction">
		<td>
			<s:if test="'Similar Rules' == key">
				<a href="?button=merge">Merge</a>
			</s:if>
			<s:if test="'More Granular' == key || 'Current Rule' == key">
				<a class="remove" href="?id=<s:property value="id"/>&button=delete">Delete</a>
			</s:if>
			<s:if test="'Current Rule' == key">
				<br /><a class="remove" href="?id=<s:property value="id"/>&button=deleteChildren">Delete All</a>
				<br /><a class="edit" href="?id=<s:property value="id"/>&button=edit">Edit</a>
				<br /><a class="add" href="?id=<s:property value="id"/>&button=copy">Copy</a>
			</s:if>
		</td>
	</s:if>
</tr>
