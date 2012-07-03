Ext.define('PICS.view.layout.SearchBox', {
    extend: 'Ext.form.field.ComboBox',
    alias: ['widget.searchbox'],

    autoScroll: false,
    displayField: 'name',
    // TODO use the translated 'search' value
    emptyText: 'search',
    hideTrigger: true,
    fieldLabel: '<i class="icon-search icon-large"></i>',
    labelSeparator: '',
    // TODO remove inline style
    labelStyle: 'color: gray;',
    labelWidth: '25px',

    listConfig: {
        id: 'site_menu_search_list',
        // TODO get the translated version
        loadingText: 'Searching...',
        maxHeight: 500,

        tpl: Ext.create('Ext.XTemplate',
            '<ul>',
                '<tpl for=".">',
                    '<li role="option" class="x-boundlist-item {[xindex % 2 === 0 ? "even" : "odd"]}">',
                        '<div class="search-item">',
                            '<div>',
                                '<span class="name"><em>{name}</em></span>',
                                '<span class="id"><em>ID {id}</em></span>',
                            '</div>',
                            '<div>',
                                '<span class="company">{at}</span>',
                                '<span class="type">{type}</span>',
                            '</div>',
                        '</div>',
                    '</li>',
                '</tpl>',
            '</ul>'
        ),
    },

    listeners: {
//        beforequery: function (queryEvent, options) {
//            // Prevent empty queries
//            if (!queryEvent.query) {
//                return false;
//            }
//        },
        select: function(combo, selection) {
            var post = selection[0];
            if (post) {
                // TODO change this to be more betterer
                window.location = Ext.String.format('http://localhost:8080/SearchBox.action?button=search&searchTerm={0}', post.get('name'));
            }
        }
    },

    minChars: 1,
    name: 'searchTerm',
    queryMode: 'remote',
    queryParam: 'q',
    valueField: 'q',

    store: {
        fields: [ 'type', 'id', 'name', 'at' ],
        proxy: {
            type: 'ajax',
            url: 'SearchBoxJson.action',
            reader: {
                root: 'results',
                type: 'json',
                totalProperty: 'totalCount'
            }
        }
    },
    width: 300
});
