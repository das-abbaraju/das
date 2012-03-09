Ext.define('PICS.view.report.ReportOptionsFilters', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportoptionsfilters'],

    bbar: [{
        action: "apply",
        text: 'Apply Filter'
    }],    
    items: [{
    	xtype: 'gridpanel',
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
    	flex: 2,
    	selModel: {mode: 'multi'},
        width: "100%"    
    },{
        xtype: 'panel',

        autoScroll:true,
        flex: 1,
        id: 'options',
        width: "100%"
    }],
    layout: "vbox",
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