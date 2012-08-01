<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<html>
<head>
<title><s:text name="ManageProjects.title" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
<style type="text/css">
.newValue {
	display: none;
}

#closeAssignTasks {
	clear: both;
}
</style>
<s:include value="../jquery.jsp"/>
<script type="text/javascript">
function getTasks(siteID) {
	$('#jobSiteTasks:hidden').slideDown();
	$('#editProject:visible').slideUp();

	startThinking({div: 'jobSiteTasks', message: translate('JS.ManageProjects.loading.LoadingTasks'), type: 'large'});
	$('#jobSiteTasks').load('ManageProjects!getTasks.action', { jobSite: siteID, operator: <s:property value="operator.id" /> },
		function() {
			$('#addSiteTasks').empty();
			$('#jobSiteTasks').slideDown();
		}
	);
}

function getNewSiteTasks(siteID) {
	$('#addTaskLink').fadeOut();
	startThinking({div: 'addSiteTasks', message: translate('JS.ManageProjects.loading.LoadingNewTasks'), type: 'large'});
	$('#addSiteTasks').load('ManageProjects!newTasks.action', { jobSite: siteID, operator: <s:property value="operator.id" /> });
}

function addTask(siteID, taskID) {
	var controlSpan = $('tr#' + taskID).find('input[name="controlSpan"]').val();
	var data = {
		jobSite: siteID,
		jobTask: taskID,
		controlSpan: controlSpan,
		operator: <s:property value="operator.id" />
	};

	$('#jobSiteTasks').load('ManageProjects!addTask.action', data,
		function() {
			getNewSiteTasks(siteID);
		}
	);
}

