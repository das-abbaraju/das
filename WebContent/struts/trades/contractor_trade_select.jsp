<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:if test="trade != null">
	
	<div id="trade-view-single"<s:if test="trade.id > 0">class="current"</s:if>>

		<s:include value="../actionMessages.jsp"/>
	
		<%-- Logo --%>
		<s:if test="!isStringEmpty(trade.trade.imageLocationI)">
			<img src="TradeTaxonomy!tradeLogo.action?trade=<s:property value="trade.trade.id"/>" class="trade"/>
		</s:if>
	
		<%-- Breadcrumbs --%>
		<s:if test="!permissions.operatorCorporate">
			<s:if test="trade.trade.parent != null">
				<div class="trade-section">
					<s:iterator value="tradeClassification" var="atrade">
						<a href="ContractorTrades!tradeAjax.action?contractor=<s:property value="contractor.id"/>&trade.trade=<s:property value="#atrade.id"/>" class="trade">
							<s:if test="isStringEmpty(#atrade.name2)">
								<s:property value="#atrade.name"/>
							</s:if>
							<s:else>
								<s:property value="#atrade.name2"/>
							</s:else>
						</a> &gt;
					</s:iterator>
				</div>
			</s:if>
		</s:if>
	
		<h3>${trade.trade.name}</h3>
		
		<s:if test="!isStringEmpty(trade.trade.help)">
			<div id="trade_description" class="trade-section">
				<s:property value="trade.trade.help" />
			</div>
		</s:if>
		
		<s:if test="mode == 'Edit'">
			<s:include value="contractor_trade_edit.jsp" />
		</s:if>
		<s:else>
			<s:include value="contractor_trade_view.jsp" />
		</s:else>
		
	</div>

</s:if>