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
fieldset.form label {
	width: 5em;
	margin-right: 0px;
}

.newValue {
	display: none;
}

table.report tbody td {
	border: none;
}
</style>
<s:include value="../jquery.jsp"/>
<script type="text/javascript">
function editCriteria(rowID) {
	$('.oldValue').show();
	$('.newValue').hide();
	$('tr#'+rowID+' .oldValue').hide();
	$('tr#'+rowID+' .newValue').show();
}
</script>
</head>
<body>
<s:include value="opHeader.jsp"></s:include>
<table id="criteriaTable">
	<tr>
		<td>
			<s:if test="criterias.size() > 0">
				<h3 style="padding-bottom:10px;"><s:property value="jobTask.label"/>&nbsp;<s:property value="jobTask.name"/></h3>
				<div>
					<table class="report jobReport">
						<thead>
							<tr>
								<th>Task Type</th>
								<th>Qualification Type: Method</th>
								<th>Months To Expire</th>
								<th>PICS Verifiable</th>
								<th>Status</th>
								<pics:permission perm="ManageJobTasks" type="Edit">
									<th>Edit</th>
									<th>Delete</th>
								</pics:permission>
							</tr>
						</thead>
						<tbody>
							<s:iterator value="jobTask.jobTaskCriteriaMap.keySet()" id="groupNumber" status="group">
								<s:iterator value="jobTask.jobTaskCriteriaMap.get(#groupNumber)" status="criteria">
									<s:if test="!#criteria.first">
										<tr>
											<td class="and center"<pics:permission perm="ManageJobTasks" type="Edit"> colspan="7"</pics:permission><pics:permission perm="ManageJobTasks" negativeCheck="true"> colspan="5"</pics:permission>>- AND -</td>
										</tr>
									</s:if>
									<tr id="<s:property value="#groupNumber" />_<s:property value="id" />">
										<td class="center">
											<span class="oldValue"><s:property value="jobTask.taskType" /></span>
											<span class="newValue"><s:select list="#{'L/G':'L/G','L':'L','G':'G'}"></s:select></span>
										</td>
										<td style="padding:0 20px 0 10px;"><s:property value="assessmentTest.assessmentCenter.name" />&nbsp;<s:property value="assessmentTest.qualificationType" />:&nbsp;<s:property value="assessmentTest.qualificationMethod" /></td>
										<td class="center">
											<span class="oldValue"><s:property value="assessmentTest.monthsToExpire" /></span>
											<span class="newValue"><input type="text" size="5" name="monthsToExpire" value="<s:property value="assessmentTest.monthsToExpire" />" /></span>
										</td>
										<td class="center">
											<span class="oldValue"><s:if test="assessmentTest.verifiable">Y</s:if><s:else>N</s:else></span>
											<span class="newValue"><input type="checkbox" name="picsVerifiable" <s:if test="assessmentTest.verifiable">checked="checked"</s:if>/></span>
										</td>
										<td class="center">
											<span class="oldValue"><s:if test="jobTask.active">Active</s:if><s:else>Inactive</s:else></span>
											<span class="newValue"><input type="checkbox" name="active" <s:if test="jobTask.active">checked="checked"</s:if>/></span>
										</td>
										<pics:permission perm="ManageJobTasks" type="Edit">
											<td class="center">
												<a href="#" onclick="editCriteria('<s:property value="#groupNumber" />_<s:property value="id" />'); return false;" class="edit"></a>
											</td>
											<td class="center">
												<a href="ManageJobTaskCriteria.action?id=<s:property value="operator.id" />&groupNumber=<s:property value="#groupNumber" />&jobTaskID=<s:property value="jobTaskID" />&button=Remove&jobTaskCriteriaID=<s:property value="id" />"
													class="remove" onclick="return confirm('Are you sure you want to remove this task criteria?');"></a>
											</td>
										</pics:permission>
									</tr>									
								</s:iterator>
								<pics:permission perm="ManageJobTasks" type="Edit">
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
								</pics:permission>
								<s:if test="!#group.last">
									<tr class="or">
										<td class="center"<pics:permission perm="ManageJobTasks" type="Edit"> colspan="7"</pics:permission><pics:permission perm="ManageJobTasks" negativeCheck="true"> colspan="5"</pics:permission>>- OR -</td>
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
<a href="ManageJobTasksOperator.action?id=<s:property value="operator.id" />">Return to Job Tasks List</a>
</body>
</html>
