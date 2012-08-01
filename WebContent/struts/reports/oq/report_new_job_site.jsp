<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:text name="%{scope}.title" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<s:include value="../reportHeader.jsp"/>
<script type="text/javascript">
function getEmployees(jobSiteID) {
	startThinking({div: "currentEmployees", message: translate('JS.ReportNewProjects.message.LoadingEmployees') });
	$('#currentEmployees').load("ReportNewProjects!employees.action", { jobSite: jobSiteID });

	return false;
}

function addEmployee(jobSiteID, employeeID) {
	startThinking({div: "currentEmployees", message: translate('JS.ReportNewProjects.message.AddingEmployee') });
	$('#currentEmployees').load("ReportNewProjects!addEmployee.action", { jobSite: jobSiteID, employee: employeeID });

	return false;
}

function removeEmployee(jobSiteID, employeeID) {
	var del = confirm(translate('JS.ReportNewProjects.confirm.RemoveEmployeeFromProject'));

	if (del) {
		startThinking({div: "currentEmployees", message: translate('JS.ReportNewProjects.message.RemovingEmployee') });
		$('#currentEmployees').load("ReportNewProjects!removeEmployee.action", { jobSite: jobSiteID, employee: employeeID });
	}

	return false;
}

$(function() {
	$('#existingProjects a.remove').live('click', function() {
		return confirm(translate('JS.ReportNewProjects.confirm.RemoveProject'));
	});
	
	$('#existingProjects a.preview').live('click', function(e) {
		e.preventDefault();
		var siteID = $(this).closest('tr').attr('id').split('_');
		getEmployees(siteID[1]);
	});
	
	$('#selectEmployee').live('change', function() {
		var employeeID = $(this).val();
		var jobSiteID = $(this).closest('table').attr('id').split('_');
		
		addEmployee(jobSiteID[1], employeeID);
	});
});
</script>
</head>
<body>
	<h1><s:property value="contractor.name" /><span class="sub"><s:text name="%{scope}.title" /></span></h1>
	<div id="search">
		<s:form id="form1" action="%{filter.destinationAction}">
			<s:hidden name="filter.ajax" />
			<s:hidden name="filter.destinationAction" />
			<s:hidden name="showPage" value="1" />
			<s:hidden name="orderBy" />
		
			<div>
				<button id="searchfilter" type="submit" name="button" value="Search"
					onclick="checkCountrySubdivisionAndCountry('form1_state','form1_country'); return clickSearch('form1');"
					class="picsbutton positive"><s:text name="button.Search" /></button>
			</div>
		
			<div class="filterOption">
				<s:text name="global.CompanyName" />: <s:textfield name="filter.name" cssClass="forms" size="18" onfocus="clearText(this)" />
			</div>
			
			<div class="filterOption">
				<s:text name="global.City" />: <s:textfield name="filter.city" cssClass="forms" size="18" onfocus="clearText(this)" />
			</div>
		
			<div class="filterOption">
				<a href="#" onclick="toggleBox('form1_state'); return false;"><s:text name="CountrySubdivision" /></a> = 
				<span id="form1_state_query"><s:text name="JS.Filters.status.All" /></span><br />
				<span id="form1_state_select" style="display: none" class="clearLink">
					<s:select id="form1_state" name="filter.countrySubdivision" list="filter.countrySubdivisionList" listKey="isoCode" 
						listValue="name" cssClass="forms" multiple="true" size="15" /><br />
					<a class="clearLink" href="#" onclick="clearSelected('form1_state'); return false;">
						<s:text name="Filters.status.Clear" />
					</a>
				</span>
			</div>
		
			<div class="filterOption">
				<a href="#" onclick="toggleBox('form1_country'); return false;"><s:text name="Country" /></a> =
				<span id="form1_country_query"><s:text name="JS.Filters.status.All" /></span><br />
				<span id="form1_country_select" style="display: none" class="clearLink">
					<s:select id="form1_country" name="filter.country" list="filter.countryList" listKey="isoCode"
						listValue="name" cssClass="forms" multiple="true" size="15" /><br />
					<a class="clearLink" href="#" onclick="clearSelected('form1_country'); return false;">
						<s:text name="Filters.status.Clear" />
					</a>
				</span>
			</div>
			
			<div class="filterOption">
				<a href="#" onclick="toggleBox('form1_start'); return false;"><s:text name="Filters.label.Start" /></a> =
				<span id="form1_start_query"><s:text name="JS.Filters.status.All" /></span><br />
				<span id="form1_start_select" style="display: none" class="clearLink">
					<s:textfield cssClass="forms datepicker" size="10" id="form1_start" 
						name="filter.start" />
					<br />
					<a class="clearLink" href="#" onclick="clearTextField('form1_start'); return false;">
						<s:text name="Filters.status.Clear" />
					</a>
				</span>
			</div>
		
			<div class="filterOption">
				<a href="#" onclick="toggleBox('form1_operator'); return false;"><s:text name="global.Operators" /></a> =
				<span id="form1_operator_query"><s:text name="JS.Filters.status.All" /></span>
				<br />
				<span id="form1_operator_select" style="display: none" class="clearLink">
					<s:select id="form1_operator" list="filter.operatorList" cssClass="forms"
						name="filter.operator" listKey="id" listValue="name" multiple="true"
						size="%{filter.operatorList.size() < 25 ? filter.operatorList.size() : 25}" />
					<br />
					<a class="clearLink" href="#" onclick="clearSelected('form1_operator'); return false;">
						<s:text name="Filters.status.Clear" />
					</a>
				</span>
			</div>
		</s:form>
		
		<div class="clear"></div>
	</div>
	<div class="info"><s:text name="%{scope}.help.OperatorAddedAutomatically" /></div>
	<table class="report">
		<thead>
			<tr>
				<th><s:text name="global.Operator" /></th>
				<th><s:text name="%{scope}.label.ProjectName" /></th>
				<th><s:text name="%{scope}.label.Start" /></th>
				<th><s:text name="%{scope}.label.Location" /></th>
				<th><s:text name="button.Add" /></th>
			</tr>
		</thead>
		<tbody>
			<s:if test="data.size > 0">
				<s:iterator value="data" var="d">
					<tr>
						<td><s:property value="#d.get('operatorName')" /></td>
						<td><s:property value="#d.get('name')" /></td>
						<td><s:date name="#d.get('projectStart')" format="%{getText('date.short')}" /></td>
						<td><s:property value="getAddress(#d)" /></td>
						<td class="center">
							<a href="ReportNewProjects!add.action?id=<s:property value="contractor.id" />&jobSite=<s:property value="#d.get('id')" />" class="add"></a>
						</td>
					</tr>
				</s:iterator>
			</s:if>
			<s:else>
				<tr><td colspan="5"><s:text name="Report.message.NoRowsFound" /></td></tr>
			</s:else>
		</tbody>
	</table>
	<s:include value="../../actionMessages.jsp"/>
	<s:if test="current.size > 0">
		<table>
			<tr>
				<td>
					<h3><s:text name="%{scope}.label.ManageProjects" /></h3>
					<table class="report" id="existingProjects">
						<thead>
							<tr>
								<th><s:text name="global.Operator" /></th>
								<th><s:text name="%{scope}.label.ProjectName" /></th>
								<th><s:text name="%{scope}.label.Start" /></th>
								<th><s:text name="%{scope}.label.Location" /></th>
								<th><s:text name="global.Employees" /></th>
								<th><s:text name="button.Remove" /></th>
							</tr>
						</thead>
						<tbody>
							<s:iterator value="current">
								<tr id="jobSite_<s:property value="id" />">
									<td><s:property value="operator.name" /></td>
									<td><s:property value="name" /></td>
									<td><s:date name="projectStart" format="%{getText('date.short')}" /></td>
									<td><s:property value="location" /></td>
									<td class="center"><a href="#" class="preview"></a></td>
									<td class="center"><a href="ReportNewProjects!remove.action?jobSite=<s:property value="id" />" class="remove"></a></td>
								</tr>
							</s:iterator>
						</tbody>
					</table>
				</td>
				<td style="padding-left: 20px;"><div id="currentEmployees"></div></td>
			</tr>
		</table>
	</s:if>
</body>
</html>