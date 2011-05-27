function placeholder() {
	$('#suggest input.searchText').val($('#suggest input.searchText').attr("placeholder")).css("color", "#999");
	$('#suggest input.searchText').click(function() {
		$(this).val("");
		$(this).css("color", "black");
		$(this).unbind("click");
	});
}

$(function() {
	var ROOT_NODE = 5;
	var oldform = $('#trade-view').html();

	if (typeof(setupTree) == 'function') {
		setupTree();
	}

	$('#trade-nav').tabs();

	$.extend(true, $.jstree.defaults, {
		"themes": {
			theme: "classic"
		},
		"types": {
			"types": {
				"service": {
					"icon": {
						"image": "images/icon_box_blue.png",
						"position": "0px 2px"
					}
				},
				"product": {
					"icon": {
						"image": "images/icon_box_orange.png",
						"position": "0px 2px"
					}
				},
				"product-service": {
					"icon": {
						"image": "images/icon_box_combo.png",
						"position": "0px 2px"
					}
				}
			}
		},"ui": {
			"select_limit": -1,
			"select_multiple_modifier": "ctrl"
		},
		"json_data": {
			"ajax": {
				"url": 'TradeTaxonomy!json.action',
				"success": function(json) {
					return json.result;
				},
				"data": function(node) {
					result = {};
					if (node.attr) {
						result.trade = node.attr('id');
					} else {
						result.trade = ROOT_NODE;
					}
					return result;
				}
			}
		}
	});

	search_tree = $('#search-tree').jstree({
			"json_data": {
				"ajax": {
					"url": function(node) {
						if (node.attr) {
							return 'TradeTaxonomy!json.action';
						}
						return 'TradeTaxonomy!searchJson.action';
					},
					"success": function(json) {
						return json.result;
					},
					"data": function(node) {
						result = $('#suggest').serializeArray();
						if (node.attr) {
							result.push({name: "trade", value: node.attr('id')});
						} else {
							result.push({name: "trade", value: ROOT_NODE});
						}
						return result;
					}
				}
			}
	});

	browse_tree = $('#browse-tree').jstree();

	$('#search-tab').delegate('input.searchType', 'click', function(e) {
		var parent = $(this).closest('#search-tab');
		parent.removeClass(function (index, css) {
		    return (css.match (/\bsearchType-\S+/g) || []).join(' ');
		});

		parent.addClass('searchType-' + $(this).val());
	});

	$('#search-tab input.searchType:checked').trigger('click');

	$('div.searchType-list #suggest').live('submit', function(e) {
		var q = $('input[name="q"]', this).val();
		if ($.trim(q).length > 0) {
			$.post('TradeAutocomplete!tokenJson.action', $(this).serializeArray(), function(json) {
				if (json.result) {
					if (json.result.length > 0) {
						var ul = $('<ul>');
						$.each(json.result, function(i, trade) {
							var linkText = trade.name;
							$.each(q.split(' '), function(i, term) {
								if ($.trim(term).length > 0) {
									var regex = new RegExp("("+term+")", "ig")
									linkText = linkText.replace(regex,"<strong>$1</strong>");
								}
							});

							var li = $('<li>')
								.append(
									$('<a>', { "href":ajaxUrl+trade.id, "class":"trade" })
										.html(linkText)
									);
							ul.append(li);
						});
						$('#search-list').html(ul);
					} else {
						$('#search-list').msg('alert', 'No results for that query.', true);
					}
				}
			}, 'json')
		} else {
			$('#search-list').html('');
		}
	});

	$('div.searchType-tree #suggest').live('submit', function(e) {
		search_tree.jstree('close_all').jstree('refresh');
	});

	$('#suggest').submit(function(e) {
		e.preventDefault();
	});

	if ($.browser.msie) {
		placeholder();

		$('#suggest input.searchText').blur(function() {
			if ($(this).val().length == 0)
				placeholder();
		});
	}
});