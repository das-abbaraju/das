<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>

<table class="report">
	<thead>
		<tr>
			<th>
				<s:text name="global.Contractor" />
			</th>
			<s:if test="permissions.admin">
				<th>
					<s:text name="ContractorAccount.requestedBy" />
				</th>
			</s:if>
			<th>
				<s:if test="permissions.admin">
					<s:text name="RegisteredContractorsAjax.DateRegistered" />
				</s:if>
				<s:else>
					<s:text name="RegisteredContractorsAjax.DateAdded" />
				</s:else>
			</th>
		</tr>
	</thead>
	<s:iterator value="newContractors">
		<tr>
			<s:if test="permissions.admin">
				<td>
					<s:url action="ContractorView" var="contractor_view">
						<s:param name="id">
							${id}
						</s:param>
					</s:url>
					<a class="account${status}" href="${contractor_view}">
						${name}
					</a>
				</td>
			</s:if>
			<s:elseif test="permissions.operatorCorporate">
				<td>
					<s:url action="ContractorView" var="contractor_view">
						<s:param name="id">
							${contractorAccount.id}
						</s:param>
					</s:url>
					<a class="account${status}" href="${contractor_view}">
						${contractorAccount.name}
					</a>
				</td>
			</s:elseif>
			<s:if test="permissions.admin">
				<td>
					${requestedBy.name}
				</td>
			</s:if>
			<td class="center">
				<s:date name="creationDate" format="%{getText('date.long')}" />
			</td>
		</tr>
	</s:iterator>
</table>
