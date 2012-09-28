<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<head>
	<title>
		<s:text name="ManageJobRoles.title" />
	</title>
	
	<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=${version}" />
	<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=${version}" />
	
	<style type="text/css">
		.fill
		{
			padding: 0 !important;
			width: 100%;
		}
		
		.column
		{
			padding-right: 10px;
			width: 50%;
		}
		
		.spacer
		{
			width: 25px;
		}
		
		table.competencies td
		{
			border-bottom: 1px solid #E4E4E4;
		}
	</style>
</head>
<body>
	<div id="${actionName}_${methodName}_page" class="${actionName}-page page">
		<h1>
			${account.name} 
			<span class="sub">
				<s:text name="ManageJobRoles.title" />
			</span>
		</h1>
		<s:include value="../actionMessages.jsp" />
	
		<s:if test="audit.id > 0">
			<s:if test="questionId==3669">
				<div class="info">
					<s:text name="ManageJobRoles.Step1">
						<s:param>
							${audit.id}
						</s:param>
						<s:param>
							<s:text name="AuditType.99.name" />
						</s:param>
					</s:text>
				</div>
			</s:if>
			<s:elseif test="questionId==3675">
				<div class="info">
					<s:text name="ManageJobRoles.Step2">
						<s:param>
							${audit.id}
						</s:param>
						<s:param>
							<s:text name="AuditType.99.name" />
						</s:param>
					</s:text>
				</div>
				<div class="info">
					<a href="resources/HSECompetencyReview.pdf">
						<s:text name="ManageJobRoles.link.QuestionReviewPDF" />
					</a>
					<br />
					<s:text name="ManageJobRoles.help.QuestionReviewPDF" />
				</div>
			</s:elseif>
		</s:if>
	
		<s:if test="audit == null">
			<div>
				<a href="EmployeeDashboard.action?id=<s:property value='account.id' />">
					<s:text name="global.EmployeeGUARD" />
				</a>
			</div>
		</s:if>
	
		<table id="roles_table">
			<tr>
				<td class="column">
					<s:if test="jobRoles.size > 0">
						<table class="report normal">
							<thead>
								<tr>
									<th>
										<s:text name="ManageJobRoles.label.JobRole" />
									</th>
									<th>
										<s:text name="global.Active" />
									</th>
								</tr>
							</thead>
							<tbody>
								<s:iterator value="jobRoles" var="existing">
									<tr>
										<td>
											<a
												href="javascript:;"
												class="role-link <s:if test="!active">inactive</s:if>"
												data-account="${account.id}"
												data-audit="${audit.id}"
												data-questionId="${questionId}"
												data-role="${existing.id}">
												${existing.name}
											</a>
										</td>
										<td class="center">
											<s:if test="#existing.active">
												<s:text name="YesNo.Yes" />
											</s:if>
											<s:else>
												<s:text name="YesNo.No" />
											</s:else>
										</td>
									</tr>
								</s:iterator>
							</tbody>
						</table>
					</s:if>
					<a
						href="javascript:;"
						class="add role-link"
						data-account="${account.id}"
						data-audit="${audit.id}"
						data-questionId="${questionId}">
						<s:text name="ManageJobRoles.link.AddNewJobRole" />
					</a>
	
				</td>
				<td class="column">
					<div id="edit_role"></div>
				</td>
			</tr>
		</table>
	</div>
</body>