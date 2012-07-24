<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:if test="employee.employeeSites.size > 0">
	<fieldset class="form">
		<h2 class="formLegend">
			<s:text name="ManageEmployees.header.HSESites" />
		</h2>
		<ol>
			<li>
				<table class="report" style="width: 500px;">
					<thead>
						<tr>
							<th>
								<s:text name="global.Operator" />
							</th>
							<th>
								<s:text name="ManageEmployees.label.Since" />
							</th>
							<th>
								<s:text name="ManageEmployees.label.Orientation" />
							</th>
							<th>
								<s:text name="button.Edit" />
							</th>
						</tr>
					</thead>
					<s:iterator value="employee.employeeSites" var="site">
						<s:if test="#site.current && #site.jobSite == null">
							<tr>
								<td>
									${operator.name}
								</td>
								<td>
									<s:date name="effectiveDate" />
								</td>
								<td>
									<s:date name="orientationDate" />
								</td>
								<td class="center">
									<s:set name="edit_site_title">
										<s:text name="ManageEmployees.header.EditSiteProject">
											<s:param>
												${operator.name}
											</s:param>
											<s:param value="0" />
										</s:text>
									</s:set>
									<a
										href="javascript:;"
										class="edit site"
										data-employee="${employee.id}"
										data-site="${id}"
										title="${edit_site_title}"></a>
								</td>
							</tr>
						</s:if>
					</s:iterator>
					<tr>
						<td colspan="4">
							<s:if test="hseOperators.size > 0">
								<s:select
									data-employee="${employee.id}"
									headerKey=""
									headerValue=" - %{getText('ManageEmployees.header.AssignSite')} - "
									id="hse_operator_list"
									list="hseOperators"
									listKey="id"
									listValue="name" />
							</s:if>
							<s:else>
								<h5>
									<s:text name="ManageEmployees.message.AssignedAllHSESites" />
								</h5>
							</s:else>
						</td>
					</tr>
				</table>
				<s:if test="hseOperators.size > 0">
					<div class="fieldhelp" title="<s:text name="ManageEmployees.label.AddHSESite" />">
						<p>
							<s:text name="ManageEmployees.label.AddHSESite.fieldhelp" />
						</p>
					</div>
				</s:if>
			</li>
		</ol>
	</fieldset>
	
	<s:if test="employee.account.requiresOQ">
		<s:include value="site_oq.jsp" />
	</s:if>
</s:if>
<s:if test="employee.prevAssigned">
	<fieldset class="form">
		<h2 class="formLegend">
			<s:text name="ManageEmployees.header.PreviouslyAssignedSitesProjects" />
		</h2>
		<ol>
			<li>
				<table class="report">
					<thead>
						<tr>
							<th>
								<s:text name="global.Type" />
							</th>
							<th>
								<s:text name="global.Operator" />
							</th>
							<th>
								<s:text name="JobSite.name" />
							</th>
							<th>
								<s:text name="JobSite.projectStop" />
							</th>
						</tr>
					</thead>
					<s:iterator value="employee.employeeSites" var="site" status="stat">
						<s:if test="!#site.current">
							<tr>
								<td>
									<s:if test="jobSite.id > 0">
										<s:text name="ManageEmployees.header.OQProjects" />
									</s:if>
									<s:else>
										<s:text name="ManageEmployees.header.HSESites" />
									</s:else>
								</td>
								<td>
									<s:property value="operator.name" />
								</td>
								<td>
									<s:if test="jobSite.id > 0">
										${jobSite.id}
									</s:if>
								</td>
								<td>
									<s:date name="expirationDate" format="%{getText('date.short')}" />
								</td>
							</tr>
						</s:if>
					</s:iterator>
				</table>
			</li>
		</ol>
	</fieldset>
</s:if>