<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<div id="${actionName}_${methodName}_page" class="${actionName}-page page">
	<p>
		<s:text name="JS.RequestNewContractor.message.MatchingOnWords" />
	</p>
	<p class="matched-on">
		<s:iterator value="usedTerms" var="used_term">
			${used_term}
		</s:iterator>
	</p>
	<p>
		<s:text name="JS.RequestNewContractor.message.CompanyInSystem" />
	</p>
	<p>
		<s:iterator value="results" var="result">
			<s:if test="worksForOperator || permissions.picsEmployee">
				<s:url action="ContractorView" var="contractor_view">
					<s:param name="id">
						${result.id}
					</s:param>
				</s:url>
				<a href="${contractor_view}">
					${result.name}
				</a>
			</s:if>
			<s:else>
				${result.name}
			</s:else>
			<br />
		</s:iterator>
	</p>
</div>