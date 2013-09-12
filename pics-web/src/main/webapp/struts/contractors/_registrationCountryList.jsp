<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="getCountrySubdivisionList(countryString).size() > 0">
    <label for="Registration_contractor_countrySubdivision">
        <s:property value="getCountrySubdivisionLabelFor(countryString)" />
    </label>

    <select class="contractor-countrySubdivision" id="Registration_contractor_countrySubdivision"
            name="countrySubdivision">
        <option value="">- <s:text name="CountrySubdivisionList.list.select.header" /> -</option>
        <s:iterator value="getCountrySubdivisionList(countryString)">
            <option value="${isoCode}">${simpleName}</option>
        </s:iterator>
    </select>
</s:if>