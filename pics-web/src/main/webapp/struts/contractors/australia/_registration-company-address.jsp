<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<% if (request.getParameter("country_iso_code") != null) { %>
    <s:set name="country_iso_code">${param.country_iso_code}</s:set>
<% } %>

<s:set name="theme" value="'pics'" scope="page" />
<s:set var="country_subdivision_list_item" value="getCountrySubdivisionList()" />

<li class="address">
	<s:textfield name="contractor.address" />
</li>
<li class="address">
    <s:textfield name="contractor.address2" cssClass="no-label"/>
</li>
<li class="city">
	<s:textfield name="contractor.city" />
</li>
<li class="countrySubdivision" style="${countrySubdivision_display}">
    <s:include value="/struts/contractors/_country_subdivision_select.jsp">
        <s:param name="country_iso_code">${country_iso_code}</s:param>
        <s:param name="subdivision_id">Registration_contractor_countrySubdivision</s:param>
        <s:param name="select_name">countrySubdivision</s:param>
        <s:param name="select_theme">pics</s:param>
        <s:param name="select_css">select2</s:param>
    </s:include>
</li>
<li class="zipcode" style="${zip_display}">
	<s:textfield name="contractor.zip" />
</li>
