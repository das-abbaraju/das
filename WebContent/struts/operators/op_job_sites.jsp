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
</style>
<s:include value="../jquery.jsp"/>
<script type="text/javascript">
function getTasks(siteID) {
	var data = {
		button: 'Tasks',
		siteID: siteID,
		id: <s:property value="operator.id" />
	};

	$('#jobSiteTasks').load('ManageJobSitesAjax.action', data,
		function() {
			$('#taskEmployees').empty();
			$('#addSiteTasks').empty();
		}
	);
}

function getEmployees(siteTaskID) {
	var data = {
		button: 'Employees',
		siteTaskID: siteTaskID,
		id: <s:property value="operator.id" />
	};

	$('#taskEmployees').load('ManageJobSitesAjax.action', data);
}

function getNewSiteTasks(siteID) {
	var data = {
		button: 'NewTasks',
		siteID: siteID,
		id: <s:property value="operator.id" />
	};

	$('#addSiteTasks').load('ManageJobSitesAjax.action', data, 
		function() {
			$('#addTaskLink').hide();
		}
	);
}

function addTask(siteID, taskID) {
	var data = {
		button: 'AddTask',
		siteID: siteID,
		taskID: taskID,
		id: <s:property value="operator.id" />
	};

	$('#jobSiteTasks').load('ManageJobSitesAjax.action', data,
		function() {
			$('#taskEmployees').empty();
			$('#addSiteTasks').empty();
		}
	);
}

function removeTask(siteID, siteTaskID) {
	var data = {
		button: 'RemoveTask',
		siteID: siteID,
		siteTaskID: siteTaskID,
		id: <s:property value="operator.id" />
	};

	$('#jobSiteTasks').load('ManageJobSitesAjax.action', data,
		function() {
			$('#taskEmployees').empty();
			$('#addSiteTasks').empty();
		}
	);
}
</script>
</head>
<body>
<s:include value="opHeader.jsp"></s:include>
<div id="clear" style="width: 100%"></div>
<table id="sitesTable">
	<tr>
		<td style="width: 300px;">
			<s:if test="activeSites.size() > 0">
				<h3>Active Sites</h3>
				<table class="report">
					<thead>
						<tr><th></th>
							<th>Label</th>
							<th>Site Name</th>
							<s:if test="canEdit">
								<th>Remove</th>
							</s:if>
						</tr>
					</thead>
					<tbody>
						<s:iterator value="activeSites" status="stat" id="site">
							<tr>
								<td><s:property value="#stat.count" /></td>
								<td><s:property value="#site.label" /></td>
								<td><a href="#" onclick="getTasks(<s:property value="#site.id" />); return false;">
									<s:property value="#site.name" /></a>
								</td>
								<s:if test="canEdit">
									<td class="center">
										<a href="ManageJobSites.action?id=<s:property value="operator.id" />&button=Remove&siteID=<s:property value="#site.id" />"
											class="remove"></a>
									</td>
								</s:if>
							</tr>
						</s:iterator>
					</tbody>
				</table>
			</s:if>
			<s:if test="canEdit">
				<a onclick="$('#addJobSite').show(); $('#addLink').hide(); return false;"
					href="#" id="addLink" class="add">Add New Job Site</a>
				<div id="addJobSite" style="display: none; clear: both;">
					<s:form id="newJobSite" method="POST" enctype="multipart/form-data" cssStyle="clear: both;">
						<s:hidden name="id" />
						<fieldset class="form bottom">
							<legend><span>Add New Job Site</span></legend>
							<ol>
								<li><label>Label:</label>
									<s:textfield name="newSite.label" size="20" />
								</li>
								<li><label>Name:</label>
									<s:textfield name="newSite.name" size="20" />
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
			</s:if>
		</td>
		<td rowspan="2">
			<div id="jobSiteTasks"></div>
			<s:if test="canEdit">
				<div id="addSiteTasks"></div>
			</s:if>
		</td>
		<td rowspan="2" style="width: 300px;">
			<div id="taskEmployees"></div>
		</td>
	</tr>
</table>
<a href="#" onclick="$('#notes').show(); $(this).hide(); return false;">Show Details</a>
<div id="notes" style="display: none;">
	<s:if test="inactiveSites.size() > 0">
		<h3>Past Sites</h3>
		<table class="report">
			<thead>
				<tr><th></th>
					<th>Label</th>
					<th>Site Name</th>
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
						<s:if test="canEdit">
							<td class="center">
								<a href="ManageJobSites.action?id=<s:property value="operator.id" />&button=Reactivate&siteID=<s:property value="#site.id" />"
									class="add"></a>
							</td>
						</s:if>
					</tr>
				</s:iterator>
			</tbody>
		</table>
	</s:if>
	<s:include value="../notes/account_notes_embed.jsp"></s:include>
</div>
</body>
</html>
