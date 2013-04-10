<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:set var="countryString" value="countryString" />

<s:if test="getCountrySubdivisionList(#countryString).size() > 0">
    <s:select
        label="%{getCountrySubdivisionLabelKeyFor(#countryString)}"
        id="countrySubdivision_sel"
        list="getCountrySubdivisionList(#countryString)"
        cssClass="contractor-countrySubdivision"
        name="%{countrySubdivisionPrefix + (needsSuffix ? '.isoCode' : '')}"
        listKey="isoCode"
        listValue="simpleName"
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
