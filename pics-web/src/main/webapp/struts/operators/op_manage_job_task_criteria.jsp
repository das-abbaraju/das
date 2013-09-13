<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:text name="%{scope}.title" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />

<s:include value="../jquery.jsp" />

<script type="text/javascript" src="js/jquery/dataTables/jquery.dataTables.min.js?v=${version}"></script>
<link rel="stylesheet" href="js/jquery/dataTables/css/dataTables.css?v=${version}"/>

<style type="text/css">
.newValue {
	display: none;
}

table.jobReport tbody td {
	border: none;
}

.addJobTaskCriteriaDiv {
	display: none;
	clear: both;
	text-align: left;
	background-color: #eee;
}
#addJobTaskGroup {
	display: none;
	clear: both;
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
	
	$('a.unclickable').live('click', function(e) {
		e.preventDefault();
	});
	
	$('.jobReport .remove').live('click', function(e) {
		if ($(this).hasClass('cancel')) {
			e.preventDefault();
			var groupNumber = $(this).closest('div').attr('id').split('_')[1];
			$('#addJobTaskCriteria_' + groupNumber).hide();
			$('#addLink_' + groupNumber).show();
		} else {
			return confirm('<s:text name="%{scope}.confirm.RemoveTaskCriteria" />');
		}
	});
	
	$('.jobReport .add').live('click', function(e) {
		if (!$(this).hasClass('new')) {
			e.preventDefault();
			var groupNumber = $(this).attr('id').split('_')[1];
			$('#addJobTaskCriteria_' + groupNumber).show();
			$('#addLink_' + groupNumber).hide();
		}
	});
	
	$('#addLinkGroup').live('click', function(e) {
		e.preventDefault();
		$('#addJobTaskGroup').show();
		$(this).hide();
	});
	
	$('#addJobTaskGroup .cancel').live('click', function(e) {
		e.preventDefault();
		$('#addJobTaskGroup').hide();
		$('#addLinkGroup').show();
	});
});
</script>
</head>
<body>
<s:include value="opHeader.jsp"></s:include>
<a href="ManageJobTasksOperator.action?operator=<s:property value="operator.id" />">
	&lt;&lt; <s:text name="%{scope}.link.ReturnToJobTasksList" />
