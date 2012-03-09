Ext.define('PICS.view.report.ReportOptionsColumns', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportoptionscolumns'],
    
    items: [{
    	xtype: 'gridpanel',
    	store: 'report.ReportsColumn',
    	
    	// custom type to determine panel actions
    	_column_type: 'column',
    	
    	columns: [{
    		xtype: 'rownumberer'
    	}, {
    		xtype: 'gridcolumn',
    		
    		dataIndex: 'name',
    		flex: 1,
    		hideable: false,
    		sortable: false,
    		text: 'Column',
    		
    		renderer: function (value, metaData, record, rowIndex, colIndex, store) {
                return record.data.field.get('text');
            }
    	}, {
    	    xtype: 'actioncolumn',
    	    
    	    hideable: false,
    	    items: [{
    	        icon: 'images/cross.png',
    	        iconCls: 'ext-icon grid remove-column',
    	        tooltip: 'Remove'
    	    }],
    	    sortable: false,
    	    width: 25
    	}],
    	enableColumnResize: false,
        tbar: [{
            action: 'add-column',
            icon: 'js/pics/resources/images/dd/drop-add.gif',
            text: 'Add Column'
        }],
        viewConfig: {
        	plugins: {
                dragText: 'Drag and drop to reorganize',
                ptype: 'gridviewdragdrop'
            }
        }
    }, {
    	xtype: 'fieldset',
    	
        flex: 1,
        items: [{
            xtype: 'displayfield',
            
            fieldLabel: 'Field',
            value: 'Status'
        }, {
            xtype: 'combobox',
            
            fieldLabel: 'Operator'
        }, {
            xtype: 'radiogroup',
            
            fieldLabel: 'Type',
            items: [{
                xtype: 'radiofield',
                
                boxLabel: 'Value'
            }, {
                xtype: 'radiofield',
                
                boxLabel: 'Field'
            }]
        }, {
            xtype: 'textfield',
            
            fieldLabel: 'Value'
        }]
    }],
    layout: {
        align: 'stretch',
        type: 'vbox'
    },
    title: 'Columns'
});