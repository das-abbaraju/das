<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:include value="../actionMessages.jsp" />

<div id="thinking_sites" class="right"></div>

<table class="report"">
	<thead>
		<tr>
			<th colspan="2">Assigned Site</th>
			<th>Since</th>
			<th>Remove</th>
		</tr>
	</thead>
	<s:iterator value="employee.employeeSites" id="site">
		<s:if test="#site.current">
			<tr>
				<td title=""><s:property value="#site.operator.name" /><s:if test="#site.jobSite.id > 0">: <s:property value="#site.jobSite.label" /></s:if></td>
				<td><s:if test="operator.requiresOQ">OQ</s:if>
 					<s:if test="operator.requiresCompetencyReview">CompReview</s:if>
				</td>
				<td><s:property value="#site.effectiveDate"/></td>
				<td><a href="#"
					onclick="removeJobSite(<s:property value="#site.id"/>); return false;"
					class="remove">Remove</a></td>
			</tr>
		</s:if>
	</s:iterator>
	<tr>
		<td colspan="4"><s:if test="operators.size > 0">
			<s:select
				onchange="addJobSite(this.value);"
				list="operators" name="operator.id" listKey="id" listValue="name"
				headerKey="" headerValue=" - Add Job Site - " id="operator" />
		</s:if><s:else>
			<h5>This employee has been assigned to all available sites.</h5>
		</s:else></td>
	</tr>
</table>
