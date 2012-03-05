Ext.define('PICS.view.report.ReportOptionsColumns', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportoptionscolumns'],
    
    items: [{
    	xtype: 'gridpanel',
    	autoScroll: true,
    	store: 'report.ReportsColumn',
    	columns: [{
    		xtype: "rownumberer"
    	},{
    		xtype: 'gridcolumn',
    		dataIndex: 'text',
    		flex: 1,
    		hideable: false,
    		sortable: false,
    		text: 'Column'
    	}]
    }],
    tbar: [{
        text: 'Add Column',
        
        handler: function () {
            var window = Ext.ComponentQuery.query('reportcolumnselector');
            
            if (!window.length) {
                var window = Ext.create('PICS.view.report.ColumnSelector');
            } else {
                window = window[0];
            }
            
            window.show();
        }
    }],
    title: 'Columns'
});