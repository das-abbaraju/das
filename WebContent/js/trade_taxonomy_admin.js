var search_tree, browse_tree;

function loadTradeCallback() {
	$('#saveTrade').ajaxForm({
		target: '#trade-view',
		success: function() {
			loadTradeCallback();
			browse_tree.jstree('refresh');
		}
	});
	var tradeID = $('#saveTrade [name="trade"]').val()
	if (tradeID > 0) {
		showCategoryRules(tradeID);
		showAuditTypeRules(tradeID);
	}
}

function showCategoryRules(tradeID) {
	startThinking({ div: "tradeCategoryRules", message: "Loading Related Category Rules" });
	$('#tradeCategoryRules').load('CategoryRuleTableAjax.action', { 'comparisonRule.trade': tradeID, 'columnsIgnore': ['auditType','bidOnly','rootCategory'] });
}

function showAuditTypeRules(tradeID) {
	startThinking({ div: "tradeAuditRules", message: "Loading Related Audit Type Rules" });
	$('#tradeAuditRules').load('AuditTypeRuleTableAjax.action', { 'comparisonRule.trade': tradeID, 'columnsIgnore': ['auditType','bidOnly','rootCategory'] });
}

function setupTree() {
	$.extend(true, $.jstree.defaults, {
		"contextmenu": {
			"show_at_node": false,
			"select_node": true,
			"items": function(node) {
				return {
					"addChild": {
						"label": "Add Child Object",
						"action": function(node) {
							this.open_node(node);
							$('#trade-view').load('TradeTaxonomy!tradeAjax.action', {"trade.parent": node.attr('id')}, loadTradeCallback);
						}
					},
					"refresh": {
						"label": "Refresh",
						"action": function(node) {
							this.refresh(node);
						}
					},
					"delete": {
						"label": "Delete",
						"action": function(node) {
							this.remove(node);
						}
					}
				}
			}
		},
		"plugins": ["themes", "types", "json_data", "dnd", "crrm", "contextmenu", "ui", "sort"]
	});
}

$(function() {
	$('#browse-tree').bind("move_node.jstree", function (e, data) {
		var parent = null;
		if (data.rslt.np[0] !== this)
			parent = data.rslt.np.attr('id');

		var trades = [];
		$.each(data.rslt.o, function(i, v) {
			trades.push($(v).attr('id'));
		});

		$.post('TradeTaxonomy!moveTradeJson.action',
			{
				trade: parent,
				trades: trades
			},
			function(json) {
				if (json.success) {
					data.inst.deselect_all();
					data.inst.select_node(data.rslt.o);
				} else {
					alert("Error moving trades. Please try again later.");
					$.jstree.rollback(data.rlbk);
				}
			},
			'json');
	}).bind('delete_node.jstree', function(e, data) {
		var trades = [];
		$.each(data.rslt.obj, function(i,v) {
			if ($(v).attr('id'))
				trades.push($(v).attr('id'));
		});
		if (trades.length > 0) {
			$.post('TradeTaxonomy!deleteMultipleJson.action', {
					trades: trades
				}, function(json) {
					if (json.success) {
						$('#trade-view').html(oldform);
						data.inst.deselect_all(data.inst.get_selected());
						tree.jstree("refresh");
					} else {
						if (json.actionError) {
							alert(json.actionError);
						}
						$.jstree.rollback(data.rlbk);
					}
				},
				'json');
		}
	}).bind('refresh.jstree', function(e, data) {
		$('#tree-wrapper').unblock();
		$('body').removeClass('busy')
	});

	$('#trade-view').delegate('#removelogo','click',function(e) {
		e.preventDefault();
		$('#trade-view').load('TradeTaxonomy!removeFileAjax.action', {trade: $('#saveTrade [name="trade"]').val()}, loadTradeCallback);
	}).delegate('#add-alternate','click', function(e) {
		e.preventDefault();
		$('#alternateNames').load('TradeTaxonomy!addAlternateAjax.action',
				{alternateCategory: $('#alternateCategory').val(), alternateName: $('#alternateName').val(), trade: $('#saveTrade [name="trade"]').val()});
		$('#alternateName').val('');
	}).delegate('#delete-alternate', 'click', function(e) {
		e.preventDefault();
		$('#alternateNames').load($(this).attr('href'), {trade: $('#saveTrade [name="trade"]').val()});
	});
});