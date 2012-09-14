<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:set var="countryString" value="countryString" />

<s:if test="getCountrySubdivisionList(#countryString).size() > 0">
    <s:if test="#countryString == 'US'">
        <s:set var="label" value="'CountrySubdivision'" />
    </s:if>
	<s:elseif test="#countryString == 'CA'">
		<s:set var="label" value="'ContractorAccount.province'" />
    </s:elseif>
	<s:elseif test="#countryString == 'GB'">
		<s:set var="label" value="'ContractorAccount.county'" />
    </s:elseif>
    <s:else>
		<s:set var="label" value="'ContractorAccount.countrySubdivision'" />
    </s:else>
    <s:select
        label="%{#label}"
        id="countrySubdivision_sel"
        list="getCountrySubdivisionList(#countryString)"
        cssClass="contractor-countrySubdivision"
        name="%{countrySubdivisionPrefix + (needsSuffix ? '.isoCode' : '')}"
        listKey="isoCode"
        listValue="simpleName"
        theme="pics"
        value="countrySubdivision"
    />
	<s:if test="countrySubdivision == null" >
		<span class="redMain" id="countrySubdivision_req">*</span>
	</s:if>
	<pics:fieldhelp title="Country Subdivision">
		<s:text name="ContractorAccount.countrySubdivision.isoCode.fieldhelp"/>
	</pics:fieldhelp>
</s:if>