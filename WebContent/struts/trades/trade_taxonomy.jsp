<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Trade Taxonomy</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<script type="text/javascript" src="js/jquery/jsTree/jquery.jstree.js"></script>
<script type="text/javascript">
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
					result = $('#filter').serialize();
					if (node.attr) {
						result += "&trade=" + node.attr("id");
					}
					return result;
				}
			}
		},
		"types": {
			"default": {
			"select_node": true,
			"hover_node":true
			}
		},
		"contextmenu": {
			"show_at_node": false,
			"select_node": true,
			"items": function(node) {
				return {
					"rename": {
						"label": "Rename",
						"action": function() {
						}
					},
					"delete": {
						"label": "Delete",
						"action": function() {
						}
					},
					"addChild": {
						"label": "Add Child Object",
						"action": function() {
						}
					}
				};
			}
		},
		"plugins": ["themes", "json_data", "types", "dnd", "contextmenu", "ui"]
	}).bind("move_node.jstree", function (e, data) {
		var np = data.rslt.np.attr('id').split('_');
		alert("move");
		var types=[], ids=[];
		data.inst._get_children(data.rslt.np).each(function(i) {
			var n = $(this).attr('id').split('_');
			types.push(n[0]);
			ids.push(n[1]);
		});
		startThinking();
		$.ajax({
			async: false,
			type: 'POST',
			dataType: 'json',
			url: '',
			data: {
				button: 'move',
				types: types,
				ids: ids,
				parentType: np[0],
				parentID: np[1]
			},
			success: function(r) {
				if (r.success) {
					data.inst.refresh(data.rslt.np);
					data.inst.refresh(data.rslt.op);
					data.inst.open_node(data.rslt.op);
				} else {
					$.jstree.rollback(data.rlbk);
				}
				stopThinking();
			}
		});
	});
	$('#trade-nav').delegate('.jstree a', 'click', function(e) {
		e.preventDefault();
		var data = { trade: $(this).parent().data('jstree').id };
		setMainStatus('Loading Trade');
		$('#trade-detail').load('TradeTaxonomy!tradeAjax.action', data);
	});
	
	$('.psAutocomplete').autocomplete('TradeAutocomplete.action', {
    	minChars: 2,
    	max: 100,
    	formatResult: function(data,i,count) { return data[1]; }
    });
});
</script>
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