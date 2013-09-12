<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

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
