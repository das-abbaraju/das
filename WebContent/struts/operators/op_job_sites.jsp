<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title>Manage Projects</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
<style type="text/css">
#sitesTable table.report {
	margin-right: 10px;
	margin-bottom: 10px;
}

fieldset.form label {
	width: 5em;
	margin-right: 5px;
}

.newValue {
	display: none;
}
</style>
<s:include value="../jquery.jsp"/>
<script type="text/javascript">
function getTasks(siteID) {
	$('#jobSiteTasks:hidden').slideDown();
	$('#editProject:visible').slideUp();
	var data = {
		button: 'Tasks',
		siteID: siteID,
		id: <s:property value="operator.id" />
	};

	startThinking({div: 'jobSiteTasks', message: 'Loading tasks', type: 'large'});
	$('#jobSiteTasks').load('ManageProjectsAjax.action', data,
		function() {
			$('#addSiteTasks').empty();
			$('#jobSiteTasks').slideDown();
		}
	);
}

function getNewSiteTasks(siteID) {
	var data = {
		button: 'NewTasks',
		siteID: siteID,
		id: <s:property value="operator.id" />
	};

	$('#addTaskLink').fadeOut();
	startThinking({div: 'addSiteTasks', message: 'Loading new tasks', type: 'large'});
	$('#addSiteTasks').load('ManageProjectsAjax.action', data);
}

function addTask(siteID, taskID) {
	var controlSpan = $('tr#' + taskID).find('input[name=controlSpan]').val();
	var data = {
		button: 'AddTask',
		siteID: siteID,
		taskID: taskID,
		controlSpan: controlSpan,
		id: <s:property value="operator.id" />
	};

	$('#jobSiteTasks').load('ManageProjectsAjax.action', data,
		function() {
			getNewSiteTasks(siteID);
		}
	);
}

function removeTask(siteID, siteTaskID) {
	var remove = confirm('Are you sure you want to remove this task?');

	if (remove) {
		var data = {
			button: 'RemoveTask',
			siteID: siteID,
			siteTaskID: siteTaskID,
			id: <s:property value="operator.id" />
		};
	
		$('#jobSiteTasks').load('ManageProjectsAjax.action', data,
			function() {
				$('#addSiteTasks').empty();
			}
		);
	}

	return remove;
}

function editSite(siteID) {
	$('#addJobSite:visible').slideUp();
	$('#addLink:hidden').fadeIn();
	$('#editProject:hidden').slideDown();
	startThinking({div: 'editProject', message: 'Loading project'});
	$('#editProject').load('ManageProjectsAjax.action',
			{ button: 'EditSite', siteID: siteID, id: <s:property value="operator.id" /> });
}

function getStates(country) {
	$('.loadStates').load('StateListAjax.action',{countryString: country, stateString: '<s:property value="newSite.state.english"/>'});
}

$(function() {
	$('.datepicker').datepicker();
});
</script>
</head>
<body>
<s:include value="opHeader.jsp"></s:include>
<s:if test="history != null">
	<s:form id="historyForm">
		<s:hidden name="id"></s:hidden>
		<div style="display: none">
			View history: <s:select list="history" name="date" value="%{maskDateFormat(date)}" onchange="$('#historyForm').submit();" />
			<br />
		</div>
	</s:form>
</s:if>
<table id="sitesTable">
	<tr>
		<td>
			<h3>Active Projects</h3>
			<table class="report">
				<thead>
					<tr><th></th>
						<th>Short Label</th>
						<th>Description</th>
						<s:if test="canEdit">
							<th>Edit</th>
							<th>Tasks</th>
							<th>Start Date</th>
							<th>End Date</th>
						</s:if>
						<s:else>
							<th>City</th>
							<th>State</th>
							<th>Country</th>
							<th>Start Date</th>
							<th>End Date</th>
						</s:else>
					</tr>
				</thead>
				<tbody>
				<s:if test="activeSites.size() > 0">
					<s:iterator value="activeSites" status="stat" id="site">
						<tr id="<s:property value="#site.id" />">
							<td><s:property value="#stat.count" /></td>
							<td>
								<s:property value="#site.label" />
							</td>
							<td>
								<s:property value="#site.name" />
							</td>
							<s:if test="canEdit">
								<td class="center">
									<a href="#" onclick="editSite(<s:property value="#site.id" />); return false;"><img src="images/edit_pencil.png" alt="Edit project" /></a>
								</td>
								<td class="center">
									<a href="#" onclick="getTasks(<s:property value="#site.id" />); return false;">View</a>
								</td>
								<td><s:date name="#site.projectStart" format="MM/dd/yyyy" /></td>
								<td><s:date name="#site.projectStop" format="MM/dd/yyyy" /></td>
							</s:if>
							<s:else>
								<td><s:property value="#site.city" /></td>
								<td><s:property value="#site.state.english" /></td>
								<td><s:property value="#site.country.isoCode" /></td>
								<td><s:date name="#site.projectStart" format="MM/dd/yyyy" /></td>
								<td><s:date name="#site.projectStop" format="MM/dd/yyyy" /></td>
							</s:else>
						</tr>
					</s:iterator>
					</s:if>
					<!--  -->
					<s:if test="futureSites.size() > 0">
						<s:iterator value="futureSites" status="stat" id="site">
							<tr class="future" id="<s:property value="#site.id" />">
								<td><s:property value="#stat.count + activeSites.size()" /></td>
								<td>
									<s:property value="#site.label" />
								</td>
								<td>
									<s:property value="#site.name" />
								</td>
								<s:if test="canEdit">
									<td class="center">
										<a href="#" onclick="editSite(<s:property value="#site.id" />); return false;"><img src="images/edit_pencil.png" alt="Edit project" /></a>
									</td>
									<td class="center">
										<a href="#" onclick="getTasks(<s:property value="#site.id" />); return false;">View</a>
									</td>
									<td><s:date name="#site.projectStart" format="MM/dd/yyyy" /></td>
									<td><s:date name="#site.projectStop" format="MM/dd/yyyy" /></td>
								</s:if>
								<s:else>
									<td><s:property value="#site.city" /></td>
									<td><s:property value="#site.state.english" /></td>
									<td><s:property value="#site.country.isoCode" /></td>
									<td><s:date name="#site.projectStart" format="MM/dd/yyyy" /></td>
									<td><s:date name="#site.projectStop" format="MM/dd/yyyy" /></td>
								</s:else>
							</tr>
						</s:iterator>
					</s:if>
				</tbody>
			</table>
		</td>
		<td rowspan="2">
			<div id="jobSiteTasks"></div>
			<s:if test="canEdit">
				<div id="addSiteTasks"></div>
			</s:if>
		</td>
	</tr>
