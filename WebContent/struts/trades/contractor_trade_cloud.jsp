<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<h4><s:text name="ContractorTrades.SelectedTrades" /></h4>

<s:if test="!#hideTradeCloudInstructions">
	<s:if test="!permissions.operatorCorporate">
		<s:if test="scope != 'ContractorView'">
			<s:if test="trade == null">
				<div id="trade_instructions" class="alert-message">
					<p>
						<s:text name="ContractorTrades.instructions" />
					</p>
				</div>
			</s:if>
			<s:else>
				<div>
					<a class="CTInstructions help" rel="#ContractorTradesInstructions"><s:text name="ContractorTrades.InstructionsTitle" /></a>
					
					<div id="ContractorTradesInstructions">
						<p>
							<s:text name="ContractorTrades.instructions" />
						</p>
					</div>
				</div>
			</s:else>
		</s:if>
	</s:if>
</s:if>

<s:if test="contractor.trades.size() > 0">
	<div id="trade_cloud">
		<ul>
			<s:iterator value="contractor.tradesSorted" var="trade" status="status">
				<li>
					<a href="ContractorTrades!tradeAjax.action?contractor=<s:property value="contractor.id"/>&trade=<s:property value="#trade.id"/>&mode=View" 
						rel="ContractorTrades!quickTrade.action?contractor=<s:property value="contractor.id"/>&trade=<s:property value="#trade.id"/>"
						class="trade trade-cloud-<s:property value="tradeCssMap.get(#trade)"/> btn <s:if test="#trade.id == [1].trade.id">primary</s:if>">
						<s:property value="#trade.trade.name"/>
					</a>
				</li>
			</s:iterator>
		</ul>
	</div>
</s:if>
<s:else>
	<script>$('#next_button').hide()</script>
</s:else>