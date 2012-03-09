Ext.define('PICS.view.report.ReportOptionsFilters', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportoptionsfilters'],

    items: [{
    	xtype: 'gridpanel',
    	store: 'report.ReportsFilter',
    	
    	columns: [{
    		xtype: 'rownumberer'
    	},{
    		xtype: 'gridcolumn',
    		dataIndex: 'name',
    		flex: 1,
    		hideable: false,
    		renderer: function (value, metaData, record, rowIndex, colIndex, store) {
    			// TODO Based on filterType
    			return "'" + record.data.field.get('text') + "' = " + record.get('value');
    		},
    		sortable: false,
    		text: 'Filter'
    	}],
    	flex: 2,
    	selModel: {
    	    mode: 'multi'
	    }
    },{
        xtype: 'panel',
        dock: 'bottom',
        autoScroll:true,
        flex: 1,
        id: 'options'
    }],
    layout: {
        align: 'stretch',
        type: 'vbox'
    },
    tbar: [{
        action: 'add',
        column_type:'filter',
        text: 'Add Filter'
    },{
        action: 'remove',
        column_type:'filter',
        text: 'Remove'
    }],
    title: 'Filters'
});