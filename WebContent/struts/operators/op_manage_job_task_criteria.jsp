<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Manage Job Task Criteria</title>
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
	margin-right: 0px;
}
</style>
<s:include value="../jquery.jsp"/>
</head>
<body>
<s:include value="opHeader.jsp"></s:include>
<table id="criteriaTable">
	<tr>
		<td>
			<s:if test="criterias.size() > 0">
				<h3 style="padding-bottom:10px;"><s:property value="jobTask.label"/>&nbsp;<s:property value="jobTask.name"/></h3>
				<div>
						<table class="jobReport">
							<thead>
								<tr>
									<th>Task Type</th>
									<th>Qualification Type: Method</th>
									<th>Expire Months</th>
									<th>PICS Verifiable</th>
									<th>Status</th>
									<s:if test="canEdit">
										<th>Edit</th>
										<th>Delete</th>
									</s:if>
								</tr>
							</thead>
							<tbody>
								<s:iterator value="jobTask.jobTaskCriteriaMap.keySet()" id="groupNumber" status="group">
								<s:iterator value="jobTask.jobTaskCriteriaMap.get(#groupNumber)" status="criteria">
									<s:if test="!#criteria.first">
										<tr>
											<td></td>
											<td></td>
											<td class="and center" >- AND -</td>
											<td></td>
											<td></td>
											<s:if test="canEdit">
												<td></td>
												<td></td>
											</s:if>
										</tr>
									</s:if>
									<tr>
										<td class="center"><s:property value="jobTask.taskType" /></td>
										<td style="padding:0 20px 0 10px;"><s:property value="assessmentTest.assessmentCenter.name" />&nbsp;<s:property value="assessmentTest.qualificationType" />:&nbsp;<s:property value="assessmentTest.qualificationMethod" /></td>
										<td class="center"><s:property value="assessmentTest.monthsToExpire" /></td>
										<td class="center"><s:if test="assessmentTest.verifiable">Y</s:if><s:else>N</s:else></td>
										<td class="center" style="color:#330099">Active</td>
										<s:if test="canEdit">
											<td class="center">
												<a href="ManageJobTaskCriteria.action?id=<s:property value="operator.id" />&groupNumber=<s:property value="#groupNumber" />&jobTaskID=<s:property value="jobTaskID" />&button=Remove&jobTaskCriteriaID=<s:property value="id" />"
													class="edit"></a>
											</td>
											<td class="center">
												<a href="ManageJobTaskCriteria.action?id=<s:property value="operator.id" />&groupNumber=<s:property value="#groupNumber" />&jobTaskID=<s:property value="jobTaskID" />&button=Remove&jobTaskCriteriaID=<s:property value="id" />"
													class="remove"></a>
											</td>
										</s:if>
									</tr>									
								</s:iterator>
									<s:if test="canEdit">
										<td colspan="7" class="center">
											<s:if test="getUsedAssessmentsByGroup(#groupNumber).size > 0">
												<a onclick="$('#addJobTaskCriteria_<s:property value="#groupNumber"/>').show(); $('#addLink_<s:property value="#groupNumber"/>').hide(); return false;"
													href="#" id="addLink_<s:property value="#groupNumber"/>" class="add">Add New Assessment</a>
												<div id="addJobTaskCriteria_<s:property value="#groupNumber"/>" style="display: none; clear: both;">
													<s:form id="newJobTaskCriteria" method="POST" enctype="multipart/form-data">
														<s:hidden name="jobTaskID" />
														<s:hidden name="id" />
														<s:hidden name="groupNumber" value="%{#groupNumber}"/>
														<fieldset class="form bottom">
															<legend><span>Add New Assessment</span></legend>
															<ol>
																<li><label>Assessments:</label>
																	<s:select 
																		list="getUsedAssessmentsByGroup(#groupNumber)"
																		name="assessmentTestID"
																		listKey="id"
																		listValue="name">
																	</s:select>
																</li>
															</ol>
															<div style="text-align: center; margin: 0px auto;">
																<input type="submit" value="Save" class="picsbutton positive" name="button" />
																<button onclick="$('#addLink_<s:property value="#groupNumber"/>').show(); $('#addJobTaskCriteria_<s:property value="#groupNumber"/>').hide(); return false;"
																	class="picsbutton negative">Cancel</button>
															</div>
														</fieldset>
													</s:form>
												</div>
											</s:if>
											<s:else>
												<legend><span class="error">No remaining assessments for this group</span></legend>
											</s:else>
										</td>
									</s:if>
									<s:if test="!#group.last">
										<tr class="or">
											<td></td>
											<td></td>
											<td class="center">- OR -</td>
											<td></td>
											<td></td>
											<s:if test="canEdit">
												<td></td>
												<td></td>
											</s:if>
										</tr>
									</s:if>
								</s:iterator>
							</tbody>
						</table>
					<div class="clear"></div>
				</div>				
				<a onclick="$('#addJobTaskGroup').show(); $('#addLinkGroup').hide(); return false;"
					href="#" id="addLinkGroup" class="add">Add New Group</a>
				<div id="addJobTaskGroup" style="display: none; clear: both;">
					<s:form id="newJobTaskGroup" method="POST" enctype="multipart/form-data">
						<s:hidden name="jobTaskID" />
						<s:hidden name="id" />
						<fieldset class="form bottom">
							<legend><span>First Assessment in Group</span></legend>
							<ol>
								<li><label>Assessments:</label>
									<s:select 
										list="allAssessments"
										name="assessmentTestID"
										listKey="id"
										listValue="name">
									</s:select>
								</li>
							</ol>
							<div style="text-align: center; margin: 0px auto;">
								<input type="submit" value="Create" class="picsbutton positive" name="button" />
								<button onclick="$('#addLinkGroup').show(); $('#addJobTaskGroup').hide(); return false;"
									class="picsbutton negative">Cancel</button>
							</div>
						</fieldset>
					</s:form>
				</div>
			</s:if>

		</td>
	</tr>
</table>
</body>
</html>
