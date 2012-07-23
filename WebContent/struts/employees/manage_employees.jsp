<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<%-- URLS --%>
<s:url action="EmployeeDashboard" var="employee_dashboard">
	<s:param name="id">
		${account.id}
	</s:param>
</s:url>
<s:url action="ManageEmployees" method="add" var="add_employee">
	<s:param name="account">
		${account.id}
	</s:param>
	<s:param name="audit">
		${audit.id}
	</s:param>
	<s:param name="questionId">
		${questionId}
	</s:param>
</s:url>
<s:url action="EmployeeList" method="download" var="employee_list_download">
	<s:param name="filter.accountName">
		${account.id}
	</s:param>
</s:url>

<head>
	<title>
		<s:text name="ManageEmployees.title" />
	</title>

	<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=${version}" />
	<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=${version}" />
	<link rel="stylesheet" type="text/css" href="js/jquery/jquery-ui/jquery-ui-1.7.2.custom.css?v=${version}">
	<link rel="stylesheet" type="text/css" href="js/jquery/dataTables/css/dataTables.css?v=${version}"/>
	<style>
		.sites-label
		{
			display: inline !important;
			font-weight: normal !important;
		}
		
		.layout td
		{
			vertical-align: top;
		}
	</style>
	
	<script type="text/javascript" src="js/jquery/bbq/jquery.ba-bbq.min.js?v=${version}"></script>
	<script type="text/javascript" src="js/jquery/cluetip/jquery.cluetip.min.js?v=${version}"></script>
	<script type="text/javascript" src="js/jquery/dataTables/jquery.dataTables.min.js?v=${version}"></script>
</head>
<body>
	<div id="${actionName}_${methodName}_page" class="${actionName}-page page">
		<s:if test="audit.id > 0">
			<s:if test="questionId == 3673">
				<div class="info">
					<s:text name="ManageEmployees.Step3">
						<s:param>
							<s:property value="audit.id" />
						</s:param>
						<s:param>
							<s:text name="AuditType.99.name" />
						</s:param>
					</s:text>
				</div>
			</s:if>
			<s:elseif test="questionId == 3674">
				<div class="info">
					<s:text name="ManageEmployees.Step4">
						<s:param>
							<s:property value="audit.id" />
						</s:param>
						<s:param>
							<s:text name="AuditType.99.name" />
						</s:param>
					</s:text>
				</div>
			</s:elseif>
		</s:if>
	
		<h1>
			<s:property value="account.name" />
			<span class="sub">
				<s:text name="ManageEmployees.title" />
			</span>
		</h1>
		
		<s:include value="../actionMessages.jsp"/>
	
		<s:if test="audit == null" >
			<div>
				<a href="${employee_dashboard}">
					<s:text name="global.EmployeeGUARD" />
				</a>
			</div>
		</s:if>
		
		<s:if test="account.employees.size() == 0 && employee == null">
			<div class="info">
				<s:text name="ManageEmployees.message.NoEmployees">
					<s:param>
						<s:text name="ManageEmployees.link.Add" />
					</s:param>
				</s:text>
			</div>
		</s:if>
	
		<a href="${add_employee}" class="add">
			<s:text name="ManageEmployees.link.Add" />
		</a>
		<br />
		<a href="javascript:;" class="add" id="import_excel" data-account="${account.id}">
			<s:text name="ManageEmployees.link.Import" />
		</a>
		
		<table class="layout">
			<tr>
				<s:if test="account.employees.size > 0">
					<td style="width: 25%;">
						<table class="report" id="employee_table">
							<thead>
								<tr>
									<th>id</th>
									<th>
										<s:text name="Employee.lastName" />
									</th>
									<th>
										<s:text name="Employee.firstName" />
									</th>
									<th>
										<s:text name="Employee.title" />
									</th>
									<th>
										<s:text name="EmployeeClassification" />
									</th>
									<th>
										<s:text name="button.Edit" />
									</th>
									<th>
										<s:text name="ManageEmployees.message.Profile" />
									</th>
								</tr>
							</thead>
							<tbody>
								<s:iterator value="activeEmployees" id="e">
									<tr id="employee_${e.id}">
										<td>
											<s:property value="#e.id" />
										</td>
										<td>
											<a
												href="#employee=<s:property value="#e.id" />"
												class="load-employee"
												title="<s:text name="ManageEmployees.title.EditProfile" />"
												data-audit="${audit.id}"
												data-employee="${e.id}"
												data-questionId="${questionId}"
											>
												<s:property value="#e.lastName" />
											</a>
										</td>
										<td>
											<a
												href="#employee=<s:property value="#e.id" />"
												class="load-employee"
												title="<s:text name="ManageEmployees.title.EditProfile" />"
												data-audit="${audit.id}"
												data-employee="${e.id}"
												data-questionId="${questionId}"
											>
												<s:property value="#e.firstName" />
											</a>
										</td>
										<td>
											<s:property value="#e.title" />
										</td>
										<td>
											<s:if test="#e.classification != null">
												<s:text name="%{#e.classification.getI18nKey('description')}" />
											</s:if>
										</td>
										<td class="center">
											<a
												href="#employee=<s:property value="#e.id" />"
												class="load-employee edit"
												title="<s:text name="ManageEmployees.title.EditProfile" />"
												data-audit="${audit.id}"
												data-employee="${e.id}"
												data-questionId="${questionId}"
											></a>
										</td>
										<td class="center">
											<a
												href="EmployeeDetail.action?employee=<s:property value="#e.id" />"
												class="preview"
											></a>
										</td>
									</tr>
								</s:iterator>
							</tbody>
						</table>
						
						<br clear="both" />
						<a href="${employee_list_download}" class="excel"><s:text name="global.Download" /></a>
					</td>
					<td style="width: 20px;"></td>
				</s:if>
				<td>
					<div id="employee_form"<s:if test="employee.id > 0"> data-employee="${employee.id}"</s:if>>
						<s:if test="employee != null && employee.id == 0">
							<s:include value="manage_employees_form.jsp" />
						</s:if>
					
				</td>
			</tr>
		</table>
		
		<div id="siteEditBox"></div>
	</div>
</body>