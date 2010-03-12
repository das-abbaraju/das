<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<table class="report" style="width: 250px;">
	<thead>
		<tr><th colspan="3"><s:property value="flagCriteriaOperator.criteria.descriptionBeforeHurdle"/> <s:property value="flagCriteriaOperator.hurdle"/> <s:property value="flagCriteriaOperator.criteria.descriptionAfterHurdle"/> </th></tr>
	</thead>
	<tbody>
		<s:if test="affected.size() == 0">
			<tr><td colspan="<s:property value="criteria.allowCustomValue ? 3 : 2"/>" class="center">No Impact</td></tr>
		</s:if>
		<s:else>
			<s:iterator value="affected" status="stat">
				<tr>
					<td><s:property value="#stat.index + 1" /></td>
					<td><a
						href="ContractorView.action?id=<s:property value="contractor.id" />"><s:property
						value="contractor.name" /></a></td>
					<s:if test="criteria.allowCustomValue">
						<td><s:property value="criteriaContractor.answer"/></td>
					</s:if>
				</tr>
			</s:iterator>
		</s:else>
	</tbody>
</table>