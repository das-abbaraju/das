<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title>Manage Job Tasks</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
<style type="text/css">
.newValue {
	display: none;
	line-height: 25px;
}
</style>
<s:include value="../jquery.jsp" />
<s:include value="../reports/reportHeader.jsp" />
<script type="text/javascript">
function editTask(jobTaskID) {
	if ($('tr#'+jobTaskID+' .newValue').is(':visible')) {
		$('tr#'+jobTaskID+' .oldValue').show();
		$('tr#'+jobTaskID+' .newValue').hide();
	} else {
		$('.oldValue').show();
		$('.newValue').hide();
		$('tr#'+jobTaskID+' .oldValue').hide();
		$('tr#'+jobTaskID+' .newValue').show();
	}
}

function saveEdit(jobTaskID) {
	var url = 'ManageJobTasksOperator.action?id=' + <s:property value="operator.id" /> + '&button=Edit&jobTaskID='
		+ jobTaskID + '&' + $('tr#'+jobTaskID+' .newValue input, tr#'+jobTaskID+' .newValue select').serialize();
	
	self.location = url;
}
</script>
</head>
<body>
<s:include value="opHeader.jsp"></s:include>

<div id="search">
<s:form id="form1" action="%{filter.destinationAction}">
	<s:hidden name="filter.ajax" />
	<s:hidden name="filter.destinationAction" />
	<s:hidden name="showPage" value="1" />
	<s:hidden name="orderBy" />
	<s:hidden name="id" value="%{operator.id}" />
	
	<div>
		<button id="searchfilter" type="submit" name="button" value="Search"
			onclick="return clickSearch('form1');" class="picsbutton positive">Search</button>
	</div>
	<div class="filterOption">
		Label: <s:textfield name="filter.label" size="5" onclick="clearText(this)" />
	</div>
	<div class="filterOption">
		Task Name: <s:textfield name="filter.name" />
	</div>
	<div class="filterOption">
		<label><s:checkbox name="filter.active" /> Show Active</label>
	</div>
	<div class="filterOption">
		<a href="#" onclick="toggleBox('form1_taskType'); return false;">Task Type</a> =
		<span id="form1_taskType_query">ALL</span>
		<br />
		<span id="form1_taskType_select" style="display: none" class="clearLink">
			<s:select list="filter.taskTypeList" multiple="true" cssClass="forms"
				name="filter.taskType" id="form1_taskType" />
			<br />
			<script type="text/javascript">updateQuery('form1_taskType');</script>
			<a class="clearLink" href="#" onclick="clearSelected('form1_taskType'); return false;">Clear</a>
		</span>
	</div>
</s:form>

<div class="clear"></div>
</div>

<s:if test="report.allRows == 0">
	<div class="alert">No rows found matching the given criteria. Please try again.</div>
</s:if>
<s:else>
	<div class="right">
		<a class="excel" <s:if test="report.allRows > 500">onclick="return confirm('Are you sure you want to download all <s:property value="report.allRows"/> rows? This may take a while.');"</s:if> 
			href="javascript: download('ManageJobTasksOperator');" title="Download all <s:property value="report.allRows"/> results to a CSV file">Download</a></div>
	<div><s:property value="report.pageLinksWithDynamicForm" escape="false" /></div>
	<table class="report">
		<thead>
			<tr>
				<th><a href="?orderBy=displayOrder">Label</a></th>
				<th><a href="?orderBy=name">Task Name</a></th>
				<th>Active</th>
				<th><a href="?orderBy=taskType,displayOrder">Task Type</a></th>
				<th>Task Criteria</th>
				<pics:permission perm="ManageJobTasks" type="Edit">
					<th>Edit</th>
				</pics:permission>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="data" status="stat">
				<tr id="<s:property value="get('id')" />">
					<td class="label">
						<span class="oldValue"><s:property value="get('label')" /></span>
						<span class="newValue">
							<input type="text" value="<s:property value="get('label')" />" name="jobTaskLabel" size="10" /><br />
						</span>
					</td>
					<td class="name">
						<span class="oldValue"><s:property value="get('name')" /></span>
						<span class="newValue"><input type="text" value="<s:property value="get('name')" />" name="jobTaskName" size="40" /></span>
					</td>
					<td class="center active">
						<span class="oldValue">
							<s:if test="get('activeLabel') == 'Active'"><span style="color: #309">Active</span></s:if>
							<s:else><span style="color: #930">Inactive</span></s:else>
						</span>
						<span class="newValue">
							<s:checkbox name="taskActive" value="%{get('activeLabel') == 'Active'}"></s:checkbox>
						</span>
					</td>
					<td class="center type">
						<span class="oldValue"><s:property value="get('taskType')" /></span>
						<span class="newValue">
							<s:select list="#{'L/G':'L/G','L':'L','G':'G'}" name="taskType"></s:select>
						</span>
					</td>
					<td class="center"><a href="ManageJobTaskCriteria.action?id=<s:property value="operator.id" />&jobTaskID=<s:property value="get('id')" />"
							name="Manage Task Criteria">Manage</a></td>
					<pics:permission perm="ManageJobTasks" type="Edit">
						<td class="center">
							<nobr><a href="#" onclick="editTask(<s:property value="get('id')" />); return false;"><img src="images/edit_pencil.png" alt="Edit Task" /><span class="newValue">Close</span></a>
							<span class="newValue">
								<br />
								<a href="#" onclick="saveEdit(<s:property value="get('id')" />); return false;" title="Save Edit" class="save">Save</a>
								<br />
								<a href="ManageJobTasksOperator.action?id=<s:property value="operator.id" />&jobTaskID=<s:property value="get('id')" />&button=Remove" 
									onclick="return confirm('Are you sure you want to remove this task?');" title="Remove Task" class="remove">Remove</a>
							</span></nobr>
						</td>
					</pics:permission>
				</tr>
			</s:iterator>
		</tbody>
	</table>
	<div><s:property value="report.pageLinksWithDynamicForm" escape="false" /></div>
</s:else>

<pics:permission perm="ManageJobTasks" type="Edit">
	<a onclick="$('#addJobTask').show(); $('#addLink').hide(); return false;"
		href="#" id="addLink" class="add">Add New Job Task</a>
	<div id="addJobTask" style="display: none; clear: both;">
		<s:form id="newJobTask" method="POST" enctype="multipart/form-data">
			<s:hidden name="id" />
			<fieldset class="form" >
				<h2 class="formLegend">Add New Job Task</h2>
				<ol>
					<li><label>Label:</label>
						<s:textfield name="newTask.label" size="10" />
					</li>
					<li><label>Name:</label>
						<s:textfield name="newTask.name" size="40" />
					</li>
					<li><label>Active:</label>
						<s:checkbox name="newTask.active"></s:checkbox>
					</li>
					<li><label>Task Type:</label>
						<s:select list="#{'L/G':'L/G','L':'L','G':'G'}" name="newTask.taskType"></s:select>
					</li>
				</ol>
			</fieldset>
			<fieldset class="form submit">
				<input type="submit" value="Save" class="picsbutton positive" name="button" />
				<button onclick="$('#addLink').show(); $('#addJobTask').hide(); return false;"
					class="picsbutton negative">Cancel</button>
			</fieldset>
		</s:form>
	</div>
</pics:permission>

</body>
</html>
