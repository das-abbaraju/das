<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:bean name="com.picsauditing.util.ContractorTradeComparator" var="tradeComparator" />

<h4>Selected trades</h4>
<div id="trade-cloud">
	<s:if test="contractor.trades.size() > 0">
		<s:sort comparator="#tradeComparator" source="contractor.trades">
		<s:iterator var="trade">
			<a href="ContractorTrades!tradeAjax.action?contractor=<s:property value="contractor.id"/>&trade=<s:property value="#trade.id"/>" 
				class="trade trade-cloud-<s:property value="tradeCssMap.get(#trade)"/> <s:if test="#trade.id == [1].trade.id">current</s:if>">
				<s:property value="#trade.trade.name"/></a>
		</s:iterator>
		</s:sort>
	</s:if>
	<s:else>
		<p><s:text name="ContractorTrades.instructions"></s:text></p>
	</s:else>
</div>

