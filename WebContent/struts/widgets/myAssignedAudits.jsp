<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"  errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report">
	<thead>
	<tr>
	    <td>Contractor</td>
	    <td>Type</td>
	    <td>Status</td>
	    <td>Assigned</td>
	</tr>
	</thead>
	<s:iterator value="newlyAssigned">
		<tr>
			<td><a href="ContractorView.action?id=<s:property value="audit.contractorAccount.id"/>"><s:property value="audit.contractorAccount.name"/></a></td>
			<td><a href="Audit.action?auditID=<s:property value="audit.id"/>"><nobr><s:property value="audit.auditType.name"/></nobr></a></td>
			<td><s:property value="status"/></td>
			<td class="center"><s:date name="audit.assignedDate" format="%{@com.picsauditing.util.PicsDateFormat@Iso}" /></td>
		</tr>
	</s:iterator>
</table>
