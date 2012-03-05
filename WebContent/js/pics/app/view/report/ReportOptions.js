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
        }]
    },{
        xtype: 'panel',
        
        flex: 1,
        items: [{
            xtype: 'reportoptionscolumns'
        }, {
            xtype: 'reportoptionsfilters'            
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