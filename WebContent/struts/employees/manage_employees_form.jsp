<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<script type="text/javascript">
employeeID = <s:property value="employee == null ? 0 : employee.id"/>;
$(function() {
	setupEmployee();
	
	<s:if test="employee.id != 0">
		<s:if test="employee.active">
			$('#termDate').hide();
		</s:if>
		<s:else>
			$('#termDate').show();
		</s:else>
	</s:if>
	<s:else>
		$('#termDate').hide();
	</s:else>
});
</script>
<s:if test="employee.id > 0">
	<a href="EmployeeDetail.action?employee=<s:property value="employee.id" />">
		<s:text name="ManageEmployees.link.ViewProfile" />
	</a>
	<a href="#" class="help cluetip" rel="#cluetip1" title="<s:text name="ManageEmployees.ViewProfileAssignedTasks" />"></a>
	<div id="cluetip1">
		<s:text name="ManageEmployees.help.ProfileInfo" />
	</div>
	<br clear="all" />
</s:if>
<s:form id="employeeForm">
	<s:hidden name="account" />
	<s:hidden name="audit" />
	<s:hidden name="employee" />
	<s:hidden name="questionId" />
	<s:if test="!selectRolesSites">
		<fieldset class="form">
			<h2 class="formLegend"><s:text name="ManageEmployees.header.EmployeeDetails" /></h2>
			<ol>
				<li<s:if test="employee.firstName == null || employee.firstName == ''"> class="required"</s:if>>
					<s:textfield name="employee.firstName" theme="formhelp" />
				</li>
				<li<s:if test="employee.lastName == null || employee.lastName == ''"> class="required"</s:if>>
					<s:textfield name="employee.lastName" theme="formhelp" />
				</li>
				<li><s:textfield id="titleSuggest" name="employee.title" theme="formhelp" /></li>
				<li>
					<s:select name="employee.classification" 
						list="@com.picsauditing.jpa.entities.EmployeeClassification@values()" 
						listValue="getText(getI18nKey('description'))" theme="formhelp" />
				</li>
				<li><s:checkbox name="employee.active" id="employeeActive" theme="formhelp" /></li>
				<li>
					<s:textfield name="employee.hireDate" value="%{maskDateFormat(employee.hireDate)}"
						cssClass="datepicker" theme="formhelp" />
				</li>
				<li id="termDate">
					<s:textfield name="employee.fireDate" value="%{maskDateFormat(employee.fireDate)}"
						cssClass="datepicker" theme="formhelp" />
				</li>
				<s:if test="employee.id > 0">
					<s:if test="employee.photo.length() > 0">
						<li><label><s:text name="Employee.photo" />:</label>
							<a href="EmployeePhotoUpload.action?employee=<s:property value="employee.id"/>" class="edit">
								<img id="cropPhoto" src="EmployeePhotoStream.action?employeeID=<s:property value="employee.id"/>" 
								style="width: 25px; height: 25px; vertical-align: bottom;" />
							</a>
						</li>
					</s:if>
					<s:else>
						<li><label><s:text name="ManageEmployees.label.UploadPhoto" />:</label>
							<a href="EmployeePhotoUpload.action?employee=<s:property value="employee.id"/>" class="add">
								<s:text name="button.Add" />
							</a>
						</li>
					</s:else>
				</s:if>
				<li><s:textfield name="employee.email" theme="formhelp" /></li>
				<li><s:textfield name="employee.phone" theme="formhelp" /></li>
				<li>
					<s:textfield name="employee.twicExpiration" value="%{maskDateFormat(employee.twicExpiration)}" 
						cssClass="datepicker" theme="formhelp" />
				</li>
			</ol>
		</fieldset>
	</s:if>
	<s:if test="employee.id > 0">
		<s:if test="showJobRolesSection">
			<fieldset class="form">
				<h2 class="formLegend"><s:text name="ManageEmployees.header.JobRoles" /></h2>
				<div id="employee_role">
					<s:include value="manage_employee_roles.jsp" />
				</div>
			</fieldset>
		</s:if>
		<div id="employee_site">
			<s:include value="manage_employee_sites.jsp" />
		</div>
		<s:if test="employee.account.requiresOQ">
			<div id="employee_nccer">
				<s:if test="nccerResults.size > 0">
					<fieldset class="form">
						<h2 class="formLegend"><s:text name="EmployeeDetail.label.NCCERAssessmentData" /></h2>
						<ol>
							<li>
								<table class="report">
									<thead>
										<tr>
											<th><s:text name="AssessmentTest.qualificationType" /></th>
											<th><s:text name="AssessmentTest.qualificationMethod" /></th>
											<th><s:text name="AssessmentTest.effectiveDate" /></th>
											<th><s:text name="AssessmentTest.expirationDate" /></th>
										</tr>
									</thead>
									<tbody>
										<s:iterator value="nccerResults">
											<tr>
												<td><s:property value="assessmentTest.qualificationType" /></td>
												<td><s:property value="assessmentTest.qualificationMethod" /></td>
												<td><s:date name="effectiveDate" /></td>
												<td><s:date name="expirationDate" /></td>
											</tr>
										</s:iterator>
									</tbody>
								</table>
							</li>
							<li>
								<s:text name="ManageEmployees.label.EmployeeNCCERUploadMore" /><br />
								<a href="#" id="employee_nccer_link" class="add"><s:text name="ManageEmployees.link.EmployeeNCCERUpload" /></a>
							</li>
						</ol>
					</fieldset>
				</s:if>
				<s:else>
					<fieldset class="form">
						<h2 class="formLegend"><s:text name="ManageEmployees.label.EmployeeNCCERUpload" /></h2>
						<div class="info" id="nccerUploadFieldhelp">
							<s:text name="ManageEmployees.label.EmployeeNCCERUploadText" />
						</div>
						<ol>
							<li>
								<a href="#" id="employee_nccer_link" class="add"><s:text name="ManageEmployees.link.EmployeeNCCERUpload" /></a>
							</li>
						</ol>
					</fieldset>
				</s:else>
			</div>
		</s:if>
	</s:if>
	<s:if test="!selectRolesSites">
		<fieldset class="form submit">
			<s:if test="auditID > 0 && employee.id == 0"><s:hidden name="button" value="Continue" /></s:if>
			<s:submit method="save" cssClass="picsbutton positive" value="%{auditID > 0 && employee.id == 0 ? getText('button.Continue') : getText('button.Save')}" />
			<s:submit method="delete" cssClass="picsbutton negative" id="deleteEmployee" value="%{getText('button.Delete')}" />
		</fieldset>
	</s:if>
	<s:else>
		<fieldset class="form submit" style="text-align: center;">
			<a href="ManageEmployees.action?employee=<s:property value="employee.id" />" class="picsbutton"><s:text name="ManageEmployees.message.ViewComplete" /></a>
		</fieldset>
	</s:else>
</s:form>