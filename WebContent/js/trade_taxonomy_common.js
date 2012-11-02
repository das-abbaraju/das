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
				},
                "transportation": {
                    "icon": {
                        "image": "images/icon_box_dark_blue.png",
                        "position": "0px 2px"
                    }
                },
                "transportation-service": {
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
        var input = $(this).find('input[name="q"]'),
            search_list = $('#search-list'),
            search_terms = input.val();

        event.preventDefault();

        // clear search list
        search_list.empty();

        // must have at least one character to be searched
        if ($.trim(search_terms).length > 0) {

            //split by search term
            search_terms = search_terms.split(' ');

            //loading icon
            search_list.html('<div class="load-area"></div>');

            PICS.ajax({
                url: 'TradeAutocomplete!json.action',
                data: $(this).serializeArray(),
                dataType: 'json',
                success: function (data, textStatus, jqXHR) {
                    if (data.result.length > 0) {
                        var trade_list = '<ul>';

                        //loop over each trade
                        $.each(data.result, function(i, trade) {
                            if (trade.name && trade.type){
                                var linkText = trade.name;

                                //add strong tag around search terms
                                $.each(search_terms, function(j, term) {
                                    var regex = new RegExp('(' + term + ')', 'ig');

                                    linkText = linkText.replace(regex, '<strong>$1</strong>');
                                });

                                //add to result list
                                var contractor_id = input.attr('data-contractor');

                                if (contractor_id.length > 0) {
                                    trade_list += '<li><a href="ContractorTrades!tradeAjax.action?contractor=' + contractor_id + '&trade.trade=' + trade.id + '" class="trade ' + trade.type + '">' + linkText + '</a></li>';
                                } else {
                                    trade_list += '<li><a href="TradeTaxonomy!tradeAjax.action?trade=' + trade.id + '" class="trade ' + trade.type + '">' + linkText + '</a></li>';
                                }
                            } else {
                                try {
                                    console.log(trade);
                                    console.log("Trade id " + trade.id + " has no name or type");
                                } catch (e) {}
                            }
                        });

                        trade_list += '</ul>';

                        search_list.html(trade_list);
                    } else {
                        search_list.msg('alert', translate('JS.TradeTaxonomy.NoMatch'), true);
                    }
                }
            });
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