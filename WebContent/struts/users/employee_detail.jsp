<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
	<title>Employee Details</title>
	<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
	<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
	
	<s:include value="../jquery.jsp"/>
</head>
<body>
	<h1>Employee Details <span class="sub"><s:property value="employee.firstName"/> <s:property value="employee.lastName"/></span></h1>

<div id="internalnavcontainer">
<ul id="navlist">
	<li><a href="#">Edit</a></li>
	<li><a href="AssessmentResults.action?employee.id=<s:property value="employee.id" />">Assessments</a></li>
	<li><a href="#">Data Partner IDs</a></li>
</ul>
</div>
	
	<s:include value="../actionMessages.jsp"/>

	<table>
		<tr>
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