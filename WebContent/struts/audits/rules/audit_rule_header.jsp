<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<thead>
	<tr>
		<td>View</td>
		<td>Include</td>
		<td>Priority</td>
		<td>Audit Type</td>
		<s:if test="categoryRule">
			<td>Category</td>
		</s:if>
		<td>Operator</td>
		<td>Risk</td>
		<td>Tag</td>
		<td>Bid-Only</td>
		<s:if test="#showAction">
			<td>Actions</td>
		</s:if>
		<s:if test="permissions.canEditAuditRules || permissions.canEditCategoryRules">
			<td>Delete</td>
		</s:if>
	</tr>
</thead>