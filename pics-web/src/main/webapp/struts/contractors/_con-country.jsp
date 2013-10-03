<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:if test="!isStringEmpty(countryString)">
    <s:set var="countryString" value="countryString" />
</s:if>

<s:include value="/struts/contractors/_country_subdivision_select.jsp">
    <s:param name="country_iso_code" value="countryString" />
    <s:param name="subdivision_id">countrySubdivision_sel</s:param>
    <s:param name="select_name" value="countrySubdivisionPrefix + (needsSuffix ? '.isoCode' : '')"></s:param>
    <s:param name="selected_subdivision_iso_code" value="accountCountrySubdivision" />
    <s:param name="select_theme">pics</s:param>
    <s:param name="mark_required" value="required" />
</s:include>