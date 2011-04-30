<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<html>
<head>
<title>Contractor Trades</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>
<script type="text/javascript" src="js/jquery/jsTree/jquery.jstree.js?v=<s:property value="version"/>"></script>
<style>
a.trade, a.trade:hover, a.trade:active {
	text-decoration: none;
	white-space: nowrap;
}
#trade-cloud a.trade:hover {
	color: white;
	background-color: black;
}

<s:iterator value="contractor.trades">
li.trade-<s:property value="trade.id"/> > a {
	font-weight: bold;
	color: white;
	background-color: #012142;
}
</s:iterator>

#trade-view {
	margin: 20px;
}

#trade-view ul, #trade-view ol {
	list-style: none;
}
#trade-view ol > li {
	padding: 5px;
}

.fieldoption {
	padding: 10px;
}

.search {
	width: 400px;
}

</style>
<script>
var conID = '<s:property value="id"/>';
$(function() {
	var tree = $('#trade-nav').jstree({
		"themes": {
			theme: "classic"	
		},
		"json_data": {
			"ajax": {
				"url": 'TradeTaxonomy!json.action',
				"dataType": "json",
				"success": function(json) {
					return json.result;
				},
				"data": function(node) {
					result = $('#suggest').serialize();
					if (node.attr) {
						result += "&trade=" + node.attr('id');
					}
					return result;
				}
			}
		},
		"plugins": ['themes', "json_data", "search"]
	});

	function loadTrades(url, data) {
		$('#trade-view').load(url, data, function() {
			$('#trade-hierarchy').jstree({
				"themes": {
					theme: "classic"	
				},
				"json_data": {
					"ajax": {
						"url": 'TradeTaxonomy!hierarchyJson.action',
						"dataType": "json",
						"success": function(json) {
							return json.result;
						},
						"data": function(node) {
							return {
								trade: $('#trade-form [name=trade.trade]').val()
							};
						}
					}
				},
				"plugins": ['themes', "json_data"]
			}).resizeable();
		});
	}

	$('body').delegate('.jstree a', 'click', function(e) {
		e.preventDefault();
		var data = { contractor: conID, "trade.trade": $(this).parent().attr('id') };
		loadTrades('ContractorTrades!tradeAjax.action', data);
	});
	
	$('#trade-view').delegate('a.trade', 'click', function(e) {
		e.preventDefault();
		loadTrades($(this).attr('href'));
	});
	
	$('#suggest').submit(function(e) {
		e.preventDefault();
	});
	
	$('input.search').change(function(e) {
		tree.jstree('close_all').jstree('refresh');
	});

	$('#trade-view').delegate('#trade-form', 'submit', function(e) {
		e.preventDefault();
	}).delegate('#trade-form .save', 'click', function(e) {
		loadTrades('ContractorTrades!saveTradeAjax.action', $('#trade-form').serialize())
	}).delegate('#trade-form .remove', 'click', function(e) {
		if (confirm("Are you sure you want to remove this trade?")) {
			loadTrades('ContractorTrades!removeTradeAjax.action', $('#trade-form').serialize());
		}
	});
});
</script>
</head>
<body>

<s:include value="../contractors/conHeader.jsp"/>

<table class="clearfix">
	<tr>
	<td>
		<div>
		<form id="suggest">
			<label>Trade Search:</label>
			<input type="search" name="q" class="search" />
		</form>
		</div>
		<div id="trade-nav"></div>
	</td>
	<td width="20px"></td>
	<td id="trade-view">
		<s:include value="contractor_trade_cloud.jsp"/>
	</td>
	</tr>
</table>
</body>
</html>