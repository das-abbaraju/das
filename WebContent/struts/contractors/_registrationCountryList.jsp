<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="getCountrySubdivisionList(countryString).size() > 0">
	<s:if test="countryString == 'CA'">
		<s:set var="label" value="'ContractorAccount.province'" />
	</s:if>
	<s:elseif test="countryString == 'GB'">
		<s:set var="label" value="'ContractorAccount.county'" />
	</s:elseif>
	<s:else>
		<s:set var="label" value="'ContractorAccount.countrySubdivision'" />
	</s:else>
	
	<s:select
		label="%{#label}"
		id="Registration_contractor_countrySubdivision"
		list="getCountrySubdivisionList(countryString)" 
		cssClass="contractor-countrySubdivision"
		name="countrySubdivision" 
		listKey="isoCode" 
		listValue="simpleName"
		theme="pics"
	/>
</s:if>