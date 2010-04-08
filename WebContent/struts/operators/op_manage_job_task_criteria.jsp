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
		<td style="width: 300px;">
			<s:if test="criterias.size() > 0">
				<h3>Job Task Criteria</h3>
				<table class="report">
					<thead>
						<tr>
							<th>Center</th>
							<th>Qualification Type</th>
							<th>Qualification Method</th>
							<th>Description</th>
							<th>Effective Date</th>
							<th>Expiration Date</th>
							<th>Verifiable</th>
							<th>Months to Expire</th>
							<s:if test="canEdit">
								<th>Remove</th>
							</s:if>
						</tr>
					</thead>
					<tbody>
						<s:iterator value="criterias" status="stat" id="criteria">
							<tr>
								<td><s:property value="assessmentTest.assessmentCenter.name" /></td>
								<td><s:property value="assessmentTest.qualificationType" /></td>
								<td><s:property value="assessmentTest.qualificationMethod" /></td>
								<td><s:property value="assessmentTest.Description" /></td>
								<td><s:property value="assessmentTest.effectiveDate" /></td>
								<td><s:property value="assessmentTest.expirationDate" /></td>
								<td><s:property value="assessmentTest.verifiable" /></td>
								<td><s:property value="assessmentTest.monthsToExpire" /></td>
								<s:if test="canEdit">
									<td class="center">
										<a href="ManageJobTaskCriteria.action?id=<s:property value="operator.id" />&button=Remove&jobTaskCriteriaID=<s:property value="id" />"
											class="remove"></a>
									</td>
								</s:if>
							</tr>
						</s:iterator>
					</tbody>
				</table>
			</s:if>
			<s:if test="canEdit">
				<a onclick="$('#addJobTaskCriteria').show(); $('#addLink').hide(); return false;"
					href="#" id="addLink" class="add">Add New Task Criteria</a>
				<div id="addJobTaskCriteria" style="display: none; clear: both;">
					<s:form id="newJobTaskCriteria" method="POST" enctype="multipart/form-data">
						<s:hidden name="id" />
						<fieldset class="form bottom">
							<legend><span>Add New Task Criteria</span></legend>
							<ol>
								<li><label>Assessments:</label>
									<s:select name="name" list="allAssessments"
										value="id"></s:select>
								</li>
							</ol>
							<div style="text-align: center; margin: 0px auto;">
								<input type="submit" value="Save" class="picsbutton positive" name="button" />
								<button onclick="$('#addLink').show(); $('#addJobTaskCriteria').hide(); return false;"
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
