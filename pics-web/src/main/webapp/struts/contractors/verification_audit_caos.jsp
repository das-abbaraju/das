<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>

<%@ include file="_reject_and_approve_all_cao_buttons.jsp" %>

<s:if test="caos.keySet().size > 0">
	<table class="report">
		<thead>	
			<tr>
				<th>Operators</th>
				<th>Status</th>
				<th>Action</th>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="caos">
				<tr>
					<td><s:property value="key.name" /></td>
					<td>
						<s:if test="value.audit.auditFor.length() > 0"><s:property value="value.audit.auditFor" />: </s:if><s:property value="value.status" />
					</td>
					<td>
						<s:iterator value="getCurrentCaoStep(value.id)">
							<a href="#" onclick="return changeAuditStatus(<s:property value="conAudit.id" />,'<s:property value="newStatus" />',$(this).text(),<s:property value="value.id" />);"><div class="button <s:property value="newStatus.color" />"><s:property value="newStatus.button" /></div></a>
						</s:iterator>
					</td>
				</tr>
			</s:iterator>
		</tbody>
	</table>
</s:if>