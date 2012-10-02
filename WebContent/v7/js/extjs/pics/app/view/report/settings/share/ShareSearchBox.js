Ext.define('PICS.view.settings.share.ShareSearchBox', {
    extend: 'Ext.form.field.ComboBox',
    alias: ['widget.sharesearchbox'],

    autoScroll: false,
    autoSelect: false,
    cls: 'site-menu-search',
    displayField: 'name',
    emptyText: 'Search Users and Groups',
    fieldLabel: '<i class="icon-search icon-large"></i>',
    hideTrigger: true,
    labelSeparator: '',
    labelWidth: 15,
    
    listConfig: {
        cls: 'site-menu-search-list',
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
                                '<span class="company">{result_at}</span>',
                                '<span class="type">{result_type}</span>',
                            '</div>',
                        '</div>',
                    '</li>',
                '</tpl>',
            '</ul>'
        ),
    },

    // TO DO: Move this out to the controller.
    listeners: {
        select: function (combo, records, eOpts) {
            var post = records[0];

            if (post) {
                var account = {
                                name: post.get('result_name'),
                                at: post.get('result_at')
                              };

                var cmp = Ext.ComponentQuery.query('reportsettingsshare')[0];
                cmp.update(account);
            }
        }
    },
    
    minChars: 1,
    name: 'search_term',
    pickerAlign: 'br',
    pickerOffset: [-305, 2],
    queryMode: 'remote',
    queryParam: 'q',

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
            url: 'SearchBox!json.action',
            reader: {
                root: 'results',
                type: 'json',
            }
        }
    },
    
    valueField: 'q',
    width: 325
});