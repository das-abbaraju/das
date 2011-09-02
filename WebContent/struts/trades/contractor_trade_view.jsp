<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:if test="!permissions.operatorCorporate">
	<a href="ContractorTrades!tradeAjax.action?contractor=${contractor.id}&trade=${trade.id}&mode=Edit" class="trade edit">
		<s:text name="global.Edit" />
	</a>
</s:if>

<%-- Specialties (Trade children) --%>
<s:if test="!permissions.operatorCorporate">
	<s:if test="trade.trade.children.size > 0">
		<div id="trade-section-nav">
			<ul>
				<li>
					<a href="#trade_children" class="tradeInfo">
						<s:text name="ContractorTrade.specialties">
							<s:param value="%{trade.trade.children.size}"/>
						</s:text>
					</a>
				</li>
			</ul>
		</div>
		
		<div id="trade_children" class="trade-section">
			<ul>
				<s:iterator value="trade.trade.children" var="atrade">
					<li class="trade-child">
						<a href="ContractorTrades!tradeAjax.action?contractor=<s:property value="contractor.id"/>&trade.trade=<s:property value="#atrade.id"/>" class="trade">
							<s:if test="isStringEmpty(#atrade.name2)">
								<s:property value="#atrade.name"/>
							</s:if>
							<s:else>
								<s:property value="#atrade.name2"/>
							</s:else>
						</a>
					</li>
				</s:iterator>
			</ul>
		</div>
	</s:if>
</s:if>

<s:if test="!trade.trade.selectable">
	<div id="nonSelectable" class="alert">
		<s:text name="ContractorTrade.nonSelectable" />
	</div>
</s:if>

<s:if test="trade.id > 0 && trade.activityPercent == 0">
	<div class="alert">
		<s:text name="ContractorTrade.activityPercent.missing"/>
	</div>
</s:if>

<%-- Trade Activity Percentages --%>
<div id="activityPercent">
	<s:if test="permissions.operatorCorporate">
		<s:text name="ContractorTrade.businessRepresentationOther">
			<s:param>
				<s:text name="ContractorTrade.activityPercent.%{trade.activityPercent}"/>
			</s:param>
		</s:text>
	</s:if>
	<s:else>
		<s:text name="ContractorTrade.businessRepresentation">
			<s:param>
				<s:text name="ContractorTrade.activityPercent.%{activityPercentMap.get(trade.activityPercent)}" />
			</s:param>
		</s:text>
	</s:else>
</div>

<%-- Trade Options --%>
<div id="tradeOptions" class="clearfix">
	<s:if test="trade.trade.productI">
		<div>
			<s:text name="ContractorTrade.businessProduct">
				<s:param>
					<s:if test="trade.manufacture">
					    <s:text name="ContractorTrade.manufacture.Manufacture"/>
					</s:if>
					<s:else>
						<s:text name="ContractorTrade.manufacture.Distribute"/>
					</s:else>
				</s:param>
			</s:text>
		</div>
	</s:if>
	
	<s:if test="trade.trade.serviceI">
		<div>
			<s:text name="ContractorTrade.businessService">
				<s:param>
					<s:if test="trade.selfPerformed">
					    <s:text name="ContractorTrade.selfPerformed.SelfPerform"/>
					</s:if>
					<s:else>
						<s:text name="ContractorTrade.selfPerformed.SubContract"/>
					</s:else>
				</s:param>
			</s:text>
		</div>
	</s:if>
</div>