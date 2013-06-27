<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<s:include value="../actionMessages.jsp"/>

<s:if test="trade == null">
	<div class="info">
		<s:text name="TradeTaxonomy.SearchOrBrowse"/>
	</div>
</s:if>
<s:else>
	<div id="trade-view-single">
		<s:if test="!isStringEmpty(trade.imageLocationI)">
			<img src="<s:url method="tradeLogo"><s:param name="trade" value="trade.id"/></s:url>" class="trade"/>
		</s:if>
		
		<s:if test="trade.parent != null">
			<div class="trade-section">
				<s:iterator value="tradeClassification" var="atrade">
					<a href="<s:url method="tradeAjax"><s:param name="trade" value="%{#atrade.id}"/></s:url>" class="trade">
						<s:if test="#atrade.name2 != null && !#atrade.name2.equals('') && !#atrade.name2.equals(#atrade.getI18nKey('name2'))">
							<s:property value="#atrade.name2"/>
						</s:if>
						<s:else>
							<s:property value="#atrade.name"/>
						</s:else>
					</a> &gt;
				</s:iterator>
			</div>
		</s:if>
		
		<h3><s:property value="trade.name"/></h3>
		<p>
			<s:text name="TradeTaxonomy.IndustryAverage"/>
			<s:property value="trade.getNaicsTRIRI()" />
		</p>
		<p>
			<s:text name="TradeTaxonomy.NumberOfContractors">
				<s:param><s:property value="trade.contractorCount"/></s:param>
			</s:text>
		</p>
		<p>
			<a href="<s:url action="ContractorList"><s:param name="filter.trade" value="%{trade.id}"/></s:url>">
				<s:text name="TradeTaxonomy.ViewContractors"/>
			</a>
		</p>
	</div>
</s:else>