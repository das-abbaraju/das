Ext.define('PICS.view.report.Viewport', {
    extend : 'Ext.container.Viewport',
    
    layout : 'border',
    
    requires: [
       'PICS.view.main.Header',
       'PICS.view.main.Menu',
       'PICS.view.report.FilterPanel',
       'PICS.view.main.Footer'
    ],
    
    title: 'Main',
    
    initComponent: function () {
        this.items = [{
            xtype: 'header'
        }, {
            region: 'center',
            layout: 'border',
            
            id: 'main',
            
            dockedItems: [{
                xtype: 'menu',
                
                dock: 'top',
                height: 30
            }],
            
            items: [{
                xtype: 'filterpanel',
                
                region: 'west',
                
                id: 'aside',
                width: 300
            }, {
                xtype: 'tabpanel',
                
                region: 'center',
                
                title: 'Recently Added Contractors',
                
                items: [{
                    title: 'Grid',
                    xtype: 'reportgrid'
                }, {
                    title: 'Chart'
                }]
            }]
        }, {
            xtype: 'footer'
        }];
        
        this.callParent();
    }
});