/**
 * Report Controller
 * 
 * Controls report refresh
 */
Ext.define('PICS.controller.report.ReportController', {
    extend: 'Ext.app.Controller',
    alias: ['widget.reportdatacontroller'],

    refs: [{
        ref: 'dataSetGrid',
        selector: 'reportdatasetgrid'
    },{
        ref: 'reportColumnSelector',
        selector: 'reportcolumnselector'
    }],
    stores: [
        'report.AvailableFields',
        'report.AvailableFieldsByCategory',
        'report.DataSets',
        'report.Reports'
    ],

    init: function () {
        this.control({
            'reportdatasetgrid': {
                beforerender: this.configureColumnMenu
            },
            'reportsorttoolbar button[action=add-column]': {
                click: this.showColumnSelector
            },
        });

        this.application.on({
            refreshreport: this.refreshReport,
            scope: this
        });
    },
    
    onLaunch: function () {
        this.getReportAvailableFieldsStore().load();
        
        this.getReportReportsStore().load({
            scope: this,
            callback: function(records, operation, success) {
                this.refreshReport();
                this.refreshFilters();
                this.refreshSorts();
            }
        });
    },
    
    configureColumnMenu: function (grid) {
        var controllerHandle = this;
        
        grid.columns[0].ownerCt.on('menucreate', function (container, menu, opts) {
            //delete existing column hide menu
            menu.remove(menu.items.items[3], true);

            //add new menu items
            var options = {
                xtype: 'menu',
                border: false,
                enableScrolling: false,
                floating: false,
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
                border: false,
                floating: false,
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
    },

    refreshFilters: function () {
        this.application.fireEvent('refreshfilters');
    },
    
    refreshReport: function () {
        this.getReportDataSetsStore().populateGrid();
    },
    
    refreshSorts: function () {
        this.application.fireEvent('refreshsorts');
    },
    
    removeColumn: function (activeMenuItem) {
        var column_store = this.getReportReportsStore().first().columns(),
            colIndex = column_store.find('name', activeMenuItem);

        column_store.removeAt(colIndex);
        this.refreshReport();
    },
    
    showColumnSelector: function(component, e, options) {
        var window = this.getReportColumnSelector();

        if (!window) {
            var store = this.getReportAvailableFieldsByCategoryStore();
            store.clearFilter();

            window = Ext.create('PICS.view.report.ColumnSelector');

            window._column_type = 'column';
            window.show();
        }
    }
});
