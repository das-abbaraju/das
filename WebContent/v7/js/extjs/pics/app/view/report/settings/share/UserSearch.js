Ext.define('PICS.view.report.settings.share.UserSearch', {
    extend: 'Ext.form.field.ComboBox',
    alias: 'widget.usersearch',

    autoScroll: false,
    autoSelect: false,
    displayField: 'name',
    emptyText: 'Search Users and Groups',
    fieldLabel: '<i class="icon-search icon-large"></i>',
    hideTrigger: true,
    id: 'settings_user_search',
    labelSeparator: '',
    labelWidth: 25,

    listConfig: {
        id: 'settings_user_search_list',
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
                '</tpl>',
            '</ul>'
        )
    },

    listeners: {
        select: function (combo, records, eOpts) {
            if (records[0]) {
                this.select(records[0]);
            }
        },

        specialkey: function (base, e, eOpts) {
            if (e.getKey() === e.ENTER) {
                this.select(base.getValue());
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
            url: 'SearchBox!userJson.action',
            reader: {
                root: 'results',
                type: 'json'
            }
        }
    },
    width: 200,

    select: function (record) {
        // Set the selected user
        //record.get('result_name');
        //record.get('result_id');
        //record.get('result_type');
        //record.get('result_at');

        return false;
    }
});
