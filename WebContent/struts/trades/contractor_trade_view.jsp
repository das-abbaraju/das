<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:if test="!permissions.operatorCorporate && trade.trade.selectable">
	<a href="ContractorTrades!tradeAjax.action?contractor=${contractor.id}&trade=${trade.id}&mode=Edit" class="trade edit"><s:text name="global.Edit" /></a>
</s:if>

<s:if test="!trade.trade.selectable">
	<div id="nonSelectable" class="alert-message">
		<s:text name="ContractorTrade.nonSelectable" />
	</div>
	
	<s:if test="contractor.tradesSorted.contains(trade)">
		<s:form id="trade-form">
			<s:hidden name="contractor"/>
			<s:hidden name="trade"/>
			<s:hidden name="trade.trade"/>
			
			<s:submit method="removeTradeAjax" value="%{getText('button.Remove')}" cssClass="remove btn danger" /> 
		</s:form>
	</s:if>
</s:if>

<s:if test="trade.id > 0 && trade.activityPercent == 0">
	<div class="alert-message">
		<s:text name="ContractorTrade.activityPercent.missing"/>
	</div>
</s:if>

<%-- Trade Activity Percentages --%>
<div id="activityPercent">
	<p>
		<s:if test="permissions.operatorCorporate">
			<s:text name="ContractorTrade.businessRepresentationOther">
				<s:param>
					<b><s:text name="ContractorTrade.activityPercent.%{trade.activityPercent}"/></b>
				</s:param>
			</s:text>
		</s:if>
		<s:else>
			<s:text name="ContractorTrade.businessRepresentation">
				<s:param>
					<b><s:text name="ContractorTrade.activityPercent.%{activityPercentMap.get(trade.activityPercent)}" /></b>
				</s:param>
			</s:text>
		</s:else>
	</p>
</div>

<%-- Trade Options --%>
<div id="tradeOptions" class="clearfix">
	<s:if test="trade.trade.productI">
		<p>
			<s:text name="ContractorTrade.businessProduct">
				<s:param>
					<b>
						<s:if test="trade.manufacture">
						    <s:text name="ContractorTrade.manufacture.Manufacture"/>
						</s:if>
						<s:else>
							<s:text name="ContractorTrade.manufacture.Distribute"/>
						</s:else>
					</b>
				</s:param>
			</s:text>
		</p>
	</s:if>
	
	<s:if test="trade.trade.serviceI">
		<p>
			<s:text name="ContractorTrade.businessService">
				<s:param>
					<b>
						<s:if test="trade.selfPerformed">
						    <s:text name="ContractorTrade.selfPerformed.SelfPerform"/>
						</s:if>
						<s:else>
							<s:text name="ContractorTrade.selfPerformed.SubContract"/>
						</s:else>
					</b>
				</s:param>
			</s:text>
		</p>
	</s:if>
</div>