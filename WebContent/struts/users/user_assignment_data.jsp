<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<h3>Contractor Count by Region</h3>
<table class="report">
<tr>
	<thead>
	<tr>
	    <td>Country</td>
	    <td>State</td>
	    <td>Contractor Count</td>
	</tr>
	</thead>
	<s:iterator value="auditedByState">
	<tr>
		<td><s:property value="get('country')"/></td>
		<td><s:property value="get('state')"/></td>
		<td><s:property value="get('cnt')"/></td>
	</tr>
	</s:iterator>
</tr>
</table>
