<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<h3>Contractor Count by Region</h3>
<table class="report">
<tr>
	<thead>
	<tr>
	    <td>Country</td>
	    <td>CountrySubdivision</td>
	    <td>Contractor Count</td>
	</tr>
	</thead>
	<s:iterator value="auditedByCountrySubdivision">
	<tr>
		<td><s:property value="get('country')"/></td>
		<td><s:property value="get('countrySubdivision')"/></td>
		<td><s:property value="get('cnt')"/></td>
	</tr>
	</s:iterator>
</tr>
</table>
