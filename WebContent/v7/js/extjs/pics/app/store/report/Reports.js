Ext.define('PICS.store.report.Reports', {
    extend : 'PICS.store.report.base.Store',
    model : 'PICS.model.report.Report',
    
    constructor: function () {
        this.setProxyForRead();
        
        this.callParent(arguments);
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