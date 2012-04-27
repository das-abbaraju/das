/**
 * Report Store
 * 
 * load backend report into local report database via ajax
 * sends backend report from local to server
 */
Ext.define('PICS.store.report.Reports', {
	extend : 'Ext.data.Store',
	model : 'PICS.model.report.Report',

	autoLoad: false,
    proxy: {
        reader: {
            root: 'report',
            type: 'json'
        },
        type: 'ajax',
        writer: {
        	xtype: 'writer.base',
        	write: function(request) {
        	    // TODO
        		// See http://docs.sencha.com/ext-js/4-0/source/Json3.html#Ext-data-writer-Json
        		// writeRecords
        		var report = request.records[0];
        		
                request.params['report.parameters'] = report.parameters;
                request.url = 'ReportDynamic!save.action?report=' + report.getId();
                
                return request;
        	}
        }
    },
    
    constructor: function () {
        var url = Ext.Object.fromQueryString(document.location.search);
        
        this.proxy.url = 'ReportDynamic!getReportParameters.action?report=' + url.report;
        
        this.callParent(arguments);
    }
});