Ext.define('PICS.store.report.Data', {
    extend: 'Ext.data.Store',

    fields: [],
    proxy: {
        reader: {
            messageProperty: 'message',
            root: 'data',
            type: 'json'
        },
        type: 'ajax'
    }
});