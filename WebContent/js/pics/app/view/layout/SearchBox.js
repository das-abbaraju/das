Ext.define('PICS.view.layout.SearchBox', {
    extend: 'Ext.form.field.ComboBox',
    alias: ['widget.searchbox'],

    autoScroll: false,
    displayField: 'name',
    emptyText: 'search',
    fieldLabel: '<i class="icon-search icon-large"></i>',
    hideTrigger: true,
    id: 'site_menu_search',
    labelSeparator: '',
    labelWidth: 25,

    listConfig: {
        id: 'site_menu_search_list',
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
        select: function(combo, selection) {
            var post = selection[0];
            if (post) {
                // TODO change this to be more betterer
                window.location = Ext.String.format('http://alpha.picsorganizer.com/SearchBox.action?button=search&searchTerm={0}', post.get('name'));
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
