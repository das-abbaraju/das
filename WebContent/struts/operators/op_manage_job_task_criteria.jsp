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
.newValue {
	display: none;
}

table.report tbody td {
	border: none;
}
</style>
<s:include value="../jquery.jsp"/>
<script type="text/javascript">
function getHistory(date) {
	self.location = 'ManageJobTaskCriteria.action?id=' + <s:property value="operator.id" />
		+ '&jobTaskID=' + <s:property value="jobTaskID" /> + '&date=' + date;
}
</script>
</head>
<body>
<s:include value="opHeader.jsp"></s:include>
<a href="ManageJobTasksOperator.action?id=<s:property value="operator.id" />">Return to Job Tasks List</a>
<table id="criteriaTable">
	<tr>
		<td>
			<s:if test="criterias.size() > 0 && criteriaMap.size() > 0">
				<s:if test="history != null">
					Effective On: <s:select list="history" name="date" onchange="getHistory(this.value);"></s:select><br />
					<a href="ManageJobTaskCriteria.action?id=<s:property value="operator.id" />&jobTaskID=<s:property value="jobTaskID" />">View Today</a>
				</s:if>
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
					<s:form id="newJobTaskGroup" method="POST" enctype="multipart/form-data">
						<s:hidden name="jobTaskID" />
						<s:hidden name="id" />
						<fieldset class="form bottom">
							<legend><span>First Assessment in Group</span></legend>
							<ol>
								<li><label>Assessments:</label>
									<s:select list="allAssessments"	name="assessmentTestID"	listKey="id"
										listValue="name"></s:select>
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
