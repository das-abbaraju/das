var search_tree, browse_tree;

var ajaxUrl = 'ContractorTrades!tradeAjax.action?contractor='+conID+'&trade.trade=';

function loadTradeCallback() {
	if (($('input.product').length > 0) || ($('input.service').length == 2 && $('input.service:checked').length == 0)) {
		$("#addButton").attr("disabled", "disabled");
	}

	$('#trade_children').hide();

	if ($('#nonSelectable').length > 0) {
		$("#addButton").attr("disabled", "disabled");
		$('#activityPercent').addClass('hide');
		$('#tradeOptions').addClass('hide');
	}

	setupCluetip();
}

function setupCluetip() {
	$('a.CTInstructions').cluetip( {
		local: true,
		hideLocal: true,
		showTitle: false,
		hoverClass : 'cluetip',
		cluetipClass : 'jtip',
		arrows: true
	});	
}

$(function() {
	$('a.tradeInfo').live('click',function(e) {
		e.preventDefault();
		
		$('#trade_children').toggle();
	});

	$('body').delegate('.jstree a', 'click', function(e) {
		e.preventDefault();
		var data = { contractor: conID, "trade.trade": $(this).parent().attr('id') };
		$('#trade-view').load('ContractorTrades!tradeAjax.action', data, loadTradeCallback);
	});

	$('#trade-view').delegate('#trade-form', 'submit', function(e) {
		e.preventDefault();
	}).delegate('#trade-form .save', 'click', function(e) {
		$('#trade-view').load('ContractorTrades!saveTradeAjax.action', $('#trade-form').serializeArray(), loadTradeCallback)
	}).delegate('#trade-form .remove', 'click', function(e) {
		if (confirm(translate("JS.TradeTaxonomy.RemoveTrade"))) {
			$('#trade-view').load('ContractorTrades!removeTradeAjax.action', $('#trade-form').serializeArray());
		}
	});

	$('input.service').live("change", function() {
		if ($('input.service').length == 2 && $('input.service:checked').length == 0)
			$("#addButton").attr("disabled", "disabled");
		else
			$("#addButton").removeAttr("disabled");
	});

	$('input.product').live('change', function() {
		if ($(this).is(':checked'))
			$("#addButton").removeAttr("disabled");
		else
			$("#addButton").attr("disabled", "disabled");
	});

	setupCluetip();
	
	if (!$('#trade-view-single').length > 0) {
		var maxArray = [];
		$('a.trade').each(function(){
			var classArray = $.trim($(this).attr('class')).split(' ');
			for (var cls in classArray) {
				var str = classArray[cls];
				if(str.indexOf('trade-cloud-') > -1){
					maxArray.push(Number((str.slice(str.lastIndexOf('-')+1, str.length))));
					break;
				}
			}
		});
		if(maxArray.length > 0){
			var max = Math.max.apply(Math, maxArray);
			$('a.trade-cloud-'+max).first().click();
		}
	}
});