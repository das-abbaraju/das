<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<div id="trade-cloud">
	<s:iterator value="contractor.trades" var="trade">
		<a href="ContractorTrades!tradeAjax.action?contractor=<s:property value="contractor.id"/>&trade=<s:property value="#trade.id"/>" class="trade" style="font-size: <s:property value="tradeCssMap.get(#trade)"/>px"><s:property value="#trade.trade.name"/></a>
	</s:iterator>
</div>