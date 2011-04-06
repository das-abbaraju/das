$(function() {
	var oldform = $('#trade-detail').html();
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
						result += "trade=" + node.attr('id');
					}
					return result;
				}
			}
		},
		"ui": {
			"select_limit": -1,
			"select_multiple_modifier": "ctrl", 
		},
		"contextmenu": {
			"show_at_node": false,
			"select_node": true,
			"items": function(node) {
				return {
					"addChild": {
						"label": "Add Child Object",
						"action": function(node) {
							tree.jstree('open_node', node);
							$('#trade-detail').load('TradeTaxonomy!tradeAjax.action', {"trade.parent": node.attr('id')});
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
		"plugins": ["themes", "json_data", "dnd", "crrm", "contextmenu", "ui"]
	}).bind("move_node.jstree", function (e, data) {
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
					data.inst.deselect_all(data.inst.get_selected());
					data.inst.select_node(data.rslt.o);
				} else {
					alert("Error moving trades. Please try again later");
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
			$.post('TradeTaxonomy!deleteTradeAjax.action',
				{
					trades: trades
				}, 
				function(json) {
					if (json.success) {
						$('#trade-detail').html(oldform);
						data.inst.deselect_all(data.inst.get_selected());
						tree.jstree("refresh");
					} else {
						alert("Error deleting trade. Please try again later");
						$.jstree.rollback(data.rlbk);
					}
				}, 
				'json');
		}
	});

	$('#trade-nav').delegate('.jstree a', 'click', function(e) {
		e.preventDefault();
		var data = { trade: $(this).parent().attr('id') };
		setMainStatus('Loading Trade');
		$('#trade-detail').load('TradeTaxonomy!tradeAjax.action', data);
	});
	
	$('.psAutocomplete').autocomplete('TradeAutocomplete.action', {
    	minChars: 2,
    	max: 100,
    	formatResult: function(data,i,count) { return data[1]; }
    });
	
	$('a.add').live('click', function(e) {
		e.preventDefault();
		$('#trade-detail').load('TradeTaxonomy!tradeAjax.action');
	});
	
	$('#trade-detail').delegate('.save', 'click', function(e) {
		e.preventDefault();
		$('#trade-detail').load('TradeTaxonomy!saveTradeAjax.action', $('#saveTrade').serialize(), function() {
			tree.jstree('refresh');
		});
	}).delegate('.delete', 'click', function(e) {
		e.preventDefault();
		tree.jstree('remove', '#'+$(this).closest('form').find('[name=trade]').val());
	});
});
