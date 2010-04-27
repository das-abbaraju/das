<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Manage Employees</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />

<s:include value="../jquery.jsp"/>

<script type="text/javascript" src="js/jquery/jquery.maskedinput-1.2.2.min"></script>

<script type="text/javascript" src="js/jquery/dataTables/jquery.dataTables.min.js"></script>
<link rel="stylesheet" href="js/jquery/dataTables/css/dataTables.css"/>

<script type="text/javascript">
var employeeID = 0;
<s:if test="employee != null">
employeeID = <s:property value="employee.id"/>;
</s:if>

function show(id) {
	$.getJSON('ManageEmployeesAjax.action',
			{employeeID: id, button: 'load'},
			function (json, result) {
				alert(json);
			}
		);
}

function addJobRole(id) {
	startThinking({div: 'thinking_roles', message: 'Adding Job Role'})
	$('#employee_role').load('ManageEmployeesAjax.action', {button: 'addRole', 'employee.id': employeeID, childID: id});
}

function addJobSite(id) {
	startThinking({div: 'thinking_sites', message: 'Assigning Employee to Job Site'})
	$('#employee_site').load('ManageEmployeesAjax.action', {button: 'addSite', 'employee.id': employeeID, childID: id});
}

function removeJobRole(id) {
	startThinking({div: 'thinking_roles', message: 'Removing Job Role'})
	$('#employee_role').load('ManageEmployeesAjax.action', {button: 'removeRole', 'employee.id': employeeID, childID: id});
}

function removeJobSite(id) {
	startThinking({div: 'thinking_sites', message: 'Removing Employee from Job Site'})
	$('#employee_site').load('ManageEmployeesAjax.action', {button: 'removeSite', 'employee.id': employeeID, childID: id});
}

$(function() {
	$.mask.definitions['S']='[X0-9]';
	$('input.ssn').mask('SSS-SS-SSSS');
	$('input.date').mask('99/99/9999');

	$('#employees').dataTable({
			aoColumns: [
		            {bVisible: false},
		            null,
		            null,
		            null,
		            null
				],
			aaSorting: [[1, 'asc']],
			aaData: <s:property value="employeeData" escape="false"/>,
			bStateSave: true,
			oLanguage: {
				sSearch:"",
				sLengthMenu: '_MENU_', 
				sInfo:"_START_ to _END_ of _TOTAL_",
				sInfoEmpty:"",
				sInfoFiltered:"(filtered from _MAX_)" },
			iDisplayLength: 25,
			fnRowCallback: function( nRow, aData, iDisplayIndex ) {
				if (aData[0] == employeeID)
					$(nRow).not('.highlight').addClass('highlight');

				$(nRow).not('.clickable').addClass('clickable').click(function() {
						location.href='?employee.id='+aData[0];
					});
				return nRow;
			}
		});
});
</script>

<style>
div.dataTables_filter { width: 65%; }
div.dataTables_length { width: 35%; }
</style>
</head>
<body>
	<h1>Manage Employees<span class="sub"><s:property value="subHeading" escape="false"/></span></h1>
	
	<s:include value="../actionMessages.jsp"/>

	<s:if test="account.employees.size() == 0 && employee == null">
		<div class="info">
			There are no employees on this account. Click the "Add New Employee" button to add a new employee.
		</div>
	</s:if>

	<a href="?id=<s:property value="account.id"/>&button=Add" class="add">Add New Employee</a>

	<table style="margin-top: 20px;">
		<tr>
			<s:if test="account.employees.size() > 0">
				<td style="vertical-align:top; width: 25%;">
					<table class="report" id="employees">
						<thead>
							<tr>
								<th>id</th>
								<th>Last Name</th>
								<th>First Name</th>
								<th>Title</th>
								<th>Classification</th>
							</tr>
						</thead>
					</table>
				</td>
				
				<td style="width: 20px;"></td>
			</s:if>
			<s:if test="employee != null">
				<td style="vertical-align:top;">
				<a href="EmployeeDetail.action?employee.id=<s:property value="employee.id" />">View Profile Page</a>
					<s:form id="employeeForm">
						<s:hidden name="id"/>
						<s:hidden name="employee.id"/>
						<fieldset class="form">
							<legend><span>Employee Details</span></legend>
							<ol>
								<li><label>First Name:</label>
									<s:textfield name="employee.firstName"/>
								</li>
								<li><label>Last Name:</label>
									<s:textfield name="employee.lastName"/>
								</li>
								<li><label>SSN:</label>
									<s:textfield name="ssn" cssClass="ssn"/>
								</li>
								<li><label>Title:</label>
									<s:textfield name="employee.title"/>
								</li>
								<li><label>Birth Date:</label>
									<s:textfield name="employee.birthDate" value="%{maskDateFormat(employee.birthDate)}" cssClass="date"/>
								</li>
								<li><label>Classification:</label>
									<s:select name="employee.classification" 
										list="@com.picsauditing.jpa.entities.EmployeeClassification@values()" 
										listValue="description" />
								</li>
								<li><label>Active</label>
									<s:checkbox name="employee.active"/>
								</li>
								<li><label>Hire Date:</label>
									<s:textfield name="employee.hireDate" value="%{maskDateFormat(employee.hireDate)}" cssClass="date"/>
								</li>
								<li><label>Fire Date:</label>
									<s:textfield name="employee.fireDate" value="%{maskDateFormat(employee.fireDate)}" cssClass="date"/>
								</li>
								<li><label>Location:</label>
									<s:textfield name="employee.location"/>
								</li>
								<li><label>Email:</label>
									<s:textfield name="employee.email"/>
								</li>
								<li><label>Phone #:</label>
									<s:textfield name="employee.phone"/>
								</li>
						
							</ol>
						</fieldset>
						<fieldset class="form submit">
							<input type="submit" value="Save" name="button" class="picsbutton positive"/>
							<input type="submit" value="Delete" name="button" class="picsbutton negative" 
								onclick="return confirm('Are you sure you want to delete this employee? This action cannot be undone.');"/>
						</fieldset>
					</s:form>
				
					<s:if test="employee.id > 0">
						<s:if test="permissions.requiresCompetencyReview">
							<s:if test="(unusedJobRoles.size() + employee.employeeRoles.size()) > 0">
								<div style="float:left; padding-right:20px;">
									<h3>Job Roles</h3>
									<div id="employee_role">
										<s:include value="manage_employee_roles.jsp"/>
									</div>
								</div>
							</s:if>
						</s:if>
						
						<div style="float:left">
							<h3>Assigned Sites</h3>
							<div id="employee_site">
								<s:include value="manage_employee_sites.jsp"/>
							</div>
						</div>
					</s:if>
				</td>
			</s:if>
		</tr>
	</table>
</body>
</html>