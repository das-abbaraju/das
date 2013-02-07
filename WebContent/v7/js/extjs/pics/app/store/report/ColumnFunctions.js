Ext.define('PICS.store.report.ColumnFunctions', {
    extend: 'PICS.store.report.base.Store',
    model: 'PICS.model.report.ColumnFunctions',
    
    // dynamic url needs to be generated to obtain specific column's "sql functions" 
    setProxyForRead: function (url) {
        var proxy = {
            reader: {
                root: 'column_functions',
                type: 'json'
            },
            type: 'ajax',
            url: url
        };
        
        this.setProxy(proxy);
    }
});