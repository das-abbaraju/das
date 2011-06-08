<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<h4>Selected trades</h4>


<s:if test="trade == null">
	<div id="trade-instructions">
		<p><s:text name="ContractorTrades.instructions"></s:text></p>
	</div><br />
</s:if>
<s:else>
<div>
	<a class="CTInstructions help" href="#ContractorTradesInstructions" rel="#ContractorTradesInstructions">Instructions</a>
</div>
</s:else>

<s:if test="contractor.trades.size() > 0">
	<div id="trade-cloud">
		<table>
			<tr>
				<td>
					<s:iterator value="contractor.tradesSorted" var="trade" begin="0" end="contractor.trades.size()/2 - 1">
						<a href="ContractorTrades!tradeAjax.action?contractor=<s:property value="contractor.id"/>&trade=<s:property value="#trade.id"/>" 
							rel="ContractorTrades!quickTrade.action?contractor=<s:property value="contractor.id"/>&trade=<s:property value="#trade.id"/>"
							class="trade trade-cloud-<s:property value="tradeCssMap.get(#trade)"/> <s:if test="#trade.id == [1].trade.id">current</s:if>">
							<s:property value="#trade.trade.name"/></a>
					</s:iterator>
				</td>
				<td>
					<s:iterator value="contractor.tradesSorted" var="trade" begin="contractor.trades.size()/2">
						<a href="ContractorTrades!tradeAjax.action?contractor=<s:property value="contractor.id"/>&trade=<s:property value="#trade.id"/>" 
							rel="ContractorTrades!quickTrade.action?contractor=<s:property value="contractor.id"/>&trade=<s:property value="#trade.id"/>"
							class="trade trade-cloud-<s:property value="tradeCssMap.get(#trade)"/> <s:if test="#trade.id == [1].trade.id">current</s:if>">
							<s:property value="#trade.trade.name"/></a>
					</s:iterator>
				</td>
			</tr>
		</table>
	</div>
</s:if>
<s:else>
	<script>$('#next_button').hide()</script>
</s:else>

<div id="ContractorTradesInstructions" Class="hide">
	<p><s:text name="ContractorTrades.instructions"></s:text></p>
</div>