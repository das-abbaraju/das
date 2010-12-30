<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:include value="../actionMessages.jsp" />

<script type="text/javascript">
	$(function() {
		$('.datepicker').datepicker({
				changeMonth: true,
				changeYear:true,
				yearRange: '1940:2010',
				showOn: 'button',
				buttonImage: 'images/icon_calendar.gif',
				buttonImageOnly: true,
				buttonText: 'Choose a date...',
				constrainInput: true,
				showAnim: 'fadeIn'
			});
	});
</script>

<table>
	<tbody>
		<tr>
			<td style="vertical-align: top;">
				<s:if test="employee.account.requiresCompetencyReview">
					<h3>HSE Sites</h3>
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
									<td><a href="#"
										onclick="$('._under').hide(); $('#'+<s:property value="%{#stat.count}" />+'_under').toggle(); return false;"
										class="edit"></a>
									</td>
								</tr>
								</tr>
								<tr id="<s:property value="#stat.count" />_under" class="_under" style="display: none;">
									<td colspan="4">
										<div id="siteEditBox">
											<form id="siteForm_<s:property value="#site.id" />" >
												<input type="hidden" value="<s:property value="#site.id" />" name="childID" />
												<div style="float:left; width: 50%;">
													Start Date: <s:textfield id="sDate_%{#site.id}" cssClass="datepicker" name="effectiveDate" value="%{maskDateFormat(effectiveDate)}" size="10" /><br />
													End Date: <s:textfield id="eDate_%{#site.id}" cssClass="datepicker" name="expirationDate" value="%{maskDateFormat(expirationDate)}" size="10" /><br />
													<input type="submit" value="Save" onclick="editAssignedSites(<s:property value="#site.id" />); return false;" class="picsbutton positive"/>
												</div>
												<div style="float:right; width: 50%;">
													Site Orientation: <s:textfield id="oDate_%{#site.id}" cssClass="datepicker" name="orientationDate" value="%{maskDateFormat(orientationDate)}" size="10" /><br />
													Expires in: <s:select label="Expires" id="expires_%{#site.id}"
														list="#{0:36, 1:24, 2:12, 3:6, 4:' '}"
														value="monthsToExp" /> months<br />
													<input type="submit" value="Remove" onclick="return removeJobSite(<s:property value="#site.id" />);" class="picsbutton negative" />
												</div>
											</form>
										</div>
									</td>
								</tr>
							</s:if>
						</s:iterator>
						<tr>
							<td colspan="4"><s:if test="hseOperators.size > 0">
								<s:select onchange="addJobSite(this.value);" list="hseOperators" name="operator.id" 
									listKey="id" listValue="name" headerKey="" headerValue=" - Assign Site - " 
									id="operator" />
								</s:if><s:else>
									<h5>This employee has been assigned to all available HSE sites.</h5>
								</s:else>
							</td>
						</tr>
					</table>
				</s:if>
				<s:if test="employee.account.requiresOQ">
					<h3>OQ Projects</h3>
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
						<s:iterator value="employee.employeeSites" id="site" status="stat">
							<s:if test="#site.current && #site.jobSite != null">
								<tr>
									<td><s:property value="operator.name" /></td>
									<td><s:property value="#site.jobSite.label" /></td>
									<td><s:property value="effectiveDate"/></td>
									<td><a href="#"
										onclick="$('._under').hide(); $('#'+<s:property value="%{#stat.count}" />+'_under').toggle(); return false;"
										class="edit"></a>
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
									</td>
								</tr>
								<tr id="<s:property value="#stat.count" />_under" class="_under" style="display: none;">
									<td colspan="5">
										<div id="siteEditBox">
											<form id="siteForm_<s:property value="#site.id" />" >
												<input type="hidden" value="<s:property value="#site.id" />" name="childID" />
												<div style="float:left; width: 50%;">
													Start Date: <s:textfield id="sDate_%{#site.id}" cssClass="datepicker" name="effectiveDate" value="%{maskDateFormat(effectiveDate)}" size="10" /><br />
													End Date: <s:textfield id="eDate_%{#site.id}" cssClass="datepicker" name="expirationDate" value="%{maskDateFormat(expirationDate)}" size="10" /><br />
													<input type="submit" value="Save" onclick="editAssignedSites(<s:property value="#site.id" />); return false;" class="picsbutton positive"/>
												</div>
												<div style="float:right; width: 50%;">
													Expires in: <s:select label="Expires" id="expires_%{#site.id}"
														list="#{0:36, 1:24, 2:12, 3:6, 4:' '}"
														value="monthsToExp" /> months<br />
													<input type="submit" value="Remove" onclick="return removeJobSite(<s:property value="#site.id" />);" class="picsbutton negative" />
												</div>
											</form>
										</div>
									</td>
								</tr>
							</s:if>
						</s:iterator>
						<tr>
							<td colspan="5"><s:if test="oqOperators.size > 0">
								<s:select onchange="addJobSite(this.value);" list="oqOperators" name="operator.id" 
									listKey="id" listValue="name" headerKey="" headerValue=" - Assign Project - " 
									id="operator" />
								</s:if><s:else>
									<a href="ReportNewProjects.action" class="add">Find New Projects</a>
								</s:else>
							</td>
						</tr>
						<pics:permission perm="ManageProjects" type="Edit">
							<tr>
								<td colspan="5">
									<a class="add" href="#" onclick="$('#newJobSite').show(); $(this).hide(); return false;" id="newJobSiteLink">Add New Job Site</a>
									<div style="display: none;" id="newJobSite">
										<s:form id="newJobSiteForm">
											<fieldset class="form">
												<h2 class="formLegend">Add New Job Site</h2>
												<ol>
													<s:if test="permissions.admin && employee.account.contractor">
														<li><label>Operator:</label>
															<s:select list="employee.account.operators" listKey="id" listValue="name" name="opID" id="opID" />
														</li>
													</s:if>
													<li><label>Site Label<span class="redMain">*</span>:</label>
														<s:textfield name="siteLabel" maxlength="15" />
													</li>
													<li><label>Site Name<span class="redMain">*</span>:</label>
														<s:textfield name="siteName" maxlength="255" />
													</li>
													<li><label>Start Date:</label> <input type="text" name="siteStart" cssClass="datepicker" value="<s:date name="siteStart" format="MM/dd/yyyy" />" /></li>
													<li><label>Stop Date:</label> <input type="text" name="siteStop" cssClass="datepicker" value="<s:date name="siteStop" format="MM/dd/yyyy" />" /></li>
												</ol>
											</fieldset>
											<fieldset class="submit">
												<a href="#" onclick="newJobSite(); return false;" class="picsbutton positive">Save</a>
												<a href="#" onclick="$('#newJobSite').hide(); $('#newJobSiteLink').show(); return false;" class="picsbutton negative">Cancel</a>
											</fieldset>
										</s:form>
									</div>
								</td>
							</tr>
						</pics:permission>
					</table>
				</s:if>
			</td>
		</tr>
		<tr>
			<td style="vertical-align: top;">
				<s:if test="employee.prevAssigned">
					<h3>Previously Assigned Sites/Projects</h3>
					<table class="report"">
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
				</s:if>
			</td>
		</tr>
	</tbody>
</table>