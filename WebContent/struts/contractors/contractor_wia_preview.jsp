<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="contractor.trades.size() > 0">
	<table>
		<tr>
			<td>Trade Name</td>
			<td>Activity Percent</td>
			<td>NAICS Average</td>
		</tr>
		<s:iterator value="contractor.trades" var="conTrade">
			<tr>
				<td><s:property value="#conTrade.trade.name" /></td>
				<td><s:property value="#conTrade.getPercentOfTotal()" />%</td>
				<td><s:property value="#conTrade.trade.getNaicsTRIRI()" /></td>
			</tr>
		</s:iterator>
		<tr>
			<td><s:property value="contractor.getWeightedIndustryAverage()" /></td>
		</tr>
	</table>
</s:if>