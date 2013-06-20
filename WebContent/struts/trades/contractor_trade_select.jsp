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
						<s:if test="#atrade.name2 != null && !#atrade.name2.equals('') && !#atrade.name2.equals(#atrade.getI18nKey('name2'))">
							<s:set name="trade_name" value="#atrade.name2" />
						</s:if>
						<s:else>
							<s:set name="trade_name" value="#atrade.name" />
						</s:else>
						
						<a href="ContractorTrades!tradeAjax.action?contractor=${contractor.id}&trade.trade=${atrade.id}" class="trade">${trade_name}</a> &gt;
					</s:iterator>
				</div>
			</s:if>
		</s:if>
	
		<h3>${trade.trade.name}</h3>
		
		<s:if test="trade.trade.help != null && !trade.trade.help.equals('') && !trade.trade.help.equals(trade.trade.getI18nKey('help'))">
			<div id="trade_description" class="trade-section">
				<s:property value="trade.trade.help" />
			</div>
		</s:if>
		
		<h5>Average TRIR: <s:property value="trade.trade.getNaicsTRIRI()"/></h5>
		
		<div class="trade-information">
			<s:if test="mode == 'Edit'">
				<s:include value="contractor_trade_edit.jsp" />
			</s:if>
			<s:else>
				<s:include value="contractor_trade_view.jsp" />
			</s:else>
			
			<%-- Specialties (Trade children) --%>
			<s:if test="!permissions.operatorCorporate">
				<s:if test="trade.trade.children.size > 0">
					<div id="trade-section-nav">
						<a href="#trade_children" class="tradeInfo btn">
							<s:text name="ContractorTrade.specialties">
								<s:param value="%{trade.trade.children.size}"/>
							</s:text>
							<span>[toggle]</span>
						</a>
					</div>
					
					<div id="trade_children" class="trade-section">
						<h5>Specialties</h5>
						<ul>
							<s:iterator value="trade.trade.children" var="atrade">
								<li class="trade-child">
									<a href="ContractorTrades!tradeAjax.action?contractor=<s:property value="contractor.id"/>&trade.trade=<s:property value="#atrade.id"/>" class="trade">
										<s:if test="#atrade.name2 != null && !#atrade.name2.equals('') && !#atrade.name2.equals(#atrade.getI18nKey('name2'))">
											<s:property value="#atrade.name2"/>
										</s:if>
										<s:else>
											<s:property value="#atrade.name"/>
										</s:else>
									</a>
								</li>
							</s:iterator>
						</ul>
					</div>
				</s:if>
			</s:if>
		</div>
		
	</div>

</s:if>