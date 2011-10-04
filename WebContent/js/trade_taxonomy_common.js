function placeholder() {
	$('#suggest input.searchText').val($('#suggest input.searchText').attr("placeholder")).css("color", "#999");
	$('#suggest input.searchText').click(function() {
		$(this).val("");
		$(this).css("color", "black");
		$(this).unbind("click");
	});
}

$(function() {
	var ROOT_NODE = 5; /* Trade.TOP_ID */
	var oldform = $('#trade-view').html();
	
	if (typeof(loadTradeCallback) == "undefined") {
		loadTradeCallback = $.noop;
	}

	if (typeof(setupTree) == 'function') {
		setupTree();
	} else {
		$.extend(true, $.jstree.defaults, {
			"plugins": ["themes", "types", "json_data", "ui", "sort"]
		});
	}

	$('#trade-nav').tabs();
	
	/**
	 * jsTree configuration
	 * 
	 * http://www.jstree.com/documentation/core#configuration
	 */
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
		},
		"ui": {
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
	
	/**
	 * jsTree
	 * 
	 * http://www.jstree.com/documentation/json_data
	 */
	search_tree = $('#search-tree').jstree({
		json_data: {
			// The ajax config object is pretty much the same as the jQuery ajax settings object.
			// http://www.jstree.com/documentation/json_data#configuration
			ajax: {
				url: function(node) {
					if (node.attr) {
						return 'TradeTaxonomy!json.action';
					}
					
					return 'TradeTaxonomy!searchJson.action';
				},
				data: function(node) {
					result = $('#suggest').serializeArray();
					
					if (node.attr) {
						result.push({
							name: 'trade', 
							value: node.attr('id')
						});
					} else {
						result.push({
							name: 'trade', value: ROOT_NODE
						});
					}
					
					return result;
				},
				success: function(json) {
					return json.result;
				},
				complete: function(XMLHttpRequest, textStatus) {
					// remove the loading class set on the jsTree ul element
					$('#search-tree ul:first').removeClass('load-area');
				}
			}
		}
	});
	
	browse_tree = $('#browse-tree').jstree();

	$('#search-tab').delegate('input.searchType', 'click', function(e) {
		var parent = $(this).closest('#search-tab');
		parent.removeClass(function (index, css) {
		    return (css.match(/\bsearchType-\S+/g) || []).join(' ');
		});

		parent.addClass('searchType-' + $(this).val());

		$('#suggest').trigger('submit');
	});

	$('#suggest .searchText').live('change', function(e) {
		$(this).closest('#search-tab').removeClass(function (index, css) {
		    return (css.match (/\bclean-\S+/g) || []).join(' ');
		});
	});

	/**
	 * Search Trades List
	 */
	$('div.searchType-list #suggest').live('submit', function(event) {
		event.preventDefault();
		
		var q = $('input[name="q"]', this).val();
		
		// clear search list
		$('#search-list').empty();
		
		// must have at least one character to be searched
		if ($.trim(q).length > 0) {
			$('#search-list').html('<div class="load-area">');
			
			$.post('TradeAutocomplete!json.action', $(this).serializeArray(), function(json) {
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

							var li = $('<li>').append(
								$('<a>', { "href":ajaxUrl+trade.id, "class":"trade "+trade.type }).html(linkText)
							);
							
							ul.append(li);
						});
						
						$('#search-list').html(ul);
					} else {
						$('#search-list').msg('alert', translate('JS.TradeTaxonomy.NoMatch'), true);
					}
				}
			}, 'json')
		}
		
		$(this).parent().addClass('clean-list');
	});

	/**
	 * Search Trades Tree
	 */
	$('div.searchType-tree #suggest').live('submit', function(event) {
		event.preventDefault();
		
		var q = $('input[name="q"]', this).val();
		
		// clear search list
		$('#search-list').empty();
		
		// must have at least one character to be searched
		if ($.trim(q).length > 0) {
			search_tree.children('ul').addClass('load-area');
		}
				
		search_tree.jstree('close_all');
		
		search_tree.jstree('refresh');
		
		$(this).parent().addClass('clean-tree');
	});

	$('#search-tab input.searchType:checked').trigger('click');

	if ($.browser.msie) {
		placeholder();

		$('#suggest input.searchText').blur(function() {
			if ($(this).val().length == 0) {
				placeholder();
			}
		});
	}
	
	if (!window.TRADES) {
		TRADES = {};
	}
	
	TRADES._config = {
		load_area: {
			min: 200,
			max: 700
		}
	};
	
	// select trade from search / browse
	TRADES.select_trade = {
		init: function() {
			/**
			 * Search List Trade Link
			 * Trade Cloud Trade Link
			 * Trade Cloud Specialties Links
			 */
			$('#search-list, #trade-view').delegate('a.trade', 'click', function(event) {
				event.preventDefault();
				
				var element = $('#trade-view');
				var url = $(this).attr('href');
				
				TRADES.select_trade.events.select_trade(element, url);
			});
			
			/**
			 * Search Tree Trade Link
			 * Browse Tree Trade Link
			 */
			$('#trade-nav').delegate('.jstree a', 'click', function(event) {
				event.preventDefault();
				
				var element = $('#trade-view');
				var url = ajaxUrl + $(this).parent().attr('id');
				
				TRADES.select_trade.events.select_trade(element, url);
			});
		},
		
		events: {
			select_trade: function(element, url) {
				var element_height = element.height();
				
				element.css({
					height: element_height > TRADES._config.load_area.max || element_height < TRADES._config.load_area.min ? TRADES._config.load_area.max : element_height
				});
				
				element.html('<div class="load-area">');
				
				element.load(url, function() {
					loadTradeCallback();
					
					element.css({
						height: ''
					});
				});
			}
		}
	};
	
	TRADES.select_trade.init();

	// trade taxonomy add top level trade
	TRADES.add_trade = {
		init: function() {
			$('a.add.trade').live('click', function(event) {
				event.preventDefault();
				
				var element = $('#trade-view');
				var url = 'TradeTaxonomy!tradeAjax.action';
				
				TRADES.add_trade.events.add_trade(element, url);
			});
		},
		
		events: {
			add_trade: function(element, url) {
				var element_height = element.height();
				
				element.css({
					height: element_height > TRADES._config.load_area.max || element_height < TRADES._config.load_area.min ? TRADES._config.load_area.max : element_height
				});
				
				element.html('<div class="load-area">');
				
				element.load(url, function() {
					loadTradeCallback();
					
					element.css({
						height: ''
					});
				});
			}
		}
	};
	
	TRADES.add_trade.init();
});