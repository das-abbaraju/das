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
	<a href="EmployeeDetail.action?employee=<s:property value="employee.id" />"><s:text name="%{scope}.link.ViewProfile" /></a>
	<a href="#" class="help cluetip" rel="#cluetip1" title="View Profile/Assigned Tasks"></a>
	<div id="cluetip1"><s:text name="%{scope}.help.ProfileInfo" /></div>
	<br clear="all" />
</s:if>
<s:form id="employeeForm">
	<s:hidden name="id"/>
	<s:hidden name="employee" />
	<s:if test="!selectRolesSites">
		<fieldset class="form">
			<h2 class="formLegend"><s:text name="%{scope}.header.EmployeeDetails" /></h2>
			<ol>
				<li<s:if test="employee.firstName == null || employee.firstName == ''"> class="required"</s:if>><label>First Name:</label>
					<s:textfield name="employee.firstName" />
					<pics:fieldhelp title="First Name">
						<p>The first given name of the employee. This can include a middle initial or middle name if needed to differentiate between employees.</p>
						<h5>Examples:</h5>
						<ul>
							<li>John</li>
							<li>John Q.</li>
							<li>John Quincy</li>
						</ul>
					</pics:fieldhelp>
				</li>
				<li<s:if test="employee.lastName == null || employee.lastName == ''"> class="required"</s:if>><label>Last Name:</label>
					<s:textfield name="employee.lastName"/>
					<pics:fieldhelp title="Last Name">
						<p>The last name (aka family name) of the employee.</p>
					</pics:fieldhelp>
				</li>
				<li><label>Title:</label>
					<s:textfield id="titleSuggest" name="employee.title"/>
					<pics:fieldhelp title="Title">
					<p>The optional title of the employee.</p>
					<h5>Examples:</h5>
					<ul>
						<li>President</li>
						<li>Senior Engineer</li>
						<li>Apprentice</li>
					</ul>
					<p>Suggestions are based on common titles from all companies located in PICS Organizer.</p>
					</pics:fieldhelp>
				</li>
				<li><label>Birth Date:</label>
					<s:textfield name="employee.birthDate" value="%{maskDateFormat(employee.birthDate)}" cssClass="datepicker"/>
					<pics:fieldhelp title="Birth Date">
					<p>Optional date of birth field. Included for future use.</p>
					</pics:fieldhelp>
				</li>
				<li><label>Classification:</label>
					<s:select name="employee.classification" 
						list="@com.picsauditing.jpa.entities.EmployeeClassification@values()" 
						listValue="description" />
				</li>
				<li><label>Active</label>
					<s:checkbox name="employee.active" onclick="$('#termDate').toggle();"/>
					<pics:fieldhelp title="Active">
					<p>Unchecking this box will remove this employee from most reports. Uncheck this once the person no longer works for your company.</p>
					</pics:fieldhelp>
				</li>
				<li><label>Hire Date:</label>
					<s:textfield name="employee.hireDate" value="%{maskDateFormat(employee.hireDate)}" cssClass="datepicker"/>
					<pics:fieldhelp title="Hire Date">
					<p>The date (or best approximation) the employee first started working for this company.</p>
					</pics:fieldhelp>
				</li>
				<li id="termDate"><label>Termination Date:</label>
					<s:textfield name="employee.fireDate" value="%{maskDateFormat(employee.fireDate)}" cssClass="datepicker"/>
				</li>
				<s:if test="employee.id > 0">
					<s:if test="employee.photo.length() > 0">
						<li><label>Photo:</label>
							<a href="EmployeePhotoUpload.action?employeeID=<s:property value="employee.id"/>" class="edit"><img 
								id="cropPhoto" src="EmployeePhotoStream.action?employeeID=<s:property value="employee.id"/>" 
								style="width: 25px; height: 25px; vertical-align: bottom;" /></a>
						</li>
					</s:if>
					<s:else>
						<li><label>Upload Photo:</label>
							<a href="EmployeePhotoUpload.action?employeeID=<s:property value="employee.id"/>" class="add">Add </a>
						</li>
					</s:else>
				</s:if>
				<li><label>Email:</label>
					<s:textfield name="employee.email"/>
					<pics:fieldhelp title="Email">
					<p>The employee's primary work email address. This optional field is included for future use.
					PICS will not SPAM email addresses or share this address without your permission.</p>
					</pics:fieldhelp>
				</li>
				<li><label>Phone #:</label>
					<s:textfield name="employee.phone"/>
					<pics:fieldhelp title="Phone">
					<p>The employee's primary work phone. This field is optional.</p>
					</pics:fieldhelp>
				</li>
				<li><label>TWIC Card Expiration:</label>
					<s:textfield name="employee.twicExpiration" value="%{maskDateFormat(employee.twicExpiration)}" cssClass="datepicker"/>
					<pics:fieldhelp title="TWIC">
						<p>The expiration date of the employee's TWIC Card if available. If the card is in transit, please provide an estimate of the expiration date.</p>
					</pics:fieldhelp>
				</li>
				<li><label>SSN:</label>
					<s:textfield name="ssn" cssClass="ssn"/>
					<pics:fieldhelp title="Social Security Number">
					<p>The employee's Social Security Number issued by the United States. Leave blank if employee does not work in the USA.
					This field is NOT used directly by PICS. However some third party data providers require this number. You can always add it later if needed.</p>
					</pics:fieldhelp>
				</li>
				<li><label>Location:</label>
					<s:textfield name="employee.location" id="locationSuggest"/>
					<pics:fieldhelp title="Location">
					<p>The employee's primary work location. This could one of your own work locations or the location of one of your clients.</p>
					<h5>Examples:</h5>
					<ul>
						<li>Dallas</li>
						<li>Building C</li>
					</ul>
					<p>Suggestions based on common locations of other employees will appear after you start to type.</p>
					</pics:fieldhelp>
				</li>
			</ol>
		</fieldset>
	</s:if>
	<s:if test="employee.id > 0">
		<s:if test="employee.account.requiresCompetencyReview && (unusedJobRoles.size() + employee.employeeRoles.size()) > 0">
			<fieldset class="form">
				<h2 class="formLegend">Job Roles</h2>
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
											<th>Qualification Type</th>
											<th>Qualification Method</th>
											<th>Effective Date</th>
											<th>Expiration Date</th>
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
								<s:text name="%{scope}.label.EmployeeNCCERUploadMore" /><br />
								<a href="#" id="employee_nccer_link" class="add"><s:text name="%{scope}.link.EmployeeNCCERUpload" /></a>
							</li>
						</ol>
					</fieldset>
				</s:if>
				<s:else>
					<fieldset class="form">
						<h2 class="formLegend"><s:text name="%{scope}.label.EmployeeNCCERUpload" /></h2>
						<div class="info" id="nccerUploadFieldhelp">
							<s:text name="%{scope}.label.EmployeeNCCERUploadText" />
						</div>
						<ol>
							<li>
								<a href="#" id="employee_nccer_link" class="add"><s:text name="%{scope}.link.EmployeeNCCERUpload" /></a>
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
			<a href="ManageEmployees.action?employee=<s:property value="employee.id" />" class="picsbutton"><s:text name="%{scope}.message.ViewComplete" /></a>
		</fieldset>
	</s:else>
</s:form>