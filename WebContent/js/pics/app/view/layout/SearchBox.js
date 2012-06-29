Ext.define('PICS.view.layout.SearchBox', {
    extend: 'Ext.form.field.ComboBox',
    alias: ['widget.searchbox'],

    autoScroll: false,
    displayField: 'name',
    // TODO use the translated 'search' value
    emptyText: 'search',
    hideTrigger: true,
    listConfig: {
        // TODO get the translated version
        loadingText: 'Searching...',
        maxHeight: 500,

        // Custom rendering template for each item
        getInnerTpl: function() {
            return '<div style="float: left; margin 20px;" class="menu-list-left">{type}:<br />(ID {id})</div>' +
                   '<div style="" class="menu-list-right">{name}<br />at ({at})';
        }
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
