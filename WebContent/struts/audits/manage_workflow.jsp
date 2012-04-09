<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<head>
	<title>Manage Audit Workflow</title>
	<s:include value="../jquery.jsp" />
	<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
	<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
	<style type="text/css">
		#workflow_edit{
			display: none;
		}
		.workflow_has_reqs{
			padding-left: 10px;
			font-size: small;
			color: gray;
		}
	</style>
</head>
<body>
	<div id="${actionName}-page">
		<h1>Manage Workflow</h1>
		
		<s:include value="../config_environment.jsp" />
		
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
											<a class="loadSteps showPointer"><s:property value="name" /></a>	
											<div class="workflow_has_reqs"><s:if test="hasRequirements">Has Requirements</s:if></div>					
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
						<a href="javascript:;" class="add showWorkflow">Add New Workflow</a>
					</pics:permission>
				</td>
				<td style="padding-left: 10px;">
					<div id="workflowSteps"></div>
					<div id="workflow_edit">
						<s:include value="manage_workflow_edit.jsp" />
					</div>
				</td>
			</tr>
		</table>
	</div>
	<script type="text/javascript">
		$(function() {  
		    <s:if test="id > 0">
		        $('tr.workflowList#<s:property value="id" /> a.loadSteps').click();
		    </s:if>
		});
	</script>
</body>