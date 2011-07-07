<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title><s:property value="subHeading" escape="false" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/autocomplete/jquery.autocomplete.css" />
<style type="text/css">
#roleForm {
	clear: right;
}

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
<script type="text/javascript">
var accountID = '<s:property value="account.id" />';

function removeCompetency(competencyID, roleID) {
	$("a.compEditor").hide();
	$('#jobCompetencyList').load('ManageJobRoles!removeCompetency.action', {role: roleID, competencyID: competencyID});
}

function addCompetency(competencyID, roleID) {
	$("a.compEditor").hide();
	$('#jobCompetencyList').load('ManageJobRoles!addCompetency.action', {role: roleID, competencyID: competencyID});
}

function getRole(roleID) {
	$('#roleCell').load('ManageJobRoles!get.action', { role: roleID, id: accountID });
}

$(function() {
	$('#roleInputBox').autocomplete('RoleSuggestAjax.action',{
		minChars: 1,
		formatItem: function(data,i,count) {
			return data[1];
		}
	});
	
	$('#roleCell').delegate('.removeCompetency', 'click', function(e) {
		e.preventDefault();
		var competencyID = $(this).closest('tr').attr('id');
		var roleID = $(this).closest('table').attr('id');
		removeCompetency(competencyID, roleID);
	});
	
	$('#roleCell').delegate('.addCompetency', 'click', function(e) {
		e.preventDefault();
		var competencyID = $(this).closest('tr').attr('id');
		var roleID = $(this).closest('table').attr('id');
		addCompetency(competencyID, roleID);
	});
	
	$('#addLink').click(function(e) {
		e.preventDefault();
		$('#roleCell').load('ManageJobRoles!get.action', { role: 0 });
	});
	
	$('#cancelButton').click(function(e) {
		e.preventDefault();
		$('#roleCell').empty();
	});
	
	$('#deleteButton').click(function() {
		return confirm('<s:text name="%{scope}.confirm.RemoveJobRole" />');
	});
	
	$(window).bind('hashchange', function() {
		startThinking({div: 'roleCell', message: '<s:text name="%{scope}.message.LoadingJobRole" />'});
		getRole(location.hash.substring(1));
	});
});
</script>
</head>
<body>

<s:if test="auditID > 0">
	<div class="info">
		<s:text name="%{scope}.message.AuditHelp">
			<s:param>
				<s:iterator value="shellOps" status="stat">
					<s:property value="name" /><s:if test="#stat.count < (shellOps.size - 1)">,</s:if><s:if test="#stat.count == (shellOps.size - 1)"> <s:text name="global.And" /></s:if>
				</s:iterator>
			</s:param>
		</s:text>
		<br />
		<a href="Audit.action?auditID=<s:property value="auditID" />"><s:text name="Audit.link.ReturnToHSESAAudit" /></a>
	</div>
</s:if>

<h1><s:property value="account.name" /><span class="sub"><s:property value="subHeading" escape="false" /></span></h1>

<s:include value="../actionMessages.jsp" />

<table id="rolesTable">
	<tr>
		<td style="vertical-align: top; padding-right: 10px;">
			<s:if test="jobRoles.size > 0">
				<table class="report">
					<thead>
						<tr>
							<th><s:text name="%{scope}.label.JobRole" /></th>
							<th><s:text name="%{scope}.label.Active" /></th>
						</tr>
					</thead>
					<tbody>
						<s:iterator value="jobRoles">
							<tr>
								<td><a href="#<s:property value="id" />" <s:if test="!active">class="inactive"</s:if>><s:property value="name" /></a></td>
								<td class="center"><s:if test="active"><s:text name="YesNo.Yes" /></s:if><s:else><s:text name="YesNo.No" /></s:else></td>
							</tr>
						</s:iterator>
					</tbody>
				</table>
			</s:if>
			<a href="#" id="addLink" class="add"><s:text name="%{scope}.link.AddNewJobRole" /></a>
		</td>
		<td style="vertical-align: top">
			<div id="roleCell"></div>
		</td>
	</tr>
</table>

</body>
</html>
