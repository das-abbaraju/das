(function ($) {
    PICS.define('layout.menu.Menu', {
        methods: {
            jqXHR: null,

            init: function () {
                var navbar_element = $('#primary_navigation'),
                    dropdown_toggle_element = navbar_element.find('.dropdown-toggle'),
                    search_query_element = navbar_element.find('.search-query');

                if (navbar_element.length > 0) {
                    this.configureDropdown(dropdown_toggle_element);

                }
                
                // autocomplete user searchbox
                if (search_query_element.length > 0) {
                    this.configureUserSearch(search_query_element);
                }
            },

            configureDropdown: function (dropdown_toggle_element) {
                dropdown_toggle_element.dropdown();
            },

            configureUserSearch: function (search_query_element) {
                search_query_element.typeahead({
                    items: 10,
                    source: PICS.debounce(this.search, 350),
                    menu: '<ul id="user_searchbox" class="typeahead dropdown-menu dropdown-striped"></ul>',
                    item: '<li><a href="#"></a></li>'
                });


                search_query_element.data('typeahead').process = function (items, total_results) {
                    return this.render(items.slice(0, this.options.items), total_results).show();                    
                };

                search_query_element.data('typeahead').render = function (items, total_results) {
                    var that = this,
                        results = 0;

                    //format items
                    if (!items.length) {
                      items = $('<li class="no-results">No results found</li>');
                    } else {
                        items = $(items).map(function (i, item) {
                            i = $(that.options.item).attr({
                                'data-name': item.name,
                                'data-value': item.id,
                                'data-search': item.search
                            });

                            i.addClass(item.search + ' ' + item.status);

                            i.find('a').html([
                                '<div class="clearfix">',
                                    '<div class="name">',
                                        item.name,
                                    '</div>',
                                    '<div class="id">',
                                        item.id,
                                    '</div>',
                                '</div>',
                                '<div class="clearfix">',
                                    '<div class="location">',
                                        item.location,
                                    '</div>',
                                    '<div class="type">',
                                        item.type,
                                    '</div>',
                                '</div>',
                            ].join(''));

                            return i[0];
                        });

                        // save result length before adding display text
                        results = items.length;

                        // if the results are the same as the default
                        if (results === that.options.items) {
                            items.push($('<li class="more-results"><a href="#">More Results...</a></li>').get(0));
                        }

                        items.push($('<li class="total-results"><p>Displaying ' + results + ' of ' + total_results + '</p></li>').get(0));
                    }

                    items.first().addClass('active');

                    this.$menu.html(items);

                    return this;
                };

                search_query_element.data('typeahead').select = function () {
                    var item = this.$menu.find('.active'),
                        name = item.attr('data-name'),
                        id = item.attr('data-value'),
                        search = item.attr('data-search');

                    if (id) {
                        //TODO Fix backend call to not be ugly
                        window.location.href = 'Search.action?button=getResult&searchID=' + id + '&searchType=' + search;
                    } else if (item.hasClass('more-results')) {
                        window.location.href = 'SearchBox.action?button=search&searchTerm=' + this.$element.val();
                    }

                    return this.hide();
                };
                
                // fix for https://github.com/twitter/bootstrap/issues/2954
                search_query_element.data('typeahead').move = function (e) {
                    if (!this.shown) return

                    switch(e.keyCode) {
                        case 9: // tab
                        case 13: // enter
                        case 27: // escape
                            e.preventDefault()
                            break
                        
                        case 38: // up arrow
                            if (!e.charCode) {
                                e.preventDefault()
                                this.prev()
                            }
                            
                            break
                        
                        case 40: // down arrow
                            if (!e.charCode) {
                                e.preventDefault()
                                this.next()
                            }
                            
                            break
                    }

                    e.stopPropagation()
                };
            },

            search: function (query, process) {
                var that = this,
                    cls = PICS.getClass('layout.menu.Menu');

                if (cls && cls.jqXHR && typeof cls.jqXHR.abort == 'function') {
                    cls.jqXHR.abort();
                }

                cls.jqXHR = PICS.ajax({
                    url: 'SearchBox!json.action',
                    dataType: 'json',
                    data: {
                        q: query
                    },
                    success: function (data, textStatus, jqXHR) {
                        if (that.$element.val().length > 0) {
                            process($.map(data.results, function (item) {
                                var status = 'account-' + item.account_status;

                                return {
                                    id: item.result_id,
                                    name: item.result_name,
                                    location: item.result_at,
                                    search: item.search_type,
                                    type: item.result_type,
                                    status: status
                                };
                            }), data.total_results);
                        }

                        cls.jqXHR = null;
                    }
                });
            }
        }
    });
}(jQuery));