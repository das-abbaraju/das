<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:if test="report.allRows == 0">
	<div class="alert">
		<s:text name="Report.message.NoRowsFound" />
	</div>
</s:if>
<s:else>
	<div>
		${report.pageLinksWithDynamicForm}
	</div>

	<table class="report">
		<thead>
			<tr>
				<th>
					<s:text name="global.Employees" />
				</th>
				<th>
					<s:text name="JobRole" />
				</th>
				<s:iterator value="employeeCompetencyTable.columnKeySet()" var="competency">
					<th>
						${competency.label}
						<img
							src="images/help.gif"
							alt="${competency.label}"
							title="${competency.category}: ${competency.description}"
						/>
					</th>
				</s:iterator>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="employeeCompetencyTable.rowKeySet()" var="employee">
				<tr>
					<td>
						<s:url action="EmployeeDetail" var="employee_detail">
							<s:param name="employee">
								${employee.id}
							</s:param>
						</s:url>
						<a href="${employee_detail}">
							${employee.lastName}, ${employee.firstName}
						</a>
					</td>
					<td>
						${employeeJobRoles.get(employee)}
					</td>
					<s:iterator value="employeeCompetencyTable.columnKeySet()" var="competency">
						<s:if test="employeeCompetencyTable.get(#employee, #competency) != null">
							<s:set name="competent" value="%{employeeCompetencyTable.get(#employee, #competency).skilled}" />
							<td class="center<s:if test="#competent"> green</s:if><s:else> red</s:else>">
								<s:if test="permissions.contractor">
									<input
										type="checkbox"
										<s:if test="#competent">checked="checked" </s:if>
										id="${employee.id}_${competency.id}"
									/>
								</s:if>
								<s:else>
									<s:if test="#competent">
										<img alt="X" src="images/okCheck.gif" />
									</s:if>
									<s:else>
										<img alt="" src="images/notOkCheck.gif" />
									</s:else>
								</s:else>
							</td>
						</s:if>
						<s:else>
							<td></td>
						</s:else>
					</s:iterator>
				</tr>
			</s:iterator>
		</tbody>
	</table>
	
	<div>
		${report.pageLinksWithDynamicForm}
	</div>
</s:else>

<div id="messages" style="clear: both;">
	<s:include value="../actionMessages.jsp" />
</div>