</table>
<s:if test="canEdit"><div style="width: 50%;">
	<div id="editProject"></div>
	<a onclick="$('#editProject:visible').hide(); $('#addJobSite').show(); $('#addLink').hide(); return false;"
		href="#" id="addLink" class="add">Add New Project</a>
	<div id="addJobSite" style="display: none; clear: both;">
		<s:form id="newJobSite" method="POST" enctype="multipart/form-data" cssStyle="clear: both;">
			<s:hidden name="id" />
			<fieldset class="form">
				<h2 class="formLegend">Add New Project</h2>
				<ol>
					<li><label>Label<span class="redMain">*</span>:</label>
						<s:textfield name="siteLabel" size="20" maxlength="15" />
					</li>
					<li><label>Name<span class="redMain">*</span>:</label>
						<s:textfield name="siteName" size="20" maxlength="255" />
					</li>
					<li><label>City:</label>
						<s:textfield name="siteCity" size="20" maxlength="30" />
					</li>
					<li><label>Country:</label>
						<s:select list="countryList" name="siteCountry.isoCode" listKey="isoCode"
							headerValue="- Country -" headerKey="" listValue="name"
							onchange="getStates(this.value);"></s:select>
					</li>
					<li class="loadStates"><label>State:</label>
						<s:select list="getStateList('US')" id="state_sel" name="state.isoCode" 
							headerKey="" headerValue="- State -" listKey="isoCode" listValue="name" value="stateString"/>
					</li>
					<li><label>Start Date:</label>
						<s:textfield name="siteStart" size="20" cssClass="datepicker" />
					</li>
					<li><label>End Date:</label>
						<s:textfield name="siteEnd" size="20" cssClass="datepicker" />
					</li>
				</ol>
			</fieldset>
			<fieldset class="form submit">
				<input type="submit" value="Save" class="picsbutton positive" name="button" />
				<button onclick="$('#addLink').show(); $('#addJobSite').hide(); return false;"
					class="picsbutton negative">Cancel</button>
			</fieldset>
		</s:form>
	</div>
</div></s:if>
<s:if test="inactiveSites.size() > 0">
	<div>
		<h3>Past Projects</h3>
		<table class="report">
			<thead>
				<tr><th></th>
					<th>Label</th>
					<th>Site Name</th>
					<th>City</th>
					<th>State</th>
					<th>Country</th>
					<th>Start Date</th>
					<th>End Date</th>
					<s:if test="canEdit">
						<th>Reactivate</th>
					</s:if>
				</tr>
			</thead>
			<tbody>
				<s:iterator value="inactiveSites" status="stat" id="site">
					<tr>
						<td><s:property value="#stat.count" /></td>
						<td><s:property value="#site.label" /></td>
						<td><s:property value="#site.name" /></td>
						<td><s:property value="#site.city" /></td>
						<td><s:property value="#site.state.english" /></td>
						<td><s:property value="#site.country.isoCode" /></td>
						<td class="center"><s:property value="maskDateFormat(#site.projectStart)" /></td>
						<td class="center"><s:property value="maskDateFormat(#site.projectStop)" /></td>
						<s:if test="canEdit">
							<td class="center">
								<a href="ManageProjects.action?id=<s:property value="operator.id" />&button=Reactivate&siteID=<s:property value="#site.id" />"
									class="add"></a>
							</td>
						</s:if>
					</tr>
				</s:iterator>
			</tbody>
		</table>
	</div>
</s:if>
</body>
</html>