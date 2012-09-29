<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:text name="ContractorTrades.title"></s:text> </title>

<link rel="stylesheet" type="text/css" media="screen" href="css/trades.css?v=<s:property value="version"/>" />

<script type="text/javascript" src="js/jquery/jsTree/jquery.jstree.js?v=<s:property value="version"/>"></script>
<script>
var conID = '<s:property value="id"/>';
</script>

<script type="text/javascript" src="js/trade_taxonomy_contractor.js?v=<s:property value="version"/>"></script>
<script type="text/javascript" src="js/trade_taxonomy_common.js?v=<s:property value="version"/>"></script>

</head>
<body>
<s:include value="../contractors/conHeader.jsp" />

<s:if test="contractor.tradesUpdated == null && contractor.trades.size() > 0 && !permissions.operatorCorporate">
	<s:form>
		<div class="alert" id="reviewTrades">
			<s:hidden name="contractor" />
			<s:text name="ContractorTrade.reviewTrades"/><br /><br />
			<s:submit method="removeAllTradesAjax" value="%{getText('button.StartOver')}" cssClass="picsbutton positive" />
			<s:submit method="continueWithTradesAjax" value="%{getText('button.KeepTrades')}" cssClass="picsbutton" />
		</div>
	</s:form>
</s:if>

<s:if test="permissions.contractor">
	<div class="navigationButtons">
		<s:if test="registrationStep.done">
			<s:if test="contractor.needsTradesUpdated">
				<div class="alert"><s:text name="ContractorTrades.NeedsUpdating" /></div>
			</s:if>
		</s:if>
	</div>
</s:if>

<s:if test="!permissions.operatorCorporate">
<h4><s:text name="ContractorTrades.SearchTitle" /></h4>
<s:include value="trade_search.jsp"/>
</s:if>

<div id="trade-view" <s:if test="permissions.operatorCorporate">class="fullwidth"</s:if>>
	<s:include value="contractor_trade_select_trades.jsp"/>
</div>

</body>
</html>