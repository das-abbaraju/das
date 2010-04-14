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
	<h1>Manage Employees<span class="sub"><s:property value="subHeading" escape="false"/></span></h1>
	
	<s:include value="../actionMessages.jsp"/>

	<a href="?button=Add" class="picsbutton">Add New Employee</a>

	<table>
		<tr>
		<s:if test="account.employees.size() > 0">
			<td style="vertical-align:top; width: 10%">
				<table class="report">
					<thead>
						<tr>
							<th>Name</th>
							<th>Title</th>
							<th>Status</th>
							<th>Classification</th>
						</tr>
					</thead>
					<s:iterator value="account.employees">
						<tr>
							<td><nobr><a href="EmployeeDetail.action?employee.id=<s:property value="employee.id"/>&account.id=<s:property value="account.id"/>"><s:property value="employee.lastName"/>, <s:property value="employee.firstName"/></a></nobr></td>
							<th><s:property value="title"/></th>
							<th><s:property value="status"/></th>
							<th><s:property value="classification"/></th>
						</tr>
					</s:iterator>
				</table>
			</td>
			
			<td style="width: 20px;"></td>
		</s:if>
			<td style="vertical-align:top;">
				<s:if test="employee != null">
					<s:form>
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
									<s:textfield name="ssn"/>
								</li>
								<li><label>Birth Date:</label>
									<s:textfield name="employee.birthDate" cssClass="datepicker"/>
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
						<fieldset class="form submit">
							<input type="submit" value="Save" name="button" class="picsbutton positive"/>
							<input type="submit" value="Delete" name="button" class="picsbutton negative"/>
						</fieldset>
					</s:form>
				</s:if>
			</td>
		</tr>
	</table>
</body>
</html>