function removeTask(siteID, siteTaskID) {
	var remove = confirm(translate('JS.ManageProjects.confirm.RemoveTask'));

	if (remove) {
		var data = {
			jobSite: siteID,
			jobSiteTask: siteTaskID,
			operator: <s:property value="operator.id" />
		};
	
		$('#jobSiteTasks').load('ManageProjects!removeTask.action', data,
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
	startThinking({div: 'editProject', message: translate('JS.ManageProjects.loading.LoadingProject')});
	$('#editProject').load('ManageProjects!editSite.action',
		{ jobSite: siteID, operator: <s:property value="operator.id" /> },
		function () {
			$('.datepicker').datepicker();
		}
	);
}

function getCountrySubdivisions(country) {
	$('.loadCountrySubdivisions').load('CountrySubdivisionListAjax.action',{countryString: country, countrySubdivisionString: '<s:property value="jobSite.countrySubdivision.english"/>'});
}

function addCompany(conID, siteID) {
	var data = {
		operator: <s:property value="operator.id" />,
		contractor: conID,
		jobSite: siteID
	};

	$('#jobSiteTasks').load('ManageProjects!addCompany.action', data);
}

$(function() {
	$('.datepicker').datepicker();
	
	$('#sitesTable').delegate('a.edit', 'click', function(e) {
		e.preventDefault();
		var id = $(this).closest('tr').attr('id').split('_')[1];
		editSite(id);
	});
	
	$('#sitesTable').delegate('a.preview', 'click', function(e) {
		e.preventDefault();
		var id = $(this).closest('tr').attr('id').split('_')[1];
		getTasks(id);
	});
	
	$('#sitesTable').delegate('.cancelButton', 'click', function(e) {
		e.preventDefault();
		$('#addLink').show();
		$('#addJobSite').hide();
		$('#editJobSite').hide();
	});
	
	$('#addLink').live('click', function(e) {
		e.preventDefault();
		$('#editProject:visible').hide();
		$('#addJobSite').show();
		$('#addLink').hide();
	});
	
	$('#jobSiteTasks').delegate('.addTaskLink', 'click', function(e) {
		e.preventDefault();
		var siteID = $(this).attr('id').split('_')[1];
		getNewSiteTasks(siteID);
		$('#addSiteTasks:hidden').slideDown();
	});
	
	$('#jobSiteTasks').delegate('.removeTask', 'click', function(e) {
		e.preventDefault();
		var ids = $(this).attr('id').split('_');
		removeTask(ids[1], ids[2]);
	});
	
	$('#jobSiteTasks').delegate('#closeTasks', 'click', function(e) {
		e.preventDefault();
		$('#jobSiteTasks:visible').slideUp();
		$('#addSiteTasks:visible').slideUp();
	});
	
	$('#jobSiteTasks').delegate('#addCompany', 'change', function(e) {
		var siteID = $(this).closest('table').attr('id').split('_')[1];
		addCompany($(this).val(), siteID);
	});
	
	$('#addSiteTasks').delegate('.add', 'click', function(e) {
		e.preventDefault();
		var ids = $(this).attr('id').split('_');
		addTask(ids[1], ids[2]);
	});
	
	$('#addSiteTasks').delegate('#closeAssignTasks', 'click', function(e) {
		e.preventDefault();
		$('#addSiteTasks:visible').slideUp();
		$('#addTaskLink:hidden').fadeIn();
	});
	
	$('#editProject').delegate('#removeSiteButton', 'click', function(e) {
		return confirm(translate('JS.ManageProjects.confirm.RemoveProject'));
	});
});
</script>
</head>
<body>
<s:include value="opHeader.jsp"></s:include>

<table id="sitesTable">
	<tr>
		<td style="padding-right: 10px;">
			<h3><s:text name="ManageProjects.label.Projects" /></h3>
			<h4><s:text name="global.Active" /></h4>
			<table class="report">
				<thead>
					<tr><th></th>
						<th><s:text name="JobSite.label" /></th>
						<th><s:text name="JobSite.name" /></th>
						<s:if test="canEdit">
							<th><s:text name="button.Edit" /></th>
							<th><s:text name="ManageProjects.label.TasksAndCompanies" /></th>
							<th><s:text name="JobSite.projectStart" /></th>
							<th><s:text name="JobSite.projectStop" /></th>
						</s:if>
						<s:else>
							<th><s:text name="global.City" /></th>
							<th><s:text name="CountrySubdivision" /></th>
							<th><s:text name="Country" /></th>
							<th><s:text name="JobSite.projectStart" /></th>
							<th><s:text name="JobSite.projectStop" /></th>
						</s:else>
					</tr>
				</thead>
				<tbody>
					<s:if test="activeSites.size() > 0">
						<s:iterator value="activeSites" status="stat" id="site">
							<tr id="edit_<s:property value="#site.id" />">
								<td><s:property value="#stat.count" /></td>
								<td>
									<s:property value="#site.label" />
								</td>
								<td>
									<s:property value="#site.name" />
								</td>
								<s:if test="canEdit">
									<td class="center">
										<a href="#" class="edit" title="<s:text name="ManageProjects.help.EditProject" />"></a>
									</td>
									<td class="center">
										<a href="#" class="preview" title="<s:text name="button.View" />"></a>
									</td>
									<td><s:date name="#site.projectStart" /></td>
									<td><s:date name="#site.projectStop" /></td>
								</s:if>
								<s:else>
									<td><s:property value="#site.city" /></td>
									<td><s:property value="#site.countrySubdivision.english" /></td>
									<td><s:property value="#site.country.isoCode" /></td>
									<td><s:date name="#site.projectStart" /></td>
									<td><s:date name="#site.projectStop" /></td>
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
										<a href="#" class="edit" title="Edit Project"></a>
									</td>
									<td class="center">
										<a href="#" class="preview" title="<s:text name="button.View" />"></a>
									</td>
									<td><s:date name="#site.projectStart" /></td>
									<td><s:date name="#site.projectStop" /></td>
								</s:if>
								<s:else>
									<td><s:property value="#site.city" /></td>
									<td><s:property value="#site.countrySubdivision.english" /></td>
									<td><s:property value="#site.country.isoCode" /></td>
									<td><s:date name="#site.projectStart" /></td>
									<td><s:date name="#site.projectStop" /></td>
								</s:else>
							</tr>
						</s:iterator>
					</s:if>
					<s:if test="(activeSites.size + futureSites.size) == 0">
						<tr>
							<td colspan="<s:property value="canEdit ? 7 : 8" />"><s:text name="ManageProjects.message.NoSites" /></td>
						</tr>
					</s:if>
				</tbody>
			</table>
			<s:if test="canEdit">
				<div id="editProject"></div>
				<a href="#" id="addLink" class="add"><s:text name="ManageProjects.link.AddNewProject" /></a>
				<div id="addJobSite" style="display: none; clear: both;">
					<s:form id="newJobSite" method="POST" enctype="multipart/form-data" cssStyle="clear: both;">
						<s:hidden name="operator" />
						<fieldset class="form">
							<h2 class="formLegend"><s:text name="ManageProjects.link.AddNewProject" /></h2>
							<ol>
								<li><label><s:text name="JobSite.label" /><span class="redMain">*</span>:</label>
									<s:textfield name="siteLabel" size="20" maxlength="15" />
								</li>
								<li><label><s:text name="JobSite.name" /><span class="redMain">*</span>:</label>
									<s:textfield name="siteName" size="20" maxlength="255" />
								</li>
								<li><label><s:text name="global.City" />:</label>
									<s:textfield name="siteCity" size="20" maxlength="30" />
								</li>
								<li><label><s:text name="Country" />:</label>
									<s:select list="countryList" name="siteCountry.isoCode" listKey="isoCode"
										headerValue="- Country -" headerKey="" listValue="name"
										onchange="getCountrySubdivisions(this.value);"></s:select>
								</li>
								<li class="loadCountrySubdivisions"><label><s:text name="CountrySubdivision" />:</label>
									<s:select list="getCountrySubdivisionList('US')" id="countrySubdivision_sel" name="countrySubdivision.isoCode" 
										headerKey="" headerValue="- Country Subdivision -" listKey="isoCode" listValue="name" value="countrySubdivisionString"/>
								</li>
								<li><label><s:text name="JobSite.projectStart" />:</label>
									<s:textfield name="siteStart" size="20" cssClass="datepicker" />
								</li>
								<li><label><s:text name="JobSite.projectStop" />:</label>
									<s:textfield name="siteEnd" size="20" cssClass="datepicker" />
								</li>
							</ol>
						</fieldset>
						<fieldset class="form submit">
							<s:submit method="save" value="%{getText('button.Save')}" cssClass="picsbutton positive" />
							<input type="button" class="picsbutton cancelButton" value="<s:text name="button.Cancel" />" />
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
	</tr>
</table>
<s:if test="inactiveSites.size() > 0">
	<div>
		<h4><s:text name="ManageProjects.label.PastProjects" /></h4>
		<table class="report">
			<thead>
				<tr><th></th>
					<th><s:text name="JobSite.label" /></th>
					<th><s:text name="JobSite.name" /></th>
					<th><s:text name="global.City" /></th>
					<th><s:text name="CountrySubdivision" /></th>
					<th><s:text name="Country" /></th>
					<th><s:text name="JobSite.projectStart" /></th>
					<th><s:text name="JobSite.projectStop" /></th>
					<s:if test="canEdit">
						<th><s:text name="ManageProjects.label.Reactivate" /></th>
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
						<td><s:property value="#site.countrySubdivision.english" /></td>
						<td><s:property value="#site.country.isoCode" /></td>
						<td class="center"><s:property value="maskDateFormat(#site.projectStart)" /></td>
						<td class="center"><s:property value="maskDateFormat(#site.projectStop)" /></td>
						<s:if test="canEdit">
							<td class="center">
								<a href="ManageProjects!reactivate.action?operator=<s:property value="operator.id" />&jobSite=<s:property value="#site.id" />" class="add"></a>
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