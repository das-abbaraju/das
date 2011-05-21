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
			<ul class="filter">
				<li><a href="#">Most Used Products</a></li>
				<li><a href="#">Show All</a></li>
			</ul>
		</form>
		<div class="messages"></div>
		<div id="search-tree"></div>
	</div>
	<div id="browse-tab">
		<div id="browse-tree"></div>
	</div>
</div>