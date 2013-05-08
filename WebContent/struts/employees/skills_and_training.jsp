<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<s:url var="employee_dashboard" action="EmployeeDashboard">
	<s:param name="id">
		${employee.account.id}
	</s:param>
</s:url>

<head>
	<title>
		<s:text name="EmployeeSkillsTraining.title"/>
	</title>
	<style type="text/css">
		table.table {
			margin-bottom: 2em;
			width: auto;
		}

		h3 {
			margin-bottom: 0.5em;
		}
	</style>
</head>
<body>
<div class="${actionName}-page page" id="${actionName}_${methodName}_page">
	<h1>
		<s:text name="EmployeeSkillsTraining.title"/>
				<span class="sub">
					${employee.name}
				</span>
	</h1>

	<a href="${employee_dashboard}">
		<s:text name="EmployeeGUARD.Dashboard.title"/>
	</a>
	<br/>
	<br/>

	<s:if test="competenciesMissingDocumentation.size() > 0">
		<h3>
			<s:text name="EmployeeSkillsTraining.Pending"/>
		</h3>
		<table class="table">
			<thead>
			<tr>
				<th>
					<s:text name="global.Operator"/>
				</th>
				<th>
					<s:text name="EmployeeSkillsTraining.SkillsTraining"/>
				</th>
				<s:if test="canAccessDocumentation">
					<th>
						<s:text name="EmployeeSkillsTraining.Document"/>
					</th>
				</s:if>
			</tr>
			</thead>
			<tbody>
			<s:iterator value="competenciesMissingDocumentation" var="competency_missing_documentation" status="step">
				<tr class="${step.even ? 'even' : 'odd'}">
					<td>
							${competency_missing_documentation.operator.name}
					</td>
					<td>
							${competency_missing_documentation.label}
					</td>
					<s:if test="canAccessDocumentation">
						<td>
							<s:url var="employee_competency_upload" action="EmployeeDocumentationFileUpload">
								<s:param name="employee">
									${employee.id}
								</s:param>
								<s:param name="competency">
									${competency_missing_documentation.id}
								</s:param>
							</s:url>
							<a href="${employee_competency_upload}">
								<s:text name="button.Upload"/>
							</a>
						</td>
					</s:if>
				</tr>
			</s:iterator>
			</tbody>
		</table>
	</s:if>

	<s:iterator value="filesByStatus.keySet()" var="file_status">
		<h3>
			<s:text name="%{#file_status}"/>
		</h3>

		<table class="table">
			<thead>
			<tr>
				<th>
					<s:text name="global.Operator"/>
				</th>
				<th>
					<s:text name="EmployeeSkillsTraining.SkillsTraining"/>
				</th>
				<s:if test="canAccessDocumentation">
					<th>
						<s:text name="EmployeeSkillsTraining.Document"/>
					</th>
				</s:if>
				<th>
					<s:text name="global.ExpirationDate"/>
				</th>
			</tr>
			</thead>
			<tbody>
			<s:iterator value="filesByStatus.get(#file_status)" var="employee_file" status="employee_file_step">
				<tr class="${employee_file_step.even ? 'even' : 'odd'}">
					<td>
							${employee_file.competency.operator.name}
					</td>
					<td>
							${employee_file.competency.label}
					</td>
					<s:if test="canAccessDocumentation">
						<td>
							<s:url var="download_employee_file" method="download">
								<s:param name="employeeFile">
									${employee_file.id}
								</s:param>
							</s:url>
							<a href="${download_employee_file}">
									${employee_file.fileName}
							</a>
						</td>
					</s:if>
					<td>
						<s:date name="#employee_file.expiration"/>
					</td>
				</tr>
			</s:iterator>
			</tbody>
		</table>
	</s:iterator>
</div>
</body>