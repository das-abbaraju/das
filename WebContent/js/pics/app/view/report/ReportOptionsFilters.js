Ext.define('PICS.view.report.ReportOptionsFilters', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportoptionsfilters'],
    
    items: [{
    	xtype: 'gridpanel',
    	autoScroll: true,
    	store: 'report.ReportsColumn',
    	columns: [{
    		xtype: "rownumberer"
    	},{
    		xtype: 'gridcolumn',
    		dataIndex: 'name',
    		flex: 1,
    		hideable: false,
    		sortable: false,
    		text: 'Filter'
    	}]
    }],
    tbar: [{
        text: 'Add Filter',
        
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
    title: 'Filters'
});