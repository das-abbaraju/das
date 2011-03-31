<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<form id="saveTrade" class="form">
	<s:hidden name="trade.id" />
	<fieldset>
	<h2><s:property value="trade.description"/></h2>
		<ol>
			<li><label>Trade ID:</label> <s:property value="trade.id"/></li>
			<li><label>Short Trade Name:</label> <s:textfield name="trade.short"/></li>
			<li><label>Full Trade Name:</label> <s:textfield name="trade.long"/></li>
			<li><label>Help Text:</label> <s:textarea name="trade.help"></s:textarea> </li>
		</ol>
	</fieldset>

<s:if test="trade.productI">
	<p><label>Product Critical:</label> <s:property value="trade.riskLevelI"/></p>
</s:if>
<s:if test="trade.serviceI">
	<p><label>Safety Critical:</label> <s:property value="trade.riskLevelI"/></p>
</s:if>
<s:if test="trade.psmApplies">
	<p><label>PSM Critical:</label> Yes</p>
</s:if>
</form>

<s:if test="trade.classificationCode.length() > 0">
	<p><label>NAICS Code:</label> <s:property value="trade.classificationCode"/></p>
</s:if>
<s:if test="trade.bestMatch != null">
	<p><label>Mapped To:</label></p>
	<ul>
		<s:set var="ps" value="trade.bestMatch" />
		<s:include value="service_printer.jsp" />
	</ul>
</s:if>
