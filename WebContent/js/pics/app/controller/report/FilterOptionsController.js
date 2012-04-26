Ext.define('PICS.controller.report.FilterOptionsController', {
    extend: 'Ext.app.Controller',

    refs: [{
        ref: 'reportColumnSelector',
        selector: 'reportcolumnselector'
    }, {
        ref: 'filterOptions',
        selector: 'filteroptions #filterDetails'
    }],
    stores: [
        'report.AvailableFieldsByCategory',
        'report.Reports'
    ],

    generateFilterPanels: function () {
        var filterContainer = Ext.create('Ext.panel.Panel', {border: false}),
            count = 0,
            me = this,
            store = this.getReportReportsStore().first().filters();

        store.each(function (record) {
            var type = record.data.field.data.filterType,
                panelClass = me.setFilterPanelClass(type),
                filterPanel = null;

            if (panelClass !== null) {
                filterPanel = Ext.create(panelClass, {record: record, panelNumber: ++count});
                filterContainer.add(filterPanel);
            }
        });
        return filterContainer;
    },
    refreshFilters: function () {
        var filterContainer = null;

        this.getFilterOptions().removeAll();

        filterContainer = this.generateFilterPanels();
        
        this.getFilterOptions().add(filterContainer);
    },
    init: function() {
        this.control({
            'filteroptions button[action=add-filter]': {
                click: this.showColumnSelector
            },
            'filteroptions button[action=search]': {
                click: function () {
                    console.log('search click');
                    PICS.app.fireEvent('refreshreport');
                }
            }
        });
        this.application.on({
            refreshfilters: this.refreshFilters,
            scope: this
        });
    },
    setFilterPanelClass: function (type) {
        var panelClass = '';

        switch (type) {
            case 'String': panelClass = 'PICS.view.report.filter.StringFilter'; break;
            case 'AccountName': panelClass = 'PICS.view.report.filter.StringFilter'; break;
            case 'Boolean': panelClass = 'PICS.view.report.filter.BooleanFilter'; break;
            case 'Number': panelClass = 'PICS.view.report.filter.NumberFilter'; break;
            case 'Integer': panelClass = 'PICS.view.report.filter.NumberFilter'; break;
            default: panelClass = null; break;
        }
        return panelClass;
    },
    showColumnSelector: function(component, e, options) {
        var window = this.getReportColumnSelector();

        if (!window) {
            var store = this.getReportAvailableFieldsByCategoryStore();
            store.clearFilter();

            window = Ext.create('PICS.view.report.ColumnSelector');
            window._column_type = 'filter';
            window.show();
        }
    }
});
