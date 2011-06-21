<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<div id="trade-nav">
	<ul>
		<li><a href="#search-tab"><s:text name="ContractorTrades.header.Search"/></a></li>
		<li><a href="#browse-tab"><s:text name="ContractorTrades.header.Browse"/></a></li>
	</ul>
	<div id="search-tab">
		<form id="suggest">
			<input type="search" class="searchText" name="q" placeholder="Search..."/>
			<input type="submit" class="searchButton" title="Submit Search" value="Search" />
		</form>
		Use one or more key words to search for what your company does. Examples might include "drilling wells", "asbestos abatement", "civil engineering" or even a NAICS code.<br/><strong>Display results as:</strong>
		<s:radio cssClass="searchType" name="searchType" list="#{'list':'List', 'tree':'Tree'}" value="'list'"/>
		<div class="messages"></div>
		<div id="search-list"></div>
		<div id="search-tree"></div>
	</div>
	<div id="browse-tab">
		<div id="browse-tree"></div>
		<pics:permission perm="ManageTrades" type="Edit">
		<div id="indexTrades">
			<s:form>
				<s:submit action="TradeTaxonomy!index" value="Reindex Trade Nested Set" title="Click this button after rearranging the trades." />
			</s:form>
		</div>
		</pics:permission>
	</div>
	<div>
		<span class="service">Service</span>
		<span class="product">Product</span>
		<span class="product-service">Both</span>
	</div>
</div>