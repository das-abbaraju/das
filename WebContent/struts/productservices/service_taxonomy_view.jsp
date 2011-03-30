<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<ul>
	<li class="center"><h3><s:property value="service.description"/></h3></li>
	<li><label><s:property value="service.classificationType"/> ID:</label> <s:property value="service.id"/></li>
	<s:if test="service.productI">
		<li><label>Product Critical:</label> <s:property value="service.riskLevelI"/></li>
	</s:if>
	<s:if test="service.serviceI">
		<li><label>Safety Critical:</label> <s:property value="service.riskLevelI"/></li>
	</s:if>
	<s:if test="service.psmApplies">
		<li><label>PSM Critical:</label> Yes</li>
	</s:if>
	<s:if test="service.classificationCode.length() > 0">
		<li><label>NAICS Code:</label> <s:property value="service.classificationCode"/></li>
	</s:if>
	<s:if test="service.bestMatch != null">
		<li><label>Mapped To:</label> <s:property value="service.bestMatch.toString()"/></li>
	</s:if>
	<s:if test="service.mappedServices.get(@com.picsauditing.jpa.entities.ClassificationType@Suncor)">
	<li><label>Suncor Mapping:</label>
		<ul>
		<s:iterator value="service.mappedServices.get(@com.picsauditing.jpa.entities.ClassificationType@Suncor)" var="suncor">
			<li><label>Suncor ID:</label> <s:property value="#suncor.toString()"/> (<s:property value="#suncor.id"/>)</li>
		</s:iterator>
		</ul>
	</li>
	</s:if>
</ul>
