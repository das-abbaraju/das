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
			<s:iterator value="contractor.tradesSorted" var="con_trade" status="status">
				<li>
					<s:url action="ContractorTrades" method="tradeAjax" var="view_contractor_trade">
						<s:param name="contractor">
							${contractor.id}
						</s:param>
						<s:param name="trade">
							${con_trade.id}
						</s:param>
						<s:param name="mode">
							view
						</s:param>
					</s:url>
					<s:url action="ContractorTrades" method="preview" var="preview_contractor_trade">
						<s:param name="contractor">
							${contractor.id}
						</s:param>
						<s:param name="trade">
							${con_trade.trade.id}
						</s:param>
					</s:url>
					<a href="${view_contractor_trade}"
						rel="${preview_contractor_trade}"
						class="trade trade-cloud-<s:property value="tradeCssMap.get(#con_trade)"/> btn<s:if test="#con_trade.id == [1].trade.id"> primary</s:if>"
						title="${con_trade.trade.name}">
						${con_trade.trade.name}  <s:if test="#con_trade.id == contractor.topTrade.id"> *</s:if>

                    </a>
				</li>
			</s:iterator>
		</ul>
	</div>
</s:if>
<s:else>
	<script>$('#next_button').hide()</script>
</s:else>