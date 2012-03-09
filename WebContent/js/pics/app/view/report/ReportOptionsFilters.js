Ext.define('PICS.view.report.ReportOptionsFilters', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportoptionsfilters'],

    items: [{
    	xtype: 'gridpanel',
    	store: 'report.ReportsFilter',
    	
    	// custom type to determine panel actions
        _column_type: 'filter',
        
    	columns: [{
    		xtype: 'rownumberer'
    	}, {
    		xtype: 'gridcolumn',
    		
    		dataIndex: 'name',
    		flex: 1,
    		hideable: false,
    		sortable: false,
            text: 'Filter',
                
    		renderer: function (value, metaData, record, rowIndex, colIndex, store) {
    			// TODO Based on filterType
    			return "'" + record.data.field.get('text') + "' = " + record.get('value');
    		}
    	}, {
            xtype: 'actioncolumn',
            
            hideable: false,
            items: [{
                icon: 'images/cross.png',
                iconCls: 'ext-icon grid remove-filter',
                tooltip: 'Remove'
            }],
            sortable: false,
            width: 25
        }],
    	enableColumnResize: false,
	    tbar: [{
	        action: 'add-filter',
            icon: 'js/pics/resources/images/dd/drop-add.gif',
	        text: 'Add Filter'
	    }]
    },{
        xtype: 'panel',
        
        autoScroll: true,
        dock: 'bottom',
        flex: 1,
        id: 'options'
    }],
    layout: {
        align: 'stretch',
        type: 'vbox'
    },
    title: 'Filters'
});