<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Manage Job Sites</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=<s:property value="version"/>" />
<style type="text/css">
#sitesTable table.report {
	margin-right: 10px;
	margin-bottom: 10px;
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

	$('#jobSiteTasks').load('ManageJobSitesAjax.action', data);
}
</script>
</head>
<body>
<s:include value="opHeader.jsp"></s:include>
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
		</td>
		<td>
			<div id="jobSiteTasks"></div>
		</td>
	</tr>
	<tr>
		<td colspan="2">
			<s:if test="canEdit">
				<a onclick="$('#addJobSite').show(); $('#addLink').hide(); return false;"
					href="#" id="addLink" class="add">Add New Job Site</a>
				<div id="addJobSite" style="display: none; clear: both;">
					<s:form id="newJobSite" method="POST" enctype="multipart/form-data">
						<s:hidden name="id" />
						<fieldset class="form bottom">
							<legend><span>Add New Job Site</span></legend>
							<ol>
								<li><label>Label:</label>
									<s:textfield name="newSite.label" size="35" />
								</li>
								<li><label>Name:</label>
									<s:textfield name="newSite.name" size="35" />
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
		</td>
	</tr>
</table>

<div style="margin-top: 20px;"><s:include value="../notes/account_notes_embed.jsp"></s:include></div>
</body>
</html>
