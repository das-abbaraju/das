<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<s:url action="EmployeeList" method="download" var="employee_list_download">
	<s:param name="filter.accountName">${account.id}</s:param>
</s:url>

<s:url action="ManageEmployees" method="add" var="add_employee">
	<s:param name="account">${account.id}</s:param>
	<s:param name="audit">${audit.id}</s:param>
	<s:param name="questionId">${questionId}</s:param>
</s:url>

<s:include value="_manage-employees-head.jsp"/>

<body>
	<div id="${actionName}_${methodName}_page" class="${actionName}-page page">

		<s:include value="_manage-employees-header.jsp"/>

		<s:if test="account.employees.size() == 0">
			<div class="info">
				<s:text name="ManageEmployees.message.NoEmployees">
					<s:param>
						<s:text name="ManageEmployees.link.Add"/>
					</s:param>
					<s:param>
						<s:text name="ManageEmployees.link.Import"/>
					</s:param>
				</s:text>
			</div>
		</s:if>

		<a href="${add_employee}" class="add" id="addNewEmployee">
			<s:text name="ManageEmployees.link.Add"/>
		</a>
		<br/>

		<a href="javascript:;" class="add" id="import_excel" data-account="${account.id}">
			<s:text name="ManageEmployees.link.Import"/>
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
									<s:text name="Employee.lastName"/>
								</th>
								<th>
									<s:text name="Employee.firstName"/>
								</th>
								<th>
									<s:text name="Employee.title"/>
								</th>
								<th>
									<s:text name="Employee.classification"/>
								</th>
								<th>
									<s:text name="button.Edit"/>
								</th>
								<th>
									<s:text name="ManageEmployees.message.Profile"/>
								</th>
							</tr>
							</thead>

							<tbody>
							<s:iterator value="activeEmployees" id="currentEmployee">

								<s:url action="ManageEmployees" method="edit" var="edit_employee">
									<s:param name="account">${account.id}</s:param>
									<s:param name="employee">${currentEmployee.id}</s:param>
									<s:param name="audit">${audit.id}</s:param>
									<s:param name="questionId">${questionId}</s:param>
								</s:url>

								<s:set var="edit_profile_title" value="%{getText('ManageEmployees.title.EditProfile')}"/>

								<tr id="employee_${currentEmployee.id}">
									<td>
										<s:property value="#currentEmployee.id"/>
									</td>
									<td>
										<a
												href="${edit_employee}"
												class="load-employee"
												title="${edit_profile_title}"
												data-audit="${audit.id}"
												data-employee="${currentEmployee.id}"
												data-questionId="${questionId}"
												>
											<s:property value="#currentEmployee.lastName"/>
										</a>
									</td>
									<td>
										<a
												href="${edit_employee}"
												class="load-employee"
												title="${edit_profile_title}"
												data-audit="${audit.id}"
												data-employee="${currentEmployee.id}"
												data-questionId="${questionId}"
												>
											<s:property value="#currentEmployee.firstName"/>
										</a>
									</td>
									<td>
										<s:property value="#currentEmployee.title"/>
									</td>
									<td>
										<s:if test="#currentEmployee.classification != null">
											<s:text name="%{#currentEmployee.classification.getI18nKey('description')}"/>
										</s:if>
									</td>
									<td class="center">
										<a
												href="${edit_employee}"
												class="load-employee edit"
												title="${edit_profile_title}"
												data-audit="${audit.id}"
												data-employee="${currentEmployee.id}"
												data-questionId="${questionId}"
												></a>
									</td>
									<td class="center">
										<a
												href="EmployeeDetail.action?employee=<s:property value="#currentEmployee.id" />"
												class="preview"
												></a>
									</td>
								</tr>
							</s:iterator>
							</tbody>
						</table>

						<br clear="both"/>

						<a href="${employee_list_download}" class="excel">
							<s:text name="global.Download"/>
						</a>
					</td>
					<td style="width: 20px;"></td>
				</s:if>
			</tr>
		</table>

		<div id="siteEditBox"></div>
	</div>
</body>