(function ($) {
    PICS.define('layout.menu.Menu', {
        methods: {
            init: function () {
                var navbar_element = $('.navbar'),
                    dropdown_toggle_element = navbar_element.find('.dropdown-toggle'),
                    search_query_element = navbar_element.find('.search-query');
                
                if (navbar_element.length > 0) {
                    // drop down menu
                    this.configureDropdown(dropdown_toggle_element);
                    
                    // autocomplete user searchbox
                    this.configureUserSearch(search_query_element);
                }
            },
            
            configureDropdown: function (dropdown_toggle_element) {
                dropdown_toggle_element.dropdown();
            },
            
            configureUserSearch: function (search_query_element) {
                search_query_element.typeahead({
                    source: PICS.debounce(this.search, 350),
                    menu: '<ul id="user_searchbox" class="typeahead dropdown-menu"></ul>',
                    item: '<li class="user"><a href="#"></a></li>'
                });
                
                search_query_element.data('typeahead').process = function (items) {
                    var that = this;

                    if (!items.length) {
                        return this.shown ? this.hide() : this;
                    }
                    
                    return this.render(items.slice(0, this.options.items)).show();
                };
                
                search_query_element.data('typeahead').render = function (items) {
                    var that = this;

                    items = $(items).map(function (i, item) {
                        i = $(that.options.item).attr({
                            'data-name': item.name,
                            'data-value': item.id
                        });
                        
                        i.find('a').html([
                            '<div>',
                                '<strong class="name">',
                                    item.name,
                                '</strong>',
                                '<strong class="id">',
                                    item.id,
                                '</strong>',
                            '</div>',
                            '<div>',
                                '<span class="company">',
                                    item.company,
                                '</span>',
                                '<span class="type">',
                                    item.type,
                                '</span>',
                            '</div>',
                        ].join(''));
                        
                        return i[0];
                    });
                    
                    // add custom more results link
                    more_results = $('<li><a href="#" class="more-results"><i>More Results</i></a></li>');
                    items.push(more_results[0]);

                    items.first().addClass('active');
                    this.$menu.html(items);
                    
                    return this;
                };
                
                search_query_element.data('typeahead').select = function () {
                    var item = this.$menu.find('.active'),
                        name = item.attr('data-name'),
                        val = item.attr('data-value');
                    
                    
                    if (item.find('a').hasClass('more-results')) {
                        window.location.href = 'SearchBox.action?button=search&searchTerm=' + this.$element.val();
                    } else {
                        window.location.href = 'ContractorView.action?id=' + val; 
                    }
                        
                    return this.hide();
                }
            },
            
            search: function (query, process) {
                PICS.ajax({
                    url: 'SearchBox!json.action',
                    dataType: 'json',
                    data: {
                        q: query
                    },
                    success: function (data, textStatus, jqXHR) {
                        process($.map(data.results, function (item) {
                            return {
                                name: item.result_name,
                                id: item.result_id,
                                type: item.result_type,
                                company: item.result_at
                            };
                        }));
                    }
                });
            }
        }
    });
}(jQuery));