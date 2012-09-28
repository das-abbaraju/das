<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"  errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report">
	<thead>
		<tr>
			<td>Contractor</td>
			<td>Type</td>
			<td>Comp%</td>
			<td>Submitted</td>
		</tr>
	</thead>
	<s:if test="submittedAudits.size() > 0">
		<s:iterator value="submittedAudits" status="stat">
			<tr>
				<td><a
					href="ContractorView.action?id=<s:property value="get('id')"/>"><s:property
					value="get('name')" /></a></td>
				<td><a
					href="Audit.action?auditID=<s:property value="get('auditID')"/>"><s:text
					name="%{get('atype.name')}" /></a></td>
				<td class="right"><s:property value="get('percentComplete')" />%</td>
				<td class="center"><s:date name="get('statusChangedDate')"
					format="%{@com.picsauditing.util.PicsDateFormat@Iso}" /></td>
			</tr>
		</s:iterator>
	</s:if>
	<s:else>
		<tr>
			<td colspan="2" class="center">No outstanding submitted audits
			currently</td>
		</tr>
	</s:else>
</table>