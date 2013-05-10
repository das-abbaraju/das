<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<head>
	<title><s:text name="ReportEmployeeDocumentation.title"/></title>
	<s:include value="../reports/reportHeader.jsp" />
</head>
<body>
	<h1><s:text name="ReportEmployeeDocumentation.title"/></h1>

	<s:include value="../reports/filters.jsp"/>

	<div id="report_data">
		<div>
			<s:property value="report.pageLinksWithDynamicForm" escape="false" />
		</div>
		<table class="report">
			<thead>
			<tr>
				<th>

				</th>
				<th>
					<s:text name="global.Contractor"/>
				</th>
				<th>
					<s:text name="Employee.name"/>
				</th>
				<th>
					<s:text name="OperatorCompetency.label"/>
				</th>
				<th>
					<s:text name="global.ExpirationDate"/>
				</th>
			</tr>
			</thead>
			<tbody>
			<s:if test="data.size() > 0">
				<s:iterator value="data" var="competency_file" status="status">
					<tr>
						<td>
							${status.index + report.firstRowNumber}
						</td>
						<td>
							<s:url var="contractor_view" action="ContractorView">
								<s:param name="id">
									${competency_file.get('id')}
								</s:param>
							</s:url>
							<a href="${contractor_view}">
								${competency_file.get('name')}
							</a>
						</td>
						<td>
							<s:url var="employee_profile" action="EmployeeDetail">
								<s:param name="employee">
									${competency_file.get('employeeID')}
								</s:param>
							</s:url>
							<a href="${employee_profile}">
								${competency_file.get('firstName')} ${competency_file.get('lastName')}
							</a>
						</td>
						<td>
							${competency_file.get('label')}
						</td>
						<td>
							<s:url var="skills_training" action="EmployeeSkillsTraining">
								<s:param name="employee">
									${competency_file.get('employeeID')}
								</s:param>
							</s:url>
							<a href="${skills_training}">
								${competency_file.get('expiration')}
							</a>
						</td>
					</tr>
				</s:iterator>
			</s:if>
			<s:else>
			</s:else>
			</tbody>
		</table>
		<div>
			<s:property value="report.pageLinksWithDynamicForm" escape="false" />
		</div>
	</div>
</body>