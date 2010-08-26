<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<table class="report" style="width: 250px;">
	<thead>
		<tr>
			<th colspan="<s:property value="affected.get(0).flagData.criteria.allowCustomValue ? 3 : 2" />"><s:property value="flagCriteriaOperator.replaceHurdle"/></th>
			<s:if test="override">
				<th>Forced Flag</th>
			</s:if>
		</tr>
	</thead>
	<tbody>
		<s:if test="affected.size() == 0">
			<tr><td <s:property value="(affected.get(0).flagData.criteria.allowCustomValue ? 3 : 2) + (override ? 1 : 0)" />" class="center">No Impact</td></tr>
		</s:if>
		<s:else>
			<s:iterator value="affected" status="stat" id="fdo">
				<tr>
					<td><s:property value="#stat.count" /></td>
					<td><a href="ContractorView.action?id=<s:property value="flagData.contractor.id" />">
						<s:property value="flagData.contractor.name" /></a></td>
					<s:if test="flagData.criteria.allowCustomValue">
						<s:if test="flagData.criteria.description.contains('fatalities')">
							<td><s:property value="@com.picsauditing.util.Strings@trimTrailingZeros(flagData.criteriaContractor.answer)"/></td>
						</s:if>
						<s:elseif test="flagData.criteria.dataType == 'number'">
							<td class="right"><s:property value="@com.picsauditing.util.Strings@formatDecimalComma(flagData.criteriaContractor.answer)"/></td>
						</s:elseif>
						<s:else>
							<td><s:property value="flagData.criteriaContractor.answer"/></td>
						</s:else>
					</s:if>
					<s:if test="override">
						<td class="center"><s:property value="@com.picsauditing.jpa.entities.FlagColor@getSmallIcon(#fdo.forcedFlag)" escape="false" /></td>
					</s:if>
				</tr>
			</s:iterator>
			<tr><td colspan="<s:property value="(affected.get(0).flagData.criteria.allowCustomValue ? 3 : 2) + (override ? 1 : 0)" />" class="center">
				<a href="#" onclick="downloadImpact(<s:property value="flagCriteriaOperator.id"/>); return false;"
					class="excel">Download this list</a></td></tr>
		</s:else>
	</tbody>
</table>