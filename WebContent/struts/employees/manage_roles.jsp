<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Define Job Roles</title>
<link rel="stylesheet" type="text/css" media="screen"
	href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen"
	href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen"
	href="css/notes.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen"
	href="css/audit.css?v=<s:property value="version"/>" />
<style type="text/css">
#rolesTable td {
	vertical-align: top;
}

#rolesTable table.report {
	margin-right: 10px;
	margin-bottom: 10px;
}

fieldset.form label {
	width: 5em;
	margin-right: 0px;
}
</style>
<s:include value="../jquery.jsp" />
<script type="text/javascript" src="js/jquery/autocomplete/jquery.autocomplete.min.js"></script>
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/autocomplete/jquery.autocomplete.css" />

<script type="text/javscript" src="js/jquery/jquery.bgiframe.min.js"></script>
<script type="text/javascript">
function removeCompetency(competencyID) {
	$("a.compEditor").hide();
	$('#jobCompetencyList').load('ManageJobRolesAjax.action', {button: 'removeCompetency', 'role.id': <s:property value="role.id" />, competencyID: competencyID});
}

function addCompetency(competencyID) {
	$("a.compEditor").hide();
	$('#jobCompetencyList').load('ManageJobRolesAjax.action', {button: 'addCompetency', 'role.id': <s:property value="role.id" />, competencyID: competencyID});
}

$(function() {
	$('#roleInputBox').autocomplete('RoleSuggestAjax.action',{
			minChars: 1,
			formatResult: function(data,count) {
				return data[0];
			}
		});
});
</script>
</head>
<body>
<h1>Manage Job Roles <span class="sub"> <s:property
	value="subHeading" escape="false" /> </span></h1>

<a href="JobCompetencyMatrix.action?id=<s:property value="account.id" />">HSE Competency Matrix</a>
<table id="rolesTable">
	<tr>
		<td style="vertical-align: top">
		<table class="report">
			<thead>
				<tr>
					<th>Job Role</th>
					<th>Active</th>
				</tr>
			</thead>
			<tbody>
				<s:if test="jobRoles.size > 0">
					<s:iterator value="jobRoles">
						<tr>
							<td><a href="?id=<s:property value="account.id" />&role.id=<s:property value="id" />" <s:if test="!active">class="inactive"</s:if>><s:property value="name" /></a></td>
							<td class="center"><s:if test="active">Y</s:if><s:else>N</s:else></td>
						</tr>
					</s:iterator>
				</s:if>
			</tbody>
		</table>
		<a href="?id=<s:property value="account.id" />&button=Add" class="add">Add New Job Role</a>
		</td>
		<s:if test="role != null">
			<td style="vertical-align: top" id="roleCell"><s:form>
				<s:hidden name="id" />
				<s:hidden name="role.id" />
				<fieldset class="form bottom"><legend><span>Define Role</span></legend>
				<ol>
					<li><label>Role:</label> <s:textfield id="roleInputBox" name="role.name" /></li>
					<li><label>Active:</label> <s:checkbox name="role.active" value="role.active" /> </li>
				</ol>
				<div style="text-align: center; margin: 0px auto;">
					<input type="submit" value="Save" class="picsbutton positive" name="button" />
					<input type="button" onclick="$('#roleCell').empty(); return false;" class="picsbutton" value="Cancel"/>
					<s:if test="role.id != 0">
						<input type="submit" name="button" value="Delete" class="picsbutton negative"
							onclick="return confirm('Press ok to remove this job role. This action cannot be undone.');" />
					</s:if>
				</div>
				</fieldset>
			</s:form>
			<div id="jobCompetencyList">
				<s:if test="role.id != 0">
					<s:include value="manage_roles_competencies.jsp"></s:include>
				</s:if>
			</div>
			</td>
		</s:if>
	</tr>
</table>

</body>
</html>
