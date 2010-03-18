<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<table class="report" style="width: 250px;">
	<thead>
		<tr><th colspan="3"><s:property value="flagCriteriaOperator.replaceHurdle"/></th></tr>
	</thead>
	<tbody>
		<s:if test="affected.size() == 0">
			<tr><td colspan="3" class="center">No Impact</td></tr>
		</s:if>
		<s:else>
			<s:iterator value="affected" status="stat">
				<tr>
					<td><s:property value="#stat.index + 1" /></td>
					<td <s:if test="!criteria.allowCustomValue">colspan="2"</s:if>><a href="ContractorView.action?id=<s:property value="contractor.id" />">
						<s:property value="contractor.name" /></a></td>
					<s:if test="criteria.allowCustomValue">
						<s:if test="criteria.description.contains('fatalities')">
							<td><s:property value="@com.picsauditing.util.Strings@trimTrailingZeros(criteriaContractor.answer)"/></td>
						</s:if>
						<s:elseif test="criteria.dataType == 'number'">
							<td><s:property value="@com.picsauditing.util.Strings@formatDecimalComma(criteriaContractor.answer)"/></td>
						</s:elseif>
						<s:else>
							<td><s:property value="criteriaContractor.answer"/></td>
						</s:else>
					</s:if>
				</tr>
			</s:iterator>
		</s:else>
	</tbody>
</table>