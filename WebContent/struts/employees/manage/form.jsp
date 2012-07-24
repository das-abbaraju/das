<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

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
<s:form>
	<s:hidden name="account" />
	<s:hidden name="audit" />
	<s:hidden name="employee" />
	<s:hidden name="questionId" />
	<fieldset class="form">
		<h2 class="formLegend">
			<s:text name="ManageEmployees.header.EmployeeDetails" />
		</h2>
		<ol>
			<li <s:if test="employee.firstName == null || employee.firstName == ''"> class="required"</s:if>>
				<s:textfield name="employee.firstName" theme="formhelp" />
			</li>
			<li <s:if test="employee.lastName == null || employee.lastName == ''"> class="required"</s:if>>
				<s:textfield name="employee.lastName" theme="formhelp" />
			</li>
			<li>
				<s:textfield id="titleSuggest" name="employee.title" theme="formhelp" data-json="${previousTitlesJSON}" />
			</li>
			<li>
				<s:select
					name="employee.classification"
					list="@com.picsauditing.jpa.entities.EmployeeClassification@values()"
					listValue="getText(getI18nKey('description'))"
					theme="formhelp" />
			</li>
			<li>
				<s:textfield
					name="employee.hireDate"
					value="%{maskDateFormat(employee.hireDate)}"
					cssClass="datepicker"
					theme="formhelp" />
			</li>
			<li id="termDate">
				<s:textfield
					name="employee.fireDate"
					value="%{maskDateFormat(employee.fireDate)}"
					cssClass="datepicker"
					theme="formhelp" />
			</li>
			<s:if test="employee.id > 0">
				<s:url action="EmployeePhotoUpload" var="employee_photo_upload">
					<s:param name="employee">
						${employee.id}
					</s:param>
				</s:url>
				<s:if test="employee.photo.length() > 0">
					<li>
						<label><s:text name="Employee.photo" />:</label>
						<s:url action="EmployeePhotoStream" var="employee_photo_crop">
							<s:param name="employeeID">
								${employee.id}
							</s:param>
						</s:url>
						<a href="${employee_photo_upload}" class="edit">
							<img id="cropPhoto" src="${employee_photo_crop}" style="width: 25px; height: 25px; vertical-align: bottom;" />
						</a>
					</li>
				</s:if>
				<s:else>
					<li>
						<label><s:text name="ManageEmployees.label.UploadPhoto" />:</label>
						<a href="${employee_photo_upload}" class="add">
							<s:text name="button.Add" />
						</a>
					</li>
				</s:else>
			</s:if>
			<li>
				<s:textfield name="employee.email" theme="formhelp" />
			</li>
			<li>
				<s:textfield name="employee.phone" theme="formhelp" />
			</li>
			<li>
				<s:textfield
					name="employee.twicExpiration"
					value="%{maskDateFormat(employee.twicExpiration)}"
					cssClass="datepicker"
					theme="formhelp"
				/>
			</li>
			<s:if test="employee.id == 0 && account.contractor">
				<li>
					<label>
						<s:text name="ManageEmployees.EmployeesFacilities" />
					</label>
					<s:iterator value="account.operatorAccounts" var="site">
						<input type="checkbox" name="initialSites" value="${site.id}" id="site_${site.id}" />
						<label for="site_${site.id}" class="sites-label">
							${site.name}
						</label>
						<br />
					</s:iterator>
				</li>
			</s:if>
		</ol>
	</fieldset>
	<s:if test="employee.id > 0">
		<s:if test="showJobRolesSection">
			<fieldset class="form">
				<h2 class="formLegend">
					<s:text name="ManageEmployees.header.JobRoles" />
				</h2>
				<div id="employee_role">
					<s:include value="role.jsp" />
				</div>
			</fieldset>
		</s:if>
		<div id="employee_site">
			<s:include value="site.jsp" />
		</div>
		<s:if test="employee.account.requiresOQ">
			<div id="employee_nccer">
				<s:if test="nccerResults.size > 0">
					<fieldset class="form">
						<h2 class="formLegend">
							<s:text name="EmployeeDetail.label.NCCERAssessmentData" />
						</h2>
						<ol>
							<li>
								<table class="report">
									<thead>
										<tr>
											<th>
												<s:text name="AssessmentTest.qualificationType" />
											</th>
											<th>
												<s:text name="AssessmentTest.qualificationMethod" />
											</th>
											<th>
												<s:text name="AssessmentTest.effectiveDate" />
											</th>
											<th>
												<s:text name="AssessmentTest.expirationDate" />
											</th>
										</tr>
									</thead>
									<tbody>
										<s:iterator value="nccerResults">
											<tr>
												<td>
													<s:property value="assessmentTest.qualificationType" />
												</td>
												<td>
													<s:property value="assessmentTest.qualificationMethod" />
												</td>
												<td>
													<s:date name="effectiveDate" />
												</td>
												<td>
													<s:date name="expirationDate" />
												</td>
											</tr>
										</s:iterator>
									</tbody>
								</table>
							</li>
							<li>
								<s:text name="ManageEmployees.label.EmployeeNCCERUploadMore" />
								<br />
								<a href="javascript:;" id="employee_nccer_link" class="add">
									<s:text name="ManageEmployees.link.EmployeeNCCERUpload" />
								</a>
							</li>
						</ol>
					</fieldset>
				</s:if>
				<s:else>
					<fieldset class="form">
						<h2 class="formLegend">
							<s:text name="ManageEmployees.label.EmployeeNCCERUpload" />
						</h2>
						<div
							class="info"
							id="nccerUploadFieldhelp">
							<s:text name="ManageEmployees.label.EmployeeNCCERUploadText" />
						</div>
						<ol>
							<li>
								<a href="javascript:;" id="employee_nccer_link" class="add" data-employee="${employee.id}">
									<s:text name="ManageEmployees.link.EmployeeNCCERUpload" />
								</a>
							</li>
						</ol>
					</fieldset>
				</s:else>
			</div>
		</s:if>
	</s:if>
	<fieldset class="form submit">
		<s:if test="employee.status.toString().equals('Active')">
			<s:submit method="save" cssClass="picsbutton positive" value="%{getText('button.Save')}" />
			<s:if test="employee.id > 0">
				<s:submit method="inactivate" cssClass="picsbutton negative" value="%{getText('button.Inactivate')}" />
			</s:if>
		</s:if>
		<s:else>
			<s:submit method="activate" cssClass="picsbutton positive" value="%{getText('button.Activate')}" />
			<s:submit method="delete" cssClass="picsbutton negative" id="deleteEmployee" value="%{getText('button.Delete')}" />
		</s:else>
	</fieldset>
</s:form>