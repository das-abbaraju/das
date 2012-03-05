Ext.define('PICS.view.report.ReportOptionsFilters', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportoptionsfilters'],
    
    items: [{
    	xtype: 'gridpanel',
    	autoScroll: true,
    	store: 'report.ReportsFilter',
    	columns: [{
    		xtype: "rownumberer"
    	},{
    		xtype: 'gridcolumn',
    		dataIndex: 'name',
    		flex: 1,
    		hideable: false,
    		sortable: false,
    		text: 'Filter'
    	}],
    	selModel: {mode: 'multi'}    	
    }],
    tbar: [{
        action: "add",
        store: "columns",
        text: 'Add Filter',
        columntype:'filter'
    },{
        action: "remove",
        store: "columns",
        text: 'Remove',
        columntype:'filter'            
    }],    
    title: 'Filters'
});