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
$(function() {
	$('.showWorkflow').live('click',function(){
		$('#workflowEdit').load('ManageAuditWorkFlowAjax.action', function(){
			$('#workflowEdit').show();
			$('#workflowSteps').hide();
		});
	});
	
	$('.showAddStep').live('click', function(){
		$('#form_steps').show();
	});
	
	$('.closeEdit').live('click',function(){
		$('#workflowEdit').hide();
	});
	
	$('.editWorkflow').live('click',function(){
		var id = $(this).closest('tr').attr('id');
		$('#workflowEdit').load('ManageAuditWorkFlowAjax.action?id=' + id, function(){
			$('#workflowEdit').show();
			$('#workflowSteps').hide();
		});
	});
	
	$('.deleteStep').live('click', function(){
		loadData(this, 'deleteStep');
	});
	
	$('.editStep').live('click', function(){
		loadData(this, 'editStep');
	});
	
	$('.loadSteps').live('click', function(){
		var workflowID = $(this).closest('tr').attr('id');
		$('#workflowSteps').load('ManageAuditWorkFlowAjax.action?button=getSteps&id=' + workflowID);	
		$('#workflowEdit').hide();
		$('#workflowSteps').show();	
	});

	$('.addStep').live('click', function(){
		$('#workflowSteps').load('ManageAuditWorkFlowAjax.action?' + $('#form_steps').serialize());
	});
});
function loadData(that, action){
		var stepID = $(that).closest('tr').attr('id').replace('step_', '');
		var data = $('#step_'+stepID+' :input').serialize();
		var workflowID = $('[name=workflowID]').val();
		data += '&id='+workflowID+'&stepID='+stepID+'&button='+action;
		$('#workflowSteps').load('ManageAuditWorkFlowAjax.action', data);	
}
</script>
<style type="text/css">
#workflowEdit{
	display: none;
}
</style>
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
							<th>Edit</th>
						</tr>
					</thead>
					<tbody>
						<s:iterator value="workflowList">
							<tr class="workflowList" id="<s:property value="id" />">
								<td>
									<a href="#" class="loadSteps"><s:property value="name" /></a>						
								</td>
								<td>
									<a class="edit showPointer editWorkflow"></a>
								</td>
							</tr>					
						</s:iterator>
					</tbody>
				</table>
			</s:if>
			<pics:permission perm="ManageAuditWorkFlow" type="Edit">
				<a href="#" class="add showWorkflow">Add New Workflow</a>
			</pics:permission>
		</td>
		<td style="padding-left: 10px;">
			<div id="workflowSteps"></div>
			<div id="workflowEdit">
				<s:include value="manage_workflow_edit.jsp" />
			</div>
		</td>
	</tr>
</table>
</body>
</html>
