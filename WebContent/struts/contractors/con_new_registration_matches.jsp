<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
Potential Matches:<br />
<s:iterator value="potentialMatches" id="account">
	<s:if test="#account.contractor">
		<s:if test="permissions.admin || (permissions.operatorCorporate && worksForOperator(#account.id))">
			<a href="ContractorView.action?id=<s:property value="#account.id" />">
				<s:property value="#account.name" /></a>
		</s:if>
		<s:elseif test="searchForNew">
			<a href="NewContractorSearch.action?filter.accountName=<s:property value="#account.id" />&filter.performedBy=Self%20Performed&filter.primaryInformation=true&filter.tradeInformation=true">
				<s:property value="#account.name" /></a>
		</s:elseif>
		<s:else><s:property value="#account.name" /></s:else>
		<br />
	</s:if>
</s:iterator>
