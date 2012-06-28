Ext.define('PICS.view.layout.SearchBox', {
    extend: 'Ext.form.field.ComboBox',
    alias: ['widget.searchbox'],

    displayField: 'name',
    // TODO use the translated 'search' value
    emptyText: 'search',
    hideTrigger: true,
    listConfig: {
        // TODO get the translated version
        loadingText: 'Searching...',

        // Custom rendering template for each item
        getInnerTpl: function() {
            return '{type} {name} - (ID {id}) at ({at})';
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
            url: 'SearchBox!json.action',
            reader: {
                root: 'results',
                type: 'json',
                totalProperty: 'totalCount'
            }
        }
    }
});
