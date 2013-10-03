<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<%-- Required Parameters --%>

<% if (request.getParameter("country_iso_code") != null) { %>
    <s:set name="country_iso_code">${param.country_iso_code}</s:set>
<% } %>

<s:set var="subdivision_list" value="getCountrySubdivisionList(#country_iso_code)" />

<% if (request.getParameter("select_name") != null) { %>
    <s:set name="select_name">${param.select_name}</s:set>
<% } %>

<%-- Optional Parameters --%>

<% if (request.getParameter("subdivision_id") != null) { %>
    <s:set name="subdivision_id">${param.subdivision_id}</s:set>
<% } %>

<% if (request.getParameter("selected_subdivision_iso_code") != null) { %>
    <s:set name="selected_subdivision_iso_code">${param.selected_subdivision_iso_code}</s:set>
<% } %>

<%-- It looks like the struts theme attribute is used for styling, validation messages, labeling, etc. --%>
<%-- I've found themes named form, formhelp, pics, simple, and translate, but we only need to worry about formhelp and pics for country subdivisions. --%>

<% if (request.getParameter("select_theme") != null) { %>
    <s:set name="select_theme">${param.select_theme}</s:set>
<% } %>

<% if (request.getParameter("select_css") != null) { %>
    <s:set name="select_css">${param.select_css}</s:set>
<% } %>

<% if (request.getParameter("mark_required") != null && request.getParameter("mark_required").equalsIgnoreCase("true")) { %>
    <s:set name="mark_required" value="true" />
<% } %>

<s:if test="#subdivision_list.size() > 0">
    <s:if test="#select_theme == 'formhelp'">
        <s:select
            label="%{getCountrySubdivisionLabelFor(#country_iso_code)}"
            id="%{subdivision_id}"
            name="%{select_name}"
            list="#subdivision_list"
            listKey="isoCode"
            listValue="simpleName"
            value="%{selected_subdivision_iso_code}"
            theme="%{select_theme}"
            cssClass="%{select_css}"
            required="%{mark_required}"
        />
    </s:if>
    <s:else>
        <s:select
            label="%{getCountrySubdivisionLabelFor(#country_iso_code)}"
            id="%{subdivision_id}"
            name="%{select_name}"
            list="#subdivision_list"
            listKey="isoCode"
            listValue="simpleName"
            value="%{selected_subdivision_iso_code}"
            headerKey=""
            headerValue="- %{getText('CountrySubdivisionList.list.select.header')} -"
            theme="%{select_theme}"
            cssClass="%{select_css}"
        />
        <s:if test="#mark_required">
            <span class="redMain" id="countrySubdivision_req">*</span>
        </s:if>
    </s:else>
</s:if>