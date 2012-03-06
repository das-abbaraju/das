Ext.define('PICS.view.report.ReportOptions', {
    extend: 'Ext.panel.Panel',
    alias: ['widget.reportoptions'],
    
    collapsed: false,
    collapsible: true,
    items: [{
        xtype: 'panel',
        
        bodyPadding: 10,
        items: [{
			xtype: 'button',
			action: 'save',
			// disabled: true,
		    text: 'Save'
        },{
			xtype: 'button',
			action: 'refresh',
		    text: 'Refresh'
        }]
    },{
        xtype: 'panel',
        
        flex: 1,
        items: [{
            xtype: 'reportoptionsfilters'
        }, {
            xtype: 'reportoptionscolumns'
        }, {
            title: 'Sort'
        }],
        layout: 'accordion'
    }],
    layout: {
        align: 'stretch',
        type: 'vbox'
    },
    resizable: {
        handles: 'e'
    },
    title: 'Report Options',
    
    initComponent: function () {
        this.callParent();
    }
});