<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:if test="getCountrySubdivisionList(countryString).size() > 0">
	<label>
		<s:if test="countryString == 'CA'">
			<s:text name="global.Province"/>:
		</s:if>
		<s:else>
			<s:text name="CountrySubdivision"/>:
		</s:else>
	</label>
	<s:select list="getCountrySubdivisionList(countryString)"
		id="countrySubdivision_sel"
		name="%{countrySubdivisionPrefix + (needsSuffix ? '.isoCode' : '')}"
		listKey="isoCode" listValue="simpleName" value="countrySubdivisionString"
		headerKey=""
		headerValue="- %{countryString == 'CA' ? getText('global.Province') : getText('CountrySubdivision')} -"
	/>
	<s:if test="countrySubdivisionString.length() < 1 || required" >
		<span class="redMain" id="countrySubdivision_req">*</span>
	</s:if>
	<pics:fieldhelp title="Country Subdivision">
		<s:text name="ContractorAccount.countrySubdivision.isoCode.fieldhelp"/>
	</pics:fieldhelp>
</s:if>