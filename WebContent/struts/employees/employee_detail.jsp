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
	<li><a href="EmployeeDetail.action?employee.id=<s:property value="employee.id" />">Edit</a></li>
	<li><a href="EmployeeAssessmentResults.action?employee.id=<s:property value="employee.id" />">Assessments</a></li>
	<li><a href="EmployeeDataPartnerIDs.action?employee.id=<s:property value="employee.id" />">Data Partner IDs</a></li>
</ul>
</div>
	
	<s:include value="../actionMessages.jsp"/>
	<div>
		<s:if test="employee != null">
			<s:form>
				<fieldset class="form">
					<legend><span>Employee Details</span></legend>
					<ol>
						<li><label>First Name:</label>
							<s:property value="employee.firstName"/>
							<br/>
						</li>
						<li><label>Last Name:</label>
							<s:property value="employee.lastName"/>
							<br/>
						</li>
						<li><label>SSN:</label>
							<s:property value="ssn"/>
							<br/>
						</li>
						<li><label>Birth Date:</label>
							<s:property value="employee.birthDate"/>
							<br/>
						</li>
						<li><label>Classification:</label>
							<s:property value="employee.classification"/>
							<br/>
						</li>
						<li><label>Hire Date:</label>
							<s:property value="employee.hireDate"/>
							<br/>
						</li>
						<li><label>Fire Date:</label>
							<s:property value="employee.fireDate"/>
							<br/>
						</li>
						<li><label>Title:</label>
							<s:property value="employee.title"/>
							<br/>
						</li>
						<li><label>Location:</label>
							<s:property value="employee.location"/>
							<br/>
						</li>
						<li><label>Email:</label>
							<s:property value="employee.email"/>
							<br/>
						</li>
						<li><label>Phone #:</label>
							<s:property value="employee.phone"/>
							<br/>
						</li>
						<li><label>Photo:</label>
							<s:property value="employee.photo"/>
							<br/>
						</li>
					</ol>
				</fieldset>
			</s:form>
		</s:if>
	</div>
</body>
</html>