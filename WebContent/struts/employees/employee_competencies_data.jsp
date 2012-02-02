<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:if test="report.allRows == 0">
	<div class="alert"><s:text name="Report.message.NoRowsFound" /></div>
</s:if>
<s:else>
	<div>
		<s:property value="report.pageLinksWithDynamicForm" escape="false" />
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
				<s:iterator value="competencies">
					<th>
						<s:property value="label" />
						<img
							src="images/help.gif"
							alt="<s:property value="label" />"
							title="<s:property value="category" />: <s:property value="description" />"
						/>
					</th>
				</s:iterator>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="employees" var="e">
				<tr>
					<td>
						<a href="EmployeeDetail.action?employee=<s:property value="#e.id" />">
							<s:property value="#e.lastName" />, <s:property value="#e.firstName" />
						</a>
					</td>
					<td>
						<s:property value="employeeJobRoles.get(#e)" />
					</td>
					<s:iterator value="competencies" var="c">
						<s:if test="map.get(#e, #c) != null">
							<td class="center<s:if test="map.get(#e, #c).skilled"> green</s:if><s:else> red</s:else>">
								<s:if test="permissions.contractor">
									<input
										type="checkbox"
										<s:if test="map.get(#e, #c).skilled">checked="checked" </s:if>
										id="<s:property value="#e.id" />_<s:property value="#c.id" />"
									/>
								</s:if>
								<s:else>
									<s:if test="map.get(#e, #c).skilled"><img src="images/okCheck.gif" /></s:if>
									<s:else><img src="images/notOkCheck.gif" /></s:else>
								</s:else>
							</td>
						</s:if>
						<s:else><td></td></s:else>
					</s:iterator>
				</tr>
			</s:iterator>
		</tbody>
	</table>
	
	<div>
		<s:property value="report.pageLinksWithDynamicForm" escape="false" />
	</div>
</s:else>

<div id="messages" style="clear: both;"><s:include value="../actionMessages.jsp" /></div>