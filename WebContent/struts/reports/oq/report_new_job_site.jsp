<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Manage/Find New Projects</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<s:include value="../reportHeader.jsp"/>
<script type="text/javascript">
function getEmployees(jobSiteID) {
	var data = {
		button: "employees",
		jobSiteID: jobSiteID
	};

	startThinking({div: "currentEmployees", message: "Loading employees" });
	$('#currentEmployees').load("ReportNewProjectsAjax.action", data);

	return false;
}

function removeEmployee(jobSiteID, employeeID) {
	var del = confirm("Are you sure you want to remove this employee from the project?");

	if (del) {
		var data = {
			button: "removeEmployee",
			jobSiteID: jobSiteID,
			employeeID: employeeID
		};
	
		startThinking({div: "currentEmployees", message: "Removing employee" });
		$('#currentEmployees').load("ReportNewProjectsAjax.action", data);
	}

	return false;
}

function addEmployee(jobSiteID, employeeID) {
	var data = {
		button: "addEmployee",
		jobSiteID: jobSiteID,
		employeeID: employeeID
	};

	startThinking({div: "currentEmployees", message: "Adding employee" });
	$('#currentEmployees').load("ReportNewProjectsAjax.action", data);

	return false;
}
</script>
</head>
<body>
	<h1><s:property value="account.name" /><span class="sub">Manage/Find New Projects</span></h1>
	<div id="search">
		<s:form id="form1" action="%{filter.destinationAction}">
			<s:hidden name="filter.ajax" />
			<s:hidden name="filter.destinationAction" />
			<s:hidden name="showPage" value="1" />
			<s:hidden name="orderBy" />
		
			<div>
				<button id="searchfilter" type="submit" name="button" value="Search"
					onclick="checkStateAndCountry('form1_state','form1_country'); return clickSearch('form1');"
					class="picsbutton positive">Search</button>
			</div>
		
			<div class="filterOption">
				Name = <s:textfield name="filter.name" cssClass="forms" size="18" onfocus="clearText(this)" />
			</div>
			
			<div class="filterOption">
				City = <s:textfield name="filter.city" cssClass="forms" size="18" onfocus="clearText(this)" />
			</div>
		
			<div class="filterOption">
				<a href="#" onclick="toggleBox('form1_state'); return false;">State</a> = 
				<span id="form1_state_query">ALL</span><br />
				<span id="form1_state_select" style="display: none" class="clearLink">
					<s:select id="form1_state" name="filter.state" list="filter.stateList" listKey="isoCode" 
						listValue="name" cssClass="forms" multiple="true" size="15" /><br />
					<script type="text/javascript">updateQuery('form1_state');</script>
					<a class="clearLink" href="#" onclick="clearSelected('form1_state'); return false;">Clear</a>
				</span>
			</div>
		
			<div class="filterOption">
				<a href="#" onclick="toggleBox('form1_country'); return false;">Country</a> =
				<span id="form1_country_query">ALL</span><br />
				<span id="form1_country_select" style="display: none" class="clearLink">
					<s:select id="form1_country" name="filter.country" list="filter.countryList" listKey="isoCode"
						listValue="name" cssClass="forms" multiple="true" size="15" /><br />
					<script type="text/javascript">updateQuery('form1_country');</script>
					<a class="clearLink" href="#" onclick="clearSelected('form1_country'); return false;">Clear</a>
				</span>
			</div>
			
			<div class="filterOption">
				<a href="#" onclick="toggleBox('form1_start'); return false;">Start</a> =
				<span id="form1_start_query">ALL</span><br />
				<span id="form1_start_select" style="display: none" class="clearLink">
					<s:textfield cssClass="forms datepicker" size="10" id="form1_start" 
						name="filter.start" />
					<script type="text/javascript">updateQuery('form1_start');</script>
					<br />
					<a class="clearLink" href="#" onclick="clearTextField('form1_start'); return false;">Clear</a>
				</span>
			</div>
		
			<div class="filterOption">
				<a href="#" onclick="toggleBox('form1_operator'); return false;">Operators</a> =
				<span id="form1_operator_query">ALL</span>
				<br />
				<span id="form1_operator_select" style="display: none" class="clearLink">
					<s:select id="form1_operator" list="filter.operatorList" cssClass="forms"
						name="filter.operator" listKey="id" listValue="name" multiple="true"
						size="%{filter.operatorList.size() < 25 ? filter.operatorList.size() : 25}" />
					<script type="text/javascript">updateQuery('form1_operator');</script>
					<br />
					<a class="clearLink" href="#" onclick="clearSelected('form1_operator'); return false;">Clear</a>
				</span>
			</div>
		</s:form>
		
		<div class="clear"></div>
	</div>
	<div class="info">If you are not associated with an added project's operator, the operator will automatically be added to your facilities.</div>
	<table class="report">
		<thead>
			<tr>
				<th>Operator</th>
				<th>Project Name</th>
				<th>Start</th>
				<th>Location</th>
				<th>Add</th>
			</tr>
		</thead>
		<tbody>
			<s:if test="data.size > 0">
				<s:iterator value="data" var="d">
					<tr>
						<td><s:property value="#d.get('operatorName')" /></td>
						<td><s:property value="#d.get('name')" /></td>
						<td><s:date name="#d.get('projectStart')" format="M/d/yyyy" /></td>
						<td><s:property value="getAddress(#d)" /></td>
						<td class="center">
							<a href="?id=<s:property value="id" />&button=Add&jobSiteID=<s:property value="#d.get('id')" />" class="add"></a>
						</td>
					</tr>
				</s:iterator>
			</s:if>
			<s:else>
				<tr><td colspan="5">No projects found.</td></tr>
			</s:else>
		</tbody>
	</table>
	<s:include value="../../actionMessages.jsp"/>
	<s:if test="current.size > 0">
		<table>
			<tr>
				<td>
					<h3>Manage Projects</h3>
					<table class="report">
						<thead>
							<tr>
								<th>Operator</th>
								<th>Project Name</th>
								<th>Start</th>
								<th>Location</th>
								<th>Employees</th>
								<th>Remove</th>
							</tr>
						</thead>
						<tbody>
							<s:iterator value="current">
								<tr>
									<td><s:property value="operator.name" /></td>
									<td><s:property value="name" /></td>
									<td><s:date name="projectStart" format="M/d/yyyy" /></td>
									<td><s:property value="location" /></td>
									<td class="center"><a href="#" onclick="return getEmployees(<s:property value="id" />);">View</a></td>
									<td class="center"><a href="?button=Remove&jobSiteID=<s:property value="id" />" class="remove"></a></td>
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