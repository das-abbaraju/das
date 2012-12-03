<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:text name="RequestNewContractor.message.PotentialMatches" />:
<br />
<s:iterator
	value="potentialMatches"
	id="account">
	<s:if test="#account.contractor">
		<s:if test="permissions.admin || (permissions.operatorCorporate && worksForOperator(#account.id))">
			<s:url action="ContractorView" var="contractor_view">
				<s:param name="id">
					${account.id}
				</s:param>
			</s:url>
			<a href="${contractor_view}">
				${account.name}
			</a>
		</s:if>
		<s:elseif test="searchForNew">
			<s:url action="NewContractorSearch" var="new_contractor_search">
				<s:param name="filter.accountName">
					${account.id}
				</s:param>
				<s:param name="filter.performedBy" value="'Self Performed'" />
				<s:param name="filter.primaryInformation" value="true" />
				<s:param name="filter.tradeInformation" value="true" />
			</s:url>
			<a
				href="${new_contractor_search}">
				${account.name}
			</a>
		</s:elseif>
		<s:else>
			${account.name}
		</s:else>
		<br />
	</s:if>
</s:iterator>
