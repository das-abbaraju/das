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
	<pics:permission perm="AllOperators">
		<div id="search">
			<s:form id="filterOperator">
				<div class="filterOption">
					<h4>Account:</h4>
					<s:select name="account.id" headerKey="1100"
						headerValue="PICS Employees" list="operators" listKey="id"
						listValue="name" onchange="$('form#filterOperator').submit();"/>
				</div>
			<div class="clear"></div>
			</s:form>
		</div>
	</pics:permission>

	<a href="?button=Add" class="picsbutton">Add New Employee</a>

	<table>
		<tr>
			<td style="vertical-align:top; width: 10%">
				<table class="report">
					<thead>
						<tr>
							<th>Name</th>
						</tr>
					</thead>
					<s:iterator value="employees">
						<tr>
							<td><a href="?employee.id=<s:property value="id"/>"><s:property value="displayName"/></a></td>
						</tr>
					</s:iterator>
				</table>
			</td>
			
			<td style="width: 20px;"></td>
			
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
									<s:textfield name="employee.ssn"/>
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
								<li><label>Status</label>
									<s:select name="employee.status" 
										list="@com.picsauditing.jpa.entities.EmployeeStatus@values()"
										headerKey="" headerValue="- Status -"/>
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