<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<% if (request.getParameter("country_iso_code") != null) { %>
    <s:set name="country_iso_code">${param.country_iso_code}</s:set>
<% } %>

<% if (request.getParameter("country_subdivision_iso_code") != null) { %>
    <s:set name="country_subdivision_iso_code">${param.country_subdivision_iso_code}</s:set>
<% } %>

<s:set name="theme" value="'pics'" scope="page" />

<li class="address">
	<s:textfield name="registrationForm.address" label="ContractorAccount.address"/>
</li>
<li class="city">
	<s:textfield name="registrationForm.city" label="ContractorAccount.city"/>
</li>
<li class="countrySubdivision">
    <s:include value="/struts/contractors/_country_subdivision_select.jsp">
        <s:param name="country_iso_code">${country_iso_code}</s:param>
        <s:param name="subdivision_id">Registration_contractor_countrySubdivision</s:param>
        <s:param name="selected_subdivision_iso_code">${country_subdivision_iso_code}</s:param>
        <s:param name="select_name">registrationForm.countrySubdivision</s:param>
        <s:param name="select_theme">pics</s:param>
        <s:param name="select_css">select2</s:param>
    </s:include>
</li>

<s:if test="#country_iso_code != 'AE'">
    <li class="zipcode">
        <s:if test="#country_iso_code == 'US'">
            <s:textfield name="registrationForm.zip" label="global.ZipPostalCode" />
        </s:if>
        <s:else>
            <s:textfield name="registrationForm.zip" label="ContractorAccount.zip" />
        </s:else>
    </li>
</s:if>
