<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:if test="employee.account.requiresCompetencyReview">
	<fieldset class="form">
		<h2 class="formLegend">HSE Sites</h2>
		<ol>
			<li>
				<table class="report" style="width: 500px;">
					<thead>
						<tr>
							<th>Operator</th>
							<th>Since</th>
							<th>Orientation</th>
							<th>Edit</th>
						</tr>
					</thead>
					<s:iterator value="employee.employeeSites" id="site" status="stat">
						<s:if test="#site.current && #site.jobSite == null">
							<tr>
								<td><s:property value="operator.name" /></td>
								<td><s:property value="effectiveDate"/></td>
								<td>
									<s:property value="orientationDate"/>
								</td>
								<td class="center"><a href="#" onclick="getSite(<s:property value="id" />); return false;" class="edit"></a>
								</td>
							</tr>
						</s:if>
					</s:iterator>
					<tr>
						<td colspan="4"><s:if test="hseOperators.size > 0">
							<s:select onchange="addJobSite(this);" list="hseOperators" listKey="id" 
								listValue="name" headerKey="" headerValue=" - Assign Site - " id="hseOperator" />
							</s:if><s:else>
								<h5>This employee has been assigned to all available HSE sites.</h5>
							</s:else>
						</td>
					</tr>
				</table>
			</li>
		</ol>
	</fieldset>
