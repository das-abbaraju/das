<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="../../exception_handler.jsp"%>
<html>
<head>
<title>Manage Job Task Criteria</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />

<s:include value="../jquery.jsp" />

<script type="text/javascript" src="js/jquery/dataTables/jquery.dataTables.min.js"></script>
<link rel="stylesheet" href="js/jquery/dataTables/css/dataTables.css"/>

<style type="text/css">
.newValue {
	display: none;
}

table.jobReport tbody td {
	border: none;
}
</style>
<script type="text/javascript">
$(function() {
	$('#tests').dataTable({
		aoColumns: [
	            null,
	            null,
	            null,
	            null,
	            null
			],
		aaSorting: [[2, 'asc']],
		bJQueryUi: true,
		bStateSave: true,
		oLanguage: {
			sSearch:"Search",
			sLengthMenu: '_MENU_', 
			sInfo:"_START_ to _END_ of _TOTAL_",
			sInfoEmpty:"",
			sInfoFiltered:"(filtered from _MAX_)" },
		iDisplayLength: 25
	});

	<s:iterator value="criteriaMap.keySet()" id="groupNumber">
		$('#addTest_<s:property value="#groupNumber" />').dataTable({
			aoColumns: [
		            null,
		            null,
		            null,
		            null,
		            null
				],
			aaSorting: [[2, 'asc']],
			bJQueryUi: true,
			bStateSave: true,
			oLanguage: {
				sSearch:"Search",
				sLengthMenu: '_MENU_', 
				sInfo:"_START_ to _END_ of _TOTAL_",
				sInfoEmpty:"",
				sInfoFiltered:"(filtered from _MAX_)" },
			iDisplayLength: 10
		});
	</s:iterator>
});
</script>
</head>
<body>
<s:include value="opHeader.jsp"></s:include>
<a href="ManageJobTasksOperator.action?id=<s:property value="operator.id" />">Return to Job Tasks List</a>
<table id="criteriaTable">
	<tr>
		<td>
			<s:if test="criterias.size() > 0 && criteriaMap.size() > 0">
				<div>
					<table class="report jobReport">
						<thead>
							<tr>
								<th>Qualification Type: Method</th>
								<th>Since</th>
								<th>Months To Expire</th>
								<th>PICS Verifiable</th>
								<th>Status</th>
								<s:if test="canEdit">
									<th>Delete</th>
								</s:if>
							</tr>
						</thead>
						<tbody>
							<s:iterator value="criteriaMap.keySet()" id="groupNumber" status="group">
								<s:iterator value="criteriaMap.get(#groupNumber)" status="criteria" id="jobTaskCriteria">
									<s:if test="!#criteria.first">
										<tr>
											<td class="and center"<s:if test="canEdit"> colspan="6"</s:if><s:else> colspan="5"</s:else>>- AND -</td>
										</tr>
									</s:if>
									<tr>
										<td style="padding:0 20px 0 10px;"><s:property value="assessmentTest.assessmentCenter.name" />&nbsp;<s:property value="assessmentTest.qualificationType" />:&nbsp;<s:property value="assessmentTest.qualificationMethod" /></td>
										<td class="center"><s:date name="effectiveDate" format="MM/dd/yyyy" /></td>
										<td class="center"><s:property value="assessmentTest.monthsToExpire" /></td>
										<td class="center"><s:if test="assessmentTest.verifiable">Y</s:if><s:else>N</s:else></td>
										<td class="center">
											<s:if test="jobTask.active"><span style="color: #309">Active</span></s:if>
											<s:else><span style="color: #930">Inactive</span></s:else>
										</td>
										<s:if test="canEdit">
											<td class="center">
												<a href="ManageJobTaskCriteria.action?id=<s:property value="operator.id" />&groupNumber=<s:property value="#groupNumber" />&jobTaskID=<s:property value="jobTaskID" />&button=Remove&jobTaskCriteriaID=<s:property value="id" />"
													class="remove" onclick="return confirm('Are you sure you want to remove this task criteria?');"></a>
											</td>
										</s:if>
									</tr>
								</s:iterator>
								<s:if test="canEdit">
									<td colspan="6" class="center">
										<s:if test="getUsedAssessmentsByGroup(#groupNumber).size > 0">
											<a onclick="$('#addJobTaskCriteria_<s:property value="#groupNumber"/>').show(); $('#addLink_<s:property value="#groupNumber"/>').hide(); return false;"
												href="#" id="addLink_<s:property value="#groupNumber"/>" class="add">Add New Assessment</a>
											<div id="addJobTaskCriteria_<s:property value="#groupNumber"/>" style="display: none; clear: both; text-align: left; background-color: #eee;">
												<a href="#" onclick="$('#addJobTaskCriteria_<s:property value="#groupNumber"/>').hide(); $('#addLink_<s:property value="#groupNumber"/>').show(); return false;" class="remove">Cancel</a>
												<table class="report" id="addTest_<s:property value="#groupNumber"/>">
													<thead>
														<tr>
															<th>Add</th>
															<th><a href="#" onclick="return false;">Assessment Center</a></th>
															<th><a href="#" onclick="return false;">Qualification Type</a></th>
															<th><a href="#" onclick="return false;">Qualification Method</a></th>
															<th><a href="#" onclick="return false;">Description</a></th>
														</tr>
													</thead>
													<tbody>
														<s:iterator value="getUsedAssessmentsByGroup(#groupNumber)">
															<tr>
																<td class="center"><a href="?id=<s:property value="operator.id" />&button=Save&jobTaskID=<s:property value="jobTaskID" />&assessmentTestID=<s:property value="id" />&groupNumber=<s:property value="#groupNumber" />" class="add"></td>
																<td><s:property value="assessmentCenter.name" /></td>
																<td><s:property value="qualificationType" /></td>
																<td><s:property value="qualificationMethod" /></td>
																<td><s:property value="description" /></td>
															</tr>
														</s:iterator>
													</tbody>
												</table>
											</div>
										</s:if>
										<s:else>
											<legend><span class="error">No remaining assessments for this group</span></legend>
										</s:else>
									</td>
								</s:if>
								<s:if test="!#group.last">
									<tr class="or">
										<td class="or center"<s:if test="canEdit"> colspan="6"</s:if><s:else> colspan="5"</s:else>>- OR -</td>
									</tr>
								</s:if>
							</s:iterator>
						</tbody>
					</table>
					<div class="clear"></div>
				</div>				
			</s:if>
			<s:else>
				<div class="info">To add new task criteria for <s:property value="jobTask.name" />, click on "Add New Group" and choose the assessment test required for this job task. You will be able to add more assessment tests to the same group, or create a new group of requirements.</div>
			</s:else>
			<s:if test="canEdit">
				<a onclick="$('#addJobTaskGroup').show(); $('#addLinkGroup').hide(); return false;"
					href="#" id="addLinkGroup" class="add">Add New Group</a>
				<div id="addJobTaskGroup" style="display: none; clear: both;">
					<table class="report" id="tests">
						<thead>
							<tr>
								<th>Add</th>
								<th><a href="#" onclick="return false;">Assessment Center</a></th>
								<th><a href="#" onclick="return false;">Qualification Type</a></th>
								<th><a href="#" onclick="return false;">Qualification Method</a></th>
								<th><a href="#" onclick="return false;">Description</a></th>
							</tr>
						</thead>
						<tbody>
							<s:iterator value="allAssessments">
								<tr>
									<td class="center"><a href="?id=<s:property value="operator.id" />&button=Create&jobTaskID=<s:property value="jobTaskID" />&assessmentTestID=<s:property value="id" />" class="add"></td>
									<td><s:property value="assessmentCenter.name" /></td>
									<td><s:property value="qualificationType" /></td>
									<td><s:property value="qualificationMethod" /></td>
									<td><s:property value="description" /></td>
								</tr>
							</s:iterator>
						</tbody>
					</table>
				</div>
			</s:if>
		</td>
	</tr>
</table>
</body>
</html>
