<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:include value="../actionMessages.jsp"/>

<div id="thinking_sites" class="right"></div>

<h3>Assigned Sites</h3>
<table class="report"">
	<thead>
		<tr>
			<th colspan="2">Assigned Site</th>
		</tr>
	</thead>
	<s:iterator value="employee.employeeSites" id="site">
		<tr>
			<td><s:property value="#site.operator.name"/></td>
			<td><a href="#" onclick="modJobSite('removeSite','<s:property value="employee.id"/>','','<s:property value="#site.operator.id"/>'); return false;" class="remove">Remove</a></td>
		</tr>
	</s:iterator>
	<s:if test="operators.size > 0">
		<tr>
			<td colspan="2">
				<s:select onchange="modJobSite('addSite','%{employee.id}','',this.value);"
					list="operators"
					name="operator.id"
					listKey="id"
					listValue="name"
					headerKey=""
					headerValue=" - Add Job Site - "
					id="operator"
				/>
			</td>
		</tr>
	</s:if>
</table>
<s:if test="operators.size == 0">
	<h5>This employee as been assigned to all available sites.</h5>
</s:if>