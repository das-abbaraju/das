(function ($) {
    PICS.define('layout.menu.Menu', {
        methods: {
            jqXHR: null,

            init: function () {
                var navbar_element = $('#primary_navigation'),
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
                    items: 10,
                    source: PICS.debounce(this.search, 350),
                    menu: '<ul id="user_searchbox" class="typeahead dropdown-menu"></ul>',
                    item: '<li><a href="#"></a></li>'
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
                            'data-value': item.id,
                            'data-search': item.search
                        });

                        i.addClass(item.search);

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
                        id = item.attr('data-value'),
                        search = item.attr('data-search');

                    if (item.find('a').hasClass('more-results')) {
                        window.location.href = 'SearchBox.action?button=search&searchTerm=' + this.$element.val();
                    } else {
                        //TODO Fix backend call to not be ugly
                        window.location.href = 'Search.action?button=getResult&searchID=' + id + '&searchType=' + search;
                    }

                    return this.hide();
                }
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
                                return {
                                    id: item.result_id,
                                    name: item.result_name,
                                    location: item.result_at,
                                    search: item.search_type,
                                    type: item.result_type
                                };
                            }));
                        }

                        cls.jqXHR = null;
                    }
                });
            }
        }
    });
}(jQuery));