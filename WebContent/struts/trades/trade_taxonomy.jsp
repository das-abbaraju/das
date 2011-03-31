<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Trade Taxonomy</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<script type="text/javascript" src="js/jquery/jsTree/jquery.jstree.js?v=<s:property value="version"/>"></script>
<script type="text/javascript" src="js/trade_taxonomy.js?v=<s:property value="version"/>"></script>
<style>
#trades {
	width: 100%;
}
#trade-nav {
	width: 50%;
}
</style>
</head>
<body>
<h1>Trade Taxonomy</h1>

<div id="suggest">
<form>
	<label>Trade Search:</label>
	<input class="psAutocomplete" name="tradeSearch" style="width: 400px" />
</form>
</div>

<table id="trades">
	<tr>
		<td id="trade-nav">Loading Trades</td>
		<td id="trade-detail">Click a trade on the left
			<div id="loadingTrade"></div>
		</td>
	</tr>
</table>

<s:form cssStyle="padding-top: 1em">
	<s:submit action="TradeTaxonomy!index" value="Index Trade Taxonomy" cssClass="picsbutton" />
</s:form>

</body>
</html>