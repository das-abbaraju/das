<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<div id="trade-cloud">
	<s:iterator value="contractor.trades" var="trade" status="stat">
		<a href="ContractorTrades!tradeAjax.action?contractor=<s:property value="contractor.id"/>&trade=<s:property value="#trade.id"/>" class="trade <s:property value="tradeCssMap.get(#trade)"/>"><s:property value="#trade.trade.name"/></a>
	</s:iterator>
</div>

<h3><s:property value="trade.trade.name"/></h3>

<s:if test="affectedTrades.size > 0">
	<div class="info">
		<s:text name="ContractorTrades.affectedTrades">
			<s:param>
				<ul>
				<s:iterator value="affectedTrades" var="trade">
					<li><a href="ContractorTrades!tradeAjax.action?contractor=<s:property value="contractor.id"/>&trade=<s:property value="#trade.id"/>" class="trade"><s:property value="#trade.trade.name"/></a></li>
				</s:iterator>
				</ul>
			</s:param>
		</s:text>
	</div>
</s:if>

<s:form id="trade-form">
<s:hidden name="contractor"/>
<s:hidden name="trade"/>
<s:hidden name="trade.trade"/>

<s:hidden name="decorator" value="none"/>

<s:include value="../actionMessages.jsp"/>

<div class="clearfix">
	<s:if test="trade.trade.productI">
		<div class="fieldoption left">
			<s:radio name="trade.manufacture" theme="translate" list="#{true: 'Manufacture', false:'Distribute' }"/>
		</div>
	</s:if>
	<s:if test="trade.trade.serviceI">
		<div class="fieldoption left">
			<s:radio name="trade.selfPerformed" theme="translate" list="#{true: 'SelfPerform', false:'SubContract' }"/>
		</div>
	</s:if>
</div>


<div>
	<s:text name="ContractorTrade.businessRepresentation">
		<s:param><s:select name="trade.activityPercent" list="activityPercentMap" emptyOption="true" theme="translate"/></s:param>
	</s:text>
</div>

<div>
	<ol class="form-style">
		<s:if test="!isStringEmpty(trade.trade.name2.toString())">
			<li>
				<label><s:text name="Trade.name2"/></label>
				<s:property value="trade.trade.name2"/>	
			</li>
		</s:if>
		<li>
			<label><s:text name="Trade"/></label>
			<div class="hierarchy">
				<div id="trade-hierarchy"></div>
			</div>
		</li>
		<s:if test="!isStringEmpty(trade.trade.help.toString())">
			<li>
				<label><s:text name="Trade.help"/></label>
				<s:property value="trade.trade.help"/>
			</li>
		</s:if>
		<li>
			<label><s:text name="Trade.alternateSearch"/></label>
			<s:if test="trade.trade.alternates.size() > 0">
				<ul>
					<s:iterator value="trade.trade.alternates">
						<li><s:property value="name"/></li>
					</s:iterator>
				</ul>
			</s:if>
		</li>
	</ol>
</div>

<div>
	<s:if test="trade.id == 0">
		<s:submit method="saveTradeAjax" value="Add" cssClass="save"/>
	</s:if>
	<s:else>
		<s:submit method="saveTradeAjax" value="Save" cssClass="save"/>
		<s:submit method="removeTradeAjax" value="Remove" cssClass="remove"/>
	</s:else>
</div>
</s:form>
