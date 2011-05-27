<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title></title>
<s:include value="../jquery.jsp"/>

<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/cluetip/jquery.cluetip.css"/>
<link rel="stylesheet" type="text/css" media="screen" href="css/trades.css?v=<s:property value="version"/>" />

</head>
<body>
<s:if test="trade != null">
<div id="trade-view-single" 
	<s:if test="trade.id > 0">class="current"</s:if>>

<s:form id="trade-form">
<s:if test="!isStringEmpty(trade.trade.imageLocationI)">
	<img src="TradeTaxonomy!tradeLogo.action?trade=<s:property value="trade.trade.id"/>" class="trade"/>
</s:if>

<s:if test="trade.trade.parent != null">
	<div class="trade-section">
		<s:iterator value="tradeClassification" var="atrade">
			<s:if test="isStringEmpty(#atrade.name2)">
				<s:property value="#atrade.name"/>
			</s:if>
			<s:else>
				<s:property value="#atrade.name2"/>
			</s:else>
			 &gt;
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

<div>
	<s:text name="ContractorTrade.businessRepresentationOther">
		<s:param>
			<s:text name="ContractorTrade.activityPercent.%{trade.activityPercent}"/>
		</s:param>
	</s:text>
</div>

<div class="clearfix">
	<s:if test="trade.trade.productI">
		<div>
			<s:text name="ContractorTrade.businessProductOther">
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
			<s:text name="ContractorTrade.businessServiceOther">
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
</s:form>
</div>
</s:if>
</body>
