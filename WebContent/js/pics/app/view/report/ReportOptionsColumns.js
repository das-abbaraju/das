Ext.define('PICS.view.report.ReportOptionsColumns', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportoptionscolumns'],
    
    items: [{
    	xtype: 'gridpanel',
    	store: 'report.ReportsColumn',
    	columns: [{
    		xtype: "rownumberer"
    	},{
    		xtype: 'gridcolumn',
    		dataIndex: 'name',
    		flex: 1,
    		hideable: false,
    		renderer: function(value, metaData, record, rowIndex, colIndex, store) {
    			return record.data.field.get("text");
    		},
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
    layout: 'fit',
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