<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>

<s:iterator value="employee.employeeSites" var="site" status="stat">
	<s:if test="#site.current && #site.jobSite != null">
		<s:set name="oqSiteCount" value="#oqSiteCount + 1" />
		<tr>
			<td>
				${operator.name}
			</td>
			<td>
				${jobSite.label}
			</td>
			<td>
				${effectiveDate}
			</td>
			<td class="center">
				<s:set name="edit_site_title">
					<s:text name="ManageEmployees.header.EditSiteProject">
						<s:param>
							${operator.name}
						</s:param>
						<s:param value="1" />
						<s:param>
							${jobSite.label}
						</s:param>
					</s:text>
				</s:set>
				<a
					href="javascript:;"
					class="edit site"
					data-employee="${employee.id}"
					data-site="${id}"
					data-jobSite="${jobSite.id}"
					title="${edit_site_title}"></a>
			</td>
			<td class="center">
				<a href="javascript:;" class="preview tasks" data-order="${stat.count}"></a>
			</td>
		</tr>
		<tr class="qualified-tasks" data-order="${stat.count}">
			<td colspan="5" style="padding: 10px">
				<s:set name="missingTasks" value="getMissingTasks(#site.jobSite.id)" />
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
							<s:iterator value="#missingTasks.qualifiedTasks" var="mt" status="mtStat">
								<tr>
									<td>
										${mt.label}
									</td>
									<td>
										${mt.name}
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
							<s:iterator value="#missingTasks.missingTasks" var="mt" status="mtStat">
								<tr>
									<td>
										${mt.label}
									</td>
									<td>
										${mt.name}
									</td>
									<td>
										<s:iterator value="#mt.jobTaskCriteriaMap.keySet()" var="mtGroupNum" status="mtGroup">
											<s:iterator value="#mt.jobTaskCriteriaMap.get(#mtGroupNum)" var="mtCrit">
												${mtCrit.assessmentTest.assessmentCenter.name}:
												${mtCrit.assessmentTest.qualificationType}
												-
												${mtCrit.assessmentTest.qualificationMethod}
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
			</td>
		</tr>
	</s:if>
</s:iterator>