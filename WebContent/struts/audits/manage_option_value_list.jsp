<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<table class="report">
	<thead>
		<tr>
			<th><s:text name="AuditOptionValue.number" /></th>
			<th><s:text name="AuditOptionValue.name" /></th>
			<th><s:text name="AuditOptionValue.visible" /></th>
			<th><s:text name="AuditOptionValue.score" /></th>
			<th><s:text name="AuditOptionValue.uniqueCode" /></th>
			<pics:permission perm="ManageAudits" type="Edit">
				<th><s:text name="button.Edit" /></th>
			</pics:permission>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="group.values">
			<tr id="item_<s:property value="id" />">
				<td class="optionNumber right"><s:property value="number" /></td>
				<td class="optionName"><s:property value="name" /></td>
				<td class="center optionVisible"><s:if test="visible"><img src="images/okCheck.gif" /></s:if></td>
				<td class="optionScore right"><s:property value="score" /></td>
				<td class="optionUniqueCode"><s:property value="uniqueCode" /></td>
				<pics:permission perm="ManageAudits" type="Edit">
					<td class="optionEdit"><a href="#value=<s:property value="id"/>" class="edit"></a></td>
				</pics:permission>
			</tr>
		</s:iterator>
		<s:if test="group.values.size == 0">
			<tr>
				<td colspan="7" class="center">No question options found</td>
			</tr>
		</s:if>
	</tbody>
</table>