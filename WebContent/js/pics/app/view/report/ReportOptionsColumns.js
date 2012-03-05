Ext.define('PICS.view.report.ReportOptionsColumns', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportoptionscolumns'],
    
    // TODO This isn't right, for some reason the grid isn't scrolling, so I put it here for now
    autoScroll: true,
    items: [{
    	xtype: 'gridpanel',
        // autoScroll: true,
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
    	}],
    	selModel: {mode: 'multi'},
        viewConfig: {
            plugins: {
                ptype: 'gridviewdragdrop',
                dragText: 'Drag and drop to reorganize'
            }
        }    	
    }],
    tbar: [{
    	action: "add",
    	store: "columns",
        text: 'Add Column',
        columntype: 'column'
    },{
    	action: "remove",
    	store: "columns",
        text: 'Remove',
        columntype: 'column'        
    }],
    title: 'Columns'
});