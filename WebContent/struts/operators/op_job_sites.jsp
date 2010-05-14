<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Manage Job Sites</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
<style type="text/css">
#sitesTable {
	width: 100%;
}

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

fieldset.form {
	width: auto;
}
</style>
<s:include value="../jquery.jsp"/>
<script type="text/javascript">
function getTasks(siteID) {
	var data = {
		button: 'Tasks',
		siteID: siteID,
		id: <s:property value="operator.id" />
	};

	startThinking({div: 'jobSiteTasks', message: 'Loading tasks'});
	$('#jobSiteTasks').load('ManageProjectsAjax.action', data,
		function() {
			$('#addSiteTasks').empty();
		}
	);
}

function getNewSiteTasks(siteID) {
	var data = {
		button: 'NewTasks',
		siteID: siteID,
		id: <s:property value="operator.id" />
	};

	$('#addTaskLink').hide();
	startThinking({div: 'addSiteTasks', message: 'Loading new tasks'});
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
	startThinking({div: 'editProject', message: 'Loading project'});
	$('#editProject').load('ManageProjectsAjax.action', { button: 'EditSite', siteID: siteID });
}

function getStates(country) {
	$('#loadStates').load('StateListAjax.action',{countryString: country, stateString: '<s:property value="newSite.state.english"/>'});
}

$(function() {
	$('.datepicker').datepicker();
});
</script>
</head>
<body>
<s:include value="opHeader.jsp"></s:include>
<div id="clear" style="width: 100%"></div>
<table id="sitesTable">
	<tr>
		<td>
			<s:if test="activeSites.size() > 0">
				<h3>Active Sites</h3>
				<table class="report">
					<thead>
						<tr><th></th>
							<th>Label</th>
							<th>Site Name</th>
							<th>Tasks</th>
							<pics:permission perm="ManageProjects" type="Edit">
								<th>Edit</th>
								<th>Remove</th>
							</pics:permission>
						</tr>
					</thead>
					<tbody>
						<s:iterator value="activeSites" status="stat" id="site">
							<tr id="<s:property value="#site.id" />">
								<td><s:property value="#stat.count" /></td>
								<td>
									<s:property value="#site.label" />
								</td>
								<td>
									<s:property value="#site.name" />
								</td>
								<td class="center">
									<a href="#" onclick="getTasks(<s:property value="#site.id" />); return false;">View</a>
								</td>
								<pics:permission perm="ManageProjects" type="Edit">
									<td class="center">
										<a href="#" onclick="editSite(<s:property value="#site.id" />); return false;"><img src="images/edit_pencil.png" alt="Edit site" /></a>
									</td>
									<td class="center">
										<a href="ManageProjects.action?id=<s:property value="operator.id" />&button=Remove&siteID=<s:property value="#site.id" />"
											onclick="return confirm('Are you sure you want to remove this job site?');" class="remove"></a>
									</td>
								</pics:permission>
							</tr>
						</s:iterator>
					</tbody>
				</table>
			</s:if>
			<pics:permission perm="ManageProjects" type="Edit">
				<div id="editProject"></div>
				<a onclick="$('#addJobSite').show(); $('#addLink').hide(); return false;"
					href="#" id="addLink" class="add">Add New Job Site</a>
				<div id="addJobSite" style="display: none; clear: both;">
					<s:form id="newJobSite" method="POST" enctype="multipart/form-data" cssStyle="clear: both;">
						<s:hidden name="id" />
						<fieldset class="form bottom">
							<legend><span>Add New Project</span></legend>
							<ol>
								<li><label>Label:</label>
									<s:textfield name="siteLabel" size="20" />
								</li>
								<li><label>Name:</label>
									<s:textfield name="siteName" size="20" />
								</li>
								<li><label>City:</label>
									<s:textfield name="siteCity" size="20" />
								</li>
								<li><label>Country:</label>
									<s:select list="countryList" name="siteCountry.isoCode" listKey="isoCode"
										listValue="name" onchange="getStates(this.value);"></s:select>
								</li>
								<li id="loadStates"></li>
								<li><label>Start Date:</label>
									<s:textfield name="siteStart" size="20" cssClass="datepicker" />
								</li>
								<li><label>End Date:</label>
									<s:textfield name="siteEnd" size="20" cssClass="datepicker" />
								</li>
							</ol>
							<div style="text-align: center; margin: 0px auto;">
								<input type="submit" value="Save" class="picsbutton positive" name="button" />
								<button onclick="$('#addLink').show(); $('#addJobSite').hide(); return false;"
									class="picsbutton negative">Cancel</button>
							</div>
						</fieldset>
					</s:form>
				</div>
			</pics:permission>
		</td>
		<td rowspan="2">
			<div id="jobSiteTasks"></div>
			<pics:permission perm="ManageProjects" type="Edit">
				<div id="addSiteTasks"></div>
			</pics:permission>
		</td>
	</tr>
</table>
<s:if test="inactiveSites.size() > 0">
	<a href="#" onclick="$('#pastSites').show(); $(this).hide(); return false;">Show Details</a>
	<div id="pastSites" style="display: none;">
		<h3>Past Sites</h3>
		<table class="report">
			<thead>
				<tr><th></th>
					<th>Label</th>
					<th>Site Name</th>
					<pics:permission perm="ManageProjects" type="Edit">
						<th>Reactivate</th>
					</pics:permission>
				</tr>
			</thead>
			<tbody>
				<s:iterator value="inactiveSites" status="stat" id="site">
					<tr>
						<td><s:property value="#stat.count" /></td>
						<td><s:property value="#site.label" /></td>
						<td><s:property value="#site.name" /></td>
						<pics:permission perm="ManageProjects" type="Edit">
							<td class="center">
								<a href="ManageProjects.action?id=<s:property value="operator.id" />&button=Reactivate&siteID=<s:property value="#site.id" />"
									class="add"></a>
							</td>
						</pics:permission>
					</tr>
				</s:iterator>
			</tbody>
		</table>
	</div>
</s:if>
</body>
</html>
