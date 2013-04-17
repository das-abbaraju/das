<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:if test="!isStringEmpty(countryString)">
    <s:set var="countryString" value="countryString" />
</s:if>

<s:if test="getCountrySubdivisionList(#countryString).size() > 0">
    <s:select
        cssClass="contractor-countrySubdivision"
        headerKey=""
        headerValue="- %{getText('CountrySubdivisionList.list.select.header')} -"
        id="countrySubdivision_sel"
        label="%{getCountrySubdivisionLabelKeyFor(#countryString)}"
        list="getCountrySubdivisionList(#countryString)"
        listKey="isoCode"
        listValue="simpleName"
        name="%{countrySubdivisionPrefix + (needsSuffix ? '.isoCode' : '')}"
        theme="pics"
        value="accountCountrySubdivision"
    />
	<s:if test="accountCountrySubdivision == null" >
		<span class="redMain" id="countrySubdivision_req">*</span>
	</s:if>
	<pics:fieldhelp title="Country Subdivision">
		<s:text name="ContractorAccount.countrySubdivision.isoCode.fieldhelp"/>
	</pics:fieldhelp>
</s:if>
