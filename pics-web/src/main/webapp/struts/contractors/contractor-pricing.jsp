<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>

<s:set var="pricingAmounts" value="pricingTiers.get(0).pricingAmounts" />
<s:set var="num_client_sites" value="clients.size()" />

<img src="v7/img/logo/logo-large.png" class="logo" alt="PICS"/>

<%-- Page title --%>
<s:include value="/struts/employee-guard/_page-header.jsp">
    <s:param name="title"><s:text name="ContractorPricing.title" /></s:param>
    <s:param name="subtitle">&nbsp;</s:param>
    <s:param name="breadcrumbs">false</s:param>
</s:include>

<div class="row">
    <div class="col-md-8">
        <table class="table table-striped table-condensed">
            <thead>
                <tr>
                    <th><s:text name="ContractorPricing.PricingTable.clientSitesHeader" /></th>
                    <s:iterator value="pricingAmounts" var="pricingAmount">
                        <th>${pricingAmount.feeClass}</th>
                    </s:iterator>
                </tr>
            </thead>
            <tbody>
                <s:iterator value="pricingTiers" var="pricingTier" status="status">
                    <s:set var="level" value="#pricingTier.level == '50-10000' ? '50+' : #pricingTier.level" />
                    <s:set var="pricingAmounts" value="pricingTier.pricingAmounts" />

                    <tr>
                        <td class="level">${level}</td>
                        <s:iterator value="pricingAmounts" var="pricingAmount">
                            <s:set var="applies_class" value="#pricingAmount.applies ? 'applies' : ''" /> <%-- Change to: pricingAmount.applies ? 'applies' : '' --%>

                            <td class="${applies_class}">${pricingAmount.feeAmount} ${con.country.currency}</td>
                        </s:iterator>
                    </tr>
                </s:iterator>
            </tbody>
        </table>
        <p><s:text name="ContractorPricing.PricingTable.highlightExplanation" /></p>
    </div>
    <div class="col-md-4">
         <div class="client-list-panel panel panel-default">
            <div class="contractor-tier-arrow arrow-left arrow-default"></div>
            <div class="panel-heading">
                <s:set var="clients_list_title" value="(#num_client_sites == 1) ? 'ContractorPricing.ClientsListSingle.title' : 'ContractorPricing.ClientsList.title'" />

                <h3 class="panel-title">${num_client_sites} <s:text name="%{clients_list_title}" /></h3>
            </div>
            <div class="panel-body">
                <ul class="list-unstyled">
                    <s:iterator value="clients" var="client" status="status">
                        <s:set var="more_class" value="#status.index < 10 ? '' : 'more'" />

                        <li class="${more_class}"><s:property value="name" /></li>
                    </s:iterator>

                    <s:if test="#num_client_sites > 10">
                        <a class="show-more-link" href="#"><s:text name="ContractorPricing.ClientsList.showMoreLinkText" /></a>
                        <a class="show-less-link" href="#"><s:text name="ContractorPricing.ClientsList.showLessLinkText" /></a>
                    </s:if>
                </ul>
            </div>
        </div>
    </div>
</div>