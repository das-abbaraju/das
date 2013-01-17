Ext.define('PICS.store.report.Reports', {
    extend : 'PICS.store.report.base.Store',
    model : 'PICS.model.report.Report',
    
    proxy: {
        reader: {
            root: 'report',
            type: 'json'
        },
        type: 'memory'
    }

    /*,

    updateProxy: function (type) {
        var proxy = {
            reader: {
                root: 'report',
                type: 'json'
            },
            writer: {
                root: 'report',
                type: 'json'
            }
        };
        
        switch (type) {
            case 'data':
                proxy.type = 'ajax';
                proxy.url = 'ReportApi.action?includeData=true';
 
                break;
            case 'copy':
                proxy.type = 'ajax';
                proxy.url = 'Report!copy.action?report=1';
                
                break;
            default:
                proxy.type = 'memory';
        }

        this.setProxy(proxy);
    }*/
});