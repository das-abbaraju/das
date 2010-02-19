<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<table class="report" style="width: 250px;">
	<thead>
		<tr><th colspan="2">Impact</th></tr>
	</thead>
	<tbody>
		<s:if test="getAffectedByCriteria(criteriaID).size() > 0">
			<s:iterator value="getAffectedByCriteria(criteriaID)" status="stat">
				<tr>
					<td><s:property value="#stat.index + 1" /></td>
					<td><a href="ContractorView.action?id=<s:property value="id" />"><s:property value="name" /></a></td>
				</tr>
			</s:iterator>
		</s:if>
		<s:else>
			<tr><td colspan="2" class="center">No impact</td></tr>
		</s:else>
	</tbody>
</table>