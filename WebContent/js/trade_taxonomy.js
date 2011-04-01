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
						result += "&trade=" + node.data('jstree').id;
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
