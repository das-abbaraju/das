Ext.define('PICS.view.report.Viewport', {
    extend : 'Ext.container.Viewport',
    
    layout : 'border',
    
    requires: [
       'PICS.view.layout.Header',
       'PICS.view.layout.Menu',
       'PICS.view.report.FilterPanel',
       'PICS.view.report.Grid',
       'PICS.view.layout.Footer'
    ],
    
    title: 'Main',
    
    initComponent: function () {
        this.items = [{
            xtype: 'layoutheader'
        }, {
            region: 'center',
            layout: 'border',
            
            id: 'main',
            
            dockedItems: [{
                xtype: 'layoutmenu',
                
                dock: 'top',
                height: 30
            }],
            
            items: [{
                xtype: 'reportfilterpanel',
                
                region: 'west',
                
                id: 'aside',
                width: 300
            }, {
                xtype: 'tabpanel',
                
                region: 'center',
                
                title: 'Recently Added Contractors',
                
                items: [{
                    xtype: 'reportgrid'
                }, {
                    title: 'Chart'
                }]
            }]
        }, {
            xtype: 'layoutfooter'
        }];
        
        this.callParent();
    }
});