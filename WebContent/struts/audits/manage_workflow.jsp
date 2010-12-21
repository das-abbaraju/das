<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Manage Audit Workflow</title>
<s:include value="../jquery.jsp" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<script type="text/javascript">
function getSteps(id) {
	$('#workflowSteps').load('ManageAuditWorkFlowAjax.action?button=getSteps&id=' + id);
}

function editStep(stepID, workflowID) {
	var data = $('#'+stepID+' :input').serialize();
	data += '&id='+workflowID+'&stepID='+stepID+'&button=editStep';
	$('#workflowSteps').load('ManageAuditWorkFlowAjax.action', data);
}
function addStep(){
	$('#workflowSteps').load('ManageAuditWorkFlowAjax.action?' + $('#form_steps').serialize());
}

</script>
</head>
<body>
<h1>Manage Workflow</h1>
<table>
	<tr>
		<td>
			<s:if test="workflowList.size() > 0">
				<table class="report">
					<thead>
						<tr>
							<th>Name</th>
						</tr>
					</thead>
					<tbody>
						<s:iterator value="workflowList">
							<tr>
								<td><a href="#" onclick="getSteps(<s:property value="id" />); return false;"><s:property value="name" /></a></td>
							</tr>					
						</s:iterator>
					</tbody>
				</table>
			</s:if>
			<pics:permission perm="ManageAuditWorkFlow" type="Edit">
				<a href="#" onclick="$('#form1').show(); return false;" class="add">Add New Workflow</a>
				<s:form id="form1" cssStyle="display: none;">
					<fieldset class="form">
						<h2 class="formLegend">Add New Workflow</h2>
						<ol>
							<li><label>Name:</label>
								<s:textfield name="name" /></li>
						</ol>
					</fieldset>
					<br clear="all">
					<fieldset class="forms submit">
						<input type="submit" value="Create" name="button" class="picsbutton positive" />
						<input type="button" value="Cancel" onclick="$('#form1').hide(); return false;" class="picsbutton negative" />
					</fieldset>
				</s:form>			
			</pics:permission>
		</td>
		<td style="padding-left: 10px;">
			<div id="workflowSteps"></div>
		</td>
	</tr>
</table>
</body>
</html>
