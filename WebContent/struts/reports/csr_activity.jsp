<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title><s:text name="ReportCsrActivity.title" /></title>
<s:include value="reportHeader.jsp" />
<link rel="stylesheet" type="text/css" media="screen"
	href="css/forms.css?v=<s:property value="version"/>" /></head>
<body>
<h1><s:text name="ReportCsrActivity.title" /></h1>

<div id="search">
	<s:form id="form1" method="post" cssStyle="background-color: #F4F4F4;">
	<s:hidden name="showPage" value="1" />
	<s:hidden name="startsWith" />
	<s:hidden name="orderBy" />
	<div>
		<button id="searchfilter" type="submit" name="button" value="Search"  class="picsbutton positive"><s:text name="button.Search" /></button>
	</div>
	<s:if test="permissions.hasGroup(981)">
	<div class="filterOption"><a href="#"
		onclick="toggleBox('form1_conAuditorId'); return false;"><s:text name="global.CSR" /></a> = <span
		id="form1_conAuditorId_query"><s:text name="JS.Filters.status.All" /></span><br />
	<span id="form1_conAuditorId_select" style="display: none" class="clearLink">
		<s:select name="csrIds" cssClass="forms" list="csrs" listKey="id" listValue="name" multiple="true" size="5" 
			id="form1_conAuditorId" />
		<br />
	<a class="clearLink" href="#" onclick="clearSelected('form1_conAuditorId'); return false;"><s:text name="Filters.status.Clear" /></a>
	</span></div>
	</s:if>

	<s:text name="ReportCsrActivity.label.Year" />: <s:select list="yearsList"
	cssClass="forms" name="year"/>
	
	<div class="filterOption">
		<a href="#" onclick="showTextBox('form1_filterDate'); return false;"><s:text name="ReportCsrActivity.label.FilterDate" /></a>
		<span id="form1_filterDate_query">= <s:text name="JS.Filters.status.All" /></span><br />
		<span id="form1_filterDate" style="display: none" class="clearLink">
			<s:textfield cssClass="forms datepicker" size="10" id="form1_filterDate1" name="filterDate1" />
			<s:text name="Filters.label.To" />:
			<s:textfield cssClass="forms datepicker" size="10" id="form1_filterDate2" name="filterDate2" />
			<br />
			<a class="clearLink" href="#" onclick="clearTextField('form1_filterDate'); return false;">
				<s:text name="Filters.status.Clear" />
			</a>
		</span>
	</div>
	<br clear="all"/>
	</s:form>
</div>
<br/>
<table class="report">
	<thead>
		<tr>
			<td><s:text name="ReportCsrActivity.label.User" /></td>
			<td><s:text name="ReportCsrActivity.label.AUVerified" /></td>
			<td><s:text name="ReportCsrActivity.label.AURejected" /></td>
			<td><s:text name="ReportCsrActivity.label.PoliciesVerified" /></td>
			<td><s:text name="ReportCsrActivity.label.PoliciesRejected" /></td>
			<td><s:text name="ReportCsrActivity.label.NoteNumber" /></td>
			<td><s:text name="ReportCsrActivity.label.Month" /></td>
		</tr>
	</thead>
	<s:iterator value="data">
		<tr>
			<td><s:property value="get('name')"/></td>
			<td><s:property value="get('AUVerified')"/></td>
			<td><s:property value="get('AURejected')"/></td>
			<td><s:property value="get('InsuranceVerified')"/></td>
			<td><s:property value="get('InsuranceRejected')"/></td>
			<td><s:property value="get('notesCreated')"/></td>
			<td><s:property value="get('month_name')"/></td>	
		</tr>
	</s:iterator>
</table>
</body>
</html>
