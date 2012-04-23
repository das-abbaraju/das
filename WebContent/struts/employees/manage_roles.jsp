<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
	<head>
		<title>
			<s:text name="ManageJobRoles.title" />
		</title>
		
		<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
		
		<style type="text/css">
			#roleForm {
				clear: right;
			}
			
			#rolesTable td.leftCell, #rolesTable td.rightCell {
				vertical-align: top;
			}
			
			#rolesTable table.report {
				margin-right: 10px;
				margin-bottom: 10px;
			}
			
			fieldset.form label {
				width: 5em;
				margin-right: 0px;
			}
			
			td.leftCell {
				width: 50%;
			}
			
			td.rightCell {
				padding-left: 10px;
			}
		</style>
	</head>
	<body>
		<h1>
			<s:property value="account.name" />
			<span class="sub">
				<s:text name="ManageJobRoles.title" />
			</span>
		</h1>
		<s:include value="../actionMessages.jsp" />
		
		<s:if test="audit.id > 0">
			<s:if test="questionId==3669" >
				<div class="info">
				<s:text name="ManageJobRoles.Step1">
					<s:param>
						<s:property value="audit.id" />
					</s:param>
					<s:param>
						<s:text name="AuditType.99.name" />
					</s:param>
				</s:text>
			</div>
			</s:if>
			<s:elseif test="questionId==3675" >
			<div class="info">
				<s:text name="ManageJobRoles.Step2">
					<s:param>
						<s:property value="audit.id" />
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
			</s:elseif >
		</s:if>
		
		<s:if test="audit == null" >
			<div>
				<a href="EmployeeDashboard.action?id=<s:property value='account.id' />" >
					<s:text name="global.EmployeeGUARD" />
				</a>
			</div>
		</s:if>
		
		
		<table id="rolesTable">
			<tr>
				<td class="leftCell">
					<s:if test="jobRoles.size > 0">
						<table class="report">
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
								<s:iterator value="jobRoles">
									<tr>
										<td>
											<a
												href="#"
												id="<s:property value="id" />"
												class="roleLink<s:if test="!active"> inactive</s:if>"
												data-account="<s:property value="account.id" />"
												data-audit="<s:property value="audit.id" />"
												data-questionId="<s:property value="questionId" />"
											>
												<s:property value="name" />
											</a>
										</td>
										<td class="center">
											<s:if test="active">
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
						href="#"
						id="addLink"
						class="add"
						data-account="<s:property value="account.id" />"
						data-audit="<s:property value="audit.id" />"
						data-questionId="<s:property value="questionId" />"
					>
						<s:text name="ManageJobRoles.link.AddNewJobRole" />
					</a>
					
				</td>
				<td class="rightCell">
					<div id="roleCell"></div>
				</td>
			</tr>
		</table>
		<s:include value="../jquery.jsp" />
		<script type="text/javascript" src="js/core.js?v=${version}"></script>
		<script type="text/javascript" src="js/employee_manage_job_roles.js?v=${version}"></script>
	</body>
</html>
