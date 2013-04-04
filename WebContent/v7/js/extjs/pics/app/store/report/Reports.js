Ext.define('PICS.store.report.Reports', {
    extend : 'PICS.store.report.base.Store',
    model : 'PICS.model.report.Report',
    
    constructor: function () {
        this.setProxyForRead();
        
        this.callParent(arguments);
    },

    listeners: {
        add: function (store, record, index, eOpts) {
            record.has_unsaved_changes = true;
        },

        remove: function (store, record, index, eOpts) {
            record.has_unsaved_changes = true;
        },

        update: function (store, record, operation, modifiedFieldNames, eOpts) {
            record.has_unsaved_changes = true;
        }
    },

    setProxyForRead: function () {
        var proxy = {
            reader: {
                root: 'report',
                type: 'json'
            },
            type: 'memory'
        };
        
        this.setProxy(proxy);
    },

    setProxyForWrite: function (url) {
        var proxy = {
            writer: {
                root: 'report',
                type: 'json'
            },
            type: 'ajax',
            url: url
        };
        
        this.setProxy(proxy);
    }
});