<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<h4><s:property value="trade.description"/></h4>
<hr />
<p><label><s:property value="trade.classificationType"/> ID:</label> <s:property value="trade.id"/></p>
<s:if test="trade.productI">
	<p><label>Product Critical:</label> <s:property value="trade.riskLevelI"/></p>
</s:if>
<s:if test="trade.serviceI">
	<p><label>Safety Critical:</label> <s:property value="trade.riskLevelI"/></p>
</s:if>
<s:if test="trade.psmApplies">
	<p><label>PSM Critical:</label> Yes</p>
</s:if>
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
<s:if test="trade.matches.size() > 0">
<p><label>Suncor Mapping:</label>
	<ul>
	<s:iterator value="trade.matches" var="suncor">
		<li><s:property value="#suncor.toString()"/> (<s:property value="#suncor.id"/>)</li>
	</s:iterator>
	</ul>
</p>
</s:if>
