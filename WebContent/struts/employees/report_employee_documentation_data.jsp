<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="report.allRows == 0">
	<div class="alert">
		<s:text name="Report.message.NoRowsFound" />
	</div>
</s:if>
<s:else>
	<div class="right">
		<a class="excel" <s:if test="report.allRows > 500">onclick="return confirm('<s:text name="JS.ConfirmDownloadAllRows"><s:param value="%{report.allRows}" /></s:text>');"</s:if>
		   href="javascript: download('ReportEmployeeDocumentation');"
		   title="<s:text name="javascript.DownloadAllRows"><s:param value="%{report.allRows}" /></s:text>">
			<s:text name="global.Download" />
		</a>
	</div>

	<div>
		<s:property value="report.pageLinksWithDynamicForm" escape="false" />
	</div>

	<table class="report">
		<thead>
		<tr>
			<th class="right"></th>
			<th>
				<a href="javascript:;" data-orderby="name,lastName,firstName">
					<s:text name="global.Contractor"/>
				</a>
			</th>
			<th>
				<a href="javascript:;" data-orderby="TRIM(firstName)">
					<s:text name="Employee.firstName"/>
				</a>
			</th>
			<th>
				<a href="javascript:;" data-orderby="TRIM(lastName)">
					<s:text name="Employee.lastName"/>
				</a>
			</th>
			<s:if test="permissions.picsEmployee || permissions.corporate">
				<th>
					<s:text name="global.Operator"/>
				</th>
			</s:if>
			<th>
				<s:text name="OperatorCompetency.label"/>
			</th>
			<th>
				<a href="javascript:;" data-orderby="expiration DESC">
					<s:text name="global.ExpirationDate"/>
				</a>
			</th>
		</tr>
		</thead>
		<tbody>
			<s:iterator value="data" var="competency_file" status="status">
				<s:url var="employee_profile" action="EmployeeDetail">
					<s:param name="employee">
						${competency_file.get('employeeID')}
					</s:param>
				</s:url>
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
						<a href="${employee_profile}">
							${competency_file.get('firstName')}
						</a>
					</td>
					<td>
						<a href="${employee_profile}">
							${competency_file.get('lastName')}
						</a>
					</td>
					<s:if test="permissions.picsEmployee || permissions.corporate">
						<td>
							${competency_file.get('opName')}
						</td>
					</s:if>
					<td>
						${competency_file.get('label')}
					</td>

                    <%-- Set highlight if urgent --%>
                    <s:set var="urgency_highlight"></s:set>
                    <s:if test="#competency_file.get('fileStatus') == 'Needed'">
                        <s:set var="urgency_highlight">needed</s:set>
                    </s:if>
                    <s:elseif test="#competency_file.get('otherExpiration') == 'Expired'">
                        <s:set var="urgency_highlight">expired</s:set>
                    </s:elseif>

                    <%-- Set url for link --%>
                    <s:url var="skills_training" action="EmployeeSkillsTraining">
                        <s:param name="employee">
                            ${competency_file.get('employeeID')}
                        </s:param>
                    </s:url>

                    <%-- Get expiration date or 'needed' --%>
                    <s:if test="#competency_file.get('expiration')">
                        <s:set var="document_status">${competency_file.get('expiration')}</s:set>
                    </s:if>
                    <s:else>
                        <s:set var="document_status">${competency_file.get('fileStatus')}</s:set>
                    </s:else>

					<td class="${urgency_highlight}">
                        <a href="${skills_training}">${document_status}</a>
					</td>
				</tr>
			</s:iterator>
		</tbody>
	</table>

	<div>
		<s:property value="report.pageLinksWithDynamicForm" escape="false" />
	</div>
</s:else>