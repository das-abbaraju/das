<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<h4>Selected trades</h4>
<div id="trade-cloud">
	<s:if test="contractor.trades.size() > 0">
		<table>
			<tr>
				<td>
					<s:iterator value="contractor.tradesSorted" var="trade" begin="0" end="contractor.trades.size()/2 - 1">
						<a href="ContractorTrades!tradeAjax.action?contractor=<s:property value="contractor.id"/>&trade=<s:property value="#trade.id"/>" 
							class="trade trade-cloud-<s:property value="tradeCssMap.get(#trade)"/> <s:if test="#trade.id == [1].trade.id">current</s:if>">
							<s:property value="#trade.trade.name"/></a>
					</s:iterator>
				</td>
				<td>
					<s:iterator value="contractor.tradesSorted" var="trade" begin="contractor.trades.size()/2">
						<a href="ContractorTrades!tradeAjax.action?contractor=<s:property value="contractor.id"/>&trade=<s:property value="#trade.id"/>" 
							class="trade trade-cloud-<s:property value="tradeCssMap.get(#trade)"/> <s:if test="#trade.id == [1].trade.id">current</s:if>">
							<s:property value="#trade.trade.name"/></a>
					</s:iterator>
				</td>
			</tr>
		</table>
	</s:if>
	<s:else>
		<p><s:text name="ContractorTrades.instructions"></s:text></p>
	</s:else>
</div>

