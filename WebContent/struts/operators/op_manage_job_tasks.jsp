<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Manage Job Tasks</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
<style type="text/css">
#tasksTable {
	width: 100%;
}

#tasksTable table.report {
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
<table id="tasksTable" >
	<tr>
		<td>
			<s:if test="tasks.size() > 0">
				<h3>Job Tasks</h3>
				<table class="report">
					<thead>
						<tr><th></th>
							<th>Label</th>
							<th>Task Name</th>
							<s:if test="canEdit">
								<th>Manage Task Criteria</th>
								<th>Remove</th>
							</s:if>
						</tr>
					</thead>
					<tbody>
						<s:iterator value="tasks" status="stat" id="task">
							<tr>
								<td><s:property value="#stat.count" /></td>
								<td><s:property value="#task.label" /></td>
								<td><a href="ManageJobTaskCriteria.action?id=<s:property value="operator.id" />&jobTaskID=<s:property value="#task.id" />"
											name="Manage Task Criteria"><s:property value="#task.name" /></a>
								</td>
								<s:if test="canEdit">
									<td class="center">
										<a href="ManageJobTaskCriteria.action?id=<s:property value="operator.id" />&jobTaskID=<s:property value="#task.id" />"
											class="picsbutton positive" name="Manage Task Criteria">Manage Task Criteria</a>
									</td>
									<td class="center">
										<a href="ManageJobTasksOperator.action?id=<s:property value="operator.id" />&button=Remove&jobTaskID=<s:property value="#task.id" />"
											class="remove"></a>
									</td>
								</s:if>
							</tr>
						</s:iterator>
					</tbody>
				</table>
			</s:if>
			<s:if test="canEdit">
				<a onclick="$('#addJobTask').show(); $('#addLink').hide(); return false;"
					href="#" id="addLink" class="add">Add New Job Task</a>
				<div id="addJobTask" style="display: none; clear: both;">
					<s:form id="newJobTask" method="POST" enctype="multipart/form-data">
						<s:hidden name="id" />
						<fieldset class="form bottom" >
							<legend><span>Add New Job Task</span></legend>
							<ol>
								<li><label>Label:</label>
									<s:textfield name="newTask.label" size="10" />
								</li>
								<li><label>Name:</label>
									<s:textfield name="newTask.name" size="40" />
								</li>
							</ol>
							<div style="text-align: center; margin: 0px auto;">
								<input type="submit" value="Save" class="picsbutton positive" name="button" />
								<button onclick="$('#addLink').show(); $('#addJobTask').hide(); return false;"
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
