var search_tree, browse_tree;
function loadTradeCallback() {
	$('#trade-hierarchy').jstree({
		"json_data": {
			"ajax": {
				"url": function(node) {
					if (node == -1)
						return 'TradeTaxonomy!hierarchyJson.action';
					else
						return 'TradeTaxonomy!json.action';
				},
				"data": function(node) {
					if (node == -1) {
						return {
							trade: $('#trade-form [name=trade.trade]').val()
						};
					} else {
						result = $('#suggest').serialize();
						if (node.attr) {
							result += "&trade=" + node.attr('id');
						}
						return result;
					}
				}
			}
		}
	});

	if ($('input.services,input.products').length > 0) {
		$("#addButton").attr("disabled", "disabled");
	}

	$('div.trade-section').hide();
}

function setupTree() {
	$.extend(true, $.jstree.defaults, {
		"plugins": ["themes", "types", "json_data", "ui", "sort"]
	});
}

$(function() {
	$('a.tradeInfo').live('click',function(e) {
		e.preventDefault();
		$($(this).attr('href')).toggle();
	});
	
	$('body').delegate('.jstree a', 'click', function(e) {
		e.preventDefault();
		var data = { contractor: conID, "trade.trade": $(this).parent().attr('id') };
		$('#trade-view').load('ContractorTrades!tradeAjax.action', data, loadTradeCallback);
	});
	
	$('#trade-view').delegate('a.trade', 'click', function(e) {
		e.preventDefault();
		$('#trade-view').load($(this).attr('href'), loadTradeCallback);
	});
	
	$('#suggest').submit(function(e) {
		e.preventDefault();
		search_tree.jstree('close_all').jstree('refresh');
	});

	$('#trade-view').delegate('#trade-form', 'submit', function(e) {
		e.preventDefault();
	}).delegate('#trade-form .save', 'click', function(e) {
		$('#trade-view').load('ContractorTrades!saveTradeAjax.action', $('#trade-form').serializeArray(), loadTradeCallback)
	}).delegate('#trade-form .remove', 'click', function(e) {
		if (confirm("Are you sure you want to remove this trade?")) {
			loadTrades('ContractorTrades!removeTradeAjax.action', $('#trade-form').serializeArray());
		}
	});

	$('input.service').live("change", function() {
		if ($('input.service:checked').length > 0)
			$("div.buttons .picsbutton").removeAttr("disabled");
		else
			$("div.buttons .picsbutton").attr("disabled", "disabled");
	});
	
	$('input.product').live('change', function() {
		if ($(this).is(':checked'))
			$("#addButton").removeAttr("disabled");
		else
			$("#addButton").attr("disabled", "disabled");
	});
});