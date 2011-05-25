<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:include value="contractor_trade_cloud.jsp"/>

<s:if test="trade != null">
<div id="trade-view-single" 
	<s:if test="trade.id > 0">class="current"</s:if>>

<s:include value="../actionMessages.jsp"/>

<s:form id="trade-form">
<s:if test="!isStringEmpty(trade.trade.imageLocationI)">
	<img src="TradeTaxonomy!tradeLogo.action?trade=<s:property value="trade.trade.id"/>" class="trade"/>
</s:if>

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

<h3 <s:if test="trade.id == 0">class="new"</s:if>><s:property value="trade.trade.name"/></h3>

<s:hidden name="contractor"/>
<s:hidden name="trade"/>
<s:hidden name="trade.trade"/>

<s:if test="!isStringEmpty(trade.trade.help.toString())">
	<div id="trade_description" class="trade-section">
		<s:property value="trade.trade.help.toString()" />
	</div>
</s:if>

<s:if test="trade.trade.children.size > 0">
<div id="trade-section-nav">
	<ul>
		<li><a href="#trade_children" class="tradeInfo"><s:property value="trade.trade.children.size"/> Child Trade(s)</a></li>
	</ul>
</div>
<div id="trade_children" class="trade-section">
	<ul>
		<s:iterator value="trade.trade.children" var="atrade">
			<li>
				<a href="ContractorTrades!tradeAjax.action?contractor=<s:property value="contractor.id"/>&trade.trade=<s:property value="#atrade.id"/>" class="trade">
					<s:property value="#atrade.name"/>
				</a>
			</li>
		</s:iterator>
	</ul>
</div>
</s:if>

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

<div class="clearfix">
	<s:if test="trade.trade.productI">
		<div>
			<s:text name="ContractorTrade.businessProduct">
				<s:param>
					<s:select name="trade.manufacture" theme="translate" list="#{true: 'Manufacture', false:'Distribute' }" />
				</s:param>
			</s:text>
		</div>
	</s:if>
	<s:if test="trade.trade.serviceI">
		<div>
			<s:text name="ContractorTrade.businessService">
				<s:param>
					<s:select name="trade.selfPerformed" theme="translate" list="#{true: 'SelfPerform', false:'SubContract' }" />
				</s:param>
			</s:text>
		</div>
	</s:if>
</div>

<s:if test="trade.id == 0">
<div>
	<s:if test="requiresService">
		<s:checkbox name="conTypes" fieldValue="Onsite" value="%{contractor.onsiteServices}" cssClass="service" /> OnSite Services
		<s:checkbox name="conTypes" fieldValue="Offsite" value="%{contractor.offsiteServices}" cssClass="service" /> OffSite Services
	</s:if>
	<s:elseif test="requiresMaterial">
		<s:checkbox name="conTypes" fieldValue="Supplier" value="%{contractor.materialSupplier}" cssClass="product"/> I understand that this will list my account as a material supplier.
	</s:elseif>
</div>
</s:if>

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

<div>
	<s:if test="trade.id == 0">
		<s:submit method="saveTradeAjax" value="Add" cssClass="save picsbutton positive" id="addButton"/>
	</s:if>
	<s:else>
		<s:submit method="saveTradeAjax" value="Save" cssClass="save picsbutton positive"/>
		<s:submit method="removeTradeAjax" value="Remove" cssClass="remove picsbutton negative"/>
	</s:else>
</div>

</s:form>
</div>
</s:if>
