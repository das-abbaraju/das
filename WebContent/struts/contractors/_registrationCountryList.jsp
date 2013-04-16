<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="getCountrySubdivisionList(countryString).size() > 0">
	<s:select
		label="%{getCountrySubdivisionLabelKeyFor(countryString)}"
		id="Registration_contractor_countrySubdivision"
		list="getCountrySubdivisionList(countryString)" 
		cssClass="contractor-countrySubdivision"
		name="countrySubdivision" 
		listKey="isoCode" 
		listValue="simpleName"
		theme="pics"
	/>
</s:if>