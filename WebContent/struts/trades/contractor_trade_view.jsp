<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:include value="contractor_trade_cloud.jsp"/>

<s:if test="trade != null">
<div id="trade-view-single" 
	<s:if test="trade.id > 0">class="current"</s:if>>

<s:include value="../actionMessages.jsp"/>

<s:form id="trade-form">
<div class="buttons">
	<s:if test="trade.id == 0">
		<s:submit method="saveTradeAjax" value="Add" cssClass="save picsbutton positive"/>
	</s:if>
	<s:else>
		<s:submit method="saveTradeAjax" value="Save" cssClass="save picsbutton positive"/>
		<s:submit method="removeTradeAjax" value="Remove" cssClass="remove picsbutton negative"/>
	</s:else>
</div>

<img class="trade" src="images/trades/construction.jpg" />
<h3 <s:if test="trade.id == 0">class="new"</s:if>><s:property value="trade.trade.name"/></h3>

<s:hidden name="contractor"/>
<s:hidden name="trade"/>
<s:hidden name="trade.trade"/>

<s:if test="affectedTrades.size > 0">
	<div class="alert">
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

<div class="clearfix">
	<s:if test="trade.trade.productI">
		<div class="fieldoption left">
			What do you do with this product?
			<s:radio name="trade.manufacture" theme="translate" list="#{true: 'Manufacture', false:'Distribute' }"/>
		</div>
	</s:if>
	<s:if test="trade.trade.serviceI">
		<div class="fieldoption left">
			How is this service performed?
			<s:radio name="trade.selfPerformed" theme="translate" list="#{true: 'SelfPerform', false:'SubContract' }" />
		</div>
	</s:if>
</div>

<s:if test="trade.id > 0 && trade.activityPercent == 0">
<div class="alert">
	<s:text name="ContractorTrade.activityPercent.missing"/>
</div>
</s:if>

<div>
	<s:text name="ContractorTrade.businessRepresentation">
		<s:param><s:select name="trade.activityPercent" list="activityPercentMap" theme="translate"/></s:param>
	</s:text>
</div>

<div>
	<ol class="form-style">
		<s:if test="!isStringEmpty(trade.trade.help.toString())">
			<li>
				<label><s:text name="Trade.help"/></label>
				<s:property value="trade.trade.help"/>
			</li>
		</s:if>
		<li>
			<label><s:text name="ContractorTrade.taxonomy"/></label>
			<div class="hierarchy">
				<div id="trade-hierarchy"></div>
			</div>
		</li>
	</ol>
</div>
</s:form>
</div>
</s:if>
