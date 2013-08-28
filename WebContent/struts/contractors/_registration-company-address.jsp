<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set name="theme" value="'pics'" scope="page" />
<s:set var="country_subdivision_list_item" value="getCountrySubdivisionList()" />

<li class="address">
	<s:textfield name="contractor.address" />
</li>
<li class="city">
	<s:textfield name="contractor.city" />
</li>
<li class="countrySubdivision" style="${countrySubdivision_display}">
    <s:if test="#country_subdivision_list_item.size() > 0">

            <label for="Registration_contractor_countrySubdivision">
                <s:property value="getCountrySubdivisionLabelFor()" />
            </label>
                <select class="select2 contractor-countrySubdivision" id="Registration_contractor_countrySubdivision" name="countrySubdivision">
                    <option value="">- <s:text name="CountrySubdivisionList.list.select.header" /> -</option>
                    <s:iterator value="getCountrySubdivisionList()" var="country_subdivision_list_item">
                        <s:set var="country_subdivision_selection" value="''" />
                        <s:if test="countrySubdivision != null && countrySubdivision == #country_subdivision_list_item">
                            <s:set var="country_subdivision_selection" value="' selected=\"selected\"'" />
                        </s:if>
                        <option value="${isoCode}"${country_subdivision_selection}>${simpleName}</option>
                    </s:iterator>
                </select>

            <s:fielderror fieldName="countrySubdivision" id="Registration_country_subdivision_error" />
    </s:if>
</li>
<li class="zipcode" style="${zip_display}">
	<s:textfield name="contractor.zip" />
</li>
