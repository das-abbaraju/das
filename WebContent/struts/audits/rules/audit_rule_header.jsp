<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<thead>
	<tr>
		<td>Include</td>
		<td>Priority</td>
		<td>Audit Type</td>
		<s:if test="categoryRule">
			<td>Category</td>
		</s:if>
		<td>Contractor Type</td>
		<td>Operator</td>
		<td>Risk</td>
		<td>Tag</td>
		<td>Bid-Only</td>
		<s:if test="!categoryRule">
			<td colspan="2">Dependent Audit</td>
		</s:if>
		<td colspan="3">Question</td>
		<s:if test="#showAction">
			<td>Actions</td>
		</s:if>
	</tr>
</thead>