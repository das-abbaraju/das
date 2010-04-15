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
employeeID = <s:property value="employee.id"/>
</s:if>

function show(id) {
	$.getJSON('ManageEmployeesAjax.action',
			{employeeID: id, button: 'load'},
			function (json, result) {
				alert(json);
			}
		);
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
			bJQueryUI: true,
			bStateSave: true,
			bInfo: false,
			oLanguage: { sLengthMenu: 'Show _MENU_' },
			iDisplayLength: 25,
			sPaginationType: "full_numbers",
			fnRowCallback: function( nRow, aData, iDisplayIndex ) {
				if (aData[0] == employeeID)
					$(nRow).not('.highlight').addClass('highlight');
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

	<a href="?id=<s:property value="account.id"/>&button=Add" class="picsbutton">Add New Employee</a>

	<table style="margin-top: 20px;">
		<tr>
			<s:if test="account.employees.size() > 0">
				<td style="vertical-align:top; width: 25%;">
					<table class="report" id="employees">
						<thead>
							<tr>
								<th>ID</th>
								<th>First Name</th>
								<th>Last Name</th>
								<th>Title</th>
								<th>Classification</th>
							</tr>
						</thead>
						<s:iterator value="account.employees">
							<tr class="clickable" onclick="javascript:window.location.href='?employee.id=<s:property value="id"/>'">
								<td><s:property value="id"/></td>
								<td><s:property value="firstName"/></td>
								<td><s:property value="lastName"/></td>
								<td><s:property value="title"/></td>
								<td><s:property value="classification.description"/></td>
							</tr>
						</s:iterator>
					</table>
				</td>
				
				<td style="width: 20px;"></td>
			</s:if>
			<s:if test="employee != null">
				<td style="vertical-align:top;">
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
										listValue="description"
										headerKey="" headerValue="- Classification -"/>
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
							<input type="submit" value="Delete" name="button" class="picsbutton negative"/>
						</fieldset>
					</s:form>
					
					<h3>Job Roles</h3>
					<table class="report">
						<thead>
							<tr>
								<th colspan="2">Role</th>
							</tr>
						</thead>
						<s:iterator value="employee.employeeRoles">
							<tr>
								<td><s:property value="jobRole.name"/></td>
								<td><a href="#" class="remove">Remove</a></td>
							</tr>
						</s:iterator>
						<s:iterator value="unusedJobRoles">
							<tr>
								<td><s:property value="name"/></td>
								<td><a href="#" class="add">Add</a></td>
							</tr>
						</s:iterator>					
					</table>
				</td>
			</s:if>
		</tr>
	</table>
</body>
</html>