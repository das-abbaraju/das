<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="contractor.trades.size() > 0">
	<table cellpadding="0" cellspacign="0" border="0" class="trir-popover">
		<tr>
			<th>Trade Name</th>
			<th>NAICS Average</th>
		</tr>

		<s:if test="contractor.hasSelfPerformedTrades()">		
			<s:iterator value="contractor.trades" var="conTrade">
				<s:if test="#conTrade.isSelfPerformed()">
					<tr>
						<td><s:property value="#conTrade.trade.name" /></td>
						<td class="naics"><s:property value="#conTrade.trade.getNaicsTRIRI()" /></td>
					</tr>
				</s:if>
			</s:iterator>
		</s:if>
		<s:else>
			<s:iterator value="contractor.trades" var="conTrade">
					<tr>
						<td><s:property value="#conTrade.trade.name" /></td>
						<td class="naics"><s:property value="#conTrade.trade.getNaicsTRIRI()" /></td>
					</tr>
			</s:iterator>
		</s:else>
		
		<tr>
			<td colspan="3" class="average">Weighted Average: <s:property value="format(contractor.getWeightedIndustryAverage())" /></td>
		</tr>
	</table>
</s:if>