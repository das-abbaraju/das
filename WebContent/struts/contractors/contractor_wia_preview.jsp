<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="contractor.trades.size() > 0">
	<div>
		<p>
			<s:text name="TrirWIA.helpText" />
		</p>
	</div>
	<table cellpadding="0" cellspacign="0" border="0" class="table trir-weighted-industry-average">
		<thead>
			<tr>
				<th class="trade-name">Trade Name</th>
				<th class="naics-average">NAICS Average</th>
			</tr>
		</thead>

		<s:if test="contractor.hasSelfPerformedTrades()">		
			<s:iterator value="contractor.trades" var="conTrade" status="rowstatus">
				<s:if test="#conTrade.isSelfPerformed()">
					<tr class="<s:if test="#rowstatus.odd == true">odd</s:if><s:else>even</s:else>">
						<td class="trade-name"><s:property value="#conTrade.trade.name" /></td>
						<td class="naics-average"><s:property value="#conTrade.trade.getNaicsTRIRI()" /></td>
					</tr>
				</s:if>
			</s:iterator>
		</s:if>
		<s:else>
			<s:iterator value="contractor.trades" var="conTrade" status="rowstatus">
				<tr class="<s:if test="#rowstatus.odd == true">odd</s:if><s:else>even</s:else>">
					<td class="trade-name"><s:property value="#conTrade.trade.name" /></td>
					<td class="naics-average"><s:property value="#conTrade.trade.getNaicsTRIRI()" /></td>
				</tr>
			</s:iterator>
		</s:else>
		
		<tfoot>
			<tr>
				<td colspan="2" class="weighted-average">Weighted Average: <s:property value="format(contractor.getWeightedIndustryAverage())" /></td>
			</tr>
		</tfoot>
	</table>
</s:if>