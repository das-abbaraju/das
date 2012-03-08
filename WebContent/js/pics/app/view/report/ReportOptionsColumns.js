Ext.define('PICS.view.report.ReportOptionsColumns', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportoptionscolumns'],
    
    items: [{
    	xtype: 'gridpanel',
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
    	store: 'report.ReportsColumn',
        viewConfig: {
        	minHeight: 200, // this doesn't seem to work yet
        	plugins: {
                ptype: 'gridviewdragdrop',
                dragText: 'Drag and drop to reorganize'
            }
        }
    },{
    	xtype: 'fieldset',
        flex: 1,
        items: [{
            xtype: 'displayfield',
            value: 'Status',
            fieldLabel: 'Field'
        },{
            xtype: 'combobox',
            fieldLabel: 'Operator'
        },{
            xtype: 'radiogroup',
            fieldLabel: 'Type',
            items: [{
                xtype: 'radiofield',
                boxLabel: 'Value'
            },{
                xtype: 'radiofield',
                boxLabel: 'Field'
            }]
        },{
            xtype: 'textfield',
            fieldLabel: 'Value'
        }]
    }],
    layout: {
        align: 'stretch',
        type: 'vbox'
    },
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