Ext.define('PICS.view.layout.SearchBox', {
    extend: 'Ext.form.field.ComboBox',
    alias: 'widget.searchbox',

    autoScroll: false,
    autoSelect: false,
    cls: 'site-menu-search',
    displayField: 'name',
    emptyText: 'Search',
    fieldLabel: '<i class="icon-search icon-large"></i>',
    hideTrigger: true,
    labelSeparator: '',
    labelWidth: 15,

    listConfig: {
        cls: 'site-menu-search-list',
        listeners: {
            el: {
                click: {
                    delegate: '.more-results',
                    fn: function (e, t, eOpts) {
                        var cmp = Ext.ComponentQuery.query('searchbox')[0];
                        var term = cmp.inputEl.getValue();

                        cmp.search(term);
                    }
                }
            }
        },
        loadingText: 'Searching...',
        maxHeight: 700,
        minWidth: 300,

        tpl: Ext.create('Ext.XTemplate',
            '<ul>',
                '<tpl for=".">',
                    '<li role="option" class="x-boundlist-item {[xindex % 2 === 0 ? "even" : "odd"]}">',
                        '<div class="search-item">',
                            '<div>',
                                '<span class="name"><em>{result_name}</em></span>',
                                '<span class="id"><em>ID {result_id}</em></span>',
                            '</div>',
                            '<div>',
                                '<span class="location">{result_at}</span>',
                                '<span class="type">{result_type}</span>',
                            '</div>',
                        '</div>',
                    '</li>',
                '</tpl>',
                '{[this.setTotalResults()]}',
                '<tpl if="this.total_results &gt; 0">',
                    '<li class="more-results {[(this.getTotalRecords() - 1) % 2 === 0 ? "even" : "odd"]}">',
                        '<tpl if="this.total_results &gt; 10">',
                            '<a href="#">',
                                'More Results...',
                            '</a>',
                        '</tpl>',
                        '<p>Displaying {[this.getTotalRecords()]} of {[this.total_results]}</p>',
                    '</li>',
                '</tpl>',
                '<tpl if="this.total_results == 0">',
                    '<li class="no-results">No results found</li>',
                '</tpl>',
            '</ul>',
            {
                total_results: 0,
                getTotalRecords: function () {
                    var combo_store = Ext.ComponentQuery.query('searchbox')[0].getStore();

                    return combo_store.getCount();
                },
                setTotalResults: function () {
                    var combo_store = Ext.ComponentQuery.query('searchbox')[0].getStore();

                    this.total_results = combo_store.getTotalCount();
                }
        })
    },

    listeners: {
        select: function (combo, records, eOpts) {
            var post = records[0];

            if (post) {
                var id = escape(post.get('result_id'));
                var type = escape(post.get('search_type'));
                var search_terms = 'button=getResult&searchID=' + id + '&searchType=' + type;
                document.location = '/Search.action?' + search_terms;
            }
        },

        specialkey: function (base, e, eOpts) {
            if (e.getKey() === e.ENTER) {
                var term = base.getValue();
                this.search(term);
            } else if (e.getKey() === e.BACKSPACE && base.getRawValue().length <= 1) {
                base.collapse();
            }
        }
    },

    minChars: 1,
    name: 'search_term',
    pickerAlign: 'br',
    pickerOffset: [-300, 2],
    queryMode: 'remote',
    queryParam: 'q',
    valueField: 'q',
    store: {
        fields: [
            'result_type',
            'result_id',
            'result_name',
            'result_at',
            'search_type'
        ],
        proxy: {
            type: 'ajax',
            url: '/SearchBox!json.action',
            reader: {
                root: 'results',
                totalProperty: 'total_results',
                type: 'json'
            }
        }
    },
    width: 200,

    search: function (term) {
        document.location = '/SearchBox.action?button=search&searchTerm=' + escape(term);

        return false;
    }
});
