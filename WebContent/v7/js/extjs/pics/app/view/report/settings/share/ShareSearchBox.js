Ext.define('PICS.view.report.settings.share.ShareSearchBox', {
    extend: 'Ext.form.field.ComboBox',
    alias: ['widget.sharesearchbox'],

    autoScroll: false,
    autoSelect: false,
    cls: 'share-searchbox',
    displayField: 'name',
    emptyText: 'Search Users and Groups',
    fieldLabel: '<i class="icon-search icon-large"></i>',
    hideTrigger: true,
    labelSeparator: '',
    labelWidth: 15,
    
    listConfig: {
        cls: 'site-menu-search-list',
        loadingText: 'Searching' + '&hellip;',
        maxHeight: 700,
        minWidth: 300,

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
                                '<span class="location">{location}</span>',
                                '<span class="type">{type}</span>',
                            '</div>',
                        '</div>',
                    '</li>',
                '</tpl>',
            '</ul>'
        )
    },

    minChars: 1,
    name: 'search_term',
    pickerAlign: 'br',
    pickerOffset: [-305, 2],
    queryMode: 'remote',
    queryParam: 'searchQuery',

    store: {
        autoLoad: false,
        fields: [
            'access_type',
            'id',
            'name',
            'location',
            'type'
        ],
        proxy: {
            type: 'ajax',
            url: '',
            reader: {
                root: 'results',
                type: 'json'
            },
            pageParam: false,
            startParam: false,
            limitParam: false
        }
    },
    valueField: 'searchQuery',
    width: 325
});