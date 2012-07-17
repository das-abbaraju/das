Ext.define('PICS.view.layout.SearchBox', {
    extend: 'Ext.form.field.ComboBox',
    alias: ['widget.searchbox'],

    autoScroll: false,
    autoSelect: false,
    displayField: 'name',
    emptyText: 'Search',
    fieldLabel: '<i class="icon-search icon-large"></i>',
    hideTrigger: true,
    id: 'site_menu_search',
    labelSeparator: '',
    labelWidth: 25,

    listConfig: {
        id: 'site_menu_search_list',
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
        maxHeight: 500,
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
                                '<span class="company">{result_at}</span>',
                                '<span class="type">{result_type}</span>',
                            '</div>',
                        '</div>',
                    '</li>',
                    '<tpl if="xindex == xcount">',
                        '<li>',
                            '<div class="search-item">',
                                '<a href="#" class="more-results">',
                                    'More Results...',
                                '</a>',
                            '</div>',
                        '</li>',
                    '</tpl>',
                '</tpl>',
            '</ul>'
        ),
    },

    listeners: {
        select: function (combo, records, eOpts) {
            var post = records[0];

            if (post) {
                this.search(post.get('name'));
            }
        },

        specialkey: function (base, e, eOpts) {
            if (e.getKey() === e.ENTER) {
                this.search(base.getValue());
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
        fields: [ 'result_type', 'result_id', 'result_name', 'result_at' ],
        proxy: {
            type: 'ajax',
            url: 'SearchBox!json.action',
            reader: {
                root: 'results',
                type: 'json',
                totalProperty: 'totalCount'
            }
        }
    },
    width: 200,

    search: function (term) {
        document.location = '/SearchBox.action?button=search&searchTerm=' + term;

        return false;
    }
});
