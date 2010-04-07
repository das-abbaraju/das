<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
	<title>Manage Employees</title>
	<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
	<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
	
	<s:include value="../jquery.jsp"/>
	
	<script type="text/javascript">
		function show(id) {
			$.getJSON('ManageEmployeesAjax.action',
					{employeeID: id, button: 'load'},
					function (json, result) {
						alert(json);
					}
				);
		}

		$(function() {
			$('input.datepicker').datepicker();
		});
	</script>
</head>
<body>
	<div class="left">
	<table class="report">
		<thead>
			<tr>
				<th>Name</th>
			</tr>
		</thead>
		<s:iterator value="employees">
			<tr>
				<td><a href="?employeeID=<s:property value="id"/>"><s:property value="firstName"/> <s:property value="lastName"/></a></td>
			</tr>
		</s:iterator>
	</table>
	</div>
	
	<div class="left">
		<form>
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
						<s:textfield name="employee.ssn"/>
					</li>
					<li><label>Birth Date:</label>
						<s:textfield name="employee.birthDate" cssClass="datepicker"/>
					</li>
					<li><label>Classification:</label>
						<s:textfield name="employee.classification"/>
					</li>
					<li><label>Status</label>
						<s:textfield name="employee.status"/>
					</li>
					<li><label>Hire Date:</label>
						<s:textfield name="employee.hireDate" cssClass="datepicker"/>
					</li>
					<li><label>Fire Date:</label>
						<s:textfield name="employee.fireDate" cssClass="datepicker"/>
					</li>
					<li><label>Title:</label>
						<s:textfield name="employee.title"/>
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
					<li><label>Photo:</label>
						<s:textfield name="employee.photo"/>
					</li>
				</ol>
			</fieldset>
		</form>
	</div>
</body>
</html>