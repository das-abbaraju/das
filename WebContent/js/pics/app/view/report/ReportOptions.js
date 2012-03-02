Ext.define('PICS.view.report.ReportOptions', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportoptions'],
    
    collapsed: false,
    collapsible: true,
    layout: {
        align: 'stretch',
        type: 'vbox'
    },
    resizable: {
        handles: 'e'
    },
    
    title: 'Report Options',
    
    items: [{
    	xtype: 'panel',
    	bodyPadding: 10,
        items: [{
			xtype: 'button',
			// disabled: true,
			id: 'saveReport',
			text: 'Save'
        }]
    },{
    	xtype: 'panel',
    	flex: 1,
        layout: 'accordion',
        items: [{
            title: 'Filters',
            xtype: 'reportoptionsfilters'            
        }, {
            title: 'Columns',
            xtype: 'reportoptionscolumns'
        }, {
            title: 'Sort'
        }, {
            title: 'Share'
        }, {
            title: 'Save'
        }]
    }],
    
    initComponent: function () {
        
        this.callParent();
    }
});