</a>
<table id="criteriaTable">
	<tr>
		<td>
			<s:if test="jobTask.jobTaskCriteria.size > 0 && criteriaMap.size > 0">
				<div>
					<table class="report jobReport">
						<thead>
							<tr>
								<th><s:text name="%{scope}.label.QualificationTypeMethod" /></th>
								<th><s:text name="%{scope}.label.Since" /></th>
								<th><s:text name="%{scope}.label.MonthsToExpire" /></th>
								<th><s:text name="%{scope}.label.PicsVerifiable" /></th>
								<th><s:text name="global.Status" /></th>
								<s:if test="canEdit">
									<th><s:text name="button.Delete" /></th>
								</s:if>
							</tr>
						</thead>
						<tbody>
							<s:iterator value="criteriaMap.keySet()" id="groupNumber" status="group">
								<s:iterator value="criteriaMap.get(#groupNumber)" status="criteria" id="jobTaskCriteria">
									<s:if test="!#criteria.first">
										<tr>
											<td class="and center" colspan="<s:property value="canEdit ? 6 : 5" />">
												<s:text name="%{scope}.separator.AND" />
											</td>
										</tr>
									</s:if>
									<tr>
										<td style="padding:0 20px 0 10px;">
											<s:property value="assessmentTest.assessmentCenter.name" />
											<s:property value="assessmentTest.qualificationType" />:
											<s:property value="assessmentTest.qualificationMethod" />
										</td>
										<td class="center"><s:date name="effectiveDate" /></td>
										<td class="center"><s:property value="assessmentTest.monthsToExpire" /></td>
										<td class="center">
											<s:if test="assessmentTest.verifiable"><s:text name="YesNo.Yes" /></s:if>
											<s:else><s:text name="YesNo.No" /></s:else>
										</td>
										<td class="center">
											<s:if test="jobTask.active"><span style="color: #309"><s:text name="JobTask.active" /></span></s:if>
											<s:else><span style="color: #930"><s:text name="JobTask.inactive" /></span></s:else>
										</td>
										<s:if test="canEdit">
											<td class="center">
												<a href="<s:property value="scope" />!remove.action?operator=<s:property value="operator.id" />&groupNumber=<s:property value="#groupNumber" />&jobTask=<s:property value="jobTask.id" />&jobTaskCriteriaID=<s:property value="id" />" class="remove"></a>
											</td>
										</s:if>
									</tr>
								</s:iterator>
								<s:if test="canEdit">
									<td colspan="6" class="center">
										<s:if test="getUsedAssessmentsByGroup(#groupNumber).size > 0">
											<a href="#" id="addLink_<s:property value="#groupNumber"/>" class="add">
												<s:text name="%{scope}.link.AddNewAssessment" />
											</a>
											<div id="addJobTaskCriteria_<s:property value="#groupNumber"/>" class="addJobTaskCriteriaDiv">
												<a href="#" class="remove cancel"><s:text name="button.Cancel" /></a>
												<table class="report" id="addTest_<s:property value="#groupNumber"/>">
													<thead>
														<tr>
															<th><s:text name="button.Add" /></th>
															<th><a href="#" class="unclickable"><s:text name="global.AssessmentCenter" /></a></th>
															<th><a href="#" class="unclickable"><s:text name="AssessmentTest.qualificationType" /></a></th>
															<th><a href="#" class="unclickable"><s:text name="AssessmentTest.qualificationMethod" /></a></th>
															<th><a href="#" class="unclickable"><s:text name="AssessmentTest.description" /></a></th>
														</tr>
													</thead>
													<tbody>
														<s:iterator value="getUsedAssessmentsByGroup(#groupNumber)">
															<tr>
																<td class="center"><a href="<s:property value="scope" />!save.action?operator=<s:property value="operator.id" />&jobTask=<s:property value="jobTask.id" />&assessmentTest=<s:property value="id" />&groupNumber=<s:property value="#groupNumber" />" class="add new"></td>
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
											<legend><span class="error"><s:text name="%{scope}.message.NoRemainingAssessments" /></span></legend>
										</s:else>
									</td>
								</s:if>
								<s:if test="!#group.last">
									<tr class="or">
										<td class="or center" colspan="<s:property value="canEdit ? 6 : 5" />"><s:text name="%{scope}.separator.OR" /></td>
									</tr>
								</s:if>
							</s:iterator>
						</tbody>
					</table>
					<div class="clear"></div>
				</div>				
			</s:if>
			<s:else>
				<div class="info"><s:text name="%{scope}.help.AddNewTaskCriteria"><s:param value="%{jobTask.name}" /></s:text></div>
			</s:else>
			<s:if test="canEdit">
				<a href="#" id="addLinkGroup" class="add"><s:text name="%{scope}.link.AddNewGroup" /></a>
				<div id="addJobTaskGroup">
					<a href="#" class="remove cancel"><s:text name="button.Cancel" /></a>
					<table class="report" id="tests">
						<thead>
							<tr>
								<th><s:text name="button.Add" /></th>
								<th><a href="#" class="unclickable"><s:text name="global.AssessmentCenter" /></a></th>
								<th><a href="#" class="unclickable"><s:text name="AssessmentTest.qualificationType" /></a></th>
								<th><a href="#" class="unclickable"><s:text name="AssessmentTest.qualificationMethod" /></a></th>
								<th><a href="#" class="unclickable"><s:text name="AssessmentTest.description" /></a></th>
							</tr>
						</thead>
						<tbody>
							<s:iterator value="allAssessments">
								<tr>
									<td class="center"><a href="<s:property value="scope" />!create.action?operator=<s:property value="operator.id" />&jobTask=<s:property value="jobTask.id" />&assessmentTest=<s:property value="id" />" class="add"></td>
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
