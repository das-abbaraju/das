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
#tasksTable table.report {
	margin-right: 10px;
	margin-bottom: 10px;
}

.newValue {
	display: none;
}
</style>
<s:include value="../jquery.jsp"/>
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

function sortTable(sortBy) {
	var tbody = $('#tasksTable table.report').find('tbody');
	var rows = $(tbody).children();
	$(tbody).empty();

	var sortBys = sortBy.split(',');
	rows.sort(function(a, b) {
		var sort1 = sort(a, b, sortBys[0]);

		if (sort1 == 0 && sortBys.length > 1) {
			var sort2 = 0;
			var count = 1;

			while (sort2 == 0) {
				sort2 = sort(a, b, sortBys[count]);
				count++;
				sort1 = sort2;
			}
		}

		return sort1;
	});

	$.each(rows, function (index, row) { $(tbody).append(row); });
}

function sort(a, b, sortBy) {
	if (sortBy == 'label') {
		var a1 = Number($(a).find('.' + sortBy).text());
		var b1 = Number($(b).find('.' + sortBy).text());

		if (a1 != 'NaN' && b1 != 'NaN')
			return a1 - b1;
	}

	var a1 = $(a).find('.' + sortBy).text().toUpperCase();
	var b1 = $(b).find('.' + sortBy).text().toUpperCase();
	return (a1 < b1) ? -1 : (a1 > b1) ? 1 : 0;
}
</script>
</head>
<body>
<s:include value="opHeader.jsp"></s:include>
<table id="tasksTable" >
	<tr>
		<td>
			<s:if test="tasks.size() > 0">
				<table class="report">
					<thead>
						<tr>
							<th><a href="#" onclick="sortTable('label'); return false;">Label</a></th>
							<th><a href="#" onclick="sortTable('name'); return false;">Task Name</a></th>
							<th><a href="#" onclick="sortTable('active,label'); return false;">Active</a></th>
							<th><a href="#" onclick="sortTable('type,label'); return false;">Task Type</a></th>
							<th>Task Criteria</th>
							<pics:permission perm="ManageJobTasks" type="Edit">
								<th>Edit</th>
							</pics:permission>
						</tr>
					</thead>
					<tbody>
						<s:iterator value="tasks" status="stat" id="task">
							<tr id="<s:property value="#task.id" />">
								<td class="label">
									<span class="oldValue"><s:property value="#task.label" /></span>
									<span class="newValue"><input type="text" value="<s:property value="#task.label" />" name="jobTaskLabel" size="10" /></span>
								</td>
								<td class="name">
									<span class="oldValue"><s:property value="#task.name" /></span>
									<span class="newValue"><input type="text" value="<s:property value="#task.name" />" name="jobTaskName" size="40" /></span>
								</td>
								<td class="center active">
									<span class="oldValue">
										<s:if test="#task.active"><span style="color: #309">Active</span></s:if>
										<s:else><span style="color: #930">Inactive</span></s:else>
									</span>
									<span class="newValue">
										<s:checkbox name="taskActive" value="#task.active"></s:checkbox>
									</span>
								</td>
								<td class="center type">
									<span class="oldValue"><s:property value="#task.taskType" /></span>
									<span class="newValue">
										<s:select list="#{'L/G':'L/G','L':'L','G':'G'}" name="taskType"></s:select>
									</span>
								</td>
								<td class="center"><a href="ManageJobTaskCriteria.action?id=<s:property value="operator.id" />&jobTaskID=<s:property value="#task.id" />"
										name="Manage Task Criteria">Manage</a></td>
								<pics:permission perm="ManageJobTasks" type="Edit">
									<td class="center">
										<nobr><a href="#" onclick="editTask(<s:property value="#task.id" />); return false;"><img src="images/edit_pencil.png" alt="Edit Task" /></a>
										<span class="newValue">
											<a href="#" onclick="saveEdit(<s:property value="#task.id" />); return false;" title="Save Edit" class="save"></a>
											<a href="ManageJobTasksOperator.action?id=<s:property value="operator.id" />&jobTaskID=<s:property value="#task.id" />&button=Remove" 
												onclick="return confirm('Are you sure you want to remove this task?');" title="Remove Task" class="remove"></a>
										</span></nobr>
									</td>
								</pics:permission>
							</tr>
						</s:iterator>
					</tbody>
				</table>
			</s:if>
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
		</td>
	</tr>
</table>
<script type="text/javascript">sortTable('label');</script>
</body>
</html>
