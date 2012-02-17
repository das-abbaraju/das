<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
	<head>
		<title>
			<s:text name="ManageEmployees.title" />
		</title>
		<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
		<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
		
		<s:include value="../jquery.jsp"/>
		<script type="text/javascript" src="js/manage_employees.js"></script>
		<script type="text/javascript" src="js/jquery/jquery.maskedinput-1.2.2.min.js"></script>
		
		<script type="text/javascript" src="js/jquery/dataTables/jquery.dataTables.min.js"></script>
		<link rel="stylesheet" href="js/jquery/dataTables/css/dataTables.css"/>
		
		<script type="text/javascript" src="js/jquery/blockui/jquery.blockui.js"></script>
		<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/blockui/blockui.css" />
		<style>
			div.dataTables_filter { width: 65%; }
			div.dataTables_length { width: 35%; }
			.newJobSite, #siteEditBox { display: none; }
			#newJobSiteForm { display: none; clear: both; }
			<s:if test="employee.id == 0 || employee.active">
				#termDate { display: none; }
			</s:if>
		</style>
		<script type="text/javascript">
			var employeeID = '<s:property value="employee == null ? 0 : employee.id"/>';
			var audit = '<s:property value="audit.id" />';
			
			var json_previousLocations = <s:property value="previousLocationsJSON" escape="false"/>;
			var json_previousTitles = <s:property value="previousTitlesJSON" escape="false"/>;
			
			$(function() {
				startup();
			});
			
			function showExcelUpload() {
				url = 'ManageEmployeesUpload.action?account=<s:property value="account.id" />';
				title = translate('JS.ManageEmployees.message.UploadEmployee');
				pars = 'scrollbars=yes,resizable=yes,width=650,height=400,toolbar=0,directories=0,menubar=0';
				fileUpload = window.open(url, title, pars);
				fileUpload.focus();
			}
		</script>
	</head>
	
	<body>
		<s:if test="audit.id > 0">
		<s:if test="questionId==3673" >
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
		<s:elseif test="questionId==3674">
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
		</s:elseif >
		</s:if>
		
		<h1>
			<s:property value="account.name" />
			<span class="sub">
				<s:text name="ManageEmployees.title" />
			</span>
		</h1>
		<s:include value="../actionMessages.jsp"/>
	
		<s:if test="account.employees.size() == 0 && employee == null">
			<div class="info">
				<s:text name="ManageEmployees.message.NoEmployees">
					<s:param>
						<s:text name="ManageEmployees.link.Add" />
					</s:param>
				</s:text>
			</div>
		</s:if>
	
		<s:url action="ManageEmployees" method="add" var="addEmployee">
			<s:param name="account" value="%{account.id}" />
			<s:param name="audit" value="%{audit.id}" />
			<s:param name="questionId" value="%{questionId}" />
		</s:url>
		<a href="${addEmployee}" class="add">
			<s:text name="ManageEmployees.link.Add" />
		</a>
		<br />
		<a href="#" class="add" id="addExcel">
			<s:text name="ManageEmployees.link.Import" />
		</a>
		
		<table>
			<tr>
				<s:if test="account.employees.size() > 0">
					<td style="vertical-align:top; width: 25%;">
						<table class="report" id="employees">
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
								<s:iterator value="account.employees" id="e">
									<tr>
										<td>
											<s:property value="#e.id" />
										</td>
										<td>
											<a
												href="#employee=<s:property value="#e.id" />"
												class="loadEmployee"
												title="<s:text name="ManageEmployees.title.EditProfile" />"
												data-audit="<s:property value="audit.id" />"
												data-questionId="<s:property value="questionId" />"
											>
												<s:property value="#e.lastName" />
											</a>
										</td>
										<td>
											<a
												href="#employee=<s:property value="#e.id" />"
												class="loadEmployee"
												title="<s:text name="ManageEmployees.title.EditProfile" />"
												data-audit="<s:property value="audit.id" />"
												data-questionId="<s:property value="questionId" />"
											>
												<s:property value="#e.firstName" />
											</a>
										</td>
										<td>
											<s:property value="#e.title" />
										</td>
										<td>
											<s:text name="%{#e.classification.getI18nKey('description')}" />
										</td>
										<td class="center">
											<a
												href="#employee=<s:property value="#e.id" />"
												class="loadEmployee edit"
												title="<s:text name="ManageEmployees.title.EditProfile" />"
												data-audit="<s:property value="audit.id" />"
												data-questionId="<s:property value="questionId" />"
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
						<a href="EmployeeList!download.action?filter.accountName=<s:property value="account.id" />" class="excel"><s:text name="global.Download" /></a>
					</td>
					<td style="width: 20px;"></td>
				</s:if>
				<td style="vertical-align:top;">
					<div id="employeeFormDiv">
						<s:if test="employee != null && employee.id == 0">
							<s:include value="manage_employees_form.jsp" />
						</s:if>
					</div>
				</td>
			</tr>
		</table>
		
		<div id="siteEditBox"></div>
	</body>
</html>