</s:if>
<s:if test="employee.account.requiresOQ">
	<fieldset class="form">
		<h2 class="formLegend">OQ Projects</h2>
		<ol>
			<li>
				<table class="report" style="width: 500px;">
					<thead>
						<tr>
							<th>Operator</th>
							<th>Project</th>
							<th>Since</th>
							<th>Edit</th>
							<th>Tasks</th>
						</tr>
					</thead>
					<s:set name="oqSiteCount" value="0" />
					<s:iterator value="employee.employeeSites" id="site" status="stat">
						<s:if test="#site.current && #site.jobSite != null">
							<s:set name="oqSiteCount" value="#oqSiteCount + 1" />
							<tr>
								<td><s:property value="operator.name" /></td>
								<td><s:property value="#site.jobSite.label" /></td>
								<td><s:property value="effectiveDate"/></td>
								<td class="center"><a href="#" onclick="getSite(<s:property value="id" />); return false;" class="edit"></a>
								</td>
								<td class="center">
									<a href="#" onclick="$('.qualifiedTasks').hide(); $('#'+<s:property value="#stat.count" />+'_tasks').show(); return false;">View</a>
								</td>
							</tr>
							<tr id="<s:property value="#stat.count" />_tasks" class="qualifiedTasks" style="display: none;">
								<td colspan="5" style="padding: 10px">
									<s:set name="missingTasks" value="getMissingTasks(#site.jobSite.id)" />
									<h4>Qualified Tasks</h4>
									<s:if test="#missingTasks.qualifiedTasks.size > 0">
										<h5>Qualified (<s:property value="#missingTasks.qualifiedTasks.size" /> of <s:property value="#missingTasks.totalCount" />):</h5>
										<table class="report">
											<thead>
												<tr>
													<th>Label</th>
													<th>Name</th>
												</tr>
											</thead>
											<tbody>
												<s:iterator value="#missingTasks.qualifiedTasks" var="mt" status="mtStat">
													<tr>
														<td><s:property value="#mt.label" /></td>
														<td><s:property value="#mt.name" /></td>
													</tr>
												</s:iterator>
											</tbody>
										</table>
									</s:if>
									<s:if test="#missingTasks.missingTasks.size > 0">
										<h5>Missing (<s:property value="#missingTasks.missingTasks.size" /> of <s:property value="#missingTasks.totalCount" />):</h5>
										<table class="report">
											<thead>
												<tr>
													<th>Label</th>
													<th>Name</th>
												</tr>
											</thead>
											<tbody>
												<s:iterator value="#missingTasks.missingTasks" var="mt" status="mtStat">
													<tr>
														<td><s:property value="#mt.label" /></td>
														<td><s:property value="#mt.name" /></td>
													</tr>
												</s:iterator>
											</tbody>
										</table>
									</s:if>
									<s:if test="#site.jobSite.tasks.size == 0">
										<h5>This site has no job tasks.</h5><br />
									</s:if>
									<a href="#" onclick="$('#<s:property value="#stat.count" />_tasks').hide(); return false;" class="remove">Close</a>
								</td>
							</tr>
						</s:if>
					</s:iterator>
					<s:if test="#oqSiteCount == 0">
						<tr>
							<td colspan="5">No assigned projects</td>
						</tr>
					</s:if>
				</table>
			</li>
			<li>
				<s:if test="oqOperators.size > 0">
					<s:select onchange="addJobSite(this);" list="oqOperators" listKey="id" listValue="name" 
						headerKey="" headerValue=" - Assign Project - " id="oqOperator" />
				</s:if>
			</li>
			<pics:permission perm="ManageProjects" type="Edit">
				<li>
					<a class="add" href="#" onclick="$('#newJobSiteForm').show(); $(this).hide(); return false;" id="newJobSiteLink">Add New Job Site</a>
				</li>
				<div id="newJobSiteForm" style="display: none; clear: both;">
					<h4>Add New Job Site</h4>
					<s:if test="permissions.admin && employee.account.contractor">
						<li><label>Operator:</label>
							<s:select list="allOqOperators" listKey="id" listValue="name" name="op.id" id="op" onchange="$('#opName').val($(this).text().trim())" />
							<s:hidden name="op.name" value="%{allOqOperators.get(0).name}" id="opName" />
						</li>
					</s:if>
					<s:else>
						<s:hidden name="op.id" value="%{employee.account.id}" />
						<s:hidden name="op.name" value="%{employee.account.name}" />
					</s:else>
					<li class="required"><label>Site Label:</label>
						<s:textfield name="jobSite.label" maxlength="15" />
					</li>
					<li class="required"><label>Site Name:</label>
						<s:textfield name="jobSite.name" maxlength="255" />
					</li>
					<li><label>Start Date:</label>
						<input type="text" name="jobSite.projectStart" cssClass="datepicker" value="<s:date name="today" format="MM/dd/yyyy" />" />
					</li>
					<li><label>Stop Date:</label>
						<input type="text" name="jobSite.projectStop" cssClass="datepicker" value="<s:date name="expirationDate" format="MM/dd/yyyy" />" />
					</li>
					<li>
						<a href="#" onclick="newJobSite(); return false;" class="picsbutton positive">Save New Job Site</a>
						<a href="#" onclick="$('#newJobSiteForm').hide(); $('#newJobSiteLink').show(); return false;" class="picsbutton negative">Cancel</a>
					</li>
				</div>
			</pics:permission>
			<li><a href="ReportNewProjects.action" class="add">Find New Projects</a></li>
		</ol>
	</fieldset>
</s:if>
<s:if test="employee.prevAssigned">
	<fieldset class="form">
		<h2 class="formLegend">Previously Assigned Sites/Projects</h2>
		<ol>
			<li>
				<table class="report">
					<thead>
						<tr>
							<th>Type</th>
							<th>Operator</th>
							<th>Name</th>
							<th>Ended On</th>
						</tr>
					</thead>
					<s:iterator value="employee.employeeSites" id="site" status="stat">
						<s:if test="!#site.current"> 
							<tr>
								<td>
									<s:if test="jobSite.id > 0">OQ</s:if>
									<s:else>HSE</s:else>
								</td>
								<td><s:property value="operator.name" /></td>
								<td>
									<s:if test="jobSite.id > 0"><s:property value="#site.jobSite.label" /></s:if>
								</td>
								<td><s:date name="expirationDate" format="M/d/yyyy" /></td>
							</tr>
						</s:if>
					</s:iterator>
				</table>
			</li>
		</ol>
	</fieldset>
</s:if>