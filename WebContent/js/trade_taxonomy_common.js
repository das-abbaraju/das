function placeholder() {
	$('#suggest input.searchText').val($('#suggest input.searchText').attr("placeholder")).css("color", "#999");
	$('#suggest input.searchText').click(function() {
		$(this).val("");
		$(this).css("color", "black");
		$(this).unbind("click");
	});
}

$(function() {
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
						result = $('#suggest').serialize();
						if (node.attr) {
							result += "&trade=" + node.attr('id');
						}
						return result;
					}
				}
			}
	});

	browse_tree = $('#browse-tree').jstree();

	if ($.browser.msie) {
		placeholder();

		$('#suggest input.searchText').blur(function() {
			if ($(this).val().length == 0)
				placeholder();
		});
	}
});