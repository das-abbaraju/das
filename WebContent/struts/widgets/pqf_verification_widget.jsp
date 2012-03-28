<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"  errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report">
	<thead>
	<tr>
		<td>Contractor</td>
		<td>Completed</td>
	</tr>
	</thead>
	<s:if test="pqfVerifications.size() > 0">
		<s:iterator value="pqfVerifications" status="stat">
			<tr>
				<td><a href="VerifyView.action?id=<s:property value="get('id')"/>"><s:property value="get('name')"/></a></td>
				<td><s:date name="get('completedDate')" format="%{getText('date.short')}" /></td>
			</tr>
		</s:iterator>
	</s:if>
	<s:else>
		<tr><td colspan="2" class="center"> No PQF verifications currently</td></tr>
	</s:else>
</table>