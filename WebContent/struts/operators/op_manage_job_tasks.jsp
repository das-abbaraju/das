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

$(function() {
	$('#newJobTask .cancelButton').live('click', function(e) {
		e.preventDefault();
		$('#addLink').show();
		$('#addJobTask').hide();
	});
	
	$('.editActions .save').live('click', function(e) {
		e.preventDefault();
		
		var row = $(this).closest('tr');
		var taskID = parseInt($(row).attr('id'));
		var name = escape($(row).find('td.name input').val());
		var label = escape($(row).find('td.label input').val());
		var type = $(row).find('td.type select').val().replace("/", "%2F");
		var active = $(row).find('td.active input[type=checkbox]').is(':checked');
		
		var url = '<s:property value="scope" />!edit.action?operator=<s:property value="operator.id" />&jobTask=' + taskID 
				+ '&label=' + (typeof(label) == 'undefined' ? null : label) 
				+ '&name=' + (typeof(name) == 'undefined' ? null : name) 
				+ '&taskType=' + (typeof(type) == 'undefined' ? null : type)
				+ '&active=' + (typeof(active) == 'undefined' ? null : active); 
		
		self.location = url;
	});
	
	$('.editActions .remove').live('click', function(e) {
		return confirm('<s:text name="%{scope}.confirm.RemoveTask" />');
	});
});
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
			onclick="return clickSearch('form1');" class="picsbutton positive"><s:text name="button.Search" /></button>
	</div>
	<div class="filterOption">
		<s:text name="JobTask.label" />: <s:textfield name="filter.label" size="5" onclick="clearText(this)" />
	</div>
	<div class="filterOption">
		<s:text name="JobTask.name" />: <s:textfield name="filter.name" />
	</div>
	<div class="filterOption">
		<label><s:checkbox name="filter.active" /> <s:text name="%{scope}.label.ShowActive" /></label>
	</div>
	<div class="filterOption">
		<a href="#" onclick="toggleBox('form1_taskType'); return false;"><s:text name="JobTask.taskType" /></a> =
		<span id="form1_taskType_query"><s:text name="JS.Filters.status.All" /></span>
		<br />
		<span id="form1_taskType_select" style="display: none" class="clearLink">
			<s:select list="filter.taskTypeList" multiple="true" cssClass="forms"
				name="filter.taskType" id="form1_taskType" />
			<br />
			<a class="clearLink" href="#" onclick="clearSelected('form1_taskType'); return false;"><s:text name="Filters.status.Clear" /></a>
		</span>
	</div>
</s:form>

<div class="clear"></div>
</div>

<s:if test="report.allRows == 0">
	<div class="alert"><s:text name="Report.message.NoRowsFound" /></div>
</s:if>
<s:else>
	<div class="right">
		<a class="excel" <s:if test="report.allRows > 500">onclick="return confirm('<s:text name="JS.ConfirmDownloadAllRows"><s:param value="%{report.allRows}" /></s:text>');"</s:if> 
			href="#" onclick="download('ManageJobTasksOperator'); return false;" title="<s:text name="javascript.DownloadAllRows"><s:param value="%{report.allRows}" /></s:text>"><s:text name="global.Download" /></a></div>
	<div><s:property value="report.pageLinksWithDynamicForm" escape="false" /></div>
	<table class="report">
		<thead>
			<tr>
				<th><a href="?orderBy=displayOrder"><s:text name="JobTask.label" /></a></th>
				<th><a href="?orderBy=name"><s:text name="JobTask.name" /></a></th>
				<th><s:text name="JobTask.active" /></th>
				<th><a href="?orderBy=taskType,displayOrder"><s:text name="JobTask.taskType" /></a></th>
				<th><s:text name="JobTask.jobTaskCriteria" /></th>
				<pics:permission perm="ManageJobTasks" type="Edit">
					<th><s:text name="button.Edit" /></th>
				</pics:permission>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="data" status="stat">
				<tr id="<s:property value="get('id')" />">
					<td class="label">
						<span class="oldValue"><s:property value="get('label')" /></span>
						<span class="newValue">
							<input type="text" value="<s:property value="get('label')" />" size="10" /><br />
						</span>
					</td>
					<td class="name">
						<span class="oldValue"><s:property value="get('name')" /></span>
						<span class="newValue"><input type="text" value="<s:property value="get('name')" />" size="40" /></span>
					</td>
					<td class="center active">
						<span class="oldValue">
							<s:if test="get('activeLabel') == 'Active'"><span style="color: #309"><s:text name="JobTask.active" /></span></s:if>
							<s:else><span style="color: #930"><s:text name="JobTask.inactive" /></span></s:else>
						</span>
						<span class="newValue">
							<s:checkbox name="taskActive" value="%{get('activeLabel') == 'Active'}" />
						</span>
					</td>
					<td class="center type">
						<span class="oldValue"><s:property value="get('taskType')" /></span>
						<span class="newValue">
							<s:select list="#{'L/G':'L/G','L':'L','G':'G'}" value="%{get('taskType')}" />
						</span>
					</td>
					<td class="center">
						<a href="ManageJobTaskCriteria.action?operator=<s:property value="operator.id" />&jobTask=<s:property value="get('id')" />"
							name="<s:text name="%{scope}.help.ManageTaskCriteria" />" class="preview"></a>
					</td>
					<pics:permission perm="ManageJobTasks" type="Edit">
						<td class="center editActions">
							<nobr><a href="#" onclick="editTask(<s:property value="get('id')" />); return false;"><img src="images/edit_pencil.png" alt="Edit Task" /><span class="newValue"><s:text name="button.Close" /></span></a>
							<span class="newValue">
								<br />
								<a href="#" class="save"><s:text name="button.Save" /></a>
								<br />
								<a href="<s:property value="scope" />!remove.action?operator=<s:property value="operator.id" />&jobTask=<s:property value="get('id')" />" 
									class="remove"><s:text name="button.Remove" /></a>
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
	<a onclick="$('#addJobTask').show(); $('#addLink').hide(); return false;" href="#" id="addLink" class="add"><s:text name="%{scope}.label.AddNewJobTask" /></a>
	<div id="addJobTask" style="display: none; clear: both;">
		<s:form id="newJobTask" method="POST" enctype="multipart/form-data">
			<s:hidden name="id" />
			<fieldset class="form" >
				<h2 class="formLegend"><s:text name="%{scope}.label.AddNewJobTask" /></h2>
				<ol>
					<li>
						<label><s:text name="JobTask.label" />:</label>
						<s:textfield name="label" size="10" />
					</li>
					<li>
						<label><s:text name="JobTask.name" />:</label>
						<s:textfield name="name" size="40" />
					</li>
					<li>
						<label><s:text name="JobTask.active" />:</label>
						<s:checkbox name="active" />
					</li>
					<li><label><s:text name="JobTask.taskType" />:</label>
						<s:select list="#{'L/G':'L/G','L':'L','G':'G'}" name="taskType"></s:select>
					</li>
				</ol>
			</fieldset>
			<fieldset class="form submit">
				<s:submit method="save" cssClass="picsbutton positive" value="%{getText('button.Save')}" />
				<input type="button" value="<s:text name="button.Cancel" />" class="picsbutton cancelButton" />
			</fieldset>
		</s:form>
	</div>
</pics:permission>

</body>
</html>
