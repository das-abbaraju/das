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
    		    var filterType = record.data.field.get('filterType'),
    		        operator = "=",
    		        value = record.get('value'),
    		        text = record.data.field.get('text'),
    		        formattedData = text + " ";
    		    
    		    function formatFilterDisplay() {
                    if (record.get('not') === true) {
                        formattedData += ' Not ';
                    }
    		        
    		        if (filterType === 'Boolean') {
    		            if (!value) {
    		                value = '1';
    		                record.set('value', 1);    		                
    		            } else {
    		                (value === '1') ? operator = 'isTrue' : operator = 'isFalse';        
    		            }
    		            formattedData += operator;
    		        } else {
    		            formattedData += operator + " '" + value + "'";    
    		        }
    		    }
    		    
                if (record.data.operator) {
                    operator = record.data.operator;
                }
                
    		    formatFilterDisplay();

    		    return formattedData; 
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
    	flex: 2,
	    tbar: [{
	        action: 'add-filter',
            icon: 'js/pics/resources/images/dd/drop-add.gif',
	        text: 'Add Filter'
	    }]
    },{
        xtype: 'panel',
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