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
						result += "trade=" + node.data('jstree').id;
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
		"ui": {
			"select_limit": -1,
			"select_multiple_modifier": "ctrl", 
			/*"select_multiple_modifier": "shift"*/
		},
		"contextmenu": {
			"show_at_node": false,
			"select_node": true,
			"items": function(node) {
				return {
					"refresh": {
						"label": "Refresh",
						"action": function(node) {
							this.refresh(node);
						}
					},
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
		"plugins": ["themes", "json_data", "types", "dnd", "crrm", "contextmenu", "ui"]
	}).bind("move_node.jstree", function (e, data) {
		var parent = null;
		if (data.rslt.np[0] !== this)
			parent = data.rslt.np.data('jstree').id;
		
		var trades = [];
		$.each(data.rslt.o, function(i, v) {
			trades.push($(v).data('jstree').id);
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
	});

	$('#trade-nav').delegate('.jstree a', 'dblclick', function(e) {
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

function saveTrade() {
	var data = $('#saveTrade').serialize();
	$.ajax({
		type: 'POST',
		url: 'TradeTaxonomy!saveTradeAjax.action',
		data: data,
		success: function() {
			alert("complete");
		}
	});
}
