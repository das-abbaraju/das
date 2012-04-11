Ext.define('PICS.controller.report.ReportController', {
    extend: 'Ext.app.Controller',
    alias: ['widget.reportdatacontroller'],

    store: 'report.ReportData',

    init: function () {
        var controllerHandle = this;
        this.control({
            'reportdatagrid': {
                beforerender: function (grid) {                    
                    grid.columns[0].ownerCt.on('menucreate', function (container, menu, opts) {
                        //delete existing column hide menu
                        menu.remove(menu.items.items[3], true);
                        
                        //add new menu items                
                        var options = {
                            xtype: 'menu',
                            border: false,                            
                            floating: false,
                            hidden: false,
                            enableScrolling: false,
                            subMenuAlign: 'tl-bl?',
                            items: [{
                                text: 'Options',
                                menu: {
                                    xtype: 'menu',
                                    items: [{
                                        text: 'Temp Option 1'
                                    },{
                                        text: 'Temp Option 2'
                                    },{
                                        text: 'Temp Option 3'
                                    }]
                                }
                            }]
                        };      
        
                        var removeColumn = {
                            xtype: 'menu',
                            floating: false,
                            border: false,
                            items: [{
                                text: 'Remove',
                                handler: function (button, event) {
                                        controllerHandle.removeColumn(menu.activeHeader.dataIndex);
                                }                                
                            }]
                        };
                        menu.add(options);
                        menu.add(removeColumn);
                    });
                }
            }
        });
    },
    removeColumn: function (activeMenuItem) {
        var columnIndex = '',
        grid = Ext.ComponentQuery.query('reportdatagrid')[0];
        
        //match column and menu index
        for (x = 0; x < grid.columns.length; x++) {
            if (grid.columns[x].dataIndex === activeMenuItem){
                columnIndex = x;
            }
        }
        grid.columns[columnIndex].destroy();
        grid.getView().refresh();
    }
});