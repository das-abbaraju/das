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
					<s:iterator value="employee.employeeSites" id="site" status="stat">
						<s:if test="#site.current && #site.jobSite == null">
							<tr>
								<td>
									<s:property value="operator.name" />
								</td>
								<td>
									<s:date name="effectiveDate" />
								</td>
								<td>
									<s:date name="orientationDate" />
								</td>
								<td class="center">
									<a href="javascript:;" data-site="${id}" class="edit site"></a>
								</td>
							</tr>
						</s:if>
					</s:iterator>
					<tr>
						<td colspan="4">
							<s:if test="hseOperators.size > 0">
								<s:select
									list="hseOperators"
									listKey="id"
									listValue="name"
									headerKey=""
									headerValue=" - %{getText('ManageEmployees.header.AssignSite')} - "
									id="hseOperator" />
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
</s:if>
<s:if test="employee.account.requiresOQ">
	<fieldset class="form">
		<h2 class="formLegend">
			<s:text name="ManageEmployees.header.OQProjects" />
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
								<s:text name="ManageEmployees.label.Project" />
							</th>
							<th>
								<s:text name="ManageEmployees.label.Since" />
							</th>
							<th>
								<s:text name="button.Edit" />
							</th>
							<th>
								<s:text name="ManageEmployees.label.Tasks" />
							</th>
						</tr>
					</thead>
					<s:set name="oqSiteCount" value="0" />
					<s:iterator value="employee.employeeSites" id="site" status="stat">
						<s:if test="#site.current && #site.jobSite != null">
							<s:set name="oqSiteCount" value="#oqSiteCount + 1" />
							<tr>
								<td>
									<s:property value="operator.name" />
								</td>
								<td>
									<s:property value="#site.jobSite.label" />
								</td>
								<td>
									<s:property value="effectiveDate" />
								</td>
								<td class="center">
									<a href="javascript:;" data-site="${id}" class="edit site"></a>
								</td>
								<td class="center">
									<a href="javascript:;" class="preview qualifiedTasks" id="statCount_<s:property value="#stat.count" />"></a>
								</td>
							</tr>
							<tr
								id="<s:property value="#stat.count" />_tasks"
								class="qualifiedTasks"
								style="display: none;">
								<td
									colspan="5"
									style="padding: 10px">
									<s:set
										name="missingTasks"
										value="getMissingTasks(#site.jobSite.id)" />
									<h4>
										<s:text name="ManageEmployees.label.QualifiedTasks" />
									</h4>
									<s:if test="#missingTasks.qualifiedTasks.size > 0">
										<h5>
											<s:text name="ManageEmployees.label.NumberQualified">
												<s:param value="%{#missingTasks.qualifiedTasks.size}" />
												<s:param value="%{#missingTasks.totalCount}" />
											</s:text>
										</h5>
										<table class="report">
											<thead>
												<tr>
													<th>
														<s:text name="JobTask.label" />
													</th>
													<th>
														<s:text name="JobTask.name" />
													</th>
												</tr>
											</thead>
											<tbody>
												<s:iterator
													value="#missingTasks.qualifiedTasks"
													var="mt"
													status="mtStat">
													<tr>
														<td>
															<s:property value="#mt.label" />
														</td>
														<td>
															<s:property value="#mt.name" />
														</td>
													</tr>
												</s:iterator>
											</tbody>
										</table>
									</s:if>
									<s:if test="#missingTasks.missingTasks.size > 0">
										<h5>
											<s:text name="ManageEmployees.label.NumberMissing">
												<s:param value="%{#missingTasks.missingTasks.size}" />
												<s:param value="%{#missingTasks.totalCount}" />
											</s:text>
										</h5>
										<table class="report">
											<thead>
												<tr>
													<th>
														<s:text name="JobTask.label" />
													</th>
													<th>
														<s:text name="JobTask.name" />
													</th>
													<th>
														<s:text name="ManageEmployees.label.RecommendedAssessmentTests" />
													</th>
												</tr>
											</thead>
											<tbody>
												<s:iterator
													value="#missingTasks.missingTasks"
													var="mt"
													status="mtStat">
													<tr>
														<td>
															<s:property value="#mt.label" />
														</td>
														<td>
															<s:property value="#mt.name" />
														</td>
														<td>
															<s:iterator
																value="#mt.jobTaskCriteriaMap.keySet()"
																var="mtGroupNum"
																status="mtGroup">
																<s:iterator
																	value="#mt.jobTaskCriteriaMap.get(#mtGroupNum)"
																	var="mtCrit">
																	<s:property
																		value="#mtCrit.assessmentTest.assessmentCenter.name" />:
																	<s:property value="#mtCrit.assessmentTest.qualificationType" /> -
																	<s:property value="#mtCrit.assessmentTest.qualificationMethod" />
																	<br />
																</s:iterator>
																<s:if test="!#mtGroup.last">
																	<s:text name="ManageJobTaskCriteria.separator.OR" />
																	<br />
																</s:if>
															</s:iterator>
														</td>
													</tr>
												</s:iterator>
											</tbody>
										</table>
									</s:if>
									<s:if test="#site.jobSite.tasks.size == 0">
										<h5>
											<s:text name="ManageEmployees.message.SiteHasNoJobTasks" />
										</h5>
										<br />
									</s:if>
									<a
										href="#"
										class="remove closeSiteTasks"
										id="closeSiteTask_<s:property value="#stat.count" />">
										<s:text name="button.Close" />
									</a>
								</td>
							</tr>
						</s:if>
					</s:iterator>
					<s:if test="#oqSiteCount == 0">
						<tr>
							<td colspan="5">
								<s:text name="ManageEmployees.message.NoAssignedProjects" />
							</td>
						</tr>
					</s:if>
				</table>
			</li>
			<li>
				<s:if test="oqOperators.size > 0">
					<s:select
						list="oqOperators"
						listKey="id"
						listValue="name"
						headerKey=""
						id="oqOperator"
						headerValue=" - %{getText('ManageEmployees.header.AssignProject')} - " />
				</s:if>
			</li>
			<pics:permission
				perm="ManageProjects"
				type="Edit">
				<li>
					<a
						class="add"
						href="#"
						id="newJobSiteLink">
						<s:text name="ManageEmployees.link.AddNewJobSite" />
					</a>
				</li>
				<div id="newJobSiteForm">
					<h4>
						<s:text name="ManageEmployees.link.AddNewJobSite" />
					</h4>
					<s:if test="permissions.admin && employee.account.contractor">
						<li>
							<label><s:text name="global.Operator" />:</label>
							<s:select
								list="allOqOperators"
								listKey="id"
								listValue="name"
								name="op.id"
								id="op"
								onchange="$('#opName').val($(this).text().trim())" />
							<s:hidden
								name="op.name"
								value="%{allOqOperators.get(0).name}"
								id="opName" />
						</li>
					</s:if>
					<s:else>
						<s:hidden
							name="op.id"
							value="%{employee.account.id}" />
						<s:hidden
							name="op.name"
							value="%{employee.account.name}" />
					</s:else>
					<li class="required">
						<s:textfield
							name="jobSite.label"
							maxlength="15"
							theme="formhelp" />
					</li>
					<li class="required">
						<s:textfield
							name="jobSite.name"
							maxlength="255"
							theme="formhelp" />
					</li>
					<li>
						<s:textfield
							name="jobSite.projectStart"
							cssClass="datepicker"
							value="%{today}"
							theme="formhelp" />
					</li>
					<li>
						<s:textfield
							name="jobSite.projectStop"
							cssClass="datepicker"
							value="%{expirationDate}"
							theme="formhelp" />
					</li>
					<li>
						<a
							href="#"
							class="picsbutton positive">
							<s:text name="ManageEmployees.button.SaveNewJobSite" />
						</a>
						<a
							href="#"
							class="picsbutton cancelButton">
							<s:text name="button.Cancel" />
						</a>
					</li>
				</div>
			</pics:permission>
			<s:if test="employee.account.contractor">
				<li>
					<a
						href="ReportNewProjects.action"
						class="add">
						<s:text name="ManageEmployees.link.FindNewProjects" />
					</a>
				</li>
			</s:if>
		</ol>
	</fieldset>
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
					<s:iterator
						value="employee.employeeSites"
						id="site"
						status="stat">
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
										<s:property value="#site.jobSite.label" />
									</s:if>
								</td>
								<td>
									<s:date
										name="expirationDate"
										format="%{getText('date.short')}" />
								</td>
							</tr>
						</s:if>
					</s:iterator>
				</table>
			</li>
		</ol>
	</fieldset>
</s:if>