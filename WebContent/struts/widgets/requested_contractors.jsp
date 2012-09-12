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
		<s:iterator value="requestedContractors" var="request">
			<tr>
				<td>
					<s:if test="#request.get('systemType') == 'CRR'">
						<s:url action="RequestNewContractor" var="request_new_contractor">
							<s:param name="newContractor">
								${request.get('id')}
							</s:param>
						</s:url>
					</s:if>
					<s:else>
						<s:url action="RequestNewContractorAccount" var="request_new_contractor">
							<s:param name="requestedContractor">
								${request.get('id')}
							</s:param>
							<s:param name="requestRelationship.operatorAccount">
								${request.get('RequestedByID')}
							</s:param>
						</s:url>
					</s:else>
					<a href="${request_new_contractor}">
						${request.get('name')}
					</a>
				</td>
				<td>
					<s:if test="permissions.operator">
						<s:if test="isStringEmpty(#request.get('RequestedByUserOther'))">
							${request.get('RequestedUser')}
						</s:if>
						<s:else>
							${request.get('RequestedByUserOther')}
						</s:else>
					</s:if>
					<s:else>
						${request.get('RequestedBy')}
					</s:else>
				</td>
				<td>
					<nobr>
						<s:date name="get('deadline')" />
					</nobr>
				</td>
				<td class="call">
					<nobr>
						<s:date name="get('lastContactDate')" />
					</nobr>
				</td>
			</tr>
		</s:iterator>
	</s:else>
</table>

<a href="${requested_contractor_report}" class="preview">
	<s:text name="RequestedContractorsAjax.SeeAllOpenRequests" />
</a>