<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>

<%-- URLS --%>
<s:url action="ReportNewRequestedContractor" var="requested_contractor_report" />

<table class="report" id="requestedContractor">
	<thead>
		<tr>
			<td>
				<s:text name="RequestedContractorsAjax.header.RequestedContractor" />
			</td>
			<td>
				<s:text name="RequestedContractorsAjax.header.RequestedBy" />
			</td>
			<td>
				<s:text name="RequestedContractorsAjax.header.Deadline" />
			</td>
			<td>
				<s:text name="RequestedContractorsAjax.header.LastContacted" />
			</td>
		</tr>
	</thead>
	<s:if test="requestedContractors.size  == 0">
		<tr>
			<td colspan="4">
				<s:text name="RequestedContractorsAjax.message.NoOpenRequests" />
			</td>
		</tr>
	</s:if>
	<s:else>
		<s:iterator value="requestedContractors">
			<tr>
				<td>
					<s:url action="RequestNewContractor.action" var="request_new_contractor">
						<s:param name="newContractor">
							${id}
						</s:param>
					</s:url>
					<a href="${request_new_contractor}">
						${name}
					</a>
				</td>
				<td>
					<s:if test="permissions.operator">
						${requestedByUserString}
					</s:if>
					<s:else>
						${requestedBy.name}
					</s:else>
				</td>
				<td>
					<nobr>
						<s:date name="deadline" />
					</nobr>
				</td>
				<td class="call">
					<nobr>
						<s:date name="lastContactDate" />
					</nobr>
				</td>
			</tr>
		</s:iterator>
	</s:else>
</table>

<a href="${requested_contractor_report}" class="preview">
	<s:text name="RequestedContractorsAjax.SeeAllOpenRequests" />
</a>