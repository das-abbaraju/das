var search_tree, browse_tree;

var ajaxUrl = 'ContractorTrades!tradeAjax.action?contractor='+conID+'&trade.trade=';

function loadTradeCallback() {
	if ($('input.service,input.product').length > 0) {
		$("#addButton").attr("disabled", "disabled");
	}

	$('#trade_children').hide();

	if ($('#nonSelectable').length > 0) {
		$("#addButton").attr("disabled", "disabled");
		$('#activityPercent').addClass('hide');
		$('#tradeOptions').addClass('hide');
	}
	
	if ($('#reviewTrades').length > 0) {
		$("#addButton").attr("disabled", "disabled");
	}
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

	$('#search-list').delegate('a.trade', 'click', function(e) {
		e.preventDefault();
		$('#trade-view').load($(this).attr('href'), loadTradeCallback);
	});

	$('#trade-view').delegate('a.trade', 'click', function(e) {
		e.preventDefault();
		$('#trade-view').load($(this).attr('href'), loadTradeCallback);
		
	});

	$('#trade-view').delegate('#trade-form', 'submit', function(e) {
		e.preventDefault();
	}).delegate('#trade-form .save', 'click', function(e) {
		$('#trade-view').load('ContractorTrades!saveTradeAjax.action', $('#trade-form').serializeArray(), loadTradeCallback)
	}).delegate('#trade-form .remove', 'click', function(e) {
		if (confirm("Are you sure you want to remove this trade?")) {
			$('#trade-view').load('ContractorTrades!removeTradeAjax.action', $('#trade-form').serializeArray());
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