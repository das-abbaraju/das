Ext.define('PICS.view.layout.SearchBox', {
    extend: 'Ext.form.field.ComboBox',
    alias: ['widget.searchbox'],

    autoScroll: false,
    autoSelect: false,
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
        minWidth: 400,

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
            '</ul>',
            '<a href="">',
                '<div class="search-item-full">',
                'Full search...',
                '</div>',
            '</a>'
        ),
    },

    listeners: {
        select: function (combo, records, eOpts) {
            var post = records[0];
            if (post) {
                document.location = '/SearchBox.action?button=search&searchTerm=' + post.get('name');
            }
        },

        specialkey: function (base, e, eOpts) {
            if (e.getKey() === e.ENTER) {
                document.location = '/SearchBox.action?button=search&searchTerm=' + base.getValue();
            } else if (e.getKey() === e.BACKSPACE && base.getRawValue().length <= 1) {
                base.collapse();
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
            url: 'SearchBox!json.action',
            reader: {
                root: 'results',
                type: 'json',
                totalProperty: 'totalCount'
            }
        }
    },
    width: 200
});
