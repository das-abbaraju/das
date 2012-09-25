<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/layout/_page-header.jsp">
    <s:param name="title">Report Tester</s:param>
</s:include>

<h3><s:property value="report.name"/></h3>

<form> 
	<textarea rows="20" cols="60">
		<s:property value="report.sql"/> 
	</textarea>
</form>

<table id="report.fields">
	<thead>
	<tr>
		<th>Field Name</th>
		<th>Category</th>
		<th>Translation</th>
		<th>Sql</th>
	</tr>
	</thead>
	<s:iterator value="reportElements">
		<tr>
			<td><s:property value="fieldName" /></td>
			<td><s:property value="field.category" /></td>
			<td><s:property value="field.text" /></td>
			<td><s:property value="sql" /></td>
		</tr>
	</s:iterator>
</table>

<h3>Available Fields</h3>

<table id="report.availableFields">
	<thead>
	<tr>
		<th>Field Name</th>
	</tr>
	</thead>
	<s:iterator value="availableFields.values"  id="result">
		<tr>
			<td><s:property value="#result.name" /></td>
		</tr>
	</s:iterator>
</table>
