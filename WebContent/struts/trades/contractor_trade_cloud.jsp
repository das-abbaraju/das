<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<div id="trade-cloud">
	<s:if test="trade.size() > 0">
		<s:iterator value="contractor.trades" var="trade">
			<a 	href="ContractorTrades!tradeAjax.action?contractor=<s:property value="contractor.id"/>&trade=<s:property value="#trade.id"/>" 
				style="font-size: <s:property value="tradeCssMap.get(#trade)"/>px"
				class="trade"><s:property value="#trade.trade.name"/></a>
		</s:iterator>
	</s:if>
	<s:else>
		<div class="alert"><s:text name="ContractorTrades.instructions"></s:text></div>
	</s:else>
</div>