<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Trade Taxonomy</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>
<script type="text/javascript" src="js/jquery/jquery.form.js"></script>
<script type="text/javascript" src="js/jquery/jsTree/jquery.jstree.js?v=<s:property value="version"/>"></script>
<script type="text/javascript" src="js/trade_taxonomy.js?v=<s:property value="version"/>"></script>

<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/blockui/blockui.css" />
<script type="text/javascript" src="js/jquery/blockui/jquery.blockui.js"></script>

<style>
#trades {
	width: 100%;
	padding-bottom: 1em;
}
#trade-nav {
	width: 50%;
}

#tree-wrapper {
	min-height: 200px;
}

#trade-nav li.decorated > a {
	font-style: italic;
	font-weight: bold;
	color: white;
	background-color: #012142;
}

.tradelogo {
	float: right;
	margin: 10px;
	width: 200px;
	height: 200px;
}
</style>
</head>
<body>
<h1>Trade Taxonomy</h1>

<a href="#" class="add">Add Top Level Trade</a>
<s:include value="../actionMessages.jsp"></s:include>

<table id="trades">
	<tr>
		<td>
		<div id="tree-wrapper">
			<form id="suggest">
				<label>Trade Search:</label>
				<s:textfield name="q" style="width: 400px" />
				<input type="button" value ="Search" class="trade_search" />
				<input type="button" value ="Clear" class="trade-clear" />
			</form>
			<div id="trade-nav"></div>
		</div>
		</td>
		<td id="trade-detail">
			<div class="info">Click a trade on the left</div>
			<div id="loadingTrade"></div>
		</td>
	</tr>
</table>

<s:form>
	<s:submit action="TradeTaxonomy!index" value="Index Trade Taxonomy" cssClass="picsbutton" />
</s:form>

</body>
</html>