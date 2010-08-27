<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<thead>
	<tr>
		<td>Include</td>
		<td>Audit Type</td>
		<s:if test="categoryRule"><td>Category</td></s:if>
		<td>Account</td>
		<td>Operator</td>
		<td>Risk</td>
		<td>Tag</td>
		<td>Bid-Only</td>
		<s:if test="auditTypeRule"><td>Dependent Audit Type</td></s:if>
		<s:if test="auditTypeRule"><td>Dependent Audit Status</td></s:if>
		<td>Question</td>
		<td></td>
		<td>Answer</td>
		<s:if test="#showAction">
			<td></td>
		</s:if>
	</tr>
</thead>