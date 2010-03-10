<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<table class="report" style="width: 250px;">
	<thead>
		<tr><th colspan="2">Impact</th></tr>
	</thead>
	<tbody>
		<s:iterator value="calculateAffectedList()" status="stat">
			<tr>
				<td><s:property value="#stat.index + 1" /></td>
				<td><a href="ContractorView.action?id=<s:property value="contractor.id" />"><s:property value="contractor.name" /></a></td>
			</tr>
		</s:iterator>
	</tbody>
</table>