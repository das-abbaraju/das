<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<title>
	Manage Countries
</title>

<div id="manage_country_page" class="manage-country-page page">
	<h1>
        Manage Countries
	</h1>

    <table id="countries">
        <thead>
        <tr>
            <th>Country</th>
        </tr>
        </thead>
        <s:iterator value="countries" var="country">
            <tr>
                <td><a href="ManageCountries.action?country=<s:property value="#country.isoCode"/>"><s:property value="#country.name"/></a></td>
            </tr>
        </s:iterator>
    </table>
</div>