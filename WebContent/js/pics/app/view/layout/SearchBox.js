Ext.define('PICS.view.layout.SearchBox', {
    extend: 'Ext.form.field.ComboBox',
    alias: ['widget.searchbox'],

    autoScroll: false,
    displayField: 'name',
    // TODO use the translated 'search' value
    emptyText: 'search',
    hideTrigger: true,

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
                                '<span class="type"><em>{type}</em></span>',
                                '<span class="name">{name}</span>',
                            '</div>',
                            '<div>',
                                '<span class="id">(ID {id})</span>',
                                '<span class="company">at {at}</span>',
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
